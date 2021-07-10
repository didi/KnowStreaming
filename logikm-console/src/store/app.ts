import { observable, action } from 'mobx';
import { getAppList, getAppDetail, getAppTopicList, applyOrder, modfiyApplication, modfiyAdminApp, getAdminAppList, getAppsConnections, getTopicAppQuota } from 'lib/api';
import { IAppItem, IAppQuota, ITopic, IOrderParams, IConnectionInfo } from 'types/base-type';

class App {
  @observable
  public loading: boolean = false;

  @observable
  public connectLoading: boolean = true;

  @observable
  public data: IAppItem[] = [];

  @observable
  public adminAppData: IAppItem[] = [];

  @observable
  public selectData: IAppItem[] = [{
    appId: '-1',
    name: '所有关联应用',
    } as IAppItem,
  ];

  @observable
  public active: string = '-1';

  @observable
  public baseInfo: IAppItem = {} as IAppItem;

  @observable
  public topicList: ITopic[] = [];

  @observable
  public currentTab: string = '1';

  @observable
  public appsConnections: IConnectionInfo[] = [];

  @observable
  public principal: string = '';

  @observable
  public appQuota: IAppQuota[] = [];

  @action.bound
  public changeActiveApp(data: string) {
    this.active = data;
  }

  @action.bound
  public setTopicAppQuota(data: IAppQuota[]) {
    return this.appQuota = data.map((item, index) => {
       return {
         ...item,
         label: item.appName,
         value: item.appId,
         key: index,
       };
    });
  }

  @action.bound
  public setLoading(value: boolean) {
    this.loading = value;
  }

  @action.bound
  public setConnectLoading(value: boolean) {
    this.connectLoading = value;
  }

  @action.bound
  public setData(data: IAppItem[] = []) {
    this.data = data.map((item, index) => ({
      ...item,
      key: index,
      principalList: item.principals ? item.principals.split(',') : [],
    }));
    const selectData = data.map((item) => ({
      ...item,
      label: item.name,
      value: item.appId,
    }));

    this.selectData.push(...selectData);
    this.setLoading(false);
  }

  @action.bound
  public setAdminData(data: IAppItem[] = []) {
    this.adminAppData = data.map((item, index) => ({
      ...item,
      key: index,
      principalList: item.principals ? item.principals.split(',') : [],
    }));

    this.setLoading(false);
  }

  @action.bound
  public setCurrentTab(data: string) {
    this.currentTab = data;
  }

  @action.bound
  public setAppDetail(data: IAppItem) {
    this.baseInfo = data || {} as IAppItem;
  }

  @action.bound
  public setTopicList(data: ITopic[]) {
    this.topicList = data.map((item, index) => ({
      ...item,
      key: index,
    }));
    this.setLoading(false);
  }

  @action.bound
  public setAppsConnections = (data: IConnectionInfo[]) => {
    this.setConnectLoading(false);
    this.appsConnections = (data || []).map((item, index) => {
      return {
        key: index,
        ...item,
      };
    });
  }

  public getAppList() {
    this.setLoading(true);
    getAppList().then(this.setData);
  }

  public getTopicAppQuota(clusterId: number, topicName: string) {
    return getTopicAppQuota(clusterId, topicName).then(this.setTopicAppQuota);
  }

  public getAdminAppList() {
    this.setLoading(true);
    getAdminAppList().then(this.setAdminData);
  }

  public getAppDetail(appId: string) {
    getAppDetail(appId).then(this.setAppDetail);
  }

  public getAppTopicList(appId: string) {
    this.setLoading(true);
    const mine = this.currentTab === '1';
    getAppTopicList(appId, mine).then(this.setTopicList);
  }

  public applyApplication(params: IOrderParams) {
    return applyOrder(params);
  }

  public modfiyApplication(params: any, from?: string) {
    return from === 'admin' ? this.modfiyAdminApp(params) : modfiyApplication(params).then(() => this.getAppList());
  }

  public modfiyAdminApp(params: any) {
    return modfiyAdminApp(params).then(() => this.getAdminAppList());
  }

  public cancelProdPermission(params: IOrderParams) {
    return applyOrder(params);
  }

  public applyAppOffline(params: IOrderParams) {
    return applyOrder(params);
  }

  public getAppsConnections(appId: string) {
    this.setConnectLoading(true);
    return getAppsConnections(appId).then(this.setAppsConnections);
  }

  public applyExpand(params: IOrderParams) {
    return applyOrder(params);
  }
}

export const app = new App();
