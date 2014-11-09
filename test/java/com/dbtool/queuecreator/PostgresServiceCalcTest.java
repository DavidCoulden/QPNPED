package com.dbtool.queuecreator;

import org.junit.Test;


public class PostgresServiceCalcTest {

	@Test
	public void testSimpleCalculation() throws ServiceCalculatorException {
		PostgresExecutionServiceRateCalc calculator = new PostgresExecutionServiceRateCalc();
		double result = calculator.calculateServiceRate("SELECT * FROM city");
		return;
	}
}
