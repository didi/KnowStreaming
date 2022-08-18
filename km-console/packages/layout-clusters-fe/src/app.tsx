/* eslint-disable no-constant-condition */
import '@babel/polyfill';
import React, { useState, useEffect, useLayoutEffect } from 'react';
import { BrowserRouter, Switch, Route, useLocation, useHistory } from 'react-router-dom';
import { get as lodashGet } from 'lodash';
import { DProLayout, AppContainer, IconFont, Menu, Utils, Page403, Page404, Page500, Modal } from 'knowdesign';
import dantdZhCN from 'knowdesign/lib/locale/zh_CN';
import dantdEnUS from 'knowdesign/lib/locale/en_US';
import { DotChartOutlined } from '@ant-design/icons';
import { licenseEventBus } from './constants/axiosConfig';
import intlZhCN from './locales/zh';
import intlEnUS from './locales/en';
import registerApps from '../config/registerApps';
import feSystemsConfig from '../config/systemsConfig';
import './index.less';
import { Login } from './pages/Login';
import { getLicenseInfo } from './constants/common';
import api from './api';
import ClusterContainer from './pages/index';
import NoLicense from './pages/NoLicense';
import ksLogo from './assets/ks-logo.png';

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

const primaryFeConf = feSystemsConfig.feConfig;
const systemsConfig = feSystemsConfig.systemsConfig as any;
const defaultLanguage = 'zh-CN';

export const { Provider, Consumer } = React.createContext('zh');

const judgePage404 = () => {
  const { pathname } = window.location;
  const paths = pathname.split('/');
  const exceptionLocationPaths = ['/404', '/500', '/403'];
  const row = systemsConfig.filter((item: any) => item.ident === paths?.[1]);
  if (exceptionLocationPaths.indexOf(pathname) < -1 && paths?.[1] && !row.length) {
    window.location.href = '/404';
  }
};

const logout = () => {
  Utils.request(api.logout, {
    method: 'POST',
  }).then((res) => {
    window.location.href = '/login';
  });
};

const LicenseLimitModal = () => {
  const [visible, setVisible] = useState<boolean>(false);
  const [msg, setMsg] = useState<string>('');

  useLayoutEffect(() => {
    licenseEventBus.on('licenseError', (desc: string) => {
      !visible && setVisible(true);
      setMsg(desc);
    });
    return () => {
      licenseEventBus.removeAll('licenseError');
    };
  }, []);

  return (
    <Modal
      visible={visible}
      centered={true}
      width={400}
      zIndex={10001}
      title={
        <>
          <IconFont type="icon-yichang" style={{ marginRight: 10, fontSize: 18 }} />
          许可证限制
        </>
      }
      footer={null}
      onCancel={() => setVisible(false)}
    >
      <div style={{ margin: '0 28px', lineHeight: '24px' }}>
        <div>
          {msg}，<a>前往帮助文档</a>
        </div>
      </div>
    </Modal>
  );
};

const AppContent = (props: { setlanguage: (language: string) => void }) => {
  const { pathname } = useLocation();
  const history = useHistory();
  const userInfo = localStorage.getItem('userInfo');
  const [curActiveAppName, setCurActiveAppName] = useState('');

  useEffect(() => {
    if (pathname.startsWith('/config')) {
      setCurActiveAppName('config');
    } else {
      setCurActiveAppName('cluster');
    }
  }, [pathname]);

  return (
    <DProLayout.Container
      headerProps={{
        title: (
          <div>
            <img className="header-logo" src={ksLogo} />
          </div>
        ),
        username: userInfo ? JSON.parse(userInfo)?.userName : '',
        icon: <DotChartOutlined />,
        quickEntries: [
          {
            icon: <IconFont type="icon-duojiqunguanli" />,
            txt: '多集群管理',
            ident: '',
            active: curActiveAppName === 'cluster',
          },
          {
            icon: <IconFont type="icon-xitongguanli" />,
            txt: '系统管理',
            ident: 'config',
            active: curActiveAppName === 'config',
          },
        ],
        isFixed: false,
        userDropMenuItems: [
          <Menu.Item key={0} onClick={logout}>
            登出
          </Menu.Item>,
        ],
        onChangeLanguage: props.setlanguage,
        onClickQuickEntry: (qe) => {
          history.push({
            pathname: '/' + (qe.ident || ''),
          });
        },
        onClickMain: () => {
          history.push('/');
        },
      }}
      onMount={(customProps: any) => {
        judgePage404();
        registerApps(systemsConfig, { ...customProps, getLicenseInfo, licenseEventBus }, () => {
          // postMessage();
        });
      }}
    >
      <>
        <Switch>
          <Route path="/403" exact component={Page403} />
          <Route path="/404" exact component={Page404} />
          <Route path="/500" exact component={Page500} />
          <Route
            render={() => {
              return (
                <>
                  {curActiveAppName === 'cluster' && <ClusterContainer />}
                  <div id="ks-layout-container" />
                </>
              );
            }}
          />
        </Switch>
        <LicenseLimitModal />
      </>
    </DProLayout.Container>
  );
};

export default function App(): JSX.Element {
  const [language, setlanguage] = useState(navigator.language.substr(0, 2));
  const intlMessages = lodashGet(localeMap[language], 'intlMessages', intlZhCN);
  const [feConf] = useState(primaryFeConf || {});
  const locale = lodashGet(localeMap[defaultLanguage], 'intl', 'zh-CN');
  const antdLocale = lodashGet(localeMap[defaultLanguage], 'dantd', dantdZhCN);
  const pageTitle = lodashGet(feConf, 'title');

  if (pageTitle) {
    document.title = pageTitle;
  }

  useEffect(() => {
    window.postMessage(
      {
        type: 'language',
        value: language,
      },
      window.location.origin
    );
  }, [language]);

  return (
    <>
      <AppContainer intlProvider={{ locale, messages: intlMessages }} antdProvider={{ locale: antdLocale }} store>
        <BrowserRouter basename="">
          <Switch>
            <Route path="/login" component={Login} />
            <Route path="/no-license" exact component={NoLicense} />
            <Route render={() => <AppContent setlanguage={setlanguage} />} />
          </Switch>
        </BrowserRouter>
      </AppContainer>
    </>
  );
}
