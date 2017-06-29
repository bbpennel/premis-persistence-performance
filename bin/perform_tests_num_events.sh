#!/bin/bash

source bin/common.sh

server_check
#
# perform_test "-A -n 10 -e 1"
#
# perform_test "-A -n 10 -e 10"

# perform_all_tests "R X O H" "-n 10 -e 100"
#
# perform_all_tests "R X O H" "-n 2 -e 200"
#
# perform_all_tests "R X O H" "-n 2 -e 500"
#
# perform_all_tests "R X O H" "-n 2 -e 1000"
#
# perform_all_tests "R X O" "-n 2 -e 2000"
#
# perform_all_tests "R X O" "-n 2 -e 4000"
#
perform_all_tests "R X O" "-n 10 -e 10000"