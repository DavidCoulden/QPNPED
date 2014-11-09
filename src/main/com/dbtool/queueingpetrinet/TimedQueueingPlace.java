package com.dbtool.queueingpetrinet;

public class TimedQueueingPlace extends QueueingPlace{

	private Queue placeQueue;
	
	public TimedQueueingPlace(String name, DepartureDiscipline departureDiscpln, Queue placeQueue) {
		super(name, departureDiscpln);
		this.placeQueue = placeQueue;
	}
	
	@Override
	public Object accept(QPNVisitor visitor) {
		return visitor.visit(this);
	}
	
	public Queue getQueue() {
		return placeQueue;
	}

}
