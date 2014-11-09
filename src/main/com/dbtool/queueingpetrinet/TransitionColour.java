package com.dbtool.queueingpetrinet;

import java.util.ArrayList;
import java.util.List;

public class TransitionColour extends Colour {

	private double modeFiringRate;
	private List<TransitionModeConnection> incomingArcs;
	private List<TransitionModeConnection> outgoingArcs;
	
	public TransitionColour(String name, double modeFiringRate) {
		super(name);
		this.modeFiringRate = modeFiringRate;
		incomingArcs = new ArrayList<TransitionModeConnection>();
		outgoingArcs = new ArrayList<TransitionModeConnection>();
	}
	
	public void addIncomingArc(Place originPlace, TokenColour tokenColour, QPNWeight tokensReqToFire) {
		incomingArcs.add(new TransitionModeConnection(originPlace, tokenColour, tokensReqToFire));
	}
	
	public void addOutgoingArc(Place targetPlace, TokenColour tokenColour, QPNWeight tokensDeposited) {
		outgoingArcs.add(new TransitionModeConnection(targetPlace, tokenColour, tokensDeposited));
	}
	
	
	public List<TransitionModeConnection> getIncomingArcs() {
		return new ArrayList<TransitionModeConnection>(incomingArcs);
	}

	public List<TransitionModeConnection> getOutgoingArcs() {
		return new ArrayList<TransitionModeConnection>(outgoingArcs);
	}

	public void setFiringRate(double firingRate) {
		this.modeFiringRate = firingRate;
	}
	
	public double getFiringRate() {
		return this.modeFiringRate;
	}
	
	@Override
	public Object accept(QPNVisitor visitor) {
		return visitor.visit(this);
	}

}
