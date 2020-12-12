#!/usr/bin/env bash
set MODULE_PATH="./build/libs/javafx-base-14.jar:./build/libs/javafx-base-14-linux.jar:./build/libs/javafx-graphics-14-linux.jar"
$JAVA_HOME/bin/java --module-path=$MODULE_PATH --add-modules=javafx.graphics -cp "${MODULE_PATH}:./libs/vibrato-1.0-SNAPSHOT.jar:./build/classes/java/test" vibrato.examples.$@