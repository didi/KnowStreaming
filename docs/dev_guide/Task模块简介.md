# Task模块简介

## 1、Task简介

在 KnowStreaming 中(下面简称KS)，Task模块主要是用于执行一些周期任务，包括Cluster、Broker、Topic等指标的定时采集，集群元数据定时更新至DB，集群状态的健康巡检等。在KS中，与Task模块相关的代码，我们都统一存放在km-task模块中。

Task模块是基于 LogiCommon 中的Logi-Job组件实现的任务周期执行，Logi-Job 的功能类似 XXX-Job，它是 XXX-Job 在 KnowStreaming 的内嵌实现，主要用于简化 KnowStreaming 的部署。
Logi-Job 的任务总共有两种执行模式，分别是：

+ 广播模式：同一KS集群下，同一任务周期中，所有KS主机都会执行该定时任务。
+ 抢占模式：同一KS集群下，同一任务周期中，仅有某一台KS主机会执行该任务。

KS集群范围定义：连接同一个DB，且application.yml中的spring.logi-job.app-name的名称一样的KS主机为同一KS集群。

## 2、使用指南

Task模块基于Logi-Job的广播模式与抢占模式，分别实现了任务的抢占执行、重复执行以及均衡执行，他们之间的差别是：

+ 抢占执行：同一个KS集群，同一个任务执行周期中，仅有一台KS主机执行该任务；
+ 重复执行：同一个KS集群，同一个任务执行周期中，所有KS主机都执行该任务。比如3台KS主机，3个Kafka集群，此时每台KS主机都会去采集这3个Kafka集群的指标；
+ 均衡执行：同一个KS集群，同一个任务执行周期中，每台KS主机仅执行该任务的一部分，所有的KS主机共同协作完成了任务。比如3台KS主机，3个Kafka集群，稳定运行情况下，每台KS主机将仅采集1个Kafka集群的指标，3台KS主机共同完成3个Kafka集群指标的采集。

下面我们看一下具体例子。

### 2.1、抢占模式——抢占执行

功能说明：

+ 同一个KS集群，同一个任务执行周期中，仅有一台KS主机执行该任务。

代码例子：

```java
// 1、实现Job接口，重写excute方法； 
// 2、在类上添加@Task注解，并且配置好信息，指定为随机抢占模式； 
// 效果：KS集群中，每5秒，会有一台KS主机输出 "测试定时任务运行中"；
@Task(name = "TestJob",
      description = "测试定时任务",
      cron = "*/5 * * * * ?",
      autoRegister = true,
      consensual = ConsensualEnum.RANDOM, // 这里一定要设置为RANDOM        
      timeout = 6 * 60) 
public class TestJob implements Job {   

  @Override    
  public TaskResult execute(JobContext jobContext) throws Exception {  
  
    System.out.println("测试定时任务运行中");        
    return new TaskResult();    
    
  } 
  
}
```



### 2.2、广播模式——重复执行

功能说明：

+ 同一个KS集群，同一个任务执行周期中，所有KS主机都执行该任务。比如3台KS主机，3个Kafka集群，此时每台KS主机都会去重复采集这3个Kafka集群的指标。 

代码例子：

```java
// 1、实现Job接口，重写excute方法； 
// 2、在类上添加@Task注解，并且配置好信息，指定为广播抢占模式； 
// 效果：KS集群中，每5秒，每台KS主机都会输出 "测试定时任务运行中"； 
@Task(name = "TestJob",        
      description = "测试定时任务",
      cron = "*/5 * * * * ?",
      autoRegister = true,
      consensual = ConsensualEnum.BROADCAST, // 这里一定要设置为BROADCAST
      timeout = 6 * 60) 
public class TestJob implements Job {   

  @Override    
  public TaskResult execute(JobContext jobContext) throws Exception { 
  
    System.out.println("测试定时任务运行中");        
    return new TaskResult();    
    
    } 
    
}
```



### 2.3、广播模式——均衡执行

功能说明：

+ 同一个KS集群，同一个任务执行周期中，每台KS主机仅执行该任务的一部分，所有的KS主机共同协作完成了任务。比如3台KS主机，3个Kafka集群，稳定运行情况下，每台KS主机将仅采集1个Kafka集群的指标，3台KS主机共同完成3个Kafka集群指标的采集。

代码例子：

+ 该模式有点特殊，是KS基于Logi-Job的广播模式，做的一个扩展，以下为一个使用例子：

```java
// 1、继承AbstractClusterPhyDispatchTask，实现processSubTask方法； 
// 2、在类上添加@Task注解，并且配置好信息，指定为广播模式； 
// 效果：在本样例中，每隔1分钟ks会将所有的kafka集群列表在ks集群主机内均衡拆分，每台主机会将分发到自身的Kafka集群依次执行processSubTask方法,实现KS集群的任务协同处理。 
@Task(name = "kmJobTask",
      description = "km job 模块调度执行任务",
      cron = "0 0/1 * * * ? *",
      autoRegister = true,
      consensual = ConsensualEnum.BROADCAST,
      timeout = 6 * 60) 
public class KMJobTask extends AbstractClusterPhyDispatchTask {   
  
    @Autowired    
    private JobService jobService;     
  
    @Override    
    protected TaskResult processSubTask(ClusterPhy clusterPhy, long triggerTimeUnitMs) throws Exception {
          jobService.scheduleJobByClusterId(clusterPhy.getId());        
          return TaskResult.SUCCESS;    
    }
}
```



## 3、原理简介

### 3.1、Task注解说明

```java
public @interface Task {
    String name() default ""; //任务名称
    String description() default ""; //任务描述
    String owner() default "system";  //拥有者     
    String cron() default ""; //定时执行的时间策略     
    int retryTimes() default 0; //失败以后所能重试的最大次数 
    long timeout() default 0; //在超时时间里重试  
    //是否自动注册任务到数据库中  
    //如果设置为false，需要手动去数据库km_task表注册定时任务信息。数据库记录和@Task注解缺一不可
    boolean autoRegister() default false;   
    //执行模式：广播、随机抢占  
    //广播模式：同一集群下的所有服务器都会执行该定时任务  
    //随机抢占模式：同一集群下随机一台服务器执行该任务   
    ConsensualEnum consensual() default ConsensualEnum.RANDOM; 
  }
```

### 3.2、数据库表介绍

+ logi_task：记录项目中的定时任务信息，一个定时任务对应一条记录。
+ logi_job：具体任务执行信息。
+ logi_job_log：定时任务的执行日志。
+ logi_worker：记录机器信息，实现集群控制。

### 3.3、均衡执行简介

#### 3.3.1、类关系图

这里以KMJobTask为例，简单介绍KM中的定时任务实现逻辑。

​        ![img](http://img-ys011.didistatic.com/static/dc2img/do1_knC85EtQ8Vbn1BcBzcjz)   

+ Job：使用logi组件实现定时任务，必须实现该接口。
+ Comparable & EntufyIdInterface：比较接口，实现任务的排序逻辑。
+ AbstractDispatchTask：实现广播模式下，任务的均衡分发。
+ AbstractClusterPhyDispatchTask：对分发到当前服务器的集群列表进行枚举。
+ KMJobTask:实现对单个集群的定时任务处理。

#### 3.3.2、关键类代码

+ **AbstractDispatchTask类**

```java
// 实现Job接口的抽象类，进行任务的负载均衡执行 
public abstract class AbstractDispatchTask<E extends Comparable & EntifyIdInterface> implements Job { 
  
  // 罗列所有的任务    
  protected abstract List<E> listAllTasks(); 
  
  // 执行被分配给该KS主机的任务    
  protected abstract TaskResult processTask(List<E> subTaskList, long triggerTimeUnitMs);   
  
  // 被Logi-Job触发执行该方法    
  // 该方法进行任务的分配    
  @Override    
  public TaskResult execute(JobContext jobContext) {        
    try {            
      
      long triggerTimeUnitMs = System.currentTimeMillis();  
      
      // 获取所有的任务            
      List<E> allTaskList = this.listAllTasks();
      
      // 计算当前KS机器需要执行的任务            
      List<E> subTaskList = this.selectTask(allTaskList, jobContext.getAllWorkerCodes(), jobContext.getCurrentWorkerCode()); 
      
      // 进行任务处理           
      return this.processTask(subTaskList, triggerTimeUnitMs);        
    } catch (Exception e) {           
      // ...       
    }  
  }
}
```

+ **AbstractClusterPhyDispatchTask类**

```java
// 继承AbstractDispatchTask的抽象类，对Kafka集群进行负载均衡执行 
public abstract class AbstractClusterPhyDispatchTask extends AbstractDispatchTask<ClusterPhy> {

  // 执行被分配的任务，具体由子类实现    
  protected abstract TaskResult processSubTask(ClusterPhy clusterPhy, long triggerTimeUnitMs) throws Exception;     	
  
  // 返回所有的Kafka集群   
  @Override    
  public List<ClusterPhy> listAllTasks() {        
    return clusterPhyService.listAllClusters();   
  }     
  
  // 执行被分配给该KS主机的Kafka集群任务   
  @Override   
  public TaskResult processTask(List<ClusterPhy> subTaskList, long triggerTimeUnitMs) {        // ...     }
  
}   
```

+ **KMJobTask类**

```java
// 加上@Task注解，并配置任务执行信息 
@Task(name = "kmJobTask",   
      description = "km job 模块调度执行任务", 
      cron = "0 0/1 * * * ? *",   
      autoRegister = true,    
      consensual = ConsensualEnum.BROADCAST,  
      timeout = 6 * 60)
// 继承AbstractClusterPhyDispatchTask类 
public class KMJobTask extends AbstractClusterPhyDispatchTask {  

	@Autowired   
        private JobService jobService;  
        
        // 执行该Kafka集群的Job模块的任务  
        @Override  
        protected TaskResult processSubTask(ClusterPhy clusterPhy, long triggerTimeUnitMs) throws Exception {        
          jobService.scheduleJobByClusterId(clusterPhy.getId());   
          return TaskResult.SUCCESS;  
        }
}
```

#### 3.3.3、均衡执行总结

均衡执行的实现原理总结起来就是以下几点：

+ Logi-Job设置为广播模式，触发所有的KS主机执行任务；
+ 每台KS主机，被触发执行后，按照统一的规则，对任务列表，KS集群主机列表进行排序。然后按照顺序将任务列表均衡的分配给排序后的KS集群主机。KS集群稳定运行情况下，这一步保证了每台KS主机之间分配到的任务列表不重复，不丢失。
+ 最后每台KS主机，执行被分配到的任务。

## 4、注意事项

+ 不能100%保证任务在一个周期内，且仅且执行一次，可能出现重复执行或丢失的情况，所以必须严格是且仅且执行一次的任务，不建议基于Logi-Job进行任务控制。
+ 尽量让Logi-Job仅负责任务的触发，后续的执行建议放到自己创建的线程池中进行。
