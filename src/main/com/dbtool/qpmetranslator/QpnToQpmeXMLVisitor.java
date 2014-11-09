package com.dbtool.qpmetranslator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import com.dbtool.queuecreator.NonTransTableLevel.QPNNonTransTableLevelConstructor;
import com.dbtool.queueingpetrinet.ClientTransitionWeight;
import com.dbtool.queueingpetrinet.Colour;
import com.dbtool.queueingpetrinet.DepartureDiscipline;
import com.dbtool.queueingpetrinet.DeterminsticDistribution;
import com.dbtool.queueingpetrinet.ExponentialDistribution;
import com.dbtool.queueingpetrinet.ImmediateQueueingPlace;
import com.dbtool.queueingpetrinet.ImmediateTransition;
import com.dbtool.queueingpetrinet.Place;
import com.dbtool.queueingpetrinet.QPNTranslator;
import com.dbtool.queueingpetrinet.Queue;
import com.dbtool.queueingpetrinet.QueueSchedule;
import com.dbtool.queueingpetrinet.QueueingPetriNet;
import com.dbtool.queueingpetrinet.QueueingPlace;
import com.dbtool.queueingpetrinet.ServiceDistribution;
import com.dbtool.queueingpetrinet.TimedQueueingPlace;
import com.dbtool.queueingpetrinet.TimedTransition;
import com.dbtool.queueingpetrinet.TokenColour;
import com.dbtool.queueingpetrinet.Transition;
import com.dbtool.queueingpetrinet.TransitionColour;
import com.dbtool.queueingpetrinet.TransitionModeConnection;
import com.dbtool.queueingpetrinet.QPNWeight;
import com.dbtool.utils.Settings;
/**
 * This class creates an XML file that represents the input QPN in the QPME format. It uses a visitor pattern to traverse the generated QPN's
 * structure. To keep track of visited components a variety of maps are used. The implementation is long but relatively straight forward. Currently
 * it does not provide a location value in the meta data elements of components. An implementation could be written to generate neat locations that will
 * make the generated QPNs easier to view in QPME.
 *
 */
public class QpnToQpmeXMLVisitor implements QPNTranslator {
	
	private static final String DEFAULT_CONFIG_NAME = "Default config";
	private static final int DEFAULT_STATS_LEVEL = 1;

	private final long BASE_ID = 1_000_000_000_000L;
	
	private long id_counter = 0;
	private Set<Connection> foundConnections = new HashSet<Connection>();
	
	private Map<TokenColour, Long> colourToIdMap = new HashMap<TokenColour, Long>();
	private Map<Queue, Long> queueToIdMap = new HashMap<Queue, Long>();
	private Map<Place, Long> placeToIdMap = new HashMap<Place, Long>();
	private Map<Transition, Long> transitionToIdMap = new HashMap<Transition, Long>();
	private Map<Place, Map<TokenColour, Long>> colourRefToIdMap = new HashMap<Place, Map<TokenColour,Long>>();
	
	private Place startTransPlace;
	private Place endTransPlace;
	
	private boolean viewable;
	private Transition currentTrans = null;
	
	
	@Override
	public Object visit(Colour colour) {
		// Not needed currently
		return null;
	}

	@Override
	public Element visit(TokenColour colour) {
		Element colourEl = DocumentHelper.createElement("color");
		long id = getUniqueComponentId();
		colourEl.addAttribute("id", Long.toString(id));
		colourEl.addAttribute("name", colour.getColourName());
		colourEl.addAttribute("description", colour.getColourName());
		colourEl.addAttribute("real-color", "#ff0000");
		colourToIdMap.put(colour, id);
		return colourEl;
		
	}

	@Override
	public Element visit(TransitionColour colour) {
		Element transColourEl = DocumentHelper.createElement("mode");
		long transColId = getUniqueComponentId();
		transColourEl.addAttribute("id", Long.toString(transColId));
		transColourEl.addAttribute("name", colour.getColourName());
		transColourEl.addAttribute("firing-weight", String.valueOf(colour.getFiringRate()));
		transColourEl.addAttribute("real-color", "#ff0000");
		Element connectionsEl = transColourEl.addElement("connections");
		for (TransitionModeConnection conn : colour.getIncomingArcs()) {
			Element connEl = connectionsEl.addElement("connection");
			long connId = getUniqueComponentId();
			connEl.addAttribute("id", Long.toString(connId));
			long colourRefId = colourRefToIdMap.get(conn.getConnectedPlace()).get(conn.getTokenColour());
			connEl.addAttribute("source-id", Long.toString(colourRefId));
			connEl.addAttribute("target-id", Long.toString(transColId));
			QPNWeight weight = conn.getArcTokenAmount();
			connEl.addAttribute("count", translateWeightToString(weight));
			foundConnections.add(new Connection(placeToIdMap.get(conn.getConnectedPlace()), transitionToIdMap.get(currentTrans)));
		}
		for (TransitionModeConnection conn : colour.getOutgoingArcs()) {
			Element connEl = connectionsEl.addElement("connection");
			long connId = getUniqueComponentId();
			connEl.addAttribute("id", Long.toString(connId));
			long colourRefId = colourRefToIdMap.get(conn.getConnectedPlace()).get(conn.getTokenColour());
			connEl.addAttribute("source-id", Long.toString(transColId));
			connEl.addAttribute("target-id", Long.toString(colourRefId));
			QPNWeight weight = conn.getArcTokenAmount();
			connEl.addAttribute("count", translateWeightToString(weight));
			foundConnections.add(new Connection(transitionToIdMap.get(currentTrans), placeToIdMap.get(conn.getConnectedPlace())));
		}
		return transColourEl;
		
	}

	@Override
	public Object visit(Transition trans) {
		// Not needed currently
		return null;
	}

	@Override
	public Element visit(ImmediateTransition trans) {
		currentTrans = trans;
		Element transitionEl = DocumentHelper.createElement("transition");
		long transId = getUniqueComponentId();
		transitionEl.addAttribute("id", Long.toString(transId));
		transitionEl.addAttribute("name", trans.getTransitionName());
		transitionEl.addAttribute("priority", "0");
		transitionEl.addAttribute("weight", String.valueOf(trans.getFiringRate()));
		transitionEl.addAttribute("type", "immediate-transition");
		transitionToIdMap.put(trans, transId);
		Element connectionsEl = transitionEl.addElement("connections");
		Element modesEl = transitionEl.addElement("modes");
		for (TransitionColour transColour :trans.getAllTransitionColours()) {
			Element modeEl = (Element) transColour.accept(this);
			Element modeConnectionsEl = modeEl.element("connections");
			modeConnectionsEl.detach();
			modesEl.add(modeEl);
			for (Object connElObj : modeConnectionsEl.elements("connection")) {
				Element connEl = (Element) connElObj;
				connEl.detach();
				connectionsEl.add(connEl);
			}
		}
		currentTrans = null;
		return transitionEl;
	}

	@Override
	public Element visit(Place place) {
		Element placeEl = DocumentHelper.createElement("place");
		long placeId = getUniqueComponentId();
		placeEl.addAttribute("id", Long.toString(placeId));
		String placeName = place.getName();
		if (placeName.equals(QPNNonTransTableLevelConstructor.BEGINTRANSACTION_PLACE_NAME)) {
			startTransPlace = place;
		}
		if (placeName.equals(QPNNonTransTableLevelConstructor.ENDTRANSACTION_PLACE_NAME)) {
			endTransPlace = place;
		}
		placeEl.addAttribute("name", placeName);
		placeEl.addAttribute("type", "ordinary-place");
		placeEl.addAttribute("departure-discipline", translateDepartureDiscipline(DepartureDiscipline.NORMAL));
		Map<TokenColour, Long> colourRefMap = new HashMap<TokenColour, Long>();
		colourRefToIdMap.put(place, colourRefMap);
		Element colourRefsEl = placeEl.addElement("color-refs");
		for (Entry<TokenColour, QPNWeight> marking : place.getIntialMarking()) {
			TokenColour col = marking.getKey();
			long colourRefId = getUniqueComponentId();
			long colId = colourToIdMap.get(col);
			Element colourRefEl = colourRefsEl.addElement("color-ref");
			colourRefEl.addAttribute("id", Long.toString(colourRefId));
			colourRefEl.addAttribute("color-id", Long.toString(colId));
			colourRefEl.addAttribute("initial-population", translateWeightToString(marking.getValue()));
			colourRefEl.addAttribute("maximum-capacity", "0");
			colourRefMap.put(col, colourRefId);
		}
		Element metaAttributesEl = placeEl.addElement("meta-attributes");
		metaAttributesEl.add(createDefaultConfigMetaAttribute(DEFAULT_STATS_LEVEL));
		placeToIdMap.put(place, placeId);
		return placeEl;
	}

	@Override
	public Object visit(QueueingPlace place) {
		// Not needed currently
		return null;
	}

	@Override
	public Element visit(ImmediateQueueingPlace place) {
		Element placeEl = DocumentHelper.createElement("place");
		long placeId = getUniqueComponentId();
		placeEl.addAttribute("id", Long.toString(placeId));
		placeEl.addAttribute("name", place.getName());
		placeEl.addAttribute("type", "ordinary-place");
		placeEl.addAttribute("departure-discipline", translateDepartureDiscipline(place.getDepartureDiscipline()));
		Map<TokenColour, Long> colourRefMap = new HashMap<TokenColour, Long>();
		colourRefToIdMap.put(place, colourRefMap);
		Element colourRefsEl = placeEl.addElement("color-refs");
		for (Entry<TokenColour, QPNWeight> marking : place.getIntialMarking()) {
			TokenColour col = marking.getKey();
			long colourRefId = getUniqueComponentId();
			long colId = colourToIdMap.get(col);
			Element colourRefEl = colourRefsEl.addElement("color-ref");
			colourRefEl.addAttribute("id", Long.toString(colourRefId));
			colourRefEl.addAttribute("color-id", Long.toString(colId));
			colourRefEl.addAttribute("initial-population", translateWeightToString(marking.getValue()));
			colourRefEl.addAttribute("maximum-capacity", "0");
			colourRefMap.put(col, colourRefId);
		}
		Element metaAttributesEl = placeEl.addElement("meta-attributes");
		metaAttributesEl.add(createDefaultConfigMetaAttribute(DEFAULT_STATS_LEVEL));
		placeToIdMap.put(place, placeId);
		return placeEl;
	}

	@Override
	public Element visit(TimedQueueingPlace place) {
		Element placeEl = DocumentHelper.createElement("place");
		long placeId = getUniqueComponentId();
		placeEl.addAttribute("id", Long.toString(placeId));
		placeEl.addAttribute("name", place.getName());
		placeEl.addAttribute("type", "queueing-place");
		placeEl.addAttribute("departure-discipline", translateDepartureDiscipline(place.getDepartureDiscipline()));
		Queue placeQueue = place.getQueue();
		long queueId = queueToIdMap.get(placeQueue);
		placeEl.addAttribute("queue-ref", Long.toString(queueId));
		Map<TokenColour, Long> colourRefMap = new HashMap<TokenColour, Long>();
		colourRefToIdMap.put(place, colourRefMap);
		Element colourRefsEl = placeEl.addElement("color-refs");
		for (TokenColour colour : placeQueue.getServiceableTokens()) {
			long colourRefId = getUniqueComponentId();
			long colId = colourToIdMap.get(colour);
			QPNWeight numberOfColTok = place.getMarkingForColour(colour);
			Element colourRefEl = colourRefsEl.addElement("color-ref");
			colourRefEl.addAttribute("id", Long.toString(colourRefId));
			colourRefEl.addAttribute("color-id", Long.toString(colId));
			colourRefEl.addAttribute("initial-population", translateWeightToString(numberOfColTok));
			colourRefEl.addAttribute("maximum-capacity", "0");
			colourRefEl.addAttribute("ranking", "0");
			colourRefEl.addAttribute("priority", "0");
			ServiceDistribution dist = placeQueue.getTokenDistribution(colour);
			Element distEl = (Element) dist.accept(this);
			for (Object attrO : distEl.attributes()) {
				Attribute attr = (Attribute) attrO;
				colourRefEl.addAttribute(attr.getName(), attr.getValue());
			}
			colourRefMap.put(colour, colourRefId);
			
		}
		Element metaAttributesEl = placeEl.addElement("meta-attributes");
		metaAttributesEl.add(createDefaultConfigMetaAttribute(DEFAULT_STATS_LEVEL));
		placeToIdMap.put(place, placeId);
		return placeEl;
	}

	@Override
	public Element visit(QueueingPetriNet qpn) {
		Element netEl = DocumentHelper.createElement("net");
		netEl.addAttribute("qpme-version", "2.0.1");
		Element coloursEl = netEl.addElement("colors");
		for (TokenColour col : qpn.getColours()) {
			Element colEl = (Element) col.accept(this);
			coloursEl.add(colEl);
		}
		List<Queue> queues = enumerateQueues(qpn.getPlaces());
		Element queuesEl = netEl.addElement("queues");
		for (Queue queue : queues) {
			Element queueEl = (Element) queue.accept(this);
			queuesEl.add(queueEl);
		}
		Element placesEl = netEl.addElement("places");
		for (Place place: qpn.getPlaces()) {
			Element placeEl = (Element) place.accept(this);
			placesEl.add(placeEl);
		}
		Element transitionsEl = netEl.addElement("transitions");
		for (Transition trans : qpn.getTransitions()) {
			Element transitionEl = (Element) trans.accept(this);
			transitionsEl.add(transitionEl);
		}
		Element connectionsEl = netEl.addElement("connections");
		for (Connection conn : foundConnections) {
			Element connectionEl = connectionsEl.addElement("connection");
			connectionEl.addAttribute("source-id", Long.toString(conn.source_id));
			connectionEl.addAttribute("target-id", Long.toString(conn.dest_id));
			connectionEl.addAttribute("id", Long.toString(getUniqueComponentId()));
		}
		netEl.add(createProbesElement(qpn));
		
		//Provides the standard run configuration, could be extracted to be further user defined in future.
		Element metaAttributesEl = netEl.addElement("meta-attributes");
		Element standardRunConfigEl = metaAttributesEl.addElement("meta-attribute");
		standardRunConfigEl.addAttribute("name", "sim-qpn");
		standardRunConfigEl.addAttribute("configuration-name", DEFAULT_CONFIG_NAME);
		long configId = getUniqueComponentId();
		standardRunConfigEl.addAttribute("id", String.valueOf(configId));
		standardRunConfigEl.addAttribute("scenario", "1");
		standardRunConfigEl.addAttribute("stopping-rule", "ABSPRC");
		standardRunConfigEl.addAttribute("time-before-initial-heart-beat", "100000");
		standardRunConfigEl.addAttribute("time-between-stop-checks", "100000");
		standardRunConfigEl.addAttribute("seconds-between-stop-checks", "60");
		standardRunConfigEl.addAttribute("seconds-between-heart-beats","60");
		standardRunConfigEl.addAttribute("verbosity-level","0");
		standardRunConfigEl.addAttribute("output-directory",".");
		standardRunConfigEl.addAttribute("ramp-up-length","100000.0");
		standardRunConfigEl.addAttribute("total-run-length","1.0E9");
		return netEl;
	}
	
	@Override
	public Object visit(TimedTransition trans) {
		// Not needed currently
		return null;
	}

	@Override
	public Element visit(Queue queue) {
		Element queueEl = DocumentHelper.createElement("queue");
		long queueId = getUniqueComponentId();
		queueEl.addAttribute("id", Long.toString(queueId));
		queueEl.addAttribute("name", queue.getName());
		queueEl.addAttribute("strategy", translateQueueSchedule(queue.getSchedule()));
		queueEl.addAttribute("number-of-servers", String.valueOf(queue.getNumberOfServers()));
		queueToIdMap.put(queue, queueId);
		return queueEl;
	}
	
	@Override
	public Element visit(ExponentialDistribution dist) {
		Element distEl = DocumentHelper.createElement("dist");
		distEl.addAttribute("distribution-function", "Exponential");
		distEl.addAttribute("lambda", String.valueOf(dist.getLambda()));
		return distEl;
	}
	

	@Override
	public Object visit(DeterminsticDistribution dist) {
		Element distEl = DocumentHelper.createElement("dist");
		distEl.addAttribute("distribution-function", "Deterministic");
		distEl.addAttribute("p1", String.valueOf(dist.getRate()));
		return distEl;
	}

	@Override
	public Document translate(QueueingPetriNet qpn) {
		this.viewable = false;
		return constructXMLDocument(qpn);
	}

	private Document constructXMLDocument(QueueingPetriNet qpn) {
		colourRefToIdMap.clear();
		transitionToIdMap.clear();
		foundConnections.clear();
		colourToIdMap.clear();
		queueToIdMap.clear();
		placeToIdMap.clear();
		Document doc = DocumentHelper.createDocument();
		Element netElement = visit(qpn);
		doc.add(netElement);
		return doc;
	}
	
	@Override
	public Document translateToViewable(QueueingPetriNet qpn) {
		this.viewable = true;
		return constructXMLDocument(qpn);
	}
	
	private Element createProbesElement(QueueingPetriNet qpn) {
		Element probesEl = DocumentHelper.createElement("probes");
		Element responseProbeEl = probesEl.addElement("probe");
		responseProbeEl.addAttribute("name", "Response time probe");
		responseProbeEl.addAttribute("start-trigger", "entry");
		responseProbeEl.addAttribute("end-trigger", "exit");
		long probeId = getUniqueComponentId();
		responseProbeEl.addAttribute("id", String.valueOf(probeId));
		long startPlaceId = placeToIdMap.get(startTransPlace);
		responseProbeEl.addAttribute("start-place-id", String.valueOf(startPlaceId));
		long endPlaceId = placeToIdMap.get(endTransPlace);
		responseProbeEl.addAttribute("end-place-id", String.valueOf(endPlaceId));
		Element colourRefsEl = responseProbeEl.addElement("color-refs");
		for (TokenColour col : qpn.getColours()) {
			if (col.getColourName().equals(QPNNonTransTableLevelConstructor.CLIENT_TOKEN_NAME) ||
					col.getColourName().equals(QPNNonTransTableLevelConstructor.LOCK_TOKEN_NAME)) {
					continue;
			}
			Element colRefEl = colourRefsEl.addElement("color-ref");
			long colourRefId = getUniqueComponentId();
			colRefEl.addAttribute("id", String.valueOf(colourRefId));
			colRefEl.addAttribute("color-id", String.valueOf(colourToIdMap.get(col)));
			Element colRefmetaAttributesEl = colRefEl.addElement("meta-attributes");
			colRefmetaAttributesEl.add(createDefaultConfigColourRefMetaAttribute());
		}
		Element metaAttributesEl = responseProbeEl.addElement("meta-attributes");
		metaAttributesEl.add(createDefaultConfigMetaAttribute(4));
		return probesEl;
	}
	
	private String translateWeightToString(QPNWeight weight) {
		String weightTranslation = weight.getWeightAsString();
		if (viewable && weightTranslation.equals(ClientTransitionWeight.CLIENT_WEIGHT_STRING)) {
			return String.valueOf(Settings.DEFAULT_NUMBER_OF_CLIENTS);
		}
		return weightTranslation;
	}
	
	private long getUniqueComponentId() {
		id_counter++;
		return BASE_ID + id_counter;
	}
	
	private Element createDefaultConfigMetaAttribute(int statsLevel) {
		Element metaAttributeEl = DocumentHelper.createElement("meta-attribute");
		metaAttributeEl.addAttribute("name","sim-qpn");
		metaAttributeEl.addAttribute("configuration-name", DEFAULT_CONFIG_NAME);
		long id = getUniqueComponentId();
		metaAttributeEl.addAttribute("id", String.valueOf(id));
		metaAttributeEl.addAttribute("statsLevel", String.valueOf(statsLevel));
		return metaAttributeEl;
	}
	
	private Element createDefaultConfigColourRefMetaAttribute() {
		Element metaAttributeEl = DocumentHelper.createElement("meta-attribute");
		metaAttributeEl.addAttribute("name","sim-qpn");
		metaAttributeEl.addAttribute("configuration-name", DEFAULT_CONFIG_NAME);
		long id = getUniqueComponentId();
		metaAttributeEl.addAttribute("id", String.valueOf(id));
		metaAttributeEl.addAttribute("signLev","0.05");
		metaAttributeEl.addAttribute("reqAbsPrc", "50");
		metaAttributeEl.addAttribute("reqRelPrc", "0.05");
		metaAttributeEl.addAttribute("batchSize", "200");
		metaAttributeEl.addAttribute("minBatches", "60");
		metaAttributeEl.addAttribute("numBMeansCorlTested", "50");
		metaAttributeEl.addAttribute("bucketSize", "100.0");
		metaAttributeEl.addAttribute("maxBuckets", "1000");
        return metaAttributeEl;
	}
	
	private List<Queue> enumerateQueues(List<Place> places) {
		List<Queue> queues = new ArrayList<Queue>();
		for (Place p : places) {
			if (p instanceof TimedQueueingPlace) {
				TimedQueueingPlace queuePlace = (TimedQueueingPlace) p;
				queues.add(queuePlace.getQueue());
			}
		}
		return queues;
	}
	
	private String translateDepartureDiscipline(DepartureDiscipline disc) {
		switch(disc) {
		case NORMAL : return "NORMAL";
		case FIFO : return "FIFO";
		default : return "NORMAL";
		}
	}
	
	private String translateQueueSchedule(QueueSchedule schedule) {
		switch(schedule) {
		case FCFS : return "FCFS";
		case INFINITE_SERVER : return "IS";
		default: return "FCFS";
		}
	}
	
	private class Connection {
		public long source_id;
		public long dest_id;
		
		public Connection(long source, long dest) {
			this.source_id = source;
			this.dest_id = dest;
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + getOuterType().hashCode();
			result = prime * result + (int) (dest_id ^ (dest_id >>> 32));
			result = prime * result + (int) (source_id ^ (source_id >>> 32));
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			Connection other = (Connection) obj;
			if (!getOuterType().equals(other.getOuterType()))
				return false;
			if (dest_id != other.dest_id)
				return false;
			if (source_id != other.source_id)
				return false;
			return true;
		}

		private QpnToQpmeXMLVisitor getOuterType() {
			return QpnToQpmeXMLVisitor.this;
		}
		
	}

}
