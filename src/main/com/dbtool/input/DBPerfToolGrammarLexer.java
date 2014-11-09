// Generated from C:\Users\David\workspace\DatabasePerformanceTool\DBPerfToolGrammar.g4 by ANTLR 4.0
package com.dbtool.input;
import org.antlr.v4.runtime.Lexer;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.TokenStream;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.atn.*;
import org.antlr.v4.runtime.dfa.DFA;
import org.antlr.v4.runtime.misc.*;

@SuppressWarnings({"all", "warnings", "unchecked", "unused", "cast"})
public class DBPerfToolGrammarLexer extends Lexer {
	protected static final DFA[] _decisionToDFA;
	protected static final PredictionContextCache _sharedContextCache =
		new PredictionContextCache();
	public static final int
		TRANSACTION=1, LBRACE=2, RBRACE=3, IF=4, STATEMENT=5, TABLE=6, EXCLUSIVE=7, 
		RUNTIME=8, INFER=9, EQUAL=10, BOOLEAN=11, ID=12, FLOAT=13, WHITESPACE=14, 
		SQLTEXT=15;
	public static String[] modeNames = {
		"DEFAULT_MODE"
	};

	public static final String[] tokenNames = {
		"<INVALID>",
		"'transaction'", "'{'", "'}'", "'if'", "'statement'", "'table'", "'exclusive'", 
		"'runtime'", "'infer'", "'='", "BOOLEAN", "ID", "FLOAT", "WHITESPACE", 
		"SQLTEXT"
	};
	public static final String[] ruleNames = {
		"TRANSACTION", "LBRACE", "RBRACE", "IF", "STATEMENT", "TABLE", "EXCLUSIVE", 
		"RUNTIME", "INFER", "EQUAL", "BOOLEAN", "ID", "FLOAT", "WHITESPACE", "LETTER", 
		"DIGIT", "SQLTEXT"
	};


	public DBPerfToolGrammarLexer(CharStream input) {
		super(input);
		_interp = new LexerATNSimulator(this,_ATN,_decisionToDFA,_sharedContextCache);
	}

	@Override
	public String getGrammarFileName() { return "DBPerfToolGrammar.g4"; }

	@Override
	public String[] getTokenNames() { return tokenNames; }

	@Override
	public String[] getRuleNames() { return ruleNames; }

	@Override
	public String[] getModeNames() { return modeNames; }

	@Override
	public ATN getATN() { return _ATN; }

	@Override
	public void action(RuleContext _localctx, int ruleIndex, int actionIndex) {
		switch (ruleIndex) {
		case 13: WHITESPACE_action((RuleContext)_localctx, actionIndex); break;
		}
	}
	private void WHITESPACE_action(RuleContext _localctx, int actionIndex) {
		switch (actionIndex) {
		case 0: skip();  break;
		}
	}

	public static final String _serializedATN =
		"\2\4\21\u0093\b\1\4\2\t\2\4\3\t\3\4\4\t\4\4\5\t\5\4\6\t\6\4\7\t\7\4\b"+
		"\t\b\4\t\t\t\4\n\t\n\4\13\t\13\4\f\t\f\4\r\t\r\4\16\t\16\4\17\t\17\4\20"+
		"\t\20\4\21\t\21\4\22\t\22\3\2\3\2\3\2\3\2\3\2\3\2\3\2\3\2\3\2\3\2\3\2"+
		"\3\2\3\3\3\3\3\4\3\4\3\5\3\5\3\5\3\6\3\6\3\6\3\6\3\6\3\6\3\6\3\6\3\6\3"+
		"\6\3\7\3\7\3\7\3\7\3\7\3\7\3\b\3\b\3\b\3\b\3\b\3\b\3\b\3\b\3\b\3\b\3\t"+
		"\3\t\3\t\3\t\3\t\3\t\3\t\3\t\3\n\3\n\3\n\3\n\3\n\3\n\3\13\3\13\3\f\3\f"+
		"\3\f\3\f\3\f\3\f\3\f\3\f\3\f\5\fl\n\f\3\r\3\r\3\r\7\rq\n\r\f\r\16\rt\13"+
		"\r\3\16\6\16w\n\16\r\16\16\16x\3\16\3\16\6\16}\n\16\r\16\16\16~\5\16\u0081"+
		"\n\16\3\17\3\17\3\17\3\17\3\20\3\20\3\21\3\21\3\22\3\22\7\22\u008d\n\22"+
		"\f\22\16\22\u0090\13\22\3\22\3\22\2\23\3\3\1\5\4\1\7\5\1\t\6\1\13\7\1"+
		"\r\b\1\17\t\1\21\n\1\23\13\1\25\f\1\27\r\1\31\16\1\33\17\1\35\20\2\37"+
		"\2\1!\2\1#\21\1\3\2\5\5\13\f\17\17\"\"\6C\\aac|\u0082\u0101\3==\u0097"+
		"\2\3\3\2\2\2\2\5\3\2\2\2\2\7\3\2\2\2\2\t\3\2\2\2\2\13\3\2\2\2\2\r\3\2"+
		"\2\2\2\17\3\2\2\2\2\21\3\2\2\2\2\23\3\2\2\2\2\25\3\2\2\2\2\27\3\2\2\2"+
		"\2\31\3\2\2\2\2\33\3\2\2\2\2\35\3\2\2\2\2#\3\2\2\2\3%\3\2\2\2\5\61\3\2"+
		"\2\2\7\63\3\2\2\2\t\65\3\2\2\2\138\3\2\2\2\rB\3\2\2\2\17H\3\2\2\2\21R"+
		"\3\2\2\2\23Z\3\2\2\2\25`\3\2\2\2\27k\3\2\2\2\31m\3\2\2\2\33v\3\2\2\2\35"+
		"\u0082\3\2\2\2\37\u0086\3\2\2\2!\u0088\3\2\2\2#\u008a\3\2\2\2%&\7v\2\2"+
		"&\'\7t\2\2\'(\7c\2\2()\7p\2\2)*\7u\2\2*+\7c\2\2+,\7e\2\2,-\7v\2\2-.\7"+
		"k\2\2./\7q\2\2/\60\7p\2\2\60\4\3\2\2\2\61\62\7}\2\2\62\6\3\2\2\2\63\64"+
		"\7\177\2\2\64\b\3\2\2\2\65\66\7k\2\2\66\67\7h\2\2\67\n\3\2\2\289\7u\2"+
		"\29:\7v\2\2:;\7c\2\2;<\7v\2\2<=\7g\2\2=>\7o\2\2>?\7g\2\2?@\7p\2\2@A\7"+
		"v\2\2A\f\3\2\2\2BC\7v\2\2CD\7c\2\2DE\7d\2\2EF\7n\2\2FG\7g\2\2G\16\3\2"+
		"\2\2HI\7g\2\2IJ\7z\2\2JK\7e\2\2KL\7n\2\2LM\7w\2\2MN\7u\2\2NO\7k\2\2OP"+
		"\7x\2\2PQ\7g\2\2Q\20\3\2\2\2RS\7t\2\2ST\7w\2\2TU\7p\2\2UV\7v\2\2VW\7k"+
		"\2\2WX\7o\2\2XY\7g\2\2Y\22\3\2\2\2Z[\7k\2\2[\\\7p\2\2\\]\7h\2\2]^\7g\2"+
		"\2^_\7t\2\2_\24\3\2\2\2`a\7?\2\2a\26\3\2\2\2bc\7v\2\2cd\7t\2\2de\7w\2"+
		"\2el\7g\2\2fg\7h\2\2gh\7c\2\2hi\7n\2\2ij\7u\2\2jl\7g\2\2kb\3\2\2\2kf\3"+
		"\2\2\2l\30\3\2\2\2mr\5\37\20\2nq\5\37\20\2oq\5!\21\2pn\3\2\2\2po\3\2\2"+
		"\2qt\3\2\2\2rp\3\2\2\2rs\3\2\2\2s\32\3\2\2\2tr\3\2\2\2uw\5!\21\2vu\3\2"+
		"\2\2wx\3\2\2\2xv\3\2\2\2xy\3\2\2\2y\u0080\3\2\2\2z|\7\60\2\2{}\5!\21\2"+
		"|{\3\2\2\2}~\3\2\2\2~|\3\2\2\2~\177\3\2\2\2\177\u0081\3\2\2\2\u0080z\3"+
		"\2\2\2\u0080\u0081\3\2\2\2\u0081\34\3\2\2\2\u0082\u0083\t\2\2\2\u0083"+
		"\u0084\3\2\2\2\u0084\u0085\b\17\2\2\u0085\36\3\2\2\2\u0086\u0087\t\3\2"+
		"\2\u0087 \3\2\2\2\u0088\u0089\4\62;\2\u0089\"\3\2\2\2\u008a\u008e\7/\2"+
		"\2\u008b\u008d\n\4\2\2\u008c\u008b\3\2\2\2\u008d\u0090\3\2\2\2\u008e\u008c"+
		"\3\2\2\2\u008e\u008f\3\2\2\2\u008f\u0091\3\2\2\2\u0090\u008e\3\2\2\2\u0091"+
		"\u0092\7=\2\2\u0092$\3\2\2\2\n\2kprx~\u0080\u008e";
	public static final ATN _ATN =
		ATNSimulator.deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
	}
}