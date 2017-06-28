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
import java.util.Date;

import org.fcrepo.client.FcrepoOperationFailedException;
import org.fcrepo.client.FcrepoResponse;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.Namespace;
import org.jdom2.input.SAXBuilder;
import org.jdom2.output.XMLOutputter;

import edu.unc.lib.premistest.PremisPersistenceTest.TestConfig;

/**
 * Creates objects which contain a binary object which holds an XML log
 * containing all premis events for that object
 * 
 * @author bbpennel
 *
 */
public class XmlLogGenerator extends AbstractPremisPersistenceGenerator {
    public static final String PREMIS_V2_PREFIX = "premis";
    public static final String PREMIS_V2_URI = "info:lc/xmlns/premis-v2";
    public static final Namespace PREMIS_V2_NS = Namespace.getNamespace(
            PREMIS_V2_PREFIX, PREMIS_V2_URI);

    XMLOutputter out;
    SAXBuilder builder;

    public XmlLogGenerator(TestConfig config) {
        super(config);

        out = new XMLOutputter();
        builder = new SAXBuilder();
    }

    @Override
    protected void populateObjects() throws Exception {
        for (int objCnt = 0; objCnt < config.numObjects; objCnt++) {
            URI objectUri = createPreservedObject();

            Document doc = new Document();

            Element premisEl = new Element("premis", PREMIS_V2_NS);
            doc.addContent(premisEl);

            // Create the log file
            URI logUri = null;
            try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
                out.output(doc, baos);

                try (FcrepoResponse resp = client.post(objectUri)
                        .body(new ByteArrayInputStream(baos.toByteArray()), "text/xml")
                        .perform()) {
                    logUri = resp.getLocation();
                }
            }

            for (int i = 0; i < config.numEvents; i++) {
                Document logDoc = retrieveXMLLog(logUri);

                addXMLEvent(logDoc.getRootElement());

                persistXMLLog(logUri, logDoc);
            }

            // System.out.println(objUri);
        }
    }

    @Override
    public String getTestName() {
        return "xml_log";
    }

    private Document retrieveXMLLog(URI logUri) throws IOException, FcrepoOperationFailedException, JDOMException {
        try (FcrepoResponse resp = client.get(logUri)
                .perform()) {
            SAXBuilder builder = new SAXBuilder();
            return builder.build(resp.getBody());
        }
    }

    private void addXMLEvent(Element premisEl) {
        Element event = new Element("event", PREMIS_V2_NS);
        premisEl.addContent(event);

        // add event identifier UUID
        String uuid = String.format("urn:uuid:%1$s", java.util.UUID.randomUUID());
        event.addContent(new Element("eventIdentifier", PREMIS_V2_NS).addContent(
                new Element("eventIdentifierType", PREMIS_V2_NS).setText("URN")).addContent(
                new Element("eventIdentifierValue", PREMIS_V2_NS).setText(uuid)));

        event.addContent(new Element("eventType", PREMIS_V2_NS)
                .setText("http://id.loc.gov/vocabulary/preservationEvents/virusCheck"));

        event.addContent(new Element("eventDateTime", PREMIS_V2_NS).setText(df.format(new Date())));

        event.addContent(new Element("eventDetailInformation", PREMIS_V2_NS).addContent(
                new Element("eventDetail", PREMIS_V2_NS).setText("File passed periodic scan for viruses.")));

        event.addContent(new Element("eventOutcomeInformation", PREMIS_V2_NS).addContent(
                new Element("eventOutcome", PREMIS_V2_NS).setText("Success")));

        Element linkingAgent = new Element("linkingAgentIdentifier", PREMIS_V2_NS);
        event.addContent(linkingAgent);
        linkingAgent.addContent(new Element("linkingAgentIdentifierType", PREMIS_V2_NS).setText("software program"));
        linkingAgent.addContent(new Element("linkingAgentValue", PREMIS_V2_NS).setText("ClamAV"));
        linkingAgent.addContent(new Element("linkingAgentRole", PREMIS_V2_NS).setText("executing program"));

        Element linkingObject = new Element("linkingObjectIdentifier", PREMIS_V2_NS);
        event.addContent(linkingObject);
        linkingObject.addContent(new Element("linkingObjectIdentifierType", PREMIS_V2_NS).setText("PID"));
        linkingObject.addContent(new Element("linkingObjectIdentifierValue", PREMIS_V2_NS)
                .setText("uuid:f6cb7370-ad6c-4e0f-9f23-c9bb129c122b/DATA_FILE"));
        linkingObject.addContent(new Element("linkingObjectIdentifierRole", PREMIS_V2_NS).setText("Source Data"));

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
}
