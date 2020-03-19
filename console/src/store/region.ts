import { observable, action } from 'mobx';
import { getRegions, delRegion } from 'lib/api';

export const statusMap = ['废弃', '正常', '容量已满'];
export const levelMap =  ['普通', '重要'];
export interface IRegionData {
    brokerIdList: number[];
    clusterId: number;
    description: string;
    gmtCreate: number;
    gmtModify: number;
    level: number;
    regionId: number;
    regionName: string;
    status: number;
}

class Region {
  @observable
  public data: IRegionData[] = [];

  @action.bound
  public setData(data: IRegionData[]) {
    this.data = data;
  }

  public getRegions(clusterId: number) {
    getRegions(clusterId).then(this.setData);
  }

  public delRegion(regionId: number) {
    return delRegion(regionId);
  }
}

export const region = new Region();
