package edu.unc.lib.premistest.premistest;

public class PremisPerformanceTest {
    
//    public static final Namespace NS = Namespace.getNamespace(JDOMNamespaceUtil.PREMIS_V2_NS.getURI());
//    
//    private FcrepoClient client;
//    
//    String runBase;
//    
//    String fcrepoBase = "http://localhost:8080/fcrepo/rest/";
//    String fcrepoJettyLocation = "target/fcrepo-webapp-jetty-console.jar";
//    String fcrepoHome = "target/fcrepo4-data";
//    
//    boolean warmingUp;
//    
//    URI runContainerUri;
//    URI objUri;
//    
//    DateFormat df;
//    
//    XMLOutputter out;
//    SAXBuilder builder;
//    
//    private final FcrepoTomcatWrapper tomcatWrapper;
//    
//    private static String EVENT_URI_PREFIX = "http://repository.example/";
//    private static Resource clamAgent = createResource("http://cdr.lib.unc.edu/agent/software/ClamAV");
//    
//    public static final Property executingAgentRole = createProperty("http://id.loc.gov/vocabulary/preservation/eventRelatedAgentRole/exe");
//    public static final Property relatedObjectSource = createProperty("http://id.loc.gov/ml38281/vocabulary/preservation/eventRelatedObjectRole/sou");
//    
//    public static final String directContainerUpdate =
//            "@prefix ldp: <http://www.w3.org/ns/ldp#>\n <> a ldp:DirectContainer;\n ldp:membershipResource <.> ;";
//    
//    public PremisPerformanceTest() throws Exception {
//        client = FcrepoClient.client()
//                .throwExceptionOnFailure()
//                .build();
//        
//        TimeZone tz = TimeZone.getTimeZone("UTC");
//        df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm'Z'");
//        df.setTimeZone(tz);
//        
//        out = new XMLOutputter();
//        builder = new SAXBuilder();
//        
//        tomcatWrapper = new FcrepoTomcatWrapper(8080, fcrepoHome, "target/fcrepo-webapp-4.7.3.war");
//        
//        warmingUp = false;
//    }
//    
//    
//    
//    @Before
//    public void init() throws Exception {
//        startServer();
//    }
//    
//    @After
//    public void after() throws Exception {
//        stopServer();
//    }
//    
//    private void startServer() throws Exception {
//        tomcatWrapper.start();
//        warmup();
//    }
//    
//    private void stopServer() throws Exception {
//        try {
//            tomcatWrapper.stop();
//        } catch (Exception e) {
//            // fedora often throws exceptions on shutdown
//        }
//    }
//    
//    private void createRunContainer(String container) throws IOException {
//        try (FcrepoResponse response = client.post(URI.create(fcrepoBase))
//                .slug(container)
//                .perform()) {
//            runContainerUri = response.getLocation();
//        } catch (FcrepoOperationFailedException e) {
//        }
//    }
//    
//    private void createBaseObject(String container) throws IOException, FcrepoOperationFailedException {
//        try (FcrepoResponse response = client.post(runContainerUri)
//                .perform()) {
//            objUri = response.getLocation();
//        }
//    }
//    
//    private void warmup() throws Exception {
//        warmingUp = true;
//        runAllTests(1, 5);
//        warmingUp = false;
//    }
//    
//    private void runAllTests(int numEvents, int numObjs) throws Exception {
//        rdfFile(numEvents, numObjs);
//        ldpObjs(numEvents, numObjs);
//        hashUris(numEvents, numObjs);
//        xmlFile(numEvents, numObjs);
//        xmlFile(numEvents, numObjs);
//        hashUris(numEvents, numObjs);
//        ldpObjs(numEvents, numObjs);
//        rdfFile(numEvents, numObjs);
//    }
//    
////    @Test
//    public void objs1Events10() throws Exception {
//        
//        int numObjs = 1;
//        int numEvents = 10;
//        
//        runAllTests(numEvents, numObjs);
//    }
//    
////    @Test
//    public void objs10Events1() throws Exception {
//        
//        int numObjs = 10;
//        int numEvents = 1;
//        
//        runAllTests(numEvents, numObjs);
//    }
//    
////    @Test
//    public void objs10Events10() throws Exception {
//        
//        int numObjs = 10;
//        int numEvents = 10;
//        
//        runAllTests(numEvents, numObjs);
//    }
//    
////    @Test
//    public void objs1Events100() throws Exception {
//        
//        int numObjs = 1;
//        int numEvents = 100;
//        
//        runAllTests(numEvents, numObjs);
//    }
//    
////    @Test
//    public void objs100Events1() throws Exception {
//        
//        int numObjs = 100;
//        int numEvents = 1;
//        
//        runAllTests(numEvents, numObjs);
//    }
//    
////    @Test
//    public void objs100Events10() throws Exception {
//        
//        int numObjs = 100;
//        int numEvents = 10;
//        
//        runAllTests(numEvents, numObjs);
//    }
//    
////    @Test
//    public void objs1000Events10() throws Exception {
//        
//        int numObjs = 1000;
//        int numEvents = 10;
//        
//        runAllTests(numEvents, numObjs);
//    }
//    
////    @Test
//    public void objs100Events100() throws Exception {
//        
//        int numObjs = 100;
//        int numEvents = 100;
//        
//        runAllTests(numEvents, numObjs);
//    }
//    
//    @Test
//    public void objs10000Events10() throws Exception {
//        
//        int numObjs = 10000;
//        int numEvents = 10;
//        
////        startServer();
////        rdfFile(numEvents, numObjs);
////        stopServer();
////        startServer();
////        ldpObjs(numEvents, numObjs);
////        stopServer();
////        startServer();
////        hashUris(numEvents, numObjs);
////        stopServer();
////        startServer();
////        xmlFile(numEvents, numObjs);
////        stopServer();
////        startServer();
//        xmlFile(numEvents, numObjs);
//        stopServer();
//        startServer();
//        hashUris(numEvents, numObjs);
//        stopServer();
//        startServer();
//        ldpObjs(numEvents, numObjs);
//        stopServer();
//        startServer();
//        rdfFile(numEvents, numObjs);
////        stopServer();
//    }
//    
////    @Test
//    public void objs100000Events10() throws Exception {
//        
//        int numObjs = 100000;
//        int numEvents = 10;
//        
//        rdfFile(numEvents, numObjs);
//    }
//    
//    public void rdfFile(int numEvents, int numObjs) throws IOException, FcrepoOperationFailedException {
//        String runContainer = "o"  + numObjs + "e" + numEvents + "_rdfFile";
//        createRunContainer(runContainer);
//        
//        long start = System.currentTimeMillis();
//        
//        for (int objCnt = 0; objCnt < numObjs; objCnt++) {
//            //System.out.println("Obj " + objCnt);
//            createBaseObject(runContainer);
//            
//            // Create the log object
//            URI logUri = null;
//            try (FcrepoResponse resp = client.post(objUri)
//                    .body(new ByteArrayInputStream("".getBytes()), "text/plain")
//                    .perform()) {
//                logUri = resp.getLocation();
//            }
//            
//            for (int i = 0; i < numEvents; i++) {
//                // Retrieve log object
//                String logBody = retrieveRDFLog(logUri);
//                
//                // create new entry
//                String eventId = UUID.randomUUID().toString();
//                Resource premisObjResc = createRDFEvent(EVENT_URI_PREFIX + eventId);
//
//                // Add log entry to file
//                try (ByteArrayOutputStream outStream = new ByteArrayOutputStream()) {
//                    RDFDataMgr.write(outStream, premisObjResc.getModel(), RDFFormat.TURTLE_PRETTY);
//                    String eventString = outStream.toString("UTF-8");
//                    
//                    logBody += eventString;
//                }
//                
//                // Stream log object back to fedora
//                try (FcrepoResponse resp = client.put(logUri)
//                        .body(new ByteArrayInputStream(logBody.getBytes()), "text/plain")
//                        .perform()) {
//                }
//            }
//            
//            // System.out.println(objUri);
//        }
//        
//        printResults("rdfFile", numEvents, numObjs, start);
//    }
//    
//    private Resource createRDFEvent(String eventUri) {
//        Model model = ModelFactory.createDefaultModel();
//        Resource premisObjResc = model.createResource(eventUri);
//
//        premisObjResc.addProperty(Premis.hasEventType, "http://id.loc.gov/vocabulary/preservationEvents/virusCheck");
//        premisObjResc.addProperty(Premis.hasEventDateTime, df.format(new Date()));
//        premisObjResc.addProperty(Premis.hasEventOutcomeDetail, "File passed preriodic scan for viruses");
//        
//        Resource eventOutcomeInfo = model.createResource("#outcome");
//        eventOutcomeInfo.addProperty(Premis.hasEventOutcome, "Success");
//        premisObjResc.addProperty(Premis.hasEventOutcomeInformation, eventOutcomeInfo);
//        
//        premisObjResc.addProperty(executingAgentRole, clamAgent);
//        premisObjResc.addProperty(relatedObjectSource, "http://cdr.lib.unc.edu/objects/uuid:d1b44f02-2d11-4589-b81d-3575d85353e1/DATA_FILE");
//        
//        return premisObjResc;
//    }
//    
//    private String retrieveRDFLog(URI logUri) throws IOException, FcrepoOperationFailedException {
//        try (FcrepoResponse resp = client.get(logUri)
//                .perform()) {
//            return IOUtils.toString(resp.getBody());
//        }
//    }
//    
//    public void ldpObjs(int numEvents, int numObjs) throws Exception {
//        String runContainer = "o"  + numObjs + "e" + numEvents + "_ldpObjects";
//        createRunContainer(runContainer);
//        
//        long start = System.currentTimeMillis();
//        
//        for (int objCnt = 0; objCnt < numObjs; objCnt++) {
//            createBaseObject(runContainer);
//            
//            // Create log direct container
//            URI logUri = null;
//            Model model = ModelFactory.createDefaultModel();
//            Resource containerResc = model.createResource("");
//            containerResc.addProperty(createProperty("http://www.w3.org/1999/02/22-rdf-syntax-ns#type"),
//                    createResource("http://www.w3.org/ns/ldp#DirectContainer"));
//            containerResc.addProperty(createProperty("http://www.w3.org/ns/ldp#membershipResource"),
//                    createResource("."));
//            containerResc.addProperty(createProperty("http://www.w3.org/ns/ldp#hasMemberRelation"),
//                    Premis.hasEvent);
//            
//            try (ByteArrayOutputStream outStream = new ByteArrayOutputStream()) {
//                RDFDataMgr.write(outStream, containerResc.getModel(), RDFFormat.TURTLE_PRETTY);
//                // System.out.println(outStream.toString("UTF-8"));
//                InputStream inStream = new ByteArrayInputStream(outStream.toByteArray());
//                
//                try (FcrepoResponse resp = client.post(objUri)
//                        .slug("event")
//                        .body(inStream, "application/x-turtle")
//                        .perform()) {
//                    logUri = resp.getLocation();
//                }
//            }
//            
//            for (int i = 0; i < numEvents; i++) {
//                // Get event rdf resource
//                Resource premisObjResc = createRDFEvent("");
//                // Turn event into string and inputstream
//                try (ByteArrayOutputStream outStream = new ByteArrayOutputStream()) {
//                    RDFDataMgr.write(outStream, premisObjResc.getModel(), RDFFormat.TURTLE_PRETTY);
//                    InputStream inStream = new ByteArrayInputStream(outStream.toByteArray());
//                    
//                    // Create event object
//                    try (FcrepoResponse resp = client.post(logUri)
//                            .slug("event" + i)
//                            .body(inStream, "application/x-turtle")
//                            .perform()) {
//                    }
//                }
//            }
//            
////            System.out.println(objUri);
//        }
//        
//        printResults("ldpObjs", numEvents, numObjs, start);
//    }
//    
//    public void hashUris(int numEvents, int numObjs) throws Exception {
//        String runContainer = "o"  + numObjs + "e" + numEvents + "_hashUris";
//        createRunContainer(runContainer);
//        
//        long start = System.currentTimeMillis();
//        
//        for (int objCnt = 0; objCnt < numObjs; objCnt++) {
//            createBaseObject(runContainer);
//            
//            // Create 
//            URI logUri = null;
//            try (FcrepoResponse resp = client.post(objUri)
//                    .slug("event")
//                    .perform()) {
//                logUri = resp.getLocation();
//            }
//            
//            
//            String eventBaseUri = objUri.toString() + "/event#";
//            for (int i = 0; i < numEvents; i++) {
//                // Get event rdf resource
//                String eventInsert = createSparqlInsertEvent(eventBaseUri + "e" + i);
//                // Turn event into string and inputstream
//                InputStream inStream = new ByteArrayInputStream(eventInsert.getBytes());
//                
//                // Create event object
//                try (FcrepoResponse resp = client.patch(logUri)
//                        .body(inStream)
//                        .perform()) {
//                    
//                }
//            }
//            
//            //System.out.println(objUri);
//        }
//        
//        printResults("hashUris", numEvents, numObjs, start);
//    }
//    
//    private String createSparqlInsertEvent(String eventUri) {
//        Model model1 = ModelFactory.createDefaultModel();
//        Resource premisObjResc = model1.createResource(eventUri);
//
//        premisObjResc.addProperty(Premis.hasEventType, "http://id.loc.gov/vocabulary/preservationEvents/virusCheck");
//        premisObjResc.addProperty(Premis.hasEventDateTime, df.format(new Date()));
//        premisObjResc.addProperty(Premis.hasEventOutcomeDetail, "File passed preriodic scan for viruses");
//        
//        Model model2 = ModelFactory.createDefaultModel();
//        Resource eventOutcomeInfo = model2.createResource(eventUri + "Outcome");
//        eventOutcomeInfo.addProperty(Premis.hasEventOutcome, "Success");
//        premisObjResc.addProperty(Premis.hasEventOutcomeInformation, eventOutcomeInfo);
//        
//        premisObjResc.addProperty(executingAgentRole, clamAgent);
//        premisObjResc.addProperty(relatedObjectSource, "http://cdr.lib.unc.edu/objects/uuid:d1b44f02-2d11-4589-b81d-3575d85353e1/DATA_FILE");
//        
//        return RDFModelUtil.createSparqlInsert(model1) + ";\n" + RDFModelUtil.createSparqlInsert(model2);
//    }
//    
//    public void xmlFile(int numEvents, int numObjs) throws Exception {
//        String runContainer = "o"  + numObjs + "e" + numEvents + "_xmlFile";
//        createRunContainer(runContainer);
//        
//        long start = System.currentTimeMillis();
//        
//        for (int objCnt = 0; objCnt < numObjs; objCnt++) {
//            createBaseObject(runContainer);
//            
//            Document doc = new Document();
//            
//            Element premisEl = new Element("premis", NS);
//            doc.addContent(premisEl);
//            
//            // Create the log file
//            URI logUri = null;
//            try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
//                out.output(doc, baos);
//                
//                try (FcrepoResponse resp = client.post(objUri)
//                        .body(new ByteArrayInputStream(baos.toByteArray()), "text/xml")
//                        .perform()) {
//                    logUri = resp.getLocation();
//                }
//            }
//            
//            for (int i = 0; i < numEvents; i++) {
//                Document logDoc = retrieveXMLLog(logUri);
//                
//                addXMLEvent(logDoc.getRootElement());
//                
//                persistXMLLog(logUri, logDoc);
//            }
//            
//            // System.out.println(objUri);
//        }
//        
//        printResults("xmlFiles", numEvents, numObjs, start);
//    }
//    
//    private Document retrieveXMLLog(URI logUri) throws IOException, FcrepoOperationFailedException, JDOMException {
//        try (FcrepoResponse resp = client.get(logUri)
//                .perform()) {
//            SAXBuilder builder = new SAXBuilder();
//            return builder.build(resp.getBody());
//        }
//    }
//    
//    private void addXMLEvent(Element premisEl) {
//        Element event = new Element("event", NS);
//        premisEl.addContent(event);
//
//        // add event identifier UUID
//        String uuid = String.format("urn:uuid:%1$s", java.util.UUID.randomUUID());
//        event.addContent(new Element("eventIdentifier", NS).addContent(
//                new Element("eventIdentifierType", NS).setText("URN")).addContent(
//                new Element("eventIdentifierValue", NS).setText(uuid)));
//        
//        event.addContent(new Element("eventType", NS)
//                .setText("http://id.loc.gov/vocabulary/preservationEvents/virusCheck"));
//        
//        event.addContent(new Element("eventDateTime", NS).setText(df.format(new Date())));
//        
//        event.addContent(new Element("eventDetailInformation", NS).addContent(
//                new Element("eventDetail", NS).setText("File passed periodic scan for viruses.")));
//        
//        event.addContent(new Element("eventOutcomeInformation", NS).addContent(
//                new Element("eventOutcome", NS).setText("Success")));
//        
//        Element linkingAgent = new Element("linkingAgentIdentifier", NS);
//        event.addContent(linkingAgent);
//        linkingAgent.addContent(new Element("linkingAgentIdentifierType", NS).setText("software program"));
//        linkingAgent.addContent(new Element("linkingAgentValue", NS).setText("ClamAV"));
//        linkingAgent.addContent(new Element("linkingAgentRole", NS).setText("executing program"));
//        
//        Element linkingObject = new Element("linkingObjectIdentifier", NS);
//        event.addContent(linkingObject);
//        linkingObject.addContent(new Element("linkingObjectIdentifierType", NS).setText("PID"));
//        linkingObject.addContent(new Element("linkingObjectIdentifierValue", NS)
//                .setText("uuid:f6cb7370-ad6c-4e0f-9f23-c9bb129c122b/DATA_FILE"));
//        linkingObject.addContent(new Element("linkingObjectIdentifierRole", NS).setText("Source Data"));
//        
//    }
//    
//    private void persistXMLLog(URI logUri, Document doc) throws FcrepoOperationFailedException, IOException {
//        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
//            out.output(doc, baos);
//            
//            try (FcrepoResponse resp = client.put(logUri)
//                .body(new ByteArrayInputStream(baos.toByteArray()), "text/xml")
//                .perform()) {
//            }
//        }
//    }
//    
//    private void printResults(String type, int numEvents, int numObjs, long start) {
//        if (warmingUp) {
//            return;
//        }
//        
//        long total = System.currentTimeMillis() - start;
//        
//        long perEvent = total / (numEvents * numObjs);
//        System.out.println(type + "(events=" + numEvents + ", objs=" + numObjs + ") = " + total + "ms (" + perEvent + "ms/objEvent)");
//    }
}
