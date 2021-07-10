import { observable, action } from 'mobx';
import { getBillList, getBillDetail } from 'lib/api';
import { IBill, IBillDetail } from 'types/base-type';
import { getBillBarOption } from 'lib/bar-pie-config';

class Bill {
  @observable
  public loading: boolean = false;

  @observable
  public data: IBill[] = [];

  @observable
  public billDetailData: IBillDetail[] = [];

  @action.bound
  public setLoading(value: boolean) {
    this.loading = value;
  }

  @action.bound
  public setData(data: IBill[] = []) {
    this.loading = false;
    if (data) {
      this.data = data.map((item, index) => ({
        ...item,
        cost: +item.cost.toFixed(2),
        key: index,
      }));
    }

    return getBillBarOption(this.data);
  }

  @action.bound
  public setDetailData(data: any) {
    this.loading = false;
    const billList = data.billList || [] as IBill[];
    this.billDetailData = billList.map((item: IBill, index: number) => ({
      ...item,
      cost: +item.cost.toFixed(2),
      key: index,
    }));
  }

  public getList(startTime: number, endTime: number) {
    this.setLoading(true);
    return getBillList(startTime, endTime).then(this.setData);
  }

  public getDetailList(timestamp: number) {
    this.setLoading(true);
    return getBillDetail(timestamp).then(this.setDetailData);
  }
}

export const bill = new Bill();
