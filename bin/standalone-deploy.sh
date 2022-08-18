#!/bin/bash
set -x

function Install_Java(){
	cd $dir
	wget https://s3-gzpu.didistatic.com/pub/jdk11.tar.gz
    tar -zxf $dir/jdk11.tar.gz -C /usr/local/
    mv -f /usr/local/jdk-11.0.2 /usr/local/java11 >/dev/null 2>&1 
    echo "export JAVA_HOME=/usr/local/java11" >> ~/.bashrc
    echo "export CLASSPATH=/usr/java/java11/lib" >> ~/.bashrc
    echo "export PATH=\$JAVA_HOME/bin:\$PATH:\$HOME/bin" >> ~/.bashrc
    source ~/.bashrc
}

function Install_Mysql(){
	cd $dir
	wget https://s3-gzpu.didistatic.com/pub/mysql5.7.tar.gz
	rpm -qa | grep -E "mariadb|mysql" | xargs yum -y remove >/dev/null 2>&1 
	mv -f /var/lib/mysql/ /var/lib/mysqlbak$(date "+%s") >/dev/null 2>&1 
	mkdir -p $dir/mysql/ && cd $dir/mysql/
	tar -zxf $dir/mysql5.7.tar.gz -C $dir/mysql/
	yum -y localinstall mysql* libaio*
	systemctl start mysqld
	systemctl enable mysqld >/dev/null 2>&1 
	old_pass=`grep 'temporary password' /var/log/mysqld.log | awk '{print $NF}' | tail -n 1`
	mysql -NBe "alter user USER() identified by '$mysql_pass';" --connect-expired-password -uroot -p$old_pass
	if [ $? -eq 0 ];then
		echo  "Mysql database installation completed"
	else
		echo "Mysql database configuration failed. The script exits"
		exit
	fi
}

function Install_ElasticSearch(){
	kill -9 $(ps -ef | grep elasticsearch | grep -v "grep" | awk '{print $2}')  >/dev/null 2>&1   
	id esuser  >/dev/null 2>&1  
	if [ "$?" != "0" ];then
		useradd esuser
		echo "esuser soft nofile 655350" >>/etc/security/limits.conf
		echo "esuser hard nofile 655350" >>/etc/security/limits.conf
		echo "vm.max_map_count = 655360" >>/etc/sysctl.conf
		sysctl -p >/dev/null 2>&1
	fi
	mkdir -p /km_es/es_data  && cd /km_es/ >/dev/null 2>&1
	wget https://s3-gzpu.didistatic.com/pub/elasticsearch.tar.gz
	tar -zxf elasticsearch.tar.gz -C /km_es/
	chown -R esuser:esuser /km_es/
	su - esuser <<-EOF
		export JAVA_HOME=/usr/local/java11
		sh /km_es/elasticsearch/control.sh start
	EOF
	sleep 5
	es_status=`sh /km_es/elasticsearch/control.sh status | grep -o "started"`
	if [ "$es_status" = "started" ];then
		echo "elasticsearch started successfully～ "
	else
		echo "Elasticsearch failed to start. The script exited"
		exit	
	fi
}

function Install_KnowStreaming(){
	cd $dir
	wget https://s3-gzpu.didistatic.com/pub/knowstreaming/KnowStreaming-3.0.0-beta.tar.gz
	tar -zxf KnowStreaming-3.0.0-beta.tar.gz -C $dir/
	mysql -uroot -p$mysql_pass -e "create database know_streaming;"
	mysql -uroot -p$mysql_pass  know_streaming < ./KnowStreaming/init/sql/ddl-ks-km.sql
	mysql -uroot -p$mysql_pass  know_streaming < ./KnowStreaming/init/sql/ddl-logi-job.sql 
    mysql -uroot -p$mysql_pass  know_streaming < ./KnowStreaming/init/sql/ddl-logi-security.sql
    mysql -uroot -p$mysql_pass  know_streaming < ./KnowStreaming/init/sql/dml-ks-km.sql 
    mysql -uroot -p$mysql_pass  know_streaming < ./KnowStreaming/init/sql/dml-logi.sql
    sh ./KnowStreaming/init/template/template.sh
    sed -i "s/mysql_pass/"$mysql_pass"/g" ./KnowStreaming/conf/application.yml
    cd $dir/KnowStreaming/bin/ && sh startup.sh

}

dir=`pwd`
mysql_pass=`date +%s |sha256sum |base64 |head -c 10 ;echo`"_Di2"
echo "$mysql_pass" > $dir/mysql.password

Install_Java
Install_Mysql
Install_ElasticSearch
Install_KnowStreaming