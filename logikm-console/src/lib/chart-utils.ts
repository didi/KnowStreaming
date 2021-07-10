import { ISeriesOption } from 'types/base-type';

export const isArrElementAllNull = (arr: any[]) => {
  let isAllNull = true;
  arr.forEach(item => {
    if (item !== null) {
      isAllNull = false;
    }
  });
  return isAllNull;
};

export const dealFlowData = (metricList: string[], data: any[]) => {
  let name = '';
  const metricData = [] as Array<{
    metric: string,
    data: number[],
  }>;
  metricList.map(metric => {
    metricData.push({
      metric,
      data: data.map(item => item[metric]),
    });
  });

  if (metricData.map(i => isMB(i.data)).some(i => i === true)) {
    name = 'MB/s';
    metricList.map(metric => {
      data.map(item => {
        item[metric] = item[metric] !== null ? Number((item[metric] / (1024 * 1024)).toFixed(2)) : null;
       });
    });
  } else if (metricData.map(i => isKB(i.data)).some(i => i === true)) {
    name = 'KB/s';
    metricList.map(metric => {
      data.map(item => {
        item[metric] = item[metric] !== null ? Number((item[metric] / (1024)).toFixed(2)) : null;
       });
    });
  } else {
    name = 'B/s';
    metricList.map(metric => {
      data.map(item => {
        item[metric] = item[metric] !== null ? Number(item[metric].toFixed(2)) : null;
       });
    });
  }
  return { name, data };
};

export function isMB(arr: number[]) {
  const filterData = arr.filter(i => i !== 0);
  if (filterData.length) return filterData.reduce((cur, pre) => cur + pre) / filterData.length >= 100000;
  return false;
}

export function isKB(arr: number[]) {
  const filterData = arr.filter(i => i !== 0);
  if (filterData.length) return filterData.reduce((cur, pre) => cur + pre) / filterData.length >= 1000;
  return false;
}

export const getFilterSeries = (series: ISeriesOption[]) => {
  const filterSeries = [].concat(...series);
  const nullIndex: string[] = [];
  for (const row of series) {
    if (isArrElementAllNull(row.data)) {
      nullIndex.push(row.name);
    }
  }

  nullIndex.map(line => {
    const index = filterSeries.findIndex(row => row.name === line);
    if (index > -1) {
      filterSeries.splice(index, 1);
    }
  });
  for (const item of filterSeries) {
    delete item.data;
  }
  return filterSeries;
};

export const baseLineConfig = {
  tooltip: {
    trigger: 'axis',
    padding: 10,
    backgroundColor: 'rgba(0,0,0,0.7)',
    borderColor: '#333',
    textStyle: {
      color: '#f3f3f3',
      fontSize: 10,
    },
  },
  yAxis: {
    type: 'value',
    nameLocation: 'end',
    nameGap: 10,
  },
  legend: {
    right: '1%',
    top: '10px',
  },
  grid: {
    left: '1%',
    right: '1%',
    bottom: '3%',
    top: '40px',
    containLabel: true,
  },
};
