import React, { useState, useEffect, useRef } from 'react';
import { arrayMoveImmutable } from 'array-move';
import { Utils, Empty, IconFont, Spin, AppContainer, SingleChart, Tooltip } from 'knowdesign';
import { useParams } from 'react-router-dom';
import api, { MetricType } from '@src/api';
import SingleChartHeader, { KsHeaderOptions } from '../SingleChartHeader';
import DragGroup from '../DragGroup';
import ChartDetail from './ChartDetail';
import { MetricInfo, MetricDefaultChartDataType, MetricChartDataType, formatChartData, getChartConfig } from './config';
import './index.less';
import { MAX_TIME_RANGE_WITH_SMALL_POINT_INTERVAL } from '@src/constants/common';

interface IcustomScope {
  label: string;
  value: string | number;
}

type ChartFilterOptions = Omit<KsHeaderOptions, 'gridNum'>;

type PropsType = {
  type: MetricType;
};

const { EventBus } = Utils;
const busInstance = new EventBus();

const DRAG_GROUP_GUTTER_NUM: [number, number] = [16, 16];

const DashboardDragChart = (props: PropsType): JSX.Element => {
  const [global] = AppContainer.useGlobalValue();
  const { type: dashboardType } = props;
  const { clusterId } = useParams<{
    clusterId: string;
  }>();
  const [loading, setLoading] = useState<boolean>(true);
  const [scopeList, setScopeList] = useState<IcustomScope[]>([]); // 节点范围列表
  const [metricsList, setMetricsList] = useState<MetricInfo[]>([]); // 指标列表
  const [selectedMetricNames, setSelectedMetricNames] = useState<(string | number)[]>([]); // 默认选中的指标的列表
  const [curHeaderOptions, setCurHeaderOptions] = useState<ChartFilterOptions>();
  const [metricChartData, setMetricChartData] = useState<MetricChartDataType[]>([]); // 指标图表数据列表
  const [gridNum, setGridNum] = useState<number>(12); // 图表列布局
  const chartDetailRef = useRef(null);
  const chartDragOrder = useRef([]);
  const curFetchingTimestamp = useRef(0);

  // 获取节点范围列表
  const getScopeList = async () => {
    const res: any = await Utils.request(api.getDashboardMetadata(clusterId, dashboardType));
    const list = res.map((item: any) => {
      return dashboardType === MetricType.Broker
        ? {
            label: item.host,
            value: item.brokerId,
          }
        : {
            label: item.topicName,
            value: item.topicName,
          };
    });
    setScopeList(list);
  };

  // 获取指标列表
  const getMetricList = () => {
    Utils.request(api.getDashboardMetricList(clusterId, dashboardType)).then((res: MetricInfo[] | null) => {
      if (!res) return;
      const showMetrics = res.filter((metric) => metric.support);
      const selectedMetrics = showMetrics.filter((metric) => metric.set).map((metric) => metric.name);
      setMetricsList(showMetrics);
      setSelectedMetricNames(selectedMetrics);
    });
  };

  // 更新指标
  const setMetricList = (metricsSet: { [name: string]: boolean }) => {
    return Utils.request(api.getDashboardMetricList(clusterId, dashboardType), {
      method: 'POST',
      data: {
        metricsSet,
      },
    });
  };

  // 根据筛选项获取图表信息
  const getMetricChartData = () => {
    !curHeaderOptions.isAutoReload && setLoading(true);
    const [startTime, endTime] = curHeaderOptions.rangeTime;

    const curTimestamp = Date.now();
    curFetchingTimestamp.current = curTimestamp;
    Utils.post(api.getDashboardMetricChartData(clusterId, dashboardType), {
      startTime,
      endTime,
      metricsNames: selectedMetricNames,
      topNu: curHeaderOptions?.scopeData?.isTop ? curHeaderOptions.scopeData.data : null,
      [dashboardType === MetricType.Broker ? 'brokerIds' : 'topics']: curHeaderOptions?.scopeData?.isTop
        ? null
        : curHeaderOptions.scopeData.data,
    }).then(
      (res: MetricDefaultChartDataType[] | null) => {
        // 如果当前请求不是最新请求，则不做任何操作
        if (curFetchingTimestamp.current !== curTimestamp) {
          return;
        }

        if (res === null) {
          // 结果为 null 时，不展示图表
          setMetricChartData([]);
        } else {
          // 格式化图表需要的数据
          const supplementaryInterval = (endTime - startTime > MAX_TIME_RANGE_WITH_SMALL_POINT_INTERVAL ? 10 : 1) * 60 * 1000;
          const formattedMetricData = formatChartData(
            res,
            global.getMetricDefine || {},
            dashboardType,
            curHeaderOptions.rangeTime,
            supplementaryInterval,
            true
          ) as MetricChartDataType[];
          // 处理图表的拖拽顺序
          if (chartDragOrder.current && chartDragOrder.current.length) {
            // 根据当前拖拽顺序排列图表数据
            formattedMetricData.forEach((metric) => {
              const i = chartDragOrder.current.indexOf(metric.metricName);
              metric.dragKey = i === -1 ? 999 : i;
            });
            formattedMetricData.sort((a, b) => a.dragKey - b.dragKey);
          }
          // 更新当前拖拽顺序（处理新增或减少图表的情况）
          chartDragOrder.current = formattedMetricData.map((data) => data.metricName);

          setMetricChartData(formattedMetricData);
        }
        setLoading(false);
      },
      () => {
        if (curFetchingTimestamp.current === curTimestamp) {
          setLoading(false);
        }
      }
    );
  };

  // 筛选项变化或者点击刷新按钮
  const ksHeaderChange = (ksOptions: KsHeaderOptions) => {
    // 重新渲染图表
    if (gridNum !== ksOptions.gridNum) {
      setGridNum(ksOptions.gridNum || 12);
      busInstance.emit('chartResize');
    } else {
      // 如果为相对时间，则当前时间减去 1 分钟，避免最近一分钟的数据还没采集到时前端多补一个点
      if (ksOptions.isRelativeRangeTime) {
        ksOptions.rangeTime = ksOptions.rangeTime.map((timestamp) => timestamp - 60 * 1000) as [number, number];
      }
      setCurHeaderOptions({
        isRelativeRangeTime: ksOptions.isRelativeRangeTime,
        isAutoReload: ksOptions.isAutoReload,
        rangeTime: ksOptions.rangeTime,
        scopeData: ksOptions.scopeData,
      });
    }
  };

  // 指标选中项更新回调
  const indicatorChangeCallback = (newMetricNames: (string | number)[]) => {
    const updateMetrics: { [name: string]: boolean } = {};
    // 需要选中的指标
    newMetricNames.forEach((name) => !selectedMetricNames.includes(name) && (updateMetrics[name] = true));
    // 取消选中的指标
    selectedMetricNames.forEach((name) => !newMetricNames.includes(name) && (updateMetrics[name] = false));

    const requestPromise = Object.keys(updateMetrics).length ? setMetricList(updateMetrics) : Promise.resolve();
    requestPromise.then(
      () => getMetricList(),
      () => getMetricList()
    );

    return requestPromise;
  };

  // 拖拽开始回调，触发图表的 onDrag 事件（ 设置为 true ），禁止同步展示图表的 tooltip
  const dragStart = () => {
    busInstance.emit('onDrag', true);
  };

  // 拖拽结束回调，更新图表顺序，并触发图表的 onDrag 事件（ 设置为 false ），允许同步展示图表的 tooltip
  const dragEnd = ({ oldIndex, newIndex }: { oldIndex: number; newIndex: number }) => {
    busInstance.emit('onDrag', false);
    chartDragOrder.current = arrayMoveImmutable(chartDragOrder.current, oldIndex, newIndex);
    setMetricChartData(arrayMoveImmutable(metricChartData, oldIndex, newIndex));
  };

  // 监听盒子宽度变化，重置图表宽度
  const observeDashboardWidthChange = () => {
    const targetNode = document.getElementsByClassName('dcd-two-columns-layout-sider-footer')[0];
    targetNode && targetNode.addEventListener('click', () => busInstance.emit('chartResize'));
  };

  useEffect(() => {
    if (selectedMetricNames.length && curHeaderOptions) {
      getMetricChartData();
    }
  }, [curHeaderOptions, selectedMetricNames]);

  useEffect(() => {
    // 初始化页面，获取 scope 和 metric 信息
    getScopeList();
    getMetricList();

    setTimeout(() => observeDashboardWidthChange());
  }, []);

  return (
    <div id="dashboard-drag-chart" className="topic-dashboard">
      <SingleChartHeader
        onChange={ksHeaderChange}
        nodeScopeModule={{
          customScopeList: scopeList,
          scopeName: dashboardType === MetricType.Broker ? 'Broker' : 'Topic',
          scopeLabel: `自定义 ${dashboardType === MetricType.Broker ? 'Broker' : 'Topic'} 范围`,
        }}
        indicatorSelectModule={{
          hide: false,
          metricType: dashboardType,
          tableData: metricsList,
          selectedRows: selectedMetricNames,
          submitCallback: indicatorChangeCallback,
        }}
      />
      <div className="topic-dashboard-container">
        <Spin spinning={loading} style={{ height: 400 }}>
          {metricChartData && metricChartData.length ? (
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
                {metricChartData.map((data) => {
                  const { metricName, metricUnit, metricLines } = data;

                  return (
                    <div key={metricName} className="dashboard-drag-item-box">
                      <div className="dashboard-drag-item-box-title">
                        <Tooltip
                          placement="topLeft"
                          title={() => {
                            let content = '';
                            const metricDefine = global.getMetricDefine(dashboardType, metricName);
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
                      <div
                        className="expand-icon-box"
                        onClick={() => {
                          const linesName = scopeList.map((item) => item.value);
                          chartDetailRef.current.onOpen(dashboardType, metricName, linesName);
                        }}
                      >
                        <IconFont type="icon-chuangkoufangda" className="expand-icon" />
                      </div>
                      <SingleChart
                        chartKey={metricName}
                        chartTypeProp="line"
                        showHeader={false}
                        wrapStyle={{
                          width: 'auto',
                          height: 222,
                        }}
                        connectEventName={`${dashboardType}BoardDragChart`}
                        eventBus={busInstance}
                        propChartData={metricLines}
                        optionMergeProps={{ replaceMerge: curHeaderOptions.isAutoReload ? ['xAxis'] : ['series'] }}
                        {...getChartConfig(`${metricName}{unit|（${metricUnit}）}`, metricLines.length)}
                      />
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
      {/* 图表详情 */}
      <ChartDetail ref={chartDetailRef} />
    </div>
  );
};

export default DashboardDragChart;
