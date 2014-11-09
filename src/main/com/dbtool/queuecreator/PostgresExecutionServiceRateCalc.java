package com.dbtool.queuecreator;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.dbtool.utils.DatabaseConnector;
import com.dbtool.utils.Settings;
/**
 * Implementation to measure SQL statement performance for postgresql using jdbc. This implementation actually should work for all DBMS but
 * it is untested on those. The current implementation does not take into account of any overheads that could occur from using JDBC. Alternative
 * methods have been investigated but they did not prove to be more accurate. For example using EXPLAIN ANALYZE statements in postgres had at times
 * a large overhead and as such could not be used.
 *
 */
public class PostgresExecutionServiceRateCalc implements
		TransactionServiceRateCalculator {
	
	private static final int NUMBER_OF_TRIALS = Settings.ISOLATED_QUERY_EXECUTIONS;
	private static final int TRIAL_THINKTIME_MS = Settings.ISOLATED_QUERY_THINK_TIME;
	private Connection conn;
	
	public PostgresExecutionServiceRateCalc() {
		conn = DatabaseConnector.getConnection();
	}

	@Override
	public double calculateServiceRate(String sqlStatement) throws ServiceCalculatorException {
		List<Long> observations = new ArrayList<Long>();
		for (int trial = 0; trial < NUMBER_OF_TRIALS; trial++) {
			try {
				Statement stat = conn.createStatement();
				long startTime = System.nanoTime();
				stat.execute(sqlStatement);
				long endTime = System.nanoTime();
				observations.add(endTime - startTime);
				Thread.sleep(TRIAL_THINKTIME_MS);
			} catch (SQLException e) {
				throw new ServiceCalculatorException(e);
			} catch (InterruptedException e) {
				//Ignore and continue
			}
		}
		Collections.sort(observations);
		//Choose the middle 2/3 observations to roughly remove outliers
		List<Long> truncatedObs = observations.subList(NUMBER_OF_TRIALS / 3, (2 * NUMBER_OF_TRIALS) / 3);
		long total = 0;
		for (long obs : truncatedObs) {
			total += obs;
		}
		double totalMillisTime = total / (double) 1_000_000;
		double averageServiceTime = totalMillisTime / truncatedObs.size();
		return 1 / averageServiceTime;
	}

}
