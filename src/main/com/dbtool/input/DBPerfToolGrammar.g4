/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

grammar DBPerfToolGrammar;

TRANSACTION : 'transaction';
LBRACE : '{';
RBRACE : '}';
IF : 'if';
STATEMENT : 'statement';
TABLE : 'table';
EXCLUSIVE : 'exclusive';
RUNTIME : 'runtime';
INFER : 'infer';
EQUAL : '=';
BOOLEAN : 'true' | 'false';
ID : LETTER (LETTER | DIGIT)*;
FLOAT : DIGIT+('.'DIGIT+)?;
WHITESPACE : [ \n\r\t] -> skip;
fragment LETTER : [a-zA-Z\u0080-\u00FF_];
fragment DIGIT : '0' .. '9';
SQLTEXT : '-'(~[;])*';';

specification : transaction+ EOF;

transaction : TRANSACTION ID FLOAT LBRACE transactionBody RBRACE;

transactionBody : statement+;
                  
statement : IF FLOAT LBRACE transactionBody RBRACE #ifStatement
          | sqlstatement #rawSqlStatement
          | STATEMENT attribute* LBRACE sqlstatement RBRACE #detailedSqlStatement;

sqlstatement : SQLTEXT;


attribute : EXCLUSIVE EQUAL BOOLEAN
          | RUNTIME EQUAL FLOAT
          | TABLE EQUAL ID
          | INFER EQUAL BOOLEAN;
