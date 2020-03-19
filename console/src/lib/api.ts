import fetch from './fetch';
import { INewCluster, ITopic, IAlarmBase, IDilation, ITaskBase, IRebalance, IOrderTopic, IUser, ISample, IDeleteTopic, IOffset } from 'types/base-type';
import { IRegionData } from 'store/region';

export const getClusters = (cluster?: number) => {
  return fetch(`/clusters${cluster ? '/' + cluster : ''}`);
};

export const getTopic = (clusterId?: number, favorite?: boolean) => {
  const query = !clusterId && favorite === undefined ? '/topics' :
  (clusterId ? `/topics?clusterId=${clusterId}` : `/topics?favorite=${favorite}`);
  return fetch(query);
};

export const collect = (topicFavoriteList: ITopic[]) => {
  return fetch('/topics/favorite', {
    body: {
      topicFavoriteList,
    },
  });
};

export const uncollect = (topicFavoriteList: ITopic[]) => {
  return fetch('/topics/favorite', {
    method: 'DELETE',
    body: {
      topicFavoriteList,
    },
  });
};

export const getTopicBasicInfo = (topicName: string, clusterId: number) => {
  return fetch(`/${clusterId}/topics/${topicName}/basic-info`);
};

export const getTopicConsumeInfo = (clusterId: number, topicName: string) => {
  return fetch(`/${clusterId}/topics/${topicName}/consumer-groups`);
};

export const getConsumeInfo = (clusterId: number) => {
  return fetch(`/${clusterId}/consumers/consumer-groups`);
};

export const getTopicStatusInfo = (topicName: string, clusterId: number) => {
  return fetch(`/${clusterId}/topics/${topicName}/metrics`);
};

export const getGroupInfo = (topicName: string, clusterId: number, group: string, location: string) => {
  return fetch(`/${clusterId}/consumers/${group}/topics/${topicName}/consume-detail?location=${location}`);
};

export const getTopicOrder = () => {
  return fetch('/orders/topic');
};

export const getPartitionOrder = () => {
  return fetch('/orders/partition');
};

export const getAlarm = () => {
  return fetch(`/alarms/alarm-rules`);
};

export const createTopic = (params: ITopic) => {
  return fetch('/orders/topic', {
    method: 'POST',
    body: JSON.stringify(params),
  });
};

export const getRegions = (clusterId: number) => {
  return fetch(`/admin/${clusterId}/regions`);
};

export const delRegion = (regionId: number) => {
  return fetch(`/admin/regions/${regionId}`, {
    method: 'DELETE',
  });
};

export const addRegion = (params: IRegionData) => {
  return fetch(`/admin/regions/region`, {
    method: 'POST',
    body: JSON.stringify(params),
  });
};

export const modifyRegion = (params: IRegionData) => {
  return fetch(`/admin/regions/region`, {
    method: 'PUT',
    body: JSON.stringify(params),
  });
};

export const topicDilatation = (params: IDilation) => {
  return fetch('/admin/utils/topic/dilatation', {
    method: 'PUT',
    body: JSON.stringify(params),
  });
};

export const addAlarm = (params: IAlarmBase) => {
  return fetch('/alarms/alarm-rule', {
    method: 'POST',
    body: JSON.stringify(params),
  });
};

export const modifyAlarm = (params: IAlarmBase) => {
  return fetch('/alarms/alarm-rule', {
    method: 'PUT',
    body: JSON.stringify(params),
  });
};

export const deleteAlarm = (alarmRuleId: number) => {
  return fetch(`/alarms/alarm-rule?alarmRuleId=${alarmRuleId}`, {
    method: 'DELETE',
  });
};

export const recallOrder = (orderId: number) => {
  return fetch(`/orders/topic?orderId=${orderId}`, {
    method: 'DELETE',
  });
};

export const getBrokerBaseInfo = (clusterId: number, brokerId: number) => {
  return fetch(`/${clusterId}/brokers/${brokerId}/basic-info`);
};

export const getBrokerList = (clusterId: number) => {
  return fetch(`/${clusterId}/brokers/overview`);
};

export const getTopicBroker = (clusterId: number, topicName: string) => {
  return fetch(`/${clusterId}/topics/${topicName}/brokers`);
};

export const getTopicPartition = (clusterId: number, topicName: string) => {
  return fetch(`/${clusterId}/topics/${topicName}/partitions`);
};

export const getBrokerNetwork = (clusterId: number) => {
  return fetch(`/clusters/${clusterId}/metrics`);
};

export const getOneBrokerNetwork = (clusterId: number, brokerId: number) => {
  return fetch(`/${clusterId}/brokers/${brokerId}/metrics`);
};

export const getBrokerPartition = (clusterId: number) => {
  return fetch(`/${clusterId}/brokers/overall`);
};

export const getBrokerTopic = (clusterId: number, brokerId: number) => {
  return fetch(`/${clusterId}/brokers/${brokerId}/topics`);
};

export const getController = (clusterId: number) => {
  return fetch(`/clusters/${clusterId}/controller-history`);
};

export const getConsumeGroup = (clusterId: number, consumerGroup: string, location: string) => {
  return fetch(`/${clusterId}/consumer/${consumerGroup}/topics?location=${location}`);
};

export const newCluster = (cluster: INewCluster) => {
  return fetch(`/clusters`, {
    method: 'POST',
    body: JSON.stringify(cluster),
  });
};

export const modifyCluster = (cluster: INewCluster) => {
  return fetch(`/clusters`, {
    method: 'PUT',
    body: JSON.stringify(cluster),
  });
};

export const getKafkaVersion = () => {
  return fetch(`/clusters/kafka-version`);
};

export const getClusterMetricsHistory = (clusterId: number, startTime: string, endTime: string) => {
  return fetch(`/clusters/${clusterId}/metrics-history?startTime=${startTime}&endTime=${endTime}`);
};

export const getPartitions = (clusterId: number, brokerId: number) => {
  return fetch(`/${clusterId}/brokers/${brokerId}/partitions`);
};

export const getBrokerKeyMetrics = (clusterId: number, brokerId: number, startTime: string, endTime: string) => {
  return fetch(`/${clusterId}/brokers/${brokerId}/key-metrics?startTime=${startTime}&endTime=${endTime}`);
};

export const getTask = (value?: number) => {
  return fetch(`/admin/migration/tasks${value ? '/' + value : ''}`);
};

export const executeTask = (params: ITaskBase) => {
  return fetch('/admin/migration/tasks', {
    method: 'POST',
    body: JSON.stringify(params),
  });
};

export const modifyTask = (params: ITaskBase) => {
  return fetch('/admin/migration/tasks', {
    method: 'PUT',
    body: JSON.stringify(params),
  });
};

export const getAdminTopicOrder = () => {
  return fetch('/admin/orders/topic');
};

export const getAdminPartitionOrder = (orderId?: number) => {
  return fetch(`/admin/orders/partition${orderId ? '?orderId=' + orderId : ''}`);
};

export const getRebalanceStatus = (clusterId: number) => {
  return fetch(`/admin/utils/rebalance/clusters/${clusterId}/status`);
};

export const addRebalance = (params: IRebalance) => {
  return fetch('/admin/utils/rebalance', {
    method: 'POST',
    body: JSON.stringify(params),
  });
};

export const getBrokerTopicAnalyzer = (clusterId: number, brokerId: number) => {
  return fetch(`/${clusterId}/brokers/${brokerId}/analysis`);
};

export const addTopicApprove = (params: IOrderTopic) => {
  return fetch('/admin/orders/topic', {
    method: 'POST',
    body: JSON.stringify(params),
  });
};

export const addAdminPartition = (params: IOrderTopic) => {
  return fetch('/admin/orders/partition', {
    method: 'POST',
    body: JSON.stringify(params),
  });
};

export const addPartitionApprove = (params: IOrderTopic) => {
  return fetch('/orders/partition', {
    method: 'POST',
    body: JSON.stringify(params),
  });
};

export const getUsers = () => {
  return fetch('/admin/accounts');
};

export const addUser = (params: IUser) => {
  return fetch('/admin/accounts/account', {
    method: 'POST',
    body: JSON.stringify(params),
  });
};

export const deleteUser = (username: string) => {
  return fetch(`/admin/accounts/account?username=${username}`, {
    method: 'DELETE',
  });
};

export const modifyUser = (params: IUser) => {
  return fetch('/admin/accounts/account', {
    method: 'PUT',
    body: JSON.stringify(params),
  });
};

export const getLogin = () => {
  return fetch('/login/loginPage');
};

export const userLogin = (params: IUser) => {
  return fetch('/login/login', {
    method: 'POST',
    body: JSON.stringify(params),
  });
};

export const userLogoff = (value: string) => {
  return fetch(`/login/logoff?username=${value}`, {
    method: 'POST',
  });
};

export const addSample = (params: ISample) => {
  const {clusterId, topicName, ...rest} = params;
  return fetch(`/${clusterId}/topics/${topicName}/sample`, {
    method: 'POST',
    body: JSON.stringify(rest),
  });
};

export const deleteTopic = (params: IDeleteTopic) => {
  return fetch(`/admin/utils/topic`, {
    method: 'DELETE',
    body: JSON.stringify(params),
  });
};

export const modifyTopic = (params: ITopic) => {
  return fetch('/admin/utils/topic/config', {
    method: 'PUT',
    body: JSON.stringify(params),
  });
};

export const resetOffset = (params: IOffset) => {
  return fetch('/consumers/offsets', {
    method: 'PUT',
    body: JSON.stringify(params),
  });
};

export const getClustersBasic = () => {
  return fetch('/clusters/basic-info');
};

export const getAlarmConstant = () => {
  return fetch('/alarms/alarm/constant');
};

export const getTopicNameById = (clusterId: number) => {
  return fetch(`/${clusterId}/topics/topic-names`);
};

export const deleteBroker = (clusterId: number, brokerId: number) => {
  return fetch(`/${clusterId}/brokers/${brokerId}`, {
    method: 'DELETE',
  });
};

export const recallPartition = (orderId: number) => {
  return fetch(`/orders/partition?orderId=${orderId}`, {
    method: 'DELETE',
  });
};

export const getBrokerNameList = (clusterId: number) => {
  return fetch(`/${clusterId}/brokers/broker-metadata`);
};

export const adminCreateTopic = (params: ITopic) => {
  return fetch('/admin/utils/topic', {
    method: 'POST',
    body: JSON.stringify(params),
  });
};

export const getAdminTopicDetail = (clusterId: number, topicName: string) => {
  return fetch(`/admin/utils/${clusterId}/topics/${topicName}/detail`);
};

export const getTopicMetriceInfo = (clusterId: number, topicName: string, startTime: string, endTime: string) => {
  return fetch(`/${clusterId}/topics/${topicName}/metrics-history?startTime=${startTime}&endTime=${endTime}`);
};

export const getBrokerMetrics = (clusterId: number, brokerId: number, startTime: string, endTime: string) => {
  return fetch(`/${clusterId}/brokers/${brokerId}/metrics-history?startTime=${startTime}&endTime=${endTime}`);
};

export const getTopicMetaData = (clusterId: number, topicName: string) => {
  return fetch(`/${clusterId}/topics/${topicName}/metadata`);
};
