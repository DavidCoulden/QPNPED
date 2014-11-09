package com.dbtool.queuecreator;

import static org.junit.Assert.*;

import java.util.List;
import java.util.Map;

import org.junit.Test;

import com.dbtool.queuecreator.AST.IfStatement;
import com.dbtool.queuecreator.AST.SQLStatement;
import com.dbtool.queuecreator.AST.TrafficComposition;
import com.dbtool.queuecreator.AST.Transaction;
import com.dbtool.queuecreator.intermediate.TableConnection;
import com.dbtool.queueingpetrinet.Queue;
import com.dbtool.queueingpetrinet.TokenColour;

public class QueueNetworkConstructionTest {

	private static final double EPSILON = 1e-10;
	private VirtualIfNodeRemover ifNodeRemover = new VirtualIfNodeRemover();
	
	@Test
	public void testConstructionFromSimpleDesign() {
		Transaction trans = new Transaction("Trans 1", new SQLStatement("ARBITRARY SQL", false, "A", 0.25), 
													   new SQLStatement("SQL", false, "B", 0.25));
		QueueNetworkConstructionVisitor qnConstructVisitor = new QueueNetworkConstructionVisitor();
		qnConstructVisitor.visit(trans);
		TokenColour trans1Token = new TokenColour("Trans 1");
		List<TokenColour> tokens = qnConstructVisitor.getTokenColours();
		assertTrue(tokens.contains(trans1Token));
		Map<String, Queue> queueMap = qnConstructVisitor.getQueueMap();
		Queue queueA = queueMap.get("A");
		Queue queueB = queueMap.get("B");
		assertTrue(queueA != null);
		assertTrue(queueB != null);
		assertEquals(queueA.getServiceableTokens().size(), 1);
		assertEquals(queueB.getServiceableTokens().size(), 1);
		List<TableConnection> connections = qnConstructVisitor.getQNConnections();
		assertEquals(connections.size(), 3);
		checkContainsConnection(connections, TableConnection.NETWORK_SOURCE, "A", trans1Token);
		checkContainsConnection(connections, "A", "B", trans1Token);
		checkContainsConnection(connections, "B", TableConnection.NETWORK_END, trans1Token);
		
	}
	
	@Test
	public void testConstructionFromTwoTransDesign() {
		Transaction trans1 = new Transaction("Trans 1", new SQLStatement("ARBITRARY SQL", false, "A", 0.25), 
				   									    new SQLStatement("SQL", false, "B", 0.25));
		Transaction trans2 = new Transaction("Trans 2", new SQLStatement("SQL", false, "B", 0.25),
														new SQLStatement("SQL", false, "A", 0.25));
		TrafficComposition traffComp = new TrafficComposition(trans1, trans2);
		QueueNetworkConstructionVisitor qnConstructVisitor = new QueueNetworkConstructionVisitor();
		qnConstructVisitor.visit(traffComp);
		TokenColour trans1Token = new TokenColour("Trans 1");
		TokenColour trans2Token = new TokenColour("Trans 2");
		List<TokenColour> tokens = qnConstructVisitor.getTokenColours();
		assertTrue(tokens.contains(trans1Token));
		assertTrue(tokens.contains(trans2Token));
		Map<String, Queue> queueMap = qnConstructVisitor.getQueueMap();
		Queue queueA = queueMap.get("A");
		Queue queueB = queueMap.get("B");
		assertTrue(queueA != null);
		assertTrue(queueB != null);
		assertEquals(queueA.getServiceableTokens().size(), 2);
		assertEquals(queueB.getServiceableTokens().size(), 2);
		List<TableConnection> connections = qnConstructVisitor.getQNConnections();
		assertEquals(connections.size(), 6);
		checkContainsConnection(connections, TableConnection.NETWORK_SOURCE, "A", trans1Token);
		checkContainsConnection(connections, "A", "B", trans1Token);
		checkContainsConnection(connections, "B", TableConnection.NETWORK_END, trans1Token);
		checkContainsConnection(connections, TableConnection.NETWORK_SOURCE, "B", trans2Token);
		checkContainsConnection(connections, "B", "A", trans2Token);
		checkContainsConnection(connections, "A", TableConnection.NETWORK_END, trans2Token);
	}
	
	@Test
	public void testConstructionUsingIf() {
		Transaction ifTrans = new Transaction("If trans", new IfStatement(0.2, new SQLStatement("SQL", false, "A", 0.25),
																			   new SQLStatement("SQL", false, "B", 0.25)),
														  new SQLStatement("SQL", false, "C", 0.25),
														  new SQLStatement("SQL", false, "D", 0.25));
		QueueNetworkConstructionVisitor qnConstructVisitor = new QueueNetworkConstructionVisitor();
		qnConstructVisitor.visit(ifTrans);
		TokenColour ifTransToken = new TokenColour("If trans");
		List<TokenColour> tokens = qnConstructVisitor.getTokenColours();
		assertEquals(tokens.size(), 1);
		assertTrue(tokens.contains(ifTransToken));
		Map<String, Queue> queueMap = qnConstructVisitor.getQueueMap();
		assertEquals(queueMap.size(), 4);
		List<TableConnection> connections = qnConstructVisitor.getQNConnections();
		assertEquals(connections.size(), 7);
		String ifNode1 = TableConnection.getVirtualIfNodeName(1);
		checkContainsConnection(connections, TableConnection.NETWORK_SOURCE, ifNode1, ifTransToken);
		checkContainsConnectionWithProp(connections, ifNode1, "A", ifTransToken, 0.2);
		checkContainsConnection(connections, "A", "B", ifTransToken);
		checkContainsConnection(connections, "B", "C", ifTransToken);
		checkContainsConnectionWithProp(connections, ifNode1, "C", ifTransToken, 0.8);
		checkContainsConnection(connections, "C", "D", ifTransToken);
		checkContainsConnection(connections, "D", TableConnection.NETWORK_END, ifTransToken);
		connections = ifNodeRemover.removeIfNodes(connections, qnConstructVisitor.getNumberOfIfNodes());
		assertEquals(connections.size(), 6);
		checkContainsConnectionWithProp(connections, TableConnection.NETWORK_SOURCE, "A", ifTransToken, 0.2);
		checkContainsConnection(connections, "A", "B", ifTransToken);
		checkContainsConnection(connections, "B", "C", ifTransToken);
		checkContainsConnectionWithProp(connections, TableConnection.NETWORK_SOURCE, "C", ifTransToken, 0.8);
		checkContainsConnection(connections, "C", "D", ifTransToken);
		checkContainsConnection(connections, "D", TableConnection.NETWORK_END, ifTransToken);
	}
	
	@Test
	public void testNestedIfs() {
		Transaction ifTrans = new Transaction("If trans", new IfStatement(0.2, new IfStatement(0.3, new SQLStatement("SQL", false, "A", 0.25))));
		QueueNetworkConstructionVisitor qnConstructVisitor = new QueueNetworkConstructionVisitor();
		qnConstructVisitor.visit(ifTrans);
		TokenColour ifTransToken = new TokenColour("If trans");
		List<TokenColour> tokens = qnConstructVisitor.getTokenColours();
		assertEquals(tokens.size(), 1);
		assertTrue(tokens.contains(ifTransToken));
		Map<String, Queue> queueMap = qnConstructVisitor.getQueueMap();
		assertEquals(queueMap.size(), 1);
		List<TableConnection> connections = qnConstructVisitor.getQNConnections();
		assertEquals(connections.size(), 6);
		String ifNode1 = TableConnection.getVirtualIfNodeName(1);
		String ifNode2 = TableConnection.getVirtualIfNodeName(2);
		checkContainsConnection(connections, TableConnection.NETWORK_SOURCE, ifNode1, ifTransToken);
		checkContainsConnectionWithProp(connections, ifNode1, ifNode2, ifTransToken, 0.2);
		checkContainsConnectionWithProp(connections, ifNode2, "A", ifTransToken, 0.3);
		checkContainsConnectionWithProp(connections, ifNode1, TableConnection.NETWORK_END, ifTransToken, 0.8);
		checkContainsConnectionWithProp(connections, ifNode2, TableConnection.NETWORK_END, ifTransToken, 0.7);
		checkContainsConnection(connections, "A", TableConnection.NETWORK_END, ifTransToken);
		connections = ifNodeRemover.removeIfNodes(connections, qnConstructVisitor.getNumberOfIfNodes());
		assertEquals(connections.size(), 3);
		checkContainsConnectionWithProp(connections, TableConnection.NETWORK_SOURCE, "A", ifTransToken, 0.2*0.3);
		checkContainsConnectionWithProp(connections, TableConnection.NETWORK_SOURCE, TableConnection.NETWORK_END, ifTransToken, 1 - 0.2*0.3);
		checkContainsConnection(connections, "A", TableConnection.NETWORK_END, ifTransToken);
		
	}
	
	private void checkContainsConnection(List<TableConnection> connections, String sourceTable, String endTable, TokenColour connectedColour) {
		assertTrue(connections.contains(new TableConnection(sourceTable, endTable, connectedColour)));
	}
	
	private void checkContainsConnectionWithProp(List<TableConnection> connections, String sourceTable, String endTable, 
													TokenColour connectedColour, double proportion) {
		TableConnection connection = new TableConnection(sourceTable, endTable, connectedColour);
		int indexOfConnection = connections.indexOf(connection);
		assertTrue(indexOfConnection != -1);
		TableConnection actualConnection = connections.get(indexOfConnection);
		assertEquals(proportion, actualConnection.getProportion(), EPSILON);
	}

}
