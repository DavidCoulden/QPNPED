// Generated from C:\Users\David\workspace\DatabasePerformanceTool\DBPerfToolGrammar.g4 by ANTLR 4.0
package com.dbtool.input;
import org.antlr.v4.runtime.tree.*;
import org.antlr.v4.runtime.Token;

public interface DBPerfToolGrammarVisitor<T> extends ParseTreeVisitor<T> {
	T visitDetailedSqlStatement(DBPerfToolGrammarParser.DetailedSqlStatementContext ctx);

	T visitSqlstatement(DBPerfToolGrammarParser.SqlstatementContext ctx);

	T visitRawSqlStatement(DBPerfToolGrammarParser.RawSqlStatementContext ctx);

	T visitTransaction(DBPerfToolGrammarParser.TransactionContext ctx);

	T visitSpecification(DBPerfToolGrammarParser.SpecificationContext ctx);

	T visitAttribute(DBPerfToolGrammarParser.AttributeContext ctx);

	T visitTransactionBody(DBPerfToolGrammarParser.TransactionBodyContext ctx);

	T visitIfStatement(DBPerfToolGrammarParser.IfStatementContext ctx);
}