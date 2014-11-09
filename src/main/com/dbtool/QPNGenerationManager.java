package com.dbtool;

import java.io.File;
import java.io.FileNotFoundException;

import com.dbtool.input.DBSpecificationReader;
import com.dbtool.qpmetranslator.QPNWriter;
import com.dbtool.queuecreator.AtomizationException;
import com.dbtool.queuecreator.DBDesignAtomizationVisitor;
import com.dbtool.queuecreator.QueryAtomizer;
import com.dbtool.queuecreator.SpecificationFileOutputVisitor;
import com.dbtool.queuecreator.SpecificationToQPNTranslator;
import com.dbtool.queuecreator.AST.TrafficComposition;
import com.dbtool.queueingpetrinet.QueueingPetriNet;
/*
 * Provides the entry point to AutoQPNPED with implementations for spec reading, atomization and qpn output 
 */
public class QPNGenerationManager {
	
	private DBSpecificationReader reader;
	private QueryAtomizer atomizer;
	private SpecificationToQPNTranslator specTranslator;
	private QPNWriter qpnWriter;
	
	public QPNGenerationManager(DBSpecificationReader reader, QueryAtomizer atomizer, SpecificationToQPNTranslator specTranslator, QPNWriter qpnWriter) {
		this.reader = reader;
		this.atomizer = atomizer;
		this.specTranslator = specTranslator;
		this.qpnWriter = qpnWriter;
	}
	
	public QueueingPetriNet generateQPN(String inputFilename) {
		return generateQPN(inputFilename, null);	
	}

	public QueueingPetriNet generateQPN(String inputFilename, String transactionSpecOutputFilename) {
		System.out.println("Reading specification...");
		//Read specification
		TrafficComposition comp = reader.readSpecification(inputFilename);
		if (comp == null) {
			return null;
		}
		System.out.println("Atomizing statements...");
		//Perform atomization 
		DBDesignAtomizationVisitor atomizationVisitor = new DBDesignAtomizationVisitor(atomizer);
		TrafficComposition atomizedComposition = null;
		try {
			atomizationVisitor.visit(comp);
			atomizedComposition = atomizationVisitor.getAtomizedComposition();
		}
		catch (AtomizationException e) {
			System.err.println("Halting due to error during atomization");
			return null;
		}
		//Output annotated specification to filename if necessary
		if (transactionSpecOutputFilename != null) {
			System.out.println("Writing annotated specification to file...");
			writeTransactionSpecification(atomizedComposition, transactionSpecOutputFilename);
		}
		System.out.println("Generating QPN");
		QueueingPetriNet qpn = specTranslator.translateSpecification(atomizedComposition);
		return qpn;	
	}
	
	public void writeTransactionSpecification(TrafficComposition composition, String filename) {
		try {
			SpecificationFileOutputVisitor outputVisitor = new SpecificationFileOutputVisitor(new File(filename));
			outputVisitor.visit(composition);
		} catch (FileNotFoundException e) {
			System.err.println("Could not open " + filename + " for writing");
			System.err.println(e.getMessage());
			return;
		}
	}
	
	public void writeTemplateQPN(QueueingPetriNet qPetriNet, String outputFilename) {
		qpnWriter.writeQPNTemplate(outputFilename, qPetriNet);
	}
	
	public void writeQPN(QueueingPetriNet qPetriNet, String outputFilename) {
		qpnWriter.writeViewableQPN(outputFilename, qPetriNet);
	}
}
