package com.dbtool.qpmerunner;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileFilter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;

import com.dbtool.queueingpetrinet.ClientTransitionWeight;
import com.dbtool.utils.OSUtils;
import com.dbtool.utils.Settings;
/**
 * This class provides automated simulations of generated QPNs using QPME. It does this by generating processes which is quite messy. This should be
 * changed if QPME is ever updated to feature an API. The implementation is quite adhoc and could do with some tidying. Can be extended to
 * retrieve data other than mean response times.
 *
 */
public class QpmeQPNRunner {
	private int startClients;
	private int endClients;
	private int clientStep;
	
	public QpmeQPNRunner(int startClients, int endClients, int clientStep) {
		this.clientStep = clientStep;
		this.startClients = startClients;
		this.endClients = endClients;
	}
	
	public void runQpmeSimulations(final String identifier, String templateQpe, String outputFolder, String outputDataFileName) {
		if (Settings.QPME_SIM_LOCATION == null) {
			System.err.println("No specified qpme location in dbtool.properties");
			return;
		}
		final File qpmeLocation = new File(Settings.QPME_SIM_LOCATION);
		if (!qpmeLocation.exists() || !qpmeLocation.isDirectory()) {
			System.err.println("Specified qpme location does not exist or is not a directory");
			return;
		}
		File qpmeTemplateQpn = new File(templateQpe);
		if (!qpmeTemplateQpn.exists() || qpmeTemplateQpn.isDirectory()) {
			System.err.println("QPME template file: " + templateQpe + " does not exist or is a directory");
			return;
		}
		File outputDir = new File(outputFolder);
		if (!outputDir.exists() || !outputDir.isDirectory()) {
			System.err.println("Output file directory: " + outputFolder + " does not exist or is not a directory");
			return;
		}
		File outputDataFile = new File(outputDir + "/" + outputDataFileName);
		if (outputDataFile.isDirectory()) {
			System.err.println("Output data file: " + outputDataFileName + " is a directory in " + outputDir);
			return;
		}
		SAXReader saxReader = new SAXReader();
		Document templateQpnDoc = null;
		try {
			templateQpnDoc = saxReader.read(qpmeTemplateQpn);
		} catch (DocumentException e) {
			System.err.println("Template file: " + templateQpe + " was not an XML file");
			return;
		}
		Map<Integer, String> generatedQpeFilePathMap = new HashMap<Integer, String>();
		System.out.println("Generating QPN model files from template...");
		//This replaces the template placeholders with each amount of clients. For each client amount an appropriate QPN file is generated.
		for (int clients = startClients; clients <= endClients; clients += clientStep) {
			Document clientQpnDoc = (Document) templateQpnDoc.clone();
			for (Object obj : clientQpnDoc.selectNodes("//*[@initial-population='" + ClientTransitionWeight.CLIENT_WEIGHT_STRING + "']")) {
				Element elem = (Element) obj;
				if (elem == null) {
					System.err.println("Badly formed template file: " + templateQpe);
					return;
				}
				elem.addAttribute("initial-population", String.valueOf(clients));
			}
			for (Object obj : clientQpnDoc.selectNodes("//*[@count='" + ClientTransitionWeight.CLIENT_WEIGHT_STRING + "']")) {
				Element elem = (Element) obj;
				if (elem == null) {
					System.err.println("Badly formed template file: " + templateQpe);
					return;
				}
				elem.addAttribute("count", String.valueOf(clients));
			}
			for (Object obj : clientQpnDoc.selectNodes("//*[@configuration-name]")) {
				Element elem = (Element) obj;
				if (elem == null) {
					System.err.println("Badly formed template file: " + templateQpe);
					return;
				}
				elem.addAttribute("configuration-name", identifier + "Clients" + clients + "Config");
			}
			for (Object obj : clientQpnDoc.selectNodes("//*[@output-directory]")) {
				Element elem = (Element) obj;
				if (elem == null) {
					System.err.println("Badly formed template file: " + templateQpe);
					return;
				}
				elem.addAttribute("output-directory", outputDir.getAbsolutePath());
			}
	    	OutputFormat format = OutputFormat.createPrettyPrint();
	    	String filename = outputDir.getAbsolutePath() + "/Clients" + String.valueOf(clients) + qpmeTemplateQpn.getName().replace(".qpetemplate", ".qpe");
	    	try {
				XMLWriter writer = new XMLWriter(new FileWriter(filename), format);
				writer.write(clientQpnDoc);
				writer.close();
			} catch (IOException e) {
				System.err.println("Error writing qpe file: " + filename);
				System.err.println("Exception message: " + e.getMessage());
			}
	    	generatedQpeFilePathMap.put(clients, filename);
		}
		//Chooses the appropriate filename for invoking QPME
		final String scriptFileName = OSUtils.isWindows() ? "SimQPN.bat" : OSUtils.isLinux() ? "SimQPN.sh" : null;
		if (scriptFileName == null) {
			System.err.println("The AutoQPNPED QPME runner does not support this operating system");
			return;
		}
		System.out.println("Settings off QPME simulations...");
		BlockingQueue<Runnable> taskQueue = new ArrayBlockingQueue<Runnable>(generatedQpeFilePathMap.size());
		ThreadPoolExecutor executor = new ThreadPoolExecutor(Settings.QPME_RUNNER_THREADS, Settings.QPME_RUNNER_THREADS, 5, TimeUnit.SECONDS, taskQueue);
		//Loop through all the generated QPN filepaths and set a simulation off for each by using a thread pool
		for (Entry<Integer, String> filenameEntry : generatedQpeFilePathMap.entrySet()) {
			final int clients = filenameEntry.getKey();
			//Perform filename changes to create correct filepath for each OS
			final String filename = OSUtils.isWindows() ? "\"" + filenameEntry.getValue() + "\"" : "file://" + filenameEntry.getValue().replaceAll(" ", "\\ ");
			//Create a new thread in the thread pool run/wait for the simulation from that
			executor.execute(new Runnable() {
				@Override
				public void run() {
					ProcessBuilder builder = new ProcessBuilder(qpmeLocation.getAbsolutePath() + "/" + scriptFileName, "-r", identifier + "Clients" + clients 
							+ "Config", filename);
					builder.directory(qpmeLocation);
					try {
						Process process= builder.start();
						InputStreamReader isr = new InputStreamReader(process.getInputStream());
			            BufferedReader ibr = new BufferedReader(isr);
			            while (ibr.readLine() != null) {
			            }
			            InputStreamReader esr = new InputStreamReader(process.getErrorStream());
			            BufferedReader ebr = new BufferedReader(esr);
			            while (ebr.readLine() != null) {
			            }
			            try {
							process.waitFor();
						} catch (InterruptedException e) {
							//Ignore and carry on
						}
					} catch (IOException e) {
						System.err.println("Unexpected error when executing the QPME script");
						System.err.println(e.getMessage());
					}
					
				}
			});
		}
		try {
			executor.shutdown();
			executor.awaitTermination(60, TimeUnit.MINUTES);
		} catch (InterruptedException e) {
			//Ignore and carry on 
		}
		System.out.println("Collecting results...");
		generateMeanResponseDatafile(identifier, outputDir, outputDataFile);
	}

	private void generateMeanResponseDatafile(final String identifier,
			File outputDir, File outputDataFile) {
		//Filter the output files to only simulation outputs
		File[] potentialOutputFiles = outputDir.listFiles(new FileFilter() {
			@Override
			public boolean accept(File arg0) {
				String filename = arg0.getName();
				return filename.startsWith("SimQPN_Output") && filename.endsWith(".simqpn");
			}
		});
		//Sort the output files to have the newest first, to reduce the search time later
		Arrays.sort(potentialOutputFiles, new Comparator<File>() {
			@Override
			public int compare(File o1, File o2) {
				return o1.lastModified() == o2.lastModified() ? 0 : o1.lastModified() > o2.lastModified() ? 1 : -1;
			}
		});
		SortedMap<Integer, SortedMap<String, Double>> transactionToClientResponseTimesMap = new TreeMap<Integer, SortedMap<String, Double>>();
		//Assemble the transaction to client response time map by looping the clients amounts and through the potential output files
		for (int clients = startClients; clients <= endClients; clients += clientStep) {
			for (int n = 0; n < potentialOutputFiles.length; n++) {
				File file = potentialOutputFiles[n];
				//The file is the correct simulation output if it contains the run identifier and is for the right number of clietns
				if (file.getName().contains(identifier + "Clients" + clients + "Config")) {
					//If it is the correct file then read the file to generate a map from transaction to mean response time
					SortedMap<String, Double> transToResponseTimeMap = retrieveResponseTimeFromSimResult(file);
					transactionToClientResponseTimesMap.put(clients, transToResponseTimeMap);
					break;
				}
			}
		}
		String firstLine = "Number of clients";
		String body = "";
		boolean constructFirstLine = true;
		//Traverse map to generate the contents of a data file for the results
		for (Entry<Integer, SortedMap<String, Double>> clientToTransactionResponseMapEntry : transactionToClientResponseTimesMap.entrySet()) {
			SortedMap<String, Double> transactionToResponseMap = clientToTransactionResponseMapEntry.getValue();
			if (constructFirstLine) {
				for (String transactionName : transactionToResponseMap.keySet()) {
					firstLine += ", " + transactionName;
				}
				firstLine += "\n";
				constructFirstLine = false;
			}
			body += clientToTransactionResponseMapEntry.getKey();
			for (Double value : transactionToResponseMap.values()) {
				body += ", " + value;
			}
			body += "\n";
		}
		//Write the generated mean response time summarisation to a file.
		try {
			FileWriter filewriter = new FileWriter(outputDataFile);
			BufferedWriter buffWriter = new BufferedWriter(filewriter);
			buffWriter.write(firstLine);
			buffWriter.write(body);
			buffWriter.close();
		} catch (IOException e) {
			System.err.println("Unexpected error when writing to data file: " + outputDataFile.getPath());
			System.err.println(e.getMessage());
		}
	}

	private SortedMap<String, Double> retrieveResponseTimeFromSimResult(File file) {
		SortedMap<String, Double> responseTimeMap = new TreeMap<String, Double>();
		SAXReader saxReader = new SAXReader();
		//Read file and retrieve mean response time results from probe data
		Document qpnResultDoc = null;
		try {
			qpnResultDoc = saxReader.read(file);
		} catch (DocumentException e) {
			System.err.println("Unexpected error when parsing the result file: " + file.getPath());
			System.err.println(e.getMessage());
			return null;
		}
		Element probeEl = (Element) qpnResultDoc.selectSingleNode("/simqpn-results/observed-element[@type='probe']");
		for (Object selectedObj : probeEl.selectNodes("color")) {
			Element colourEl = (Element) selectedObj;
			String transColourName = colourEl.attributeValue("name");
			Element meanSTEl = (Element) colourEl.selectSingleNode("metric[@type='meanST']");
			Double value = Double.valueOf(meanSTEl.attributeValue("value"));
			responseTimeMap.put(transColourName, value);
		}
		return responseTimeMap;
	}
}
