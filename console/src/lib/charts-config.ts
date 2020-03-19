import { ISeriesOption, IOptionType, IClusterMetrics, IBrokerMetrics, IValueLabel } from 'types/base-type';
import moment = require('moment');

export const getClusterMetricOption = (type: IOptionType, data: IClusterMetrics[]) => {
  let name;
  let series: ISeriesOption[];
  const date = data.map(i => moment(i.gmtCreate).format('YYYY-MM-DD HH:mm:ss'));
  const legend = type === 'byteIn/byteOut' ? ['bytesInPerSec', 'bytesOutPerSec'] :
   type === 'messageIn/totalProduceRequests' ? ['messagesInPerSec', 'totalProduceRequestsPerSec'] : [type];
  series = Array.from(legend, (item: IOptionType) => ({
    name: item,
    type: 'line',
    symbol: 'circle',
    data: data.map(i => {
      let seriesType = item as keyof IClusterMetrics;
      if (type !== 'byteIn/byteOut' && type !== 'messageIn/totalProduceRequests') {
        seriesType = item === 'byteRejected' ? 'bytesRejectedPerSec' : 'messagesInPerSec';
      }
      return Number(i[seriesType]);
    }),
  }));
  switch (type) {
    case 'byteRejected':
      name = 'B/s';
      break;
    case 'messageIn/totalProduceRequests':
      name = 'QPS';
      break;
    case 'messageIn':
      name = '条';
      break;
    default:
      if (series.map(i => isMB(i.data)).some(i => i === true)) {
        name = 'MB/s';
        series.map(i => {i.data = i.data.map(ele => (ele / (1024 * 1024)).toFixed(2)) as []; });
      } else {
        name = 'KB/s';
        series.map(i => {i.data = i.data.map(ele => (ele / 1024).toFixed(2)) as []; });
      }
  }
  return {
    tooltip: {
      trigger: 'axis',
      padding: 10,
      backgroundColor: 'rgba(0,0,0,0.7)',
      borderColor: '#333',
      textStyle: {
        color: '#f3f3f3',
        fontSize: '12px',
      },
    },
    xAxis: {
      boundaryGap: false,
      data: date,
    },
    legend: {
      data: legend,
      right: '1%',
      top: '10px',
    },
    yAxis: {
      type: 'value',
      name,
      nameLocation: 'end',
      nameGap: 10,
    },
    grid: {
      left: '1%',
      right: '1%',
      bottom: '3%',
      top: '40px',
      containLabel: true,
    },
    series,
  };
};

export const getBrokerMetricOption = (charts: IValueLabel[], data: any) => {
  const legend: string[] = [];
  const seriesData = new Map();
  let date: string[] = [];
  const valueData = Object.keys(data).map(item => {
    legend.push(item);
    return data[item];
  });

  if (valueData.length) {
    date = valueData[0].map((i: IBrokerMetrics) => moment(i.gmtCreate).format('HH:mm:ss'));
  }
  charts.forEach(item => {
    if (!item.value) return false;
    seriesData.set(item.value, legend.map((ele, index) => {
        return {
          name: ele,
          type: 'line',
          smooth: true,
          symbol: 'circle',
          data: valueData[index].map((i: IBrokerMetrics) => i[item.value as keyof IBrokerMetrics]),
        };
      }));
  });
  return charts.map(i => {
    if (!i.value) return null;
    return {
      tooltip: {
        trigger: 'axis',
        padding: 10,
        backgroundColor: 'rgba(0,0,0,0.7)',
        borderColor: '#333',
        textStyle: {
          color: '#f3f3f3',
          fontSize: '12px',
        },
      },
      xAxis: {
        boundaryGap: false,
        data: date,
      },
      legend: {
        data: legend,
        right: '1%',
        top: '10px',
      },
      yAxis: {},
      grid: {
        left: '1%',
        right: '1%',
        bottom: '3%',
        top: '40px',
        containLabel: true,
      },
      series: seriesData.get(i.value),
    };
  });
};

function isMB(arr: number[]) {
  const filterData = arr.filter(i => i !== 0);
  if (filterData.length) return filterData.reduce((cur, pre) => cur + pre) / filterData.length >= 100000;
  return false;
}
