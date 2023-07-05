import { getBasicChartConfig } from '@src/constants/chartConfig';

export const getChartConfig = (title: string) => {
  return {
    option: getBasicChartConfig({
      title: { text: title },
      tooltip: { enterable: true },
    }),
    seriesCallback: (lines: { name: string; data: [][] }[]) => {
      // series 配置
      return lines.map((line) => ({
        ...line,
        lineStyle: {
          width: 1,
        },
        smooth: 0.25,
        symbol: 'emptyCircle',
        symbolSize: 4,
        emphasis: {
          disabled: true,
        },
      }));
    },
  };
};

export const HealthStateMap: any = {
  '-1': 'Unknown',
  0: '好',
  1: '中',
  2: '差',
  3: 'Down',
};
