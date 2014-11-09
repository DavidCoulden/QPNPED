package com.dbtool.queuecreator;

import java.sql.SQLException;

public class ServiceCalculatorException extends Exception {

	private static final long serialVersionUID = -1014974732258916818L;
	
	public ServiceCalculatorException() {
		super();
	}

	public ServiceCalculatorException(SQLException e) {
		super(e);
	}

}
