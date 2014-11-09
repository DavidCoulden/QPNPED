package com.dbtool.queuecreator;

import com.dbtool.queuecreator.AST.TrafficComposition;
import com.dbtool.queueingpetrinet.QueueingPetriNet;

public interface SpecificationToQPNTranslator {

	public QueueingPetriNet translateSpecification(
			TrafficComposition composition);

}