package com.dbtool.queuecreator.AST;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.dbtool.queuecreator.DBDesignComponent;
import com.dbtool.queuecreator.DBDesignVisitor;
import com.dbtool.utils.Pair;

public class TrafficComposition implements DBDesignComponent {
	private Map<Transaction, Double> transactionToTrafficProp;
	
	@SafeVarargs
	public TrafficComposition(Pair<Transaction, Double>...transactionsProps) {
		this.transactionToTrafficProp = new HashMap<Transaction, Double>();
		for (Pair<Transaction, Double> tranProp : transactionsProps) {
			this.transactionToTrafficProp.put(tranProp.first(), tranProp.second());
		}
	}
	
	public TrafficComposition(Transaction...transactions) {
		this.transactionToTrafficProp = new HashMap<Transaction, Double>();
		int noOfTransactions = transactions.length;
		for (Transaction t : transactions) {
			this.transactionToTrafficProp.put(t, 1 / (double) noOfTransactions);
		}
	}
	
	public TrafficComposition(Collection<Pair<Transaction, Double>> transactionComposition) {
		this.transactionToTrafficProp = new HashMap<Transaction, Double>();
		for (Pair<Transaction, Double> tranProp : transactionComposition) {
			this.transactionToTrafficProp.put(tranProp.first(), tranProp.second());
		}
	}
	
	@Override
	public void accept(DBDesignVisitor dbVisitor) {
		dbVisitor.visit(this);
	}
	
	public Double getProportionForTrans(Transaction trans) {
		return transactionToTrafficProp.get(trans);
	}
	
	public List<Transaction> getTransactions() {
		return new ArrayList<Transaction>(transactionToTrafficProp.keySet());
	}
	
}
