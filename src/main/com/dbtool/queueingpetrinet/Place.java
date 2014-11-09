package com.dbtool.queueingpetrinet;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

public class Place implements QPNComponent {
	private String name;
	private Map<TokenColour, QPNWeight> initialMarking;
	
	public Place(String name) {
		this.initialMarking = new HashMap<TokenColour, QPNWeight>();
		this.name = name;
	}
	
	public String getName() {
		return name;
	}
	
	public void addToInitialMarking(TokenColour colour, QPNWeight marking) {
		this.initialMarking.put(colour, marking);
	}
	
	public Collection<Entry<TokenColour, QPNWeight>> getIntialMarking() {
		return initialMarking.entrySet();
	}
	
	public QPNWeight getMarkingForColour(TokenColour colour) {
		return initialMarking.get(colour);
	}

	@Override
	public Object accept(QPNVisitor visitor) {
		return visitor.visit(this);
	}
}
