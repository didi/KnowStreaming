import moment from 'moment';
import { EChartOption } from 'echarts';
import { ICurve, ILineData, baseLineLegend, baseLineGrid, baseAxisStyle, noAxis, UNIT_HEIGHT } from 'container/common-curve/config';
import { IClusterMetrics, ISeriesOption } from 'types/base-type';
import { timeFormat } from 'constants/strategy';
import { getFilterSeries } from 'lib/chart-utils';
import { dealFlowData } from 'lib/chart-utils';

export const getBaseOptions = (option: ICurve, data: ILineData[]) => {
  const date = (data || []).map(i => moment(i.timeStamp).format(timeFormat));

  return {
    animationDuration: 200,
    tooltip: {
      trigger: 'axis',
    },
    toolbox: {
      feature: {
        saveAsImage: {},
      },
    },
    legend: {
      ...baseLineLegend,
      bottom: '0',
      align: 'auto',
    },
    grid: {
      ...baseLineGrid,
    },
    xAxis: {
      type: 'category',
      boundaryGap: false,
      data: date,
      ...baseAxisStyle,
    },
    yAxis: {
      type: 'value',
      ...baseAxisStyle,
      ...noAxis,
      name: option.unit || '',
      nameTextStyle: {
        lineHeight: UNIT_HEIGHT,
      },
    },
    series: [{
      type: 'line',
      data: data.map(i => {
        return Number(i.value);
      }),
    }],
  } as EChartOption;
};

export const parseLine = (option: ICurve, data: ILineData[]): EChartOption => {
  return Object.assign({}, getBaseOptions(option, data), {
    legend: {
      ...baseLineLegend,
      bottom: '0',
      align: 'auto',
    },
  }) as EChartOption;
};

export const parseBrokerMetricOption = (option: ICurve, data: IClusterMetrics[]): EChartOption => {
  let name;
  let series: ISeriesOption[];
  data = data || [];
  data = data.map(item => {
    return {
      time: moment(item.gmtCreate).format(timeFormat),
      ...item,
    };
  });
  data = data.sort((a, b) => a.gmtCreate - b.gmtCreate);
  const legend = option.path === 'byteIn/byteOut' ? ['bytesInPerSec', 'bytesOutPerSec'] : [option.path];
  series = Array.from(legend, (item: string) => ({
    name: item,
    id: item,
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
    data: data.map(row => row[item] !== null ? Number(row[item]) : null),
  }));

  const filterSeries = getFilterSeries(series);
  const { name: unitName, data: xData } = dealFlowData(legend, data);
  name = unitName;
  data = xData;

  return {
    animationDuration: 200,
    tooltip: {
      trigger: 'axis',
    },
    toolbox: {
      feature: {
        saveAsImage: {},
      },
    },
    grid: {
      ...baseLineGrid,
    },
    xAxis: {
      splitLine: null,
      type: 'time',
    },
    yAxis: {
      type: 'value',
      ...baseAxisStyle,
      ...noAxis,
      name,
      nameTextStyle: {
        lineHeight: UNIT_HEIGHT,
      },
    },
    legend: {
      data: legend,
      ...baseLineLegend,
      bottom: '0',
      align: 'auto',
    },
    dataset: {
      source: data,
    },
    series: filterSeries,
  };
};

export function isM(arr: number[]) {
  const filterData = arr.filter(i => i !== 0);
  if (filterData.length) return filterData.reduce((cur, pre) => cur + pre) / filterData.length >= 1000000;
  return false;
}

export function isK(arr: number[]) {
  const filterData = arr.filter(i => i !== 0);
  if (filterData.length) return filterData.reduce((cur, pre) => cur + pre) / filterData.length >= 1000;
  return false;
}
