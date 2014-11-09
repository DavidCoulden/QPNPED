package com.dbtool.input;

public class SpecParseException extends RuntimeException {
	
	private static final long serialVersionUID = 1L;
	private int lineNumber;
	
	public SpecParseException(String message, int lineNumber) {
		super(message);
		this.lineNumber = lineNumber;
	}
	
	public String getSpecErrorMessage() {
		return "Error reading specification: " + this.getMessage() + " on line " + lineNumber;
	}
}
