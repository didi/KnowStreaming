import React, { useEffect, useState } from 'react';
import { MetricType } from '@src/api';
import { FormattedMetricData } from '@src/constants/chartConfig';
import { AppContainer, Empty, SingleChart, Spin, Tooltip } from 'knowdesign';
import { arrayMoveImmutable } from 'array-move';
import DragGroup from '../DragGroup';
import { IconFont } from '@knowdesign/icons';
import { getChartConfig } from './config';
import { EventBus } from 'knowdesign/lib/utils/event-bus';

const DRAG_GROUP_GUTTER_NUM: [number, number] = [16, 16];

interface ChartListProps {
  busInstance: EventBus;
  loading: boolean;
  gridNum: number;
  data: FormattedMetricData[];
  autoReload: boolean;
  dragCallback: (oldIndex: number, newIndex: number) => void;
  onExpand: (metricName: string, metricType: MetricType) => void;
}

const ChartList = (props: ChartListProps) => {
  const { loading, gridNum, data, autoReload, busInstance, dragCallback, onExpand } = props;
  const [global] = AppContainer.useGlobalValue();
  const [chartData, setChartData] = useState(data);
  // 拖拽开始回调，触发图表的 onDrag 事件（ 设置为 true ），禁止同步展示图表的 tooltip
  const dragStart = () => {
    busInstance.emit('onDrag', true);
  };

  // 拖拽结束回调，更新图表顺序，并触发图表的 onDrag 事件（ 设置为 false ），允许同步展示图表的 tooltip
  const dragEnd = ({ oldIndex, newIndex }: { oldIndex: number; newIndex: number }) => {
    dragCallback(oldIndex, newIndex);
    busInstance.emit('onDrag', false);
    setChartData(arrayMoveImmutable(chartData, oldIndex, newIndex));
  };

  // 监听盒子宽度变化，重置图表宽度
  const observeDashboardWidthChange = () => {
    const targetNode = document.getElementsByClassName('dcd-two-columns-layout-sider-footer')[0];
    targetNode && targetNode.addEventListener('click', () => busInstance.emit('chartResize'));
  };

  useEffect(() => {
    setChartData(data);
  }, [data]);

  useEffect(() => {
    setTimeout(() => observeDashboardWidthChange());
  }, []);

  return (
    <>
      <div className="topic-dashboard-container">
        <Spin spinning={loading} style={{ height: 400 }}>
          {chartData && chartData.length ? (
            <div className="no-group-con">
              <DragGroup
                sortableContainerProps={{
                  onSortStart: dragStart,
                  onSortEnd: dragEnd,
                  axis: 'xy',
                  useDragHandle: true,
                }}
                gridProps={{
                  span: gridNum,
                  gutter: DRAG_GROUP_GUTTER_NUM,
                }}
              >
                {chartData.map((data) => {
                  const { metricName, metricType, metricUnit, metricLines, showLegend } = data;

                  return (
                    <div key={metricName} className="dashboard-drag-item-box">
                      <div className="dashboard-drag-item-box-title">
                        <Tooltip
                          placement="topLeft"
                          title={() => {
                            let content = '';
                            const metricDefine = global.getMetricDefine(metricType, metricName);
                            if (metricDefine) {
                              content = metricDefine.desc;
                            }
                            return content;
                          }}
                        >
                          <span>
                            <span className="name">{metricName}</span>
                            <span className="unit">（{metricUnit}）</span>
                            {(metricType === MetricType.Connect || metricType === MetricType.Connectors) && (
                              <span
                                style={{
                                  padding: '2px 8px',
                                  borderRadius: 4,
                                  fontSize: 12,
                                  background: metricType === MetricType.Connect ? '#ECECF6' : 'rgba(85,110,230,0.10)',
                                  color: metricType === MetricType.Connect ? '#495057' : '#5664FF',
                                }}
                              >
                                {metricType === MetricType.Connect ? 'Cluster' : 'Connector'}
                              </span>
                            )}
                          </span>
                        </Tooltip>
                      </div>
                      {metricLines?.length > 0 && (
                        <div className="expand-icon-box" onClick={() => onExpand(metricName, metricType)}>
                          <IconFont type="icon-chuangkoufangda" className="expand-icon" />
                        </div>
                      )}
                      {metricLines?.length > 0 ? (
                        <SingleChart
                          chartKey={metricName}
                          chartTypeProp="line"
                          showHeader={false}
                          wrapStyle={{
                            width: 'auto',
                            height: 222,
                          }}
                          connectEventName={`${metricType}BoardDragChart`}
                          eventBus={busInstance}
                          propChartData={metricLines}
                          optionMergeProps={{ replaceMerge: autoReload ? ['xAxis'] : ['series'] }}
                          {...getChartConfig(`${metricName}{unit|（${metricUnit}）}`, metricLines.length, showLegend)}
                        />
                      ) : (
                        <Empty
                          description="指标采集失败，请刷新或联系管理员排查"
                          image={Empty.PRESENTED_IMAGE_CUSTOM}
                          style={{ padding: '40px 0' }}
                        />
                      )}
                    </div>
                  );
                })}
              </DragGroup>
            </div>
          ) : loading ? (
            <></>
          ) : (
            <Empty description="数据为空，请选择指标或刷新" image={Empty.PRESENTED_IMAGE_CUSTOM} style={{ padding: '100px 0' }} />
          )}
        </Spin>
      </div>
    </>
  );
};

export default ChartList;
