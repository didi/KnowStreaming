#!/bin/bash
set -x

function Install_Java(){
	if [ ! -d "/usr/local/java11" ];then
		cd $dir
		wget -O "jdk11.tar.gz" https://s3-gzpu.didistatic.com/pub/jdk11.tar.gz
	    tar -zxf $dir/jdk11.tar.gz -C /usr/local/
	    mv -f /usr/local/jdk-11.0.2 /usr/local/java11
	    echo "export JAVA_HOME=/usr/local/java11" >> ~/.bashrc
	    echo "export CLASSPATH=/usr/java/java11/lib" >> ~/.bashrc
	    echo "export PATH=\$JAVA_HOME/bin:\$PATH:\$HOME/bin" >> ~/.bashrc
	    source ~/.bashrc
	fi
}

function Install_Mysql(){
	while : 
		do
		read -p "Do you need to install MySQL(yes/no): " my_result
		if [ "$my_result" == "no" ];then
			which mysql >/dev/null 2>&1 
			if [ "$?" != "0" ];then
				echo "MySQL client is not installed on this machine. Start to install now"
				cd $dir
				wget -O "mysql5.7.tar.gz" https://s3-gzpu.didistatic.com/pub/mysql5.7.tar.gz
				mkdir -p $dir/mysql/ && cd $dir/mysql/
				tar -zxf $dir/mysql5.7.tar.gz -C $dir/mysql/
				rpm -ivh $dir/mysql/mysql-community-common-5.7.36-1.el7.x86_64.rpm
				rpm -ivh $dir/mysql/mysql-community-libs-5.7.36-1.el7.x86_64.rpm
				rpm -ivh $dir/mysql/mysql-community-client-5.7.36-1.el7.x86_64.rpm
			fi
			read -p "Please enter the MySQL service address: " mysql_ip
			read -p "Please enter MySQL service port(default is 3306): " mysql_port
			read -p "Please enter the root password of MySQL service: " mysql_pass
			if [ "$mysql_port" == "" ];then
				mysql_port=3306
			fi
			break
		elif [ "$my_result" == "yes" ];then
			read -p "Installing MySQL service will uninstall the installed(if any), Do you want to continue(yes/no): " option
			if [ "$option" == "yes" ];then	
				cd $dir
				wget -O "mysql5.7.tar.gz" https://s3-gzpu.didistatic.com/pub/mysql5.7.tar.gz
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
					mysql_ip="127.0.0.1"
					mysql_port="3306"
					echo  "Mysql database installation completed"
				else
					echo -e "${RED} Mysql database configuration failed. The script exits ${RES}"
					exit
				fi
				break
			else 
				exit 1
			fi
		else
			Printlog "Input error, please re-enter(yes/no)"
			continue
		fi
	done
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
	wget -O "elasticsearch-7.6.1.tar.gz" https://s3-gzpu.didistatic.com/pub/elasticsearch-7.6.1.tar.gz
	tar -zxf elasticsearch-7.6.1.tar.gz -C /km_es/
	chown -R esuser:esuser /km_es/
	su - esuser <<-EOF
		export JAVA_HOME=/usr/local/java11
		sh /km_es/elasticsearch-7.6.1/control.sh start
	EOF
	sleep 5
	es_status=`sh /km_es/elasticsearch-7.6.1/control.sh status | grep -o "started"`
	if [ "$es_status" = "started" ];then
		echo "elasticsearch started successfully～ "
	else
		echo -e "${RED} Elasticsearch failed to start. The script exited ${RES}"
		exit	
	fi
}

function Install_KnowStreaming(){
	mysql_command="mysql -h${mysql_ip} -uroot -p${mysql_pass} -P${mysql_port}"
	cd $dir
	wget -O "KnowStreaming-3.0.0-beta.1.tar.gz" https://s3-gzpu.didistatic.com/pub/knowstreaming/KnowStreaming-3.0.0-beta.1.tar.gz
	tar -zxf KnowStreaming-3.0.0-beta.1.tar.gz -C $dir/
	echo "exit" | $mysql_command >/dev/null 2>&1
	if [ $? -ne 0 ];then
		echo -e "${RED} Database connection failed !!! ${RES}"
		exit
	else
		$mysql_command -e "create database know_streaming;"
		$mysql_command  know_streaming < ./KnowStreaming/init/sql/ddl-ks-km.sql
		$mysql_command  know_streaming < ./KnowStreaming/init/sql/ddl-logi-job.sql 
	    $mysql_command  know_streaming < ./KnowStreaming/init/sql/ddl-logi-security.sql
	    $mysql_command  know_streaming < ./KnowStreaming/init/sql/dml-ks-km.sql 
	    $mysql_command  know_streaming < ./KnowStreaming/init/sql/dml-logi.sql
	   
	    sed -i "s/password:.*/password: "$mysql_pass"/g" ./KnowStreaming/conf/application.yml
	    sh ./KnowStreaming/init/template/template.sh

	    pids=$(ps -ef | grep ks-km.jar | grep -v "grep" | awk '{print $2}')
	    count=$(echo $pids | awk '{print NF}')
	    if [ $count -gt 0 ];then
	    	for pid in $pids
	    	do
	    		netstat -alntp | grep "$pid" | grep -E "0.0.0.0:[0-9]{1,9}|\:\:\:[0-9]{1,9}"
	    		if [ $? -ne 0 ];then
	    			kill -9 $pid
	    		fi
	    	done
	    fi
	    km_port=$(cat $dir/KnowStreaming/conf/application.yml | grep "port: " | awk '{print $2}')
	    netstat -alntp | grep "$km_port" 
	    if [ $? -eq 0 ];then
	    	echo -e " ${RED} $km_port The port is occupied and has been modified to 1$km_port ${RES}"
	    	sed -i "s/port:.*/port: 1"$km_port"/g" $dir/KnowStreaming/conf/application.yml
	    fi
	    cd $dir/KnowStreaming/bin/ && sh startup.sh
	fi

}

RED='\E[1;31m'
RES='\E[0m'

dir=`pwd`
mysql_pass=`date +%s |sha256sum |base64 |head -c 10 ;echo`"_Di2"
echo "$mysql_pass" > $dir/mysql.password
echo -e "${RED} The database password is stored in: $dir/mysql.password ${RES}"

Install_Mysql
Install_Java
Install_ElasticSearch
Install_KnowStreaming