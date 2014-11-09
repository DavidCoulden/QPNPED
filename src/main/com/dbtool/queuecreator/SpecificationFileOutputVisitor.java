package com.dbtool.queuecreator;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintWriter;

import com.dbtool.queuecreator.AST.IfStatement;
import com.dbtool.queuecreator.AST.SQLStatement;
import com.dbtool.queuecreator.AST.TrafficComposition;
import com.dbtool.queuecreator.AST.Transaction;
import com.dbtool.queuecreator.AST.TransactionStatement;

/**
 * A visitor used to output annotated specifications to a file
 */
public class SpecificationFileOutputVisitor extends DefaultDBDesignVisitor {
	private PrintWriter writer;
	private double currentProp;
	private int tabs = 0;
	
	public SpecificationFileOutputVisitor(File outputFile) throws FileNotFoundException {
		writer = new PrintWriter(new FileOutputStream(outputFile));
	}
	
	@Override
	public void visit(TrafficComposition comp) {
		for (Transaction trans : comp.getTransactions()) {
			double prop = comp.getProportionForTrans(trans);
			currentProp = prop;
			trans.accept(this);
		}
		writer.flush();
	}
	
	@Override
	public void visit(Transaction trans) {
		printString("transaction " + trans.getTransactionName() + " " + currentProp + " {");
		tabs++;
		for (TransactionStatement stat : trans.getSQLStatements()) {
			stat.accept(this);
		}
		tabs--;
		printString("}");
	}
	
	@Override
	public void visit(SQLStatement sqlStat) {
		printString("statement exclusive=" + sqlStat.isExclusive() + " runtime=" + sqlStat.getTotalServiceTime() + " table=" + sqlStat.getTableAccessed() + " {");
		tabs++;
		printString("-" + sqlStat.getSQLContent());
		tabs--;
		printString("}");
	}
	
	@Override
	public void visit(IfStatement ifStat) {
		printString("if " + ifStat.getProportionEntered() + " {");
		tabs++;
		for (TransactionStatement stat : ifStat.getEnclosedStatements()) {
			stat.accept(this);
		}
		tabs--;
		printString("}");
	}
	
	private void printString(String printString) {
		printTabs();
		String tabString = "";
		for (int n = 0; n < tabs; n++) {
			tabString += "\t";
		}
		String formatttedString = printString.replaceAll("\n", "\n" + tabString);
		writer.println(formatttedString);
	}
	
	private void printTabs() {
		for (int n = 0; n < tabs; n++) {
			writer.print('\t');
		}
	}
}
