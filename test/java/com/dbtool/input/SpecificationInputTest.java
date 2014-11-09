package com.dbtool.input;


import static org.junit.Assert.assertNotNull;

import java.util.List;

import org.junit.Test;

import com.dbtool.queuecreator.AST.TrafficComposition;
import com.dbtool.queuecreator.AST.Transaction;

public class SpecificationInputTest {
	
	@Test
	public void testSimpleOneTransactionSpec() {
		DBSpecificationReader reader = new ANTLRDBSpecificationReader();
		TrafficComposition comp = reader.readSpecification("test/resources/test.txt");
		assertNotNull(comp);
		List<Transaction> trans = comp.getTransactions();
		return;
		
	}
	
	@Test
	public void testDetailedTransactionSpec() {
		DBSpecificationReader reader = new ANTLRDBSpecificationReader(true);
		TrafficComposition comp = reader.readSpecification("test/resources/detailed_spec.txt");
		List<Transaction> trans = comp.getTransactions();
		return;
	}

}
