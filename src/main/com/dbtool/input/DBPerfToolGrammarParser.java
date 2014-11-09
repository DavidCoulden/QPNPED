// Generated from C:\Users\David\workspace\DatabasePerformanceTool\DBPerfToolGrammar.g4 by ANTLR 4.0
package com.dbtool.input;
import org.antlr.v4.runtime.atn.*;
import org.antlr.v4.runtime.dfa.DFA;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.misc.*;
import org.antlr.v4.runtime.tree.*;
import java.util.List;
import java.util.Iterator;
import java.util.ArrayList;

@SuppressWarnings({"all", "warnings", "unchecked", "unused", "cast"})
public class DBPerfToolGrammarParser extends Parser {
	protected static final DFA[] _decisionToDFA;
	protected static final PredictionContextCache _sharedContextCache =
		new PredictionContextCache();
	public static final int
		TRANSACTION=1, LBRACE=2, RBRACE=3, IF=4, STATEMENT=5, TABLE=6, EXCLUSIVE=7, 
		RUNTIME=8, INFER=9, EQUAL=10, BOOLEAN=11, ID=12, FLOAT=13, WHITESPACE=14, 
		SQLTEXT=15;
	public static final String[] tokenNames = {
		"<INVALID>", "'transaction'", "'{'", "'}'", "'if'", "'statement'", "'table'", 
		"'exclusive'", "'runtime'", "'infer'", "'='", "BOOLEAN", "ID", "FLOAT", 
		"WHITESPACE", "SQLTEXT"
	};
	public static final int
		RULE_specification = 0, RULE_transaction = 1, RULE_transactionBody = 2, 
		RULE_statement = 3, RULE_sqlstatement = 4, RULE_attribute = 5;
	public static final String[] ruleNames = {
		"specification", "transaction", "transactionBody", "statement", "sqlstatement", 
		"attribute"
	};

	@Override
	public String getGrammarFileName() { return "DBPerfToolGrammar.g4"; }

	@Override
	public String[] getTokenNames() { return tokenNames; }

	@Override
	public String[] getRuleNames() { return ruleNames; }

	@Override
	public ATN getATN() { return _ATN; }

	public DBPerfToolGrammarParser(TokenStream input) {
		super(input);
		_interp = new ParserATNSimulator(this,_ATN,_decisionToDFA,_sharedContextCache);
	}
	public static class SpecificationContext extends ParserRuleContext {
		public List<TransactionContext> transaction() {
			return getRuleContexts(TransactionContext.class);
		}
		public TerminalNode EOF() { return getToken(DBPerfToolGrammarParser.EOF, 0); }
		public TransactionContext transaction(int i) {
			return getRuleContext(TransactionContext.class,i);
		}
		public SpecificationContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_specification; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof DBPerfToolGrammarListener ) ((DBPerfToolGrammarListener)listener).enterSpecification(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof DBPerfToolGrammarListener ) ((DBPerfToolGrammarListener)listener).exitSpecification(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof DBPerfToolGrammarVisitor ) return ((DBPerfToolGrammarVisitor<? extends T>)visitor).visitSpecification(this);
			else return visitor.visitChildren(this);
		}
	}

	public final SpecificationContext specification() throws RecognitionException {
		SpecificationContext _localctx = new SpecificationContext(_ctx, getState());
		enterRule(_localctx, 0, RULE_specification);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(13); 
			_errHandler.sync(this);
			_la = _input.LA(1);
			do {
				{
				{
				setState(12); transaction();
				}
				}
				setState(15); 
				_errHandler.sync(this);
				_la = _input.LA(1);
			} while ( _la==TRANSACTION );
			setState(17); match(EOF);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class TransactionContext extends ParserRuleContext {
		public TerminalNode FLOAT() { return getToken(DBPerfToolGrammarParser.FLOAT, 0); }
		public TerminalNode RBRACE() { return getToken(DBPerfToolGrammarParser.RBRACE, 0); }
		public TerminalNode ID() { return getToken(DBPerfToolGrammarParser.ID, 0); }
		public TerminalNode TRANSACTION() { return getToken(DBPerfToolGrammarParser.TRANSACTION, 0); }
		public TransactionBodyContext transactionBody() {
			return getRuleContext(TransactionBodyContext.class,0);
		}
		public TerminalNode LBRACE() { return getToken(DBPerfToolGrammarParser.LBRACE, 0); }
		public TransactionContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_transaction; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof DBPerfToolGrammarListener ) ((DBPerfToolGrammarListener)listener).enterTransaction(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof DBPerfToolGrammarListener ) ((DBPerfToolGrammarListener)listener).exitTransaction(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof DBPerfToolGrammarVisitor ) return ((DBPerfToolGrammarVisitor<? extends T>)visitor).visitTransaction(this);
			else return visitor.visitChildren(this);
		}
	}

	public final TransactionContext transaction() throws RecognitionException {
		TransactionContext _localctx = new TransactionContext(_ctx, getState());
		enterRule(_localctx, 2, RULE_transaction);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(19); match(TRANSACTION);
			setState(20); match(ID);
			setState(21); match(FLOAT);
			setState(22); match(LBRACE);
			setState(23); transactionBody();
			setState(24); match(RBRACE);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class TransactionBodyContext extends ParserRuleContext {
		public List<StatementContext> statement() {
			return getRuleContexts(StatementContext.class);
		}
		public StatementContext statement(int i) {
			return getRuleContext(StatementContext.class,i);
		}
		public TransactionBodyContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_transactionBody; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof DBPerfToolGrammarListener ) ((DBPerfToolGrammarListener)listener).enterTransactionBody(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof DBPerfToolGrammarListener ) ((DBPerfToolGrammarListener)listener).exitTransactionBody(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof DBPerfToolGrammarVisitor ) return ((DBPerfToolGrammarVisitor<? extends T>)visitor).visitTransactionBody(this);
			else return visitor.visitChildren(this);
		}
	}

	public final TransactionBodyContext transactionBody() throws RecognitionException {
		TransactionBodyContext _localctx = new TransactionBodyContext(_ctx, getState());
		enterRule(_localctx, 4, RULE_transactionBody);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(27); 
			_errHandler.sync(this);
			_la = _input.LA(1);
			do {
				{
				{
				setState(26); statement();
				}
				}
				setState(29); 
				_errHandler.sync(this);
				_la = _input.LA(1);
			} while ( (((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << IF) | (1L << STATEMENT) | (1L << SQLTEXT))) != 0) );
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class StatementContext extends ParserRuleContext {
		public StatementContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_statement; }
	 
		public StatementContext() { }
		public void copyFrom(StatementContext ctx) {
			super.copyFrom(ctx);
		}
	}
	public static class DetailedSqlStatementContext extends StatementContext {
		public SqlstatementContext sqlstatement() {
			return getRuleContext(SqlstatementContext.class,0);
		}
		public TerminalNode RBRACE() { return getToken(DBPerfToolGrammarParser.RBRACE, 0); }
		public List<AttributeContext> attribute() {
			return getRuleContexts(AttributeContext.class);
		}
		public TerminalNode STATEMENT() { return getToken(DBPerfToolGrammarParser.STATEMENT, 0); }
		public AttributeContext attribute(int i) {
			return getRuleContext(AttributeContext.class,i);
		}
		public TerminalNode LBRACE() { return getToken(DBPerfToolGrammarParser.LBRACE, 0); }
		public DetailedSqlStatementContext(StatementContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof DBPerfToolGrammarListener ) ((DBPerfToolGrammarListener)listener).enterDetailedSqlStatement(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof DBPerfToolGrammarListener ) ((DBPerfToolGrammarListener)listener).exitDetailedSqlStatement(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof DBPerfToolGrammarVisitor ) return ((DBPerfToolGrammarVisitor<? extends T>)visitor).visitDetailedSqlStatement(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class RawSqlStatementContext extends StatementContext {
		public SqlstatementContext sqlstatement() {
			return getRuleContext(SqlstatementContext.class,0);
		}
		public RawSqlStatementContext(StatementContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof DBPerfToolGrammarListener ) ((DBPerfToolGrammarListener)listener).enterRawSqlStatement(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof DBPerfToolGrammarListener ) ((DBPerfToolGrammarListener)listener).exitRawSqlStatement(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof DBPerfToolGrammarVisitor ) return ((DBPerfToolGrammarVisitor<? extends T>)visitor).visitRawSqlStatement(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class IfStatementContext extends StatementContext {
		public TerminalNode FLOAT() { return getToken(DBPerfToolGrammarParser.FLOAT, 0); }
		public TerminalNode RBRACE() { return getToken(DBPerfToolGrammarParser.RBRACE, 0); }
		public TransactionBodyContext transactionBody() {
			return getRuleContext(TransactionBodyContext.class,0);
		}
		public TerminalNode LBRACE() { return getToken(DBPerfToolGrammarParser.LBRACE, 0); }
		public TerminalNode IF() { return getToken(DBPerfToolGrammarParser.IF, 0); }
		public IfStatementContext(StatementContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof DBPerfToolGrammarListener ) ((DBPerfToolGrammarListener)listener).enterIfStatement(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof DBPerfToolGrammarListener ) ((DBPerfToolGrammarListener)listener).exitIfStatement(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof DBPerfToolGrammarVisitor ) return ((DBPerfToolGrammarVisitor<? extends T>)visitor).visitIfStatement(this);
			else return visitor.visitChildren(this);
		}
	}

	public final StatementContext statement() throws RecognitionException {
		StatementContext _localctx = new StatementContext(_ctx, getState());
		enterRule(_localctx, 6, RULE_statement);
		int _la;
		try {
			setState(49);
			switch (_input.LA(1)) {
			case IF:
				_localctx = new IfStatementContext(_localctx);
				enterOuterAlt(_localctx, 1);
				{
				setState(31); match(IF);
				setState(32); match(FLOAT);
				setState(33); match(LBRACE);
				setState(34); transactionBody();
				setState(35); match(RBRACE);
				}
				break;
			case SQLTEXT:
				_localctx = new RawSqlStatementContext(_localctx);
				enterOuterAlt(_localctx, 2);
				{
				setState(37); sqlstatement();
				}
				break;
			case STATEMENT:
				_localctx = new DetailedSqlStatementContext(_localctx);
				enterOuterAlt(_localctx, 3);
				{
				setState(38); match(STATEMENT);
				setState(42);
				_errHandler.sync(this);
				_la = _input.LA(1);
				while ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << TABLE) | (1L << EXCLUSIVE) | (1L << RUNTIME) | (1L << INFER))) != 0)) {
					{
					{
					setState(39); attribute();
					}
					}
					setState(44);
					_errHandler.sync(this);
					_la = _input.LA(1);
				}
				setState(45); match(LBRACE);
				setState(46); sqlstatement();
				setState(47); match(RBRACE);
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class SqlstatementContext extends ParserRuleContext {
		public TerminalNode SQLTEXT() { return getToken(DBPerfToolGrammarParser.SQLTEXT, 0); }
		public SqlstatementContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_sqlstatement; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof DBPerfToolGrammarListener ) ((DBPerfToolGrammarListener)listener).enterSqlstatement(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof DBPerfToolGrammarListener ) ((DBPerfToolGrammarListener)listener).exitSqlstatement(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof DBPerfToolGrammarVisitor ) return ((DBPerfToolGrammarVisitor<? extends T>)visitor).visitSqlstatement(this);
			else return visitor.visitChildren(this);
		}
	}

	public final SqlstatementContext sqlstatement() throws RecognitionException {
		SqlstatementContext _localctx = new SqlstatementContext(_ctx, getState());
		enterRule(_localctx, 8, RULE_sqlstatement);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(51); match(SQLTEXT);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class AttributeContext extends ParserRuleContext {
		public TerminalNode TABLE() { return getToken(DBPerfToolGrammarParser.TABLE, 0); }
		public TerminalNode FLOAT() { return getToken(DBPerfToolGrammarParser.FLOAT, 0); }
		public TerminalNode BOOLEAN() { return getToken(DBPerfToolGrammarParser.BOOLEAN, 0); }
		public TerminalNode INFER() { return getToken(DBPerfToolGrammarParser.INFER, 0); }
		public TerminalNode EXCLUSIVE() { return getToken(DBPerfToolGrammarParser.EXCLUSIVE, 0); }
		public TerminalNode ID() { return getToken(DBPerfToolGrammarParser.ID, 0); }
		public TerminalNode EQUAL() { return getToken(DBPerfToolGrammarParser.EQUAL, 0); }
		public TerminalNode RUNTIME() { return getToken(DBPerfToolGrammarParser.RUNTIME, 0); }
		public AttributeContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_attribute; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof DBPerfToolGrammarListener ) ((DBPerfToolGrammarListener)listener).enterAttribute(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof DBPerfToolGrammarListener ) ((DBPerfToolGrammarListener)listener).exitAttribute(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof DBPerfToolGrammarVisitor ) return ((DBPerfToolGrammarVisitor<? extends T>)visitor).visitAttribute(this);
			else return visitor.visitChildren(this);
		}
	}

	public final AttributeContext attribute() throws RecognitionException {
		AttributeContext _localctx = new AttributeContext(_ctx, getState());
		enterRule(_localctx, 10, RULE_attribute);
		try {
			setState(65);
			switch (_input.LA(1)) {
			case EXCLUSIVE:
				enterOuterAlt(_localctx, 1);
				{
				setState(53); match(EXCLUSIVE);
				setState(54); match(EQUAL);
				setState(55); match(BOOLEAN);
				}
				break;
			case RUNTIME:
				enterOuterAlt(_localctx, 2);
				{
				setState(56); match(RUNTIME);
				setState(57); match(EQUAL);
				setState(58); match(FLOAT);
				}
				break;
			case TABLE:
				enterOuterAlt(_localctx, 3);
				{
				setState(59); match(TABLE);
				setState(60); match(EQUAL);
				setState(61); match(ID);
				}
				break;
			case INFER:
				enterOuterAlt(_localctx, 4);
				{
				setState(62); match(INFER);
				setState(63); match(EQUAL);
				setState(64); match(BOOLEAN);
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static final String _serializedATN =
		"\2\3\21F\4\2\t\2\4\3\t\3\4\4\t\4\4\5\t\5\4\6\t\6\4\7\t\7\3\2\6\2\20\n"+
		"\2\r\2\16\2\21\3\2\3\2\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\4\6\4\36\n\4\r\4"+
		"\16\4\37\3\5\3\5\3\5\3\5\3\5\3\5\3\5\3\5\3\5\7\5+\n\5\f\5\16\5.\13\5\3"+
		"\5\3\5\3\5\3\5\5\5\64\n\5\3\6\3\6\3\7\3\7\3\7\3\7\3\7\3\7\3\7\3\7\3\7"+
		"\3\7\3\7\3\7\5\7D\n\7\3\7\2\b\2\4\6\b\n\f\2\2G\2\17\3\2\2\2\4\25\3\2\2"+
		"\2\6\35\3\2\2\2\b\63\3\2\2\2\n\65\3\2\2\2\fC\3\2\2\2\16\20\5\4\3\2\17"+
		"\16\3\2\2\2\20\21\3\2\2\2\21\17\3\2\2\2\21\22\3\2\2\2\22\23\3\2\2\2\23"+
		"\24\7\1\2\2\24\3\3\2\2\2\25\26\7\3\2\2\26\27\7\16\2\2\27\30\7\17\2\2\30"+
		"\31\7\4\2\2\31\32\5\6\4\2\32\33\7\5\2\2\33\5\3\2\2\2\34\36\5\b\5\2\35"+
		"\34\3\2\2\2\36\37\3\2\2\2\37\35\3\2\2\2\37 \3\2\2\2 \7\3\2\2\2!\"\7\6"+
		"\2\2\"#\7\17\2\2#$\7\4\2\2$%\5\6\4\2%&\7\5\2\2&\64\3\2\2\2\'\64\5\n\6"+
		"\2(,\7\7\2\2)+\5\f\7\2*)\3\2\2\2+.\3\2\2\2,*\3\2\2\2,-\3\2\2\2-/\3\2\2"+
		"\2.,\3\2\2\2/\60\7\4\2\2\60\61\5\n\6\2\61\62\7\5\2\2\62\64\3\2\2\2\63"+
		"!\3\2\2\2\63\'\3\2\2\2\63(\3\2\2\2\64\t\3\2\2\2\65\66\7\21\2\2\66\13\3"+
		"\2\2\2\678\7\t\2\289\7\f\2\29D\7\r\2\2:;\7\n\2\2;<\7\f\2\2<D\7\17\2\2"+
		"=>\7\b\2\2>?\7\f\2\2?D\7\16\2\2@A\7\13\2\2AB\7\f\2\2BD\7\r\2\2C\67\3\2"+
		"\2\2C:\3\2\2\2C=\3\2\2\2C@\3\2\2\2D\r\3\2\2\2\7\21\37,\63C";
	public static final ATN _ATN =
		ATNSimulator.deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
	}
}