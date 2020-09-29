import * as React from 'react';

import { TopicAppList, AllTopic, MineTopic, TopicDetail } from 'container/topic';
import { AppDetail } from 'container/app';
import CommonRoutePage from './common';
import urlParser from 'lib/url-parser';
import urlQuery from 'store/url-query';

export default class Home extends React.Component<any> {
  public pageRoute = [{
    path: '/',
    exact: true,
    component: MineTopic,
  }, {
    path: '/topic',
    exact: true,
    component: MineTopic,
  }, {
    path: '/topic/topic-all',
    exact: true,
    component: AllTopic,
  }, {
    path: '/topic/topic-detail',
    exact: true,
    component: TopicDetail,
  }, {
    path: '/topic/app-list',
    exact: true,
    component: TopicAppList,
  }, {
    path: '/topic/app-detail',
    exact: true,
    component: AppDetail,
  }];

  constructor(props: any) {
    super(props);
    const search = urlParser().search;
    urlQuery.clusterId = Number(search.clusterId);
    urlQuery.brokerId = Number(search.brokerId);
    urlQuery.appId = search.appId;
  }

  public render() {
    return (
      <CommonRoutePage pageRoute={this.pageRoute} active="topic"/>
    );
  }
}
