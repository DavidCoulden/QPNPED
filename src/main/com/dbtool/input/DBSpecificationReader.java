package com.dbtool.input;

import java.io.File;
import java.io.InputStream;

import com.dbtool.queuecreator.AST.TrafficComposition;
/**
 * This interface defines a variety of methods used to read traffic specifications.
 *
 */
public interface DBSpecificationReader {

	public TrafficComposition readSpecificationFromString(String specification);

	public TrafficComposition readSpecification(String filename);

	public TrafficComposition readSpecification(File file);

	public TrafficComposition readSpecification(InputStream stream);

}