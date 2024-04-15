#!/bin/bash

cd `dirname $0`/../lib
target_dir=`pwd`

pid=`ps ax | grep -i 'robot-server' | grep ${target_dir} | grep java | grep -v grep | awk '{print $1}'`
if [ -z "$pid" ] ; then
        echo "No robot server running."
        exit -1;
fi

echo "The robot server(${pid}) is running..."

kill ${pid}

echo "Send shutdown request to robot server(${pid}) OK"
