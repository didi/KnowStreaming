import { getUnit, getDataNumberUnit, getBasicChartConfig, CHART_COLOR_LIST } from '@src/constants/chartConfig';
import { MetricType } from '@src/api';
import { MetricsDefine } from '@src/pages/CommonConfig';

export interface MetricInfo {
  name: string;
  desc: string;
  type: number;
  set: boolean;
  support: boolean;
}

// 接口返回图表原始数据类型
export interface MetricDefaultChartDataType {
  metricName: string;
  metricLines: {
    name: string;
    createTime: number;
    updateTime: number;
    metricPoints: {
      aggType: string;
      timeStamp: number;
      value: number;
      createTime: number;
      updateTime: number;
    }[];
  }[];
}

// 格式化后图表数据类型
export interface MetricChartDataType {
  metricName: string;
  metricUnit: string;
  metricLines: {
    name: string;
    data: (string | number)[][];
  }[];
  dragKey?: number;
}

// 补点
export const supplementaryPoints = (
  lines: MetricChartDataType['metricLines'],
  timeRange: readonly [number, number],
  interval: number,
  extraCallback?: (point: [number, 0]) => any[]
) => {
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
  metricData: MetricDefaultChartDataType[],
  getMetricDefine: (type: MetricType, metric: string) => MetricsDefine[keyof MetricsDefine],
  metricType: MetricType,
  timeRange: readonly [number, number],
  supplementaryInterval: number,
  needDrag = false,
  transformUnit: [string, number] = undefined
): MetricChartDataType[] => {
  return metricData.map(({ metricName, metricLines }) => {
    const curMetricInfo = (getMetricDefine && getMetricDefine(metricType, metricName)) || null;
    const isByteUnit = curMetricInfo?.unit?.toLowerCase().includes('byte');
    let maxValue = -1;

    const PointsMapMethod = ({ timeStamp, value }: { timeStamp: number; value: string | number }) => {
      let parsedValue: string | number = Number(value);

      if (Number.isNaN(parsedValue)) {
        parsedValue = value;
      } else {
        // 为避免出现过小的数字影响图表展示效果，图表值统一只保留到小数点后三位
        parsedValue = parseFloat(parsedValue.toFixed(3));
        if (maxValue < parsedValue) maxValue = parsedValue;
      }

      return [timeStamp, parsedValue];
    };

    const chartData = Object.assign(
      {
        metricName,
        metricUnit: curMetricInfo?.unit || '',
        metricLines: metricLines
          .sort((a, b) => Number(a.name < b.name) - 0.5)
          .map(({ name, metricPoints }) => ({
            name,
            data: metricPoints.map(PointsMapMethod),
          })),
      },
      needDrag ? { dragKey: 999 } : {}
    );

    chartData.metricLines.forEach(({ data }) => data.sort((a, b) => (a[0] as number) - (b[0] as number)));
    supplementaryPoints(chartData.metricLines, timeRange, supplementaryInterval);

    // 将所有图表点的值按单位进行转换
    if (maxValue > 0) {
      const [unitName, unitSize]: [string, number] = transformUnit || isByteUnit ? getUnit(maxValue) : getDataNumberUnit(maxValue);
      chartData.metricUnit = isByteUnit
        ? chartData.metricUnit.toLowerCase().replace('byte', unitName)
        : `${unitName}${chartData.metricUnit}`;
      chartData.metricLines.forEach(({ data }) => data.forEach((point: any) => (point[1] /= unitSize)));
    }

    return chartData;
  });
};

const seriesCallback = (lines: { name: string; data: [number, string | number][] }[]) => {
  const len = CHART_COLOR_LIST.length;
  // series 配置
  return lines.map((line, i) => {
    return {
      ...line,
      lineStyle: {
        width: 1.5,
      },
      connectNulls: false,
      symbol: 'emptyCircle',
      symbolSize: 4,
      smooth: 0.25,
      areaStyle: {
        color: {
          type: 'linear',
          x: 0,
          y: 0,
          x2: 0,
          y2: 1,
          colorStops: [
            {
              offset: 0,
              color: CHART_COLOR_LIST[i % len] + '10',
            },
            {
              offset: 1,
              color: 'rgba(255,255,255,0)', // 100% 处的颜色
            },
          ],
          global: false, // 缺省为 false
        },
      },
    };
  });
};

// 返回图表配置
export const getChartConfig = (title: string, metricLength: number) => {
  return {
    option: getBasicChartConfig({
      title: { show: false },
      grid: { top: 24 },
      tooltip: { enterable: metricLength > 9, legendContextMaxHeight: 192 },
      color: CHART_COLOR_LIST,
      // xAxis: {
      //   type: 'time',
      //   boundaryGap: ['5%', '5%'],
      // },
    }),
    seriesCallback,
  };
};

export const getDetailChartConfig = (title: string, sliderPos: readonly [number, number]) => {
  return {
    option: getBasicChartConfig({
      title: {
        show: false,
      },
      xAxis: {
        type: 'time',
        boundaryGap: false,
      },
      legend: {
        show: false,
      },
      color: CHART_COLOR_LIST,
      dataZoom: [
        {
          type: 'inside',
          startValue: sliderPos[0],
          endValue: sliderPos[1],
          zoomOnMouseWheel: false,
          minValueSpan: 10 * 60 * 1000,
        },
        {
          start: 0,
          end: 0,
        },
      ],
    }),
    seriesCallback,
  };
};
