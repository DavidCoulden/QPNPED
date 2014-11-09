package com.dbtool.queuecreator;

import java.util.ArrayList;
import java.util.List;

import com.dbtool.queuecreator.AST.SQLStatement;
/**
 * An atomizer that does not change the statements at all.
 *
 */
public class IdentityAtomizer implements QueryAtomizer {

	@Override
	public List<SQLStatement> atomizeSqlStatment(SQLStatement statement) {
		List<SQLStatement> statements = new ArrayList<SQLStatement>();
		statements.add(statement);
		return statements;
	}

}
