import * as React from 'react';
import CommonRoutePage from './common';
import urlParser from 'lib/url-parser';
import urlQuery from 'store/url-query';
import { AppDetail } from 'container/app';
import { AdminAppList, ClusterList, ClusterDetail, BrokerDetail, UserManagement, VersionManagement, OperationManagement, OperationDetail, BillManagement, ConfigureManagement, IndividualBill, MigrationDetail, BillDetail, OperationRecord } from 'container/admin';
import { PlatformManagement } from 'container/admin/platform-management';

export default class Home extends React.Component<any> {

  private pageRoute = [{
    path: '/admin',
    exact: true,
    component: ClusterList,
  }, {
    path: '/admin/cluster-detail',
    exact: true,
    component: ClusterDetail,
  }, {
    path: '/admin/broker-detail',
    exact: true,
    component: BrokerDetail,
  }, {
    path: '/admin/operation',
    exact: true,
    component: OperationManagement,
  }, {
    path: '/admin/operation-detail',
    exact: true,
    component: OperationDetail,
  }, {
    path: '/admin/bill',
    exact: true,
    component: BillManagement,
  }, {
    path: '/admin/bill-individual',
    exact: true,
    component: IndividualBill,
  }, {
    path: '/admin/bill-detail',
    exact: true,
    component: BillDetail,
  }, {
    path: '/admin/app',
    exact: true,
    component: PlatformManagement,
  }, {
    path: '/admin/app-detail',
    exact: true,
    component: AppDetail,
  }, {
    path: '/admin/migration-detail',
    exact: true,
    component: MigrationDetail,
  }, {
    path: '/admin/operation-record',
    exact: true,
    component: OperationRecord,
  },];

  constructor(props: any) {
    super(props);
    const search = urlParser().search;
    urlQuery.clusterId = Number(search.clusterId);
    urlQuery.brokerId = Number(search.brokerId);
    urlQuery.group = search.group;
    urlQuery.location = search.location;
    urlQuery.topicName = search.topic;
  }

  public render() {
    return (
      <CommonRoutePage pageRoute={this.pageRoute} mode="admin" active="admin" />
    );
  }
}
