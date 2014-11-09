package com.dbtool.qpmetranslator;

import com.dbtool.queueingpetrinet.QueueingPetriNet;

/*
 * Interface that provides methods for exporting QPNs to a file
 */
public interface QPNWriter {

	public void writeQPNTemplate(String filename, QueueingPetriNet qpn);
	
	public void writeViewableQPN(String filename, QueueingPetriNet qpn);

}