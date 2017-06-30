Creation of objects and events are implemented using Java based clients to accurately represent performance of serialization and clients within a java based repository application.

Tests are coordinated with shell scripts for ease of use and to keep the fcrepo JVM separate from the test execution.

*For more information using the java testing utility:*
1) build the project `mvn install`
2) then run `java -jar target/premistest-0.0.1.jar -h`

Setup Batch Tests
=====
1) Download fcrepo 4.7.3 jetty console and place it into the downloads directory: https://github.com/fcrepo4/fcrepo4/releases/download/fcrepo-4.7.3/fcrepo-webapp-4.7.3-jetty-console.jar
2) Run test scripts located in the `bin` directory.  To perform, run:
`bin/perform\_test_50k.sh`
`bin/perform\_tests_num_events.sh`
or create a new script based off of those.

Methodology:
* Jetty restarted between most tests to avoid performance degradation
* Data within fedora deleted during restart unless specified otherwise

*Regex for converting result format to CSV*

([^ ]+) \(events=(\d+), objs=(\d+)\) = (\d+)ms \((\d+).*
$1,$2,$3,$4,$5