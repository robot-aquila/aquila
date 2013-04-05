#!/bin/sh

CONTRIB="contrib"
BUILD="build"

CP=""
CP="${CP}:${CONTRIB}/mysql-connector-java-5.1.18-bin.jar"
CP="${CP}:${BUILD}/aquila-ta-combat.jar"

java -classpath "$CP" ru.prolib.aquila.ChaosTheory.RunFinamCsv2Db $@
