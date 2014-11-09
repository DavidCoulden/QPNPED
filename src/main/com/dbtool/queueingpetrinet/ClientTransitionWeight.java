package com.dbtool.queueingpetrinet;

public class ClientTransitionWeight implements QPNWeight {
	
	public static final String CLIENT_WEIGHT_STRING = "NUMBER_OF_CLIENTS";
	private static final ClientTransitionWeight clientWeight = new ClientTransitionWeight();
	
	private ClientTransitionWeight() {}

	@Override
	public String getWeightAsString() {
		return CLIENT_WEIGHT_STRING;
	}
	
	public static ClientTransitionWeight getNumberOfClientWeight() {
		return clientWeight;
	}

}
