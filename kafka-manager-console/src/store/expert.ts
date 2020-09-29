import { observable, action } from 'mobx';
import { IHotTopics, IReassignTasks, ITopicMetadata, ITasksDetail, IReassign, IPartition, IResource, IAnomalyFlow, IMetaData, ILabelValue, IExecute } from 'types/base-type';
import { getHotTopics, getReassignTasks, getTaskTopicMetadata, getReassignTasksDetail, getReassignTasksStatus, getInsufficientPartition, getResourceManagement, getAnomalyFlow, getMetaData, getExecuteSubTask, getExecuteTask } from 'lib/api';
import Moment from 'moment';

interface IPartitionMap {
  [key: string]: ILabelValue[];
}
class Expert {
  @observable
  public hotTopics: IHotTopics[] = [];

  @observable
  public reassignTasks: IReassignTasks[] = [];

  @observable
  public taskTopicMetadata: ITopicMetadata[] = [];

  @observable
  public partitionIdMap: IPartitionMap = {};

  @observable
  public tasksDetail: ITasksDetail;

  @observable
  public tasksStatus: IReassign[];

  @observable
  public partitionedData: IPartition[] = [];

  @observable
  public resourceData: IResource[] = [];

  @observable
  public anomalyFlowData: IAnomalyFlow[] = [];

  @observable
  public metaData: IMetaData[] = [];

  @observable
  public active: number = null;

  @action.bound
  public changePhysical(data: number) {
    this.active = data;
  }

  @action.bound
  public setHotTopics(data: IHotTopics[]) {
    this.hotTopics = data ? data.map((item, index) => {
      item.key = index;
      return item;
    }) : [];
  }

  @action.bound
  public setReassignTasks(data: IReassignTasks[]) {
    this.reassignTasks = data ? data.map((item, index) => {
      item.key = index;
      return item;
    }) : [];
  }

  @action.bound
  public setTaskTopicMetadata(data: ITopicMetadata[]) {
    this.taskTopicMetadata = data ? data.map((item, index) => {
      item.key = index;
      this.partitionIdMap[item.topicName] = item.partitionIdList.map(item => {
        return {
          label: item + '',
          value: item + '',
        };
      });
      return {
        ...item,
        label: item.topicName,
        value: item.topicName,
      };
    }) : [];
  }

  @action.bound
  public setReassignTasksDetail(data: ITasksDetail) {
    this.tasksDetail = data;
  }

  @action.bound
  public setReassignTasksStatus(data: IReassign[]) {
    this.tasksStatus = data ? data.map((item, index) => {
      item.key = index;
      item.reassignList.map((ele, i) => {
        ele.key = i;
      });
      return item;
    }) : [];
  }

  @action.bound
  public setInsufficientPartition(data: IPartition[]) {
    this.partitionedData = data ? data.map((item, index) => {
      item.key = index;
      return item;
    }) : [];
  }

  @action.bound
  public setResourceManagement(data: IResource[]) {
    this.resourceData = data ? data.map((item, index) => {
      item.key = index;
      return item;
    }) : [];
  }

  @action.bound
  public setAnomalyFlow(data: IAnomalyFlow[]) {
    this.anomalyFlowData = data ? data.map((item, index) => {
      item.key = index;
      return item;
    }) : [];
  }

  @action.bound
  public setMetaData(data: IMetaData[]) {
    data = data ? data : [];
    data.unshift({
      clusterId: -1,
      clusterName: '所有物理集群',
    } as IMetaData);
    this.metaData = data;
    this.active = (this.metaData[0] || { clusterId: null }).clusterId;
  }

  public getHotTopics() {
    getHotTopics().then(this.setHotTopics);
  }

  public getReassignTasks() {
    getReassignTasks().then(this.setReassignTasks);
  }

  public getTaskTopicMetadata(clusterId: number) {
    return getTaskTopicMetadata(clusterId).then(this.setTaskTopicMetadata);
  }

  public getReassignTasksDetail(taskId: number) {
    getReassignTasksDetail(taskId).then(this.setReassignTasksDetail);
  }

  public getReassignTasksStatus(taskId: number) {
    getReassignTasksStatus(taskId).then(this.setReassignTasksStatus);
  }

  public getInsufficientPartition() {
    getInsufficientPartition().then(this.setInsufficientPartition);
  }

  public getResourceManagement() {
    getResourceManagement().then(this.setResourceManagement);
  }

  public getAnomalyFlow(timestamp: number) {
    getAnomalyFlow(timestamp).then(this.setAnomalyFlow);
  }

  public getMetaData(needDetail: boolean) {
    getMetaData(needDetail).then(this.setMetaData);
  }

  public getExecuteSubTask(params: IExecute, taskId: number) {
    return getExecuteSubTask(params).then(() => this.getReassignTasksDetail(taskId));
  }

  public getExecuteTask(params: IExecute) {
    return getExecuteTask(params).then(() => this.getReassignTasks());
  }
}

export const expert = new Expert();
