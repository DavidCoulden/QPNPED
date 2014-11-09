package com.dbtool.queuecreator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.dbtool.queuecreator.AST.IfStatement;
import com.dbtool.queuecreator.AST.SQLStatement;
import com.dbtool.queuecreator.AST.Transaction;
import com.dbtool.queuecreator.AST.TransactionStatement;
import com.dbtool.queuecreator.intermediate.TableConnection;
import com.dbtool.queueingpetrinet.ExponentialDistribution;
import com.dbtool.queueingpetrinet.Queue;
import com.dbtool.queueingpetrinet.TokenColour;

/**
 * Visitor class used to create a pseudo queueing network from a traffic composition. Visits transactions in turn developing the a queue per table accessed
 * and connections that represent transaction structure.
 *
 */
public class QueueNetworkConstructionVisitor extends DefaultDBDesignVisitor {

	private List<TokenColour> tokenColours;
	private Map<String, Queue> queueMap;
	private List<TableConnection> connections;
	
	private TokenColour currentTokenColour;
	private int numberOfIfNodes;
	private String currentTable;
	private double splitProp;
	private LinkedList<DetachedIfState> detachedIfStates;
	
	public QueueNetworkConstructionVisitor() {
		this.tokenColours= new ArrayList<TokenColour>();
		this.queueMap = new HashMap<String, Queue>();
		this.connections = new ArrayList<TableConnection>();
		this.detachedIfStates = new LinkedList<DetachedIfState>();
		splitProp = -1;
		numberOfIfNodes = 0;
	}
	
	@Override
	public void visit(Transaction trans) {
		//Record token colour to represent this transaction
		TokenColour tokColour = new TokenColour(trans.getTransactionName());
		tokenColours.add(tokColour);
		currentTokenColour = tokColour;
		currentTable = TableConnection.NETWORK_SOURCE;
		//Visit each of the statements in the transaction
		for (TransactionStatement stat : trans.getSQLStatements()) {
			stat.accept(this);
		}
		String previousTable = currentTable;
		currentTable = TableConnection.NETWORK_END;
		//Connect last table in transaction to end table
		createConnection(previousTable, currentTable);
		//Connect detached if branches to end table also
		connectDetachedIfNodes();
	}
	
	@Override
	public void visit(IfStatement ifStat) {
		numberOfIfNodes++;
		String previousTable = currentTable;
		//Generate an if node
		String ifNode = TableConnection.getVirtualIfNodeName(numberOfIfNodes);
		currentTable = ifNode;
		//If previous statement was if then create connection with proportion otherwise create regular connection
		if (splitProp != -1) {
			createConnection(previousTable, currentTable, splitProp);
			splitProp = -1;
		}
		else {
			createConnection(previousTable, currentTable);
		}
		//Connect any detached if paths to the created if node
		connectDetachedIfNodes();
		double ifProp = ifStat.getProportionEntered();
		splitProp = ifProp;
		//Visit child statements of if
		for (TransactionStatement stat : ifStat.getEnclosedStatements()) {
			stat.accept(this);
		}
		//Create detached if state for path that did not enter if
		detachedIfStates.add(new DetachedIfState(ifNode, 1 - ifProp));
	}
	
	@Override
	public void visit(SQLStatement sqlStat) {
		String table = sqlStat.getTableAccessed();
		//Create queue if no queue representing this table exists
		Queue queue = queueMap.get(table);
		if (queue == null) {
			queue = Queue.createInfiniteServerQueue(table);
			queueMap.put(table, queue);
		}
		queue.addServicableToken(currentTokenColour, new ExponentialDistribution(sqlStat.getTotalServiceRate()), sqlStat.isExclusive());
		String previousTable = currentTable;
		currentTable = table;
		//Connect detached if paths to this table
		connectDetachedIfNodes();
		//If previous statement was if then create connection with proportion otherwise create regular connection
		if (splitProp != -1) {
			createConnection(previousTable, currentTable, splitProp);
			splitProp = -1;
		}
		else {
			createConnection(previousTable, currentTable);
		}
	}
	
	private void connectDetachedIfNodes() {
		while (!detachedIfStates.isEmpty()) {
			DetachedIfState state = detachedIfStates.remove();
			createConnection(state.detachedIfTable, currentTable, state.detachedIfProp);
		}
	}
	
	private void createConnection(String source, String dest) {
		connections.add(new TableConnection(source, dest, currentTokenColour));
	}
	
	private void createConnection(String source, String dest, double prop) {
		connections.add(new TableConnection(source, dest, currentTokenColour, prop));
	}
	
	public List<Queue> getQueues() {
		return new ArrayList<Queue>(queueMap.values());
	}

	public Map<String, Queue> getQueueMap() {
		return queueMap;
	}
	
	public int getNumberOfIfNodes() {
		return numberOfIfNodes;
	}

	public List<TableConnection> getQNConnections() {
		return new ArrayList<TableConnection>(connections);
	}

	public List<TokenColour> getTokenColours() {
		return new ArrayList<TokenColour>(tokenColours);
	}
	
	//A simple class to record if branches that have not yet been explored in the recursion
	private class DetachedIfState {
		public String detachedIfTable;
		public double detachedIfProp;
		
		public DetachedIfState(String detachedIfTable, double detachedIfProp) {
			this.detachedIfTable = detachedIfTable;
			this.detachedIfProp = detachedIfProp;
		}
	}
	
	
}
