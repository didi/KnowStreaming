import { observable, action } from 'mobx';

import moment from 'moment';

class TimeStore {
  @observable
  public startTime: moment.Moment;

  @observable
  public endTime: moment.Moment;

  @action.bound
  public initTime() {
    this.startTime = moment().startOf('date');
    this.endTime =  moment();
  }

  @action.bound
  public changeStartTime(value: moment.Moment) {
    this.startTime = value;
  }

  @action.bound
  public changeEndTime(value: moment.Moment ) {
    this.endTime = value;
  }
}

export const timestore = new TimeStore();
