import * as React from 'react';

import 'component/antd';
import './index.less';
import { Header } from 'container/header';
import { LeftMenu } from 'container/left-menu';
import { UserHome } from 'container/user-home';
import { TopicDetail } from 'container/topic-detail';
import AllModalInOne from 'container/modal';
import AllDrawerInOne from 'container/drawer';
import { MyOrder } from 'container/my-order';
import { Alarm } from 'container/alarm';
import { Consumer } from 'container/consumer';
import ModifyUser from 'container/modify-user';
import { BrowserRouter as Router, Route } from 'react-router-dom';

export default class Home extends React.Component<any> {

  public render() {
    const { match } = this.props;
    const page = match.url;
    return (
      <>
        <Header active="user"/>
        <Router>
          <div className="core-container">
            <LeftMenu page={page} />
            <div className="content-container">
              <Route path="/" exact={true} component={UserHome} />
              <Route path="/user/topic_detail" exact={true} component={TopicDetail} />
              <Route path="/user/consumer" exact={true} component={Consumer} />
              <Route path="/user/my_order" exact={true} component={MyOrder} />
              <Route path="/user/alarm" exact={true} component={Alarm} />
              <Route path="/user/modify_user" exact={true} component={ModifyUser} />
            </div>
          </div>
        </Router>
        <AllModalInOne />
        <AllDrawerInOne />
      </>
    );
  }
}
