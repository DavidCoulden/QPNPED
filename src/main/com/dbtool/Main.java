package com.dbtool;

import org.apache.commons.cli.BasicParser;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

import com.dbtool.input.ANTLRDBSpecificationReader;
import com.dbtool.input.DBSpecificationReader;
import com.dbtool.qpmerunner.QpmeQPNRunner;
import com.dbtool.qpmetranslator.QPNToXMLWriter;
import com.dbtool.qpmetranslator.QPNWriter;
import com.dbtool.qpmetranslator.QpnToQpmeXMLVisitor;
import com.dbtool.queuecreator.IdentityAtomizer;
import com.dbtool.queuecreator.PostgresExecutionServiceRateCalc;
import com.dbtool.queuecreator.PostgresqlQueryAtomizer;
import com.dbtool.queuecreator.QueryAtomizer;
import com.dbtool.queuecreator.SpecificationToQPNTranslator;
import com.dbtool.queuecreator.NonTransTableLevel.NonTransTableLevelTranslator;
import com.dbtool.queueingpetrinet.QueueingPetriNet;

public class Main {

	/**
	 * @param args
	 */
	private static HelpFormatter formatter = new HelpFormatter();
	private static Options options;

	public static void main(String[] args) {
		//Parses command line arguments
		CommandLine commLine = createCommandLine(args);
		if (commLine == null) {
			return;
		}
		//If runmode is on then enter runner mode
		if (commLine.hasOption("r")) {
			useRunnerMode(commLine);
			return;
		}
		if (!commLine.hasOption("f")) {
			System.err.println("Expected an input specification file specified with -f");
			formatter.printHelp("AutoQPNPED", options);
			return;
		}
		String inputSpecFilename = commLine.getOptionValue("f");
		boolean useFileValues = false;
		if (commLine.hasOption("s")) {
			useFileValues = true;
		}
		//Create specification reader
		DBSpecificationReader reader = new ANTLRDBSpecificationReader(useFileValues);
		QueryAtomizer atomizer;
		if (useFileValues) {
			//If using annotated spec then use an atomizer that makes no changes to statements
			atomizer = new IdentityAtomizer();
		}
		else {
			//Otherwise use postgres atomizer
			atomizer = new PostgresqlQueryAtomizer(new PostgresExecutionServiceRateCalc());
		}
		//Create specification translator for non transaction table level locking
		SpecificationToQPNTranslator specTrans = new NonTransTableLevelTranslator();
		//Create the output writer for Qpme format
		QPNWriter qpnWriter = new QPNToXMLWriter(new QpnToQpmeXMLVisitor());
		//Create the qpn generator that will perform the whole process
		QPNGenerationManager qpnGenerator = new QPNGenerationManager(reader, atomizer, specTrans, qpnWriter);
		QueueingPetriNet generatedQpn;
		if (commLine.hasOption("i")) {
			String fullSpecFilename = commLine.getOptionValue("i");
			//Generate QPN and output the full specification to specified file
			generatedQpn = qpnGenerator.generateQPN(inputSpecFilename, fullSpecFilename);
			if (generatedQpn == null) {
				return;
			}
		}
		else {
			//Generate QPN
			generatedQpn = qpnGenerator.generateQPN(inputSpecFilename);
			if (generatedQpn == null) {
				return;
			}
		}
		if (commLine.hasOption("V")) {
			//Output a viewable file to given filename
			String viewableFilename = commLine.getOptionValue("V");
			qpnGenerator.writeQPN(generatedQpn, viewableFilename);
		}
		if (!commLine.hasOption("o")) {
			System.err.println("Expected an output qpe template file specified with -o");
			formatter.printHelp("AutoQPNPED", options);
			return;
		}
		//Output the template file to given filename
		String templateFilename = commLine.getOptionValue("o");
		qpnGenerator.writeTemplateQPN(generatedQpn, templateFilename);
	}

	private static void useRunnerMode(CommandLine commLine) {
		String templateFilename = commLine.getOptionValue("r");
		String runIdentifier = "";
		if (commLine.hasOption("n")) {
			runIdentifier = commLine.getOptionValue("n");
		}
		if (!commLine.hasOption("d")) {
			System.err.println("Expected an output directory specified with -d");
			formatter.printHelp("AutoQPNPED", options);
			return;
		}
		String outputDir = commLine.getOptionValue("d");
		String outputDatFileName = "qpmeresponseResults.csv";
		if (commLine.hasOption("df")) {
			outputDatFileName = commLine.getOptionValue("df");
		}
		int startClients = 10;
		int endClients = 60;
		int stepClients = 10;
		//Set the client range to be used
		if (commLine.hasOption("c")) {
			String[] clientArgs = commLine.getOptionValues("c");
			try {
				startClients = Integer.parseInt(clientArgs[0]);
				endClients = Integer.parseInt(clientArgs[1]);
				stepClients = Integer.parseInt(clientArgs[2]);
			}
			catch (NumberFormatException e) {
				System.err.println("Error when parsing client amount range");
				return;
			} 
		}
		//Create and set off the runner
		QpmeQPNRunner runner = new QpmeQPNRunner(startClients, endClients, stepClients);
		System.out.println("Starting AutoQPNPED in runner mode");
		runner.runQpmeSimulations(runIdentifier, templateFilename, outputDir, outputDatFileName);
		
	}

	@SuppressWarnings("static-access")
	private static CommandLine createCommandLine(String[] args) {
		options = new Options();
		Option specFileOption = OptionBuilder.withArgName("file")
											 .hasArg()
											 .withLongOpt("specification")
											 .withDescription("Transaction traffic specification file")
											 .create("f");
		Option generateFullSpecification = OptionBuilder.withDescription("Strictly require fully annotated statements")
														.withLongOpt("require-annotations")
														.create("s");
		
		Option outputFullSpecification = OptionBuilder.withArgName("file")
													  .hasArg()
													  .withDescription("Output file for the specification with inferred details")
													  .withLongOpt("annotated-output")
													  .create("i");
		Option outputViewableQPN = OptionBuilder.withArgName("file")
												.hasArg()
												.withDescription("Output file for the viewable Queueing Petri Net translation")
												.withLongOpt("viewable-qpnout")
												.create("V");
		Option outputTemplateQPN = OptionBuilder.withArgName("file")
												.hasArg()
												.withDescription("Output file for the template Queueing Petri Net translation")
												.withLongOpt("template-qpnout")
												.create("o");
		Option runModeOption = OptionBuilder.withArgName("file")
											.hasArg()
											.withDescription("Run QPME simulator on a qpetemplate file")
											.withLongOpt("run-mode")
											.create("r");
		Option numClientsOption = OptionBuilder.withArgName("initial clients> <end clients> <client step amount")
											   .hasArgs(3)
											   .withDescription("Range of clients to run over, specified by initial clients, end clients and" +
											   		"client step values")
											   	.withLongOpt("clients")
											   .create("c");
		Option runIdentifierOption = OptionBuilder.withArgName("Run identifier")
												  .hasArg()	
												  .withDescription("A value used to identify QPME simulation results")
												  .withLongOpt("run-id")
												  .create("n");
		Option runOutputDirectory = OptionBuilder.withArgName("directory")
												 .hasArg()
												 .withDescription("The output directory for the simulation results, required when using -r")
												 .withLongOpt("sim-dir")
												 .create("d");
		Option outputDataFileName = OptionBuilder.withArgName("filename")
												 .hasArg()
												 .withDescription("The name of the datafile that stores the response times of the runs, will be stored in output directory")
												 .withLongOpt("sim-datafile")
												 .create("df");
		options.addOption(specFileOption);
		options.addOption(generateFullSpecification);
		options.addOption(outputFullSpecification);
		options.addOption(outputViewableQPN);
		options.addOption(outputTemplateQPN);
		options.addOption(runModeOption);
		options.addOption(numClientsOption);
		options.addOption(runIdentifierOption);
		options.addOption(runOutputDirectory);
		options.addOption(outputDataFileName);
		CommandLineParser parser = new BasicParser();
		CommandLine commLine;
		try {
			commLine = parser.parse(options, args);
		} catch (ParseException e) {
			System.err.println(e.getMessage());
			formatter.printHelp("AutoQPNPED", options);
			return null;
		}
		return commLine;
	}

}
