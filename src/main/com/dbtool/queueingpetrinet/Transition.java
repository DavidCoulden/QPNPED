package com.dbtool.queueingpetrinet;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class Transition implements QPNComponent {
	private String name;
	private double firingRate;
	private Map<String, TransitionColour> nameToModeMap;	
	
	public Transition(String name, double firingRate) {
		this.name = name;
		this.firingRate = firingRate;
		nameToModeMap = new HashMap<String, TransitionColour>();
	}
	
	public void addFiringMode(TransitionColour modeColour) {
		nameToModeMap.put(modeColour.getColourName(), modeColour);
	}
	
	public String getTransitionName() {
		return name;
	}
	
	public TransitionColour getTransitionColour(String colourName) {
		return nameToModeMap.get(colourName);
	}
	
	public Collection<TransitionColour> getAllTransitionColours() {
		return nameToModeMap.values();
	}
	
	public double getFiringRate() {
		return firingRate;
	}

	@Override
	public Object accept(QPNVisitor visitor) {
		return visitor.visit(this);
	}
	
	
}
