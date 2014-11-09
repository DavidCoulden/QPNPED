package com.dbtool.queueingpetrinet;

public class DeterminsticDistribution implements ServiceDistribution {
	
	private double rate;
	
	public DeterminsticDistribution(double rate) {
		this.rate = rate;
	}
	
	public double getRate() {
		return rate;
	}

	
	@Override
	public Object accept(QPNVisitor visitor) {
		return visitor.visit(this);
	}

}
