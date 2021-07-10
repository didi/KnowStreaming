import * as React from 'react';
import { HotSpotTopic, PartitionTopic, GovernanceTopic, Diagnosis } from 'container/expert';
import { MigrationDetail } from 'container/admin';

import CommonRoutePage from './common';

export default class Expert extends React.Component<any> {
  public pageRoute = [{
    path: '/expert',
    exact: true,
    component: HotSpotTopic,
  }, {
    path: '/expert/topic-partition',
    exact: true,
    component: PartitionTopic,
  }, {
    path: '/expert/topic-governance',
    exact: true,
    component: GovernanceTopic,
  }, {
    path: '/expert/diagnosis',
    exact: true,
    component: Diagnosis,
  }, {
    path: '/expert/hotspot-detail',
    exact: true,
    component: MigrationDetail,
  }];

  constructor(props: any) {
    super(props);
  }

  public render() {
    return (
      <CommonRoutePage pageRoute={this.pageRoute} mode="expert" active="expert" />
    );
  }
}
