import moment from 'moment';
import { MetricType } from '@src/api';
import { MetricsDefine } from '@src/pages/CommonConfig';

export interface MetricInfo {
  name: string;
  desc: string;
  type: number;
  set: boolean;
  rank: number | null;
  support: boolean;
}

// 接口返回图表原始数据类型
export interface OriginMetricData {
  metricName: string;
  metricLines?: {
    name: string;
    createTime: number;
    updateTime: number;
    metricPoints: {
      aggType: string;
      timeStamp: number;
      value: string;
      createTime: number;
      updateTime: number;
    }[];
  }[];
  metricPoints?: {
    aggType: string;
    name: string;
    timeStamp: number;
    value: string;
  }[];
}

// 格式化后图表数据类型
export interface FormattedMetricData {
  metricName: string;
  metricUnit: string;
  metricType: MetricType;
  metricLines: {
    name: string;
    data: (string | number)[][];
  }[];
  showLegend: boolean;
  targetUnit: [string, number] | undefined;
}

// 图表颜色库
export const CHART_COLOR_LIST = [
  '#556ee6',
  '#94BEF2',
  '#95e7ff',
  '#9DDEEB',
  '#A7B1EB',
  '#C2D0E3',
  '#CCABF1',
  '#F9D77B',
  '#F5C993',
  '#A7E6C7',
  '#F19FC9',
  '#F5B6B3',
  '#C9E795',
];

// 图表存储单位
export const MEMORY_MAP = {
  TB: Math.pow(1024, 4),
  GB: Math.pow(1024, 3),
  MB: Math.pow(1024, 2),
  KB: 1024,
};
// 图表时间单位
export const TIME_MAP = {
  h: 1000 * 60 * 60,
  min: 1000 * 60,
  s: 1000,
};
// 图表数字单位
export const NUM_MAP = {
  十亿: Math.pow(1000, 3),
  百万: Math.pow(1000, 2),
  千: 1000,
};
const calculateUnit = (map: { [unit: string]: number }, targetValue: number) => {
  return Object.entries(map).find(([, size]) => targetValue / size >= 1);
};
const getMemoryUnit = (value: number) => calculateUnit(MEMORY_MAP, value) || ['Byte', 1];
const getTimeUnit = (value: number) => calculateUnit(TIME_MAP, value) || ['ms', 1];
const getNumUnit = (value: number) => calculateUnit(NUM_MAP, value) || ['', 1];

export const getDataUnit = {
  Memory: getMemoryUnit,
  Time: getTimeUnit,
  Num: getNumUnit,
};
export type DataUnitType = keyof typeof getDataUnit;

// 图表补点间隔计算
export const SUPPLEMENTARY_INTERVAL_MAP = {
  '0': 60 * 1000,
  // 6 小时：10 分钟间隔
  '21600000': 10 * 60 * 1000,
  // 24 小时：1 小时间隔
  '86400000': 60 * 60 * 1000,
};
export const getSupplementaryInterval = (range: number) => {
  return Object.entries(SUPPLEMENTARY_INTERVAL_MAP)
    .reverse()
    .find(([curRange]) => range > Number(curRange))[1];
};

// 处理图表排序
export const resolveMetricsRank = (metricList: MetricInfo[]) => {
  const isRanked = metricList.some(({ rank }) => rank !== null);
  let shouldUpdate = false;
  let list: string[] = [];
  if (isRanked) {
    const rankedMetrics = metricList.filter(({ rank }) => rank !== null).sort((a, b) => a.rank - b.rank);
    const unRankedMetrics = metricList.filter(({ rank }) => rank === null);
    // 如果有新增/删除指标的情况，需要触发更新
    if (unRankedMetrics.length || rankedMetrics.some(({ rank }, i) => rank !== i)) {
      shouldUpdate = true;
    }
    list = [...rankedMetrics.map(({ name }) => name), ...unRankedMetrics.map(({ name }) => name).sort()];
  } else {
    shouldUpdate = true;
    // 按字母先后顺序初始化指标排序
    list = metricList.map(({ name }) => name).sort();
  }

  return {
    list,
    listInfo: list.map((metric, rank) => ({ metric, rank, set: metricList.find(({ name }) => metric === name)?.set || false })),
    shouldUpdate,
  };
};

// 图表补点
export const supplementaryPoints = (
  lines: FormattedMetricData['metricLines'],
  timeRange: readonly [number, number],
  extraCallback?: (point: [number, 0]) => any[]
): void => {
  const interval = getSupplementaryInterval(timeRange[1] - timeRange[0]);

  lines.forEach(({ data }) => {
    // 获取未补点前线条的点的个数
    let len = data.length;
    // 记录当前处理到的点的下标值
    let i = 0;

    for (; i < len; i++) {
      if (i === 0) {
        let firstPointTimestamp = data[0][0] as number;
        while (firstPointTimestamp - interval > timeRange[0]) {
          const prevPointTimestamp = firstPointTimestamp - interval;
          data.unshift(extraCallback ? extraCallback([prevPointTimestamp, 0]) : [prevPointTimestamp, 0]);
          firstPointTimestamp = prevPointTimestamp;
          len++;
          i++;
        }
      }

      if (i === len - 1) {
        let lastPointTimestamp = data[i][0] as number;
        while (lastPointTimestamp + interval < timeRange[1]) {
          const nextPointTimestamp = lastPointTimestamp + interval;
          data.push(extraCallback ? extraCallback([nextPointTimestamp, 0]) : [nextPointTimestamp, 0]);
          lastPointTimestamp = nextPointTimestamp;
        }
        break;
      }

      {
        let timestamp = data[i][0] as number;
        while (timestamp + interval < data[i + 1][0]) {
          const nextPointTimestamp = timestamp + interval;
          data.splice(i + 1, 0, extraCallback ? extraCallback([nextPointTimestamp, 0]) : [nextPointTimestamp, 0]);
          timestamp = nextPointTimestamp;
          len++;
          i++;
        }
      }
    }
  });
};

// 格式化图表数据
export const formatChartData = (
  // 图表源数据
  metricsData: OriginMetricData[],
  // 获取指标单位
  getMetricDefine: (type: MetricType, metric: string) => MetricsDefine[keyof MetricsDefine],
  // 指标类型
  metricType: MetricType,
  // 图表时间范围，用于补点
  timeRange: readonly [number, number],
  targetUnit: [string, number] = undefined
): FormattedMetricData[] => {
  return metricsData.map((originData) => {
    const { metricName } = originData;
    const curMetricInfo = (getMetricDefine && getMetricDefine(metricType, metricName)) || null;
    let showLegend = true;
    let metricLines = [];
    let maxValue = -1;
    let unitType: DataUnitType;

    if (originData?.metricLines && originData?.metricLines !== null) {
      metricLines = originData.metricLines;
    } else {
      showLegend = false;
      metricLines = [
        {
          name: metricName,
          metricPoints: originData.metricPoints,
        },
      ];
    }

    {
      const originUnit = curMetricInfo?.unit?.toLowerCase();
      unitType = originUnit.includes('byte') ? 'Memory' : originUnit.includes('ms') ? 'Time' : 'Num';
    }

    const PointsMapMethod = ({ timeStamp, value }: { timeStamp: number; value: string | number }) => {
      let parsedValue: string | number = Number(value);

      if (Number.isNaN(parsedValue)) {
        parsedValue = value;
      } else {
        // 为避免出现过小的数字影响图表展示效果，图表值统一保留小数点后三位
        parsedValue = parseFloat(parsedValue.toFixed(3));
        if (maxValue < parsedValue) maxValue = parsedValue;
      }

      return [timeStamp, parsedValue];
    };

    // 初始化返回结构
    const chartData: FormattedMetricData = {
      metricName,
      metricType,
      metricUnit: curMetricInfo?.unit || '',
      metricLines: metricLines
        .sort((a, b) => Number(a.name < b.name) - 0.5)
        .map(({ name, metricPoints }) => ({
          name,
          data: metricPoints.map(PointsMapMethod),
        })),
      showLegend,
      targetUnit: undefined,
    };
    // 按时间先后进行对图表点排序
    chartData.metricLines.forEach(({ data }) => data.sort((a, b) => (a[0] as number) - (b[0] as number)));

    // 图表值单位转换
    if (maxValue > 0) {
      const [unitName, unitSize]: [string, number] = targetUnit || getDataUnit[unitType](maxValue);
      chartData.targetUnit = [unitName, unitSize];
      chartData.metricUnit =
        unitType !== 'Num'
          ? chartData.metricUnit.toLowerCase().replace(unitType === 'Memory' ? 'byte' : 'ms', unitName)
          : `${unitName}${chartData.metricUnit}`;
      chartData.metricLines.forEach(({ data }) => data.forEach((point: any) => parseFloat((point[1] /= unitSize).toFixed(3))));
    }

    // 补点
    supplementaryPoints(chartData.metricLines, timeRange);

    return chartData;
  });
};

// 图表 tooltip 基础展示样式
const tooltipFormatter = (date: any, arr: any, tooltip: any) => {
  const str = arr
    .map(
      (item: any) => `<div style="margin: 3px 0;">
          <div style="display:flex;align-items:center;">
            <div style="margin-right:4px;width:8px;height:2px;background-color:${item.color};"></div>
            <div style="flex:1;display:flex;justify-content:space-between;align-items:center;overflow: hidden;">
              <span style="font-size:12px;color:#74788D;pointer-events:auto;margin-left:2px;line-height: 18px;font-family: HelveticaNeue;overflow: hidden; text-overflow: ellipsis; white-space: nowrap;">
                ${item.seriesName}
              </span>
              <span style="font-size:12px;color:#212529;line-height:18px;font-family:HelveticaNeue-Medium; padding-left: 6px;">
                ${parseFloat(Number(item.value[1]).toFixed(3))}
              </span>
            </div>
          </div>
        </div>`
    )
    .join('');

  return `<div style="margin: 0px 0 0; position: relative; z-index: 99;width: ${
    tooltip.customWidth ? tooltip.customWidth + 'px' : 'fit-content'
  };">
    <div style="padding: 8px 0;height: 100%;">
      <div style="font-size:12px;padding: 0 12px;color:#212529;line-height:20px;font-family: HelveticaNeue;">
        ${date}
      </div>
      <div style="${
        tooltip.legendContextMaxHeight ? 'max-height: ' + tooltip.legendContextMaxHeight + 'px' : ''
      }; margin: 4px 0 0 0;overflow-y:auto;padding: 0 12px;">
        ${str}
      </div>
    </div>
  </div>`;
};

// 折线图基础主题配置，返回 echarts 配置项。详见 https://echarts.apache.org/zh/option.html
export const getBasicChartConfig = (props: any = {}) => {
  const { title = {}, grid = {}, legend = {}, xAxis = {}, yAxis = {}, tooltip = {}, ...restConfig } = props;
  return {
    title: {
      show: true,
      text: '示例标题',
      textStyle: {
        fontSize: 14,
        fontFamily: 'HelveticaNeue-Medium',
        color: '#212529',
        letterSpacing: 0.5,
        lineHeight: 16,
        rich: {
          unit: {
            fontSize: 12,
            fontFamily: 'HelveticaNeue-Medium',
            color: '#495057',
            lineHeight: 16,
          },
        },
      },
      top: 12,
      left: 16,
      zlevel: 1,
      ...title,
    },
    // 图表整体布局
    grid: {
      zlevel: 0,
      top: 60,
      left: 22,
      right: 16,
      bottom: 40,
      containLabel: true,
      ...grid,
    },
    // 图例配置
    legend: {
      zlevel: 1,
      type: 'scroll',
      orient: 'horizontal',
      left: 20,
      top: 'auto',
      bottom: 12,
      icon: 'rect',
      itemHeight: 2,
      itemWidth: 8,
      itemGap: 8,
      textStyle: {
        fontSize: 11,
        color: '#74788D',
      },
      pageIcons: {
        horizontal: [
          'path://M474.496 512l151.616 151.616a9.6 9.6 0 0 1 0 13.568l-31.68 31.68a9.6 9.6 0 0 1-13.568 0l-190.08-190.08a9.6 9.6 0 0 1 0-13.568l190.08-190.08a9.6 9.6 0 0 1 13.568 0l31.68 31.68a9.6 9.6 0 0 1 0 13.568L474.496 512z',
          'path://M549.504 512L397.888 360.384a9.6 9.6 0 0 1 0-13.568l31.68-31.68a9.6 9.6 0 0 1 13.568 0l190.08 190.08a9.6 9.6 0 0 1 0 13.568l-190.08 190.08a9.6 9.6 0 0 1-13.568 0l-31.68-31.68a9.6 9.6 0 0 1 0-13.568L549.504 512z',
        ],
      },
      pageIconColor: '#495057',
      pageIconInactiveColor: '#ADB5BC',
      pageIconSize: 6,
      tooltip: false,
      ...legend,
    },
    color: CHART_COLOR_LIST,
    // 横坐标配置
    xAxis: {
      type: 'category',
      boundaryGap: true,
      axisLine: {
        lineStyle: {
          color: '#c5c5c5',
          width: 1,
        },
      },
      axisLabel: {
        formatter: (value: number) => {
          value = Number(value);
          return [`{date|${moment(value).format('MM-DD')}}`, `{time|${moment(value).format('HH:mm')}}`].join('\n');
        },
        padding: 0,
        rich: {
          date: {
            color: '#495057',
            fontSize: 11,
            lineHeight: 18,
            fontFamily: 'HelveticaNeue',
          },
          time: {
            color: '#ADB5BC',
            fontSize: 11,
            lineHeight: 11,
            fontFamily: 'HelveticaNeue',
          },
        },
      },
      ...xAxis,
    },
    // 纵坐标配置
    yAxis: {
      type: 'value',
      axisLabel: {
        color: '#495057',
        fontSize: 12,
      },
      splitLine: {
        show: true,
        lineStyle: {
          width: 1,
          type: 'dashed',
          color: ['#E4E7ED'],
        },
      },
      ...yAxis,
    },
    // 提示框浮层配置
    tooltip: {
      position: function (pos: any, params: any, el: any, elRect: any, size: any) {
        const tooltipWidth = el.offsetWidth || 120;
        const result =
          tooltipWidth + pos[0] < size.viewSize[0]
            ? {
                top: 10,
                left: pos[0] + 30,
              }
            : {
                top: 10,
                left: pos[0] - tooltipWidth - 30,
              };
        return result;
      },
      formatter: function (params: any) {
        let res = '';
        if (params != null && params.length > 0) {
          // 传入tooltip是为了便于拿到外部传入的控制这个自定义浮层的样式
          // 例如tooltip里写customWidth: 200，则tooltipFormatter里可以取出这个宽度使用
          res += tooltipFormatter(moment(Number(params[0].axisValue)).format('YYYY-MM-DD HH:mm'), params, tooltip);
        }
        return res;
      },
      extraCssText:
        'padding: 0;box-shadow: 0 -2px 4px 0 rgba(0,0,0,0.02), 0 2px 6px 6px rgba(0,0,0,0.02), 0 2px 6px 0 rgba(0,0,0,0.06);border-radius: 8px;',
      axisPointer: {
        type: 'line',
      },
      ...tooltip,
    },
    ...restConfig,
  };
};
