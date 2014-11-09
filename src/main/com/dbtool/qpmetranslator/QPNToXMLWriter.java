package com.dbtool.qpmetranslator;

import java.io.FileWriter;
import java.io.IOException;

import org.dom4j.Document;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;

import com.dbtool.queueingpetrinet.QPNTranslator;
import com.dbtool.queueingpetrinet.QueueingPetriNet;
/**
 * The implementation for writing QPNs in the QPME XML format. It uses the QpnToQpmeXMLVisitor to perform the bulk of the XML generation.
 *
 */
public class QPNToXMLWriter implements QPNWriter {
	
	private QPNTranslator translator;
	
	public QPNToXMLWriter(QPNTranslator translator) {
		this.translator = translator;
	}
	
	/* (non-Javadoc)
	 * @see com.dbtool.qpmetranslator.QPNWriter#writeQPN(java.lang.String, com.dbtool.queueingpetrinet.QueueingPetriNet)
	 */
	@Override
	public void writeQPNTemplate(String filename, QueueingPetriNet qpn) {
		Document doc = (Document) translator.translate(qpn);
	    try {
	    	OutputFormat format = OutputFormat.createPrettyPrint();
	    	XMLWriter writer = new XMLWriter(new FileWriter(filename), format);
			writer.write(doc);
			writer.close();
		} catch (IOException e) {
			System.err.println("Could not write to qpn template to " + filename);
			System.err.println(e.getMessage());
			return;
		}
	}

	@Override
	public void writeViewableQPN(String filename, QueueingPetriNet qpn) {
		Document doc = (Document) translator.translateToViewable(qpn);
	    try {
	    	OutputFormat format = OutputFormat.createPrettyPrint();
	    	XMLWriter writer = new XMLWriter(new FileWriter(filename), format);
			writer.write(doc);
			writer.close();
		} catch (IOException e) {
			System.err.println("Could not write to viewable qpn to " + filename);
			System.err.println(e.getMessage());
			return;
		
		}
		
	}
}
