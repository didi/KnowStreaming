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

// 小间隔（1 分钟）图表点的最大请求时间范围，单位: ms
export const MAX_TIME_RANGE_WITH_SMALL_POINT_INTERVAL = 6 * 60 * 60 * 1000;

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

const BUSINESS_VERSION = process.env.BUSINESS_VERSION;

export const getLicenseInfo = (cbk: (msg: string) => void) => {
  if (BUSINESS_VERSION) {
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
