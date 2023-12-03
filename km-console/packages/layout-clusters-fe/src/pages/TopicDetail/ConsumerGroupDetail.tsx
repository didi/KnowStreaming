import React, { useState, useEffect } from 'react';
import { useParams, useHistory } from 'react-router-dom';
import { AppContainer, Divider, Drawer, ProTable, Select, SingleChart, Space, Tooltip, Utils } from 'knowdesign';
import { IconFont } from '@knowdesign/icons';
import { DRangeTime } from 'knowdesign';
import { CHART_COLOR_LIST, getBasicChartConfig } from '@src/constants/chartConfig';
import Api from '@src/api/index';
import { hashDataParse } from '@src/constants/common';
import { ClustersPermissionMap } from '../CommonConfig';
import ResetOffsetDrawer from './ResetOffsetDrawer';
import SwitchTab from '@src/components/SwitchTab';
import ContentWithCopy from '@src/components/CopyContent';
import PubSub from "pubsub-js";

const { Option } = Select;

export interface MetricLine {
  createTime?: number;
  metricPoints: Array<{
    aggType: string;
    createTime: number;
    timeStamp: number;
    unit: string;
    updateTime: number;
    value: number;
  }>;
  name: string;
  updateTime?: number;
}
export interface MetricData {
  metricLines?: Array<MetricLine>;
  metricLine?: MetricLine;
  metricName: string;
}

export interface HashData {
  groupName: string;
  topicName: string;
}

const metricConsts = ['LogEndOffset', 'OffsetConsumed', 'Lag'];
const metricWithType = [
  { metricName: 'LogEndOffset', metricType: 104 },
  { metricName: 'OffsetConsumed', metricType: 102 },
  { metricName: 'Lag', metricType: 102 },
];

export default (props: any) => {
  const { scene, visible, setVisible, hashData } = props;
  const params = useParams<{
    clusterId: string;
  }>();
  const history = useHistory();
  const [global] = AppContainer.useGlobalValue();
  // const { record } = props;
  const now = Date.now();
  const [allGroupMetricsData, setAllGroupMetricsData] = useState<Array<MetricData>>([]);
  const [groupMetricsData, setGroupMetricsData] = useState<Array<MetricData>>([]);
  const [timeRange, setTimeRange] = useState([now - 24 * 60 * 60 * 1000, now]);
  const [consumerList, setConsumerList] = useState([]);
  const [partitionList, setPartitionList] = useState([]);
  const [curPartition, setCurPartition] = useState<string>('');
  const [showMode, setShowMode] = useState('table');
  const [pageIndex, setPageIndex] = useState(1);
  const [pageTotal, setPageTotal] = useState(1);
  const [pageSize, setPageSize] = useState(10);
  const [consumerListLoading, setConsumerListLoading] = useState(true);
  const [consumerChartLoading, setConsumerChartLoading] = useState(false);
  // const [hashData, setHashData] = useState<HashData>({ groupName: '', topicName: '' });
  // const [visible, setVisible] = useState(false);
  const [sortObj, setSortObj] = useState<{
    sortField: string;
    sortType: 'desc' | 'asc' | '';
  }>({ sortField: '', sortType: '' });
  const [pagination, setPagination] = useState<any>({
    current: 1,
    pageSize: 10,
    position: 'bottomRight',
    showSizeChanger: true,
    pageSizeOptions: ['10', '20', '50', '100', '200', '500'],
    showTotal: (total: number) => `共 ${total} 条目`,
  });
  const clusterId = Number(params.clusterId);
  const columns = [
    {
      title: 'Topic Partition',
      dataIndex: 'partitionId',
      key: 'partitionId',
      lineClampOne: true,
      needTooltip: true,
      width: 180,
      render: (v: string, record: any) => {
        return `${record.topicName}-${v}`;
      },
    },
    {
      title: 'Member ID',
      dataIndex: 'memberId',
      key: 'memberId',
      width: 200,
      render: (v: string) => {
        return v ? <ContentWithCopy content={v} /> : '-';
      },
    },
    {
      title: 'Current Offset',
      dataIndex: 'OffsetConsumed',
      key: 'OffsetConsumed',
      render: (v: any, record: any) => {
        return record?.latestMetrics?.metrics?.OffsetConsumed.toLocaleString();
      },
      sorter: true,
      // sorter: {
      //   compare: (a: any, b: any) => {
      //     let value1 = a?.metrics?.find((item: any) => item.metricName === 'OffsetConsumed' && item.metricType === 102)?.metricValue
      //     let value2 = b?.metrics?.find((item: any) => item.metricName === 'OffsetConsumed' && item.metricType === 102)?.metricValue
      //     return value1 - value2
      //   },
      //   multiple: 1
      // }
    },
    {
      title: 'Log End Offset',
      dataIndex: 'LogEndOffset',
      key: 'LogEndOffset',
      render: (v: any, record: any) => {
        return record?.latestMetrics?.metrics?.LogEndOffset.toLocaleString();
      },
      sorter: true,
      // sorter: {
      //   compare: (a: any, b: any) => {
      //     let value1 = a?.metrics?.find((item: any) => item.metricName === 'LogEndOffset' && item.metricType === 104)?.metricValue
      //     let value2 = b?.metrics?.find((item: any) => item.metricName === 'LogEndOffset' && item.metricType === 104)?.metricValue
      //     return value1 - value2
      //   },
      //   multiple: 2
      // }
    },
    {
      title: 'Lag',
      dataIndex: 'Lag',
      key: 'Lag',
      render: (v: any, record: any) => {
        return record?.latestMetrics?.metrics?.Lag.toLocaleString();
      },
      sorter: true,
      // sorter: {
      //   compare: (a: any, b: any) => {
      //     let value1 = a?.metrics?.find((item: any) => item.metricName === 'Lag' && item.metricType === 102)?.metricValue
      //     let value2 = b?.metrics?.find((item: any) => item.metricName === 'Lag' && item.metricType === 102)?.metricValue
      //     return value1 - value2
      //   },
      //   multiple: 3
      // }
    },
    {
      title: 'Host',
      dataIndex: 'host',
      key: 'host',
    },
    {
      title: 'Client ID',
      dataIndex: 'clientId',
      key: 'clientId',
      needTooltip: true,
      lineClampOne: true,
      width: 200,
    },
  ];
  const getTopicGroupMetric = ({
    hashData,
    pagination = { current: 1, pageSize: 10 },
    sorter = {},
  }: {
    hashData: HashData;
    pagination?: any;
    sorter?: any;
  }) => {
    setConsumerListLoading(true);
    const params: any = {
      // metricRealTimes: metricWithType,
      latestMetricNames: metricConsts,
      pageNo: pagination.current,
      pageSize: pagination.pageSize,
      sortField: sorter.field || undefined,
      sortType: sorter.order ? sorter.order.substring(0, sorter.order.indexOf('end')) : undefined,
    };
    // if (sorter.sortField && sorter.sortType) {
    //   params.sortField = sorter.sortField;
    //   params.sortType = sorter.sortType;
    // }
    return Utils.post(
      Api.getTopicGroupMetric({
        clusterId,
        groupName: hashData.groupName,
        topicName: hashData.topicName,
      }),
      params
    )
      .then((data: any) => {
        if (!data) return;

        setPagination({
          current: data.pagination?.pageNo,
          pageSize: data.pagination?.pageSize,
          total: data.pagination?.total,
        });
        setConsumerList(data?.bizData);
      })
      .finally(() => {
        setConsumerListLoading(false);
      });
  };
  const getTopicGroupPartitionsHistory = (hashData: HashData) => {
    return Utils.request(Api.getTopicGroupPartitionsHistory(clusterId, hashData.groupName), {
      params: {
        startTime: timeRange[0],
        endTime: timeRange[1],
      },
    });
  };
  const getTopicGroupMetricHistory = (partitions: Array<any>, hashData: HashData) => {
    const params = {
      aggType: 'sum',
      groupTopics: partitions?.map((p) => ({
        partition: p.value,
        topic: hashData.topicName,
      })),
      group: hashData.groupName,
      metricsNames: metricWithType.map((item) => item.metricName),
      startTime: timeRange[0],
      endTime: timeRange[1],
      topNu: 0,
    };
    Utils.post(Api.getTopicGroupMetricHistory(clusterId), params).then((data: Array<MetricData>) => {
      setAllGroupMetricsData(data);
    });
  };
  const getConsumersMetadata = (hashData: HashData) => {
    return Utils.request(Api.getConsumersMetadata(clusterId, hashData.groupName, hashData.topicName));
  };

  const getTopicsMetaData = (hashData: HashData) => {
    return Utils.request(Api.getTopicsMetaData(hashData.topicName, clusterId));
  };

  const onClose = () => {
    setVisible(false);
    setSortObj({
      sortField: '',
      sortType: '',
    });
    // clean hash'
    // scene === 'topicDetail' && history.goBack();
    // scene !== 'topicDetail' && window.history.pushState('', '', location.pathname);
  };

  const onTableChange = (pagination: any, filters: any, sorter: any) => {
    getTopicGroupMetric({ hashData, pagination, sorter });
    // setPageIndex(pagination.current);
  };

  useEffect(() => {
    if (curPartition === '' || allGroupMetricsData.length === 0) return;
    const filteredData = allGroupMetricsData.map((item) => {
      const allData = item.metricLines.reduce(
        (acc, cur) => {
          if (acc.metricLine.metricPoints.length === 0) {
            acc.metricLine.metricPoints = cur.metricPoints.map((p) => ({
              timeStamp: p.timeStamp,
              value: Number(p.value),
            }));
          } else {
            acc.metricLine.metricPoints.forEach((mp) => {
              const curMetricPoint = cur.metricPoints.find((curmp) => curmp.timeStamp === mp.timeStamp);
              mp.value += curMetricPoint ? Number(curMetricPoint.value) : 0;
            });
          }
          return acc;
        },
        {
          metricName: item.metricName,
          metricLine: {
            name: 'all',
            metricPoints: [],
          },
        }
      );
      return curPartition === '__all__'
        ? allData
        : {
            metricName: item.metricName,
            metricLine: item.metricLines.find((line) => line.name.indexOf(curPartition) >= 0),
          };
    });
    setGroupMetricsData(filteredData);
  }, [curPartition, allGroupMetricsData]);

  useEffect(() => {
    // const hashData = hashDataParse(location.hash);
    if (!hashData.groupName || !hashData.topicName) return;
    // setHashData(hashData);
    // 获取分区列表 为图表模式做准备
    visible &&
      getConsumersMetadata(hashData).then((res: any) => {
        if (!res.exist) {
          setVisible(false);
          // history.push(`/cluster/${params?.clusterId}/consumers`);
          return;
        }
        getTopicsMetaData(hashData)
          // .then((data: any) => {
          //   if (data.length > 0) {
          //     setCurPartition(data[0].partition);
          //   }
          //   setPartitionList(data);
          //   return data;
          // })
          .then((data: any) => {
            const partitionLists = (data?.partitionIdList || []).map((item: any) => {
              return {
                label: item,
                value: item,
              };
            });
            setCurPartition(partitionLists?.[0]?.value);
            setPartitionList(partitionLists);
            getTopicGroupMetricHistory(partitionLists, hashData);
          })
          .catch((e) => {
            // history.push(`/cluster/${params?.clusterId}/consumers`);
            setVisible(false);
          });
        // 获取Consumer列表 表格模式
        getTopicGroupMetric({ hashData: hashData as HashData });
      });
  }, [visible]);

// 订阅重置offset成功的消息
  PubSub.subscribe('TopicDetail-ResetOffset', function(message, data){
    getTopicGroupMetric({hashData: data});
  })

  useEffect(() => {
    if (partitionList.length === 0) return;
    getTopicGroupMetricHistory(partitionList, hashData);
  }, [timeRange]);

  return (
    <Drawer
      push={false}
      title="Consumer Group详情"
      width={1080}
      placement="right"
      onClose={onClose}
      visible={visible}
      className="consumer-group-detail-drawer"
      maskClosable={false}
      destroyOnClose
      extra={
        <Space>
          {global.hasPermission &&
            global.hasPermission(
              scene === 'topicDetail' ? ClustersPermissionMap.TOPIC_RESET_OFFSET : ClustersPermissionMap.CONSUMERS_RESET_OFFSET
            ) && <ResetOffsetDrawer record={hashData}></ResetOffsetDrawer>}
          <Divider type="vertical" />
        </Space>
      }
    >
      <div className="consumer-group-detail">
        <div className="title-and-mode">
          <div className="title-and-mode-header"></div>
          <div className="right">
            {showMode === 'chart' && (
              <Select
                style={{ width: 140, marginRight: 8 }}
                size="small"
                value={curPartition}
                onChange={(id) => {
                  setCurPartition(id);
                }}
              >
                <Option value={'__all__'}>全部Partition</Option>
                {partitionList.map((partition) => (
                  <Option key={partition.value} value={partition.value}>
                    {partition.value}
                  </Option>
                ))}
              </Select>
            )}
            {showMode === 'chart' && (
              <DRangeTime
                rangeTimeArr={timeRange}
                timeChange={(o: any) => {
                  setTimeRange(o);
                }}
              ></DRangeTime>
            )}
            {showMode === 'chart' && <div className="divider"></div>}
            <SwitchTab defaultKey={showMode} onChange={(key) => setShowMode(key)}>
              <SwitchTab.TabItem key="chart">
                <div style={{ width: 34, height: 23 }}>
                  <IconFont type="icon-tubiao"></IconFont>
                </div>
              </SwitchTab.TabItem>
              <SwitchTab.TabItem key="table">
                <div style={{ width: 34, height: 23 }}>
                  <IconFont type="icon-biaoge"></IconFont>
                </div>
              </SwitchTab.TabItem>
            </SwitchTab>
          </div>
        </div>
        {showMode === 'table' && (
          // <Table
          //   rowKey={'partitionId'}
          //   columns={columns}
          //   className="table"
          //   loading={consumerListLoading}
          //   dataSource={consumerList}
          //   pagination={{
          //     current: pageIndex,
          //     pageSize: pageSize,
          //     total: pageTotal,
          //     simple: true
          //   }}
          //   onChange={(pagination: any, filters: any, sorter: any) => {
          //     setSortObj({
          //       sortField: sorter.field || '',
          //       sortType: sorter.order ? sorter.order.substring(0, sorter.order.indexOf('end')) : '',
          //     });
          //     setPageIndex(pagination.current);
          //   }}
          // ></Table>
          <ProTable
            showQueryForm={false}
            tableProps={{
              loading: consumerListLoading,
              showHeader: false,
              rowKey: 'partitionId',
              columns: columns,
              dataSource: consumerList,
              paginationProps: { ...pagination },
              attrs: {
                sortDirections: ['descend', 'ascend', 'default'],
                scroll: { x: 1032 },
                // className: 'frameless-table', // 纯无边框表格类名
                onChange: onTableChange,
                bordered: false,
              },
            }}
          />
        )}
        {showMode === 'chart' && (
          <div className="single-chart">
            <SingleChart
              showHeader={false}
              wrapStyle={{
                width: '100%',
                height: 242,
              }}
              option={getBasicChartConfig({
                xAxis: {
                  type: 'time',
                },
                title: {
                  show: false,
                },
                legend: {
                  left: 'center',
                },
                color: CHART_COLOR_LIST,
                grid: {
                  left: 0,
                  right: 0,
                  top: 10,
                },
                tooltip: {
                  customWidth: 200,
                },
              })}
              chartTypeProp="line"
              propChartData={groupMetricsData}
              seriesCallback={(data: any) => {
                return data.map((metricData: any) => {
                  const partitionMetricData = metricData.metricLine?.metricPoints || [];
                  return {
                    name: metricData.metricName,
                    data: partitionMetricData.map((item: any) => [item.timeStamp, item.value, item.unit]),
                    lineStyle: {
                      width: 1.5,
                    },
                    smooth: 0.25,
                    symbol: 'emptyCircle',
                    symbolSize: 4,
                    emphasis: {
                      disabled: true,
                    },
                  };
                });
              }}
            />
          </div>
        )}
      </div>
    </Drawer>
  );
};
