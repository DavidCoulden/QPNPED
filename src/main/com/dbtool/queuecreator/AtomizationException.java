package com.dbtool.queuecreator;

public class AtomizationException extends RuntimeException {

	private static final long serialVersionUID = 1143173913874231444L;
	
	public AtomizationException() {
		super();
	}

	public AtomizationException(Exception e) {
		super(e);
	}

}
