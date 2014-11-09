package com.dbtool.queueingpetrinet;

public class TransitionModeConnection {
	private Place connectedPlace;
	private TokenColour tokColour;
	private QPNWeight connectionTokenAmount;
	
	public TransitionModeConnection(Place connectedPlace,
			TokenColour tokColour, QPNWeight connectionTokenAmount) {
		this.connectedPlace = connectedPlace;
		this.tokColour = tokColour;
		this.connectionTokenAmount = connectionTokenAmount;
	}

	public Place getConnectedPlace() {
		return connectedPlace;
	}

	public TokenColour getTokenColour() {
		return tokColour;
	}

	public QPNWeight getArcTokenAmount() {
		return connectionTokenAmount;
	}
	
}
