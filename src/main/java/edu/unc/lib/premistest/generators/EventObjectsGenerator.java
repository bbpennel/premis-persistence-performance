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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.URI;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.riot.RDFDataMgr;
import org.apache.jena.riot.RDFFormat;
import org.fcrepo.client.FcrepoResponse;

import edu.unc.lib.premistest.PremisPersistenceTest.TestConfig;
import edu.unc.lib.premistest.premistest.Premis;

/**
 * Creates objects containing events represented as individual fedora resources.
 * They are stored within a ldp:DirectContainer structure to add the
 * premis:hasEvent relation to the containing object. Nested properties are
 * stored as hash uris of the event.
 * 
 * @author bbpennel
 *
 */
public class EventObjectsGenerator extends AbstractPremisPersistenceGenerator {

    private final static Resource DIRECT_CONTAINER = createResource("http://www.w3.org/ns/ldp#DirectContainer");
    private final static Property RDF_TYPE = createProperty("http://www.w3.org/1999/02/22-rdf-syntax-ns#type");
    private final static Property MEM_RESOURCE = createProperty("http://www.w3.org/ns/ldp#membershipResource");
    private final static Property MEM_RELATION = createProperty("http://www.w3.org/ns/ldp#hasMemberRelation");
    private final static Resource PARENT_RESC = createResource(".");

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
            containerResc.addProperty(RDF_TYPE, DIRECT_CONTAINER);
            containerResc.addProperty(MEM_RESOURCE, PARENT_RESC);
            containerResc.addProperty(MEM_RELATION, Premis.hasEvent);

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
    public String getTestName() {
        return "event_resources";
    }

}
