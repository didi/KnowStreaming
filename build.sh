#!/bin/bash
workspace=$(cd $(dirname $0) && pwd -P)
cd $workspace

## TODO const
APPNAME=service-discovery
module=$APPNAME
app=$module

gitversion=.gitversion
control=./control.sh
ngxfunc=./nginxfunc.sh

## function
function build() {
    # 进行编译
    # cmd
    JVERSION=`java -version 2>&1 | awk 'NR==1{gsub(/"/,"");print $3}'`
    major=`echo $JVERSION | awk -F. '{print $1}'`
    mijor=`echo $JVERSION | awk -F. '{print $2}'`
    if [ $major -le 1 ] && [ $mijor -lt 11 ]; then
        export JAVA_HOME=/usr/local/jdk-11.0.2  #(使用jdk11请设置)
        export PATH=$JAVA_HOME/bin:$PATH
    fi
    # XXX 编译命令
    # mvn clean install -Ponline -Dmaven.test.skip=true -f ../pom.xml
    ./gradlew -PscalaVersion=2.12 releaseTarGz

    local sc=$?
    if [ $sc -ne 0 ];then
        ## 编译失败, 退出码为 非0
        echo "$app build error"
        exit $sc
    else
        echo -n "$app build ok, vsn="
        gitversion
    fi
}

function make_output() {
    # 新建output目录
    local output="./output"
    rm -rf $output &>/dev/null
    mkdir -p $output &>/dev/null

    # 填充output目录, output内的内容 即为 线上部署内容
    (
        cp -rf $control $output &&         # 拷贝 control.sh脚本 至output目录
        cp -rf $ngxfunc $output &&
        cp -rf ./APP_META $output &&
        cp -rf ./APP_META/Dockerfile $output &&
        # XXX 解压程序包到output路径
                tar -xzvf core/build/distributions/kafka_2.12-sd-2.5.0-d-100.tgz
                mv kafka_2.12-sd-2.5.0-d-100 ${output}/service-discovery
        # unzip target/${module}.war -d ${output} &&     # 解压war包到output目录
        echo -e "make output ok."
    ) || { echo -e "make output error"; exit 2; } # 填充output目录失败后, 退出码为 非0
}

## internals
function gitversion() {
    git log -1 --pretty=%h > $gitversion
    local gv=`cat $gitversion`
    echo "$gv"
}


##########################################
## main
## 其中,
##      1.进行编译
##      2.生成部署包output
##########################################

# 1.进行编译
build

# 2.生成部署包output
make_output

# 编译成功
echo -e "build done"
exit 0
