@echo off

set JAVA=%JAVA_HOME%\bin\java.exe

setlocal enabledelayedexpansion

set BASE_DIR=%~dp0
rem 删除 \bin\ 目录，获取程序主目录.
set BASE_DIR=%BASE_DIR:~0,-5%

set CUSTOM_CONFIG_LOCATIONS=file:%BASE_DIR%/config/

set SERVER_NAME=robot-server-1.0
set SERVER_LOG_OPTS=--logging.config=%BASE_DIR%/config/logback.xml
set JVM_OPTS=-server -Xms512m -Xmx512m -Xmn256m
set SERVER_OPTS=-jar %BASE_DIR%\lib\%SERVER_NAME%.jar

rem set nacos spring config location
set SERVER_CONFIG_OPTS=--spring.config.additional-location=%CUSTOM_CONFIG_LOCATIONS%

set COMMAND="%JAVA%" %JVM_OPTS% %SERVER_OPTS% %SERVER_LOG_OPTS% %SERVER_CONFIG_OPTS% %SERVER_NAME%

rem start robot server command
%COMMAND%
