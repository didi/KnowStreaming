#!/bin/bash

cd `dirname $0`/../libs
target_dir=`pwd`

pid=`ps ax | grep -i 'ks-km' | grep ${target_dir} | grep java | grep -v grep | awk '{print $1}'`
if [ -z "$pid" ] ; then
        echo "No ks-km running."
        exit -1;
fi

echo "The ks-km (${pid}) is running..."

kill ${pid}

echo "Send shutdown request to ks-km (${pid}) OK"
