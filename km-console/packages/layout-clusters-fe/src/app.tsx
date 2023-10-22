/* eslint-disable no-constant-condition */
import '@babel/polyfill';
import React, { useState, useEffect, useLayoutEffect } from 'react';
import { BrowserRouter, Switch, Route, useLocation, useHistory } from 'react-router-dom';
import { get as lodashGet } from 'lodash';
import { DProLayout, AppContainer, Menu, Utils, Page500, Modal } from 'knowdesign';
import { IconFont } from '@knowdesign/icons';
import dantdZhCN from 'knowdesign/es/locale/zh_CN';
import dantdEnUS from 'knowdesign/es/locale/en_US';
import { DotChartOutlined } from '@ant-design/icons';
import { licenseEventBus } from './constants/axiosConfig';
import { Page403, Page404, NoLicense } from './pages/ErrorPages';
import intlZhCN from './locales/zh';
import intlEnUS from './locales/en';
import registerApps from '../config/registerApps';
import feSystemsConfig from '../config/systemsConfig';
import './index.less';
import { Login } from './pages/Login';
import { getLicenseInfo } from './constants/common';
import api from './api';
import ClusterContainer from './pages/index';
import ksLogo from './assets/ks-logo.png';
import {ClustersPermissionMap} from "./pages/CommonConfig";

interface ILocaleMap {
  [index: string]: any;
}

interface VersionInfo {
  'git.branch': string;
  'git.build.itme': string;
  'git.build.version': string;
  'git.commit.id': string;
  'git.commit.id.abbrev': string;
  'git.commit.time': string;
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
  localStorage.removeItem('userInfo');
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
  const [versionInfo, setVersionInfo] = useState<VersionInfo>();
  const [global] = AppContainer.useGlobalValue();
  const quickEntries=[];

  useEffect(() => {
    if (pathname.startsWith('/config')) {
      setCurActiveAppName('config');
    } else {
      setCurActiveAppName('cluster');
    }
  }, [pathname]);

  // 获取版本信息
  useEffect(() => {
    Utils.request(api.getVersionInfo()).then((res: VersionInfo) => {
      setVersionInfo(res);
    });
  }, []);

  if (global.hasPermission && global.hasPermission(ClustersPermissionMap.CLUSTERS_MANAGE_VIEW)){
    quickEntries.push({
      icon: <IconFont type="icon-duojiqunguanli"/>,
      txt: '多集群管理',
      ident: '',
      active: curActiveAppName === 'cluster',
    });
  }
  if (global.hasPermission && global.hasPermission(ClustersPermissionMap.SYS_MANAGE_VIEW)){
    quickEntries.push({
      icon: <IconFont type="icon-xitongguanli" />,
      txt: '系统管理',
      ident: 'config',
      active: curActiveAppName === 'config',
    });
  }

  return (
    <DProLayout.Container
      headerProps={{
        title: (
          <div style={{ cursor: 'pointer' }}>
            <img className="header-logo" src={ksLogo} />
          </div>
        ),
        username: userInfo ? JSON.parse(userInfo)?.userName : '',
        icon: <DotChartOutlined />,
        quickEntries: quickEntries,
        isFixed: false,
        userDropMenuItems: [
          <Menu.Item key={0}>
            <a href="https://github.com/didi/KnowStreaming/releases" rel="noreferrer" target="_blank">
              版本: {versionInfo?.['git.build.version']}
            </a>
          </Menu.Item>,
          <Menu.Item key={1} onClick={logout}>
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
