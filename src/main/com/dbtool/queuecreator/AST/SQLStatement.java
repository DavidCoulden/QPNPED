package com.dbtool.queuecreator.AST;

import com.dbtool.queuecreator.DBDesignVisitor;

public class SQLStatement extends TransactionStatement  {
	private String sql;
	private boolean exclusive;
	private boolean inferred;
	private double serviceRate;
	private String tableAccessed;
	private int numberOfExecutions;
	
	
	public SQLStatement(String sql) {
		this.sql = sql;
		this.exclusive = false;
		this.serviceRate = 0;
		this.tableAccessed = "";
		this.numberOfExecutions = 1;
		this.inferred = true;
	}
	
	public SQLStatement(String sql, boolean exclusive, String tableAccessed, double serviceRate) {
		this.sql = sql;
		this.exclusive = exclusive;
		this.tableAccessed = tableAccessed;
		this.serviceRate = serviceRate;
		this.numberOfExecutions = 1;
		this.inferred = true;
	}
	
	public SQLStatement(String sql, boolean exclusive, String tableAccessed, double serviceRate, int numberOfExecutions) {
		this.sql = sql;
		this.exclusive = exclusive;
		this.tableAccessed = tableAccessed;
		this.serviceRate = serviceRate;
		this.numberOfExecutions = numberOfExecutions;
		this.inferred = true;
	}

	
	@Override
	public void accept(DBDesignVisitor dbVisitor) {
		dbVisitor.visit(this);
	}
	
	public String getSQLContent() {
		return sql;
	}
	
	public boolean isExclusive() {
		return exclusive;
	}
	
	public boolean isInferred() {
		return inferred;
	}
	
	public String getTableAccessed() {
		return tableAccessed;
	}
	
	public double getServiceRate() {
		return serviceRate;
	}
	
	public double getServiceTime() {
		return 1/serviceRate;
	}
	
	public double getNumberOfExecutions() {
		return numberOfExecutions;
	}
	
	public double getTotalServiceRate() {
		return getServiceRate() / numberOfExecutions;
	}
	
	public double getTotalServiceTime() {
		return getServiceTime() * numberOfExecutions;
	}
	
	
}
