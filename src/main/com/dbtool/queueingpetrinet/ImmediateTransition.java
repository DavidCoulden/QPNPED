package com.dbtool.queueingpetrinet;

public class ImmediateTransition extends Transition {

	public ImmediateTransition(String name, double firingRate) {
		super(name, firingRate);
	}
	
	@Override
	public Object accept(QPNVisitor visitor) {
		return visitor.visit(this);
	}

}
