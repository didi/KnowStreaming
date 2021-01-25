import { observable, action } from 'mobx';
import { getClusterMetaTopics, getClusters, getAllClusters, getClusterModes, applyOrder, getClusterComboList, getClusterBasicInfo, getClusterDetailRealTime, getClusterDetailMetrice, getClusterDetailTopics, getClusterDetailBroker, getClusterDetailThrottles } from 'lib/api';
import { IClusterData, IConfigInfo, IOrderParams, IBasicInfo, IClusterReal, IClusterMetrics, IOptionType, IClusterTopics, IBrokerData, IThrottles } from 'types/base-type';
import { getClusterMetricOption } from 'lib/line-charts-config';
import { timestore } from './time';

class Cluster {
  @observable
  public loading: boolean = false;

  @observable
  public realLoading: boolean = false;

  @observable
  public filterLoading: boolean = false;

  @observable
  public clusterData: IClusterData[] = [];

  @observable
  public selectData: IClusterData[] = [{
    value: -1,
    label: '所有集群',
  } as IClusterData,
  ];

  @observable
  public allData: IClusterData[] = [];

  @observable
  public selectAllData: IClusterData[] = [{
    value: -1,
    label: '所有集群',
  } as IClusterData,
  ];

  @observable
  public clusterComboList: IConfigInfo[] = [];

  @observable
  public active: number = -1;

  @observable
  public allActive: number = -1;

  @observable
  public clusterModes: IConfigInfo[] = [];

  @observable
  public clusterMode: IConfigInfo[] = [];

  @observable
  public basicInfo: IBasicInfo = null;

  @observable
  public clusterRealData: IClusterReal = null;

  @observable
  public clusterMetrics: IClusterMetrics[] = [];

  @observable
  public type: IOptionType = 'byteIn/byteOut';

  @observable
  public clusterTopics: IClusterTopics[] = [];

  @observable
  public clusterBroker: IBrokerData[] = [];

  @observable
  public clustersThrottles: IThrottles[] = [];

  @observable
  public clusterMetaTopics: IClusterTopics[] = [];

  @action.bound
  public setLoading(value: boolean) {
    this.loading = value;
  }

  @action.bound
  public setRealLoading(value: boolean) {
    this.realLoading = value;
  }

  @action.bound
  public setFilterLoading(value: boolean) {
    this.filterLoading = value;
  }

  @action.bound
  public setData(data: IClusterData[]) {
    data = data.map(item => {
      return {
        ...item,
        label: `${item.clusterName}${item.description ? '（' + item.description + '）' : ''}`,
        value: item.clusterId,
      };
    }) || [];
    this.selectData.push(...data);
    this.clusterData = data;
    this.setLoading(false);
  }

  @action.bound
  public changeCluster(data: number) {
    this.active = data;
  }

  @action.bound
  public setAllData(data: IClusterData[]) {
    data = data.map(item => {
      return {
        ...item,
        label: `${item.clusterName}${item.description ? '（' + item.description + '）' : ''}`,
        value: item.clusterId,
      };
    }) || [];
    this.selectAllData.push(...data);
    this.allData = data;
    this.setLoading(false);
  }

  @action.bound
  public changeAllCluster(data: number) {
    this.allActive = data;
  }

  @action.bound
  public setClusterCombos(data: IConfigInfo[]) {
    this.clusterComboList = data || [];
    this.clusterComboList = this.clusterComboList.map(item => {
      return {
        ...item,
        label: item.message,
        value: item.code,
      };
    });
  }

  @action.bound
  public setClusterModes(data: IConfigInfo[]) {
    this.clusterModes = data;
    this.clusterModes = this.clusterModes.map(item => {
      return {
        ...item,
        label: item.message,
        value: item.code,
      };
    });
    this.clusterMode = (this.clusterModes && this.clusterModes.filter(ele => ele.code !== 0)) || []; // 去除 0 共享集群
  }

  @action.bound
  public setClusterBasicInfo(data: IBasicInfo) {
    this.basicInfo = data;
  }

  @action.bound
  public setClusterDetailRealTime(data: IClusterReal) {
    this.clusterRealData = data;
    this.setRealLoading(false);
  }

  @action.bound
  public changeType(type: IOptionType) {
    this.type = type;
    return getClusterMetricOption(type, this.clusterMetrics);
  }

  @action.bound
  public setClusterDetailMetrice(data: IClusterMetrics[]) {
    this.clusterMetrics = data;
    return this.changeType(this.type);
  }

  @action.bound
  public setClusterDetailTopics(data: IClusterTopics[]) {
    this.clusterTopics = data;
    this.setLoading(false);
  }

  @action.bound
  public setClusterDetailBroker(data: IBrokerData[]) {
    this.clusterBroker = data ? data.map((item, index) => {
      item.key = index;
      return item;
    }) : [];
    this.setLoading(false);
  }

  @action.bound
  public setClusterDetailThrottles(data: IThrottles[]) {
    this.clustersThrottles = data ? data.map((item, index) => {
      item.key = index;
      return item;
    }) : [];
  }

  @action.bound
  public setClusterTopicsMeta(data: IClusterTopics[]) {
    this.clusterMetaTopics = data.map((item, index) => {
      item.key = index;
      return item;
    }) || [];
    const obj = {} as any;
    this.clusterMetaTopics = this.clusterMetaTopics.reduce((current, next) => {
      if (!obj[next.topicName]) {
        obj[next.topicName] = true;
        current.push(next);
      }
      return current;
    }, []);
    return this.clusterMetaTopics;
  }

  public getClusters() {
    this.setLoading(true);
    getClusters().then(this.setData);
  }

  public getAllClusters() {
    this.setLoading(true);
    getAllClusters().then(this.setAllData);
  }

  public getClusterComboList() {
    getClusterComboList().then(this.setClusterCombos);
  }

  public getClusterModes() {
    return getClusterModes().then(this.setClusterModes);
  }

  public applyCluster(params: IOrderParams) {
    return applyOrder(params);
  }

  public applyClusterOffline(params: IOrderParams) {
    return applyOrder(params).then(() => {
      this.getClusters();
    });
  }

  public applyCpacity(params: IOrderParams) {
    return applyOrder(params);
  }

  public getClusterBasicInfo(clusterId: number) {
    return getClusterBasicInfo(clusterId).then(this.setClusterBasicInfo);
  }

  public getClusterDetailRealTime(clusterId: number) {
    this.setRealLoading(true);
    return getClusterDetailRealTime(clusterId).then(this.setClusterDetailRealTime);
  }

  public getClusterDetailMetrice(clusterId: number) {
    return getClusterDetailMetrice(clusterId,
      timestore.startTime.format('x'),
      timestore.endTime.format('x')).then(this.setClusterDetailMetrice);
  }

  public getClusterDetailTopics(clusterId: number) {
    this.setLoading(true);
    return getClusterDetailTopics(clusterId).then(this.setClusterDetailTopics);
  }

  public getClusterDetailBroker(clusterId: number) {
    this.setLoading(true);
    return getClusterDetailBroker(clusterId).then(this.setClusterDetailBroker);
  }

  public getClusterDetailThrottles(clusterId: number) {
    return getClusterDetailThrottles(clusterId).then(this.setClusterDetailThrottles);
  }

  public getClusterMetaTopics(clusterId: number) {
    this.setFilterLoading(true);
    return getClusterMetaTopics(clusterId).then(this.setClusterTopicsMeta).finally(() => this.setFilterLoading(false));
  }

}

export const cluster = new Cluster();
