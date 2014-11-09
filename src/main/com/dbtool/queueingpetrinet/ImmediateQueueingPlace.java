package com.dbtool.queueingpetrinet;

public class ImmediateQueueingPlace extends QueueingPlace{

	
	public ImmediateQueueingPlace(String name, DepartureDiscipline disc) {
		super(name, disc);
	}
	
	@Override
	public Object accept(QPNVisitor visitor) {
		return visitor.visit(this);
	}
	
}
