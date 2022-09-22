import { Col, Row, SingleChart, IconFont, Utils, Modal, Spin, Empty, AppContainer, Tooltip } from 'knowdesign';
import React, { useEffect, useRef, useState } from 'react';
import api from '@src/api';
import { getChartConfig } from './config';
import './index.less';
import { useParams } from 'react-router-dom';
import {
  MetricDefaultChartDataType,
  MetricChartDataType,
  formatChartData,
  supplementaryPoints,
} from '@src/components/DashboardDragChart/config';
import { MetricType } from '@src/api';
import { getDataNumberUnit, getUnit } from '@src/constants/chartConfig';
import SingleChartHeader, { KsHeaderOptions } from '@src/components/SingleChartHeader';
import { MAX_TIME_RANGE_WITH_SMALL_POINT_INTERVAL } from '@src/constants/common';
import RenderEmpty from '@src/components/RenderEmpty';
import DragGroup from '@src/components/DragGroup';

type ChartFilterOptions = Omit<KsHeaderOptions, 'gridNum'>;
interface MetricInfo {
  type: number;
  name: string;
  desc: string;
  set: boolean;
  support: boolean;
}

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
  const [metricList, setMetricList] = useState<MetricInfo[]>([]); // 指标列表
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
  const [showChartDetailModal, setShowChartDetailModal] = useState<boolean>(false);
  const [chartDetail, setChartDetail] = useState<any>();
  const curFetchingTimestamp = useRef({
    messagesIn: 0,
    other: 0,
  });

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

  // 获取指标列表
  const getMetricList = () => {
    Utils.request(api.getDashboardMetricList(clusterId, MetricType.Cluster)).then((res: MetricInfo[] | null) => {
      if (!res) return;
      const showMetrics = res.filter((metric) => metric.support);
      const selectedMetrics = showMetrics.filter((metric) => metric.set).map((metric) => metric.name);
      !selectedMetrics.includes(DEFAULT_METRIC) && selectedMetrics.push(DEFAULT_METRIC);
      setMetricList(showMetrics);
      setSelectedMetricNames(selectedMetrics);
    });
  };

  // 更新指标
  const updateMetricList = (metricsSet: { [name: string]: boolean }) => {
    return Utils.request(api.getDashboardMetricList(clusterId, MetricType.Cluster), {
      method: 'POST',
      data: {
        metricsSet,
      },
    });
  };

  // 指标选中项更新回调
  const indicatorChangeCallback = (newMetricNames: (string | number)[]) => {
    const updateMetrics: { [name: string]: boolean } = {};
    // 需要选中的指标
    newMetricNames.forEach((name) => !selectedMetricNames.includes(name) && (updateMetrics[name] = true));
    // 取消选中的指标
    selectedMetricNames.forEach((name) => !newMetricNames.includes(name) && (updateMetrics[name] = false));

    const requestPromise = Object.keys(updateMetrics).length ? updateMetricList(updateMetrics) : Promise.resolve();
    requestPromise.then(
      () => getMetricList(),
      () => getMetricList()
    );

    return requestPromise;
  };

  // 获取 metric 列表的图表数据
  const getMetricData = () => {
    if (!selectedMetricNames.length) return;
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
      (res: MetricDefaultChartDataType[]) => {
        // 如果当前请求不是最新请求，则不做任何操作
        if (curFetchingTimestamp.current.messagesIn !== curTimestamp) {
          return;
        }

        const supplementaryInterval = (endTime - startTime > MAX_TIME_RANGE_WITH_SMALL_POINT_INTERVAL ? 10 : 1) * 60 * 1000;
        const formattedMetricData: MetricChartDataType[] = formatChartData(
          res,
          global.getMetricDefine || {},
          MetricType.Cluster,
          curHeaderOptions.rangeTime,
          supplementaryInterval
        );
        formattedMetricData.forEach((data) => (data.metricLines[0].name = data.metricName));
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
              const [unitName, unitSize]: [string, number] = getUnit(Number(value));
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
          return [timeStamp, values.MessagesIn || '0', valuesWithUnit] as [number, number | string, typeof valuesWithUnit];
        });
        result.sort((a, b) => (a[0] as number) - (b[0] as number));
        const line = {
          name: 'MessagesIn' as const,
          unit: global.getMetricDefine(MetricType.Cluster, 'MessagesIn')?.unit,
          data: result as any,
        };
        if (maxValue > 0) {
          const [unitName, unitSize]: [string, number] = getDataNumberUnit(maxValue);
          line.unit = `${unitName}${line.unit}`;
          result.forEach((point) => ((point[1] as number) /= unitSize));
        }

        if (result.length) {
          // 补充缺少的图表点
          const extraMetrics = result[0][2].map((info) => ({
            ...info,
            value: 0,
          }));
          const supplementaryInterval =
            (curHeaderOptions.rangeTime[1] - curHeaderOptions.rangeTime[0] > MAX_TIME_RANGE_WITH_SMALL_POINT_INTERVAL ? 10 : 1) * 60 * 1000;
          supplementaryPoints([line], curHeaderOptions.rangeTime, supplementaryInterval, (point) => {
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
    getMetricList();
    setTimeout(() => observeDashboardWidthChange());
  }, []);

  return (
    <div className="chart-panel cluster-detail-container">
      <SingleChartHeader
        onChange={ksHeaderChange}
        hideNodeScope={true}
        hideGridSelect={true}
        indicatorSelectModule={{
          hide: false,
          metricType: MetricType.Cluster,
          tableData: metricList,
          selectedRows: selectedMetricNames,
          checkboxProps: (record: MetricInfo) => {
            return record.name === DEFAULT_METRIC
              ? {
                  disabled: true,
                }
              : {};
          },
          submitCallback: indicatorChangeCallback,
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
                        onSortStart: () => 0,
                        onSortEnd: () => 0,
                        axis: 'xy',
                        useDragHandle: false,
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
