package com.dbtool.queueingpetrinet;

public interface QPNTranslator extends QPNVisitor {
	public Object translate(QueueingPetriNet qpn);
	public Object translateToViewable(QueueingPetriNet qpn);
}
