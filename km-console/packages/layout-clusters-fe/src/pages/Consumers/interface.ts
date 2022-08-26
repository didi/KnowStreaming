export interface getOperatingStateListParams {
  fuzzySearchDTOList?: Array<{
    fieldName: string;
    fieldValue: string;
  }>;
  groupName?: string;
  pageNo: number;
  pageSize: number;
  searchKeywords?: string;
  topicName?: string;
}
export interface getTopicGroupMetricParams {
  clusterId: number;
  dto: {
    metricRealTimes: Array<{
      metricName: string;
      metricType: number;
    }>;
    pageNo?: number;
    pageSize?: number;
    searchKeywords?: string;
  };
  topicName: string;
  groupName: string;
}
export interface getTopicGroupMetricHistoryParams {
  clusterPhyId: number;
  param: {
    groupTopics: Array<{
      groupName: string;
      partitionIdList: Array<string>;
      topicName: string;
    }>;
    groups: Array<string>;
    metricsName: Array<string>;
    startTime: number;
    endTime: number;
  };
}
export interface getTopicGroupPartitionsHistoryParams {
  clusterPhyId: number;
  groupName: string;
  startTime: number;
  endTime: number;
}
export interface ResetGroupOffset {
  clusterId: number;
  groupName: string;
  offsetList: Array<{
    offset: number;
    partitionId: number;
  }>;
  resetType: number;
  timestamp: number;
  topicName: string;
}
