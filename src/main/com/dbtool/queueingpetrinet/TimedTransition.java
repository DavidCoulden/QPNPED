package com.dbtool.queueingpetrinet;

public class TimedTransition extends Transition{

	public TimedTransition(String name, double firingRate) {
		super(name, firingRate);
	}
	
	@Override
	public Object accept(QPNVisitor visitor) {
		return visitor.visit(this);
	}

}
