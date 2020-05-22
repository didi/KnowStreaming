#!/usr/bin/env bash

cd `dirname $0`/../lib
lib_dir=`pwd`

pid=`ps ax | grep -i 'kafka-manager-web' | grep ${lib_dir} | grep java | grep -v grep | awk '{print $1}'`
if [ -z "$pid" ] ; then
        echo "No kafka-manager-web running."
        exit -1;
fi

echo "The kafka-manager-web(${pid}) is running..."

kill ${pid}

echo "Send shutdown request to kafka-manager-web(${pid}) OK"