import * as React from 'react';
import { ForbiddenPage } from 'container/error';
import { IPageRouteItem } from 'types/base-type';
import { Route } from 'react-router-dom';

export default class ErrorPage extends React.Component<any> {

  public pageRoute = [{
    path: '/error',
    exact: true,
    component: ForbiddenPage,
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
