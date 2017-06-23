#!/bin/bash

BASE_DIR=$(dirname "$0")
PROJ_DIR=$(dirname "$BASE_DIR")
FCREPO_JETTY_LOC=$PROJ_DIR/downloads/fcrepo-webapp-4.7.3-jetty-console.jar
FCREPO_HOME=$PROJ_DIR/downloads/fcrepo_data
FCREPO_URL=http://localhost:8080/rest

function start_fcrepo {
    java -jar -Xmx2g -Dfcrepo.home=$FCREPO_HOME -Dlogback.configurationFile=$PROJ_DIR/src/main/resources/logback.xml \
        $FCREPO_JETTY_LOC --headless > /dev/null &
    PROC_ID=$!
    
    until $(curl --output /dev/null --silent --head --fail $FCREPO_URL); do
        printf '.'
        sleep 3
    done
}

function stop_fcrepo {
    kill $PROC_ID
}

start_fcrepo

curl -XPOST $FCREPO_URL

java -jar target/premistest-0.0.1-jar-with-dependencies.jar -u http://localhost:8080/rest -R

stop_fcrepo



