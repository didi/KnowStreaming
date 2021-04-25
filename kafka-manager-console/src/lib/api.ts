import fetch, { formFetch } from './fetch';
import { IUploadFile, IUser, IQuotaModelItem, ILimitsItem, ITopic, IOrderParams, ISample, IMigration, IExecute, IEepand, IUtils, ITopicMetriceParams, IRegister, IEditTopic, IExpand, IDeleteTopic, INewRegions, INewLogical, IRebalance, INewBulidEnums, ITrigger, IApprovalOrder, IMonitorSilences, IConfigure, IConfigGateway, IBatchApproval } from 'types/base-type';
import { IRequestParams } from 'types/alarm';
import { apiCache } from 'lib/api-cache';

export const getRegionIdcs = () => {
  return fetch(`/normal/configs/idcs`);
};

export const getAccount = () => {
  return fetch(`/normal/accounts/account`);
};

/**
 * topic 相关接口
 * @see http://127.0.0.1:8080/swagger-ui.html#/
 */

export const getMytopics = () => {
  return fetch(`/normal/topics/mine`);
};

export const getExpiredTopics = () => {
  return fetch(`/normal/topics/expired`);
};

export const getBaseInfo = () => {
  return fetch(`/normal/topics/my-topics`);
};

export const getAllTopic = () => {
  return fetch(`/normal/topics`);
};

export const getAuthorities = (appId: string, clusterId: number, topicName: string) => {
  return fetch(`/normal/apps/${appId}/authorities?clusterId=${clusterId}&topicName=${topicName}`);
};

export const getQuotaQuery = (appId: string, clusterId: number, topicName: string) => {
  return fetch(`/normal/apps/${appId}/quotas?clusterId=${clusterId}&topicName=${topicName}`);
};

export const getTopicBroker = (clusterId: number, topicName: string) => {
  return fetch(`/${clusterId}/topics/${topicName}/brokers`);
};

export const getClusters = () => {
  return fetch(`/normal/clusters/basic-info`);
};

export const getAllClusters = () => {
  return fetch(`/normal/clusters/basic-info?all=true`);
};

export const getApply = () => {
  return fetch(`/normal/apps?status=1`);
};

// 配额
export const updateTopic = (params: IQuotaModelItem) => {
  return fetch('/normal/topics', {
    method: 'PUT',
    body: JSON.stringify(params),
  });
};

export const deferTopic = (params: ILimitsItem) => {
  return fetch('/normal/topics/expired', {
    method: 'PUT',
    body: JSON.stringify(params),
  });
};

// 详情
export const getTopicCompile = (params: ITopic) => {
  return fetch('/normal/topics', {
    method: 'PUT',
    body: JSON.stringify(params),
  });
};

export const getTopicSampling = (params: ISample, clusterId: number, topicName: string) => {
  return fetch(`/normal/${clusterId}/topics/${topicName}/sample`, {
    method: 'POST',
    body: JSON.stringify(params),
  });
};

export const getTopicBasicInfo = (clusterId: number, topicName: string) => {
  return fetch(`/normal/${clusterId}/topics/${topicName}/basic-info`);
};

export const getRealTimeTraffic = (clusterId: number, topicName: string) => {
  return fetch(`/normal/${clusterId}/topics/${topicName}/metrics`);
};

export const getRealConsume = (clusterId: number, topicName: string, percentile: string) => {
  return fetch(`/normal/${clusterId}/topics/${topicName}/request-time?percentile=${percentile}`);
};

export const getConnectionInfo = (clusterId: number, topicName: string, appId?: string) => {
  return fetch(`/normal/${clusterId}/topics/${topicName}/connections?appId=${appId}`);
};

export const getConsumerGroups = (clusterId: number, topicName: string) => {
  return fetch(`/normal/${clusterId}/consumers/${topicName}/consumer-groups`);
};

export const getConsumeDetails = (clusterId: number, topicName: string, consumerGroup: string, location: string) => {
  return fetch(
    `/normal/${clusterId}/consumers/${consumerGroup}/topics/${topicName}/consume-details?location=${location}`);
};

export const getPartitionsInfo = (clusterId: number, topicName: string) => {
  return fetch(`/normal/${clusterId}/topics/${topicName}/partitions`);
};

export const getBrokerInfo = (clusterId: number, topicName: string) => {
  return fetch(`/rd/${clusterId}/topics/${topicName}/brokers`);
};

export const getAppsIdInfo = (clusterId: number, topicName: string) => {
  return fetch(`/normal/${clusterId}/topics/${topicName}/apps`);
};

export const getBillInfo = (clusterId: number, topicName: string, startTime: number, endTime: number) => {
  return fetch(`/normal/${clusterId}/topics/${topicName}/bills?startTime=${startTime}&endTime=${endTime}`);
};

export const resetOffset = (params: any) => {
  return fetch('/normal/consumers/offsets', {
    method: 'PUT',
    body: JSON.stringify(params),
  });
};

export const getTopicBusiness = (clusterId: number, topicName: string) => {
  return fetch(`/normal/${clusterId}/topics/${topicName}/business`);
};

// 集群mode
export const getClusterModes = () => {
  return fetch(`/normal/configs/cluster-modes`);
};

export const getClusterComboList = () => {
  return fetch('/normal/configs/cluster-combos');
};

// --------------------------
export const getConsumeInfo = (clusterId: number) => {
  return fetch(`/${clusterId}/consumers/consumer-groups`);
};

export const getConsumeGroup = (clusterId: number, consumerGroup: string, location: string) => {
  return fetch(`/${clusterId}/consumer/${consumerGroup}/topics?location=${location}`);
};

// 获取echarts
export const getTopicMetriceInfo = (parmas: ITopicMetriceParams) => {
  const { clusterId, topicName, startTime, endTime, appId } = parmas;
  // tslint:disable-next-line:max-line-length
  return fetch(`/normal/${clusterId}/topics/${topicName}/metrics-history?startTime=${startTime}&endTime=${endTime}&appId=${appId}`);
};

export const getTopicMetriceTake = (params: ITopicMetriceParams) => {
  const { clusterId, topicName, startTime, endTime } = params;
  // tslint:disable-next-line:max-line-length
  return fetch(`/normal/${clusterId}/topics/${topicName}/request-time-history?startTime=${startTime}&endTime=${endTime}`);
};
/**
 * mycluster
 */

export const getClusterBasicInfo = (clusterId: number) => {
  return fetch(`/normal/clusters/${clusterId}/basic-info`);
};

export const getClusterDetailRealTime = (clusterId: number) => {
  return fetch(`/normal/clusters/${clusterId}/metrics`);
};

export const getClusterDetailMetrice = (clusterId: number, startTime: string, endTime: string) => {
  return fetch(`/normal/clusters/${clusterId}/metrics-history?startTime=${startTime}&endTime=${endTime}`);
};

export const getClusterDetailTopics = (clusterId: number) => {
  return fetch(`/normal/clusters/${clusterId}/topics`);
};

export const getClusterMetaTopics = (clusterId: number) => {
  return fetch(`/normal/clusters/${clusterId}/topic-metadata`);
};

export const getClusterDetailBroker = (clusterId: number) => {
  return fetch(`/normal/clusters/${clusterId}/brokers`);
};

export const getClusterDetailThrottles = (clusterId: number) => {
  return fetch(`/normal/clusters/${clusterId}/throttles`);
};

/**
 * 获取员工信息
 */
export const getStaff = (keyword: string) => {
  const api = `/normal/accounts?keyWord=${keyword}`;
  const cacheData = apiCache.getDataFromCache(api);
  if (cacheData) {
    return new Promise(res => res(cacheData));
  }
  return fetch(api).then((data) => {
    apiCache.setCacheMap(api, data);
    return data;
  }).catch(() => {
    apiCache.deleteDataFromCache(api);
    return [];
  });
};

export const userLogin = (params: IUser) => {
  return fetch('/sso/login', {
    method: 'POST',
    body: JSON.stringify(params),
  });
};

/**
 * 应用相关接口
 */

export const getAppList = () => {
  return fetch(`/normal/apps`);
};

export const modfiyApplication = (params: any) => {
  return fetch('/normal/apps', {
    body: JSON.stringify(params),
    method: 'PUT',
  });
};

export const getAppDetail = (appId: string) => {
  return fetch(`/normal/apps/${appId}/basic-info`);
};

export const getAppTopicList = (appId: string, mine: boolean) => {
  return fetch(`/normal/apps/${appId}/topics?mine=${mine}`);
};

/**
 * 专家服务
 */

export const getHotTopics = () => {
  return fetch(`/op/expert/regions/hot-topics`);
};

export const getReassignTasks = () => {
  return fetch(`/op/reassign-tasks`);
};

export const getTaskTopicMetadata = (clusterId: number) => {
  return fetch(`/rd/clusters/${clusterId}/topic-metadata`);
};

export const getReassignTasksDetail = (taskId: number) => {
  return fetch(`/op/reassign-tasks/${taskId}/detail`);
};

export const getReassignTasksStatus = (taskId: number) => {
  return fetch(`/op/reassign-tasks/${taskId}/status`);
};

export const getInsufficientPartition = () => {
  return fetch(`/op/expert/topics/insufficient-partitions`);
};

export const createMigrationTask = (params: IMigration[]) => {
  return fetch(`/op/reassign-tasks`, {
    method: 'POST',
    body: JSON.stringify(params),
  });
};

export const getExecuteTask = (params: IExecute) => {
  return fetch(`/op/reassign-tasks`, {
    method: 'PUT',
    body: JSON.stringify(params),
  });
};

export const getExecuteSubTask = (params: IExecute) => {
  return fetch(`/op/reassign-tasks/sub-tasks`, {
    method: 'PUT',
    body: JSON.stringify(params),
  });
};

export const getExpandTopics = (params: IEepand[]) => {
  return fetch(`/op/utils/expand-partitions`, {
    method: 'PUT',
    body: JSON.stringify(params),
  });
};

export const getResourceManagement = () => {
  return fetch(`/op/expert/topics/expired`);
};

export const getUtilsTopics = (params: IUtils) => {
  return fetch(`/op/utils/topics`, {
    method: 'DELETE',
    body: JSON.stringify(params),
  });
};

export const getAnomalyFlow = (timestamp: number) => {
  return fetch(`/op/expert/topics/anomaly-flow?timestamp=${timestamp}`);
};

/**
 * 工单
 */

export const applyOrder = (params: IOrderParams) => {
  return fetch('/normal/orders', {
    method: 'POST',
    body: JSON.stringify(params),
  });
};

export const getAppsConnections = (appId: string) => {
  return fetch(`/normal/apps/${appId}/connections`);
};

export const getTopicAppQuota = (clusterId: number, topicName: string) => {
  return fetch(`/normal/${clusterId}/topics/${topicName}/my-apps`);
};

export const getBrokerMetadata = (clusterId: number) => {
  return fetch(`/normal/clusters/${clusterId}/broker-metadata`);
};

export const approvalOrder = (params: IApprovalOrder) => {
  return fetch(`/normal/orders`, {
    method: 'PUT',
    body: JSON.stringify(params),
  });
};

/**
 * 个人中心
 * @see http://127.0.0.1:8080/swagger-ui.html#!/OP4524037213333045620851255092147540REST41/getOrderAppDetailUsingGET
 */
export const getOrderTypeList = () => {
  return fetch('/normal/orders/type-enums');
};

// 我的申请
export const getApplyOrderList = (status: number) => {
  return fetch(`/normal/orders?status=${status === 2 ? '' : status}`);
};

export const getOrderDetail = (orderId: number) => {
  return fetch(`/normal/orders/${orderId}/detail`);
};

export const cancelOrder = (id: number) => {
  return fetch(`/normal/orders?id=${id}`, {
    method: 'DELETE',
  });
};

export const batchApprovalOrders = (params: IBatchApproval) => {
  return fetch(`/normal/orders/batch`, {
    method: 'PUT',
    body: JSON.stringify(params),
  });
};

// 我的审批
export const getApprovalOrderList = (status: number) => {
  return fetch(`/normal/approvals?status=${status === 2 ? '' : status}`);
};

export const getBrokerBasicInfo = (clusterId: number) => {
  return fetch(`/rd/clusters/${clusterId}/brokers/basic-info`);
};

// 我的账单

export const getBillList = (startTime: number, endTime: number) => {
  return fetch(`/normal/bills/staff-summary?startTime=${startTime}&endTime=${endTime}`);
};

export const getBillDetail = (timestamp: number) => {
  return fetch(`/normal/bills/staff-detail?timestamp=${timestamp}`);
};

/**
 * 运维管控
 */

export const getBillStaffSummary = (username: string, startTime: number, endTime: number) => {
  return fetch(`/rd/bills/${username}/staff-summary?startTime=${startTime}&endTime=${endTime}`);
};

export const getBillStaffDetail = (username: string, timestamp: number) => {
  return fetch(`/rd/bills/${username}/staff-detail?timestamp=${timestamp}`);
};

export const getTasksKafkaFiles = (clusterId: number) => {
  return fetch(`/op/cluster-tasks/kafka-files?clusterId=${clusterId}`);
};

export const getMetaData = (needDetail: boolean = true) => {
  return fetch(`/rd/clusters/basic-info?need-detail=${needDetail}`);
};

export const getOperationRecordData = (params: any) => {
  return fetch(`/rd/operate-record`,{
    method: 'POST',
    body: JSON.stringify(params),
  });
};

export const getConfigure = () => {
  return fetch(`/rd/configs`);
};

export const addNewConfigure = (params: IConfigure) => {
  return fetch(`/rd/configs`, {
    method: 'POST',
    body: JSON.stringify(params),
  });
};

export const editConfigure = (params: IConfigure) => {
  return fetch(`/rd/configs`, {
    method: 'PUT',
    body: JSON.stringify(params),
  });
};

export const deleteConfigure = (configKey: string) => {
  return fetch(`/rd/configs?config-key=${configKey}`, {
    method: 'DELETE',
  });
};

export const getGatewayList = () => {
  return fetch(`/rd/gateway-configs`);
};

export const getGatewayType = () => {
  return fetch(`/op/gateway-configs/type-enums`);
};

export const addNewConfigGateway = (params: IConfigGateway) => {
  return fetch(`/op/gateway-configs`, {
    method: 'POST',
    body: JSON.stringify(params),
  });
};

export const editConfigGateway = (params: IConfigGateway) => {
  return fetch(`/op/gateway-configs`, {
    method: 'PUT',
    body: JSON.stringify(params),
  });
};
export const deleteConfigGateway = (params: IConfigure) => {
  return fetch(`/op/gateway-configs`, {
    method: 'DELETE',
    body: JSON.stringify(params),
  });
};

export const getDataCenter = () => {
  return fetch(`/normal/configs/idc`);
};

export const deleteCluster = (clusterId: number) => {
  return fetch(`/op/clusters?clusterId=${clusterId}`, {
    method: 'DELETE',
  });
};

export const createCluster = (params: IRegister) => {
  return fetch(`/op/clusters`, {
    method: 'POST',
    body: JSON.stringify(params),
  });
};

export const registerCluster = (params: IRegister) => {
  return fetch(`/op/clusters`, {
    method: 'PUT',
    body: JSON.stringify(params),
  });
};

export const pauseMonitoring = (clusterId: number, status: number) => {
  return fetch(`/op/clusters/${clusterId}/monitor?status=${status}`, {
    method: 'PUT',
  });
};

export const getBasicInfo = (clusterId: number) => {
  return fetch(`/rd/clusters/${clusterId}/basic-info?need-detail=false`);
};

export const getClusterRealTime = (clusterId: number) => {
  return fetch(`/rd/clusters/${clusterId}/metrics`);
};

export const getClusterMetrice = (clusterId: number, startTime: string, endTime: string) => {
  return fetch(`/rd/clusters/${clusterId}/metrics-history?startTime=${startTime}&endTime=${endTime}`);
};

export const getClusterTopics = (clusterId: number) => {
  return fetch(`/rd/clusters/${clusterId}/topics`);
};

export const getTopicsBasicInfo = (clusterId: number, topicName: string) => {
  return fetch(`/rd/${clusterId}/topics/${topicName}/basic-info`);
};

export const editTopic = (params: IEditTopic) => {
  return fetch(`/op/utils/topics`, {
    method: 'PUT',
    body: JSON.stringify(params),
  });
};

export const expandPartition = (params: IExpand[]) => {
  return fetch(`/op/utils/expand-partitions`, {
    method: 'PUT',
    body: JSON.stringify(params),
  });
};

export const deleteClusterTopic = (params: IDeleteTopic[]) => {
  return fetch(`/op/utils/topics`, {
    method: 'DELETE',
    body: JSON.stringify(params),
  });
};

export const getClusterBroker = (clusterId: number) => {
  return fetch(`/rd/clusters/${clusterId}/brokers`);
};

export const getClusterConsumer = (clusterId: number) => {
  return fetch(`/rd/${clusterId}/consumer-groups`);
};

export const getConsumerDetails = (clusterId: number, consumerGroup: string, location: string) => {
  // tslint:disable-next-line:max-line-length
  return fetch(`/rd/${clusterId}/consumer-groups/${consumerGroup}/topics?location=${location}`);
};

export const getControllerHistory = (clusterId: number) => {
  return fetch(`/rd/clusters/${clusterId}/controller-history`);
};

export const getCandidateController = (clusterId: number) => {
  return fetch(`/rd/clusters/${clusterId}/controller-preferred-candidates`);
};

export const addCandidateController = (params:any) => {
  return  fetch(`/op/cluster-controller/preferred-candidates`, {
    method: 'POST',
    body: JSON.stringify(params),
  });
};

export const deleteCandidateCancel = (params:any)=>{
  return  fetch(`/op/cluster-controller/preferred-candidates`, {
    method: 'DELETE',
    body: JSON.stringify(params),
  });
}
/**
 * 运维管控 broker
 */

export const getBrokersBasicInfo = (clusterId: number, brokerId: number) => {
  return fetch(`/rd/${clusterId}/brokers/${brokerId}/basic-info`);
};

export const getPeakFlowStatus = () => {
  return fetch(`/normal/configs/peak-flow-status`);
};

export const getBrokersStatus = (clusterId: number) => {
  return fetch(`/rd/clusters/${clusterId}/brokers-status`);
};

export const getBrokersMetrics = (clusterId: number, brokerId: number) => {
  return fetch(`/rd/${clusterId}/brokers/${brokerId}/metrics`);
};

export const getBrokersMetricsHistory = (clusterId: number, brokerId: number, startTime: string, endTime: string) => {
  return fetch(`/rd/${clusterId}/brokers/${brokerId}/metrics-history?startTime=${startTime}&endTime=${endTime}`);
};

export const getBrokersTopics = (clusterId: number, brokerId: number) => {
  return fetch(`/rd/${clusterId}/brokers/${brokerId}/topics`);
};

export const getBrokersPartitions = (clusterId: number, brokerId: number) => {
  return fetch(`/rd/${clusterId}/brokers/${brokerId}/partitions`);
};

export const getBrokersAnalysis = (clusterId: number, brokerId: number) => {
  return fetch(`/rd/${clusterId}/brokers/${brokerId}/analysis`);
};

export const getBrokersMetadata = (clusterId: number) => {
  return fetch(`/rd/${clusterId}/brokers/broker-metadata`);
};

export const getBrokersRegions = (clusterId: number) => {
  return fetch(`/rd/${clusterId}/regions`);
};

export const getLogicalClusters = (clusterId: number) => {
  return fetch(`/rd/${clusterId}/logical-clusters`);
};

export const queryLogicalClusters = (logicalClusterId: number) => {
  return fetch(`/rd/logical-clusters?id=${logicalClusterId}`);
};

export const createLogicalClusters = (params: INewLogical) => {
  return fetch(`/rd/logical-clusters`, {
    method: 'POST',
    body: JSON.stringify(params),
  });
};

export const editLogicalClusters = (params: INewLogical) => {
  return fetch(`/rd/logical-clusters`, {
    method: 'PUT',
    body: JSON.stringify(params),
  });
};

export const deteleLogicalClusters = (logicalClusterId: number) => {
  return fetch(`/rd/logical-clusters?id=${logicalClusterId}`, {
    method: 'DELETE',
  });
};

export const deteleClusterBrokers = (clusterId: number, brokerId: number) => {
  return fetch(`/rd/${clusterId}/brokers?brokerId=${brokerId}`, {
    method: 'DELETE',
  });
};

export const getStaffSummary = (timestamp: number) => {
  return fetch(`/rd/bills/staff-summary?timestamp=${timestamp}`);
};

export const addNewRegions = (params: INewRegions) => {
  return fetch(`/rd/regions`, {
    method: 'POST',
    body: JSON.stringify(params),
  });
};

export const editRegions = (params: INewRegions) => {
  return fetch(`/rd/regions`, {
    method: 'PUT',
    body: JSON.stringify(params),
  });
};

export const deleteRegions = (params: number) => {
  return fetch(`/rd/regions?id=${params}`, {
    method: 'DELETE',
  });
};

export const implementRegions = (params: IRebalance) => {
  return fetch(`/op/utils/rebalance`, {
    method: 'POST',
    body: JSON.stringify(params),
  });
};

export const rebalanceStatus = (clusterId: number) => {
  return fetch(`/op/utils/rebalance-status?clusterId=${clusterId}`);
};

export const getClustersThrottles = (clusterId: number) => {
  return fetch(`/rd/clusters/${clusterId}/throttles`);
};

export const getPartitionsLocation = (clusterId: number, brokerId: number) => {
  return fetch(`/rd/${clusterId}/brokers/${brokerId}/partitions-location`);
};

/**
 * 运维管控 任务管理
 */

export const getConfigsTaskStatus = () => {
  return fetch(`/normal/configs/task-status`);
};

export const getTaskManagement = () => {
  return fetch(`/op/cluster-tasks`);
};

export const getClusterTasksEnums = () => {
  return fetch(`/op/cluster-tasks/enums`);
};

export const getConfigsKafkaRoles = () => {
  return fetch(`/rd/configs/kafka-roles`);
};

export const newlyBuildEcmTasks = (params: INewBulidEnums) => {
  return fetch(`/op/cluster-tasks`, {
    method: 'POST',
    body: JSON.stringify(params),
  });
};

export const getTasksMetadata = (taskId: number) => {
  return fetch(`/op/cluster-tasks/${taskId}/metadata`);
};

export const triggerClusterTask = (params: ITrigger) => {
  return fetch(`/op/cluster-tasks`, {
    method: 'PUT',
    body: JSON.stringify(params),
  });
};

export const getSubtasksStatus = (taskId: number) => {
  return fetch(`/op/cluster-tasks/${taskId}/status`);
};

export const getClusterTaskLog = (taskId: number, hostname: string) => {
  return fetch(`/op/cluster-tasks/${taskId}/log?hostname=${hostname}`);
};

/**
 * 运维管控 用户管理
 */

export const getUserList = () => {
  return fetch('/rd/accounts');
};

export const modfiyUser = (params: IUser) => {
  return fetch('/rd/accounts', {
    method: 'PUT',
    body: JSON.stringify(params),
  });
};

export const addUser = (params: IUser) => {
  return fetch('/rd/accounts', {
    method: 'POST',
    body: JSON.stringify(params),
  });
};

export const deleteUser = (username: string) => {
  return fetch(`/rd/accounts?username=${username}`, {
    method: 'DELETE',
  });
};
/**
 * 运维管控 版本管理
 */

export const getFileList = () => {
  return fetch('/rd/kafka-files');
};

export const modfiyFile = (params: IUploadFile) => {
  const { file, id, description, fileName, fileMd5 } = params;
  const formData = new FormData();
  formData.append('uploadFile', file);
  formData.append('description', description);
  formData.append('fileName', fileName);
  formData.append('fileMd5', fileMd5);
  formData.append('id', id + '');

  return formFetch('/rd/kafka-files?modify=true', {
    method: 'POST',
    body: formData,
  });
};

export const addFile = (params: IUploadFile) => {
  const { file, fileType, description, fileName, clusterId, fileMd5 } = params;
  const formData = new FormData();
  formData.append('uploadFile', file);
  formData.append('fileType', fileType + '');
  formData.append('fileName', fileName);
  formData.append('clusterId', clusterId + '');
  formData.append('description', description);
  formData.append('fileMd5', fileMd5);

  return formFetch('/rd/kafka-files', {
    method: 'POST',
    body: formData,
  });
};

export const deleteFile = (id: number) => {
  return fetch(`/rd/kafka-files?id=${id}`, {
    method: 'DELETE',
  });
};

export const getFileType = () => {
  return fetch('/rd/kafka-files/enums');
};

export const getConfigFiles = (fileId: number) => {
  return fetch(`/rd/kafka-files/${fileId}/config-files`);
};

/**
 * 运维管控 应用管理
 */
export const getAdminAppList = () => {
  return fetch(`/rd/apps`);
};

export const modfiyAdminApp = (params: any) => {
  return fetch('/rd/apps', {
    body: JSON.stringify(params),
    method: 'PUT',
  });
};

/**
 * sso api
 */

export const getTicketBycode = (code: string) => {
  return fetch('/sso/opensource/login', {
    method: 'POST',
    body: JSON.stringify({ code }),
  });
};

export const userLogOut = () => {
  return fetch('/sso/logout', {
    method: 'DELETE',
  });
};

/**
 *  监控报警 alarm
 */

export const getMonitorStrategies = () => {
  return fetch('/normal/monitor-strategies');
};

export const deteleMonitorStrategies = (monitorId: number) => {
  return fetch(`/normal/monitor-strategies?monitorId=${monitorId}`, {
    method: 'DELETE',
  });
};

export const getMonitorAlerts = (monitorId: number, startTime: number, endTime: number) => {
  return fetch(`/normal/monitor-alerts?monitorId=${monitorId}&startTime=${startTime}&endTime=${endTime}`);
};

export const getAlertsDetail = (alertId: number) => {
  return fetch(`/normal/monitor-alerts/${alertId}/detail`);
};

export const createSilences = (params: IMonitorSilences) => {
  return fetch(`/normal/monitor-silences`, {
    body: JSON.stringify(params),
    method: 'POST',
  });
};

export const getMonitorSilences = (monitorId: number) => {
  return fetch(`/normal/monitor-silences?monitorId=${monitorId}`);
};

export const modifyMask = (params: IMonitorSilences) => {
  return fetch('/normal/monitor-silences', {
    body: JSON.stringify(params),
    method: 'PUT',
  });
};

export const getSilencesDetail = (silenceId: number) => {
  return fetch(`/normal/monitor-silences/${silenceId}/detail`);
};

export const deleteSilences = (monitorId: number, silenceId: number) => {
  return fetch(`/normal/monitor-silences?monitorId=${monitorId}&silenceId=${silenceId}`, {
    method: 'DELETE',
  });
};
export const getMonitorType = () => {
  return fetch('/normal/monitor-enums');
};

export const addMonitorStrategy = (params: IRequestParams) => {
  return fetch('/normal/monitor-strategies', {
    method: 'POST',
    body: JSON.stringify(params),
  });
};

export const getMonitorDetail = (monitorId: number) => {
  return fetch(`/normal/monitor-strategies/${monitorId}/detail`);
};

export const modifyMonitorStrategy = (params: IRequestParams) => {
  return fetch('/normal/monitor-strategies', {
    method: 'PUT',
    body: JSON.stringify(params),
  });
};

export const getMonitorNotifyGroups = () => {
  return fetch(`/normal/monitor-notify-groups`);
};
