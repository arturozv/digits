#!/bin/bash
./mvnw clean test install assembly:single
java -jar ./target/digit-processor-jar-with-dependencies.jar
