import { observable, action } from 'mobx';
import { ITopic, IBatchData } from 'types/base-type';

class CustomModal {
  @observable
  public modalId: string = null;

  @observable
  public drawerId: string = null;

  @observable
  public params: any = null;

  @observable
  public actionAfterClose: any = null;

  @action.bound
  public setAction(value: any) {
    this.actionAfterClose = value;
  }

  @action.bound
  public showOfflineTopicModal(value: ITopic) {
    this.modalId = 'offlineTopicModal';
    this.params = value;
  }

  @action.bound
  public showOfflineAppModal(value: string) {
    this.modalId = 'offlineAppModal';
    this.params = value;
  }

  @action.bound
  public showOfflineAppNewModal(value: any) {
    this.modalId = 'offlineAppNewModal';
    this.params = value;
  }

  @action.bound
  public showOrderOpResult() {
    this.modalId = 'orderOpResult';
  }

  @action.bound
  public showOfflineClusterModal(value: number) {
    this.modalId = 'offlineClusterModal';
    this.params = value;
  }

  @action.bound
  public showCancelTopicPermission(value: object) {
    this.modalId = 'cancelTopicPermission';
    this.params = value;
  }

  @action.bound
  public close() {
    this.modalId = null;
    this.drawerId = null;
    this.params = null;
  }
}

export const modal = new CustomModal();
