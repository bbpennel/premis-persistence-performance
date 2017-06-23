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
import java.io.InputStream;
import java.net.URI;
import java.util.Date;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Resource;
import org.fcrepo.client.FcrepoResponse;

import edu.unc.lib.premistest.PremisPersistenceTest.TestConfig;
import edu.unc.lib.premistest.premistest.Premis;
import edu.unc.lib.premistest.premistest.RDFModelUtil;

/**
 * 
 * @author bbpennel
 *
 */
public class HashUriEventsGenerator extends AbstractPremisPersistenceGenerator {

    public HashUriEventsGenerator(TestConfig config) {
        super(config);
    }

    @Override
    protected void populateObjects() throws Exception {
        for (int objCnt = 0; objCnt < config.numObjects; objCnt++) {
            URI objectUri = createPreservedObject();
            
            // Create 
            URI logUri = null;
            try (FcrepoResponse resp = client.post(objectUri)
                    .slug("event")
                    .perform()) {
                logUri = resp.getLocation();
            }
            
            
            String eventBaseUri = objectUri.toString() + "/event#";
            for (int i = 0; i < config.numEvents; i++) {
                // Get event rdf resource
                String eventInsert = createSparqlInsertEvent(eventBaseUri + "e" + i);
                // Turn event into string and inputstream
                InputStream inStream = new ByteArrayInputStream(eventInsert.getBytes());
                
                // Create event object
                try (FcrepoResponse resp = client.patch(logUri)
                        .body(inStream)
                        .perform()) {
                    
                }
            }
            
            //System.out.println(objUri);
        }
    }

    @Override
    public String getTestName() {
        return "hash_uris";
    }

    private String createSparqlInsertEvent(String eventUri) {
        Model model1 = ModelFactory.createDefaultModel();
        Resource premisObjResc = model1.createResource(eventUri);

        premisObjResc.addProperty(Premis.hasEventType, "http://id.loc.gov/vocabulary/preservationEvents/virusCheck");
        premisObjResc.addProperty(Premis.hasEventDateTime, df.format(new Date()));
        premisObjResc.addProperty(Premis.hasEventOutcomeDetail, "File passed preriodic scan for viruses");
        
        Model model2 = ModelFactory.createDefaultModel();
        Resource eventOutcomeInfo = model2.createResource(eventUri + "Outcome");
        eventOutcomeInfo.addProperty(Premis.hasEventOutcome, "Success");
        premisObjResc.addProperty(Premis.hasEventOutcomeInformation, eventOutcomeInfo);
        
        premisObjResc.addProperty(executingAgentRole, clamAgent);
        premisObjResc.addProperty(relatedObjectSource, "http://cdr.lib.unc.edu/objects/uuid:d1b44f02-2d11-4589-b81d-3575d85353e1/DATA_FILE");
        
        return RDFModelUtil.createSparqlInsert(model1) + ";\n" + RDFModelUtil.createSparqlInsert(model2);
    }
}
