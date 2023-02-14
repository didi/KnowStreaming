#!/usr/bin/env bash
###########################################################################
#  nginxfunc.sh
#  实现http_start 、http_stop 方法用于启停nginx 以及 status.do文件rename
##########################################################################

#variables
NGINX_PATH="/home/${USER}/nginx"
STATUS_FILE="${NGINX_PATH}/html/status.do"
NGXPID_FILE="${NGINX_PATH}/run/nginx.pid"
NGXBIN_STOP="${NGINX_PATH}/load.sh stop"
NGXBIN_START="${NGINX_PATH}/load.sh start"
NGINX_URL="http://127.0.0.1:8080/status.do"
LOCAL_URL="http://127.0.0.1:8888/health.do"
TOMCAT_FILE="/home/${USER}/tomcat/conf/server.xml"


function rm_status_do() {
    if [ -f ${STATUS_FILE} ];then
        rm -rf ${STATUS_FILE}
        if [ $? == 0 ];then
            return 0;
        else
            exit 1;
        fi
    fi
}

function http_stop() {
    local timeout=20
    if [ ! -f ${NGXPID_FILE} ];then
        echo "Nginx is stop!"
    else
        rm -rf ${STATUS_FILE}
        echo "Sleep [ 20 ] sec. Wait Inrouter Remove RS!"
        for((i=1;i<=$timeout;i++));do
            sleep 1;
            echo "Sleep [ $i ] "
        done

        $NGXBIN_STOP
        if [ $? != 0 ]; then
            echo "Nginx stop failed!!!"
            exit 1
        else
            echo "Nginx is stop!"
        fi
    fi
}

function http_start() {
    if [ $LOCAL_CHECK -eq 1 ];then
        local_check=$(curl -s --connect-timeout 3 --max-time 5 ${LOCAL_URL} -o /dev/null -w %{http_code})
        if [ "${local_check}" == "200" ];then
            echo "Tomcat ${LOCAL_URL} 200 OK"
        else
            echo "Tomcat ${LOCAL_URL} curl Faild!"
            exit 1
        fi
    fi

    echo "200 OK" > ${STATUS_FILE}
    if [[ ! -f ${NGXPID_FILE} ]];then
        echo "Nginx server starting!"
        ${NGXBIN_START}
    else
        echo "Nginx server Running..."
    fi

    sleep 1
    nginx_status=$(curl -s --connect-timeout 3 --max-time 5 ${NGINX_URL} -o /dev/null -w %{http_code})
    if [ "${nginx_status}" == "200" ];then
        echo "Nginx ${NGINX_URL} 200 OK"
    else
        echo "Nginx ${NGINX_URL} curl Faild!"
        rm_status_do
    fi
}

if [ -z ${APPNAME} ];then
    function http_start(){
        return 0;
    }
    function http_stop(){
        return 0;
    }
else
    sed 's/8080/8888/g' -i $TOMCAT_FILE
fi

if [ $NGINX_CHECK == 0 ];then
    function http_start(){
        return 0
    }
    function http_stop(){
        return 0
    }
fi

