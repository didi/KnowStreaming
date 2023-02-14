#!/bin/bash
#############################################
## main
## 非托管方式, 启动服务
## control.sh脚本, 必须实现start和stop两个方法
#############################################
workspace=$(cd $(dirname $0) && pwd -P)
cd ${workspace}

APPNAME=service-discovery

# TODO 如果不需要tomcat服务，请删除如下四行
#if [ -z ${APPNAME} ]; then
#    catalina_cmd="/home/${USER}/apache-tomcat-7.0.79/bin/catalina.sh"
#else
#    catalina_cmd="/home/${USER}/tomcat/bin/catalina.sh"
#fi

# TODO 设置模块名字
module=$APPNAME
app=${module}

cfgname=config
cfg="$workspace/WEB-INF/classes/${cfgname}.properties"
cfgdir="$workspace/WEB-INF/classes"
logfile=${workspace}/var/app.log
pidfile=${workspace}/var/app.pid
#export CATALINA_PID=${pidfile}

JVERSION=`java -version 2>&1 | awk 'NR==1{gsub(/"/,"");print $3}'`
major=`echo $JVERSION | awk -F. '{print $1}'`
mijor=`echo $JVERSION | awk -F. '{print $2}'`
if [ $major -le 1 ] && [ $mijor -lt 11 ]; then
    export JAVA_HOME=/usr/local/jdk-11.0.2  #(使用jdk11请设置)
    export PATH=$JAVA_HOME/bin:$PATH
fi

#nginx check
# TODO 如果不需要nginx服务，请设置NGINX_CHECK=0
NGINX_CHECK=0
LOCAL_CHECK=0
source ./nginxfunc.sh

## function
function start() {
    # 创建日志目录
    mkdir -p var &>/dev/null
    # check服务是否存活,如果存在则返回
    check_pid
    if [ $? -ne 0 ];then
        local pid=$(get_pid)
        echo "${app} is started, pid=${pid}"
        return 0
    fi

    # XXX 如果各个机房节点级别有差异使用如下方式
    # cfg
    #local clusterfile="$workspace/.deploy/service.cluster.txt"
    #if [[ -f "$clusterfile" ]]; then
    #    local cluster=`cat $clusterfile`
    #    local localconf=${cfgdir}/${cfgname}-${cluster}.properties
    #    if [[ -f $localconf ]]; then
    #        echo "using cfg file: $localconf"
    #        cp -f ${localconf} ${cfg}
    #    else
    #        echo "using default cfg file."
    #    fi
    #else
    #    echo "$clusterfile is not existed!!!"
    #    exit 1
    #fi

    # 以后台方式 启动程序
    echo -e "Starting the $module ...\c"

    ### XXX tomcat类程序启停
    #bash $catalina_cmd start
    #由于xiaoju-tomcat修改了启动脚本，所以不能用CATALINA_PID来取pid
    #pid=$(ps -ef | grep "catalina.startup.Bootstrap" | grep -v grep | awk '{print $2}')
    #if [ "x$pid" = "x" ]; then
    #    echo "start tomcat failed!!!"
    #    exit 1
    #else
    #    if [ ! -z "${pidfile}" ]; then
    #        echo $pid > ${pidfile}
    #    else
    #        echo "pidfile must be provided!!!"
    #        exit 1
    #    fi
    #fi

    ### XXX JAVA类程序启停 
    # nohup java -jar XXXNAME-*.jar 8080 > /dev/null 2>&1 &
    cd service-discovery
    nohup ./bin/kafka-server-start.sh ./config/server.properties > /dev/null 2>&1 &
    cd ../
    # 保存pid到pidfile文件中
    echo $! > ${pidfile}
    
    # 检查服务是否启动成功
    check_pid
    if [ $? -eq 0 ];then
        echo "${app} start failed, please check!!!"
        exit 1
    fi

    echo "${app} start ok, pid=${pid}"
    # 启动成功, 退出码为 0
    return 0
}

function stop() {
    local timeout=60
    # 循环stop服务, 直至60s超时
    for (( i = 0; i < $timeout; i++ )); do
        # 检查服务是否停止,如果停止则直接返回
        check_pid
        if [ $? -eq 0 ];then
            echo "${app} is stopped"
            return 0
        fi
        # 检查pid是否存在
        local pid=$(get_pid)
        if [ ${pid} == "" ];then
            echo "${app} is stopped, can't find pid on ${pidfile}"
            exit 0
        fi

        # 停止该服务
        if [ $i -eq $((timeout-1)) ]; then
            kill -9 ${pid} &>/dev/null
        else
            kill ${pid} &>/dev/null
        fi
        # 检查该服务是否停止ok
        check_pid
        if [ $? -eq 0 ];then
            # stop服务成功, 返回码为 0
            echo "${app} stop ok"
            exit 0
        fi
        # 服务未停止, 继续循环
        sleep 1
    done
    # stop服务失败, 返回码为 非0
    echo "stop timeout(${timeout}s)"
    return 1
}

function update() {
    echo "update service"
    exit 0
}

function status(){
    check_pid
    local running=$?
    if [ ${running} -ne 0 ];then
        local pid=$(get_pid)
        echo "${app} is started, pid=${pid}"
    else
        echo "${app} is stopped"
    fi
    exit 0
}

## internals
function get_pid() {
    if [ -f $pidfile ];then
        cat $pidfile
        #pid=$(cat $pidfile | sed 's/ //g')
        #(ps -fp $pid | grep $app &>/dev/null) && echo $pid
    fi
}

function check_pid() {
    pid=$(get_pid)
    if [ "x_" != "x_${pid}" ]; then
        running=$(ps -p ${pid}|grep -v "PID TTY" |wc -l)
        return ${running}
    fi
    return 0
}

action=$1
case $action in
    "start" )
        # 启动服务
        start
        http_start
        ;;
    "stop" )
        # 停止服务
        http_stop
        stop
        ;;
    "status" )
        # 检查服务
        status
        ;;
    "update" )
        # 更新操作
        update
        ;;
    * )
        echo "unknown command"
        exit 1
        ;;
esac

