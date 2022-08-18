export interface getTopicListParams {
  clusterPhyId: number;
  dto: {
    metricLines: {
      aggType?: string;
      startTime: number;
      endTime: number;
      metricsNames: Array<string>;
      topNu: number;
    },
    metricPoints: {
      aggType?: string;
      startTime: number;
      endTime: number;
      metricsNames: Array<string>;
      topNu: number;
    },
    pageNo: number;
    pageSize: number;
    searchKeywords: string;
    showInternalTopics: boolean;
    sortField: string;
    sortType: string;
  }
}
