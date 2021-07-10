import * as React from 'react';
import { MyCluster, ClusterDetail } from 'container/cluster';

import CommonRoutePage from './common';

export default class Cluster extends React.Component<any> {

  public pageRoute = [{
    path: '/cluster',
    exact: true,
    component: MyCluster,
  }, {
    path: '/cluster/cluster-detail',
    exact: true,
    component: ClusterDetail,
  }];

  public render() {
    return (
      <CommonRoutePage pageRoute={this.pageRoute}  mode="cluster" active="cluster"/>
    );
  }
}
