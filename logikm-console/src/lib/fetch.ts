import { notification } from 'component/antd';
import { region } from 'store';
import { urlPrefix } from 'constants/left-menu';
import Url from 'lib/url-parser';

const window = self.window;

export interface IRes {
  code: number;
  message: string;
  data: any;
}

const checkStatus = (res: Response) => {
  if (res.status === 401) {
    let location = `${window.location.host}${urlPrefix}/login`;
    if (!/^http(s)?:\/\//.test(location)) {
      location = `${window.location.protocol}//${location}`;
    }

    const jumpto = window.location.href.includes('login') ? urlPrefix : window.location.href;
    window.location.href = `${location}?jumpto=${encodeURIComponent(jumpto)}`;

    return null;
  }

  // if (res.status === 403) {
  //   window.location.href = window.location.origin + `${urlPrefix}/error`;
  //   return res;
  // }

  return res;
};

const filter = (init: IInit) => (res: IRes) => {
  
  if (res.code !== 0 && res.code !== 200) {
    if (!init.errorNoTips) {
      notification.error({
        message: '错误',
        description: res.message || '服务器错误，请重试！',
      });
    }
    throw res;
  }

  return res.data;
};

const prefix = '/api/v1';

interface IInit extends RequestInit {
  errorNoTips?: boolean;
  body?: BodyInit | null | any;
}

const csrfTokenMethod = ['POST', 'PUT', 'DELETE'];

const addCustomHeader = (init?: IInit) => {
  init.headers = Object.assign(init.headers || {}, {
    'X-Data-Center': region.currentRegion,
    'X_Project_Type': 'open',
  });
  return init;
};

const addExtraParameters = (url: string, init?: IInit) => {
  const isPhysical = Url().search.hasOwnProperty('isPhysicalClusterId');

  if (!init.method) { // GET
    url = isPhysical ?
      // tslint:disable-next-line:max-line-length
      url.indexOf('?') > 0 ? `${url}&isPhysicalClusterId=true&dataCenter=${region.currentRegion}` : `${url}?isPhysicalClusterId=true&dataCenter=${region.currentRegion}`
      : url.indexOf('?') > 0 ? `${url}&dataCenter=${region.currentRegion}` : `${url}?dataCenter=${region.currentRegion}`;
  } else {
    const params = isPhysical ? { dataCenter: region.currentRegion, isPhysicalClusterId: true } : { dataCenter: region.currentRegion };
    if (init.body) {
      const body = JSON.parse(init.body);
      Object.assign(body, params);
      init.body = JSON.stringify(body);
    } else {
      init.body = JSON.stringify(params);
    }
  }

  init = addCustomHeader(init);
  return { url, init };
};

export default function fetch(url: string, init?: IInit) {
  if (!init) init = {};

  if (!init.credentials) init.credentials = 'include';
  if (init.body && typeof init.body === 'object') init.body = JSON.stringify(init.body);
  if (init.body && !init.method) init.method = 'POST';
  if (init.method) init.method = init.method.toUpperCase();

  if (csrfTokenMethod.includes(init.method)) {
    init.headers = Object.assign({}, init.headers || {
      'Content-Type': 'application/json',
    });
  }

  const { url: reqUrl, init: reqInit } = addExtraParameters(url, init);
  let realUrl = reqUrl;

  if (!/^http(s)?:\/\//.test(reqUrl)) {
    realUrl = `${prefix}${reqUrl}`;
  }

  return window
    .fetch(realUrl, reqInit)
    .then(res => checkStatus(res))
    .then((res) => res.json())
    .then(filter(reqInit));
}

export function formFetch(url: string, init?: IInit) {
  url = url.indexOf('?') > 0 ?
      `${url}&dataCenter=${region.currentRegion}` : `${url}?dataCenter=${region.currentRegion}`;
  let realUrl = url;

  if (!/^http(s)?:\/\//.test(url)) {
    realUrl = `${prefix}${url}`;
  }

  init = addCustomHeader(init);

  return window
  .fetch(realUrl, init)
  .then(res => checkStatus(res))
  .then((res) => res.json())
  .then(filter(init));
}
