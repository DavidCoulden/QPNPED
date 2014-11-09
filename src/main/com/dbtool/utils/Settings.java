package com.dbtool.utils;

import java.io.IOException;
import java.util.Properties;

public class Settings {
	private static Properties props;
	
	
	private static final int DEFAULT_CLIENT_THINK_TIME = 200;
	public static final Integer DEFAULT_NUMBER_OF_CLIENTS = 10;
	private static final ClientThinkTimeDistribution DEFAULT_CLIENT_THINK_DIST_TYPE = ClientThinkTimeDistribution.EXPONENTIAL;
	private static final int DEFAULT_ISOLATED_QUERY_EXECUTIONS = 500;
	private static final int DEFAULT_ISOLATED_QUERY_THINK_TIME = 5;
	private static final int DEFAULT_QPME_RUNNER_THREADS = 4;
	
	static {
		props = new Properties();
		String clientThinkVal = null;
		String clientThinkDist = null;
		String isolatedQueryExecs = null;
		String isolatedQueryThinktime = null;
		String qpmeLocation = null;
		String qpmeRunnerThreads = null;
		try {
			props.load(Settings.class.getClassLoader().getResourceAsStream("dbtool.properties"));
			clientThinkVal = props.getProperty("client.thinktime");
			clientThinkDist = props.getProperty("client.thinkdist");
			isolatedQueryExecs = props.getProperty("annotator.service.executions");
			isolatedQueryThinktime = props.getProperty("annotator.service.thinktime");
			qpmeLocation = props.getProperty("qpme.runner.location");
			qpmeRunnerThreads = props.getProperty("qpme.runner.threads");
			
		} catch (IOException e) {
			//Fall through and set to defaults
		}
		CLIENT_THINK_TIME = (clientThinkVal != null && IntegerUtil.isInteger(clientThinkVal)) ? Integer.parseInt(clientThinkVal) 
								: DEFAULT_CLIENT_THINK_TIME;
		ClientThinkTimeDistribution clientDist = (clientThinkDist != null) ? ClientThinkTimeDistribution.getValue(clientThinkDist)
													: null;
		CLIENT_THINK_DIST_TYPE = (clientDist != null) ? clientDist : DEFAULT_CLIENT_THINK_DIST_TYPE;
		ISOLATED_QUERY_EXECUTIONS = (isolatedQueryExecs != null && IntegerUtil.isInteger(isolatedQueryExecs)) ? 
										Integer.parseInt(isolatedQueryExecs) : DEFAULT_ISOLATED_QUERY_EXECUTIONS;
	    ISOLATED_QUERY_THINK_TIME = (isolatedQueryThinktime != null && IntegerUtil.isInteger(isolatedQueryThinktime)) ?
	    								Integer.parseInt(isolatedQueryThinktime) : DEFAULT_ISOLATED_QUERY_THINK_TIME;
	    QPME_SIM_LOCATION = (qpmeLocation != null) ? qpmeLocation : null;
	    QPME_RUNNER_THREADS = (qpmeRunnerThreads != null && IntegerUtil.isInteger(qpmeRunnerThreads)) ?
	    								Integer.parseInt(qpmeRunnerThreads) : DEFAULT_QPME_RUNNER_THREADS;
	    
	}
	
	//Client think time in milliseconds
	public static final int CLIENT_THINK_TIME;
	//Client think rate
	public static final double CLIENT_THINK_RATE = CLIENT_THINK_TIME != 0 ? 1/(double) CLIENT_THINK_TIME : 0;
	//Client think time distribution type
	public static final ClientThinkTimeDistribution CLIENT_THINK_DIST_TYPE;
	//Number of times each query need to be executed to calculate service times
	public static final int ISOLATED_QUERY_EXECUTIONS;
	//Think time between repeated executions of queries in service time calculation
	public static final int ISOLATED_QUERY_THINK_TIME;
	//Directory of the QPME installation
	public static final String QPME_SIM_LOCATION;
	//Number of threads to use in the QPME runner
	public static final int QPME_RUNNER_THREADS;
	
}
