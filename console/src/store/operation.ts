import { observable, action } from 'mobx';
import { getTask, getRegions } from 'lib/api';
import { ITaskBase } from 'types/base-type';

interface IReassign {
  [propname: string]: number[];
}

interface IMigration {
  [index: string]: number;
}

export const taskMap = ['待执行', '执行中', '迁移成功', '迁移失败', '已撤销'];

export interface ITask extends ITaskBase {
  clusterName: string;
  gmtCreate: number;
  operator: string;
  reassignmentMap?: IReassign;
  migrationStatus?: IMigration;
  regionList?: Array<[string, number[]]>;
}

class Operation {
  @observable
  public tasks: ITask[] = null;

  @observable
  public taskDetail: ITask = null;

  @observable
  public RegionOptions: any[] = ['请选择集群'];

  @action.bound
  public setTask(data: ITask[]) {
    this.tasks = data;
  }

  @action.bound
  public setTaskDetail(data: ITask) {
    if (data) data.regionList = Object.keys(data.reassignmentMap).map(i => [i, data.reassignmentMap[i]]);
    this.taskDetail = data;
  }

  @action.bound
  public setRegionOptions(data: any) {
    this.RegionOptions = data.map((i: any) => ({
      value: i.regionId,
      label: i.regionName,
    }));
  }

  public getTask() {
    getTask().then(this.setTask);
  }

  public initRegionOptions = (clusterId: number) => {
    getRegions(clusterId).then(this.setRegionOptions);
  }

  public getTaskDetail(value: number) {
    getTask(value).then(this.setTaskDetail);
  }
}

export const operation = new Operation();
