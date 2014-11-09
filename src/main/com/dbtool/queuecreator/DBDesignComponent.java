package com.dbtool.queuecreator;

public interface DBDesignComponent {
	public void accept(DBDesignVisitor dbVisitor);
}
