package com.dbtool.utils;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

/**
 * Provides the database connection object using the connection details in the dbconn.properties file
 *
 */
public class DatabaseConnector {
	
	static {
		try {
			Class.forName("org.postgresql.Driver");
		} catch (ClassNotFoundException e) {
			System.err.println("Could not find jdbc PostgreSQL driver, exiting.");
			System.exit(1);
		}
	}
	
	private static Connection conn;
	private static Properties props;
	
	public static Connection getConnection() {
		if (conn == null) {
			loadProperties();
			try {
				conn = DriverManager.getConnection(props.getProperty("connection"), props.getProperty("username"), props.getProperty("password"));
			} catch (SQLException e) {
				System.err.println("Could not connect to the database specified in dbconn.properties. Exiting.");
				System.exit(1);
			}
		}
		return conn;
	}
	
	private static void loadProperties() {
		props = new Properties();
		try {
			props.load(DatabaseConnector.class.getClassLoader().getResourceAsStream("dbconn.properties"));
		} catch (IOException e) {
			System.err.println("Could not find the dbconn.properties file. Exiting.");
			System.exit(1);
		}
	}
}
