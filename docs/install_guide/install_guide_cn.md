
---

![kafka-manager-logo](../assets/images/common/logo_name.png)

**一站式`Apache Kafka`集群指标监控与运维管控平台**

--- 

# 安装手册

## 1、环境依赖

如果是以Release包进行安装的，则仅安装`Java`及`MySQL`即可。如果是要先进行源码包进行打包，然后再使用，则需要安装`Maven`及`Node`环境。

- `Java 8+`(运行环境需要)
- `MySQL 5.7`(数据存储)
- `Maven 3.5+`(后端打包依赖)
- `Node 10+`(前端打包依赖)

---

## 2、获取安装包

**1、Release直接下载**

这里如果觉得麻烦，然后也不想进行二次开发，则可以直接下载Release包，下载地址：[Github Release包下载地址](https://github.com/didi/Logi-KafkaManager/releases)

如果觉得Github的下载地址太慢了，也可以进入`Logi-KafkaManager`的用户群获取，群地址在README中。


**2、源代码进行打包**

下载好代码之后，进入`Logi-KafkaManager`的主目录，执行`mvn -Prelease-kafka-manager -Dmaven.test.skip=true clean install -U  `命令即可，
执行完成之后会在`distribution/target`目录下面生成一个`kafka-manager-*.tar.gz`。
和一个`kafka-manager-*.zip` 文件,随便任意一个压缩包都可以; 
当然此时同级目录有一个已经解压好的文件夹;



---

## 3. 解压安装包
解压完成后; 在文件目录中可以看到有`kafka-manager/conf/create_mysql_table.sql` 有个mysql初始化文件
先初始化DB

      
## 4、MySQL-DB初始化

执行[create_mysql_table.sql](../../distribution/conf/create_mysql_table.sql)中的SQL命令，从而创建所需的MySQL库及表，默认创建的库名是`logi_kafka_manager`。

```
# 示例：
mysql -uXXXX -pXXX -h XXX.XXX.XXX.XXX -PXXXX < ./create_mysql_table.sql
```

---

## 5.修该配置
请将`conf/application.yml.example` 文件复制一份出来命名为`application.yml` 放在同级目录:conf/application.yml ;
并且修改配置; 当然不修改的话 就会用默认的配置;
至少 mysql配置成自己的吧


## 6、启动/关闭
解压包中有启动和关闭脚本 
`kafka-manager/bin/shutdown.sh`
`kafka-manager/bin/startup.sh`

执行 sh startup.sh 启动
执行 sh shutdown.sh 关闭



### 6、使用

本地启动的话，访问`http://localhost:8080`，输入帐号及密码(默认`admin/admin`)进行登录。更多参考：[kafka-manager 用户使用手册](../user_guide/user_guide_cn.md)

### 7. 升级

如果是升级版本,请查看文件 [kafka-manager 升级手册](../../distribution/upgrade_config.md) 
 在您下载的启动包(V2.5及其后)中也有记录,在 kafka-manager/upgrade_config.md 中


### 8. 在IDE中启动
> 如果想参与开发或者想在IDE中启动的话
> 先执行  `mvn -Dmaven.test.skip=true clean install -U ` 
> 
> 然后这个时候可以选择去 [pom.xml](../../pom.xml) 中将`kafka-manager-console`模块注释掉;
> 注释是因为每次install的时候都会把前端文件`kafka-manager-console`重新打包进`kafka-manager-web`
> 
> 完事之后,只需要直接用IDE启动运行`kafka-manager-web`模块中的
> com.xiaojukeji.kafka.manager.web.MainApplication main方法就行了