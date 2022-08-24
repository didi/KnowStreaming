import moment from 'moment';

export const CHART_COLOR_LIST = [
  '#657DFC',
  '#A7B1EB',
  '#2AC8E4',
  '#9DDEEB',
  '#3991FF',
  '#94BEF2',
  '#C2D0E3',
  '#F5B6B3',
  '#85C80D',
  '#C9E795',
  '#A76CEC',
  '#CCABF1',
  '#FF9C1B',
  '#F5C993',
  '#FFC300',
  '#F9D77B',
  '#12CA7A',
  '#8BA3C4',
  '#FF7066',
  '#A7E6C7',
  '#F19FC9',
  '#AEAEAE',
  '#D1D1D1',
];

export const UNIT_MAP = {
  TB: Math.pow(1024, 4),
  GB: Math.pow(1024, 3),
  MB: Math.pow(1024, 2),
  KB: 1024,
};

export const DATA_NUMBER_MAP = {
  十亿: Math.pow(1000, 3),
  百万: Math.pow(1000, 2),
  千: 1000,
};

export const getUnit = (value: number) => Object.entries(UNIT_MAP).find(([, size]) => value / size >= 1) || ['Byte', 1];

export const getDataNumberUnit = (value: number) => Object.entries(DATA_NUMBER_MAP).find(([, size]) => value / size >= 1) || ['', 1];

// 图表 tooltip 基础展示样式
const tooltipFormatter = (date: any, arr: any, tooltip: any) => {
  // 从大到小排序
  // arr = arr.sort((a: any, b: any) => b.value - a.value);
  const str = arr
    .map(
      (item: any) => `<div style="margin: 3px 0;">
          <div style="display:flex;align-items:center;">
            <div style="margin-right:4px;width:8px;height:2px;background-color:${item.color};"></div>
            <div style="flex:1;display:flex;justify-content:space-between;align-items:center;overflow: hidden;">
              <span style="font-size:12px;color:#74788D;pointer-events:auto;margin-left:2px;line-height: 18px;font-family: HelveticaNeue;overflow: hidden; text-overflow: ellipsis; white-space: no-wrap;">
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
        // width: 85,
        // overflow: 'truncate',
        // ellipsis: '...',
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
