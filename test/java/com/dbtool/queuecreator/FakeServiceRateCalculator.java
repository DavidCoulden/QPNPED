package com.dbtool.queuecreator;

public class FakeServiceRateCalculator implements
		TransactionServiceRateCalculator {

	@Override
	public double calculateServiceRate(String sqlStatement) {
		return 0;
	}

}
