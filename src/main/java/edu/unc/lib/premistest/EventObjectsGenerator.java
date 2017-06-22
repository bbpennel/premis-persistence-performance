package edu.unc.lib.premistest;

import static org.apache.jena.rdf.model.ResourceFactory.createProperty;
import static org.apache.jena.rdf.model.ResourceFactory.createResource;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.URI;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.riot.RDFDataMgr;
import org.apache.jena.riot.RDFFormat;
import org.fcrepo.client.FcrepoResponse;

import edu.unc.lib.premistest.PremisTest.TestConfig;
import edu.unc.lib.premistest.premistest.Premis;

public class EventObjectsGenerator extends AbstractPremisPersistenceGenerator {

    public EventObjectsGenerator(TestConfig config) {
        super(config);
    }

    @Override
    protected void populateObjects() throws Exception {
        for (int objCnt = 0; objCnt < config.numObjects; objCnt++) {
            URI objectUri = createPreservedObject();
            
            // Create log direct container
            URI logUri = null;
            Model model = ModelFactory.createDefaultModel();
            Resource containerResc = model.createResource("");
            containerResc.addProperty(createProperty("http://www.w3.org/1999/02/22-rdf-syntax-ns#type"),
                    createResource("http://www.w3.org/ns/ldp#DirectContainer"));
            containerResc.addProperty(createProperty("http://www.w3.org/ns/ldp#membershipResource"),
                    createResource("."));
            containerResc.addProperty(createProperty("http://www.w3.org/ns/ldp#hasMemberRelation"),
                    Premis.hasEvent);
            
            try (ByteArrayOutputStream outStream = new ByteArrayOutputStream()) {
                RDFDataMgr.write(outStream, containerResc.getModel(), RDFFormat.TURTLE_PRETTY);
                // System.out.println(outStream.toString("UTF-8"));
                InputStream inStream = new ByteArrayInputStream(outStream.toByteArray());
                
                try (FcrepoResponse resp = client.post(objectUri)
                        .slug("event")
                        .body(inStream, "application/x-turtle")
                        .perform()) {
                    logUri = resp.getLocation();
                }
            }
            
            for (int i = 0; i < config.numEvents; i++) {
                // Get event rdf resource
                Resource premisObjResc = createRDFEvent("");
                // Turn event into string and inputstream
                try (ByteArrayOutputStream outStream = new ByteArrayOutputStream()) {
                    RDFDataMgr.write(outStream, premisObjResc.getModel(), RDFFormat.TURTLE_PRETTY);
                    InputStream inStream = new ByteArrayInputStream(outStream.toByteArray());
                    
                    // Create event object
                    try (FcrepoResponse resp = client.post(logUri)
                            .slug("event" + i)
                            .body(inStream, "application/x-turtle")
                            .perform()) {
                    }
                }
            }
            
//            System.out.println(objUri);
        }
    }

    @Override
    protected String getTestName() {
        return "event_objects";
    }

}
