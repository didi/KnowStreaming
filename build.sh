#!/bin/bash
workspace=$(cd $(dirname $0) && pwd -P)
cd $workspace

## constant
km_version=2.1.0
app_name=kafka-manager-$km_version

gitversion=.gitversion
control=./control.sh
create_mysql_table=./docs/install_guide/create_mysql_table.sql
app_config_file=./kafka-manager-web/src/main/resources/application.yml

## function
function build() {
    # 进行编译
#    # cmd 设置使用的JDK, 按需选择, 默认已安装了JDK 8
#    JVERSION=`java -version 2>&1 | awk 'NR==1{gsub(/"/,"");print $3}'`
#    major=`echo $JVERSION | awk -F. '{print $1}'`
#    mijor=`echo $JVERSION | awk -F. '{print $2}'`
#    if [ $major -le 1 ] && [ $mijor -lt 8 ]; then
#        export JAVA_HOME=/usr/local/jdk1.8.0_65  #(使用jdk8请设置)
#        export PATH=$JAVA_HOME/bin:$PATH
#    fi

    # 编译命令
	mvn -U clean package -Dmaven.test.skip=true

    local sc=$?
    if [ $sc -ne 0 ];then
    	## 编译失败, 退出码为 非0
        echo "$app_name build error"
        exit $sc
    else
        echo -n "$app_name build ok, vsn="`gitversion`
    fi
}

function make_output() {
	# 新建output目录
	rm -rf $app_name &>/dev/null
	mkdir -p $app_name &>/dev/null

	# 填充output目录, output内的内容 即为 线上部署内容
	(
#        cp -rf $control $output_dir &&                 # 拷贝 control.sh 脚本    至output目录
        cp -rf $create_mysql_table $app_name &&       # 拷贝 sql 初始化脚本      至output目录
        cp -rf $app_config_file $app_name &&          # 拷贝 application.yml   至output目录

        # 拷贝程序包到output路径
		cp kafka-manager-web/target/kafka-manager-web-$km_version-SNAPSHOT.jar ${app_name}/${app_name}-SNAPSHOT.jar
        echo -e "make output ok."
	) || { echo -e "make output error"; exit 2; } # 填充output目录失败后, 退出码为 非0
}

function make_package() {
	# 压缩output目录
	(
	    tar cvzf ${app_name}.tar.gz ${app_name}
        echo -e "make package ok."
	) || { echo -e "make package error"; exit 2; } # 压缩output目录失败后, 退出码为 非0
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
