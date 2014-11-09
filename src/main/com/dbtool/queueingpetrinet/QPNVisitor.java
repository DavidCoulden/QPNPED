package com.dbtool.queueingpetrinet;

public interface QPNVisitor {
	public Object visit(Colour colour);
	public Object visit(TokenColour colour);
	public Object visit(TransitionColour colour);
	public Object visit(Transition trans);
	public Object visit(TimedTransition trans);
	public Object visit(ImmediateTransition trans);
	public Object visit(Place place);
	public Object visit(QueueingPlace place);
	public Object visit(ImmediateQueueingPlace place);
	public Object visit(TimedQueueingPlace place);
	public Object visit(QueueingPetriNet qpn);
	public Object visit(Queue queue);
	public Object visit(ExponentialDistribution dist);
	public Object visit(DeterminsticDistribution dist);
}
