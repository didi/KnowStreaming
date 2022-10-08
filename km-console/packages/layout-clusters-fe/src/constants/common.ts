interface IMap {
  [key: string]: string;
}

export const defaultPagination = {
  current: 1,
  pageSize: 10,
  total: 0,
};

export const defaultPaginationConfig = {
  showQuickJumper: true,
  showSizeChanger: true,
  pageSizeOptions: ['10', '20', '50', '100', '200', '500'],
  showTotal: (total: number) => `共 ${total} 条`,
};

export const cellStyle = {
  overflow: 'hidden',
  whiteSpace: 'nowrap' as any,
  textOverflow: 'ellipsis',
  cursor: 'pointer',
  maxWidth: 150,
};

export const systemCipherKey = 'Szjx2022@666666$';

export const oneDayMillims = 24 * 60 * 60 * 1000;

export const classNamePrefix = 'bdp';

export const timeFormat = 'YYYY-MM-DD HH:mm:ss';

export const dateFormat = 'YYYY-MM-DD';

export const SMALL_DRAWER_WIDTH = 480;
export const MIDDLE_DRAWER_WIDTH = 728;
export const LARGE_DRAWER_WIDTH = 1080;

export const primaryColor = '#556EE6';

export const numberToFixed = (value: number, num = 2) => {
  if (value === null || isNaN(value)) return '-';
  value = Number(value);
  return Number.isInteger(value) ? value : value.toFixed(num);
};

const K = 1024;
const M = 1024 * K;
const G = 1024 * M;
const T = 1024 * G;

export const getSizeAndUnit: any = (value: any, unitSuffix?: any, num = 2) => {
  if (value === null || value === undefined || isNaN(+value)) {
    return { value: null, unit: '', valueWithUnit: '-' };
  }

  if (value <= K) {
    return { value: numberToFixed(value, num), unit: '' + unitSuffix, valueWithUnit: numberToFixed(value) + '' + unitSuffix };
  }
  if (value > K && value < M) {
    return { value: numberToFixed(value / K, num), unit: 'K' + unitSuffix, valueWithUnit: numberToFixed(value / K) + 'K' + unitSuffix };
  }
  if (value >= M && value < G) {
    return { value: numberToFixed(value / M, num), unit: 'M' + unitSuffix, valueWithUnit: numberToFixed(value / M) + 'M' + unitSuffix };
  }
  if (value >= G && value < T) {
    return { value: numberToFixed(value / G, num), unit: 'G' + unitSuffix, valueWithUnit: numberToFixed(value / G) + 'G' + unitSuffix };
  }
  if (value >= T) {
    return { value: numberToFixed(value / T, num), unit: 'T' + unitSuffix, valueWithUnit: numberToFixed(value / T) + 'T' + unitSuffix };
  }
  return '-';
};

export const orderTypeMap: any = {
  ascend: 'asc',
  descend: 'desc',
};

export const dealTableRequestParams = ({ searchKeywords, pageNo, pageSize, sorter, filters, isPhyId = undefined }: any) => {
  const _params = {
    searchKeywords,
    pageNo,
    pageSize,
    isPhyId,
  };
  if (sorter && sorter.field && sorter.order) {
    Object.assign(_params, {
      sortField: sorter.field,
      sortType: orderTypeMap[sorter.order],
    });
  }
  if (filters) {
    const filterDTOList = [];
    for (const key of Object.keys(filters)) {
      if (filters[key]) {
        filterDTOList.push({
          fieldName: key,
          fieldValueList: filters[key],
        });
      }
    }
    Object.assign(_params, {
      filterDTOList,
    });
  }

  return _params;
};

// url hash Parse
export const hashDataParse = (hash: string) => {
  const newHashData: any = {};
  hash
    .slice(1)
    .split('&')
    .forEach((str: string) => {
      const hashStr = str.split('=');
      newHashData[hashStr[0]] = hashStr[1];
    });

  return newHashData;
};

export const urlParser = () => {
  const Url = {
    hash: {} as IMap,
    search: {} as IMap,
  } as {
    hash: IMap;
    search: IMap;
    [key: string]: IMap;
  };

  window.location.hash
    .slice(1)
    .split('&')
    .forEach((str) => {
      const kv = str.split('=');
      Url.hash[kv[0]] = kv[1];
    });

  window.location.search
    .slice(1)
    .split('&')
    .forEach((str) => {
      const kv = str.split('=');
      Url.search[kv[0]] = kv[1];
    });

  return Url;
};

export const getLicenseInfo = (cbk: (msg: string) => void) => {
  if (process.env.BUSINESS_VERSION) {
    const info = (window as any).code;
    if (!info) {
      setTimeout(() => getLicenseInfo(cbk), 1000);
    } else {
      const res = info() || {};
      if (res.code !== 0) {
        cbk(res.msg);
      }
    }
  }
};

export const getRandomStr = (length?: number) => {
  const NUM_list = [0, 1, 2, 3, 4, 5, 6, 7, 8, 9];
  const LOW_LETTERS_LIST = [
    'a',
    'b',
    'c',
    'd',
    'e',
    'f',
    'g',
    'h',
    'i',
    'j',
    'k',
    'l',
    'm',
    'n',
    'o',
    'p',
    'q',
    'r',
    's',
    't',
    'u',
    'v',
    'w',
    'x',
    'y',
    'z',
  ];
  const CAP_LETTERS_LIST = LOW_LETTERS_LIST.map((v) => v.toUpperCase());
  const SPECIAL_LIST = [
    '!',
    '"',
    '#',
    '$',
    '%',
    '&',
    "'",
    '(',
    ')',
    '*',
    '+',
    '-',
    '.',
    '/',
    ':',
    ';',
    '<',
    '=',
    '>',
    '?',
    '@',
    '[',
    '\\',
    ']',
    '^',
    '_',
    '`',
    '{',
    '|',
    '}',
    '~',
  ];
  const ALL_LIST = [...NUM_list, ...LOW_LETTERS_LIST, ...CAP_LETTERS_LIST];
  const randomNum = (Math.random() * 128) | 0;
  const randomKeys = new Array(length ?? randomNum).fill('');

  for (let i = 0; i < randomKeys.length; i++) {
    // ALL_LIST 随机字符
    const index = (Math.random() * ALL_LIST.length) | 0;
    randomKeys[i] = ALL_LIST[index - 1];
  }
  return randomKeys.join('');
};

export const timeFormater = function formatDuring(mss: number) {
  const days = Math.floor(mss / (1000 * 60 * 60 * 24));
  const hours = Math.floor((mss % (1000 * 60 * 60 * 24)) / (1000 * 60 * 60));
  const minutes = Math.floor((mss % (1000 * 60 * 60)) / (1000 * 60));
  const seconds = (mss % (1000 * 60)) / 1000;
  const parts = [
    { v: days, unit: '天' },
    { v: hours, unit: '小时' },
    { v: minutes, unit: '分钟' },
    { v: seconds, unit: '秒' },
  ];
  return parts
    .filter((o) => o.v > 0)
    .map((o: any) => `${o.v}${o.unit}`)
    .join();
};

// 列表页Header布局前缀
export const tableHeaderPrefix = 'table-header-layout';
