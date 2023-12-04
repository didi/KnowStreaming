package com.xiaojukeji.know.streaming.km.testing.biz.impl;

import com.didiglobal.logi.log.ILog;
import com.didiglobal.logi.log.LogFactory;
import com.didiglobal.logi.security.common.dto.oplog.OplogDTO;
import com.xiaojukeji.know.streaming.km.common.annotations.enterprise.EnterpriseTesting;
import com.xiaojukeji.know.streaming.km.common.bean.dto.partition.PartitionOffsetDTO;
import com.xiaojukeji.know.streaming.km.common.bean.entity.cluster.ClusterPhy;
import com.xiaojukeji.know.streaming.km.common.bean.entity.offset.KSOffsetSpec;
import com.xiaojukeji.know.streaming.km.common.bean.entity.record.RecordHeaderKS;
import com.xiaojukeji.know.streaming.km.common.bean.entity.result.Result;
import com.xiaojukeji.know.streaming.km.common.bean.entity.result.ResultStatus;
import com.xiaojukeji.know.streaming.km.common.bean.entity.topic.Topic;
import com.xiaojukeji.know.streaming.km.common.bean.vo.topic.TopicRecordVO;
import com.xiaojukeji.know.streaming.km.common.constant.KafkaConstant;
import com.xiaojukeji.know.streaming.km.common.constant.MsgConstant;
import com.xiaojukeji.know.streaming.km.common.enums.operaterecord.ModuleEnum;
import com.xiaojukeji.know.streaming.km.common.enums.operaterecord.OperationEnum;
import com.xiaojukeji.know.streaming.km.common.exception.AdminOperateException;
import com.xiaojukeji.know.streaming.km.common.exception.NotExistException;
import com.xiaojukeji.know.streaming.km.common.utils.ConvertUtil;
import com.xiaojukeji.know.streaming.km.common.utils.ValidateUtils;
import com.xiaojukeji.know.streaming.km.core.service.cluster.ClusterPhyService;
import com.xiaojukeji.know.streaming.km.core.service.group.GroupService;
import com.xiaojukeji.know.streaming.km.core.service.oprecord.OpLogWrapService;
import com.xiaojukeji.know.streaming.km.core.service.partition.PartitionService;
import com.xiaojukeji.know.streaming.km.core.service.topic.TopicService;
import com.xiaojukeji.know.streaming.km.testing.biz.KafkaClientTestManager;
import com.xiaojukeji.know.streaming.km.testing.common.bean.dto.KafkaConsumerDTO;
import com.xiaojukeji.know.streaming.km.testing.common.bean.dto.KafkaConsumerFilterDTO;
import com.xiaojukeji.know.streaming.km.testing.common.bean.dto.KafkaConsumerStartFromDTO;
import com.xiaojukeji.know.streaming.km.testing.common.bean.dto.KafkaProducerDTO;
import com.xiaojukeji.know.streaming.km.testing.common.bean.vo.TestConsumerVO;
import com.xiaojukeji.know.streaming.km.testing.common.bean.vo.TestPartitionConsumedVO;
import com.xiaojukeji.know.streaming.km.testing.common.bean.vo.TestProducerVO;
import com.xiaojukeji.know.streaming.km.testing.common.enums.KafkaConsumerFilterEnum;
import com.xiaojukeji.know.streaming.km.testing.common.enums.KafkaConsumerStartFromEnum;
import org.apache.kafka.clients.CommonClientConfigs;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.header.Header;
import org.apache.kafka.common.header.internals.RecordHeader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.stream.Collectors;

@Component
@EnterpriseTesting
public class KafkaClientTestManagerImpl implements KafkaClientTestManager {
    private static final ILog log = LogFactory.getLog(KafkaClientTestManagerImpl.class);

    @Autowired
    private TopicService topicService;

    @Autowired
    private PartitionService partitionService;

    @Autowired
    private GroupService groupService;

    @Autowired
    private ClusterPhyService clusterPhyService;

    @Autowired
    private OpLogWrapService opLogWrapService;

    @Override
    public Result<TestConsumerVO> consumeTest(KafkaConsumerDTO dto, String operator) {
        if (ValidateUtils.anyNull(dto, operator)) {
            return Result.buildFrom(ResultStatus.PARAM_ILLEGAL);
        }

        // 获取集群信息
        ClusterPhy clusterPhy = clusterPhyService.getClusterByCluster(dto.getClusterId());
        if (clusterPhy == null) {
            return Result.buildFromRSAndMsg(ResultStatus.NOT_EXIST, MsgConstant.getClusterPhyNotExist(dto.getClusterId()));
        }

        // 检查start 和 filter两个参数是否合法
        Result<Void> rv = this.checkStartFromAndFilterLegal(dto.getStartFrom(), dto.getFilter());
        if (rv.failed()) {
            // 参数错误
            return Result.buildFromIgnoreData(rv);
        }

        KafkaConsumer<String, String> kafkaConsumer = null;
        try {
            // 获取消费测试开始消费的offset信息
            Result<List<PartitionOffsetDTO>> consumeStartOffsetResult = this.getConsumeStartOffset(dto.getClusterId(), dto.getTopicName(), dto.getStartFrom());
            if (consumeStartOffsetResult.failed()) {
                // 获取offset失败
                return Result.buildFromIgnoreData(consumeStartOffsetResult);
            }

            //获取topic的BeginOffset
            Result<Map<TopicPartition, Long>> partitionBeginOffsetMapResult = partitionService.getPartitionOffsetFromKafka(dto.getClusterId(), dto.getTopicName(), KSOffsetSpec.earliest());
            if (partitionBeginOffsetMapResult.failed()) {
                return Result.buildFromIgnoreData(partitionBeginOffsetMapResult);
            }

            //计算最终的开始offset
            consumeStartOffsetResult.getData().forEach(elem -> {
                long offset = Math.max(partitionBeginOffsetMapResult.getData().get(new TopicPartition(dto.getTopicName(), elem.getPartitionId())), elem.getOffset());
                elem.setOffset(offset);
            });

            // 获取Topic的EndOffset
            Result<Map<TopicPartition, Long>> partitionEndOffsetMapResult = partitionService.getPartitionOffsetFromKafka(dto.getClusterId(), dto.getTopicName(), KSOffsetSpec.latest());
            if (partitionEndOffsetMapResult.failed()) {
                return Result.buildFromIgnoreData(partitionEndOffsetMapResult);
            }

            // 创建Consumer客户端
            Properties properties = this.buildProperties(clusterPhy, dto.getClientProperties(), true);
            properties.put(ConsumerConfig.MAX_POLL_RECORDS_CONFIG, dto.getMaxRecords());
            kafkaConsumer = new KafkaConsumer<>(properties);

            // 消费数据
            List<ConsumerRecord> recordList = this.fetchData(
                    kafkaConsumer,
                    dto.getMaxDurationUnitMs(),
                    dto.getMaxRecords(),
                    dto.getTopicName(),
                    consumeStartOffsetResult.getData(),
                    partitionEndOffsetMapResult.getData()
            );

            // 进行数据组装
            Result<TestConsumerVO> voResult = Result.buildSuc(this.convert2TestConsumerVO(
                    dto.getTopicName(),
                    recordList,
                    partitionEndOffsetMapResult.getData(),
                    consumeStartOffsetResult.getData(),
                    dto.getFilter())
            );

            // 记录用户操作
            if (voResult.successful() && dto.getRecordOperate()) {
                opLogWrapService.saveOplogAndIgnoreException(new OplogDTO(
                        operator,
                        OperationEnum.SEARCH.getDesc(),
                        ModuleEnum.KAFKA_TOPIC_DATA.getDesc(),
                        dto.getTopicName(),
                        MsgConstant.getTopicBizStr(dto.getClusterId(), dto.getTopicName())
                ));
            }

            return voResult;
        } catch (NotExistException nee) {
            log.error("method=consumeTest||param={}||operator={}||errMsg=res not exist.", dto, operator, nee);

            return Result.buildFromRSAndMsg(ResultStatus.NOT_EXIST, nee.getMessage());
        } catch (Exception e) {
            log.error("method=consumeTest||param={}||operator={}||errMsg=exception.", dto, operator, e);

            return Result.buildFromRSAndMsg(ResultStatus.KAFKA_OPERATE_FAILED, e.getMessage());
        } finally {
            if (kafkaConsumer != null) {
                kafkaConsumer.close();
            }
        }
    }

    @Override
    public Result<List<TestProducerVO>> produceTest(@Validated KafkaProducerDTO dto, String operator) {
        if (ValidateUtils.anyNull(dto, operator)) {
            return Result.buildFrom(ResultStatus.PARAM_ILLEGAL);
        }

        // 获取集群信息
        ClusterPhy clusterPhy = clusterPhyService.getClusterByCluster(dto.getClusterId());
        if (clusterPhy == null) {
            return Result.buildFromRSAndMsg(ResultStatus.NOT_EXIST, MsgConstant.getClusterPhyNotExist(dto.getClusterId()));
        }

        // 内部Topic不允许生产
        if (KafkaConstant.KAFKA_INTERNAL_TOPICS.contains(dto.getTopicName())) {
            return Result.buildFromRSAndMsg(ResultStatus.PARAM_ILLEGAL, "内部Topic不允许进行生产测试");
        }

        KafkaProducer<String, String> kafkaProducer = null;
        try {
            // 获取Topic信息并检查分区信息是否合法
            Topic topic = topicService.getTopic(clusterPhy.getId(), dto.getTopicName());
            if (topic == null) {
                log.error("method=produceTest||param={}||operator={}||errMsg=res not exist.", dto, operator);

                return Result.buildFromRSAndMsg(ResultStatus.NOT_EXIST, MsgConstant.getTopicNotExist(clusterPhy.getId(), dto.getTopicName()));
            }

            if (!ValidateUtils.isEmptyList(dto.getPartitionIdList()) && dto.getPartitionIdList().stream().anyMatch(elem -> !topic.getPartitionMap().containsKey(elem))) {
                // 分区不存在
                return Result.buildFromRSAndMsg(ResultStatus.NOT_EXIST, MsgConstant.getPartitionNotExist(dto.getClusterId(), dto.getTopicName()));
            }

            // 创建生产客户端
            kafkaProducer = new KafkaProducer<>(this.buildProperties(clusterPhy, dto.getClientProperties(), false));

            // 进行数据生产
            Result<List<TestProducerVO>> listResult = this.sendData(kafkaProducer, dto);

            // 如果成功，并且需要进行记录，则记录操作
            if (listResult.successful() && dto.getRecordOperate()) {
                opLogWrapService.saveOplogAndIgnoreException(new OplogDTO(
                        operator,
                        OperationEnum.ADD.getDesc(),
                        ModuleEnum.KAFKA_TOPIC_DATA.getDesc(),
                        dto.getTopicName(),
                        MsgConstant.getTopicBizStr(dto.getClusterId(), dto.getTopicName())
                ));
            }

            // 返回操作结果
            return listResult;
        } catch (Exception e) {
            log.error("method=produceTest||param={}||operator={}||errMsg=exception!", dto, operator, e);

            return Result.buildFromRSAndMsg(ResultStatus.KAFKA_OPERATE_FAILED, e.getMessage());
        } finally {
            if (kafkaProducer != null) {
                kafkaProducer.close();
            }
        }
    }

    /**************************************************** private method ****************************************************/

    private Result<List<TestProducerVO>> sendData(KafkaProducer<String, String> kafkaProducer, KafkaProducerDTO dto) throws InterruptedException, ExecutionException {
        List<RecordHeader> headers = new ArrayList<>();
        if (dto.getRecordHeader() != null) {
            for (Map.Entry<Object, Object> entry: dto.getRecordHeader().entrySet()) {
                headers.add(new RecordHeader(entry.getKey().toString(), entry.getValue().toString().getBytes(StandardCharsets.UTF_8)));
            }
        }

        // 随机一个数，如果指定分区了，则从该随机数下标位置的分区开始生产
        int idx = new Random().nextInt(10000);

        long now = System.currentTimeMillis();
        List<Future<RecordMetadata>> futureList = new ArrayList<>();
        for (int i = 0; i < dto.getRecordCount(); ++i) {
            Integer partitionId = null;
            if (!ValidateUtils.isEmptyList(dto.getPartitionIdList())) {
                partitionId = dto.getPartitionIdList().get(idx % dto.getPartitionIdList().size());
                idx += 1;
            }
            if (headers.isEmpty()) {
                futureList.add(kafkaProducer.send(new ProducerRecord(dto.getTopicName(), partitionId, dto.getRecordKey(), dto.getRecordValue())));
            } else {
                futureList.add(kafkaProducer.send(new ProducerRecord(dto.getTopicName(), partitionId, dto.getRecordKey(), dto.getRecordValue(), headers)));
            }
        }

        kafkaProducer.flush();

        List<TestProducerVO> voList = new ArrayList<>();
        for (Future<RecordMetadata> metadataFuture: futureList) {
            RecordMetadata recordMetadata = metadataFuture.get();
            voList.add(new TestProducerVO(
                    recordMetadata.timestamp() - now,
                    recordMetadata.partition(),
                    recordMetadata.offset() == -1? null: recordMetadata.offset(), // 如果返回为-1，则设置为null
                    recordMetadata.timestamp())
            );
        }

        return Result.buildSuc(voList);
    }

    private Properties buildProperties(ClusterPhy clusterPhy, Properties customProperties, boolean isConsume) {
        Properties properties = ConvertUtil.str2ObjByJson(clusterPhy.getClientProperties(), Properties.class);
        if (properties == null) {
            properties = new Properties();
        }
        properties.put(CommonClientConfigs.BOOTSTRAP_SERVERS_CONFIG, clusterPhy.getBootstrapServers());
        if (isConsume) {
            // 反序列化
            properties.setProperty(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringDeserializer");
            properties.setProperty(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringDeserializer");

            // 默认禁止提交offset
            properties.setProperty(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, "false");
        } else {
            // 序列化
            properties.setProperty(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringSerializer");
            properties.setProperty(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringSerializer");
        }

        if (customProperties != null) {
            properties.putAll(customProperties);
        }
        return properties;
    }

    private List<ConsumerRecord> fetchData(KafkaConsumer<String, String> kafkaConsumer,
                                           Long maxDurationUnitMs,
                                           Integer maxRecords,
                                           String topicName,
                                           List<PartitionOffsetDTO> dtoList,
                                           Map<TopicPartition, Long> endOffsetMap) {
        long now = System.currentTimeMillis();

        // 获取有数据的分区
        Map<Integer, Long> hasDataPartitionMap = dtoList
                .stream()
                .filter(elem -> {
                    Long endOffset = endOffsetMap.get(new TopicPartition(topicName, elem.getPartitionId()));
                    return endOffset != null && endOffset > elem.getOffset();
                })
                .collect(Collectors.toMap(PartitionOffsetDTO::getPartitionId, PartitionOffsetDTO::getOffset));

        if (ValidateUtils.isEmptyMap(hasDataPartitionMap)) {
            return new ArrayList<>();
        }

        // assign进行消费的分区
        kafkaConsumer.assign(
                hasDataPartitionMap.keySet().stream().map(elem -> new TopicPartition(topicName, elem)).collect(Collectors.toList())
        );

        // 设置消费的起始offset
        for (Map.Entry<Integer, Long> entry: hasDataPartitionMap.entrySet()) {
            kafkaConsumer.seek(new TopicPartition(topicName, entry.getKey()), entry.getValue());
        }

        List<ConsumerRecord> recordList = new ArrayList<>();
        while (System.currentTimeMillis() - now <= maxDurationUnitMs && recordList.size() < maxRecords) {
            for (ConsumerRecord consumerRecord: kafkaConsumer.poll(Duration.ofSeconds(3))) {
                recordList.add(consumerRecord);
                if (recordList.size() >= maxRecords) {
                    break;
                }
            }
        }
        return recordList;
    }

    private Result<List<PartitionOffsetDTO>> getConsumeStartOffset(Long clusterPhyId, String topicName, KafkaConsumerStartFromDTO startFromDTO) throws NotExistException, AdminOperateException {
        // 最新位置开始消费
        if (KafkaConsumerStartFromEnum.LATEST.getCode().equals(startFromDTO.getStartFromType())) {
            Result<Map<TopicPartition, Long>> offsetMapResult = partitionService.getPartitionOffsetFromKafka(clusterPhyId, topicName, KSOffsetSpec.latest());
            if (offsetMapResult.failed()) {
                return Result.buildFromIgnoreData(offsetMapResult);
            }

            return Result.buildSuc(offsetMapResult.getData().entrySet()
                    .stream()
                    .map(entry-> new PartitionOffsetDTO(entry.getKey().partition(), entry.getValue()))
                    .collect(Collectors.toList())
            );
        }

        // 最旧位置开始消费
        if (KafkaConsumerStartFromEnum.EARLIEST.getCode().equals(startFromDTO.getStartFromType())) {
            Result<Map<TopicPartition, Long>> offsetMapResult = partitionService.getPartitionOffsetFromKafka(clusterPhyId, topicName, KSOffsetSpec.earliest());
            if (offsetMapResult.failed()) {
                return Result.buildFromIgnoreData(offsetMapResult);
            }

            return Result.buildSuc(offsetMapResult.getData().entrySet()
                    .stream()
                    .map(entry-> new PartitionOffsetDTO(entry.getKey().partition(), entry.getValue()))
                    .collect(Collectors.toList())
            );
        }

        // 指定时间开始消费
        if (KafkaConsumerStartFromEnum.PRECISE_TIMESTAMP.getCode().equals(startFromDTO.getStartFromType())) {
            Result<Map<TopicPartition, Long>> offsetMapResult = partitionService.getPartitionOffsetFromKafka(clusterPhyId, topicName, KSOffsetSpec.forTimestamp(startFromDTO.getTimestampUnitMs()));
            if (offsetMapResult.failed()) {
                return Result.buildFromIgnoreData(offsetMapResult);
            }

            return Result.buildSuc(offsetMapResult.getData().entrySet()
                    .stream()
                    .map(entry-> new PartitionOffsetDTO(entry.getKey().partition(), Math.max(entry.getValue(), 0L)))
                    .collect(Collectors.toList())
            );
        }

        // 指定位置开始消费
        if (KafkaConsumerStartFromEnum.PRECISE_OFFSET.getCode().equals(startFromDTO.getStartFromType())) {
            return Result.buildSuc(startFromDTO.getOffsetList());
        }

        // 指定消费组进行消费
        if (KafkaConsumerStartFromEnum.CONSUMER_GROUP.getCode().equals(startFromDTO.getStartFromType())) {
            Map<TopicPartition, Long> offsetMap = groupService.getGroupOffsetFromKafka(clusterPhyId, startFromDTO.getConsumerGroup());
            return Result.buildSuc(offsetMap.entrySet()
                    .stream()
                    .filter(elem -> elem.getKey().topic().equals(topicName))
                    .map(entry-> new PartitionOffsetDTO(entry.getKey().partition(), entry.getValue()))
                    .collect(Collectors.toList())
            );
        }

        // 近X条数据开始消费
        if (KafkaConsumerStartFromEnum.LATEST_MINUS_X_OFFSET.getCode().equals(startFromDTO.getStartFromType())) {
            Result<Map<TopicPartition, Long>> offsetMapResult = partitionService.getPartitionOffsetFromKafka(clusterPhyId, topicName, KSOffsetSpec.latest());
            if (offsetMapResult.failed()) {
                return Result.buildFromIgnoreData(offsetMapResult);
            }

            return Result.buildSuc(offsetMapResult.getData().entrySet()
                    .stream()
                    .map(entry-> new PartitionOffsetDTO(entry.getKey().partition(), Math.max(0, entry.getValue() - startFromDTO.getLatestMinusX())))
                    .collect(Collectors.toList())
            );
        }

        return Result.buildFromRSAndMsg(ResultStatus.PARAM_ILLEGAL, "startFrom类型未知");
    }

    private Result<Void> checkStartFromAndFilterLegal(KafkaConsumerStartFromDTO startFrom, KafkaConsumerFilterDTO filter) {
        // 指定时间开始消费
        if (KafkaConsumerStartFromEnum.PRECISE_TIMESTAMP.getCode().equals(startFrom.getStartFromType()) && ValidateUtils.isNullOrLessThanZero(startFrom.getTimestampUnitMs())) {
            return Result.buildFromRSAndMsg(ResultStatus.PARAM_ILLEGAL, "指定时间消费必须设置时间参数");
        }

        // 指定位置开始消费
        if (KafkaConsumerStartFromEnum.PRECISE_OFFSET.getCode().equals(startFrom.getStartFromType()) && ValidateUtils.isEmptyList(startFrom.getOffsetList())) {
            return Result.buildFromRSAndMsg(ResultStatus.PARAM_ILLEGAL, "指定offset消费必须设置offset参数");
        }

        // 指定消费组进行消费
        if (KafkaConsumerStartFromEnum.CONSUMER_GROUP.getCode().equals(startFrom.getStartFromType()) && ValidateUtils.isBlank(startFrom.getConsumerGroup())) {
            return Result.buildFromRSAndMsg(ResultStatus.PARAM_ILLEGAL, "指定Group消费必须设置Group参数");
        }

        // 近X条数据开始消费
        if (KafkaConsumerStartFromEnum.LATEST_MINUS_X_OFFSET.getCode().equals(startFrom.getStartFromType()) && ValidateUtils.isNullOrLessThanZero(startFrom.getLatestMinusX())) {
            return Result.buildFromRSAndMsg(ResultStatus.PARAM_ILLEGAL, "指定近X条开始消费必须设置latestMinusX参数");
        }

        // 包含过滤
        if (KafkaConsumerFilterEnum.CONTAINS.getCode().equals(filter.getFilterType())
                && ValidateUtils.isBlank(filter.getFilterCompareKey()) && ValidateUtils.isBlank(filter.getFilterCompareValue())) {
            return Result.buildFromRSAndMsg(ResultStatus.PARAM_ILLEGAL, "包含的方式过滤，必须有过滤的key或value");
        }

        // key包含过滤
        if (KafkaConsumerFilterEnum.KEY_CONTAINS.getCode().equals(filter.getFilterType())
            && ValidateUtils.isBlank(filter.getFilterCompareKey())) {
            return Result.buildFromRSAndMsg(ResultStatus.PARAM_ILLEGAL, "key包含的方式过滤，必须有过滤的key");
        }

        // value包含过滤
        if (KafkaConsumerFilterEnum.VALUE_CONTAINS.getCode().equals(filter.getFilterType())
            && ValidateUtils.isBlank(filter.getFilterCompareValue())) {
            return Result.buildFromRSAndMsg(ResultStatus.PARAM_ILLEGAL, "value包含的方式过滤，必须有过滤的value");
        }

        // 不包含过滤
        if (KafkaConsumerFilterEnum.NOT_CONTAINS.getCode().equals(filter.getFilterType())
                && ValidateUtils.isBlank(filter.getFilterCompareKey()) && ValidateUtils.isBlank(filter.getFilterCompareValue())) {
            return Result.buildFromRSAndMsg(ResultStatus.PARAM_ILLEGAL, "不包含的方式过滤，必须有过滤的key或value");
        }

        // 等于过滤
        if (KafkaConsumerFilterEnum.EQUAL_SIZE.getCode().equals(filter.getFilterType()) && ValidateUtils.isNullOrLessThanZero(filter.getFilterCompareSizeUnitB())) {
            return Result.buildFromRSAndMsg(ResultStatus.PARAM_ILLEGAL, "等于Size的方式过滤，必须有过滤size大小参数");
        }

        // size大于过滤
        if (KafkaConsumerFilterEnum.ABOVE_SIZE.getCode().equals(filter.getFilterType()) && ValidateUtils.isNullOrLessThanZero(filter.getFilterCompareSizeUnitB())) {
            return Result.buildFromRSAndMsg(ResultStatus.PARAM_ILLEGAL, "大于Size的方式过滤，必须有过滤size大小参数");
        }

        // size小于过滤
        if (KafkaConsumerFilterEnum.UNDER_SIZE.getCode().equals(filter.getFilterType()) && ValidateUtils.isNullOrLessThanZero(filter.getFilterCompareSizeUnitB())) {
            return Result.buildFromRSAndMsg(ResultStatus.PARAM_ILLEGAL, "小于Size的方式过滤，必须有过滤size大小参数");
        }

        return Result.buildSuc();
    }

    private TestConsumerVO convert2TestConsumerVO(String topicName,
                                                  List<ConsumerRecord> recordList,
                                                  Map<TopicPartition, Long> partitionEndOffsetMap,
                                                  List<PartitionOffsetDTO> consumedStartFromOffsetList,
                                                  KafkaConsumerFilterDTO filter) {
        Map<Integer, TestPartitionConsumedVO> consumedVOMap = new HashMap<>();
        for (PartitionOffsetDTO partitionOffsetDTO: consumedStartFromOffsetList) {
            TestPartitionConsumedVO vo = consumedVOMap.get(partitionOffsetDTO.getPartitionId());
            if (vo == null) {
                vo = new TestPartitionConsumedVO();
                vo.setPartitionId(partitionOffsetDTO.getPartitionId());
                vo.setConsumedOffset(partitionOffsetDTO.getOffset());
                vo.setRecordSizeUnitB(0L);
                vo.setRecordCount(0);
                vo.setLogEndOffset(partitionEndOffsetMap.get(new TopicPartition(topicName, partitionOffsetDTO.getPartitionId())));
                consumedVOMap.put(partitionOffsetDTO.getPartitionId(), vo);
            }
        }

        TestConsumerVO vo = new TestConsumerVO();
        vo.setRecordList(new ArrayList<>());
        vo.setTotalRecordCount(0);
        vo.setTotalRecordSizeUnitB(0L);
        vo.setMaxRecordTimestampUnitMs(0L);

        for (ConsumerRecord record: recordList) {
            // 统计消费信息
            TestPartitionConsumedVO consumedVO = consumedVOMap.get(record.partition());
            if (consumedVO == null) {
                consumedVO = new TestPartitionConsumedVO();
                consumedVO.setRecordSizeUnitB(0L);
                consumedVO.setRecordCount(0);
                consumedVO.setPartitionId(record.partition());
                consumedVO.setLogEndOffset(partitionEndOffsetMap.get(new TopicPartition(topicName, record.partition())));

                consumedVOMap.put(record.partition(), consumedVO);
            }

            if (record.offset() > consumedVO.getConsumedOffset()) {
                consumedVO.setConsumedOffset(record.offset() + 1);
            }

            consumedVO.setRecordCount(consumedVO.getRecordCount() + 1);
            consumedVO.setRecordSizeUnitB(consumedVO.getRecordSizeUnitB() + record.serializedKeySize() + record.serializedValueSize());

            // 进行数据过滤
            if (this.checkMatchFilter(record, filter)) {
                vo.getRecordList().add(this.convert2TopicRecordVO(record));
            }

            vo.setMaxRecordTimestampUnitMs(Math.max(vo.getMaxRecordTimestampUnitMs(), record.timestamp()));
        }

        vo.setTotalRecordCount(vo.getRecordList().size());
        vo.setPartitionConsumedList(new ArrayList<>(consumedVOMap.values()));
        if (ValidateUtils.isEmptyList(vo.getPartitionConsumedList())) {
            vo.setTotalRecordSizeUnitB(0L);
        } else {
            vo.setTotalRecordSizeUnitB(vo.getPartitionConsumedList().stream().map(elem -> elem.getRecordSizeUnitB()).reduce(Long::sum).get());
        }
        return vo;
    }

    private boolean checkMatchFilter(ConsumerRecord consumerRecord, KafkaConsumerFilterDTO filter) {
        if (KafkaConsumerFilterEnum.NONE.getCode().equals(filter.getFilterType())) {
            return true;
        }

        // 包含过滤
        if (KafkaConsumerFilterEnum.CONTAINS.getCode().equals(filter.getFilterType())
                && (!ValidateUtils.isBlank(filter.getFilterCompareKey()) && consumerRecord.key() != null && consumerRecord.key().toString().contains(filter.getFilterCompareKey()))
                && (!ValidateUtils.isBlank(filter.getFilterCompareValue()) && consumerRecord.value() != null && consumerRecord.value().toString().contains(filter.getFilterCompareValue()))) {
            return true;
        }

        // key包含过滤
        if (KafkaConsumerFilterEnum.KEY_CONTAINS.getCode().equals(filter.getFilterType())
            && (!ValidateUtils.isBlank(filter.getFilterCompareKey()) && consumerRecord.key() != null && consumerRecord.key().toString().contains(filter.getFilterCompareKey()))) {
            return true;
        }

        // value包含过滤
        if (KafkaConsumerFilterEnum.VALUE_CONTAINS.getCode().equals(filter.getFilterType())
            && (!ValidateUtils.isBlank(filter.getFilterCompareValue()) && consumerRecord.value() != null && consumerRecord.value().toString().contains(filter.getFilterCompareValue()))) {
            return true;
        }

        // 不包含过滤
        if (KafkaConsumerFilterEnum.NOT_CONTAINS.getCode().equals(filter.getFilterType())
                && (!ValidateUtils.isBlank(filter.getFilterCompareKey()) && (consumerRecord.key() == null || !consumerRecord.key().toString().contains(filter.getFilterCompareKey())))
                && (!ValidateUtils.isBlank(filter.getFilterCompareValue()) && (consumerRecord.value() == null || !consumerRecord.value().toString().contains(filter.getFilterCompareValue())))) {
            return true;
        }

        // 等于过滤
        if (KafkaConsumerFilterEnum.EQUAL_SIZE.getCode().equals(filter.getFilterType())
                && (!ValidateUtils.isNullOrLessThanZero(filter.getFilterCompareSizeUnitB()) && (consumerRecord.serializedValueSize() + consumerRecord.serializedValueSize()) == filter.getFilterCompareSizeUnitB())) {
            return true;
        }

        // size大于过滤
        if (KafkaConsumerFilterEnum.ABOVE_SIZE.getCode().equals(filter.getFilterType())
                && (!ValidateUtils.isNullOrLessThanZero(filter.getFilterCompareSizeUnitB()) && (consumerRecord.serializedValueSize() + consumerRecord.serializedValueSize()) > filter.getFilterCompareSizeUnitB())) {
            return true;
        }

        // size小于过滤
        if (KafkaConsumerFilterEnum.ABOVE_SIZE.getCode().equals(filter.getFilterType())
                && (!ValidateUtils.isNullOrLessThanZero(filter.getFilterCompareSizeUnitB()) && (consumerRecord.serializedValueSize() + consumerRecord.serializedValueSize()) < filter.getFilterCompareSizeUnitB())) {
            return true;
        }

        return false;
    }

    private TopicRecordVO convert2TopicRecordVO(ConsumerRecord consumerRecord) {
        TopicRecordVO vo = new TopicRecordVO();
        vo.setTopicName(consumerRecord.topic());
        vo.setPartitionId(consumerRecord.partition());
        vo.setOffset(consumerRecord.offset());
        vo.setTimestampUnitMs(consumerRecord.timestamp());
        vo.setHeaderList(new ArrayList<>());
        for (Header header: consumerRecord.headers()) {
            vo.getHeaderList().add(new RecordHeaderKS(header.key(), new String(header.value(), StandardCharsets.UTF_8)));
        }

        vo.setKey(consumerRecord.key() == null ? null: consumerRecord.key().toString());
        vo.setValue(consumerRecord.value() == null? null: consumerRecord.value().toString());
        return vo;
    }
}
