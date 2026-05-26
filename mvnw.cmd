@REM Licensed to the Apache Software Foundation (ASF) under one
@REM or more contributor license agreements.  See the NOTICE file
@REM distributed with this work for additional information
@REM regarding copyright ownership.  The ASF licenses this file
@REM to you under the Apache License, Version 2.0 (the
@REM "License"); you may not use this file except in compliance
@REM with the License.  You may obtain a copy of the License at
@REM
@REM   http://www.apache.org/licenses/LICENSE-2.0
@REM
@REM Unless required by applicable law or agreed to in writing,
@REM software distributed under the License is distributed on an
@REM "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
@REM KIND, either express or implied.  See the License for the
@REM specific language governing permissions and limitations
@REM under the License.

@IF "%__MVNW_ARG0_NAME__%"=="" (SET "BASE_DIR=%~dp0")

@SET "MAVEN_PROJECTBASEDIR=%MAVEN_BASEDIR%"
@IF "%MAVEN_PROJECTBASEDIR%"=="" (
    @SET "MAVEN_PROJECTBASEDIR=%BASE_DIR%"
)

@SET "WRAPPER_LAUNCHER=org.apache.maven.wrapper.MavenWrapperMain"
@SET "WRAPPER_JAR=%BASE_DIR%.mvn\wrapper\maven-wrapper.jar"
@SET "WRAPPER_PROPERTIES=%BASE_DIR%.mvn\wrapper\maven-wrapper.properties"

@FOR /F "usebackq tokens=1,2 delims==" %%a IN ("%WRAPPER_PROPERTIES%") DO (
    @IF "%%a"=="JAVA_HOME" (SET "JAVA_HOME=%%b")
)

@IF NOT "%JAVA_HOME%"=="" (
    @SET "JAVA_HOME=%JAVA_HOME:"=%"
)

@SET "JAVACMD=%JAVA_HOME%\bin\java.exe"
@IF NOT EXIST "%JAVACMD%" (SET "JAVACMD=java")

@IF NOT "%MAVEN_OPTS%"=="" GOTO skip_default_opts
@SET "MAVEN_OPTS=-Xms256m -Xmx512m"
:skip_default_opts

@"%JAVACMD%" %MAVEN_OPTS% %MAVEN_DEBUG_OPTS% ^
  -classpath "%WRAPPER_JAR%" ^
  "-Dmaven.multiModuleProjectDirectory=%MAVEN_PROJECTBASEDIR%" ^
  %WRAPPER_LAUNCHER% %MAVEN_CONFIG% %*

@IF ERRORLEVEL 1 GOTO error
@GOTO end

:error
@SET ERROR_CODE=1

:end
@IF "%MAVEN_SKIP_RC%"=="" GOTO skipRcPost
:skipRcPost
@EXIT /B %ERROR_CODE%
