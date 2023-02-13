import { BrowserRouter as Router, Route } from 'react-router-dom';
import { hot } from 'react-hot-loader/root';
import * as React from 'react';
import zhCN from 'antd/lib/locale/zh_CN';

import Home from './page/topic';
import Admin from './page/admin';
import Alarm from './page/alarm';
import Cluster from './page/cluster';
import Expert from './page/expert';
import User from './page/user';
import { urlPrefix } from 'constants/left-menu';
import ErrorPage from './page/error';
import Login from './page/login';
import InfoPage from './page/info';
import { ConfigProvider } from 'antd';

class RouterDom extends React.Component {
  public render() {
    return (
      <ConfigProvider locale={zhCN}>

        <Router basename={urlPrefix}>
          <Route path="/" exact={true} component={Home} />
          <Route path={`/topic`} exact={true} component={Home} />
          <Route
            path={`/topic/:page`}
            exact={true}
            component={Home}
          />

          <Route path={`/admin`} exact={true} component={Admin} />
          <Route
            path={`/admin/:page`}
            exact={true}
            component={Admin}
          />

          <Route path={`/user`} exact={true} component={User} />
          <Route path={`/user/:page`} exact={true} component={User} />

          <Route path={`/cluster`} exact={true} component={Cluster} />
          <Route
            path={`/cluster/:page`}
            exact={true}
            component={Cluster}
          />

          <Route path={`/expert`} exact={true} component={Expert} />
          <Route
            path={`/expert/:page`}
            exact={true}
            component={Expert}
          />

          <Route path={`/alarm`} exact={true} component={Alarm} />
          <Route
            path={`/alarm/:page`}
            exact={true}
            component={Alarm}
          />

          <Route
            path={`/login`}
            exact={true}
            component={Login}
          />
          <Route path={`/error`} exact={true} component={ErrorPage} />
          <Route path={`/info`} exact={true} component={InfoPage} />
        </Router>
      </ConfigProvider>
    );
  }
}

export default hot(RouterDom);
