package com.dbtool.queuecreator;

import com.dbtool.queuecreator.AST.IfStatement;
import com.dbtool.queuecreator.AST.SQLStatement;
import com.dbtool.queuecreator.AST.TrafficComposition;
import com.dbtool.queuecreator.AST.Transaction;
import com.dbtool.queuecreator.AST.TransactionStatement;
import com.dbtool.queuecreator.AST.TransactionStatementBlock;


public interface DBDesignVisitor {
	public void visit(TrafficComposition composition);
	public void visit(Transaction trans);
	public void visit(TransactionStatement transStat);
	public void visit(SQLStatement sqlStat);
	public void visit(IfStatement ifStat);
	public void visit(TransactionStatementBlock transBlock);
}
