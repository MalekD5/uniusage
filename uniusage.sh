#!/bin/bash

JAR_PATH="app/build/libs/UniUsage.jar"

if [ ! -f "$JAR_PATH" ]; then
  echo "UniUsage.jar not found. Building the project..."
  ./gradlew build
  if [ $? -ne 0 ]; then
    echo "Build failed. Exiting."
    exit 1
  fi
fi

java -jar "$JAR_PATH" "$@"
