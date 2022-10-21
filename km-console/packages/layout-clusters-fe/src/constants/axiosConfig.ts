import { Utils } from 'knowdesign';
import notification from '@src/components/Notification';

const { EventBus } = Utils;
export const licenseEventBus = new EventBus();

export const goLogin = () => {
  if (!window.location.pathname.includes('login')) {
    window.history.replaceState({}, '', `/login?redirect=${window.location.href.slice(window.location.origin.length)}`);
  }
};

const serviceInstance = Utils.service;

// 清除 axios 实例默认的响应拦截
(serviceInstance.interceptors.response as any).handlers = [];

// 请求拦截
serviceInstance.interceptors.request.use(
  (config: any) => {
    const user = Utils.getCookie('X-SSO-USER');
    const id = Utils.getCookie('X-SSO-USER-ID');
    if (!user || !id) {
      goLogin();
    } else {
      config.headers['X-SSO-USER'] = user; // 请求携带token
      config.headers['X-SSO-USER-ID'] = id;
    }
    return config;
  },
  (err: any) => {
    return err;
  }
);

// 响应拦截
serviceInstance.interceptors.response.use(
  (config: any) => {
    const res: { code: number; message: string; data: any } = config.data;
    if (res.code !== 0 && res.code !== 200) {
      const desc = res.message;
      // TODO: ---
      if (res.code === 1000000000 || res.code === 1000000001 || res.code === 1000000002) {
        licenseEventBus.emit('licenseError', desc);
      } else {
        notification.error({
          message: '错误信息',
          description: desc,
        });
      }
      throw res;
    }
    return res;
  },
  (err: any) => {
    const config = err?.config;
    if (!config || !config.retryTimes) return dealResponse(err);
    const { __retryCount = 0, retryDelay = 300, retryTimes } = config;
    config.__retryCount = __retryCount;
    if (__retryCount >= retryTimes) {
      return dealResponse(err);
    }
    config.__retryCount++;
    const delay = new Promise<void>((resolve) => {
      setTimeout(() => {
        resolve();
      }, retryDelay);
    });
    // 重新发起请求
    return delay.then(function () {
      return serviceInstance(config);
    });
  }
);

const dealResponse = (error: any) => {
  if (error?.response) {
    switch (error.response.status) {
      case 401:
        goLogin();
        break;
      case 403:
        location.href = '/403';
        break;
      case 405:
        notification.error({
          message: '错误',
          description: `${error.response.data.message || '请求方式错误'}`,
        });
        break;
      case 500:
        notification.error({
          message: '错误',
          description: '服务错误，请重试！',
        });
        break;
      case 502:
        notification.error({
          message: '错误',
          description: '网络错误，请重试！',
        });
        break;
      default:
        notification.error({
          message: '连接出错',
          description: `${error.response.status}`,
        });
    }
  } else {
    notification.error({
      message: '连接超时!',
      description: '请重试或检查服务',
    });
  }
  return Promise.reject(error);
};
