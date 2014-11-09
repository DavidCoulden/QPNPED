package com.dbtool.queueingpetrinet;

public class ExponentialDistribution implements ServiceDistribution {
	private double lambda;
	
	public ExponentialDistribution(double lambda) {
		this.lambda = lambda;
	}
	
	public double getLambda() {
		return lambda;
	}

	@Override
	public Object accept(QPNVisitor visitor) {
		return visitor.visit(this);
	}
}
