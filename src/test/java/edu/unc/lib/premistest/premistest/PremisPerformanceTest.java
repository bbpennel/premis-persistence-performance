package edu.unc.lib.premistest.premistest;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URI;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

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

import edu.unc.lib.dl.xml.JDOMNamespaceUtil;

public class PremisPerformanceTest {
	
	public static final Namespace NS = Namespace.getNamespace(JDOMNamespaceUtil.PREMIS_V2_NS.getURI());
	
	private FcrepoClient client;
	
	String fedoraBase = "http://localhost:8085/fcrepo/rest/";
	
	URI objUri;
	
	DateFormat df;
	
	XMLOutputter out;
	SAXBuilder builder;
	
	public PremisPerformanceTest() {
		client = FcrepoClient.client()
				.throwExceptionOnFailure()
				.build();
		
		TimeZone tz = TimeZone.getTimeZone("UTC");
		df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm'Z'"); // Quoted "Z" to indicate UTC, no timezone offset
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
	public void rdfFile() {
		
		Model eventModel = ModelFactory.createDefaultModel();
		
		String uuid = String.format("urn:uuid:%1$s", java.util.UUID.randomUUID());
		// 
		
		
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
