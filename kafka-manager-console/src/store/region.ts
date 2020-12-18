import { observable, action } from 'mobx';
import { getRegionIdcs } from 'lib/api';
import { setCookie, getCookie } from 'lib/utils';

export interface IRegion {
  value: string;
  label: string;
}

export interface IRegionIdcs {
  name: string;
  idc: string;
}

class RegionCenter {
  @observable
  public regionName: string = getCookie('idcName') || '国内';

  @observable
  public currentRegion: string = getCookie('idc') || 'cn';

  @observable
  public regionIdcList: IRegionIdcs[] = [
    { name: '国内', idc: 'cn' },
    // { name: '美东', idc: 'us' },
    // { name: '俄罗斯', idc: 'ru' },
  ];

  @action.bound
  public setRegion(data: IRegionIdcs) {
    this.regionName = data.name;
    this.currentRegion = data.idc;
    setCookie([{ key: 'idc', value: data.idc, time: 1 }, { key: 'idcName', value: data.name, time: 1 }]);
  }

  @action.bound
  public setRegionIdcs(data: IRegionIdcs[]) {
    this.regionIdcList = data;
  }

  public changeRegion = (value: IRegionIdcs) => {
    this.setRegion(value);
  }

  public getRegionIdcs() {
    getRegionIdcs().then(this.setRegionIdcs);
  }
}

export const region = new RegionCenter();
