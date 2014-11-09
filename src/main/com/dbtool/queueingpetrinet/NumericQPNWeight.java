package com.dbtool.queueingpetrinet;

public class NumericQPNWeight implements QPNWeight {
	private Integer immediateWeight;
	public static final NumericQPNWeight MAX_TOKENS = new NumericQPNWeight();
	
	public NumericQPNWeight(int immediateWeight) {
		this.immediateWeight = new Integer(immediateWeight);
	}
	
	private NumericQPNWeight() {
		immediateWeight = null;
	}
	
	public Integer getNumericWeight() {
		return immediateWeight;
	}
	
	public String getWeightAsString() {
		return String.valueOf(getNumericWeight());
	}
}
