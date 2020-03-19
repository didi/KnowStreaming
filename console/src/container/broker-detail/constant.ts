export  const MetricChartList =  [
  {
    value: 'requestHandlerIdlPercent',
    label: '请求处理器空闲百分比(%)',
  },
  {
    value: 'networkProcessorIdlPercent',
    label: '网络处理器空闲百分比(%)',
  },
  {
    value: 'requestQueueSize',
    label: '请求列表大小(个)',
  },
  {
    value: 'responseQueueSize',
    label: '响应列表大小(个)',
  },
  {
    value: 'logFlushTime',
    label: '刷日志时间(ms)',
  },
  {
    value: '',
    label: '',
  },
  {
    value: 'totalTimeProduceMean',
    label: 'produce请求时间-平均值(ms)',
  },
  {
    value: 'totalTimeFetchConsumerMean',
    label: 'fetch请求处理时间-平均值(ms)',
  },
  {
    value: 'totalTimeProduce99Th',
    label: 'produce请求时间-99分位(ms)',
  },
  {
    value: 'totalTimeFetchConsumer99Th',
    label: 'fetch请求处理时间-99分位(ms)',
  },
  {
    value: 'failProduceRequest',
    label: '每秒生产失败数(条／秒)',
  },
  {
    value: 'failFetchRequest',
    label: '每秒消费失败数(条／秒)',
  },
];

export const columsDefault = {
  leaderPartitionList: 'leaderPartitions:',
  followerPartitionIdList: 'followerPartitions:',
  notUnderReplicatedPartitionIdList: 'notUnderReplicatedPartitions:',
};

export const brokerMetrics = {
  bytesIn: 'Bytes In（MB/ 秒)',
  bytesOut: 'Bytes Out（MB/ 秒)',
  messagesIn: 'Messages In（条)',
  totalFetchRequests: 'Total Fetch Requests（QPS)',
  totalProduceRequests: 'Total Produce Requests（QPS)',
};
