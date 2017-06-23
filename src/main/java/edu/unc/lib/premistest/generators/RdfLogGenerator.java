/**
 * Copyright 2017 The University of North Carolina at Chapel Hill
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package edu.unc.lib.premistest.generators;

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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.unc.lib.premistest.PremisPersistenceTest.TestConfig;

/**
 * 
 * @author bbpennel
 *
 */
public class RdfLogGenerator extends AbstractPremisPersistenceGenerator {

    private static final Logger log = LoggerFactory.getLogger(RdfLogGenerator.class);
    
    public RdfLogGenerator(TestConfig config) {
        super(config);
    }

    @Override
    protected void populateObjects() throws Exception {
        System.out.println("Populating " + config.numEvents + " " + config.numObjects);
        for (int objCnt = 0; objCnt < config.numObjects; objCnt++) {
            System.out.println("Obj " + objCnt);
            log.debug("Obj {}", objCnt);
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
            
            System.out.println(objectUri);
        }
    }

    private String retrieveRDFLog(URI logUri) throws IOException, FcrepoOperationFailedException {
        try (FcrepoResponse resp = client.get(logUri)
                .perform()) {
            return IOUtils.toString(resp.getBody());
        }
    }
    
    @Override
    public String getTestName() {
        return "rdf_log";
    }

}
