#!/bin/bash
./mvnw clean test install assembly:single
java -server -XX:+UseG1GC -Xms4g -XX:MaxGCPauseMillis=100 -XX:ParallelGCThreads=4 -XX:+PrintGC -XX:+PrintGCDetails -XX:+PrintGCDateStamps -Xloggc:gc.log \
-jar ./target/digit-processor-jar-with-dependencies.jar


#-server -XX:+UseG1GC -Xms4g -XX:MaxGCPauseMillis=100 -XX:ParallelGCThreads=4 -XX:+PrintGC -XX:+PrintGCDetails -XX:+PrintGCDateStamps -Xloggc:gc.log
#-server -XX:+UseG1GC -Xms4g -XX:MaxGCPauseMillis=100 -XX:+UseParallelGC  -XX:ParallelGCThreads=4 -XX:+PrintGC -XX:+PrintGCDetails -XX:+PrintGCDateStamps -Xloggc:gc.log