package edu.unc.lib.premistest.generators;

import static org.apache.jena.rdf.model.ResourceFactory.createResource;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;

import org.apache.jena.rdf.model.Resource;
import org.apache.jena.riot.RDFDataMgr;
import org.apache.jena.riot.RDFFormat;
import org.fcrepo.client.FcrepoOperationFailedException;
import org.fcrepo.client.FcrepoResponse;

import edu.unc.lib.premistest.PremisPersistenceTest.TestConfig;
import edu.unc.lib.premistest.premistest.Premis;

public class EventResourcesRelatedObjectGenerator extends AbstractPremisPersistenceGenerator {

    public EventResourcesRelatedObjectGenerator(TestConfig config) {
        super(config);
    }

    @Override
    protected void populateObjects() throws Exception {
        URI eventsContainerUri = createEventsContainer();

        for (int objCnt = 0; objCnt < config.numObjects; objCnt++) {
            URI objectUri = createPreservedObject();
            Resource objectResc = createResource(objectUri.toString());
            
            for (int i = 0; i < config.numEvents; i++) {
                // Get event rdf resource
                Resource premisObjResc = createRDFEvent("");
                premisObjResc.addProperty(Premis.hasRelatedObject, objectResc);
                // Turn event into string and inputstream
                try (ByteArrayOutputStream outStream = new ByteArrayOutputStream()) {
                    RDFDataMgr.write(outStream, premisObjResc.getModel(), RDFFormat.TURTLE_PRETTY);
                    InputStream inStream = new ByteArrayInputStream(outStream.toByteArray());

                    // Create event object
                    try (FcrepoResponse resp = client.post(eventsContainerUri)
                            .body(inStream, "application/x-turtle")
                            .perform()) {
                    }
                }
            }
        }

    }
        
    private URI createEventsContainer() throws IOException, FcrepoOperationFailedException {
        String runContainer = "o"  + config.numObjects + "e" + config.numEvents
                + "_" + getTestName() + "_events_" + System.currentTimeMillis();
        
        try (FcrepoResponse response = client.post(URI.create(config.fedoraUrl))
                .slug(runContainer)
                .perform()) {
            return response.getLocation();
        }
    }

    @Override
    public String getTestName() {
        // TODO Auto-generated method stub
        return "event_related";
    }

}
