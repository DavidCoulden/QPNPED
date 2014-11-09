package com.dbtool.queuecreator;

import java.util.ArrayList;
import java.util.List;

import com.dbtool.queuecreator.AST.IfStatement;
import com.dbtool.queuecreator.AST.SQLStatement;
import com.dbtool.queuecreator.AST.TrafficComposition;
import com.dbtool.queuecreator.AST.Transaction;
import com.dbtool.queuecreator.AST.TransactionStatement;
import com.dbtool.utils.Pair;
/**
 * The atomizer visitor that traverses a traffic composition and atomizes each statement.
 *
 */
public class DBDesignAtomizationVisitor extends DefaultDBDesignVisitor {

	private QueryAtomizer atomizer;
	private List<TransactionStatement> atomizedStatements;
	private Transaction atomizedTransaction;
	private TrafficComposition atomizedComposition;

	public DBDesignAtomizationVisitor(QueryAtomizer atomizer) {
		this.atomizer = atomizer;
	}
	
	@Override
	public void visit(TrafficComposition composition) {
		
		List<Pair<Transaction, Double>> atomizedTransactionTraffic = new ArrayList<Pair<Transaction, Double>>();
		for (Transaction t : composition.getTransactions()) {
			t.accept(this);
			atomizedTransactionTraffic.add(new Pair<Transaction, Double>(atomizedTransaction, composition.getProportionForTrans(t)));
		}
		atomizedComposition = new TrafficComposition(atomizedTransactionTraffic);
	}
	
	@Override
	public void visit(Transaction trans) {
		atomizedStatements = new ArrayList<TransactionStatement>();
		for (TransactionStatement stat : trans.getSQLStatements()) {
			stat.accept(this);
		}
		atomizedTransaction = new Transaction(trans.getTransactionName(), atomizedStatements);
	}
	
	@Override
	public void visit (IfStatement ifStat) {
		List<TransactionStatement> priorAtomizedStatements = atomizedStatements;
		atomizedStatements = new ArrayList<TransactionStatement>();
		for (TransactionStatement stat : ifStat.getEnclosedStatements()) {
			stat.accept(this);
		}
		IfStatement atomizedIf = new IfStatement(atomizedStatements, ifStat.getProportionEntered());
		priorAtomizedStatements.add(atomizedIf);
		atomizedStatements = priorAtomizedStatements;
	}
	
	@Override
	public void visit(SQLStatement sqlStat) {
		if (sqlStat.isInferred()) {
			atomizedStatements.addAll(atomizer.atomizeSqlStatment(sqlStat));
		}
		else {
			atomizedStatements.add(sqlStat);
		}
	}
	
	public TrafficComposition getAtomizedComposition() {
		return atomizedComposition;
	}
}
