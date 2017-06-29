#!/bin/bash

source bin/common.sh

NUM_EVENTS=10
NUM_OBJS=10000
NUM_RUNS=2

cleanup_data
for i in {1..$NUM_RUNS}; do
    perform_test "-R -n $NUM_OBJS -e $NUM_EVENTS" $RETAIN_VALUE
done

cleanup_data
for i in {1..$NUM_RUNS}; do
    perform_test "-H -n $NUM_OBJS -e $NUM_EVENTS" $RETAIN_VALUE
done

cleanup_data
for i in {1..$NUM_RUNS}; do
    perform_test "-O -n $NUM_OBJS -e $NUM_EVENTS" $RETAIN_VALUE
done

cleanup_data
for i in {1..$NUM_RUNS}; do
    perform_test "-X -n $NUM_OBJS -e $NUM_EVENTS" $RETAIN_VALUE
done