package com.dbtool.queuecreator.intermediate;

import com.dbtool.queueingpetrinet.TokenColour;

public class TableConnection {
	
	public static final String NETWORK_SOURCE = "__SOURCE__";
	public static final String NETWORK_END = "__END__";
	public static final String IF_NODE = "__IF__";
	private String sourceTable;
	private String targetTable;
	private TokenColour tokenColour;
	private double proportion;
	
	public static String getVirtualIfNodeName(int node) {
		return IF_NODE + node;
	}
	
	public TableConnection(String sourceTable, String targetTable, TokenColour tokenColour) {
		this.sourceTable = sourceTable;
		this.targetTable = targetTable;
		this.tokenColour = tokenColour;
		this.proportion = 1;
	}
	
	public TableConnection(String sourceTable, String targetTable, TokenColour tokenColour, double proportion) {
		this.sourceTable = sourceTable;
		this.targetTable = targetTable;
		this.tokenColour = tokenColour;
		this.proportion = proportion;
	}

	public String getSourceTable() {
		return sourceTable;
	}

	public String getTargetTable() {
		return targetTable;
	}

	public TokenColour getTokenColour() {
		return tokenColour;
	}

	public double getProportion() {
		return proportion;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((sourceTable == null) ? 0 : sourceTable.hashCode());
		result = prime * result
				+ ((targetTable == null) ? 0 : targetTable.hashCode());
		result = prime * result
				+ ((tokenColour == null) ? 0 : tokenColour.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		TableConnection other = (TableConnection) obj;
		if (sourceTable == null) {
			if (other.sourceTable != null)
				return false;
		} else if (!sourceTable.equals(other.sourceTable))
			return false;
		if (targetTable == null) {
			if (other.targetTable != null)
				return false;
		} else if (!targetTable.equals(other.targetTable))
			return false;
		if (tokenColour == null) {
			if (other.tokenColour != null)
				return false;
		} else if (!tokenColour.equals(other.tokenColour))
			return false;
		return true;
	}
	
}
