#!/bin/sh
#集群任务脚本

set -x # 调试方式执行

#----------------------------------------日志格式------------------------------------------------------#
alias ECHO_LOG='echo `date +%F%n%T` hostname:`hostname` Line:${LINENO} '

#----------------------------------------参数列表------------------------------------------------------#
p_task_id=${1}             #任务ID
p_cluster_id=${2}          #集群ID
p_cluster_task_type=${3}   #任务类型[0:升级, 1:新部署, 2:回滚]

p_kafka_package_name=${4}             #包名
p_kafka_package_md5=${5}              #包MD5
p_kafka_package_url=${6}              #包下载地址
p_kafka_server_properties_name=${7}   #server配置名
p_kafka_server_properties_md5=${8}    #server配置MD5
p_kafka_server_properties_url=${9}    #server配置文件下载地址

#----------------------------------------配置信息------------------------------------------------------#
g_base_dir='/home'
g_cluster_task_dir=${g_base_dir}"/kafka_cluster_task/task_${p_task_id}"  #部署升级路径
g_rollback_version=${g_cluster_task_dir}"/rollback_version"              #回滚版本
g_new_kafka_package_name=''                                              #最终的包名
g_kafka_manager_addr=''                                                  #kafka-manager地址
g_local_ip=`ifconfig -a|grep inet|grep -v 127.0.0.1|grep -v inet6|awk '{print $2}'|tr -d "addr:"`
g_hostname=${g_local_ip}

#----------------------------------------操作函数------------------------------------------------------#

# dhcat通知
function dchat_alarm() {
    alarm_msg=$1
    data='
{
    "text": "'${g_hostname}' 升级失败，请及时处理",
    "attachments": [
        {
            "title": "'${alarm_msg}'",
            "color": "#ffa500"
        }
    ]
}'
#curl -H 'Content-Type: application/json' -d "${data}" ${dchat_bot}
}

# 检查并初始化环境
function check_and_init_env() {
    if [ -z "${p_task_id}" -o -z "${p_cluster_task_type}" -o -z "${p_kafka_package_url}" -o -z "${p_cluster_id}" -o -z "${p_kafka_package_name}" -o -z "${p_kafka_package_md5}" -o -z "${p_kafka_server_properties_name}" -o -z "${p_kafka_server_properties_md5}" ]; then
        ECHO_LOG "存在为空的参数不合法, 退出集群任务"
        dchat_alarm "存在为空的参数不合法, 退出集群任务"
        exit 1
    fi

    cd ${g_base_dir}
    if [ $? -ne 0 -o ! -x "${g_base_dir}" ];then
        ECHO_LOG "${g_base_dir}目录不存在或无权限, 退出集群任务"
        dchat_alarm "${g_base_dir}目录不存在或无权限, 退出集群任务"
        exit 1
    fi

    ECHO_LOG "初始化集群任务所需的目录"
    mkdir -p ${g_cluster_task_dir}
    if [ $? -ne 0 ];then
        ECHO_LOG "创建集群任务路径失败, 退出集群任务"
        dchat_alarm "创建集群任务路径失败, 退出集群任务"
        exit 1
    fi
}


# 检查并等待集群所有的副本处于同步的状态
function check_and_wait_broker_stabled() {
    under_replication_count=`curl -s -G -d "hostname="${g_hostname} ${g_kafka_manager_addr}/api/v1/third-part/${p_cluster_id}/broker-stabled | python -m json.tool | grep true |wc -l`
    while [ "$under_replication_count" -ne 1 ]; do
        ECHO_LOG "存在${under_replication_count}个副本未同步, sleep 10s"
        sleep 10
        under_replication_count=`curl -s -G -d "hostname="${g_hostname} ${g_kafka_manager_addr}/api/v1/third-part/${p_cluster_id}/broker-stabled | python -m json.tool | grep true |wc -l`
    done
    ECHO_LOG "集群副本都已经处于同步的状态, 可以进行集群升级"
}


# 拉包并检查其md5
function pull_and_check_kafka_package() {
    ECHO_LOG "开始下载${1}文件"
    wget ${1} -P ${g_cluster_task_dir}
    if [ $? -ne 0 ];then
        ECHO_LOG "下载${1}失败, 退出集群任务"
        dchat_alarm "下载${1}失败, 退出集群任务"
        exit 1
    fi

    file_md5_sum=`md5sum "${g_cluster_task_dir}/${p_kafka_package_name}.tgz" | awk -F " " '{print $1}'`
    if [ "$file_md5_sum" !=  "${2}" ];then
        ECHO_LOG "下载${1}成功, 但是校验md5失败, 退出集群任务"
        dchat_alarm "下载${1}成功, 但是校验md5失败, 退出集群任务"
        exit 1
    fi
}

# 拉配置文件并检查其md5
function pull_and_check_kafka_properties() {
    ECHO_LOG "开始下载${1}文件"
    wget ${1} -P ${g_cluster_task_dir}
    if [ $? -ne 0 ];then
        ECHO_LOG "下载${1}失败, 退出集群任务"
        dchat_alarm "下载${1}失败, 退出集群任务"
        exit 1
    fi

    file_md5_sum=`md5sum "${g_cluster_task_dir}/${p_kafka_server_properties_name}.properties" | awk -F " " '{print $1}'`
    if [ "$file_md5_sum" !=  "${2}" ];then
        ECHO_LOG "下载${1}成功, 但是校验md5失败, 退出集群任务"
        dchat_alarm "下载${1}成功, 但是校验md5失败, 退出集群任务"
        exit 1
    fi
}

# 准备集群任务的文件
function prepare_cluster_task_files() {
    pull_and_check_kafka_package ${p_kafka_package_url} ${p_kafka_package_md5}
    ECHO_LOG "解压并拷贝kafka包文件"
    tar -zxf "${g_cluster_task_dir}/${p_kafka_package_name}.tgz"  -C "${g_cluster_task_dir}"
    if [ $? -ne 0 ];then
        ECHO_LOG "解压${p_kafka_package_name}.tgz失败, 退出集群任务"
        dchat_alarm "解压${p_kafka_package_name}.tgz失败, 退出集群任务"
        exit 1
    fi

    pull_and_check_kafka_properties ${p_kafka_server_properties_url} ${p_kafka_server_properties_md5}
    ECHO_LOG "拷贝kafka配置文件"
    cp -f "${g_cluster_task_dir}/${p_kafka_server_properties_name}.properties" "${g_cluster_task_dir}/${p_kafka_package_name}/config/server.properties"
    if [ $? -ne 0 ];then
        ECHO_LOG "拷贝${p_kafka_server_properties_name}.properties失败, 退出集群任务"
        dchat_alarm "拷贝${p_kafka_server_properties_name}.properties失败, 退出集群任务"
        exit 1
    fi

    # listeners配置，换成当前机器的IP，写到server.properties最后一行
    echo "listeners=SASL_PLAINTEXT://${g_local_ip}:9093,PLAINTEXT://${g_local_ip}:9092" >> "${g_cluster_task_dir}/${p_kafka_package_name}/config/server.properties"

    # 将MD5信息写到包中
    echo "package_md5:${p_kafka_package_md5}    server_properties_md5:${p_kafka_package_md5}" > "${g_cluster_task_dir}/${p_kafka_package_name}/package_and_properties.md5"
}


# 停kafka服务
function stop_kafka_server() {
    sh ${g_base_dir}"/kafka/bin/kafka-server-stop.sh"

    ECHO_LOG "检查并等待kafka服务下线"
    kafka_pid=`jps | grep 'Kafka' | awk '{print $1}'`
    while [ ! -z "${kafka_pid}" ];do
        ECHO_LOG "kafka服务未下线, 继续sleep 5s"
        sleep 5

        kafka_pid=`jps | grep 'Kafka' | awk '{print $1}'`
    done
    ECHO_LOG "kafka服务已停掉"
}



function cal_new_package_name() {
    if [ ! -d "${g_base_dir}/${p_kafka_package_name}" ]; then
        # 当前使用的包版本未部署过
        g_new_kafka_package_name=${p_kafka_package_name}
        return
    fi

    deploy_version=1
    while [ ${deploy_version} -le 1000 ];do
        if [ ! -d "${g_base_dir}/${p_kafka_package_name}_v${deploy_version}" ]; then
            g_new_kafka_package_name="${p_kafka_package_name}_v${deploy_version}"
            return
        fi
        ECHO_LOG "包 ${p_kafka_package_name}_v${deploy_version} 已经存在"
        deploy_version=`expr ${deploy_version} + 1`
    done
}


#
function backup_and_init_new_kafka_server_soft_link() {
    cd ${g_base_dir}/"kafka"
    if [ $? -ne 0 ];then
        ECHO_LOG "kafka软链不存在, 退出集群任务"
        dchat_alarm "kafka软链不存在, 退出集群任务"
        exit 1
    fi

    # 纪录回滚版本
    kafka_absolute_path=`pwd -P`
    rollback_version=`basename "${kafka_absolute_path}"`
    echo ${rollback_version} > ${g_rollback_version}
    ECHO_LOG "上一版本: ${rollback_version}"

    # 去除软链
    unlink ${g_base_dir}/"kafka"
    if [ $? -ne 0 ];then
        ECHO_LOG "移除软链失败, 退出集群任务"
        dchat_alarm "移除软链失败, 退出集群任务"
        exit 1
    fi

    # 计算新的包名及初始化环境
    init_new_kafka_server_soft_link
}


# 回滚之前的版本
function rollback_kafka_server_soft_link() {
    if [ ! -f "${g_rollback_version}" ]; then
        ECHO_LOG "回滚文件不存在, 退出集群任务"
        dchat_alarm "回滚文件不存在, 退出集群任务"
        exit 1
    fi

    rollback_version=`cat ${g_rollback_version}`
    if [ ! -n "${rollback_version}" ]; then
        ECHO_LOG "回滚信息不存在, 退出集群任务"
        dchat_alarm "回滚信息不存在, 退出集群任务"
        exit 1
    fi

    # 去除软链
    unlink ${g_base_dir}/"kafka"
    if [ $? -ne 0 ];then
        ECHO_LOG "移除软链失败, 退出集群任务"
        dchat_alarm "移除软链失败, 退出集群任务"
        exit 1
    fi

    ln -s "${g_base_dir}/${rollback_version}" "${g_base_dir}/kafka"
    if [ $? -ne 0 ];then
        ECHO_LOG "创建软链失败, 退出集群任务"
        dchat_alarm "创建软链失败, 退出集群任务"
        exit 1
    fi
    ECHO_LOG "修改软链成功"
}


function init_new_kafka_server_soft_link() {
    if [ -L "${g_base_dir}/kafka" ];then
        ECHO_LOG "kafka软链依旧存在, 退出集群任务"
        dchat_alarm "kafka软链依旧存在, 退出集群任务"
        exit 1
    fi

    # 计算新的包名
    cal_new_package_name
    ECHO_LOG "集群任务新包的名字为${g_new_kafka_package_name}"

    # 拷贝新的包
    cp -rf "${g_cluster_task_dir}/${p_kafka_package_name}" "${g_base_dir}/${g_new_kafka_package_name}"

    ln -s "${g_base_dir}/${g_new_kafka_package_name}" "${g_base_dir}/kafka"
    if [ $? -ne 0 ];then
        ECHO_LOG "创建软链失败, 退出集群任务"
        dchat_alarm "创建软链失败, 退出集群任务"
        exit 1
    fi
    ECHO_LOG "备份并修改软链成功"
}


function check_and_wait_kafka_process_started() {
    sleep 1

    # 等待并检查kafka进程是否正常启动
    ECHO_LOG "开始等待并检查进程是否正常启动"
    if [ ! -L "${g_base_dir}/kafka" ];then
        ECHO_LOG "kafka软链不存在, 退出集群任务"
        dchat_alarm "kafka软链不存在, 退出集群任务"
        exit 1
    fi

    log_started_count=0
    while [ "$log_started_count" == "0" ];do
        # 检查进程是否存活
        kafka_pid=`jps | grep 'Kafka' | awk '{print $1}'`
        if [ -z "${kafka_pid}" ];then
           ECHO_LOG "安装失败, kafka进程不存在, 退出集群任务"
           dchat_alarm "安装失败, kafka进程不存在, 退出集群任务"
           exit 1
        fi

        sleep 2

        #检查是否存在NotLeader的分区
        not_leader_error_count=`grep Exception ${g_base_dir}/kafka/logs/server.log* | grep NotLeaderForPartitionException | wc -l`
        if [ ${not_leader_error_count} -gt 0 ];then
            ECHO_LOG "安装失败, 存在无Leader的分区, 退出集群任务"
            dchat_alarm "安装失败, 存在无Leader的分区, 退出集群任务"
            exit 1
        fi

        #判断Started的日志是否正常打印出来了
        log_started_count=`grep Started ${g_base_dir}/kafka/logs/server.log | wc -l`
    done
    ECHO_LOG "进程已正常启动, 结束进程状态检查"
}

start_new_kafka_server() {
    nohup sh "${g_base_dir}/kafka/bin/kafka-server-start.sh" "${g_base_dir}/kafka/config/server.properties" > /dev/null 2>&1 &
    if [ $? -ne 0 ];then
        ECHO_LOG "启动kafka服务失败, 退出部署升级"
        dchat_alarm "启动kafka服务失败, 退出部署升级"
        exit 1
    fi
}


#----------------------------------部署流程---------------------------------------------------------#
ECHO_LOG "集群任务启动..."
ECHO_LOG "参数信息: "
ECHO_LOG "      p_task_id=${p_task_id}"
ECHO_LOG "      p_cluster_id=${p_cluster_id}"
ECHO_LOG "      p_cluster_task_type=${p_cluster_task_type}"
ECHO_LOG "      p_kafka_package_name=${p_kafka_package_name}"
ECHO_LOG "      p_kafka_package_md5=${p_kafka_package_md5}"
ECHO_LOG "      p_kafka_server_properties_name=${p_kafka_server_properties_name}"
ECHO_LOG "      p_kafka_server_properties_md5=${p_kafka_server_properties_md5}"



if [ "${p_cluster_task_type}" == "0" -o "${p_cluster_task_type}" == "1" ];then
    ECHO_LOG "检查并初始化环境"
    check_and_init_env
    ECHO_LOG "准备集群任务所需的文件"
    prepare_cluster_task_files
else
    ECHO_LOG "升级回滚, 无需准备新文件"
fi

#ECHO_LOG "检查并等待Broker处于稳定状态"
#check_and_wait_broker_stabled

ECHO_LOG "停kafka服务"
stop_kafka_server

ECHO_LOG "停5秒, 确保"
sleep 5

if [ "${p_cluster_task_type}" == "0" ];then
    ECHO_LOG "备份并初始化升级所需的环境(包/软链等)"
    backup_and_init_new_kafka_server_soft_link
elif [ "${p_cluster_task_type}" == "1" ];then
    ECHO_LOG "初始化部署所需的环境(包/软链等)"
    init_new_kafka_server_soft_link
else
    ECHO_LOG "回滚旧的环境(包/软链等)"
    rollback_kafka_server_soft_link
fi

ECHO_LOG "启动kafka服务"
start_new_kafka_server


ECHO_LOG "检查并等待kafka服务正常运行..."
check_and_wait_kafka_process_started


ECHO_LOG "检查并等待Broker处于稳定状态"
check_and_wait_broker_stabled

ECHO_LOG "清理临时文件"
#rm -r ${g_cluster_task_dir}


ECHO_LOG "升级成功, 结束升级"