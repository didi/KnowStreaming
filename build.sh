#!/bin/bash
workspace=$(cd $(dirname $0) && pwd -P)
cd $workspace

## constant
OUTPUT_DIR=./output
KM_VERSION=2.4.0
APP_NAME=kafka-manager
APP_DIR=${APP_NAME}-${KM_VERSION}

MYSQL_TABLE_SQL_FILE=./docs/install_guide/create_mysql_table.sql
CONFIG_FILE=./kafka-manager-web/src/main/resources/application.yml

## function
function build() {
    # 编译命令
	  mvn -U clean package -Dmaven.test.skip=true

    local sc=$?
    if [ $sc -ne 0 ];then
    	## 编译失败, 退出码为 非0
        echo "$APP_NAME build error"
        exit $sc
    else
        echo "$APP_NAME build ok"
    fi
}

function make_output() {
    # 新建output目录
    rm -rf ${OUTPUT_DIR} &>/dev/null
    mkdir -p ${OUTPUT_DIR}/${APP_DIR} &>/dev/null

    # 填充output目录, output内的内容
    (
          cp -rf ${MYSQL_TABLE_SQL_FILE} ${OUTPUT_DIR}/${APP_DIR} &&       # 拷贝 sql 初始化脚本      至output目录
          cp -rf ${CONFIG_FILE} ${OUTPUT_DIR}/${APP_DIR} &&                # 拷贝 application.yml   至output目录

          # 拷贝程序包到output路径
          cp kafka-manager-web/target/kafka-manager-web-${KM_VERSION}-SNAPSHOT.jar ${OUTPUT_DIR}/${APP_DIR}/${APP_NAME}.jar
          echo -e "make output ok."
    ) || { echo -e "make output error"; exit 2; } # 填充output目录失败后, 退出码为 非0
}

function make_package() {
	# 压缩output目录
	(
	    cd ${OUTPUT_DIR} && tar cvzf ${APP_DIR}.tar.gz ${APP_DIR}
      echo -e "make package ok."
	) || { echo -e "make package error"; exit 2; } # 压缩output目录失败后, 退出码为 非0
}

##########################################
## main
## 其中,
## 		1.进行编译
##		2.生成部署包output
##		3.生成tar.gz压缩包
##########################################

# 1.进行编译
build

# 2.生成部署包output
make_output

# 3.生成tar.gz压缩包
make_package

# 编译成功
echo -e "build done"
exit 0