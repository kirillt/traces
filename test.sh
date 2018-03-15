#!/bin/bash

export SOURCE=reduced.csv 

#export DURATION=60
#export DISTANCE=2
#export EPSILON=7

#export ONLY_COUNT=yes

#FULL_GRAPH=yes TASK_FILE=top.uids sbt "run-main FindMeetings"
A=600dfbe2 B=74d917a1 sbt "run-main FindMeetings"
