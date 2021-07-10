import { EChartOption } from 'echarts/lib/echarts';
import moment from 'moment';

export interface ILineData {
  value: number;
  timeStamp: number;
}
export interface ICurve {
  title?: string;
  path: string;
  colors: string[];
  parser?: (option: ICurve, data: ILineData) => EChartOption;
  message?: string;
  unit?: string;
  api?: any;
}

export const LEGEND_HEIGHT = 18;
export const defaultLegendPadding = 10;
export const GRID_HEIGHT = 192;
export const EXPAND_GRID_HEIGHT = 250;
export const TITLE_HEIGHT = 40;
export const UNIT_HEIGHT = 20;
export const LEGEND_PADDING = 10;
export const OPERATOR_TITLE_HEIGHT = 92;

export const baseLineLegend = {
  itemWidth: 12,
  itemHeight: 2,
  icon: 'rect',
  textStyle: {
    lineHeight: LEGEND_HEIGHT,
  },
};

export const baseLineGrid = {
  left: '0',
  right: '2%',
  top: TITLE_HEIGHT + UNIT_HEIGHT,
  height: GRID_HEIGHT,
  containLabel: true,
};

export const baseAxisStyle = {
  nameTextStyle: {
    color: '#A0A4AA',
  },
  axisLine: {
    lineStyle: {
      color: '#A0A4AA',
    },
  },
  splitline: {
    lineStyle: {
      color: '#A0A4AA',
    },
  },
};

export const noAxis = {
  axisLine: {
    lineStyle: {
      color: '#A0A4AA',
    },
    show: false,
  },
  axisTick: {
    show: false,
  },
};

export const getHight = (options: EChartOption) => {
  let grid = options ? options.grid as EChartOption.Grid : null;
  if (!options || !grid) grid = baseLineGrid;
  return Number(grid.height) + getLegendHight(options) + Number(grid.top) + LEGEND_PADDING + UNIT_HEIGHT;
};

export const getLegendHight = (options: EChartOption) => {
  if (!options) return 0;
  if (options.legend.show === false) return 0;
  const legendHight = options.legend.textStyle.lineHeight + defaultLegendPadding;
  if (options.legend.orient !== 'vertical') return legendHight;
  const legendLength = options.series.length;
  return legendHight * legendLength;
};
