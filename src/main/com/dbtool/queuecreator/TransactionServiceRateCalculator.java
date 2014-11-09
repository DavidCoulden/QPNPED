package com.dbtool.queuecreator;

public interface TransactionServiceRateCalculator {

	public double calculateServiceRate(String sqlStatement) throws ServiceCalculatorException;
}
