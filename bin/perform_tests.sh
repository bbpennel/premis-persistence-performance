#!/bin/bash

source bin/common.sh

server_check

# start_fcrepo
# java -jar target/premistest-0.0.1.jar -u http://localhost:8080/rest -H -n 10 -e 10
# stop_fcrepo
#
# start_fcrepo
# java -jar target/premistest-0.0.1.jar -u http://localhost:8080/rest -A -n 10 -e 10 -r -N 2
# stop_fcrepo
#
# start_fcrepo
# java -jar target/premistest-0.0.1.jar -u http://localhost:8080/rest -A -n 10 -e 100 -r -N 2
# stop_fcrepo
#
# start_fcrepo
# java -jar target/premistest-0.0.1.jar -u http://localhost:8080/rest -A -n 10 -e 200 -r -N 2
# stop_fcrepo
#
# start_fcrepo
# java -jar target/premistest-0.0.1.jar -u http://localhost:8080/rest -R -n 2 -e 500
# stop_fcrepo
# start_fcrepo
# java -jar target/premistest-0.0.1.jar -u http://localhost:8080/rest -O -n 2 -e 500
# stop_fcrepo
# start_fcrepo
# java -jar target/premistest-0.0.1.jar -u http://localhost:8080/rest -X -n 2 -e 500
# stop_fcrepo
# start_fcrepo
# java -jar target/premistest-0.0.1.jar -u http://localhost:8080/rest -H -n 2 -e 500
# stop_fcrepo
# #
# start_fcrepo
# java -jar target/premistest-0.0.1.jar -u http://localhost:8080/rest -R -n 2 -e 1000
# stop_fcrepo
# start_fcrepo
# java -jar target/premistest-0.0.1.jar -u http://localhost:8080/rest -O -n 2 -e 1000
# stop_fcrepo
# start_fcrepo
# java -jar target/premistest-0.0.1.jar -u http://localhost:8080/rest -X -n 2 -e 1000
# stop_fcrepo
# start_fcrepo
# java -jar target/premistest-0.0.1.jar -u http://localhost:8080/rest -H -n 2 -e 1000
# stop_fcrepo

start_fcrepo
java -jar target/premistest-0.0.1.jar -u http://localhost:8080/rest -R -n 2 -e 2000
stop_fcrepo
start_fcrepo
java -jar target/premistest-0.0.1.jar -u http://localhost:8080/rest -O -n 2 -e 2000
stop_fcrepo
start_fcrepo
java -jar target/premistest-0.0.1.jar -u http://localhost:8080/rest -X -n 2 -e 2000
stop_fcrepo
start_fcrepo
java -jar target/premistest-0.0.1.jar -u http://localhost:8080/rest -H -n 2 -e 2000
stop_fcrepo

#
# start_fcrepo
# java -jar target/premistest-0.0.1.jar -u http://localhost:8080/rest -R -n 100 -e 10
# stop_fcrepo
# start_fcrepo
# java -jar target/premistest-0.0.1.jar -u http://localhost:8080/rest -O -n 100 -e 10
# stop_fcrepo
# start_fcrepo
# java -jar target/premistest-0.0.1.jar -u http://localhost:8080/rest -X -n 100 -e 10
# stop_fcrepo
# start_fcrepo
# java -jar target/premistest-0.0.1.jar -u http://localhost:8080/rest -H -n 100 -e 10
# stop_fcrepo
#
# start_fcrepo
# java -jar target/premistest-0.0.1.jar -u http://localhost:8080/rest -R -n 500 -e 10
# stop_fcrepo
# start_fcrepo
# java -jar target/premistest-0.0.1.jar -u http://localhost:8080/rest -O -n 500 -e 10
# stop_fcrepo
# start_fcrepo
# java -jar target/premistest-0.0.1.jar -u http://localhost:8080/rest -X -n 500 -e 10
# stop_fcrepo
# start_fcrepo
# java -jar target/premistest-0.0.1.jar -u http://localhost:8080/rest -H -n 500 -e 10
# stop_fcrepo
#
# start_fcrepo
# java -jar target/premistest-0.0.1.jar -u http://localhost:8080/rest -R -n 1000 -e 10
# stop_fcrepo
# start_fcrepo
# java -jar target/premistest-0.0.1.jar -u http://localhost:8080/rest -O -n 1000 -e 10
# stop_fcrepo
# start_fcrepo
# java -jar target/premistest-0.0.1.jar -u http://localhost:8080/rest -X -n 1000 -e 10
# stop_fcrepo
# start_fcrepo
# java -jar target/premistest-0.0.1.jar -u http://localhost:8080/rest -H -n 1000 -e 10
# stop_fcrepo
