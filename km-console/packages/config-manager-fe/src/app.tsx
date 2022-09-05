import React from 'react';
import { BrowserRouter as Router, Redirect, Switch } from 'react-router-dom';
import _ from 'lodash';
import './constants/axiosConfig';
import dantdZhCN from 'knowdesign/es/locale/zh_CN';
import dantdEnUS from 'knowdesign/es/locale/en_US';
import intlZhCN from './locales/zh';
import intlEnUS from './locales/en';
import { AppContainer, RouteGuard, DProLayout } from 'knowdesign';
import { leftMenus, systemKey } from './constants/menu';
import { pageRoutes } from './pages';
import './index.less';
interface ILocaleMap {
  [index: string]: any;
}

const localeMap: ILocaleMap = {
  'zh-CN': {
    dantd: dantdZhCN,
    intl: 'zh-CN',
    intlMessages: intlZhCN,
  },
  en: {
    dantd: dantdEnUS,
    intl: 'en',
    intlMessages: intlEnUS,
  },
};

export const { Provider, Consumer } = React.createContext('zh');

const defaultLanguage = 'zh';

const AppContent = (props: {
  getLicenseInfo?: (cbk: (msg: string) => void) => void | undefined;
  licenseEventBus?: Record<string, any> | undefined;
}) => {
  const { getLicenseInfo, licenseEventBus } = props;

  return (
    <div className="config-system">
      <DProLayout.Sider prefixCls={'dcd-two-columns'} width={200} theme={'light'} systemKey={systemKey} menuConf={leftMenus} />
      <DProLayout.Content>
        <RouteGuard
          routeList={pageRoutes}
          beforeEach={() => {
            getLicenseInfo?.((msg) => licenseEventBus?.emit('licenseError', msg));
            return Promise.resolve(true);
          }}
          noMatch={() => <Redirect to="/404" />}
        />
      </DProLayout.Content>
    </div>
  );
};

const App = (props: any) => {
  const { getLicenseInfo, licenseEventBus } = props;
  const intlMessages = _.get(localeMap[defaultLanguage], 'intlMessages', intlZhCN);
  const locale = _.get(localeMap[defaultLanguage], 'intl', 'zh-CN');
  const antdLocale = _.get(localeMap[defaultLanguage], 'dantd', dantdZhCN);

  return (
    <div id="sub-system">
      <AppContainer intlProvider={{ locale, messages: intlMessages }} antdProvider={{ locale: antdLocale }}>
        <Router basename={systemKey}>
          <Switch>
            <AppContent getLicenseInfo={getLicenseInfo} licenseEventBus={licenseEventBus} />
          </Switch>
        </Router>
      </AppContainer>
    </div>
  );
};

export default App;
