
## Controller优先选举
> 在原生的kafka中,Controller角色的选举,是每个Broker抢占式的去zk写入节点`Controller`
> 任何一个Broker都有可能当选Controller;
> 但是Controller角色除了是一个正常的Broker外,还承担着Controller角色的一些任务;
> 具体情况 [【kafka源码】Controller启动过程以及选举流程源码分析]()
> 当这台Broker本身压力很大的情况下,又当选Controller让Broker压力更大了;
> 所以我们期望让Controller角色落在一些压力较小的Broker上;或者专门用一台机器用来当做Controller角色;
> 基于这么一个需求,我们内部就对引擎做了些改造,用于支持`Controller优先选举`


## 改造原理
> 在`/config`节点下新增了节点`/config/extension/candidates/ `;
> 将所有需要被优先选举的BrokerID存放到该节点下面;
> 例如：
> `/config/extension/candidates/0`
> ![在这里插入图片描述](https://img-blog.csdnimg.cn/20210625145023974.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3UwMTA2MzQwNjY=,size_16,color_FFFFFF,t_70)

当Controller发生重新选举的时候, 每个Broker都去抢占式写入`/controller`节点, 但是会先去节点`/config/extension/candidates/`节点获取所有子节点,获取到有一个BrokerID=0;  这个时候会判断一下是否跟自己的BrokerID相等; 不相等的话就`sleep 3秒` 钟; 这样的话,那么BrokerId=0这个Broker就会大概率当选Controller; 如果这个Broker挂掉了,那么其他Broker就可能会当选

<font color=red>PS: `/config/extension/candidates/` 节点下可以配置多个候选Controller </font>


## KM管理平台操作

![在这里插入图片描述](https://img-blog.csdnimg.cn/202106251511242.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3UwMTA2MzQwNjY=,size_16,color_FFFFFF,t_70)
