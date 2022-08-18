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
        symbol: 'emptyCircle',
        symbolSize: 4,
        emphasis: {
          disabled: true,
        },
      }));
    },
  };
};
