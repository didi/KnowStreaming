import React, { useState, useEffect, useRef } from 'react';
import { Utils, AppContainer, Checkbox, Input, Row, Col, Button } from 'knowdesign';
import { useParams } from 'react-router-dom';
import api, { MetricType } from '@src/api';
import { OriginMetricData, FormattedMetricData, formatChartData } from '@src/constants/chartConfig';
import ChartOperateBar, { KsHeaderOptions } from '../ChartOperateBar';
import ChartDetail from './Detail';
import { getMetricDashboardReq } from './config';
import './index.less';
import MetricsFilter from '../ChartOperateBar/MetricSelect';
import ChartList from './ChartList';
import { IconFont } from '@knowdesign/icons';

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

interface SelectContentProps {
  title: string;
  list: {
    label: string;
    value: string | number;
  }[];
  isTop?: boolean;
  visibleChange?: (v: boolean) => void;
  onChange?: (list: any[], inputValue: string) => void;
  searchPlaceholder?: string;
}

const SelectContent = (props: SelectContentProps) => {
  const { searchPlaceholder = '输入内容进行搜索', list, isTop, visibleChange, onChange } = props;
  const [scopeSearchValue, setScopeSearchValue] = useState('');
  // 全选属性
  const [indeterminate, setIndeterminate] = useState(false);
  const [checkAll, setCheckAll] = useState(false);
  const [checkedListTemp, setCheckedListTemp] = useState([]);
  const [allCheckedList, setAllCheckedList] = useState([]);
  const defaultChecked = useRef([]);

  const customSure = () => {
    defaultChecked.current = checkedListTemp;
    onChange?.(checkedListTemp, `${checkedListTemp?.length}项`);
  };

  const customCancel = () => {
    setCheckedListTemp(defaultChecked.current);
    visibleChange(false);
  };

  const onCheckAllChange = (e: any) => {
    setCheckedListTemp(e.target.checked ? allCheckedList : []);
  };

  const checkChange = (val: any) => {
    setCheckedListTemp(val);
  };

  useEffect(() => {
    setIndeterminate(!!checkedListTemp.length && checkedListTemp.length < allCheckedList.length);
    setCheckAll(checkedListTemp?.length === allCheckedList.length);
  }, [checkedListTemp]);

  useEffect(() => {
    const all = list?.map((item) => item.value) || [];
    setAllCheckedList(all);
  }, [list]);

  useEffect(() => {
    if (isTop) {
      setCheckedListTemp([]);
      defaultChecked.current = [];
    }
  }, [isTop]);

  return (
    <>
      <h6 className="time_title">{props.title}</h6>
      <div className="custom-scope">
        <div className="check-row">
          <Checkbox className="check-all" indeterminate={indeterminate} checked={checkAll} onChange={onCheckAllChange}>
            全选
          </Checkbox>
          <Input
            className="search-input"
            suffix={<IconFont type="icon-fangdajing" style={{ fontSize: '16px' }} />}
            size="small"
            placeholder={searchPlaceholder}
            onChange={(e) => setScopeSearchValue(e.target.value)}
          />
        </div>
        <div className="fixed-height">
          <Checkbox.Group style={{ width: '100%' }} onChange={checkChange} value={checkedListTemp}>
            <Row gutter={[10, 12]}>
              {list
                .filter((item) => item.label.includes(scopeSearchValue))
                .map((item) => (
                  <Col span={12} key={item.value}>
                    <Checkbox value={item.value}>{item.label}</Checkbox>
                  </Col>
                ))}
            </Row>
          </Checkbox.Group>
        </div>

        <div className="btn-con">
          <Button
            type="primary"
            size="small"
            className="btn-sure"
            onClick={customSure}
            disabled={checkedListTemp?.length > 0 ? false : true}
          >
            确定
          </Button>
          <Button size="small" onClick={customCancel}>
            取消
          </Button>
        </div>
      </div>
    </>
  );
};

const DraggableCharts = (props: PropsType): JSX.Element => {
  const [global] = AppContainer.useGlobalValue();
  const { type: dashboardType } = props;
  const { clusterId } = useParams<{
    clusterId: string;
  }>();
  const [loading, setLoading] = useState<boolean>(true);
  const [scopeList, setScopeList] = useState<IcustomScope[]>([]); // 节点范围列表
  const [curHeaderOptions, setCurHeaderOptions] = useState<ChartFilterOptions>();
  const [metricList, setMetricList] = useState<(string | number)[]>([]);
  const [metricChartData, setMetricChartData] = useState<FormattedMetricData[]>([]); // 指标图表数据列表
  const [gridNum, setGridNum] = useState<number>(12); // 图表列布局
  const curFetchingTimestamp = useRef(0);
  const metricRankList = useRef<string[]>([]);
  const metricFilterRef = useRef(null);
  const chartDetailRef = useRef(null);

  // 获取节点范围列表
  const getScopeList = async () => {
    const res: any = await Utils.request(
      dashboardType !== MetricType.MM2 ? api.getDashboardMetadata(clusterId, dashboardType) : api.getMirrorMakerMetadata(clusterId)
    );
    const mockRes = [{ connectClusterId: 1, connectClusterName: 'connectClusterName', connectorName: 'connectorName' }];
    const list =
      res.length > 0
        ? res.map((item: any) => {
            return dashboardType === MetricType.Broker
              ? {
                  label: item.host,
                  value: item.brokerId,
                }
              : dashboardType === MetricType.MM2
              ? {
                  label: item.connectorName,
                  value: JSON.stringify({ connectClusterId: item.connectClusterId, connectorName: item.connectorName }),
                }
              : {
                  label: item.topicName,
                  value: item.topicName,
                };
          })
        : mockRes.map((item) => {
            return {
              label: item.connectorName,
              value: JSON.stringify(item),
            };
          });

    setScopeList(list);
  };

  // 根据筛选项获取图表信息
  const getMetricChartData = () => {
    !curHeaderOptions.isAutoReload && setLoading(true);

    const [startTime, endTime] = curHeaderOptions.rangeTime;
    const curTimestamp = Date.now();
    curFetchingTimestamp.current = curTimestamp;
    const reqBody = Object.assign(
      {
        startTime,
        endTime,
        metricsNames: metricList || [],
      },
      dashboardType === MetricType.Broker || dashboardType === MetricType.Topic || dashboardType === MetricType.MM2
        ? {
            topNu: curHeaderOptions?.scopeData?.isTop ? curHeaderOptions.scopeData.data : null,
            [dashboardType === MetricType.Broker ? 'brokerIds' : dashboardType === MetricType.MM2 ? 'connectorNameList' : 'topics']:
              curHeaderOptions?.scopeData?.isTop
                ? null
                : dashboardType === MetricType.MM2
                ? curHeaderOptions.scopeData.data?.map((item: any) => {
                    return JSON.parse(item);
                  })
                : curHeaderOptions.scopeData.data,
          }
        : {}
    );

    Utils.post(getMetricDashboardReq(clusterId, dashboardType as any), reqBody).then(
      (res: OriginMetricData[] | null) => {
        // 如果当前请求不是最新请求，则不做任何操作
        if (curFetchingTimestamp.current !== curTimestamp) {
          return;
        }

        if (res === null) {
          // 结果为 null 时，不展示图表
          setMetricChartData([]);
        } else {
          // 格式化图表需要的数据
          const formattedMetricData = formatChartData(
            res,
            global.getMetricDefine || {},
            dashboardType,
            curHeaderOptions.rangeTime
          ) as FormattedMetricData[];
          //  todo 将指标筛选选中但是没有返回的指标插入chartData中
          const nullformattedMetricData: any = [];

          metricList?.forEach((item) => {
            if (formattedMetricData && formattedMetricData.some((key) => item === key.metricName)) {
              nullformattedMetricData.push(null);
            } else {
              const chartData: any = {
                metricName: item,
                metricType: dashboardType,
                metricUnit: global.getMetricDefine(dashboardType, item)?.unit || '',
                metricLines: [],
                showLegend: false,
                targetUnit: undefined,
              };
              nullformattedMetricData.push(chartData);
            }
          });
          // 指标排序
          formattedMetricData.sort((a, b) => metricRankList.current.indexOf(a.metricName) - metricRankList.current.indexOf(b.metricName));
          const filterNullformattedMetricData = nullformattedMetricData.filter((item: any) => item !== null);
          filterNullformattedMetricData.sort(
            (a: any, b: any) => metricRankList.current.indexOf(a?.metricName) - metricRankList.current.indexOf(b?.metricName)
          );
          setMetricChartData([...formattedMetricData, ...filterNullformattedMetricData]);
        }
        setLoading(false);
      },
      () => curFetchingTimestamp.current === curTimestamp && setLoading(false)
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

  // 图表拖拽
  const dragCallback = (oldIndex: number, newIndex: number) => {
    const originFrom = metricRankList.current.indexOf(metricChartData[oldIndex].metricName);
    const originTarget = metricRankList.current.indexOf(metricChartData[newIndex].metricName);
    metricFilterRef.current?.rankChange(originFrom, originTarget);
  };

  // 展开图表详情
  const onExpand = (metricName: string) => {
    const linesName = scopeList.map((item) => item.value);
    chartDetailRef.current.onOpen(dashboardType, metricName, linesName);
  };

  // 获取图表指标
  useEffect(() => {
    if (metricList?.length && curHeaderOptions) {
      getMetricChartData();
    } else {
      setMetricChartData([]);
      setLoading(false);
    }
  }, [curHeaderOptions, metricList]);

  useEffect(() => {
    // 初始化页面，获取 scope 和 metric 信息
    (dashboardType === MetricType.Broker || dashboardType === MetricType.Topic || dashboardType === MetricType.MM2) && getScopeList();
  }, []);

  return (
    <div id="dashboard-drag-chart" className="topic-dashboard">
      <ChartOperateBar
        onChange={ksHeaderChange}
        hideNodeScope={dashboardType === MetricType.Zookeeper}
        openMetricFilter={() => metricFilterRef.current?.open()}
        nodeSelect={{
          name:
            dashboardType === MetricType.Broker
              ? 'Broker'
              : dashboardType === MetricType.Topic
              ? 'Topic'
              : dashboardType === MetricType.MM2
              ? 'MM2'
              : 'Zookeeper',
          customContent: (
            <SelectContent
              title={`自定义 ${
                dashboardType === MetricType.Broker
                  ? 'Broker'
                  : dashboardType === MetricType.Topic
                  ? 'Topic'
                  : dashboardType === MetricType.MM2
                  ? 'MM2'
                  : 'Zookeeper'
              } 范围`}
              list={scopeList}
            />
          ),
        }}
      />
      <MetricsFilter
        ref={metricFilterRef}
        metricType={dashboardType}
        onSelectChange={(list, rankList) => {
          metricRankList.current = rankList;
          setMetricList(list);
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

export default DraggableCharts;
