#!/bin/bash

cd `dirname $0`/../target
target_dir=`pwd`

pid=`ps ax | grep -i 'kafka-manager' | grep ${target_dir} | grep java | grep -v grep | awk '{print $1}'`
if [ -z "$pid" ] ; then
        echo "No kafka-manager running."
        exit -1;
fi

echo "The kafka-manager (${pid}) is running..."

kill ${pid}

echo "Send shutdown request to kafka-manager (${pid}) OK"
