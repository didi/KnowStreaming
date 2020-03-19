import { observable, action } from 'mobx';
import { getController } from 'lib/api';

export interface IController {
  brokerId: number;
  controllerTimestamp: number;
  controllerVersion: number;
  host: string;
}

class Controller {
  @observable
  public data: IController[] = [];

  @action.bound
  public setData(data: IController[]) {
    this.data = data;
  }

  public getController(clusterId: number) {
    getController(clusterId).then(this.setData);
  }
}

export const controller = new Controller();
