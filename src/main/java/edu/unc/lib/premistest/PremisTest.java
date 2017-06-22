package edu.unc.lib.premistest;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PremisTest {
    private static final Logger log = LoggerFactory.getLogger(PremisTest.class);
    
    public static void main(String[] args) throws ParseException {
        Options options = populateOptions();
        
        CommandLineParser parser = new DefaultParser();
        CommandLine cmd = parser.parse(options, args);
        
        TestConfig config = extractTestConfig(cmd);
        
        testList(cmd, config).forEach(generator -> {
            try {
                generator.populateObjects();
            } catch (Exception e) {
                log.error("Failed to perform test {}", generator.getTestName(), e);
            }
        });
    }
    
    private static Options populateOptions() {
        Options options = new Options();
        
        options.addOption("e", true, "Number of events per object.  Default 1.");
        options.addOption("n", true, "Number of objects per test.  Default 10.");
        options.addOption("u", true, "URL of the Fedora instance to use for testing.  Default http://localhost:8080/rest/");
        options.addOption("r", false, "Randomize order of tests.  Default false.");
        options.addOption("R", false, "Run RDF Log File test");
        options.addOption("O", false, "Run Events as Objects test");
        options.addOption("X", false, "Run XML Log File test");
        options.addOption("H", false, "Run Events as Hash URIs test");
        options.addOption("N", false, "Number of times to repeat tests.  Default 1.");
        options.addOption("s", true, "Silent mode, do not log any results");
        
        return options;
    }
    
    private static TestConfig extractTestConfig(CommandLine cmd) {
        TestConfig config = new TestConfig();
        
        if (cmd.hasOption('u')) {
            config.fedoraUrl = cmd.getOptionValue('u');
        }
        
        if (cmd.hasOption('e')) {
            config.numEvents = Integer.parseInt(cmd.getOptionValue('e'));
        }
        
        if (cmd.hasOption('n')) {
            config.numObjects = Integer.parseInt(cmd.getOptionValue('n'));
        }
        
        config.silentMode = cmd.hasOption('s');
        
        return config;
    }
    
    private static List<AbstractPremisPersistenceGenerator> testList(CommandLine cmd, TestConfig config) {
        final int repetitions;
        if (cmd.hasOption('N')) {
            repetitions = Integer.parseInt(cmd.getOptionValue('N'));
        } else {
            repetitions = 1;
        }
        
        List<AbstractPremisPersistenceGenerator> testList = new ArrayList<>();
        for (int i = 0; i < repetitions; i++) {
            if (cmd.hasOption('R')) {
                testList.add(new RdfLogGenerator(config));
            }
            if (cmd.hasOption('O')) {
                testList.add(new EventObjectsGenerator(config));
            }
            if (cmd.hasOption('X')) {
                testList.add(new XmlLogGenerator(config));
            }
            if (cmd.hasOption('H')) {
                testList.add(new HashUriEventsGenerator(config));
            }
        }
        
        final boolean randomOrder = cmd.hasOption('r');
        if (randomOrder) {
            Collections.shuffle(testList);
        }
        
        return testList;
    }

    public static class TestConfig {
        public int numObjects = 10;
        public int numEvents = 1;
        public String fedoraUrl = "http://localhost:8080/rest/";
        public boolean silentMode = false;
    }
}
