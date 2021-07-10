import { ISeriesOption, IMetricPoint, IOptionType, ITakeType, IClusterMetrics, ITakeMetric } from 'types/base-type';
import { metricOptionMap } from 'constants/status-map';
import moment = require('moment');
import { timeFormat } from 'constants/strategy';
import { getFilterSeries, baseLineConfig, dealFlowData } from './chart-utils';

type IMetricType = {
  [key in IOptionType | IOptionType]: string[];
};

interface IMetric {
  type: string;
  arr: string[];
}

interface IData {
  [key: string]: number;
}

interface ISeriesCoord {
  coord: any[];
}

const metricTypeMap: IMetricType = {
  'byteIn/byteOut': ['bytesInPerSec', 'bytesOutPerSec'],
  'messageIn': ['messagesInPerSec'],
  // tslint:disable-next-line:max-line-length
  'byteIn/byteOut/appByteIn/appByteOut': ['bytesInPerSec', 'bytesOutPerSec', 'appIdBytesInPerSec', 'appIdBytesOutPerSec'],
  'messageIn/totalProduceRequests': ['messagesInPerSec', 'totalProduceRequestsPerSec'],
  'byteRejected': ['bytesRejectedPerSec'],
  'topicNum': ['topicNum'],
  'brokerNum': ['brokerNum'],
};

const lengendType = {
  requestTime99thPercentile: ['fetchRequestTime99thPercentile', 'produceRequestTime99thPercentile'],
  requestTime95thPercentile: ['fetchRequestTime95thPercentile', 'produceRequestTime95thPercentile'],
  requestTime75thPercentile: ['fetchRequestTime75thPercentile', 'produceRequestTime75thPercentile'],
  requestTime50thPercentile: ['fetchRequestTime50thPercentile', 'produceRequestTime50thPercentile'],
};

export const getControlMetricOption = (type: IOptionType, data: IClusterMetrics[]) => {
  let name;
  let series: ISeriesOption[];
  data = data || [];
  data = data.map(item => {
    return {
      time: moment(item.gmtCreate).format(timeFormat),
      ...item,
    };
  });
  const legend = metricTypeMap[type] || [type];
  series = Array.from(legend, (item: IOptionType) => ({
    id: item,
    name: item,
    type: 'line',
    symbol: 'circle',
    showSymbol: false,
    smooth: true,
    encode: {
      x: 'time',
      y: item,
      tooltip: [
        item,
      ],
    },
    data: data.map(i => {
      const seriesType = item as keyof IClusterMetrics;
      return i[seriesType] !== null ? Number(i[seriesType]) : null;
    }),
  }));
  switch (type) {
    case 'byteRejected':
      name = 'B/s';
      break;
    case 'messageIn':
      name = '条';
      data.map(item => {
        item.messagesInPerSec = item.messagesInPerSec !== null ? Number(item.messagesInPerSec.toFixed(2)) : null;
      });
      break;
    case 'brokerNum':
    case 'topicNum':
      name = '个';
      break;
    default:
      const { name: unitName, data: xData } = dealFlowData(metricTypeMap[type], data);
      name = unitName;
      data = xData;
      break;
  }
  const filterSeries = getFilterSeries(series);
  return {
    ...baseLineConfig,
    xAxis: {
      type: 'time',
      splitLine: false,
    },
    yAxis: {
      ...baseLineConfig.yAxis,
      name,
    },
    legend: {
      data: legend,
      ...baseLineConfig.legend,
    },
    dataset: {
      source: data,
    },
    series: filterSeries,
  };
};

export const clusterMetricOption = (type: string, record: IData[], metricMap: IMetric[]) => {
  const metrics = record;
  let field = [] as string[];
  const ele = metricMap.find((ele: IMetric) => ele.type === type);
  if (ele) {
    field = ele.arr;
  }
  if (!field.length) { // 表示metricMap中无比对项
    return record;
  }
  const metricOption = [] as any;
  metrics.forEach((ele: any) => {
    let nullAmount = 0;
    field.forEach(e => {
      if (ele[e] === null) {
        ++nullAmount;
      }
    });
    if (nullAmount !== field.length) {
      metricOption.push(ele);
    }
  });

  return metricOption;
};

const addMarkPoint = (series: ISeriesOption[], data: IClusterMetrics[], unitName: string) => {
  const markPoint = {
    symbol: 'pin',
    symbolSize: 12,
    itemStyle: {
      color: '#ff0000',
    },
  };

  const dataMap = {
    fetch: [] as ISeriesCoord[],
    produce: [] as ISeriesCoord[],
  };

  data.map((item) => {
    if (item.produceThrottled && item.appIdBytesInPerSec) {
      dataMap.produce.push({
        coord: [moment(item.gmtCreate).format(timeFormat), item.appIdBytesInPerSec],
      });
    }
    if (item.consumeThrottled && item.appIdBytesOutPerSec) {
      dataMap.fetch.push({
        coord: [moment(item.gmtCreate).format(timeFormat), item.appIdBytesOutPerSec],
      });
    }
    return item;
  });

  series = series.map(item => {
    if (item.name === 'appIdBytesOutPerSec') {
      item.markPoint = Object.assign({}, markPoint);
      item.markPoint.data = dataMap.fetch;
      return item;
    }
    if (item.name === 'appIdBytesInPerSec') {
      item.markPoint = Object.assign({}, markPoint);
      item.markPoint.data = dataMap.produce;
      return item;
    }
    return item;
  });
};

export const getClusterMetricOption = (type: IOptionType, record: IClusterMetrics[]) => {
  let data = clusterMetricOption(type, record, metricOptionMap) as IClusterMetrics[];
  let name;
  let series: ISeriesOption[];

  data = data.map(item => {
    return {
      time: moment(item.gmtCreate).format(timeFormat),
      ...item,
    };
  });
  data = data.sort((a, b) => a.gmtCreate - b.gmtCreate);
  const legend = metricTypeMap[type] || [type];
  series = Array.from(legend, (item: string) => ({
    id: item,
    name: item,
    type: 'line',
    symbol: 'circle',
    showSymbol: false,
    smooth: true,
    encode: {
      x: 'time',
      y: item,
      tooltip: [
        item,
      ],
    },
    data: data.map((i: any) => {
      const seriesType = item as keyof IClusterMetrics;

      return i[seriesType] !== null ? Number(i[seriesType]) : null;
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
      data.map(item => {
        item.messagesInPerSec = item.messagesInPerSec !== null ? Number(item.messagesInPerSec.toFixed(2)) : null;
      });
      break;
    default:
      const { name: unitName, data: xData } = dealFlowData(metricTypeMap[type], data);
      name = unitName;
      data = xData;
      break;
  }

  const filterSeries = getFilterSeries(series);
  if (type === 'byteIn/byteOut/appByteIn/appByteOut') {
    addMarkPoint(series, data, name);
  }

  return {
    ...baseLineConfig,
    tooltip: {
      ...baseLineConfig.tooltip,
      formatter: (params: any) => {
        let result = params[0].data.time + '<br>';
        params.forEach((item: any) => {
          const unitSeries = item.data[item.seriesName] !== null ? Number(item.data[item.seriesName]) : null;
          // tslint:disable-next-line:max-line-length
          result += '<span style="display:inline-block;margin-right:0px;border-radius:10px;width:9px;height:9px;background-color:' + item.color + '"></span>';
          if ((item.data.produceThrottled && item.seriesName === 'appIdBytesInPerSec')
            || (item.data.consumeThrottled && item.seriesName === 'appIdBytesOutPerSec')) {
            return result += item.seriesName + ': ' + unitSeries + '（被限流）' + '<br>';
          }
          return result += item.seriesName + ': ' + unitSeries + '<br>';
        });
        return result;
      },
    },
    xAxis: {
      type: 'time',
      splitLine: false,
    },
    legend: {
      ...baseLineConfig.legend,
      data: legend,
    },
    yAxis: {
      ...baseLineConfig.yAxis,
      name,
    },
    dataset: {
      source: data,
    },
    series: filterSeries,
  };
};

export const getMonitorMetricOption = (seriesName: string, data: IMetricPoint[]) => {
  let series: ISeriesOption[];
  data = data || [];
  data = data.map(item => {
    return {
      ...item,
      time: moment(item.timestamp * 1000).format(timeFormat),
    };
  });

  series = [{
    name: seriesName,
    type: 'line',
    symbol: 'circle',
    id: seriesName,
    showSymbol: false,
    smooth: true,
    encode: {
      x: 'time',
      y: 'value',
      tooltip: [
        'value',
      ],
    },
    data: data.map(i => {
      return i.value !== null ? Number(i.value) : null;
    }),
  }];
  const filterSeries = getFilterSeries(series);

  return {
    ...baseLineConfig,
    tooltip: {
      ...baseLineConfig.tooltip,
      formatter: (params: any) => {
        let result = params[0].value.time + '<br>';
        params.forEach((item: any) => {
          series.forEach((ele: any) => {
            if (ele.name === item.seriesName) {
              // tslint:disable-next-line:max-line-length
              result += '<span style="display:inline-block;margin-right:0px;border-radius:10px;width:9px;height:9px;background-color:' + item.color + '"></span>';
              return result += item.seriesName + ': ' + (item.data.value === null ? '' : item.data.value.toFixed(2)) + '<br>';
            }
          });
        });
        return result;
      },
    },
    xAxis: {
      type: 'time',
      splitLine: false,
    },
    yAxis: {
      ...baseLineConfig.yAxis,
    },
    dataset: {
      source: data,
    },
    series: filterSeries,
  };
};

export const getClusterMetricTake = (type: ITakeType, data: ITakeMetric[]) => {
  let series: ISeriesOption[];
  data = data || [];

  let legend = [] as string[];
  for (const key in lengendType) {
    if (key === type) {
      legend = lengendType[key];
    }
  }
  data = data.map(item => {
    return {
      time: moment(item.gmtCreate).format(timeFormat),
      ...item,
    };
  });

  series = Array.from(legend, (item: ITakeType) => ({
    id: item,
    name: item,
    type: 'line',
    symbol: 'circle',
    showSymbol: false,
    smooth: true,
    encode: {
      x: 'time',
      y: item,
      tooltip: [
        item,
      ],
    },
    data: data.map(i => {
      const seriesType = item as keyof ITakeMetric;
      return i[seriesType] !== null ? Number(i[seriesType]) : null;
    }),
  }));

  const filterSeries = getFilterSeries(series);
  return {
    ...baseLineConfig,
    tooltip: {
      ...baseLineConfig.tooltip,
      formatter: (params: any) => {
        let result = params[0].value.time + '<br>';
        params.forEach((item: any) => {
          series.forEach((ele: any) => {
            if (ele.name === item.seriesName) {
              // tslint:disable-next-line:max-line-length
              result += '<span style="display:inline-block;margin-right:0px;border-radius:10px;width:9px;height:9px;background-color:' + item.color + '"></span>';
              return result += item.seriesName + ': ' + (item.data[item.seriesName].toFixed(2) || '') + '<br>';
            }
          });
        });
        return result;
      },
    },
    xAxis: {
      type: 'time',
      splitLine: false,
    },
    legend: {
      ...baseLineConfig.legend,
      data: legend,
    },
    yAxis: {
      ...baseLineConfig.yAxis,
      name: 'ms',
    },
    dataset: {
      source: data,
    },
    series: filterSeries,
  };
};
