import { getBasicChartConfig, CHART_COLOR_LIST } from '@src/constants/chartConfig';

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
