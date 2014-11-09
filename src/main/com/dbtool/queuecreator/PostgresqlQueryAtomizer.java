package com.dbtool.queuecreator;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import com.dbtool.queuecreator.AST.SQLStatement;
import com.dbtool.utils.DatabaseConnector;
/**
 * Atomizer implementation for postgresql. Will split multiple access statements into multiple approximating single access statements.
 * Uses EXPLAIN ANALYZE queries to retrieve query plans in XML format that are used to generate single access statements.
 *
 */
public class PostgresqlQueryAtomizer implements QueryAtomizer {

	private Connection conn;
	private TransactionServiceRateCalculator serviceRateCalc;
	private List<SQLStatement> atomizedStatements = new ArrayList<SQLStatement>();
	private SQLStatement currentSQLStat = null;
	private List<String> tablesModified = new ArrayList<String>();
	private Map<String, Boolean> tableAccessMap = new HashMap<String, Boolean>();
	
	public PostgresqlQueryAtomizer(TransactionServiceRateCalculator serviceRateCalc) {
		conn = DatabaseConnector.getConnection();
		this.serviceRateCalc = serviceRateCalc;
	}
	
	@Override
	public List<SQLStatement> atomizeSqlStatment(SQLStatement statement) throws AtomizationException {
		atomizedStatements.clear();
		tablesModified.clear();
		tableAccessMap.clear();
		currentSQLStat = statement;
		String rawSql = statement.getSQLContent();
		String explainSql = "EXPLAIN (verbose, analyse, format xml) " + rawSql;
		ResultSet result = null;
		String xmlPlan = "";
		try {
			//Execute EXPLAIN ANALYZE
			Statement stat = conn.createStatement();
			result = stat.executeQuery(explainSql);
			result.next();
			xmlPlan = result.getString(1);	
		} catch (SQLException e) {
			System.err.println("Could not atomize: '" + rawSql + "'");
			System.err.println(e.getMessage());
			throw new AtomizationException(e);
		}
		Document xmlPlanDoc = null;
		try {
			xmlPlanDoc = DocumentHelper.parseText(xmlPlan);
		} catch (DocumentException e) {
			System.err.println("Error parsing XML query plan");
			throw new AtomizationException(e);
		}
		parseExplainResult(xmlPlanDoc);
		return getAtomizedStatements();
	}
	
	private void parseExplainResult(Document xmlPlan) {
		Element root = xmlPlan.getRootElement();
		Element query = root.element("Query");
		Element plan = query.element("Plan");
		//Check if the statement plan is single access and record exclusivity of access
		checkTableAccesses(plan);
		if (tableAccessMap.size() > 1) {
			//If multiple access then a detailed traversal of plan is needed
			parsePlanNode(plan);
		}
		else {
			try {
				//Otherwise create new annotated statement from old
				for (Map.Entry<String, Boolean> access : tableAccessMap.entrySet()) {
					atomizedStatements.add(new SQLStatement(currentSQLStat.getSQLContent(), access.getValue(), 
															access.getKey(), serviceRateCalc.calculateServiceRate(currentSQLStat.getSQLContent())));
				}
			}
			catch (ServiceCalculatorException ex) {
				System.err.println("Unable to calculate service rate of statements");
				return;
			}
		}
	}
	
	/**
	 * Traverse plan recording each unique table access and recording the exclusivity required for each access.
	 * @param plan
	 * @return A Map from table name to exclusivity of access
	 */
	private Map<String, Boolean> checkTableAccesses(Element plan) {
		tableAccessMap = new HashMap<String, Boolean>();
		Stack<Element> elStack = new Stack<Element>();
		elStack.push(plan);
		while (!elStack.isEmpty()) {
			plan = elStack.pop();
			Element relationNameEl = plan.element("Relation-Name");
			if (relationNameEl != null) {
				Element nodeType = plan.element("Node-Type");
				String table = relationNameEl.getText();
				boolean exclusive = nodeType.getText().equals("ModifyTable");
				if (tableAccessMap.containsKey(table)) {
					boolean currentExclusivity = tableAccessMap.get(table);
					tableAccessMap.put(table, exclusive || currentExclusivity);
				}
				else {
					tableAccessMap.put(table, exclusive);
				}
			}
			Element plansEl = plan.element("Plans");
			if (plansEl != null) {
				for (Object planObj : plansEl.elements("Plan")) {
					elStack.push((Element) planObj);
				}
			}
		}
		return tableAccessMap;
	}

	/**
	 * Parse the current plan node. Currently only Seq Scan, Index Only Scan, Index Scan, Bitmap Heap Scan and Hash Join are explicitly supported. Nested Loop
	 * and merge joins are supported indirectly by taking into account the scans they involve.
	 * @param planEl
	 */
	private void parsePlanNode(Element planEl) {
		Element nodeType = planEl.element("Node-Type");
		switch (nodeType.getText()) {
		case "Seq Scan": parseSeqScan(planEl); break;
		case "Index Only Scan": parseIndexScan(planEl); break;
		case "Index Scan": parseIndexScan(planEl); break;
		case "Bitmap Heap Scan": parseBitmapHeapScan(planEl); break;
		case "Hash Join": parseHashJoin(planEl); break;
		case "Merge Join":
		case "Nested Loop":
		case "ModifyTable":
		default: parseOtherNodes(planEl); break;
		}
	}
	
	private void parseHashJoin(Element planEl) {
		Element plansEl = planEl.element("Plans");
		if (plansEl != null) {
			List<?> plans = plansEl.elements("Plan");
			for (int n = plans.size() - 1; n >= 0; n--) {
				parsePlanNode((Element) plans.get(n));
			}
		}
		
	}
	/**
	 * Generate SQL statement the approximates a sequential scan by using the query plan to generate the SELECT, FROM and WHERE clauses
	 * NOTE: This method has many similarities to the parse methods for other scans. Consider refactoring to eliminate repeated code.
	 * @param seqScanEl
	 */
	private void parseSeqScan(Element seqScanEl) {
		Element relNameEl = seqScanEl.element("Relation-Name");
		String tableScanned = relNameEl.getText();
		Element loopEl = seqScanEl.element("Actual-Loops");
		int numberOfLoops = Integer.parseInt(loopEl.getText());
		boolean isTableModified = isTableBeingModified(tableScanned);
		StringBuilder sqlBuilder = new StringBuilder();
		//Generate the SELECT clause
		sqlBuilder.append("SELECT ");
		Element outputEl = seqScanEl.element("Output");
		List<?> outputItemObjs = (List<?>) outputEl.elements("Item");
		Element firstOutputItemEl = (Element) outputItemObjs.get(0);
		sqlBuilder.append(firstOutputItemEl.getText());
		for (int n = 1; n < outputItemObjs.size(); n++) {
			Element outputItemEl = (Element) outputItemObjs.get(n);
			sqlBuilder.append(", ");
			sqlBuilder.append(outputItemEl.getText());
		}
		sqlBuilder.append("\n");
		//Generate the FROM clause
		sqlBuilder.append("FROM ");
		sqlBuilder.append(tableScanned);
		//Check that the generated clauses are valid SQL
		boolean correctSelect = testQuery(sqlBuilder.toString());
		if (!correctSelect) {
			//If not valid SQL then make approximation more rough by selecting *
			sqlBuilder = new StringBuilder("SELECT * \nFROM ").append(tableScanned);
		}
		//Generate the WHERE clause by translating filter string
		Element filterEl = seqScanEl.element("Filter");
		if (filterEl != null) {
			String filterText = parseFilter(filterEl.getText(), tableScanned);
			if (!filterText.equals("")) {
				sqlBuilder.append("\n");
				sqlBuilder.append("WHERE ");
				sqlBuilder.append(filterText);
			}
		}
		sqlBuilder.append(";");
		SQLStatement newStatement;
		//Annotate the generated statement
		try {
			newStatement = new SQLStatement(sqlBuilder.toString(), isTableModified, tableScanned, 
															serviceRateCalc.calculateServiceRate(sqlBuilder.toString()), numberOfLoops);
			atomizedStatements.add(newStatement);
		} catch (ServiceCalculatorException e) {
			System.err.println("Unexpected error when calculating service rate for statement:" + sqlBuilder.toString());
			throw new AtomizationException(e);
		}
		
		parsePlans(seqScanEl);
	}
	
	/**
	 * Check that the generated query is structurally sound
	 * @param string
	 * @return A boolean indicating if the query was sound
	 */
	private boolean testQuery(String string) {
		try {
			Statement stat = conn.createStatement();
			stat.executeQuery(string);
		} catch (SQLException e) {
			return false;
		}
		return true;
	}
	
	/**
	 * Generate SQL statement the approximates a bitmap heap scan by using the query plan to generate the SELECT, FROM and WHERE clauses
	 * NOTE: This method has many similarities to the parse methods for other scans. Consider refactoring to eliminate repeated code.
	 * @param bitmapScanEl
	 */
	private void parseBitmapHeapScan(Element bitmapScanEl) {
		Element relNameEl = bitmapScanEl.element("Relation-Name");
		String tableScanned = relNameEl.getText();
		Element loopEl = bitmapScanEl.element("Actual-Loops");
		int numberOfLoops = Integer.parseInt(loopEl.getText());
		boolean isTableModified = isTableBeingModified(tableScanned);
		StringBuilder sqlBuilder = new StringBuilder();
		//Generate the SELECT clause
		sqlBuilder.append("SELECT ");
		Element outputEl = bitmapScanEl.element("Output");
		List<?> outputItemObjs = (List<?>) outputEl.elements("Item");
		Element firstOutputItemEl = (Element) outputItemObjs.get(0);
		sqlBuilder.append(firstOutputItemEl.getText());
		for (int n = 1; n < outputItemObjs.size(); n++) {
			Element outputItemEl = (Element) outputItemObjs.get(n);
			sqlBuilder.append(", ");
			sqlBuilder.append(outputItemEl.getText());
		}
		sqlBuilder.append("\n");
		//Generate the FROM clause
		sqlBuilder.append("FROM ");
		sqlBuilder.append(tableScanned);
		//Check that the generated clauses are valid SQL
		boolean correctSelect = testQuery(sqlBuilder.toString());
		if (!correctSelect) {
			//If not valid SQL then make approximation more rough by selecting *
			sqlBuilder = new StringBuilder("SELECT * \nFROM ").append(tableScanned);
		}
		//Generate WHERE from bitmap condition and filter condition
		boolean requireWhere = true;
		Element bitmapCondEl = bitmapScanEl.element("Recheck-Cond");
		if (bitmapCondEl != null) {
			String condText = parseFilter(bitmapCondEl.getText(), tableScanned);
			if (!condText.equals("")) {
				sqlBuilder.append("\n");
				sqlBuilder.append("WHERE ");
				sqlBuilder.append(condText);
				requireWhere = false;
			}
		}
		Element filterEl = bitmapScanEl.element("Filter");
		if (filterEl != null) {
			String filterText = parseFilter(filterEl.getText(), tableScanned);
			if (!filterText.equals("")) {
				if (requireWhere) {
					sqlBuilder.append("\n");
					sqlBuilder.append("WHERE ");
				}
				else {
					sqlBuilder.append(" AND ");
				}
				sqlBuilder.append(filterText);
			}
		}
		sqlBuilder.append(";");
		//Annotate the generated statement
		SQLStatement newStatement;
		try {
			newStatement = new SQLStatement(sqlBuilder.toString(), isTableModified, tableScanned, 
					serviceRateCalc.calculateServiceRate(sqlBuilder.toString()), numberOfLoops);
			atomizedStatements.add(newStatement);
		} catch (ServiceCalculatorException e) {
			System.err.println("Unexpected error when calculating service rate for statement:" + sqlBuilder.toString());
			throw new AtomizationException(e);
		}
		
		parsePlans(bitmapScanEl);
	}

	/**
	 * Generate SQL statement the approximates an index scan by using the query plan to generate the SELECT, FROM and WHERE clauses
	 * NOTE: This method has many similarities to the parse methods for other scans. Consider refactoring to eliminate repeated code.
	 * @param indexScanEl
	 */
	private void parseIndexScan(Element indexScanEl) {
		Element relNameEl = indexScanEl.element("Relation-Name");
		String tableScanned = relNameEl.getText();
		Element loopEl = indexScanEl.element("Actual-Loops");
		int numberOfLoops = Integer.parseInt(loopEl.getText());
		boolean isTableModified = isTableBeingModified(tableScanned);
		StringBuilder sqlBuilder = new StringBuilder();
		//Generate the SELECT clause
		sqlBuilder.append("SELECT ");
		Element outputEl = indexScanEl.element("Output");
		List<?> outputItemObjs = (List<?>) outputEl.elements("Item");
		Element firstOutputItemEl = (Element) outputItemObjs.get(0);
		sqlBuilder.append(firstOutputItemEl.getText());
		for (int n = 1; n < outputItemObjs.size(); n++) {
			Element outputItemEl = (Element) outputItemObjs.get(n);
			sqlBuilder.append(", ");
			sqlBuilder.append(outputItemEl.getText());
		}
		sqlBuilder.append("\n");
		//Generate the FROM clause
		sqlBuilder.append("FROM ");
		sqlBuilder.append(tableScanned);
		//Check that the generated clauses are valid SQL
		boolean correctSelect = testQuery(sqlBuilder.toString());
		if (!correctSelect) {
			//If not valid SQL then make approximation more rough by selecting *
			sqlBuilder = new StringBuilder("SELECT * \nFROM ").append(tableScanned);
		}
		boolean requireWhere = true;
		//Generate WHERE clause from index condition and query filter
		Element indexCondEl = indexScanEl.element("Index-Cond");
		if (indexCondEl != null) {
			String condText = parseFilter(indexCondEl.getText(), tableScanned);
			if (!condText.equals("")) {
				sqlBuilder.append("\n");
				sqlBuilder.append("WHERE ");
				sqlBuilder.append(condText);
				requireWhere = false;
			}
		}
		Element filterEl = indexScanEl.element("Filter");
		if (filterEl != null) {
			String filterText = parseFilter(filterEl.getText(), tableScanned);
			if (!filterText.equals("")) {
				if (requireWhere) {
					sqlBuilder.append("\n");
					sqlBuilder.append("WHERE ");
				}
				else {
					sqlBuilder.append(" AND ");
				}
				sqlBuilder.append(filterText);
			}
		}
		sqlBuilder.append(";");
		//Annotate the generated statement
		SQLStatement newStatement;
		try {
			newStatement = new SQLStatement(sqlBuilder.toString(), isTableModified, tableScanned, 
					serviceRateCalc.calculateServiceRate(sqlBuilder.toString()), numberOfLoops);
			atomizedStatements.add(newStatement);
		} catch (ServiceCalculatorException e) {
			System.err.println("Unexpected error when calculating service rate for statement:" + sqlBuilder.toString());
			throw new AtomizationException(e);
		}
		
		parsePlans(indexScanEl);
	}
	
	private void parseOtherNodes(Element otherNodeEl) {
		parsePlans(otherNodeEl);
	}
	
	private void parsePlans(Element parentEl) {
		Element plansEl = parentEl.element("Plans");
		if (plansEl != null) {
			for (Object planObj : plansEl.elements("Plan")) {
				parsePlanNode((Element) planObj);
			}
		}
	}
	
	/**
	 * Generates the contents of a WHERE statement using the filter string from the query plan. This requires replacement of references to values from
	 * other tables to be replaced with approximations of those results.
	 * @param filterText
	 * @param tableScanned
	 * @return
	 */
	//Implementation note: The current implementation is quite complex and hard to follow. The reason for this is that postgresql EXPLAIN ANALYZE statements
	//use special syntax that is not thoroughly documented and is difficult to handle without writing a full parser. Which I felt was too much in this case.
	private String parseFilter(String filterText, String tableScanned) {
		String result = filterText;
		boolean inQuotes = false;
		//Check that the filter does not contain results that we cannot approximate (i.e terms containing a '$' that are not a table name or field
		//This is because '$' will denote the result of a subplan or prior query which the tool currently cannot approximate
		for (char filterCh : result.toCharArray()) {
			if (filterCh == '"') {
				inQuotes = !inQuotes;
			}
			else if (filterCh == '$' && !inQuotes) {
				return "";
			}
		}
		//For each table accessed check whether the filter string references that table
		for (String tableAccessed : tableAccessMap.keySet()) {
			if (!tableAccessed.equals(tableScanned)) {
				int unavailAccessIndex = result.indexOf(tableAccessed + ".");
				while (unavailAccessIndex != -1) {
					String partialFilterString = result.substring(unavailAccessIndex);
					int index = 0;
					//Extract the field that is being accessed
					if (partialFilterString.charAt(0) == '"') {
						index++;
						inQuotes = true;
						while (inQuotes) {
							if (partialFilterString.charAt(index) == '"' && partialFilterString.charAt(index+1) == '"') {
								index += 2;
							}
							if (partialFilterString.charAt(index) == '"' && partialFilterString.charAt(index+1) == '"') {
								inQuotes = false;
								index++;
							}
							if (partialFilterString.charAt(index) != '"') {
								index++;
							}
						}
					}
					else {
						char currentChar = partialFilterString.charAt(index);
						while (currentChar != ' ' && currentChar != ')') {
							index++;
							currentChar = partialFilterString.charAt(index);
						}
					}
					String fieldString = partialFilterString.substring(0, index);
					//Retrieve a replacement value for the field
					String replacementField = findReplacement(tableAccessed, fieldString);
					//Insert the replacement value into the resulting filter string
					result = result.replace(fieldString, replacementField);
					unavailAccessIndex = result.indexOf(tableAccessed + ".");
				}
			}
		}
		//Correct for <, >
		result = result.replace("&lt;", "<").replace("&gt;", ">");
		return result;
	}
	/**
	 * Retrieve an approximating value for the field referenced by table and fieldString. The approximating value is taken as the median value of the
	 * sorted list of the values for that field.
	 * @param table
	 * @param fieldString
	 * @return
	 */
	private String findReplacement(String table, String fieldString) {
		String replacementField = "";
		String statement = "SELECT " + fieldString + " FROM " + table + " ORDER BY " + fieldString + ";";
		try {
			Statement stat = conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);
			ResultSet result = stat.executeQuery(statement);
			if (result.last()) {
				int size = result.getRow();
				result.absolute(size/2);
				replacementField = result.getString(1);
			}
		} catch (SQLException e) {
			System.err.println("Unexpected error when splitting JOIN");
			throw new AtomizationException(e);
		}
		
		return "'" + replacementField + "'";
	}

	private boolean isTableBeingModified(String table) {
		Boolean modified = tableAccessMap.get(table);
		return modified != null ? modified : false;
	}
	
	private List<SQLStatement> getAtomizedStatements() {
		return new ArrayList<SQLStatement>(atomizedStatements);
	}

}
