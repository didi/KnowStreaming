import { IFiler } from 'types/base-type';

export interface IMap {
  [index: string]: string;
}
interface ICookie {
  key: string;
  value?: string;
  time?: number;
}
export const getCookie = (key: string): string => {
  const map: IMap = {};
  document.cookie.split(';').map((kv) => {
    const d = kv.trim().split('=');
    map[d[0]] = d[1];
    return null;
  });
  return map[key];
};

export const uuid = (): string => {
  return 'c' + `${Math.random()}`.slice(2);
};

export const getRandomPassword = (len?: number) => {
  const chars = 'ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789';
  if (len) {
    let res = '';
    for (let i = 0; i < len; i++) {
      const id = Math.ceil(Math.random() * 62);
      res += chars[id];
    }
    return res;
  }
  return Math.ceil(Math.random() * 100000);
};

export const setCookie = (cData: ICookie[]) => {
  const date = new Date();
  cData.forEach(ele => {
    date.setTime(date.getTime() + (ele.time * 24 * 60 * 60 * 1000));
    const expires = 'expires=' + date.toUTCString();
    document.cookie = ele.key + '=' + ele.value + '; ' + expires + '; path=/';
  });
};

export const deleteCookie = (cData: string[]) => {
  setCookie(cData.map(i => ({key: i, value: '', time: -1})));
};

export const handleTabKey = (key: string) => {
  location.hash = key;
};

export const tableFilter = <T>(data: T[], name: keyof T): IFiler[] => {
  if (!data) return [];
  const obj: any = {};
  return data.reduce((cur, pre) => {
    if (!obj[pre[name]]) {
      obj[pre[name]] = true;
      cur.push({ text: pre[name], value: pre[name] });
    }
    return cur;
  }, []);
};
