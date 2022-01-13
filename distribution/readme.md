## 说明

### 1.创建mysql数据库文件
> conf/create_mysql_table.sql

### 2. 修改配置文件
> conf/application.yml.example
> 请将application.yml.example 复制一份改名为application.yml；
> 并放在同级目录下(conf/); 并修改成自己的配置
> 这里的优先级比jar包内配置文件的默认值高; 
> 

### 3.启动/关闭kafka-manager
> sh bin/startup.sh    启动
> 
> sh shutdown.sh       关闭
> 


### 4.升级jar包
> 如果是升级, 可以看看文件 `upgrade_config.md` 的配置变更历史;
> 