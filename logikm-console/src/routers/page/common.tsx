import * as React from 'react';

import './index.less';
import 'styles/font.css';
import 'styles/header-search.less';
import { Header } from 'container/header';
import { LeftMenu } from 'container/left-menu';
import AllWrapperInOne from 'container/wrapper';
import { BrowserRouter as Router, Route } from 'react-router-dom';
import { IPageRouteItem } from 'types/base-type';
import { region } from 'store/region';
import { observer } from 'mobx-react';
import { users } from 'store/users';
import { FullScreen } from 'container/full-screen';
import Url from 'lib/url-parser';
import AllCustomModalInOne from 'container/wrapper/custom-modal';

interface ICommonRoute {
  pageRoute: IPageRouteItem[];
  mode?: 'admin' | 'user' | 'topic' | 'cluster' | 'expert' | 'alarm';
  active: string;
}

@observer
export default class CommonRoutePage extends React.Component<ICommonRoute> {
  constructor(props: ICommonRoute) {
    super(props);
    const url = Url();
    const index = region.regionIdcList.findIndex(item => item.idc === url.search.region);
    if (index > -1) {
      region.setRegion(region.regionIdcList[index]);
    }
  }

  public componentDidMount() {
    region.getRegionIdcs();
    users.getAccount();
  }

  public render() {
    const { pageRoute, mode, active } = this.props;
    return (
      <>
        <Header active={active} />
        <div className="core-container">
          <LeftMenu mode={mode} />
          <div className="content-container">
            {pageRoute.map((item: IPageRouteItem, key: number) =>
              <Route key={key} path={item.path} exact={item.exact} component={item.component} />)}
          </div>
        </div>
        <AllWrapperInOne />
        <AllCustomModalInOne />
        <FullScreen />
      </>
    );
  }
}
