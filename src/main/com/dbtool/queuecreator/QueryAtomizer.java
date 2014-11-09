package com.dbtool.queuecreator;

import java.util.List;

import com.dbtool.queuecreator.AST.SQLStatement;


public interface QueryAtomizer {
	public List<SQLStatement> atomizeSqlStatment(SQLStatement statement) throws AtomizationException;
}
