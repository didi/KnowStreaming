import * as React from 'react';
import { ConfigureInfoPage } from 'container/configure-info';
import { IPageRouteItem } from 'types/base-type';
import { Route } from 'react-router-dom';

export default class InfoPage extends React.Component<any> {

  public pageRoute = [{
    path: '/info',
    exact: true,
    component: ConfigureInfoPage,
  }];

  public render() {
    return (
      <>
        {this.pageRoute.map((item: IPageRouteItem, key: number) =>
          <Route key={key} path={item.path} exact={item.exact} component={item.component} />)}
      </>
    );
  }
}
