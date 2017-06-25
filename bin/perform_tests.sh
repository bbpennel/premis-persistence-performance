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
        java -jar target/premistest-0.0.1.jar -u http://localhost:8080/rest -A -n 50 -e 2 -s
    fi
}

function stop_fcrepo {
    kill $(<"$PID_FILE") > /dev/null
    rm $PID_FILE
    sleep 5
}

if [ -f $PID_FILE ]; then
    echo "Fcrepo already running, stopping."
    stop_fcrepo
fi

# start_fcrepo
# java -jar target/premistest-0.0.1.jar -u http://localhost:8080/rest -A -n 10 -e 10 -R -N 3
# stop_fcrepo

# cleanup_data
# for i in {1..10}; do
#     start_fcrepo $RETAIN_VALUE
#     java -jar target/premistest-0.0.1.jar -u http://localhost:8080/rest -R -n 10000 -e 10
#     stop_fcrepo;
# done

cleanup_data
for i in {1..5}; do 
    start_fcrepo $RETAIN_VALUE
    java -jar target/premistest-0.0.1.jar -u http://localhost:8080/rest -O -n 10000 -e 10
    stop_fcrepo;
done

cleanup_data
for i in {1..5}; do 
    start_fcrepo $RETAIN_VALUE
    java -jar target/premistest-0.0.1.jar -u http://localhost:8080/rest -H -n 10000 -e 10
    stop_fcrepo;
done

cleanup_data
for i in {1..5}; do 
    start_fcrepo $RETAIN_VALUE
    java -jar target/premistest-0.0.1.jar -u http://localhost:8080/rest -X -n 10000 -e 10
    stop_fcrepo;
done
#
# start_fcrepo
# java -jar target/premistest-0.0.1.jar -u http://localhost:8080/rest -O -n 10000 -e 10
# stop_fcrepo
#
# start_fcrepo
# java -jar target/premistest-0.0.1.jar -u http://localhost:8080/rest -H -n 10000 -e 10
# stop_fcrepo

# start_fcrepo
# java -jar target/premistest-0.0.1.jar -u http://localhost:8080/rest -X -n 10000 -e 10
# stop_fcrepo

# start_fcrepo
# java -jar target/premistest-0.0.1.jar -u http://localhost:8080/rest -A -n 1 -e 10
# stop_fcrepo
