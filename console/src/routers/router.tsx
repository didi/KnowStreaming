import { BrowserRouter as Router, Route, Switch, Redirect } from 'react-router-dom';
import { hot } from 'react-hot-loader/root';
import * as React from 'react';
import { notification } from 'component/antd';
import { getCookie } from 'lib/utils';

import Home from './page/home';
import Admin from './page/admin';
import Login from './page/login';

class RouterDom extends React.Component {
  public render() {
    return (
      <Router>
        <Switch>
          <Route path="/login" component={Login} />

          <RouteGuard path="/admin/:page" component={Admin} />
          <RouteGuard path="/admin/" exact={true} component={Admin} />
          <RouteGuard path="/user/:page" component={Home} />
          <RouteGuard path="/" component={Home} />

        </Switch>
      </Router>
    );
  }
}

class RouteGuard extends React.Component<any> {
  public isLogin = getCookie('username');
  public isAdmin = getCookie('role');
  public render() {
    const { component: Component, ...rest } = this.props;
    const renderRoute = (props: any) => {
      if (!this.isLogin) {
        return <Redirect to="/login" />;
      } else if (this.props.path.indexOf('admin') !== -1 && this.isAdmin === '0') {
        notification.error({ message: '暂无权限，请联系管理员' });
        window.history.go(-1);
      } else {
        return <Component {...props} />;
      }
    };
    return (
      <Route {...rest} render={renderRoute} />
    );
  }
}
export default hot(RouterDom);
