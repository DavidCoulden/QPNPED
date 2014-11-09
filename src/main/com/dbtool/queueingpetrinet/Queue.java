package com.dbtool.queueingpetrinet;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Queue implements QPNComponent {
	private QueueSchedule schedule;
	private int numberOfServers;
	private Map<TokenColour, ServiceDistribution> tokenServiceMap;
	private Map<TokenColour, Boolean> exclusivityMap;
	private String name;
	
	public Queue(String name, QueueSchedule schedule, int numberOfServers) {
		this.name = name;
		this.schedule = schedule;
		this.numberOfServers = numberOfServers;
		this.tokenServiceMap = new HashMap<TokenColour, ServiceDistribution>();
		this.exclusivityMap = new HashMap<TokenColour, Boolean>();
	}
	
	public static Queue createInfiniteServerQueue(String name) {
		return new Queue(name, QueueSchedule.INFINITE_SERVER, 1);
	}
	
	public void addServicableToken(TokenColour tokenColour, ServiceDistribution dist) {
		tokenServiceMap.put(tokenColour, dist);
		exclusivityMap.put(tokenColour, false);
	}
	
	public void addServicableToken(TokenColour tokenColour, ServiceDistribution dist, boolean exclusive) {
		tokenServiceMap.put(tokenColour, dist);
		exclusivityMap.put(tokenColour, exclusive);
	}
	
	public List<TokenColour> getServiceableTokens() {
		return new ArrayList<TokenColour>(tokenServiceMap.keySet());
	}
	
	public String getName() {
		return name;
	}
	
	public ServiceDistribution getTokenDistribution(TokenColour tokColour) {
		return tokenServiceMap.get(tokColour);
	}
	
	public Boolean getTokenExclusivity(TokenColour tokColour) {
		return exclusivityMap.get(tokColour);
	}
	
	public QueueSchedule getSchedule() {
		return schedule;
	}

	public int getNumberOfServers() {
		return numberOfServers;
	}

	@Override
	public Object accept(QPNVisitor visitor) {
		return visitor.visit(this);
	}
}
