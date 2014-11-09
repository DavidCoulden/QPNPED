package com.dbtool.queueingpetrinet;

public interface QPNComponent {
	public Object accept(QPNVisitor visitor);
}
