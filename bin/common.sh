#!/bin/bash

BASE_DIR=$(dirname "$0")
PROJ_DIR=$(dirname "$BASE_DIR")
FCREPO_JETTY_LOC=$PROJ_DIR/downloads/fcrepo-webapp-4.7.3-jetty-console.jar
FCREPO_HOME=$PROJ_DIR/downloads/fcrepo_data
FCREPO_URL=http://localhost:8080/rest
PID_FILE=$PROJ_DIR/target/fcrepo.pid
MAX_WAIT=20
RETAIN_VALUE=true

function cleanup_data {
    printf 'Cleaning up fcrepo data'
    rm -rf $FCREPO_HOME/*
}

function start_fcrepo {
    
    if [[ "$1" != true ]]; then
        cleanup_data
    fi
    
    nohup java -jar -Xmx2g -Dfcrepo.home=$FCREPO_HOME -Dlogback.configurationFile=$PROJ_DIR/src/main/resources/logback.xml \
        $FCREPO_JETTY_LOC --headless > /dev/null 2>&1 & echo $! > "$PID_FILE"
    
    num_tries=0
    until $(curl --output /dev/null --silent --head --fail $FCREPO_URL); do
        if [[ "$num_tries" -gt $MAX_WAIT ]]; then
            printf 'Took too long to start fcrepo, trying again'
            break
        fi
        printf '.'
        sleep 3
        num_tries=$((num_tries + 1))
    done
    printf '\n'
    if [[ "$num_tries" -gt $MAX_WAIT ]]; then
        stop_fcrepo
        start_fcrepo $1
    else
        # warmup
        perform_test "-A -n 50 -e 2 -s" "true" "true"
    fi
}

function stop_fcrepo {
    kill $(<"$PID_FILE") > /dev/null
    rm $PID_FILE
    sleep 5
}

function server_check {
    if [ -f $PID_FILE ]; then
        echo "Fcrepo already running, stopping."
        stop_fcrepo
    fi
}

function perform_test {
    local arguments=$1
    local retain_data=$2
    local skip_restart=$3
    
    if [[ "$skip_restart" != true ]]; then
        start_fcrepo $retain_data
    fi
    # echo "java -jar target/premistest-0.0.1.jar -u $FCREPO_URL $arguments"
    java -jar target/premistest-0.0.1.jar -u $FCREPO_URL $arguments
    if [[ "$skip_restart" != true ]]; then
        stop_fcrepo
    fi
}

function perform_all_tests {
    local arguments=$2
    
    for i in $1; do
        perform_test "-$i $arguments"
    done
}