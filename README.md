Creation of objects and events are implemented using Java based clients to accurately represent performance of serialization and clients within a java based repository application.

Tests are coordinated with shell scripts for ease of use and to keep the fcrepo JVM separate from the test execution.

Test scripts are located in the `bin` directory.  To perform, run:
`bin/perform\_tests.sh`
`bin/perform\_test_50k.sh`
`bin/perform\_tests_num_events.sh`
or create a new script based off of those.

Methodology:
* Jetty restarted between most tests to avoid performance degradation from <link to ticket>
* Data within fedora deleted during restart unless specified otherwise
* 


Regex for converting result format to CSV

([^ ]+) \(events=(\d+), objs=(\d+)\) = (\d+)ms \((\d+).*
$1,$2,$3,$4,$5