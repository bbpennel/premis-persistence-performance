package edu.unc.lib.premistest.premistest;

import java.io.File;
import java.net.URI;

import org.apache.catalina.LifecycleException;
import org.apache.catalina.startup.Tomcat;
import org.apache.commons.io.FileUtils;
import org.fcrepo.client.FcrepoClient;
import org.fcrepo.client.FcrepoOperationFailedException;
import org.fcrepo.client.FcrepoResponse;

public class FcrepoTomcatWrapper {

    private final static int NUM_START_CHECKS = 100;
    private final static long START_CHECK_DELAY = 1000l;
    
    private String fcrepoBase;
    private String fcrepoHome;
    private int port;
    private String fcrepoWarPath;
    
    private Tomcat tomcat;
    
    public FcrepoTomcatWrapper(int port, String fcrepoHome, String fcrepoWarPath) {
        this.port = port;
        this.fcrepoHome = fcrepoHome;
        this.fcrepoWarPath = fcrepoWarPath;
        fcrepoBase = "http://localhost:" + port + "/fcrepo/";
    }

    public void start() throws Exception {
        long startTime = System.currentTimeMillis();
        
        File fcrepoHomeFile = new File(fcrepoHome);
        if (fcrepoHomeFile.exists()) {
            FileUtils.forceDelete(fcrepoHomeFile);
        }
        fcrepoHomeFile.mkdir();
        
        System.setProperty("fcrepo.modeshape.configuration", "classpath:/config/file-simple/repository.json");
        System.setProperty("fcrepo.home", this.fcrepoHome);
        System.setProperty("java.util.logging.config.file", "");
        System.setProperty("logback.configurationFile", "src/main/resources/logback.xml");
        
        tomcat = new Tomcat();
        tomcat.setSilent(true);

        //The port that we should run on can be set into an environment variable
        //Look for that variable and default to 8080 if it isn't there.
        String webPort = System.getenv("PORT");
        if(webPort == null || webPort.isEmpty()) {
            webPort = "8080";
        }

        tomcat.setPort(Integer.valueOf(webPort));
        
        File warFile = new File(fcrepoWarPath);
        // System.out.println(warFile.getAbsolutePath());
        tomcat.addWebapp("/fcrepo", warFile.getAbsolutePath());

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    tomcat.start();
                } catch (LifecycleException e) {
                    e.printStackTrace();
                }
            }
        }).start();
        
        
        FcrepoClient client = FcrepoClient.client()
                .throwExceptionOnFailure()
                .build();
        
        int retries = NUM_START_CHECKS;
        URI baseUri = URI.create(fcrepoBase);
        do {
            Thread.sleep(START_CHECK_DELAY);
            try (FcrepoResponse resp = client.get(baseUri).perform()) {
                if (resp.getStatusCode() == 200) {
                    break;
                }
            } catch (FcrepoOperationFailedException e) {
                if (--retries < 0) {
                    String message = String.format(
                            "Gave up waiting for tomcat to startup after %sms",
                            (NUM_START_CHECKS * START_CHECK_DELAY));
                    throw new RuntimeException(message, e);
                }
            }
            
        } while (true);
    }
    
    public void stop() throws Exception {
        tomcat.destroy();
    }
}
