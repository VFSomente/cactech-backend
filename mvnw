#!/bin/sh
# Licensed to the Apache Software Foundation (ASF) under one
# or more contributor license agreements.  See the NOTICE file
# distributed with this work for additional information
# regarding copyright ownership.  The ASF licenses this file
# to you under the Apache License, Version 2.0 (the
# "License"); you may not use this file except in compliance
# with the License.  You may obtain a copy of the License at
#
#   http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing,
# software distributed under the License is distributed on an
# "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
# KIND, either express or implied.  See the License for the
# specific language governing permissions and limitations
# under the License.

set -e

# Find the project base dir, i.e., the directory that contains the folder
# ".mvn".
find_maven_basedir() {
    if [ -z "$1" ]; then
        echo "Path not specified to find_maven_basedir"
        return 1
    fi
    basedir="$1"
    wdir="$1"
    while true; do
        if [ -d "$wdir"/.mvn ]; then
            basedir=$wdir
            break
        fi
        if [ "$wdir" = "/" ]; then
            break
        fi
        wdir=$(cd "$wdir/.." && pwd)
    done
    echo "${basedir}"
}

BASE_DIR=$(find_maven_basedir "$(pwd)")
MAVEN_PROJECTBASEDIR=${MAVEN_BASEDIR:-"$BASE_DIR"}

MAVEN_OPTS="${MAVEN_OPTS:--Xms256m -Xmx512m}"

# Provide a "standardized" way to retrieve the CLI args that will
# work with both Windows and non-Windows executions.
MAVEN_CMD_LINE_ARGS="$MAVEN_CONFIG $@"
export MAVEN_CMD_LINE_ARGS

WRAPPER_LAUNCHER=org.apache.maven.wrapper.MavenWrapperMain

exec "$JAVACMD" \
  $MAVEN_OPTS \
  $MAVEN_DEBUG_OPTS \
  -classpath "$MAVEN_PROJECTBASEDIR/.mvn/wrapper/maven-wrapper.jar" \
  "-Dmaven.multiModuleProjectDirectory=${MAVEN_PROJECTBASEDIR}" \
  ${WRAPPER_LAUNCHER} $MAVEN_CONFIG "$@"
