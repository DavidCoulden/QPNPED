package com.dbtool.queueingpetrinet;

public class QueueingPlace extends Place {

	protected DepartureDiscipline departureDscpln;
	
	public QueueingPlace(String name, DepartureDiscipline departureDscpln) {
		super(name);
		this.departureDscpln = departureDscpln;
	}
	
	@Override
	public Object accept(QPNVisitor visitor) {
		return visitor.visit(this);
	}
	
	public DepartureDiscipline getDepartureDiscipline() {
		return departureDscpln;
	}
}
