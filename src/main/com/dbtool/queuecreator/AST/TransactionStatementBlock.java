package com.dbtool.queuecreator.AST;

import java.util.ArrayList;
import java.util.List;

import com.dbtool.queuecreator.DBDesignComponent;
import com.dbtool.queuecreator.DBDesignVisitor;

public class TransactionStatementBlock implements DBDesignComponent {

	private List<TransactionStatement> statements;
	
	public TransactionStatementBlock(List<TransactionStatement> statements) {
		this.statements = new ArrayList<TransactionStatement>(statements);
	}
	
	public List<TransactionStatement> getStatements() {
		return statements;
	}

	@Override
	public void accept(DBDesignVisitor dbVisitor) {
		dbVisitor.visit(this);
	}

}
