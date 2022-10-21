import { getBasicChartConfig } from '@src/constants/chartConfig';
import moment from 'moment';

const DEFAULT_METRIC = 'MessagesIn';

// 图表 tooltip 展示的样式
const messagesInTooltipFormatter = (date: any, arr: any) => {
  // 面积图只有一条线，这里直接取 arr 的第 0 项
  const params = arr[0];
  // MessageIn 的指标数据存放在 data 数组第 3 项
  const metricsData = params.data[2];

  const str = `<div style="margin: 3px 0;">
    <div style="display:flex;align-items:center;">
      <div style="margin-right:4px;width:8px;height:2px;background-color:${params.color};"></div>
      <div style="flex:1;display:flex;justify-content:space-between;align-items:center;overflow: hidden;">
        <span style="flex: 1;font-size:12px;color:#74788D;pointer-events:auto;margin-left:2px;line-height: 18px;font-family: HelveticaNeue;overflow: hidden; text-overflow: ellipsis; white-space: nowrap;">
          ${params.seriesName}
        </span>
        <span style="font-size:12px;color:#212529;line-height:18px;font-family:HelveticaNeue-Medium;margin-left: 10px;">
          ${parseFloat(Number(params.value[1]).toFixed(3))}
          <span style="font-family: PingFangSC-Regular;color: #495057;">${metricsData[DEFAULT_METRIC]?.unit || ''}</span>
        </span>
      </div>
    </div>
  </div>`;

  return `<div style="margin: 0px 0 0; position: relative; z-index: 99;width: fit-content;">
    <div style="padding: 8px 0;height: 100%;">
      <div style="font-size:12px;padding: 0 12px;color:#212529;line-height:20px;font-family: HelveticaNeue;">
        ${date}
      </div>
      <div style="margin: 4px 0 0 0;padding: 0 12px;">
        ${str}
       <div style="width: 100%; height: 1px; background: #EFF2F7;margin: 8px 0;"></div>
        ${metricsData
          .map(({ key, value, unit }: { key: string; value: number; unit: string }) => {
            if (key === DEFAULT_METRIC) return '';
            return `
            <div style="display: flex; justify-content: space-between; align-items: center;">
              <span style="font-size:12px;color:#74788D;pointer-events:auto;margin-left:2px;line-height: 18px;font-family: HelveticaNeue;margin-right: 10px;">
                ${key}
              </span>
              <span style="font-size:12px;color:#212529;pointer-events:auto;margin-left:2px;line-height: 18px;font-family: HelveticaNeue;">
                ${parseFloat(Number(value).toFixed(3))}
                <span style="font-family: PingFangSC-Regular;color: #495057;">${unit}</span>
              </span>
            </div>
          `;
          })
          .join('')}
      </div>
    </div>
  </div>`;
};

export const getChartConfig = (props: any) => {
  const { lineColor, isDefaultMetric = false } = props;
  return {
    option: getBasicChartConfig({
      // TODO: time 轴图表联动有问题，先切换为 category
      // xAxis: { type: 'time', boundaryGap: isDefaultMetric ? ['2%', '2%'] : ['5%', '5%'] },
      title: { show: false },
      legend: { show: false },
      grid: { top: 24, bottom: 12 },
      tooltip: isDefaultMetric
        ? {
            formatter: function (params: any) {
              let res = '';
              if (params != null && params.length > 0) {
                res += messagesInTooltipFormatter(moment(Number(params[0].axisValue)).format('YYYY-MM-DD HH:mm'), params);
              }
              return res;
            },
          }
        : {},
    }),
    seriesCallback: (lineList: { name: string; data: [number, string | number][] }[]) => {
      // 补充线条配置
      return lineList.map((line) => {
        return {
          ...line,
          lineStyle: {
            width: 1,
          },
          smooth: 0.25,
          symbol: 'emptyCircle',
          symbolSize: 4,
          color: '#556ee6',
          // 面积图样式
          areaStyle: {
            color: lineColor,
            opacity: 0.06,
          },
        };
      });
    },
  };
};
