import { observable, action } from 'mobx';
import { getAlarm, getAlarmConstant } from 'lib/api';
import { IAlarmBase } from 'types/base-type';

export interface IAlarm extends IAlarmBase {
  gmtCreate: number;
  gmtModify: number;
  status: number;
  key?: number;
}

export interface IConstant {
  conditionTypeList: [];
  ruleTypeList: [];
  notifyTypeList: [];
  metricTypeList: [];
}

class Alarm {
  @observable
  public data: IAlarm[] = [];

  @observable
  public alarmConstant: IConstant = null;

  public curData: IAlarm = null;

  public setCurData(data: IAlarm) {
    this.curData = data;
  }

  @action.bound
  public setAlarm(data: IAlarm[]) {
    this.data = data.map((d, i) => {
      d.key = i;
      return d;
    });
  }

  @action.bound
  public setAlarmConstant(data: IConstant) {
    this.alarmConstant = data;
  }

  public getAlarm() {
    getAlarm().then(this.setAlarm);
  }

  public getAlarmConstant() {
    getAlarmConstant().then(this.setAlarmConstant);
  }
}

export const alarm = new Alarm();
