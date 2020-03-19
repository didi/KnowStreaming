import * as React from 'react';
import { observer } from 'mobx-react';
import { modal } from 'store/modal';
import TopicNew from './topic-new';
import TopicExpand from './topic-expand';
import Alarm from './alarm-config';
import ClusterNew from './cluster';
import LeaderRebalance from './leader-rebalance';
import Task from './task-new';
import OrderApprove from './order-approve';
import PartitionApprove from './partition-approve';
import NewUser from './new-user';
import Region from './region';
import TopicCreate from './topic-create';
import AdminExpand from './admin-expand';
import ConsumerTopic from './cosumer-topic';

import './index.less';

@observer
export default class AllModalInOne extends React.Component {
  public render() {
    if (!modal.id) return null;
    return (
      <>
        {modal.id === 'showNewTopic' ? <TopicNew /> : null}
        {modal.id === 'showNewCluster' ? <ClusterNew /> : null}
        {modal.id === 'showModifyCluster' ? <ClusterNew /> : null}
        {modal.id === 'showAdimTopic' ? <TopicCreate /> : null}
        {modal.id === 'showExpandTopic' ? <TopicExpand /> : null}
        {modal.id === 'showExpandAdmin' ? <AdminExpand /> : null}
        {modal.id === 'showAlarm' ? <Alarm /> : null}
        {modal.id === 'showAlarmModify' ? <Alarm /> : null}
        {modal.id === 'showRegion' ? <Region /> : null}
        {modal.id === 'showLeaderRebalance' ? <LeaderRebalance /> : null}
        {modal.id === 'showTask' ? <Task /> : null}
        {modal.id === 'showTaskDetail' ? <Task /> : null}
        {modal.id === 'showOrderApprove' ? <OrderApprove /> : null}
        {modal.id === 'showOrderDetail' ? <OrderApprove /> : null}
        {modal.id === 'showPartitionDetail' ? <PartitionApprove /> : null}
        {modal.id === 'showPartition' ? <PartitionApprove /> : null}
        {modal.id === 'showNewUser' ? <NewUser /> : null}
        {modal.id === 'showConsumerTopic' ? <ConsumerTopic /> : null}
      </>
    );
  }
}
