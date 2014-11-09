// Generated from C:\Users\David\workspace\DatabasePerformanceTool\DBPerfToolGrammar.g4 by ANTLR 4.0
package com.dbtool.input;
import org.antlr.v4.runtime.tree.*;
import org.antlr.v4.runtime.Token;

public interface DBPerfToolGrammarListener extends ParseTreeListener {
	void enterDetailedSqlStatement(DBPerfToolGrammarParser.DetailedSqlStatementContext ctx);
	void exitDetailedSqlStatement(DBPerfToolGrammarParser.DetailedSqlStatementContext ctx);

	void enterSqlstatement(DBPerfToolGrammarParser.SqlstatementContext ctx);
	void exitSqlstatement(DBPerfToolGrammarParser.SqlstatementContext ctx);

	void enterRawSqlStatement(DBPerfToolGrammarParser.RawSqlStatementContext ctx);
	void exitRawSqlStatement(DBPerfToolGrammarParser.RawSqlStatementContext ctx);

	void enterTransaction(DBPerfToolGrammarParser.TransactionContext ctx);
	void exitTransaction(DBPerfToolGrammarParser.TransactionContext ctx);

	void enterSpecification(DBPerfToolGrammarParser.SpecificationContext ctx);
	void exitSpecification(DBPerfToolGrammarParser.SpecificationContext ctx);

	void enterAttribute(DBPerfToolGrammarParser.AttributeContext ctx);
	void exitAttribute(DBPerfToolGrammarParser.AttributeContext ctx);

	void enterTransactionBody(DBPerfToolGrammarParser.TransactionBodyContext ctx);
	void exitTransactionBody(DBPerfToolGrammarParser.TransactionBodyContext ctx);

	void enterIfStatement(DBPerfToolGrammarParser.IfStatementContext ctx);
	void exitIfStatement(DBPerfToolGrammarParser.IfStatementContext ctx);
}