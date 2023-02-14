

![在这里插入图片描述](https://img-blog.csdnimg.cn/2021062418234358.jpg?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3UwMTA2MzQwNjY=,size_16,color_FFFFFF,t_70#pic_center)


### /cluster
#### /cluster/id 持久数据节点
>集群ID
>当第一台Broker启动的时候, 发现`/cluster/id`不存在,那么它就会把自己的`cluster.id`配置写入zk; 标记当前zk是属于集群哪个集群; 后面其他的Broker启动的时候会去获取该数据, 如果发现数据跟自己的配置不一致; 则抛出异常,加入的不是同一个集群;
>数据示例：`{"version":"1","id":"0"}`

### /controller_epoch 持久数据节点
> Controller选举次数;

### /Controller 临时数据节点

>当前Controller角色的BrokerId,数据示例:
>`{"version":1,"brokerid":0,"timestamp":"1624415590383"}`
>删除该节点立马触发重新选举

### /log_dir_event_notification
>zk的数据中有一个节点`/log_dir_event_notification/`，这是一个序列号持久节点
>这个节点在kafka中承担的作用是: 当某个Broker上的LogDir出现异常时(比如磁盘损坏,文件读写失败,等等异常): 向zk中谢增一个子节点`/log_dir_event_notification/log_dir_event_序列号` ；Controller监听到这个节点的变更之后,会向Brokers们发送`LeaderAndIsrRequest`请求; 然后做一些副本脱机的善后操作
详情请看 [【kafka源码】/log_dir_event_notification的LogDir脱机事件通知]()

### /isr_change_notification/log_dir_event_{序列号}
> 当Isr有变更的时候,会写入这个节点Controller监听变更

### /admin
#### /admin/delete_topics 待删除Topic
##### /admin/delete_topics/{topicName} 持久节点,待删除Topic
>存在此节点表示 当前Topic需要被删除


#### /admin/reassign_partitions 持久数据节点
>如果有此节点,表示当前正在进行数据迁移,里面的数据就是正在迁移的配置
>示例数据:  ![在这里插入图片描述](https://img-blog.csdnimg.cn/20210624183757750.png)


### /brokers
#### /brokers/seqid
>**`/brokers/seqid`: 全局序列号**
里面没有数据,主要是用了节点的`dataVersion`信息来当全局序列号
>
**在kafka中的作用: 自动生成BrokerId**
>主要是用来自动生成brokerId;
>一个集群如果特别大,配置brokerId的时候不能重复,一个个设置比较累; 可以让Broker自动生成BrokerId

`server.properties` 配置
```properties
## 设置Brokerid能够自动生成
broker.id.generation.enable=true
## 设置BrokerId<0 （如果>=0则以此配置为准）
broker.id=-1
## 自动生成配置的起始值
reserved.broker.max.id=20000
```
BrokerId计算方法
>brokerId = {reserved.broker.max.id} +` /brokers/seqid`.dataVersion
>
每次想要获取` /brokers/seqid`的dataVersion值的时候都是用 set方法,set的时候会返回version数据,并不是get；每次set这个节点数据,版本信息就会自增;所以就实现了全局自增ID了；


#### /brokers/ids/{id} 临时数据节点 : 在线BrokerID
> 在线的Broker都会在这里注册一个节点; 下线自动删除

####  /brokers/topics/{topicName}持久数据节点
>存储 topic的分区副本分配信息
>例如：`{"version":1,"partitions":{"0":[0]}}`
>
#####  /brokers/topics/{topicName}/{分区号}/state 持久数据节点
>存储指定分区的`leader`和`isr`等信息
>例如:`{"controller_epoch":203,"leader":0,"version":1,"leader_epoch":0,"isr":[0]}`



