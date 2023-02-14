#!/bin/bash

SERVICE_PATH="/home/xiaoju/${APPNAME}"

/usr/bin/monit stop all

su xiaoju -c "cd $SERVICE_PATH && ./control.sh stop"

