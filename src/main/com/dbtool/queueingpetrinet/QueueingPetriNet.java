package com.dbtool.queueingpetrinet;

import java.util.ArrayList;
import java.util.List;

public class QueueingPetriNet implements QPNComponent {
	private List<TokenColour> colours;
	private List<Place> places;
	private List<Transition> transitions;
	
	public QueueingPetriNet(List<TokenColour> colours, List<Place> places, List<Transition> transitions) {
		this.colours = new ArrayList<TokenColour>(colours);
		this.places = new ArrayList<Place>(places);
		this.transitions = new ArrayList<Transition>(transitions);
	}

	public List<TokenColour> getColours() {
		return colours;
	}

	public List<Place> getPlaces() {
		return places;
	}

	public List<Transition> getTransitions() {
		return transitions;
	}
	
	@Override
	public Object accept(QPNVisitor visitor) {
		return visitor.visit(this);
	}
}
