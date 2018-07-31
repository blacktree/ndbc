#!/bin/bash
set -e # Any subsequent(*) commands which fail will cause the shell script to exit immediately

mvn install
mvn org.jacoco:jacoco-maven-plugin:prepare-agent -pl $PROJECTS test
