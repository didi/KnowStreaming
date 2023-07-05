import { SingleChart, Utils, Spin, AppContainer, Tooltip } from 'knowdesign';
import React, { useEffect, useRef, useState } from 'react';
import { arrayMoveImmutable } from 'array-move';
import api from '@src/api';
import { useParams } from 'react-router-dom';
import { OriginMetricData, FormattedMetricData, formatChartData, supplementaryPoints } from '@src/constants/chartConfig';
import { MetricType } from '@src/api';
import { getDataUnit } from '@src/constants/chartConfig';
import ChartOperateBar, { KsHeaderOptions } from '@src/components/ChartOperateBar';
import MetricsFilter from '@src/components/ChartOperateBar/MetricSelect';
import RenderEmpty from '@src/components/RenderEmpty';
import DragGroup from '@src/components/DragGroup';
import { getChartConfig } from './config';
import './index.less';

type ChartFilterOptions = Omit<KsHeaderOptions, 'gridNum'>;

interface MessagesInDefaultData {
  aggType: string | null;
  createTime: string | null;
  updateTime: string | null;
  timeStamp: number;
  values: {
    [metric: string]: string;
  };
}

type MessagesInMetric = {
  name: 'MessagesIn';
  unit: string;
  data: (readonly [number, number | string, { key: string; value: number; unit: string }[]])[];
};

const { EventBus } = Utils;
const busInstance = new EventBus();

// 图表颜色定义 & 计算
const CHART_LINE_COLORS = ['#556EE6', '#3991FF'];
const calculateChartColor = (i: number) => {
  const isEvenRow = ((i / 2) | 0) % 2;
  const isEvenCol = i % 2;
  return CHART_LINE_COLORS[isEvenRow ^ isEvenCol];
};

const DEFAULT_METRIC = 'MessagesIn';
// 默认指标图表固定需要获取展示的指标项
const DEFUALT_METRIC_NEED_METRICS = [DEFAULT_METRIC, 'TotalLogSize', 'TotalProduceRequests', 'Topics', 'Partitions'];

const DetailChart = (props: { children: JSX.Element }): JSX.Element => {
  const [global] = AppContainer.useGlobalValue();
  const { clusterId } = useParams<{ clusterId: string }>();
  const [selectedMetricNames, setSelectedMetricNames] = useState<(string | number)[]>([]); // 默认选中的指标的列表
  const [metricDataList, setMetricDataList] = useState<any>([]);
  const [messagesInMetricData, setMessagesInMetricData] = useState<MessagesInMetric>({
    name: 'MessagesIn',
    unit: '',
    data: undefined,
  });
  const [curHeaderOptions, setCurHeaderOptions] = useState<ChartFilterOptions>();
  const [defaultChartLoading, setDefaultChartLoading] = useState<boolean>(true);
  const [chartLoading, setChartLoading] = useState<boolean>(true);
  const metricRankList = useRef<string[]>([]);
  const curFetchingTimestamp = useRef({
    messagesIn: 0,
    other: 0,
  });
  const metricFilterRef = useRef(null);

  // 筛选项变化或者点击刷新按钮
  const ksHeaderChange = (ksOptions: KsHeaderOptions) => {
    // 如果为相对时间，则当前时间减去 1 分钟，避免最近一分钟的数据还没采集到时前端多补一个点
    if (ksOptions.isRelativeRangeTime) {
      ksOptions.rangeTime = ksOptions.rangeTime.map((timestamp) => timestamp - 60 * 1000) as [number, number];
    }
    setCurHeaderOptions({
      isRelativeRangeTime: ksOptions.isRelativeRangeTime,
      isAutoReload: ksOptions.isAutoReload,
      rangeTime: ksOptions.rangeTime,
    });
  };

  // 获取 metric 列表的图表数据
  const getMetricData = () => {
    if (!selectedMetricNames?.length) return;
    !curHeaderOptions.isAutoReload && setChartLoading(true);
    const [startTime, endTime] = curHeaderOptions.rangeTime;

    const curTimestamp = Date.now();
    curFetchingTimestamp.current = {
      ...curFetchingTimestamp.current,
      messagesIn: curTimestamp,
    };
    Utils.request(api.getClusterMetricDataList(), {
      method: 'POST',
      data: {
        startTime,
        endTime,
        clusterPhyIds: [clusterId],
        metricsNames: selectedMetricNames.filter((name) => name !== DEFAULT_METRIC),
      },
    }).then(
      (res: OriginMetricData[]) => {
        // 如果当前请求不是最新请求，则不做任何操作
        if (curFetchingTimestamp.current.messagesIn !== curTimestamp) {
          return;
        }

        const formattedMetricData: FormattedMetricData[] = formatChartData(
          res,
          global.getMetricDefine || {},
          MetricType.Cluster,
          curHeaderOptions.rangeTime
        );
        formattedMetricData.forEach((data) => (data.metricLines[0].name = data.metricName));
        // 指标排序
        formattedMetricData.sort((a, b) => metricRankList.current.indexOf(a.metricName) - metricRankList.current.indexOf(b.metricName));

        setMetricDataList(formattedMetricData);
        setChartLoading(false);
      },
      () => setChartLoading(false)
    );
  };

  // 获取默认展示指标的图表数据
  const getDefaultMetricData = () => {
    !curHeaderOptions.isAutoReload && setDefaultChartLoading(true);

    const curTimestamp = Date.now();
    curFetchingTimestamp.current = {
      ...curFetchingTimestamp.current,
      other: curTimestamp,
    };
    Utils.request(api.getClusterDefaultMetricData(), {
      method: 'POST',
      data: {
        startTime: curHeaderOptions.rangeTime[0],
        endTime: curHeaderOptions.rangeTime[1],
        clusterPhyIds: [clusterId],
        metricsNames: DEFUALT_METRIC_NEED_METRICS,
      },
    }).then(
      (res: MessagesInDefaultData[]) => {
        // 如果当前请求不是最新请求，则不做任何操作
        if (curFetchingTimestamp.current.other !== curTimestamp) {
          return;
        }
        // TODO: 这里直接将指标数据放到数组第三项中，之后可以尝试优化，优化需要注意 tooltipFormatter 函数也要修改
        let maxValue = -1;
        const result = res.map((item) => {
          const { timeStamp, values } = item;
          let parsedValue: string | number = Number(values.MessagesIn);
          if (Number.isNaN(parsedValue)) {
            parsedValue = values.MessagesIn;
          } else {
            // 为避免出现过小的数字影响图表展示效果，图表值统一只保留到小数点后三位
            parsedValue = parseFloat(parsedValue.toFixed(3));
            if (maxValue < parsedValue) maxValue = parsedValue;
          }
          const valuesWithUnit = Object.entries(values).map(([key, value]) => {
            let valueWithUnit = Number(value);
            let unit = ((global.getMetricDefine && global.getMetricDefine(MetricType.Cluster, key)?.unit) || '') as string;
            if (unit.toLowerCase().includes('byte')) {
              const [unitName, unitSize]: [string, number] = getDataUnit['Memory'](Number(value));
              unit = unit.toLowerCase().replace('byte', unitName);
              valueWithUnit /= unitSize;
            }
            const returnValue = {
              key,
              value: valueWithUnit,
              unit,
            };
            return returnValue;
          });
          return [timeStamp, parsedValue || '0', valuesWithUnit] as [number, number | string, typeof valuesWithUnit];
        });
        result.sort((a, b) => (a[0] as number) - (b[0] as number));
        const line = {
          name: 'MessagesIn' as const,
          unit: global.getMetricDefine(MetricType.Cluster, 'MessagesIn')?.unit,
          data: result as any,
        };
        if (maxValue > 0) {
          const [unitName, unitSize]: [string, number] = getDataUnit['Num'](maxValue);
          line.unit = `${unitName}${line.unit}`;
          result.forEach((point) => parseFloat(((point[1] as number) /= unitSize).toFixed(3)));
        }

        if (result.length) {
          // 补充缺少的图表点
          const extraMetrics = result[0][2].map((info) => ({
            ...info,
            value: 0,
          }));
          supplementaryPoints([line], curHeaderOptions.rangeTime, (point) => {
            point.push(extraMetrics as any);
            return point;
          });
        }

        setMessagesInMetricData(line);
        setDefaultChartLoading(false);
      },
      () => setDefaultChartLoading(false)
    );
  };

  // 监听盒子宽度变化，重置图表宽度
  const observeDashboardWidthChange = () => {
    const targetNode = document.getElementsByClassName('dcd-two-columns-layout-sider-footer')[0];
    targetNode && targetNode.addEventListener('click', () => busInstance.emit('chartResize'));
  };

  // 拖拽开始回调，触发图表的 onDrag 事件（ 设置为 true ），禁止同步展示图表的 tooltip
  const dragStart = () => {
    busInstance.emit('onDrag', true);
  };

  // 拖拽结束回调，更新图表顺序，并触发图表的 onDrag 事件（ 设置为 false ），允许同步展示图表的 tooltip
  const dragEnd = ({ oldIndex, newIndex }: { oldIndex: number; newIndex: number }) => {
    busInstance.emit('onDrag', false);
    const originFrom = metricRankList.current.indexOf(metricDataList[oldIndex].metricName);
    const originTarget = metricRankList.current.indexOf(metricDataList[newIndex].metricName);
    const newList = arrayMoveImmutable(metricRankList.current, originFrom, originTarget);
    metricRankList.current = newList;
    metricFilterRef.current?.rankChange(originFrom, originTarget);
    setMetricDataList(arrayMoveImmutable(metricDataList, oldIndex, newIndex));
  };

  useEffect(() => {
    getMetricData();
  }, [selectedMetricNames]);

  useEffect(() => {
    if (curHeaderOptions && curHeaderOptions?.rangeTime.join(',') !== '0,0') {
      getDefaultMetricData();
      getMetricData();
    }
  }, [curHeaderOptions]);

  useEffect(() => {
    setTimeout(() => observeDashboardWidthChange());
  }, []);

  return (
    <div className="chart-panel cluster-detail-container">
      <ChartOperateBar
        openMetricFilter={() => metricFilterRef.current?.open()}
        onChange={ksHeaderChange}
        hideNodeScope={true}
        hideGridSelect={true}
      />
      <MetricsFilter
        ref={metricFilterRef}
        metricType={MetricType.Cluster}
        onSelectChange={(list, rankList) => {
          metricRankList.current = rankList;
          setSelectedMetricNames(list);
        }}
      />

      <div className="cluster-detail-container-main">
        {/* MessageIn 图表 */}
        <div className="header-chart-container">
          <Spin spinning={defaultChartLoading}>
            {messagesInMetricData.data && (
              <>
                <div className="cluster-detail-chart-box-title">
                  <Tooltip
                    placement="topLeft"
                    title={() => {
                      let content = '';
                      const metricDefine = global.getMetricDefine(MetricType.Cluster, messagesInMetricData.name);
                      if (metricDefine) {
                        content = metricDefine.desc;
                      }
                      return content;
                    }}
                  >
                    <span>
                      <span className="name">{messagesInMetricData.name}</span>
                      <span className="unit">（{messagesInMetricData.unit}）</span>
                    </span>
                  </Tooltip>
                </div>
                {messagesInMetricData.data.length ? (
                  <SingleChart
                    chartKey="messagesIn"
                    chartTypeProp="line"
                    showHeader={false}
                    wrapStyle={{
                      width: 'auto',
                      height: 210,
                    }}
                    connectEventName="clusterChart"
                    eventBus={busInstance}
                    propChartData={[messagesInMetricData]}
                    {...getChartConfig({
                      lineColor: CHART_LINE_COLORS[0],
                      isDefaultMetric: true,
                    })}
                  />
                ) : (
                  !defaultChartLoading && <RenderEmpty message="暂无数据" height={200} />
                )}
              </>
            )}
          </Spin>
        </div>

        <div className="content">
          {/* 其余指标图表 */}
          <div className="multiple-chart-container">
            <div className={!metricDataList.length ? 'multiple-chart-container-loading' : ''}>
              <Spin spinning={chartLoading}>
                {metricDataList.length ? (
                  <div className="no-group-con">
                    <DragGroup
                      sortableContainerProps={{
                        onSortStart: dragStart,
                        onSortEnd: dragEnd,
                        axis: 'xy',
                        useDragHandle: true,
                      }}
                      gridProps={{
                        span: 12,
                        gutter: [16, 16],
                      }}
                    >
                      {metricDataList.map((data: any, i: number) => {
                        const { metricName, metricUnit, metricLines } = data;
                        return (
                          <div key={metricName} className="cluster-detail-chart-box">
                            <div className="cluster-detail-chart-box-title">
                              <Tooltip
                                placement="topLeft"
                                title={() => {
                                  let content = '';
                                  const metricDefine = global.getMetricDefine(MetricType.Cluster, metricName);
                                  if (metricDefine) {
                                    content = metricDefine.desc;
                                  }
                                  return content;
                                }}
                              >
                                <span>
                                  <span className="name">{metricName}</span>
                                  <span className="unit">（{metricUnit}）</span>
                                </span>
                              </Tooltip>
                            </div>
                            <SingleChart
                              chartKey={metricName}
                              showHeader={false}
                              chartTypeProp="line"
                              wrapStyle={{
                                width: 'auto',
                                height: 210,
                              }}
                              connectEventName="clusterChart"
                              eventBus={busInstance}
                              propChartData={metricLines}
                              {...getChartConfig({
                                metricName: `${metricName}{unit|（${metricUnit}）}`,
                                lineColor: calculateChartColor(i),
                              })}
                            />
                          </div>
                        );
                      })}
                    </DragGroup>
                  </div>
                ) : chartLoading ? (
                  <></>
                ) : (
                  <RenderEmpty message="请先选择指标或刷新" />
                )}
              </Spin>
            </div>
          </div>
          {/* 历史配置变更记录内容 */}
          <div className="config-change-records-container">{props.children}</div>
        </div>
      </div>
    </div>
  );
};

export default DetailChart;
