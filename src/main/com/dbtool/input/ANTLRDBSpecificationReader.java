package com.dbtool.input;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CommonTokenStream;

import com.dbtool.input.DBPerfToolGrammarParser.SpecificationContext;
import com.dbtool.queuecreator.AST.TrafficComposition;
/**
 * This class is an ANTLR parser implementation for specification input. It uses a DBSpecificationVisitor as the adapter between the ANTLR parser and
 * the specification definition structures in com.dbtool.queuecreator.
 *
 */
public class ANTLRDBSpecificationReader implements DBSpecificationReader {
	private boolean requireDetailedStatements;
	
	public ANTLRDBSpecificationReader() {
		this.requireDetailedStatements = false;
	}
	
	public ANTLRDBSpecificationReader(boolean requireDetailedStatements) {
		this.requireDetailedStatements = requireDetailedStatements;
	}
	
	
	/* (non-Javadoc)
	 * @see com.dbtool.input.DBSpecificationReader#readSpecificationFromString(java.lang.String)
	 */
	@Override
	public TrafficComposition readSpecificationFromString(String specification) {
		return readSpecificationFromANTLRStream(new ANTLRInputStream(specification));
	}
	
	/* (non-Javadoc)
	 * @see com.dbtool.input.DBSpecificationReader#readSpecification(java.lang.String)
	 */
	@Override
	public TrafficComposition readSpecification(String filename) {
		return readSpecification(new File(filename));
	}
	
	/* (non-Javadoc)
	 * @see com.dbtool.input.DBSpecificationReader#readSpecification(java.io.File)
	 */
	@Override
	public TrafficComposition readSpecification(File file) {
		try {
			return readSpecification(new FileInputStream(file));
		} catch (FileNotFoundException e) {
			System.err.print(e.getMessage());
			return null;
		}
	}
	
	/* (non-Javadoc)
	 * @see com.dbtool.input.DBSpecificationReader#readSpecification(java.io.InputStream)
	 */
	@Override
	public TrafficComposition readSpecification(InputStream stream) {
		try {
			return readSpecificationFromANTLRStream(new ANTLRInputStream(stream));
		} catch (IOException e) {
			System.err.print(e.getMessage());
			return null;
		}
	}

	private TrafficComposition readSpecificationFromANTLRStream(ANTLRInputStream stream) {
		DBPerfToolGrammarLexer lexer = new DBPerfToolGrammarLexer(stream);
		DBPerfToolGrammarParser parser = new DBPerfToolGrammarParser(new CommonTokenStream(lexer));
		parser.setBuildParseTree(true);
		SpecificationContext specificationCtx = parser.specification();
		DBSpecificationVisitor visitor = new DBSpecificationVisitor(requireDetailedStatements);
		try {
			TrafficComposition comp = (TrafficComposition) visitor.visit(specificationCtx);
			return comp;
		}
		catch (SpecParseException ex) {
			System.err.println(ex.getSpecErrorMessage());
			return null;
		}
	}
	
}
