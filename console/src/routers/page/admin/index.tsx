import * as React from 'react';

import 'component/antd';
import { Header } from 'container/header';
import { LeftMenu } from 'container/left-menu';
import AllModalInOne from 'container/modal';
import { TopicDetail } from 'container/topic-detail';
import { BrokerDetail } from 'container/broker-detail';
import { BrokerInfo } from 'container/broker-info';
import { AdminHome } from 'container/admin-home';
import AdminTopic from 'container/admin-topic';
import { BrowserRouter as Router, Route } from 'react-router-dom';
import { AdminOrder } from 'container/admin-order';
import urlParser from 'lib/url-parser';
import urlQuery from 'store/url-query';
// import { AdminAlarm } from 'container/admin-alarm';
import { AdminRegion } from 'container/admin-region';
import { AdminController } from 'container/admin-controller';
import { AdminConsume } from 'container/admin-consume';
// import { ConsumerDetail } from 'container/admin-consume/detail';
import { AdminOperation } from 'container/admin-operation';
import { UserManage } from 'container/admin-usermanage';
import ModifyUser from 'container/modify-user';
import AllDrawerInOne from 'container/drawer';
import { ClusterDetail } from 'container/admin-home/cluster-detail';

export default class Home extends React.Component<any> {
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
    const { match } = this.props;
    const page = match.url;
    return (
      <>
        <Header active="admin"/>
        <Router>
          <div className="core-container">
            <LeftMenu page={page} mode="admin" />
            <div className="content-container">
              <Route path="/admin" exact={true} component={AdminHome} />
              <Route path="/admin/broker_detail" exact={true} component={BrokerDetail} />
              <Route path="/admin/controller" exact={true} component={AdminController} />
              <Route path="/admin/consumer" exact={true} component={AdminConsume} />
              {/* <Route path="/admin/consumer_detail" exact={true} component={ConsumerDetail} /> */}
              <Route path="/admin/broker_info" exact={true} component={BrokerInfo} />
              <Route path="/admin/region" exact={true} component={AdminRegion} />
              <Route path="/admin/topic" exact={true} component={AdminTopic} />
              <Route path="/admin/topic_detail" exact={true} component={TopicDetail} />
              <Route path="/admin/order" exact={true} component={AdminOrder} />
              {/* <Route path="/admin/alarm" exact={true} component={AdminAlarm} /> */}
              <Route path="/admin/operation" exact={true} component={AdminOperation} />
              <Route path="/admin/user_manage" exact={true} component={UserManage} />
              <Route path="/admin/modify_user" exact={true} component={ModifyUser} />
              <Route path="/admin/cluster_detail" exact={true} component={ClusterDetail} />
            </div>
          </div>
        </Router>
        <AllModalInOne />
        <AllDrawerInOne />
      </>
    );
  }
}
