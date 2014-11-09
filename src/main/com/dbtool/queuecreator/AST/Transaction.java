package com.dbtool.queuecreator.AST;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.dbtool.queuecreator.DBDesignComponent;
import com.dbtool.queuecreator.DBDesignVisitor;

public class Transaction implements DBDesignComponent {
	private String name;
	private List<TransactionStatement> statements;

	public Transaction(String name, TransactionStatement... statements) {
		this.name = name;
		this.statements = Arrays.asList(statements);
	}
	
	public Transaction(String name, List<TransactionStatement> statements) {
		this.name = name;
		this.statements = new ArrayList<TransactionStatement>(statements);
	}
	
	@Override
	public void accept(DBDesignVisitor dbVisitor) {
		dbVisitor.visit(this);
	}
	
	public List<TransactionStatement> getSQLStatements() {
		return new ArrayList<TransactionStatement>(statements);
	}
	
	public String getTransactionName() {
		return name;
	}
}
