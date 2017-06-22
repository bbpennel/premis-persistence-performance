package edu.unc.lib.premistest;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.net.URI;
import java.util.Date;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Resource;
import org.fcrepo.client.FcrepoResponse;

import edu.unc.lib.premistest.PremisTest.TestConfig;
import edu.unc.lib.premistest.premistest.Premis;
import edu.unc.lib.premistest.premistest.RDFModelUtil;

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
    protected String getTestName() {
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
