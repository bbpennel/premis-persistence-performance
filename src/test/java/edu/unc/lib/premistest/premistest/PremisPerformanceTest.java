package edu.unc.lib.premistest.premistest;

import static com.hp.hpl.jena.rdf.model.ResourceFactory.createProperty;
import static com.hp.hpl.jena.rdf.model.ResourceFactory.createResource;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URI;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;
import java.util.UUID;

import org.apache.commons.io.IOUtils;
import org.apache.jena.riot.RDFDataMgr;
import org.apache.jena.riot.RDFFormat;
import org.fcrepo.client.FcrepoClient;
import org.fcrepo.client.FcrepoOperationFailedException;
import org.fcrepo.client.FcrepoResponse;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.Namespace;
import org.jdom2.input.SAXBuilder;
import org.jdom2.output.XMLOutputter;
import org.junit.Before;
import org.junit.Test;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.Resource;

import edu.unc.lib.dl.xml.JDOMNamespaceUtil;

public class PremisPerformanceTest {
	
	public static final Namespace NS = Namespace.getNamespace(JDOMNamespaceUtil.PREMIS_V2_NS.getURI());
	
	private FcrepoClient client;
	
	String fedoraBase = "http://localhost:8085/fcrepo/rest/";
	
	URI objUri;
	
	DateFormat df;
	
	XMLOutputter out;
	SAXBuilder builder;
	
	private static String EVENT_URI_PREFIX = "http://repository.example/";
	private static Resource clamAgent = createResource("http://cdr.lib.unc.edu/agent/software/ClamAV");
	
	public static final Property executingAgentRole = createProperty("http://id.loc.gov/vocabulary/preservation/eventRelatedAgentRole/exe");
	public static final Property relatedObjectSource = createProperty("http://id.loc.gov/ml38281/vocabulary/preservation/eventRelatedObjectRole/sou");
	
	public PremisPerformanceTest() {
		client = FcrepoClient.client()
				.throwExceptionOnFailure()
				.build();
		
		TimeZone tz = TimeZone.getTimeZone("UTC");
		df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm'Z'");
		df.setTimeZone(tz);
		
		out = new XMLOutputter();
		builder = new SAXBuilder();
	}
	
	@Before
	public void init() throws IOException, FcrepoOperationFailedException {
		try (FcrepoResponse response = client.post(URI.create(fedoraBase))
				.perform()) {
			objUri = response.getLocation();
		}
	}
	
	@Test
	public void rdfFile() throws IOException, FcrepoOperationFailedException {
		
		// Create the log object
		URI logUri = null;
		try (FcrepoResponse resp = client.post(objUri)
				.body(new ByteArrayInputStream("".getBytes()), "text/plain")
				.perform()) {
			logUri = resp.getLocation();
		}
		
		for (int i = 0; i < 4; i++) {
			// Retrieve log object
			String logBody = retrieveRDFLog(logUri);
			
			// create new entry
			Resource premisObjResc = createRDFEvent();

			// Add log entry to file
			try (ByteArrayOutputStream outStream = new ByteArrayOutputStream()) {
				RDFDataMgr.write(outStream, premisObjResc.getModel(), RDFFormat.TURTLE_PRETTY);
				String eventString = outStream.toString("UTF-8");
				
				logBody += eventString;
			}
			
			// Stream log object back to fedora
			try (FcrepoResponse resp = client.put(logUri)
					.body(new ByteArrayInputStream(logBody.getBytes()), "text/plain")
					.perform()) {
			}
		}
		
		System.out.println(objUri);
	}
	
	private Resource createRDFEvent() {
		String eventId = UUID.randomUUID().toString();
		
		Model model = ModelFactory.createDefaultModel();
		Resource premisObjResc = model.createResource(EVENT_URI_PREFIX + eventId);

		premisObjResc.addProperty(Premis.hasEventType, "http://id.loc.gov/vocabulary/preservationEvents/virusCheck");
		premisObjResc.addProperty(Premis.hasEventDateTime, df.format(new Date()));
		premisObjResc.addProperty(Premis.hasEventOutcomeDetail, "File passed preriodic scan for viruses");
		
		Resource eventOutcomeInfo = model.createResource(EVENT_URI_PREFIX + "outcome/" + eventId);
		eventOutcomeInfo.addProperty(Premis.hasEventOutcome, "Success");
		premisObjResc.addProperty(Premis.hasEventOutcomeInformation, eventOutcomeInfo);
		
		premisObjResc.addProperty(executingAgentRole, clamAgent);
		premisObjResc.addProperty(relatedObjectSource, "http://cdr.lib.unc.edu/objects/uuid:d1b44f02-2d11-4589-b81d-3575d85353e1/DATA_FILE");
		
		return premisObjResc;
	}
	
	private String retrieveRDFLog(URI logUri) throws IOException, FcrepoOperationFailedException {
		try (FcrepoResponse resp = client.get(logUri)
				.perform()) {
			return IOUtils.toString(resp.getBody());
		}
	}
	
	@Test
	public void xmlFile() throws Exception {
		
		Document doc = new Document();
		
		Element premisEl = new Element("premis", NS);
		doc.addContent(premisEl);
		
		// Create the log file
		URI logUri = null;
		try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
			out.output(doc, baos);
			
			try (FcrepoResponse resp = client.post(objUri)
					.body(new ByteArrayInputStream(baos.toByteArray()), "text/xml")
					.perform()) {
				logUri = resp.getLocation();
			}
		}
		
		for (int i = 0; i < 3; i++) {
			Document logDoc = retrieveXMLLog(logUri);
			
			addXMLEvent(logDoc.getRootElement());
			
			persistXMLLog(logUri, logDoc);
		}
		
		System.out.println(objUri);
	}
	
	private Document retrieveXMLLog(URI logUri) throws IOException, FcrepoOperationFailedException, JDOMException {
		try (FcrepoResponse resp = client.get(logUri)
				.perform()) {
			SAXBuilder builder = new SAXBuilder();
			return builder.build(resp.getBody());
		}
	}
	
	private void addXMLEvent(Element premisEl) {
		Element event = new Element("event", NS);
		premisEl.addContent(event);

		// add event identifier UUID
		String uuid = String.format("urn:uuid:%1$s", java.util.UUID.randomUUID());
		event.addContent(new Element("eventIdentifier", NS).addContent(
				new Element("eventIdentifierType", NS).setText("URN")).addContent(
				new Element("eventIdentifierValue", NS).setText(uuid)));
		
		event.addContent(new Element("eventType", NS)
				.setText("http://id.loc.gov/vocabulary/preservationEvents/virusCheck"));
		
		event.addContent(new Element("eventDateTime", NS).setText(df.format(new Date())));
		
		event.addContent(new Element("eventDetailInformation", NS).addContent(
				new Element("eventDetail", NS).setText("File passed periodic scan for viruses.")));
		
		event.addContent(new Element("eventOutcomeInformation", NS).addContent(
				new Element("eventOutcome", NS).setText("Success")));
		
		Element linkingAgent = new Element("linkingAgentIdentifier", NS);
		event.addContent(linkingAgent);
		linkingAgent.addContent(new Element("linkingAgentIdentifierType", NS).setText("software program"));
		linkingAgent.addContent(new Element("linkingAgentValue", NS).setText("ClamAV"));
		linkingAgent.addContent(new Element("linkingAgentRole", NS).setText("executing program"));
		
		Element linkingObject = new Element("linkingObjectIdentifier", NS);
		event.addContent(linkingObject);
		linkingObject.addContent(new Element("linkingObjectIdentifierType", NS).setText("PID"));
		linkingObject.addContent(new Element("linkingObjectIdentifierValue", NS)
				.setText("uuid:f6cb7370-ad6c-4e0f-9f23-c9bb129c122b/DATA_FILE"));
		linkingObject.addContent(new Element("linkingObjectIdentifierRole", NS).setText("Source Data"));
		
	}
	
	private void persistXMLLog(URI logUri, Document doc) throws FcrepoOperationFailedException, IOException {
		try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
			out.output(doc, baos);
			
			try (FcrepoResponse resp = client.put(logUri)
				.body(new ByteArrayInputStream(baos.toByteArray()), "text/xml")
				.perform()) {
			}
		}
	}
	
	@Test
	public void fedoraObjs() {
		
	}
}
