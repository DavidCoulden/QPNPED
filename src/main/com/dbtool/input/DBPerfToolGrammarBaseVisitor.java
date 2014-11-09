// Generated from C:\Users\David\workspace\DatabasePerformanceTool\DBPerfToolGrammar.g4 by ANTLR 4.0
package com.dbtool.input;
import org.antlr.v4.runtime.tree.*;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.ParserRuleContext;

public class DBPerfToolGrammarBaseVisitor<T> extends AbstractParseTreeVisitor<T> implements DBPerfToolGrammarVisitor<T> {
	@Override public T visitDetailedSqlStatement(DBPerfToolGrammarParser.DetailedSqlStatementContext ctx) { return visitChildren(ctx); }

	@Override public T visitSqlstatement(DBPerfToolGrammarParser.SqlstatementContext ctx) { return visitChildren(ctx); }

	@Override public T visitRawSqlStatement(DBPerfToolGrammarParser.RawSqlStatementContext ctx) { return visitChildren(ctx); }

	@Override public T visitTransaction(DBPerfToolGrammarParser.TransactionContext ctx) { return visitChildren(ctx); }

	@Override public T visitSpecification(DBPerfToolGrammarParser.SpecificationContext ctx) { return visitChildren(ctx); }

	@Override public T visitAttribute(DBPerfToolGrammarParser.AttributeContext ctx) { return visitChildren(ctx); }

	@Override public T visitTransactionBody(DBPerfToolGrammarParser.TransactionBodyContext ctx) { return visitChildren(ctx); }

	@Override public T visitIfStatement(DBPerfToolGrammarParser.IfStatementContext ctx) { return visitChildren(ctx); }
}