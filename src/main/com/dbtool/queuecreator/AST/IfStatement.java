package com.dbtool.queuecreator.AST;

import java.util.Arrays;
import java.util.List;

import com.dbtool.queuecreator.DBDesignVisitor;

public class IfStatement extends TransactionStatement {
	private List<TransactionStatement> enclosedStatements;
	private double proportionEntered;
	
	public IfStatement(List<TransactionStatement> stat) {
		this.enclosedStatements = stat;
		this.proportionEntered = 0.5;
	}
	
	public IfStatement(TransactionStatement...statements ) {
		this.enclosedStatements = Arrays.asList(statements);
		this.proportionEntered = 0.5;
	}
	
	public IfStatement(List<TransactionStatement> stat, double proportion) {
		this.enclosedStatements = stat;
		this.proportionEntered = proportion;
	}
	
	public IfStatement(double proportion, TransactionStatement...statements) {
		this.enclosedStatements = Arrays.asList(statements);
		this.proportionEntered = proportion;
	}

	@Override
	public void accept(DBDesignVisitor dbVisitor) {
		dbVisitor.visit(this);
	}
	
	public List<TransactionStatement> getEnclosedStatements() {
		return enclosedStatements;
	}
	
	public double getProportionEntered() {
		return proportionEntered;
	}
}
