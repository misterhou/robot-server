#!/bin/bash

export BASE_DIR=`cd $(dirname $0)/..; pwd`

error_exit()
{
	echo "ERROR: $1 !!"
	exit 1

}


if [ -z "$JAVA_HOME" ]; then
	error_exit "Please set the JAVA_HOME variable in your environment, We need java(x64)! jdk8 or later is better!"
fi

export JAVA=$JAVA_HOME/bin/java



export SERVER_NAME=robot-server-1.0
export CUSTOM_CONFIG_LOCATIONS=$BASE_DIR/config/
export SERVER_LOG_OPTS=--logging.config=$BASE_DIR/config/logback.xml
export JVM_OPTS="-server -Xms512m -Xmx512m -Xmn256m"
export SERVER_OPTS="-jar ${BASE_DIR}/lib/${SERVER_NAME}.jar"
export SERVER_CONFIG_OPTS="--spring.config.additional-location=$CUSTOM_CONFIG_LOCATIONS"
export COMMAND="$JAVA $JVM_OPTS $SERVER_OPTS $SERVER_LOG_OPTS $SERVER_CONFIG_OPTS"

# start robot server command
echo "$COMMAND" > ${BASE_DIR}/logs/start.out 2>&1 &
nohup $COMMAND $SERVER_NAME >> ${BASE_DIR}/logs/start.out 2>&1 &
echo "robot server is starting，you can check the ${BASE_DIR}/logs/start.out"

