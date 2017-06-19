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
package edu.unc.lib.premistest.premistest;

import java.io.File;
import java.net.URI;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.fcrepo.client.FcrepoClient;
import org.fcrepo.client.FcrepoOperationFailedException;
import org.fcrepo.client.FcrepoResponse;

public class FcrepoJettyWrapper {
    
    private final static String MAX_HEAP = "3072m";
    private final static int NUM_START_CHECKS = 40;
    private final static long START_CHECK_DELAY = 500l;
    
    private String fcrepoBase;
    private String fcrepoHome;
    private String fcrepoJettyPath;
    
    private Process jettyProc;
    
    private FcrepoClient client;

    public FcrepoJettyWrapper(String fcrepoBase, String fcrepoHome, String fcrepoJetty) {
        this.fcrepoBase = fcrepoBase;
        this.fcrepoHome = fcrepoHome;
        this.fcrepoJettyPath = fcrepoJetty;
        
        client = FcrepoClient.client()
                .throwExceptionOnFailure()
                .build();
    }

    public void start() throws Exception {
        long startTime = System.currentTimeMillis();
        
        File fcrepoHomeFile = new File(fcrepoHome);
        if (fcrepoHomeFile.exists()) {
            FileUtils.forceDelete(fcrepoHomeFile);
        }
        fcrepoHomeFile.mkdir();
        
        int retries = NUM_START_CHECKS;
        
        System.out.println("java -Xms" + MAX_HEAP + " -Xmx" + MAX_HEAP + " -Dfcrepo.home=" + fcrepoHome
                + " -jar " + fcrepoJettyPath + " --headless");
        jettyProc = Runtime.getRuntime().exec("java -Xms" + MAX_HEAP + " -Xmx" + MAX_HEAP + " -Dfcrepo.home=" + fcrepoHome
                + " -jar " + fcrepoJettyPath + " --headless");
        
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
                            "Gave up waiting for jetty to startup after %sms:\n %s",
                            (NUM_START_CHECKS * START_CHECK_DELAY),
                            IOUtils.toString(jettyProc.getErrorStream()));
                    throw new RuntimeException(message, e);
                }
            }
            
        } while (true);
        System.out.println("Jetty started up in " + (System.currentTimeMillis() - startTime) + "ms");
    }

    public void stop() {
        jettyProc.destroy();
    }
}
