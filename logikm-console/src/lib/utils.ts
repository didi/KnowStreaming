import { IFilter, IStatusMap } from 'types/base-type';
import { urlPrefix } from 'constants/left-menu';
import { message } from 'antd';
import moment, { Moment } from 'moment';
import * as SparkMD5 from 'spark-md5';

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
  setCookie(cData.map(i => ({ key: i, value: '', time: -1 })));
};

export const handleTabKey = (key: string) => {
  location.hash = key;
};

export const tableFilter = <T>(data: T[], name: keyof T, nameMap: IStatusMap = null): IFilter[] => {
  if (!data) return [];
  const obj: any = {};
  return data.reduce((cur, pre) => {

    if (!obj[pre[name]]) {
      obj[pre[name]] = true;
      cur.push({
        text: pre[name] !== undefined && nameMap ? nameMap[(pre[name] as any)] : pre[name],
        value: pre[name],
      });
    }
    return cur;
  }, []);
};

export const handleTableData = (data: any[]) => {
  return data = data.map((item, index) => {
    item.key = index;
    return item;
  });
};

export const computeChecksumMd5 = (file: File) => {
  return new Promise((resolve, reject) => {
    const chunkSize = 2097152; // Read in chunks of 2MB
    const spark = new SparkMD5.ArrayBuffer();
    const fileReader = new FileReader();

    let cursor = 0; // current cursor in file

    fileReader.onerror = () => {
      reject('MD5 computation failed - error reading the file');
    };

    function processChunk(chunkStart: number) {
      const chunkEnd = Math.min(file.size, chunkStart + chunkSize);
      fileReader.readAsArrayBuffer(file.slice(chunkStart, chunkEnd));
    }

    fileReader.onload = (e: any) => {
      spark.append(e.target.result); // Accumulate chunk to md5 computation
      cursor += chunkSize; // Move past this chunk

      if (cursor < file.size) {
        processChunk(cursor);
      } else {
        // Computation ended, last chunk has been processed. Return as Promise value.
        // This returns the base64 encoded md5 hash, which is what
        // Rails ActiveStorage or cloud services expect
        // resolve(btoa(spark.end(true)));

        // If you prefer the hexdigest form (looking like
        // '7cf530335b8547945f1a48880bc421b2'), replace the above line with:
        // resolve(spark.end());
        resolve(spark.end());
      }
    };

    processChunk(0);
  });
};

export const copyString = (url: any) => {
  const input = document.createElement('textarea');
  input.value = url;
  document.body.appendChild(input);
  input.select();
  if (document.execCommand('copy')) {
    message.success('复制成功');
  }
  input.remove();
};

export const onHandleBack = () => {
  window.history.back();
};

export const handlePageBack = (url: string) => {
  window.location.href = `${urlPrefix}${decodeURIComponent(url)}`;
};

export const transMBToB = (value: number) => {
  const val = (value && value * 1024 * 1024) || '';
  return Number(val);
};

export const transBToMB = (value: number) => {
  if (value === null) return '';

  const val = Number.isInteger(value / 1024 / 1024) ? value / 1024 / 1024 : (value / 1024 / 1024).toFixed(2);
  return Number(val);
};

export const transHourToMSecond = (value: number) => {
  const time = (value && value * 1000 * 60 * 60) || '';
  return Number(time);
};

export const transMSecondToHour = (value: number) => {
  if (value === null) return '';

  const time = Number.isInteger(value / 1000 / 60 / 60) ? value / 1000 / 60 / 60 : (value / 1000 / 60 / 60).toFixed(2);
  return Number(time);
};

export const IsNotNaN = (value: any) => {
  return typeof value === 'number' && !isNaN(value);
};

export const disabledDate = (current: Moment) => {
  // Can not select days after today
  return current && current >= moment().endOf('day');
};

export const range = (start: any, end: any) => {
  const result = [];
  for (let i = start; i <= end; i++) {
    result.push(i);
  }
  return result;
};

export const disabledDateTime = (dates: any) => {
  const hours = moment().hours(); // 0~23
  const minutes = moment().minutes(); // 0~59
  const seconds = moment().seconds(); // 0~59
  // 当日只能选择当前时间之后的时间点
  if (dates && moment(dates).date() === moment().date()) {
    return {
      disabledHours: () => range(hours + 1, 23),
      disabledMinutes: () => range(minutes + 1, 59),
      disabledSeconds: () => range(null, null),
    };
  }
  return {
    disabledHours: () => range(null, null),
    disabledMinutes: () => range(null, null),
    disabledSeconds: () => range(null, null),
  };
};
