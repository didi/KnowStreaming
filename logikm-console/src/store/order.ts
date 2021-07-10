import { observable, action } from 'mobx';
import { IBaseOrder, IOrderInfo, IStatusMap, IConfigInfo, IApprovalOrder, IBrokerMetadata, IBatchApproval, IBatchApprovalData, IBatchData } from 'types/base-type';
import { getOrderTypeList, getApplyOrderList, getApprovalOrderList, getOrderDetail, cancelOrder, approvalOrder, getBrokerMetadata, getBrokerBasicInfo, batchApprovalOrders } from 'lib/api';
import { setCookie, getCookie } from 'lib/utils';

class Order {
  @observable
  public applyUnique: number = null;

  @observable
  public unique: number = null;

  @observable
  public apply: number = null;

  @observable
  public approval: number = null;

  @observable
  public loading: boolean = false;

  @observable
  public selectedRows: any[] = [];

  @observable
  public orderList: IBaseOrder[] = [];

  @observable
  public applyList: IBaseOrder[] = [];

  @observable
  public approvalList: IBaseOrder[] = [];

  @observable
  public orderTypeMap: IStatusMap = {};

  @observable
  public orderInfo: IOrderInfo = {
    applicant: {},
    detail: {},
    approverList: [],
  } as IOrderInfo;

  @observable
  public brokerMetadata: IBrokerMetadata[] = [];

  @observable
  public brokerBasicInfo: IBrokerMetadata[] = [];

  @observable
  public existence: number[] = [];

  @observable
  public batchApprovalList: IBatchData[] = [];

  @action.bound
  public setLoading(value: boolean) {
    this.loading = value;
  }

  @action.bound
  public setSelectedRows(rows?: any[]) {
    if (rows) {
      this.selectedRows = rows;
    } else {
      this.selectedRows = [];
    }
  }

  @action.bound
  public setOrderList(data: IBaseOrder[]) {
    this.orderList = data;
    this.setLoading(false);
  }

  @action.bound
  public setApplyList(data: IBaseOrder[]) {
    this.applyList = data;
    if (this.applyUnique === 0) {
      setCookie([{ key: 'apply', value: `${data.length}`, time: 1 }]);
      this.apply = Number(getCookie('apply'));
      if ( data && data.length ) {
        this.apply = data.length;
      }
    }
    this.setLoading(false);
  }

  @action.bound
  public setApprovalList(data: IBaseOrder[]) {
    this.approvalList = data;
    if (this.unique === 0) {
      setCookie([{ key: 'approval', value: `${data.length}`, time: 1 }]);
      this.approval = Number(getCookie('approval'));
      if ( data && data.length ) {
        this.approval = data.length;
      }
    }
    this.setLoading(false);
  }

  @action.bound
  public setOrderDetail(data: IOrderInfo) {
    this.orderInfo = data || {
      applicant: {},
      detail: {},
      approverList: [],
    } as IOrderInfo;
    this.setLoading(false);
  }

  @action.bound
  public setOrderTypeList(data: IConfigInfo[]) {
    data.map(item => {
      this.orderTypeMap[item.type] = item.message;
    });
  }

  @action.bound
  public setBrokerMetadata(data: IBrokerMetadata[]) {
    this.brokerMetadata = data ? data.map((item, index) => {
      item.key = index;
      return {
        ...item,
        value: item.brokerId,
        label: item.host,
        text: `${item.host} （BrokerID：${item.brokerId}`,
      };
    }) : [];
    return this.brokerMetadata;
  }

  @action.bound
  public setBrokerBasicInfo(data: IBrokerMetadata[]) {
    const existList = data.filter(ele => ele.logicClusterId);
    this.existence = existList.map(ele => {
      return ele.brokerId;
    });
    this.brokerBasicInfo = data ? data.map((item, index) => {
      item.key = index;
      return {
        ...item,
        value: item.brokerId,
        lable: item.host,
      };
    }) : [];
  }

  @action.bound
  public setBatchApprovalOrders(data: IBatchApprovalData[]) {
    const failList = data.filter(ele => ele.result.code !== 0);
    const successList = data.filter(ele => ele.result.code === 0);
    const approvalData = failList.concat(successList);
    return this.batchApprovalList = approvalData.map(ele => {
      return {
        id: ele.id,
        code: ele.result.code,
        message: ele.result.message,
      };
    });
  }

  public getOrderTypeList() {
    getOrderTypeList().then(this.setOrderTypeList);
  }

  public getApplyOrderList(status: number) {
    this.applyUnique = status;
    this.setLoading(true);
    getApplyOrderList(status).then(this.setApplyList);
  }

  public getApprovalList(status: number) {
    this.unique = status;
    this.setLoading(true);
    getApprovalOrderList(status).then(this.setApprovalList);
  }

  public getOrderDetail(orderId: number) {
    this.setLoading(true);
    getOrderDetail(orderId).then(this.setOrderDetail);
  }

  public getBrokerMetadata(clusterId: number) {
    return getBrokerMetadata(clusterId).then(this.setBrokerMetadata);
  }

  public getBrokerBasicInfo(clusterId: number) {
    getBrokerBasicInfo(clusterId).then(this.setBrokerBasicInfo);
  }

  public approvalOrder(value: IApprovalOrder) {
    return approvalOrder(value);
  }

  public cancelOrder(id: number) {
    return cancelOrder(id);
  }

  public batchApprovalOrders(params: IBatchApproval) {
    return batchApprovalOrders(params).then(this.setBatchApprovalOrders);
  }
}

export const order = new Order();
