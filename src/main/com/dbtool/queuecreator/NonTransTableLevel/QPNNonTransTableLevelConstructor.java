package com.dbtool.queuecreator.NonTransTableLevel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.dbtool.queuecreator.AST.TrafficComposition;
import com.dbtool.queuecreator.AST.Transaction;
import com.dbtool.queuecreator.intermediate.TableConnection;
import com.dbtool.queueingpetrinet.ClientTransitionWeight;
import com.dbtool.queueingpetrinet.DepartureDiscipline;
import com.dbtool.queueingpetrinet.DeterminsticDistribution;
import com.dbtool.queueingpetrinet.ExponentialDistribution;
import com.dbtool.queueingpetrinet.ImmediateQueueingPlace;
import com.dbtool.queueingpetrinet.ImmediateTransition;
import com.dbtool.queueingpetrinet.NumericQPNWeight;
import com.dbtool.queueingpetrinet.Place;
import com.dbtool.queueingpetrinet.Queue;
import com.dbtool.queueingpetrinet.QueueingPetriNet;
import com.dbtool.queueingpetrinet.TimedQueueingPlace;
import com.dbtool.queueingpetrinet.TokenColour;
import com.dbtool.queueingpetrinet.Transition;
import com.dbtool.queueingpetrinet.TransitionColour;
import com.dbtool.queueingpetrinet.QPNWeight;
import com.dbtool.queueingpetrinet.TransitionModeConnection;
import com.dbtool.utils.ClientThinkTimeDistribution;
import com.dbtool.utils.Pair;
import com.dbtool.utils.Settings;
/**
 * Core implementation for translating a queueing network representation of the traffic composition into a QPN that models table-level non-transactional
 * locking.
 *
 */
public class QPNNonTransTableLevelConstructor {
	
	public static final String CLIENT_TOKEN_NAME = "client";
	public static final String LOCK_TOKEN_NAME = "lock";
	private static final String CLIENT_QUEUE_NAME = "Client";
	private static final String ENTERDB_TRANS_NAME = "Enter database";
	private static final String EXITDB_TRANS_NAME = "Exit database";
	public static final String ENDTRANSACTION_PLACE_NAME = "End transaction";
	public static final String BEGINTRANSACTION_PLACE_NAME = "Begin transaction";
	private static final String BEGINEXECUTION_TRANSITION_NAME = "Begin execution";
	
	private static final String ENTER_TRANS_PREFIX = "Enter ";
	private static final String EXIT_TRANS_PREFIX = "Exit ";
	private static final String LOCKWAIT_PLACE_PREFIX = "Lock-wait ";
	private static final String TABLE_PLACE_PREFIX = "Table ";
	private static final String LOCKSTORE_PLACE_PREFIX = "Lock-store ";
	private static final String PRETABLE_PLACE_PREFIX = "Pre-table ";
	private static final String POSTTABLE_PLACE_PREFIX = "Post-table ";
	private static final String ACQUIRELOCK_TRANS_PREFIX = "Acquire-lock ";
	private static final String CONTINUE_TRANS_PREFIX = "Continue from ";
	
	private final Place endTransactionPlace = new Place(ENDTRANSACTION_PLACE_NAME);
	private final Transition enterDBTrans = new ImmediateTransition(ENTERDB_TRANS_NAME, 1);
	private final Place startTransactionPlace = new Place(BEGINTRANSACTION_PLACE_NAME);
	private final Transition beginExecutionTrans = new ImmediateTransition(BEGINEXECUTION_TRANSITION_NAME, 1);
	
	private List<Queue> queues;
	private List<TableConnection> connections;
	private List<TokenColour> colours;
	private Map<String, Double> trafficProp;
	private Map<String, Map<String, Place>> tableToPlaceMap;
	private Map<String, Map<String, Transition>> tableToTransitionMap;
	private final TokenColour lockColour = new TokenColour(LOCK_TOKEN_NAME);
	private final TokenColour clientColour = new TokenColour(CLIENT_TOKEN_NAME);
	
	private List<Place> finalPlaces;
	private List<Transition> finalTransitions;

	public QPNNonTransTableLevelConstructor(TrafficComposition trafficComp, List<TokenColour> colours, List<Queue> queues, List<TableConnection> connections) {
		this.queues = new ArrayList<Queue>(queues);
		this.connections = new ArrayList<TableConnection>(connections);
		this.colours = new ArrayList<TokenColour>(colours);
		this.trafficProp = new HashMap<String, Double>();
		setupTrafficPropMap(trafficComp);	
		
		this.tableToPlaceMap = new HashMap<String, Map<String, Place>>();
		this.tableToTransitionMap = new HashMap<String, Map<String, Transition>>();
		this.finalPlaces = new ArrayList<Place>();
		this.finalTransitions = new ArrayList<Transition>();
	}
	
	private void setupTrafficPropMap(TrafficComposition trafficComp) {
		for (Transaction trans : trafficComp.getTransactions()) {
			trafficProp.put(trans.getTransactionName(), trafficComp.getProportionForTrans(trans));
		}
	}
	
	
	public QueueingPetriNet getQPN() {
		return new QueueingPetriNet(colours, finalPlaces, finalTransitions);
	}
	
	public void constructQueueingPetriNet() {
		//Create client place
		Queue clientQueue = Queue.createInfiniteServerQueue(CLIENT_QUEUE_NAME);
		TimedQueueingPlace clientPlace = new TimedQueueingPlace(CLIENT_QUEUE_NAME, DepartureDiscipline.NORMAL, clientQueue);
		if (Settings.CLIENT_THINK_RATE != 0 && Settings.CLIENT_THINK_DIST_TYPE == ClientThinkTimeDistribution.EXPONENTIAL) {
			clientQueue.addServicableToken(clientColour, new ExponentialDistribution(Settings.CLIENT_THINK_RATE));
		} else {
			clientQueue.addServicableToken(clientColour, new DeterminsticDistribution(Settings.CLIENT_THINK_RATE));
		}
		
		clientPlace.addToInitialMarking(clientColour, ClientTransitionWeight.getNumberOfClientWeight());
		//Create exit transition
		ImmediateTransition exitDBTrans = new ImmediateTransition(EXITDB_TRANS_NAME, 1);
		//Setup connections for the client control subnet
		for (TokenColour colour : colours) {
			
			TransitionColour colEnterDBTransMode = new TransitionColour(colour.getColourName(), trafficProp.get(colour.getColourName()));
			colEnterDBTransMode.addIncomingArc(clientPlace, clientColour, new NumericQPNWeight(1));
			colEnterDBTransMode.addOutgoingArc(startTransactionPlace, colour, new NumericQPNWeight(1));
			enterDBTrans.addFiringMode(colEnterDBTransMode);
			
			startTransactionPlace.addToInitialMarking(colour, new NumericQPNWeight(0));
			
			TransitionColour colBeginExecMode = new TransitionColour(colour.getColourName(), 1);
			colBeginExecMode.addIncomingArc(startTransactionPlace, colour, new NumericQPNWeight(1));
			beginExecutionTrans.addFiringMode(colBeginExecMode);
			
			endTransactionPlace.addToInitialMarking(colour, new NumericQPNWeight(0));
			
			TransitionColour colExitDBTransMode = new TransitionColour(colour.getColourName(), 1);
			colExitDBTransMode.addIncomingArc(endTransactionPlace, colour, new NumericQPNWeight(1));
			colExitDBTransMode.addOutgoingArc(clientPlace, clientColour, new NumericQPNWeight(1));
			exitDBTrans.addFiringMode(colExitDBTransMode);
		}
		finalPlaces.add(clientPlace);
		finalPlaces.add(startTransactionPlace);
		finalPlaces.add(endTransactionPlace);
		finalTransitions.add(enterDBTrans);
		finalTransitions.add(beginExecutionTrans);
		finalTransitions.add(exitDBTrans);
		//Generate QPN subnets
		translateQueuesToQPNs();
		//Connect subnets
		constructQPNConnections();
		
		this.colours.add(lockColour);
		this.colours.add(clientColour);
		for (Map<String, Place> placeMap : tableToPlaceMap.values()) {
			for (Place place : placeMap.values()) {
				finalPlaces.add(place);
			}
		}
		for (Map<String, Transition> transitionMap : tableToTransitionMap.values()) {
			for (Transition trans : transitionMap.values()) {
				finalTransitions.add(trans);
			}
		}
		
	}

	/*
	 * Connect the generated subnets together in the configuration defined by the list of TableConnection
	 */
	private void constructQPNConnections() {
		Map<Pair<String, TokenColour>, Integer> connectionOccurences = new HashMap<Pair<String, TokenColour>, Integer>();
		//Translate each connection to a connection between generated subnets
		for (TableConnection conn : connections) {
			String source = conn.getSourceTable();
			TokenColour colour = conn.getTokenColour();
			String destination = conn.getTargetTable();
			double prop = conn.getProportion();
			Transition sourceTrans;
			if (source.equals(TableConnection.NETWORK_SOURCE)) {
				sourceTrans = beginExecutionTrans;			
			}
			else {
				//Find exit point of the connection source
				sourceTrans = findTransition(source, getContinueFromTransitionName(source));
			}
			assert(sourceTrans != null);
			Place prePlaceDest;
			if (destination.equals(TableConnection.NETWORK_END)) {
				prePlaceDest = endTransactionPlace;
			}
			else {
				//Find the entry point of the connection destination
				prePlaceDest = findPlace(destination, getPreTablePlaceName(destination));
			}
			assert(prePlaceDest != null);
			Pair<String, TokenColour> connectionKey = new Pair<String, TokenColour>(source, colour);
			//Use occurrences to check if there are multiple paths from source for this token colour 
			Integer occurences = connectionOccurences.get(connectionKey);
			if (occurences == null) {
				//If first occurrence then connect source transition and dest place
				TransitionColour colSourceTrans = sourceTrans.getTransitionColour(colour.getColourName());
				colSourceTrans.addOutgoingArc(prePlaceDest, colour, new NumericQPNWeight(1));
				colSourceTrans.setFiringRate(prop);
				connectionOccurences.put(connectionKey, 1);
			}
			else {
				//If multiple paths then generate a new firing mode for the path
				TransitionColour altColourMode = new TransitionColour("Alternate-path-" + occurences + "-" + colour.getColourName(), prop);
				altColourMode.addOutgoingArc(prePlaceDest, colour, new NumericQPNWeight(1));
				sourceTrans.addFiringMode(altColourMode);
				
				TransitionColour colSourceTrans = sourceTrans.getTransitionColour(colour.getColourName());
				for (TransitionModeConnection modeConn : colSourceTrans.getIncomingArcs()) {
					altColourMode.addIncomingArc(modeConn.getConnectedPlace(), modeConn.getTokenColour(), modeConn.getArcTokenAmount());
				}
				connectionOccurences.put(connectionKey, occurences+1);
			}
		}
	}
	/*
	 * Expands the generated queues in locking subnets that apply non-transactional table level locking
	 */
	private void translateQueuesToQPNs() {
		//For each queue generate a subnet
		for (Queue q : queues) {
			Map<String, Place> placeMap = new HashMap<String, Place>();
			Map<String, Transition> transitionMap = new HashMap<String, Transition>();
			String name = q.getName();
			//Create places for the queue
			ImmediateQueueingPlace lockWaitPlace = new ImmediateQueueingPlace(getLockWaitPlaceName(name), DepartureDiscipline.FIFO);
			TimedQueueingPlace tablePlace = new TimedQueueingPlace(getTablePlaceName(name), DepartureDiscipline.NORMAL, q);
			Place lockPlace = new Place(getLockStorePlaceName(name));
			Place postTablePlace = new Place(getPostTablePlaceName(name));
			Place preTablePlace = new Place(getPreTablePlaceName(name));
			//Create transitions for the queue
			ImmediateTransition enterTrans = new ImmediateTransition(getEnterTransitionName(name), 1);
			ImmediateTransition exitTrans = new ImmediateTransition(getExitTransitionName(name), 1);
			ImmediateTransition lockAcquireTrans = new ImmediateTransition(getLockAcquireTransitionName(name), 1);
			ImmediateTransition continueFromTrans = new ImmediateTransition(getContinueFromTransitionName(name), 1);
			
			//Create transition modes for each colour (adding connections to subnet)
			for (TokenColour c : q.getServiceableTokens()) {
				TransitionColour cEnterTransMode = new TransitionColour(c.getColourName(), 1);
				cEnterTransMode.addIncomingArc(preTablePlace, c, new NumericQPNWeight(1));
				cEnterTransMode.addOutgoingArc(lockWaitPlace, c, new NumericQPNWeight(1));
				enterTrans.addFiringMode(cEnterTransMode);
				
				Boolean exclusive = q.getTokenExclusivity(c);
				assert (exclusive != null);
				
				TransitionColour cAcquireLockMode = new TransitionColour(c.getColourName(), 1);
				cAcquireLockMode.addIncomingArc(lockWaitPlace, c, new NumericQPNWeight(1));
				QPNWeight lockWeight;
				//Require 1 token if shared access and for exclusive access need weight equal to number of clients
				if (!exclusive) {
					lockWeight = new NumericQPNWeight(1);
				} else {
					lockWeight = ClientTransitionWeight.getNumberOfClientWeight();
				}
				cAcquireLockMode.addIncomingArc(lockPlace, lockColour, lockWeight);
				cAcquireLockMode.addOutgoingArc(tablePlace, c, new NumericQPNWeight(1));
				lockAcquireTrans.addFiringMode(cAcquireLockMode);
				
				TransitionColour cExitTableMode = new TransitionColour(c.getColourName(), 1);
				cExitTableMode.addIncomingArc(tablePlace, c, new NumericQPNWeight(1));
				cExitTableMode.addOutgoingArc(postTablePlace, c, new NumericQPNWeight(1));
				cExitTableMode.addOutgoingArc(lockPlace, lockColour, lockWeight);
				exitTrans.addFiringMode(cExitTableMode);
				
				TransitionColour cContinueFromMode = new TransitionColour(c.getColourName(), 1);
				cContinueFromMode.addIncomingArc(postTablePlace, c, new NumericQPNWeight(1));
				continueFromTrans.addFiringMode(cContinueFromMode);
				
				//Add initial markings of token colours to places
				lockWaitPlace.addToInitialMarking(c, new NumericQPNWeight(0));
				tablePlace.addToInitialMarking(c, new NumericQPNWeight(0));
				lockPlace.addToInitialMarking(lockColour, ClientTransitionWeight.getNumberOfClientWeight());
				postTablePlace.addToInitialMarking(c, new NumericQPNWeight(0));
				preTablePlace.addToInitialMarking(c, new NumericQPNWeight(0));
			}
			//Add places and transitions to maps so they can be accessed later
			placeMap.put(lockPlace.getName(), lockPlace);
			placeMap.put(tablePlace.getName(), tablePlace);
			placeMap.put(lockWaitPlace.getName(), lockWaitPlace);
			placeMap.put(postTablePlace.getName(), postTablePlace);
			placeMap.put(preTablePlace.getName(), preTablePlace);
			tableToPlaceMap.put(name, placeMap);
			transitionMap.put(lockAcquireTrans.getTransitionName(), lockAcquireTrans);
			transitionMap.put(enterTrans.getTransitionName(), enterTrans);
			transitionMap.put(exitTrans.getTransitionName(), exitTrans);
			transitionMap.put(continueFromTrans.getTransitionName(), continueFromTrans);
			tableToTransitionMap.put(name, transitionMap);	
		}
	}
	
	private Transition findTransition(String tableName, String transitionName) {
		Map<String, Transition> transitionMap = tableToTransitionMap.get(tableName);
		if (transitionMap != null) {
			Transition trans = transitionMap.get(transitionName);
			if (trans != null) {
				return trans;
			}
			return null;
		}
		return null;
	}
	
	private Place findPlace(String tableName, String placeName) {
		Map<String, Place> placeMap = tableToPlaceMap.get(tableName);
		if (placeMap != null) {
			Place p = placeMap.get(placeName);
			if (p != null) {
				return p;
			}
			return null;
		}
		return null;
	}
	
	//The methods below ensure consistent naming of generated transitions and places
	private String getEnterTransitionName(String name){
		return ENTER_TRANS_PREFIX + name;
	}
	
	private String getExitTransitionName(String name) {
		return EXIT_TRANS_PREFIX + name;
	}
	
	private String getLockWaitPlaceName(String name) {
		return LOCKWAIT_PLACE_PREFIX + name;
	}
	
	private String getTablePlaceName(String name) {
		return TABLE_PLACE_PREFIX + name;
	}
	
	private String getLockStorePlaceName(String name) {
		return LOCKSTORE_PLACE_PREFIX + name;
	}
	
	private String getPostTablePlaceName(String name) {
		return POSTTABLE_PLACE_PREFIX + name;
	}
	
	private String getPreTablePlaceName(String name) {
		return PRETABLE_PLACE_PREFIX + name;
	}
	
	private String getLockAcquireTransitionName(String name) {
		return ACQUIRELOCK_TRANS_PREFIX + name;
	}
	
	private String getContinueFromTransitionName(String name) {
		return CONTINUE_TRANS_PREFIX + name;
	}
}
