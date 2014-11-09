package com.dbtool.queuecreator.NonTransTableLevel;

import java.util.List;

import com.dbtool.queuecreator.QueueNetworkConstructionVisitor;
import com.dbtool.queuecreator.SpecificationToQPNTranslator;
import com.dbtool.queuecreator.VirtualIfNodeRemover;
import com.dbtool.queuecreator.AST.TrafficComposition;
import com.dbtool.queuecreator.intermediate.TableConnection;
import com.dbtool.queueingpetrinet.QueueingPetriNet;
/**
 * Implementation of the specification to QPN model translation, using non-transactional table-level locking semantics.
 *
 */
public class NonTransTableLevelTranslator implements SpecificationToQPNTranslator {
	
	@Override
	public QueueingPetriNet translateSpecification(TrafficComposition composition) {
		QueueNetworkConstructionVisitor qnVisitor = new QueueNetworkConstructionVisitor();
		qnVisitor.visit(composition);
		VirtualIfNodeRemover virtualNodeRemover = new VirtualIfNodeRemover();
		List<TableConnection> connections = virtualNodeRemover.removeIfNodes(qnVisitor.getQNConnections(), qnVisitor.getNumberOfIfNodes());

		QPNNonTransTableLevelConstructor qpnConstructor = new QPNNonTransTableLevelConstructor(composition, qnVisitor.getTokenColours(), qnVisitor.getQueues(), connections);
		qpnConstructor.constructQueueingPetriNet();
		return qpnConstructor.getQPN();
	}

}
