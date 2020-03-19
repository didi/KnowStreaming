import { notification } from 'component/antd';

const window = self.window;

export interface IRes {
  code: number;
  message: string;
  data: any;
}

const checkStatus = (res: Response) => {
  if (res.status === 200 && res.redirected) {
    let url = res.url;

    if (!/^http(s)?:\/\//.test(url)) {
      url = `${window.location.protocol}//${url}`;
    }

    if (url) {
      window.location.href = `${url}?jumpto=${encodeURIComponent(window.location.href)}`;
      return null;
    }

    return res;
  }
  return res;
};

const filter = (init: IInit) => (res: IRes) => {
  if (res.code === 401) {
    let url = res.data;

    if (!/^http(s)?:\/\//.test(url)) {
      url = `${window.location.protocol}//${url}`;
    }

    window.location.href = `${url}?jumpto=${encodeURIComponent(window.location.href)}`;
    return null;
  }

  if (res.code !== 0) {
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

const preFix = '/api/v1';

interface IInit extends RequestInit {
  errorNoTips?: boolean;
  body?: BodyInit | null | any;
}

const csrfTokenMethod = ['POST', 'PUT', 'DELETE'];

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

  let realUrl = url;
  if (!/^http(s)?:\/\//.test(url)) {
    realUrl = `${preFix}${url}`;
  }
  return window
    .fetch(realUrl, init)
    .then(res => checkStatus(res))
    .then((res) => res.json())
    .then(filter(init));
}
