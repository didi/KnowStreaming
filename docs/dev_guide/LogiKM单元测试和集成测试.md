
---

![kafka-manager-logo](../assets/images/common/logo_name.png)

**一站式`Apache Kafka`集群指标监控与运维管控平台**

--- 


# LogiKM单元测试和集成测试

## 1、单元测试
### 1.1 单元测试介绍
单元测试又称模块测试，是针对软件设计的最小单位——程序模块进行正确性检验的测试工作。
其目的在于检查每个程序单元能否正确实现详细设计说明中的模块功能、性能、接口和设计约束等要求，
发现各模块内部可能存在的各种错误。单元测试需要从程序的内部结构出发设计测试用例。
多个模块可以平行地独立进行单元测试。

### 1.2 LogiKM单元测试思路
LogiKM单元测试思路主要是测试Service层的方法，通过罗列方法的各种参数，
判断方法返回的结果是否符合预期。单元测试的基类加了@SpringBootTest注解，即每次运行单测用例都启动容器

### 1.3 LogiKM单元测试注意事项
1. 单元测试用例在kafka-manager-core以及kafka-manager-extends下的test包中
2. 配置在resources/application.yml，包括运行单元测试用例启用的数据库配置等等
3. 编译打包项目时，加上参数-DskipTests可不执行测试用例，例如使用命令行mvn -DskipTests进行打包




## 2、集成测试
### 2.1 集成测试介绍
集成测试又称组装测试，是一种黑盒测试。通常在单元测试的基础上，将所有的程序模块进行有序的、递增的测试。
集成测试是检验程序单元或部件的接口关系，逐步集成为符合概要设计要求的程序部件或整个系统。

### 2.2 LogiKM集成测试思路
LogiKM集成测试主要思路是对Controller层的接口发送Http请求。
通过罗列测试用例，模拟用户的操作，对接口发送Http请求，判断结果是否达到预期。
本地运行集成测试用例时，无需加@SpringBootTest注解（即无需每次运行测试用例都启动容器）

### 2.3 LogiKM集成测试注意事项
1. 集成测试用例在kafka-manager-web的test包下 
2. 因为对某些接口发送Http请求需要先登陆，比较麻烦，可以绕过登陆，方法可见教程见docs -> user_guide -> call_api_bypass_login
3. 集成测试的配置在resources/integrationTest-settings.properties文件下，包括集群地址，zk地址的配置等等
4. 如果需要运行集成测试用例，需要本地先启动LogiKM项目
5. 编译打包项目时，加上参数-DskipTests可不执行测试用例，例如使用命令行mvn -DskipTests进行打包