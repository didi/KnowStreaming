import { observable, action } from 'mobx';
import { INewBulidEnums, ILabelValue, IClusterReal, IOptionType, IClusterMetrics, IClusterTopics, IKafkaFiles, IMetaData, IConfigure, IConfigGateway, IBrokerData, IOffset, IController, IBrokersBasicInfo, IBrokersStatus, IBrokersTopics, IBrokersPartitions, IBrokersAnalysis, IAnalysisTopicVO, IBrokersMetadata, IBrokersRegions, IThrottles, ILogicalCluster, INewRegions, INewLogical, ITaskManage, IPartitionsLocation, ITaskType, ITasksEnums, ITasksMetaData, ITaskStatusDetails, IKafkaRoles, IEnumsMap, IStaffSummary, IBill, IBillDetail } from 'types/base-type';
import {
  deleteCluster,
  getBasicInfo,
  getClusterRealTime,
  getClusterMetrice,
  getClusterTopics,
  getTopicsBasicInfo,
  getTasksKafkaFiles,
  getMetaData,
  getOperationRecordData,
  getConfigure,
  addNewConfigure,
  editConfigure,
  addNewConfigGateway,
  deleteConfigure,
  getGatewayList,
  getGatewayType,
  editConfigGateway,
  deleteConfigGateway,
  getDataCenter,
  getClusterBroker,
  getClusterConsumer,
  getControllerHistory,
  getConsumerDetails,
  getBrokersBasicInfo,
  getPeakFlowStatus,
  getBrokersStatus,
  getBrokersMetrics,
  getBrokersTopics,
  getBrokersPartitions,
  getBrokersAnalysis,
  getBrokersMetricsHistory,
  getBrokersMetadata,
  getBrokersRegions,
  addNewRegions,
  editRegions,
  getLogicalClusters,
  queryLogicalClusters,
  createLogicalClusters,
  editLogicalClusters,
  deteleLogicalClusters,
  getClustersThrottles,
  getTaskManagement,
  getPartitionsLocation,
  getClusterTasksEnums,
  getTasksMetadata,
  getSubtasksStatus,
  getClusterTaskLog,
  newlyBuildEcmTasks,
  getConfigsTaskStatus,
  getConfigsKafkaRoles,
  deteleClusterBrokers,
  getStaffSummary,
  getBillStaffSummary,
  getBillStaffDetail,
  getCandidateController,
  addCandidateController,
  deleteCandidateCancel
  } from 'lib/api';
import { getControlMetricOption, getClusterMetricOption } from 'lib/line-charts-config';

import { copyValueMap } from 'constants/status-map';
import { getPieChartOption } from 'lib/bar-pie-config';
import { getBillBarOption } from 'lib/bar-pie-config';
import { transBToMB } from 'lib/utils';

import moment from 'moment';
import { timestore } from './time';
import { message } from 'component/antd';

class Admin {
  @observable
  public loading: boolean = false;

  @observable
  public realClusterLoading: boolean = true;

  @observable
  public realBrokerLoading: boolean = false;

  @observable
  public basicInfo: IMetaData = null;

  @observable
  public clusterRealData: IClusterReal = null;

  @observable
  public clusterMetrics: IClusterMetrics[] = [];

  @observable
  public clusterTopics: IClusterTopics[] = [];

  @observable
  public topicsBasic: IClusterTopics = null;

  @observable
  public packageList = [] as IKafkaFiles[];

  @observable
  public serverPropertiesList = [] as IKafkaFiles[];

  @observable
  public metaList: IMetaData[] = [];

  @observable
  public oRList: any[] = [];

  @observable
  public oRparams:any={
    moduleId:0
  };

  @observable
  public configureList: IConfigure[] = [];

  @observable
  public configGatewayList: IConfigGateway[] = [];

  @observable
  public gatewayType: [];

  @observable
  public dataCenterList: string[] = [];

  @observable
  public clusterBroker: IBrokerData[] = [];

  @observable
  public consumerData: IOffset[] = [];

  @observable
  public brokersBasicInfo: IBrokersBasicInfo;

  @observable
  public peakFlowStatusList: IEnumsMap[];

  @observable
  public peakValueMap = [] as string[];

  @observable
  public bytesInStatus: number[];

  @observable
  public replicaStatus: number[];

  @observable
  public peakValueList = [] as ILabelValue[];

  @observable
  public copyValueList = [] as ILabelValue[];

  @observable
  public brokersStatus: IBrokersStatus;

  @observable
  public brokersMetrics: IClusterReal;

  @observable
  public brokersMetricsHistory: IClusterMetrics[];

  @observable
  public brokersTopics: IBrokersTopics[];

  @observable
  public controllerHistory: IController[] = [];

  @observable
  public controllerCandidate: IController[] = [];

   @observable
  public filtercontrollerCandidate: string = '';
  
  @observable
  public brokersPartitions: IBrokersPartitions[] = [];

  @observable
  public brokersAnalysis: IBrokersAnalysis;

  @observable
  public brokersAnalysisTopic: IAnalysisTopicVO[] = [];

  @observable
  public brokersMetadata: IBrokersMetadata[] | any = [];

  @observable
  public brokersRegions: IBrokersRegions[] = [];

  @observable
  public logicalClusters: ILogicalCluster[] = [];

  @observable
  public queryLogical: ILogicalCluster;

  @observable
  public regionIdList: number[] = [];

  @observable
  public clustersThrottles: IThrottles[] = [];

  @observable
  public partitionsLocation: IPartitionsLocation[] = [];

  @observable
  public taskManagement: ITaskManage[] = [];

  @observable
  public taskType: ITaskType;

  @observable
  public tasksEnums: ITasksEnums[];

  @observable
  public configsTaskStatus: IEnumsMap[];

  @observable
  public staffSummary: IStaffSummary[];

  @observable
  public billStaff: IBill[];

  @observable
  public billDetailStaffData: IBillDetail[] = [];

  @observable
  public tasksMetaData: ITasksMetaData;

  @observable
  public taskStatusDetails: ITaskStatusDetails;

  @observable
  public clusterTaskLog: string;

  @observable
  public kafkaRoles: IKafkaRoles[];

  @observable
  public controlType: IOptionType = 'byteIn/byteOut';

  @observable
  public type: IOptionType = 'byteIn/byteOut';

  @observable
  public currentClusterId = null as number;

  @action.bound
  public setLoading(value: boolean) {
    this.loading = value;
  }

  @action.bound
  public getRealClusterLoading(value: boolean) {
    this.realClusterLoading = value;
  }

  @action.bound
  public setRealBrokerLoading(value: boolean) {
    this.realBrokerLoading = value;
  }

  @action.bound
  public setRegionIdList(value: number[]) {
    this.regionIdList = value;
  }

  @action.bound
  public setBasicInfo(data: IMetaData) {
    this.basicInfo = data;
  }

  @action.bound
  public setClusterRealTime(data: IClusterReal) {
    this.clusterRealData = data;
    this.getRealClusterLoading(false);
  }

  @action.bound
  public changeType(controlType: IOptionType) {
    this.controlType = controlType;
    return getControlMetricOption(controlType, this.clusterMetrics);
  }

  @action.bound
  public setClusterMetrice(data: IClusterMetrics[]) {
    this.clusterMetrics = data;
    return this.changeType(this.controlType);
  }

  @action.bound
  public setClusterTopics(data: IClusterTopics[]) {
    this.clusterTopics = data ? data.map((item, index) => {
      item.key = index;
      return item;
    }) : [];
    this.setLoading(false);
  }

  @action.bound
  public setTopicsBasicInfo(data: IClusterTopics) {
    return this.topicsBasic = data;
  }

  @action.bound
  public setTasksKafkaFiles(data: IKafkaFiles[]) {
    this.packageList = (data.filter(ele => ele.fileType === 0)).map((item => {
      return {
        ...item,
        label: item.fileName,
        value: item.fileName + ',' + item.fileMd5,
      };
    }));
    this.serverPropertiesList = (data.filter(ele => ele.fileType === 1)).map((item => {
      return {
        ...item,
        label: item.fileName,
        value: item.fileName + ',' + item.fileMd5,
      };
    }));
  }

  @action.bound
  public setMetaList(data: IMetaData[]) {
    this.setLoading(false);
    this.metaList = data ? data.map((item, index) => {
      item.key = index;
      return item;
    }) : [];
  }

  @action.bound
  public setOperationRecordList(data:any){
    this.setLoading(false);
    this.oRList = data ? data.map((item:any, index: any) => {
      item.key = index;
      return item;
    }) : [];
  }

  @action.bound
  public setConfigure(data: IConfigure[]) {
    this.configureList = data ? data.map((item, index) => {
      item.key = index;
      return item;
    }) : [];
  }

  @action.bound
  public setConfigGatewayList(data: IConfigGateway[]) {
    this.configGatewayList = data ? data.map((item, index) => {
      item.key = index;
      return item;
    }) : [];
  }

  @action.bound
  public setConfigGatewayType(data: any) {
    this.setLoading(false);
    this.gatewayType = data || [];
  }

  @action.bound
  public setDataCenter(data: string[]) {
    this.dataCenterList = data || [];
  }

  @action.bound
  public setClusterBroker(data: IBrokerData[]) {
    this.clusterBroker = data ? data.map((item, index) => {
      item.key = index;
      return item;
    }) : [];
  }

  @action.bound
  public setClusterConsumer(data: IOffset[]) {
    this.consumerData = data ? data.map((item, index) => {
      item.key = index;
      return item;
    }) : [];
  }

  @action.bound
  public setControllerHistory(data: IController[]) {
    this.controllerHistory = data ? data.map((item, index) => {
      item.key = index;
      return item;
    }) : [];
  }

  @action.bound
  public setCandidateController(data: IController[]) {
    this.controllerCandidate = data ? data.map((item, index) => {
      item.key = index;
      return item;
    }) : [];
    this.filtercontrollerCandidate = data?data.map((item,index)=>{
      return item.brokerId
    }).join(','):''
  }

  @action.bound
  public setBrokersBasicInfo(data: IBrokersBasicInfo) {
    this.brokersBasicInfo = data;
  }

  @action.bound
  public setPeakFlowStatus(data: IEnumsMap[]) {
    this.peakFlowStatusList = data;
    data.forEach((ele: IEnumsMap) => {
      this.peakValueMap.push(ele.message);
    });
  }

  @action.bound
  public setBrokersStatus(data: IBrokersStatus) {
    this.brokersStatus = data;
    this.bytesInStatus = data.brokerBytesInStatusList.slice(1);
    const peakValueMap = this.peakValueMap.slice(1);
    this.replicaStatus = data.brokerReplicaStatusList.slice(1);

    this.bytesInStatus.forEach((item, index) => {
      this.peakValueList.push({ name: peakValueMap[index], value: item });
    });
    this.replicaStatus.forEach((item, index) => {
      this.copyValueList.push({ name: copyValueMap[index], value: item });
    });
  }

  @action.bound
  public setBrokersMetrics(data: IClusterReal) {
    this.brokersMetrics = data;
    this.setRealBrokerLoading(false);
  }

  @action.bound
  public changeBrokerType(type: IOptionType) {
    this.type = type;
    return getClusterMetricOption(type, this.brokersMetricsHistory);
  }

  @action.bound
  public setBrokersMetricsHistory(data: IClusterMetrics[]) {
    this.brokersMetricsHistory = data;
    return this.changeBrokerType(this.type);
  }

  @action.bound
  public setBrokersHistoryList(data: IClusterMetrics[]) {
    this.brokersMetricsHistory = data;
  }

  @action.bound
  public setBrokersTopics(data: IBrokersTopics[]) {
    this.brokersTopics = data ? data.map((item, index) => {
      item.key = index;
      return item;
    }) : [];
  }

  @action.bound
  public setBrokersPartitions(data: IBrokersPartitions[]) {
    this.brokersPartitions = data ? data.map((item, index) => {
      item.key = index;
      return item;
    }) : [];
  }

  @action.bound
  public setBrokersAnalysis(data: IBrokersAnalysis) {
    data.bytesIn = transBToMB(data.bytesIn) as number;
    data.bytesOut = transBToMB(data.bytesOut) as number;
    this.brokersAnalysis = data;
    this.brokersAnalysisTopic = data.topicAnalysisVOList ?
      data.topicAnalysisVOList.map((item: IAnalysisTopicVO, index: number) => {
        item.key = index;
        return item;
      }) : [];
  }

  @action.bound
  public setBrokersMetadata(data: IBrokersMetadata[]|any) {
    this.brokersMetadata = data ? data.map((item:any, index:any) => {
      item.key = index;
      return {
        ...item,
        text: `${item.host} （BrokerID：${item.brokerId}）`,
        label: item.host,
        value: item.brokerId,
      };
    }) : [];
  }

  @action.bound
  public setBrokersRegions(data: IBrokersRegions[]) {
    const regions = data ? data.map((item, index) => {
      item.key = index;
      const capacity = transBToMB(item.capacity) as number;
      const estimateUsed = transBToMB(item.estimateUsed) as number;
      const surplus = capacity - estimateUsed;
      const text = `${item.name}(总容量${capacity} MB, 已使用${estimateUsed.toFixed(2)} MB, 剩余${surplus.toFixed(2)} MB)`;
      const isCapacityFull = item.status === 1 ? `<容量已满>${text}` : text;
      return {
        ...item,
        label: isCapacityFull,
        value: item.id,
        surplus,
      };
    }) : [];
    regions.sort((a, b) => a.surplus < b.surplus ? 1 : -1);
    const filterRegions = [] as any;
    if (this.regionIdList && this.regionIdList.length) {
      regions.forEach(ele => {
        this.regionIdList.forEach(t => {
          if (ele.id === t) {
            filterRegions.push(ele);
          }
        });
      });
    }
    this.brokersRegions = filterRegions.length ? filterRegions : regions;
    return this.brokersRegions;
  }

  @action.bound
  public setLogicalClusters(data: ILogicalCluster[]) {
    this.logicalClusters = data ? data.map((item, index) => {
      item.key = index;
      return item;
    }) : [];
  }

  @action.bound
  public setQueryLogical(data: ILogicalCluster) {
    this.queryLogical = data;
  }

  @action.bound
  public setClustersThrottles(data: IThrottles[]) {
    this.clustersThrottles = data ? data.map((item, index) => {
      item.key = index;
      return item;
    }) : [];
  }

  @action.bound
  public setPartitionsLocation(data: IPartitionsLocation[]) {
    this.partitionsLocation = data ? data.map((item, index) => {
      item.key = index;
      return item;
    }) : [];
  }

  @action.bound
  public setTaskManagement(data: ITaskManage[]) {
    this.taskManagement = data ? data.map((item, index) => {
      item.key = index;
      return item;
    }) : [];
  }

  @action.bound
  public setClusterTasksEnums(data: ITaskType) {
    this.tasksEnums = data.taskType.map(ele => {
      return {
        ...ele,
        label: ele.message,
        value: ele.name,
      };
    });
  }

  @action.bound
  public setTasksMetadata(data: ITasksMetaData) {
    this.tasksMetaData = data;
  }

  @action.bound
  public setSubtasksStatus(data: ITaskStatusDetails) {
    this.taskStatusDetails = data;
  }

  @action.bound
  public setClusterTaskLog(data: string) {
    this.clusterTaskLog = data;
  }

  @action.bound
  public setConfigsKafkaRoles(data: IKafkaRoles[]) {
    this.kafkaRoles = data;
  }

  @action.bound
  public setConfigsTaskStatus(data: IEnumsMap[]) {
    this.configsTaskStatus = data;
  }

  @action.bound
  public setStaffSummary(data: IStaffSummary[]) {
    this.staffSummary = data ? data.map((item, index) => {
      item.key = index;
      return item;
    }) : [];
  }

  @action.bound
  public setBillStaffSummary(data: IBill[] = []) {
    this.loading = false;
    if (data) {
      this.billStaff = data.map((item, index) => ({
        ...item,
        cost: +item.cost.toFixed(2),
        key: index,
      }));
    }

    return getBillBarOption(this.billStaff);
  }

  @action.bound
  public setBillStaffDetail(data: any) {
    this.loading = false;
    const billList = data.billList || [] as IBill[];
    this.billDetailStaffData = billList.map((item: IBill, index: number) => ({
      ...item,
      cost: +item.cost.toFixed(2),
      key: index,
    }));
  }

  public deleteCluster(clusterId: number) {
    return deleteCluster(clusterId).then(() => this.getMetaData(true));
  }

  public getPeakFlowChartData(value: ILabelValue[], map: string[]) {
    return getPieChartOption(value, map);
  }

  public getSideStatusChartData(value: ILabelValue[]) {
    return getPieChartOption(value, copyValueMap);
  }

  public getBasicInfo(clusterId: number) {
    return getBasicInfo(clusterId).then(this.setBasicInfo);
  }

  public getClusterRealTime(clusterId: number) {
    this.getRealClusterLoading(true);
    return getClusterRealTime(clusterId).then(this.setClusterRealTime);
  }

  public getClusterMetrice(clusterId: number) {
    return getClusterMetrice(clusterId,
      timestore.startTime.format('x'),
      timestore.endTime.format('x')).then(this.setClusterMetrice);
  }

  public getClusterTopics(clusterId: number) {
    this.setLoading(true);
    return getClusterTopics(clusterId).then(this.setClusterTopics);
  }

  public getTopicsBasicInfo(clusterId: number, topicName: string) {
    return getTopicsBasicInfo(clusterId, topicName).then(data => {
      return this.setTopicsBasicInfo(data);
    });
  }

  public getTasksKafkaFiles(clusterId?: any) {
    return getTasksKafkaFiles(clusterId || '').then(this.setTasksKafkaFiles);
  }

  public getMetaData(needDetail: boolean) {
    this.setLoading(true);
    getMetaData(needDetail).then(this.setMetaList);
  }

  public getOperationRecordData(params: any) {
    this.setLoading(true);
    this.oRparams = params
    getOperationRecordData(params).then(this.setOperationRecordList);
  }

  public getConfigure() {
    getConfigure().then(this.setConfigure);
  }

  public addNewConfigure(params: IConfigure) {
    return addNewConfigure(params).then(() => this.getConfigure());
  }

  public editConfigure(params: IConfigure) {
    return editConfigure(params).then(() => this.getConfigure());
  }

  public deleteConfigure(configKey: string) {
    deleteConfigure(configKey).then(() => this.getConfigure());
  }

  public getGatewayList() {
    getGatewayList().then(this.setConfigGatewayList);
  }

  public getGatewayType() {
    this.setLoading(true);
    getGatewayType().then(this.setConfigGatewayType);
  }

  public addNewConfigGateway(params: IConfigGateway) {
    return addNewConfigGateway(params).then(() => this.getGatewayList());
  }

  public editConfigGateway(params: IConfigGateway) {
    return editConfigGateway(params).then(() => this.getGatewayList());
  }

  public deleteConfigGateway(params: any) {
    deleteConfigGateway(params).then(() => {
      // message.success('删除成功')
      this.getGatewayList()
    });
  }

  public getDataCenter() {
    getDataCenter().then(this.setDataCenter);
  }

  public getClusterBroker(clusterId: number) {
    return getClusterBroker(clusterId).then(this.setClusterBroker);
  }

  public getClusterConsumer(clusterId: number) {
    return getClusterConsumer(clusterId).then(this.setClusterConsumer);
  }

  public getControllerHistory(clusterId: number) {
    return getControllerHistory(clusterId).then(this.setControllerHistory);
  }

  public getCandidateController(clusterId: number) {
    return getCandidateController(clusterId).then(data=>{
      return this.setCandidateController(data)
    });
  }

  public addCandidateController(clusterId: number, brokerIdList: any) {
    return addCandidateController({clusterId, brokerIdList}).then(()=>this.getCandidateController(clusterId));
  }

  public deleteCandidateCancel(clusterId: number, brokerIdList: any){
    return deleteCandidateCancel({clusterId, brokerIdList}).then(()=>this.getCandidateController(clusterId));
  }

  public getBrokersBasicInfo(clusterId: number, brokerId: number) {
    return getBrokersBasicInfo(clusterId, brokerId).then(this.setBrokersBasicInfo);
  }

  public getPeakFlowStatus() {
    return getPeakFlowStatus().then(this.setPeakFlowStatus);
  }

  public getBrokersStatus(clusterId: number) {
    return getBrokersStatus(clusterId).then(this.setBrokersStatus);
  }

  public getBrokersMetrics(clusterId: number, brokerId: number) {
    this.setRealBrokerLoading(true);
    return getBrokersMetrics(clusterId, brokerId).then(this.setBrokersMetrics);
  }

  public getBrokersMetricsHistory(clusterId: number, brokerId: number) {
    return getBrokersMetricsHistory(clusterId, brokerId,
      timestore.startTime.format('x'), timestore.endTime.format('x')).then(this.setBrokersMetricsHistory);
  }

  // tslint:disable-next-line:max-line-length
  public getBrokersHistoryList(clusterId: number, brokerId: number, startTime: string, endTime: string) {
    return getBrokersMetricsHistory(clusterId, brokerId, startTime, endTime).then(
      (data: IClusterMetrics[]) => this.setBrokersHistoryList(data));
  }

  public getBrokersTopics(clusterId: number, brokerId: number) {
    return getBrokersTopics(clusterId, brokerId).then(this.setBrokersTopics);
  }

  public getBrokersPartitions(clusterId: number, brokerId: number) {
    this.setRealBrokerLoading(true);
    return getBrokersPartitions(clusterId, brokerId).then(this.setBrokersPartitions).finally(() => this.setRealBrokerLoading(false));
  }

  public getBrokersAnalysis(clusterId: number, brokerId: number) {
    return getBrokersAnalysis(clusterId, brokerId).then(this.setBrokersAnalysis);
  }

  public getBrokersMetadata(clusterId: number) {
    return getBrokersMetadata(clusterId).then(this.setBrokersMetadata);
  }

  public getBrokersRegions(clusterId: number) {
    return getBrokersRegions(clusterId).then(this.setBrokersRegions);
  }

  public addNewRegions(clusterId: number, params: INewRegions) {
    return addNewRegions(params).then(() => this.getBrokersRegions(clusterId));
  }

  public editRegions(clusterId: number, params: INewRegions) {
    return editRegions(params).then(() => this.getBrokersRegions(clusterId));
  }

  public getLogicalClusters(clusterId: number) {
    return getLogicalClusters(clusterId).then(this.setLogicalClusters);
  }

  public queryLogicalClusters(clusterId: number) {
    return queryLogicalClusters(clusterId).then(this.setQueryLogical);
  }

  public createLogicalClusters(clusterId: number, params: INewLogical) {
    return createLogicalClusters(params).then(() => this.getLogicalClusters(clusterId));
  }

  public editLogicalClusters(clusterId: number, params: INewLogical) {
    return editLogicalClusters(params).then(() => this.getLogicalClusters(clusterId));
  }

  public deteleLogicalClusters(clusterId: number, id: number) {
    return deteleLogicalClusters(id).then(() => this.getLogicalClusters(clusterId));
  }

  public getClustersThrottles(clusterId: number) {
    return getClustersThrottles(clusterId).then(this.setClustersThrottles);
  }

  public getPartitionsLocation(clusterId: number, brokerId: number) {
    return getPartitionsLocation(clusterId, brokerId).then(this.setPartitionsLocation);
  }

  public getTaskManagement() {
    return getTaskManagement().then(this.setTaskManagement);
  }

  public getClusterTasksEnums() {
    return getClusterTasksEnums().then(this.setClusterTasksEnums);
  }

  public getTasksMetadata(taskId: number) {
    return getTasksMetadata(taskId).then(this.setTasksMetadata);
  }

  public getSubtasksStatus(taskId: number) {
    return getSubtasksStatus(taskId).then(this.setSubtasksStatus);
  }

  public getClusterTaskLog(taskId: number, hostname: string) {
    return getClusterTaskLog(taskId, hostname).then(this.setClusterTaskLog);
  }

  public getConfigsKafkaRoles() {
    return getConfigsKafkaRoles().then(this.setConfigsKafkaRoles);
  }

  public addMigrationTask(params: INewBulidEnums) {
    return newlyBuildEcmTasks(params).then(() => this.getTaskManagement());
  }

  public getConfigsTaskStatus() {
    return getConfigsTaskStatus().then(this.setConfigsTaskStatus);
  }

  public deteleClusterBrokers(clusterId: number, brokerId: number) {
    return deteleClusterBrokers(clusterId, brokerId).then(() => this.getClusterBroker(clusterId));
  }

  public getStaffSummary(timestamp: number) {
    return getStaffSummary(timestamp).then(this.setStaffSummary);
  }

  public getBillStaffList(username: string, startTime: number, endTime: number) {
    this.setLoading(true);
    return getBillStaffSummary(username, startTime, endTime).then(this.setBillStaffSummary);
  }

  public getBillDetailStaffList(username: string, timestamp: number) {
    this.setLoading(true);
    return getBillStaffDetail(username, timestamp).then(this.setBillStaffDetail);
  }
}

export const admin = new Admin();
