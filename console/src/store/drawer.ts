import { observable, action } from 'mobx';

class Drawer {
  @observable
  public id: string = null;

  @observable
  public topicData: any = null;

  @observable
  public offsetDetail: any = null;

  @action.bound
  public showResetOffset(r: any) {
    this.id = 'showResetOffset';
    this.offsetDetail = r;
  }

  @action.bound
  public showTopicSample({ clusterId, topicName }: any) {
    this.id = 'showTopicSample';
    this.topicData = { clusterId, topicName };
  }

  @action.bound
  public close() {
    this.id = null;
  }
}

export const drawer = new Drawer();
