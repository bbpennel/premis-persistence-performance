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

import static org.apache.jena.rdf.model.ResourceFactory.createProperty;
import static org.apache.jena.rdf.model.ResourceFactory.createResource;

import java.io.IOException;
import java.net.URI;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.Resource;
import org.fcrepo.client.FcrepoClient;
import org.fcrepo.client.FcrepoOperationFailedException;
import org.fcrepo.client.FcrepoResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.unc.lib.premistest.PremisPersistenceTest.TestConfig;
import edu.unc.lib.premistest.premistest.Premis;

/**
 * 
 * @author bbpennel
 *
 */
public abstract class AbstractPremisPersistenceGenerator {
    
    private static final Logger log = LoggerFactory.getLogger(AbstractPremisPersistenceGenerator.class);
    
    protected static Resource clamAgent = createResource("http://cdr.lib.unc.edu/agent/software/ClamAV");
    
    public static final Property executingAgentRole = createProperty("http://id.loc.gov/vocabulary/preservation/eventRelatedAgentRole/exe");
    public static final Property relatedObjectSource = createProperty("http://id.loc.gov/ml38281/vocabulary/preservation/eventRelatedObjectRole/sou");
    
    DateFormat df;
    
    protected final TestConfig config;
    protected URI runContainerUri;
    protected String runContainer;
    
    protected FcrepoClient client;
    
    protected AbstractPremisPersistenceGenerator(TestConfig config) {
        this.config = config;
        
        client = FcrepoClient.client()
              .throwExceptionOnFailure()
              .build();
        
        TimeZone tz = TimeZone.getTimeZone("UTC");
        df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm'Z'");
        df.setTimeZone(tz);
    }
    
    public void run() throws Exception {
        String runContainer = "o"  + config.numObjects + "e" + config.numEvents
                + "_" + getTestName() + "_" + System.currentTimeMillis();
        createRunContainer(runContainer);
        
        long start = System.currentTimeMillis();
        
        populateObjects();
        
        printResults(start);
    }
    
    protected abstract void populateObjects() throws Exception;
    
    public abstract String getTestName();
    
    protected void createRunContainer(String container) throws IOException {
        try (FcrepoResponse response = client.post(URI.create(config.fedoraUrl))
                .slug(container)
                .perform()) {
            runContainerUri = response.getLocation();
        } catch (FcrepoOperationFailedException e) {
        }
    }
    
    protected URI createPreservedObject() throws IOException, FcrepoOperationFailedException {
        try (FcrepoResponse response = client.post(runContainerUri)
                .perform()) {
            return response.getLocation();
        }
    }
    
    protected Resource createRDFEvent(String eventUri) {
        Model model = ModelFactory.createDefaultModel();
        Resource premisObjResc = model.createResource(eventUri);

        premisObjResc.addProperty(Premis.hasEventType, "http://id.loc.gov/vocabulary/preservationEvents/virusCheck");
        premisObjResc.addProperty(Premis.hasEventDateTime, df.format(new Date()));
        premisObjResc.addProperty(Premis.hasEventOutcomeDetail, "File passed preriodic scan for viruses");
        
        Resource eventOutcomeInfo = model.createResource("#outcome");
        eventOutcomeInfo.addProperty(Premis.hasEventOutcome, "Success");
        premisObjResc.addProperty(Premis.hasEventOutcomeInformation, eventOutcomeInfo);
        
        premisObjResc.addProperty(executingAgentRole, clamAgent);
        premisObjResc.addProperty(relatedObjectSource, "http://cdr.lib.unc.edu/objects/uuid:d1b44f02-2d11-4589-b81d-3575d85353e1/DATA_FILE");
        
        return premisObjResc;
    }

    private void printResults(long start) {
        if (config.silentMode) {
            return;
        }
        
        long total = System.currentTimeMillis() - start;
        
        long perEvent = total / (config.numEvents * config.numObjects);
        log.info("{} (events={}, objs={}) = {}ms ({}ms/objEvent)",
                new Object[] {getTestName(), config.numEvents,
                        config.numObjects, total, perEvent});
    }
}
