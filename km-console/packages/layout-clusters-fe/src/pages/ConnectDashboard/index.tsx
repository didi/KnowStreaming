import React, { useState, useEffect, useRef } from 'react';
import { Utils, AppContainer } from 'knowdesign';
import api, { MetricType } from '@src/api';
import DBreadcrumb from 'knowdesign/es/extend/d-breadcrumb';
import ConnectCard from '@src/components/CardBar/ConnectCard';
import { useParams } from 'react-router-dom';
import { FormattedMetricData, formatChartData, MetricInfo } from '@src/constants/chartConfig';
import ChartOperateBar, { KsHeaderOptions } from '@src/components/ChartOperateBar';
import ChartDetail from '@src/components/DraggableCharts/Detail';
import ChartList from '@src/components/DraggableCharts/ChartList';
import MetricsFilter from './MetricsFilter';
import SelectContent, { Connector } from './SelectContent';
import './index.less';
import { ConnectCluster } from '../Connect/AddConnector';
import HasConnector from '../Connect/HasConnector';

type ChartFilterOptions = Omit<KsHeaderOptions, 'gridNum'>;

const { EventBus } = Utils;
const busInstance = new EventBus();

const DraggableCharts = (): JSX.Element => {
  const [global] = AppContainer.useGlobalValue();
  const { clusterId } = useParams<{
    clusterId: string;
  }>();
  const [loading, setLoading] = useState<boolean>(true);
  // const [scopeList, setScopeList] = useState<IcustomScope[]>([]); // 节点范围列表
  const [curHeaderOptions, setCurHeaderOptions] = useState<ChartFilterOptions>();
  const [metricList, setMetricList] = useState<{ [key: string]: (string | number)[] }>({});
  const [metricChartData, setMetricChartData] = useState<FormattedMetricData[]>([]); // 指标图表数据列表
  const [gridNum, setGridNum] = useState<number>(12); // 图表列布局
  const [scopeList, setScopeList] = useState<{
    connectClusters: ConnectCluster[];
    connectors: Connector[];
  }>({
    connectClusters: [],
    connectors: [],
  });
  const [screenType, setScreenType] = useState('all');
  const curFetchingTimestamp = useRef(0);
  const metricRankList = useRef<string[]>([]);
  const metricFilterRef = useRef(null);
  const chartDetailRef = useRef(null);

  // 根据筛选项获取图表信息
  const getMetricChartData = () => {
    !curHeaderOptions.isAutoReload && setLoading(true);
    const curTimestamp = Date.now();
    curFetchingTimestamp.current = curTimestamp;

    const [startTime, endTime] = curHeaderOptions.rangeTime;
    const { connectClusters, connectors } = curHeaderOptions.scopeData.data;
    const isTop = curHeaderOptions?.scopeData?.isTop;
    const reqBasicBody = {
      startTime,
      endTime,
      topNu: isTop ? curHeaderOptions.scopeData.data : null,
    };

    const getConnectClusterMetrics =
      metricList[MetricType.Connect] && (isTop || connectClusters?.length)
        ? Utils.post(
            api.getConnectClusterMetrics(clusterId),
            Object.assign(
              {
                ...reqBasicBody,
                metricsNames: metricList[MetricType.Connect],
              },
              isTop
                ? {}
                : {
                    connectClusterIdList: connectClusters,
                  }
            )
          )
        : Promise.resolve([]);
    const getConnectorMetrics =
      metricList[MetricType.Connectors] && (isTop || connectors?.length)
        ? Utils.post(
            api.getConnectorMetrics(clusterId),
            Object.assign(
              {
                ...reqBasicBody,
                metricsNames: metricList[MetricType.Connectors],
              },
              isTop ? {} : { connectorNameList: connectors }
            )
          )
        : Promise.resolve([]);

    Promise.all([getConnectClusterMetrics, getConnectorMetrics]).then(
      (res: any) => {
        // 如果当前请求不是最新请求，则不做任何操作
        if (curFetchingTimestamp.current !== curTimestamp) {
          return;
        }

        // 为保证指标排序结果正确，当指标全部返回后才展示图表
        if (res.length === 1 || (res.length === 2 && res[0] && res[1])) {
          const connectClusterData = formatChartData(
            res[0],
            global.getMetricDefine || {},
            MetricType.Connect,
            curHeaderOptions.rangeTime
          ) as FormattedMetricData[];
          // todo 将指标筛选选中但是没有返回的指标插入chartData中
          const newConnectClusterData: any = [];
          metricList[MetricType.Connect]?.forEach((item) => {
            if (connectClusterData && connectClusterData.some((key) => item === key.metricName)) {
              newConnectClusterData.push(null);
            } else {
              const chartData: any = {
                metricName: item,
                metricType: MetricType.Connect,
                metricUnit: global.getMetricDefine(MetricType.Connect, item)?.unit || '',
                metricLines: [],
                showLegend: false,
                targetUnit: undefined,
              };
              newConnectClusterData.push(chartData);
            }
          });
          const connectorData = formatChartData(
            res[1],
            global.getMetricDefine || {},
            MetricType.Connectors,
            curHeaderOptions.rangeTime
          ) as FormattedMetricData[];
          // todo 将指标筛选选中但是没有返回的指标插入chartData中
          const newConnectorData: any = [];

          metricList[MetricType.Connectors]?.forEach((item) => {
            if (connectorData && connectorData.some((key) => item === key.metricName)) {
              newConnectorData.push(null);
            } else {
              const chartData: any = {
                metricName: item,
                metricType: MetricType.Connectors,
                metricUnit: global.getMetricDefine(MetricType.Connectors, item)?.unit || '',
                metricLines: [],
                showLegend: false,
                targetUnit: undefined,
              };
              newConnectorData.push(chartData);
            }
          });
          // 指标排序
          const formattedMetricData = [...connectClusterData, ...connectorData];
          const nullDataMetricData = [...newConnectClusterData, ...newConnectorData].filter((item) => item !== null);
          formattedMetricData.sort((a, b) => metricRankList.current.indexOf(a.metricName) - metricRankList.current.indexOf(b.metricName));
          nullDataMetricData.sort((a, b) => metricRankList.current.indexOf(a.metricName) - metricRankList.current.indexOf(b.metricName));
          const filterMetricData = [...formattedMetricData, ...nullDataMetricData];
          setMetricChartData(
            screenType === 'Connect'
              ? filterMetricData.filter((item) => item.metricType === MetricType.Connect)
              : screenType === 'Connector'
              ? filterMetricData.filter((item) => item.metricType === MetricType.Connectors)
              : filterMetricData
          );
        } else {
          setMetricChartData([]);
        }
        setLoading(false);
      },
      () => curFetchingTimestamp.current === curTimestamp && setLoading(false)
    );
  };

  const getScopeList = () => {
    const getConnectClusters = Utils.request(api.getConnectClusters(clusterId));
    const getConnectors = Utils.request(api.getConnectors(clusterId));
    Promise.all([getConnectClusters, getConnectors]).then(([connectClusters, connectors]: [ConnectCluster[], Connector[]]) => {
      setScopeList({
        connectClusters,
        connectors,
      });
    });
  };

  // 筛选项变化或者点击刷新按钮
  const ksHeaderChange = (ksOptions: KsHeaderOptions) => {
    const { isAutoReload, gridNum: newGridNum, isRelativeRangeTime, rangeTime, scopeData } = ksOptions;
    let newRangeTime = rangeTime;
    // 重新渲染图表
    if (newGridNum !== gridNum) {
      setGridNum(newGridNum || 12);
      busInstance.emit('chartResize');
    } else {
      // 如果为相对时间，则当前时间减去 1 分钟，避免最近一分钟的数据还没采集到时前端多补一个点
      if (isRelativeRangeTime) {
        newRangeTime = rangeTime.map((timestamp) => timestamp - 60 * 1000) as [number, number];
      }
      setCurHeaderOptions({
        isRelativeRangeTime: isRelativeRangeTime,
        isAutoReload: isAutoReload,
        rangeTime: newRangeTime,
        scopeData: scopeData,
      });
    }
  };

  // 图表拖拽
  const dragCallback = (oldIndex: number, newIndex: number) => {
    const originFrom = metricRankList.current.indexOf(metricChartData[oldIndex].metricName);
    const originTarget = metricRankList.current.indexOf(metricChartData[newIndex].metricName);
    metricFilterRef.current?.rankChange(originFrom, originTarget);
  };

  // 展开图表详情
  const onExpand = (metricName: string, metricType: MetricType) => {
    const linesName =
      metricType === MetricType.Connect
        ? scopeList.connectClusters.map((cluster) => cluster.id)
        : scopeList.connectors.map((connector) => ({
            connectClusterId: connector.connectClusterId,
            connectorName: connector.connectorName,
          }));
    chartDetailRef.current.onOpen(metricType, metricName, linesName);
  };

  // 获取图表指标
  useEffect(() => {
    if (Object.values(metricList).some((list) => list.length) && curHeaderOptions) {
      getMetricChartData();
    }
  }, [curHeaderOptions, screenType]);

  useEffect(() => {
    if (Object.values(metricList).some((list) => list.length) && curHeaderOptions) {
      setLoading(true);
      getMetricChartData();
    } else {
      setMetricChartData([]);
      setLoading(false);
    }
  }, [metricList]);

  useEffect(() => {
    getScopeList();
  }, []);

  return (
    <div id="dashboard-drag-chart" className="topic-dashboard">
      <ChartOperateBar
        onChange={ksHeaderChange}
        hideNodeScope={false}
        openMetricFilter={() => metricFilterRef.current?.open()}
        nodeSelect={{
          name: 'Connect',
          customContent: <SelectContent scopeList={scopeList} title="请选择 Connect 范围" />,
        }}
        // setScreenType={setScreenType} // 3.3.1小版本发布
      />
      <MetricsFilter
        ref={metricFilterRef}
        metricType={[MetricType.Connect, MetricType.Connectors]}
        onSelectChange={(list) => {
          const res: { [key: string]: (string | number)[] } = {};
          list.forEach(({ type, name, set }) => {
            set && (res[type] ? res[type].push(name) : (res[type] = [name]));
          });
          setMetricList(res);
        }}
        onRankChange={(rankList) => {
          metricRankList.current = rankList;
        }}
      />
      <ChartList
        busInstance={busInstance}
        loading={loading}
        gridNum={gridNum}
        data={metricChartData}
        autoReload={curHeaderOptions?.isAutoReload}
        dragCallback={dragCallback}
        onExpand={onExpand}
      />
      {/* 图表详情 */}
      <ChartDetail ref={chartDetailRef} />
    </div>
  );
};

const ConnectDashboard = (): JSX.Element => {
  const [global] = AppContainer.useGlobalValue();
  return (
    <>
      <div className="breadcrumb" style={{ marginBottom: '10px' }}>
        <DBreadcrumb
          breadcrumbs={[
            { label: '多集群管理', aHref: '/' },
            { label: global?.clusterInfo?.name, aHref: `/cluster/${global?.clusterInfo?.id}` },
            { label: 'Connect', aHref: `` },
          ]}
        />
      </div>
      <HasConnector>
        <>
          <ConnectCard />
          <DraggableCharts />
        </>
      </HasConnector>
    </>
  );
};

export default ConnectDashboard;
