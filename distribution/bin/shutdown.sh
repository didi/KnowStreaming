#!/bin/bash

cd `dirname $0`/../target
target_dir=`pwd`

pid=`ps ax | grep -i 'logikm' | grep ${target_dir} | grep java | grep -v grep | awk '{print $1}'`
if [ -z "$pid" ] ; then
        echo "No logikm running."
        exit -1;
fi

echo "The logikm (${pid}) is running..."

kill ${pid}

echo "Send shutdown request to logikm (${pid}) OK"
