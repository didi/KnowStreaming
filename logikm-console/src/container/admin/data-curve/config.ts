import { EChartOption } from 'echarts/lib/echarts';
import moment from 'moment';
import { ICurve } from 'container/common-curve/config';
import { adminMonitor } from 'store/admin-monitor';
import { parseBrokerMetricOption } from './parser';

export interface IPeriod {
  label: string;
  key: string;
  dateRange: [moment.Moment, moment.Moment];
}

export const getMoment = () => {
  return moment();
};
export const baseColors = ['#F28E61', '#7082A6', '#5AD2A2', '#E96A72', '#59AEE9', '#65A8BF', '#9D7ECF'];

export enum curveKeys {
  'byteIn/byteOut' = 'byteIn/byteOut',
  bytesRejectedPerSec = 'bytesRejectedPerSec',
  failFetchRequestPerSec = 'failFetchRequestPerSec',
  failProduceRequestPerSec = 'failProduceRequestPerSec',
  fetchConsumerRequestPerSec = 'fetchConsumerRequestPerSec',
  healthScore = 'healthScore',
  messagesInPerSec = 'messagesInPerSec',
  networkProcessorIdlPercent = 'networkProcessorIdlPercent',
  produceRequestPerSec = 'produceRequestPerSec',
  requestHandlerIdlPercent = 'requestHandlerIdlPercent',
  requestQueueSize = 'requestQueueSize',
  responseQueueSize = 'responseQueueSize',
  totalTimeFetchConsumer99Th = 'totalTimeFetchConsumer99Th',
  totalTimeProduce99Th = 'totalTimeProduce99Th',
}

export const byteCurves: ICurve[] = [
  {
    title: 'byteIn/byteOut',
    path: curveKeys['byteIn/byteOut'],
    api: adminMonitor.getBrokersChartsData,
    colors: baseColors,
  }, {
    title: 'bytesRejectedPerSec',
    path: curveKeys.bytesRejectedPerSec,
    api: adminMonitor.getBrokersChartsData,
    colors: ['#E96A72'],
  },
];

export const perSecCurves: ICurve[] = [
  {
    title: 'failFetchRequestPerSec',
    path: curveKeys.failFetchRequestPerSec,
    api: adminMonitor.getBrokersChartsData,
    colors: baseColors,
  }, {
    title: 'failProduceRequestPerSec',
    path: curveKeys.failProduceRequestPerSec,
    api: adminMonitor.getBrokersChartsData,
    colors: baseColors,
  }, {
    title: 'fetchConsumerRequestPerSec',
    path: curveKeys.fetchConsumerRequestPerSec,
    api: adminMonitor.getBrokersChartsData,
    colors: baseColors,
  }, {
    title: 'produceRequestPerSec',
    path: curveKeys.produceRequestPerSec,
    api: adminMonitor.getBrokersChartsData,
    colors: baseColors,
  },
];

export const otherCurves: ICurve[] = [
  {
    title: 'healthScore',
    path: curveKeys.healthScore,
    api: adminMonitor.getBrokersChartsData,
    colors: baseColors,
  }, {
    title: 'messagesInPerSec',
    path: curveKeys.messagesInPerSec,
    api: adminMonitor.getBrokersChartsData,
    colors: baseColors,
  }, {
    title: 'networkProcessorIdlPercent',
    path: curveKeys.networkProcessorIdlPercent,
    api: adminMonitor.getBrokersChartsData,
    colors: baseColors,
  }, {
    title: 'requestHandlerIdlPercent',
    path: curveKeys.requestHandlerIdlPercent,
    api: adminMonitor.getBrokersChartsData,
    colors: baseColors,
  }, {
    title: 'requestQueueSize',
    path: curveKeys.requestQueueSize,
    api: adminMonitor.getBrokersChartsData,
    colors: baseColors,
  }, {
    title: 'responseQueueSize',
    path: curveKeys.responseQueueSize,
    api: adminMonitor.getBrokersChartsData,
    colors: baseColors,
  }, {
    title: 'totalTimeFetchConsumer99Th',
    path: curveKeys.totalTimeFetchConsumer99Th,
    api: adminMonitor.getBrokersChartsData,
    colors: baseColors,
  }, {
    title: 'totalTimeProduce99Th',
    path: curveKeys.totalTimeProduce99Th,
    api: adminMonitor.getBrokersChartsData,
    colors: baseColors,
  },
];

export enum curveType {
  byteCurves = 'byteCurves',
  perSecCurves = 'perSecCurves',
  other = 'other',
}

export interface ICurveType {
  type: curveType;
  title: string;
  curves: ICurve[];
  parser: (option: ICurve, data: any[]) => EChartOption;
}

export const byteTypeCurves: ICurveType[] = [
  {
    type: curveType.byteCurves,
    title: 'byte',
    curves: byteCurves.concat(perSecCurves, otherCurves),
    parser: parseBrokerMetricOption,
  },
];
export const perSecTypeCurves: ICurveType[] = [
  {
    type: curveType.perSecCurves,
    title: 'perSec',
    curves: perSecCurves,
    parser: parseBrokerMetricOption,
  },
];
export const otherTypeCurves: ICurveType[] = [
  {
    type: curveType.other,
    title: 'other',
    curves: otherCurves,
    parser: parseBrokerMetricOption,
  },
];

export const allCurves: ICurveType[] = [].concat(byteTypeCurves);

const curveKeyMap = new Map<string, {typeInfo: ICurveType, curveInfo: ICurve}>();
allCurves.forEach(t => {
  t.curves.forEach(c => {
    curveKeyMap.set(c.path, {
      typeInfo: t,
      curveInfo: c,
    });
  });
});
export const CURVE_KEY_MAP = curveKeyMap;

export const PERIOD_RADIO = [
  {
    label: '10分钟',
    key: 'tenMin',
    get dateRange() {
      return [getMoment().subtract(10, 'minute'), getMoment()];
    },
  },
  {
    label: '1小时',
    key: 'oneHour',
    get dateRange() {
      return [getMoment().subtract(1, 'hour'), getMoment()];
    },
  },
  {
    label: '6小时',
    key: 'sixHour',
    get dateRange() {
      return [getMoment().subtract(6, 'hour'), getMoment()];
    },
  },
  {
    label: '近1天',
    key: 'oneDay',
    get dateRange() {
      return [getMoment().subtract(1, 'day'), getMoment()];
    },
  },
  {
    label: '近1周',
    key: 'oneWeek',
    get dateRange() {
      return [getMoment().subtract(7, 'day'), getMoment()];
    },
  },
] as IPeriod[];

const periodRadioMap = new Map<string, IPeriod>();
PERIOD_RADIO.forEach(p => {
  periodRadioMap.set(p.key, p);
});
export const PERIOD_RADIO_MAP = periodRadioMap;
