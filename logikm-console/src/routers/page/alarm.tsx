import * as React from 'react';
import CommonRoutePage from './common';
import { AlarmList, AddAlarm } from 'container/alarm';
import { AlarmDetail } from 'container/alarm/alarm-detail';
import { HistoryDetail } from 'container/alarm/alarm-detail/history-detail';

export default class Alarm extends React.Component {
  public pageRoute = [{
    path: '/alarm',
    exact: true,
    component: AlarmList,
  }, {
    path: '/alarm/detail',
    exact: true,
    component: AddAlarm,
  }, {
    path: '/alarm/add',
    exact: true,
    component: AddAlarm,
  }, {
    path: '/alarm/modify',
    exact: true,
    component: AddAlarm,
  }, {
    path: '/alarm/alarm-detail',
    exact: true,
    component: AlarmDetail,
  }, {
    path: '/alarm/history-detail',
    exact: true,
    component: HistoryDetail,
  }];

  public render() {
    return (
      <CommonRoutePage pageRoute={this.pageRoute} mode="alarm" active="alarm"/>
    );
  }
}
