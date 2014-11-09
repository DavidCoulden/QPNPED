package com.dbtool.queuecreator;

import com.dbtool.queuecreator.AST.IfStatement;
import com.dbtool.queuecreator.AST.SQLStatement;
import com.dbtool.queuecreator.AST.TrafficComposition;
import com.dbtool.queuecreator.AST.Transaction;
import com.dbtool.queuecreator.AST.TransactionStatement;
import com.dbtool.queuecreator.AST.TransactionStatementBlock;

public class DefaultDBDesignVisitor implements DBDesignVisitor {

	@Override
	public void visit(TrafficComposition composition) {
		for (Transaction t : composition.getTransactions()) {
			t.accept(this);
		}
	}

	@Override
	public void visit(Transaction trans) {
		for (TransactionStatement stat : trans.getSQLStatements()) {
			stat.accept(this);
		}
	}

	@Override
	public void visit(TransactionStatement transStat) {
		//By default do nothing
	}

	@Override
	public void visit(SQLStatement sqlStat) {
		//By default do nothing
	}

	@Override
	public void visit(IfStatement ifStat) {
		for (TransactionStatement stat : ifStat.getEnclosedStatements()) {
			stat.accept(this);
		}
	}

	@Override
	public void visit(TransactionStatementBlock transBlock) {
		for (TransactionStatement stat : transBlock.getStatements()) {
			stat.accept(this);
		}
		
	}
}
