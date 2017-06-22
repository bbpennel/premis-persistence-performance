package edu.unc.lib.premistest;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URI;
import java.util.UUID;

import org.apache.commons.io.IOUtils;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.riot.RDFDataMgr;
import org.apache.jena.riot.RDFFormat;
import org.fcrepo.client.FcrepoOperationFailedException;
import org.fcrepo.client.FcrepoResponse;

import edu.unc.lib.premistest.PremisTest.TestConfig;

public class RdfLogGenerator extends AbstractPremisPersistenceGenerator {

    public RdfLogGenerator(TestConfig config) {
        super(config);
    }

    @Override
    protected void populateObjects() throws Exception {
        for (int objCnt = 0; objCnt < config.numObjects; objCnt++) {
            //System.out.println("Obj " + objCnt);
            URI objectUri = createPreservedObject();
            
            // Create the log object
            URI logUri = null;
            try (FcrepoResponse resp = client.post(objectUri)
                    .body(new ByteArrayInputStream("".getBytes()), "text/plain")
                    .perform()) {
                logUri = resp.getLocation();
            }
            
            String logUriString = logUri.toString();
            
            for (int i = 0; i < config.numEvents; i++) {
                // Retrieve log object
                String logBody = retrieveRDFLog(logUri);
                
                // create new entry
                String eventId = UUID.randomUUID().toString();
                Resource premisObjResc = createRDFEvent(logUriString + "/event" + eventId);

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
            
            // System.out.println(objUri);
        }
    }

    private String retrieveRDFLog(URI logUri) throws IOException, FcrepoOperationFailedException {
        try (FcrepoResponse resp = client.get(logUri)
                .perform()) {
            return IOUtils.toString(resp.getBody());
        }
    }
    
    @Override
    protected String getTestName() {
        return "rdf_log";
    }

}
