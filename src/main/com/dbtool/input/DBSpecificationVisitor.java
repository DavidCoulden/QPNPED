package com.dbtool.input;

import java.util.ArrayList;
import java.util.List;

import com.dbtool.queuecreator.DBDesignComponent;
import com.dbtool.queuecreator.AST.IfStatement;
import com.dbtool.queuecreator.AST.SQLStatement;
import com.dbtool.queuecreator.AST.TrafficComposition;
import com.dbtool.queuecreator.AST.Transaction;
import com.dbtool.queuecreator.AST.TransactionStatement;
import com.dbtool.queuecreator.AST.TransactionStatementBlock;
import com.dbtool.utils.DoubleUtil;
import com.dbtool.utils.Pair;
/**
 * This class translates the ANTLR syntactic AST classes into traffic compositions.
 *
 */
public class DBSpecificationVisitor extends
		DBPerfToolGrammarBaseVisitor<DBDesignComponent> {
	
	private boolean expectDetailedSpec;
	
	public DBSpecificationVisitor() {
		super();
	}
	
	public DBSpecificationVisitor(boolean expectDetailedSpec) {
		super();
		this.expectDetailedSpec = expectDetailedSpec;
	}
	
	@Override 
	public DBDesignComponent visitDetailedSqlStatement(DBPerfToolGrammarParser.DetailedSqlStatementContext ctx) {
		SQLStatement sql = (SQLStatement) visit(ctx.sqlstatement());
		SQLStatement stat;
		boolean foundExcl = false, foundTable = false, foundRuntime = false;
		double serviceTime = 1;
		boolean exclusive = false;
		String table = "";
		//Loop through attributes and perform checks that any that are required are present
		for (DBPerfToolGrammarParser.AttributeContext attributeCtx : ctx.attribute()) {
			if (attributeCtx.EXCLUSIVE() != null) {
				if (foundExcl) {
					int lineNo = attributeCtx.EXCLUSIVE().getSymbol().getLine();
					throw new SpecParseException("Found multiple definitions for exclusivity", lineNo);
				}
				exclusive = Boolean.parseBoolean(attributeCtx.BOOLEAN().getSymbol().getText());
				foundExcl = true;
			}
			else if (attributeCtx.TABLE() != null) {
				if (foundTable) {
					int lineNo = attributeCtx.EXCLUSIVE().getSymbol().getLine();
					throw new SpecParseException("Found multiple definitions for table accessed", lineNo); 
				}
				table = attributeCtx.ID().getSymbol().getText();
				foundTable = true;
			}
			else if (attributeCtx.RUNTIME() != null) {
				if (foundRuntime) {
					int lineNo = attributeCtx.EXCLUSIVE().getSymbol().getLine();
					throw new SpecParseException("Found multiple definitions for mean runtime", lineNo);
				}
				serviceTime = Double.parseDouble(attributeCtx.FLOAT().getSymbol().getText());
				if (DoubleUtil.fuzzyEqual(serviceTime, 0)) {
					int lineNo = attributeCtx.EXCLUSIVE().getSymbol().getLine();
					throw new SpecParseException("Runtime specified is zero", lineNo);
				}
				foundRuntime = true;
			}
			else {
				int lineNo = attributeCtx.EXCLUSIVE().getSymbol().getLine();
				throw new SpecParseException("Unexpected attribute on line ", lineNo);
			}
		}
		if (expectDetailedSpec) {
			if (!(foundExcl && foundTable && foundRuntime)) {
				int lineNo = ctx.start.getLine();
				throw new SpecParseException("Expected statement to be fully specified", lineNo);
			}
			stat = new SQLStatement(sql.getSQLContent(), exclusive, table, 1/serviceTime);
		}
		else {
			stat = sql;
		}
		return stat; 
	}

	@Override 
	public DBDesignComponent visitSqlstatement(DBPerfToolGrammarParser.SqlstatementContext ctx) { 
		if (expectDetailedSpec) {
			int lineNo = ctx.start.getLine();
			throw new SpecParseException("Expected statement to be fully specified", lineNo);
		}
		String rawSqlText = ctx.SQLTEXT().getSymbol().getText();
		//Remove the '-'
		rawSqlText = rawSqlText.substring(1);
		return new SQLStatement(rawSqlText); 
	}

	@Override 
	public DBDesignComponent visitRawSqlStatement(DBPerfToolGrammarParser.RawSqlStatementContext ctx) {
		SQLStatement sql = (SQLStatement) visit(ctx.sqlstatement());
		return sql; 
	}

	@Override 
	public DBDesignComponent visitTransaction(DBPerfToolGrammarParser.TransactionContext ctx) {
		String transactionName = ctx.ID().getSymbol().getText();
		TransactionStatementBlock block = (TransactionStatementBlock) visit(ctx.transactionBody());
		return new Transaction(transactionName, block.getStatements()); 
	}

	@Override 
	public DBDesignComponent visitSpecification(DBPerfToolGrammarParser.SpecificationContext ctx) {
		List<Pair<Transaction, Double>> trafficProps = new ArrayList<Pair<Transaction, Double>>();
		double totalProp = 0;
		for (DBPerfToolGrammarParser.TransactionContext transCtx : ctx.transaction()) {
			double proportion = Double.valueOf(transCtx.FLOAT().getSymbol().getText());
			Transaction trans = (Transaction) visitTransaction(transCtx);
			trafficProps.add(new Pair<Transaction, Double>(trans, proportion));
			totalProp += proportion;
		}
		if (!DoubleUtil.fuzzyEqual(totalProp, 1.0)) {
			int lineNo = ctx.start.getLine();
			throw new SpecParseException("The transaction workload proportions do not add up to 1.0", lineNo);
		}
		return new TrafficComposition(trafficProps); 
	}

	@Override 
	public DBDesignComponent visitTransactionBody(DBPerfToolGrammarParser.TransactionBodyContext ctx) {
		List<TransactionStatement> statements = new ArrayList<TransactionStatement>();
		for (DBPerfToolGrammarParser.StatementContext statCtx : ctx.statement()) {
			TransactionStatement stat = (TransactionStatement) visit(statCtx);
			statements.add(stat);
		}
		return new TransactionStatementBlock(statements); 
	}

	@Override 
	public DBDesignComponent visitIfStatement(DBPerfToolGrammarParser.IfStatementContext ctx) {
		double ifEnterProp = Double.valueOf(ctx.FLOAT().getSymbol().getText());
		if (ifEnterProp > 1.0) {
			int lineNo = ctx.FLOAT().getSymbol().getLine();
			throw new SpecParseException("The if proportion is " + ifEnterProp + " which is greater than 1.0", lineNo);
		}
		TransactionStatementBlock statements = (TransactionStatementBlock) visit(ctx.transactionBody());
		return new IfStatement(statements.getStatements(), ifEnterProp); 
	}
	

}
