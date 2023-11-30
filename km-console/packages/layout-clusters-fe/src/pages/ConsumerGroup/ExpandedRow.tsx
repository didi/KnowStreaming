import React, { useEffect, useState } from 'react';
import { useParams, useHistory } from 'react-router-dom';
import { ProTable, Utils, DRangeTime, Select, SingleChart } from 'knowdesign';
import SwitchTab from '@src/components/SwitchTab';
import { CHART_COLOR_LIST, getBasicChartConfig } from '@src/constants/chartConfig';
import ContentWithCopy from '@src/components/CopyContent';
import { IconFont } from '@knowdesign/icons';
import API from '@src/api/index';
import { hashDataParse } from '@src/constants/common';
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

export const ExpandedRow: any = ({ record, groupName, refreshKey }: any) => {
  const params: any = useParams<{
    clusterId: string;
  }>();
  const history = useHistory();
  const now = Date.now();
  const [allGroupMetricsData, setAllGroupMetricsData] = useState<Array<MetricData>>([]);
  const [showMode, setShowMode] = useState('table');
  const [curPartition, setCurPartition] = useState<string>('');
  const [partitionList, setPartitionList] = useState([]);
  const [timeRange, setTimeRange] = useState([now - 24 * 60 * 60 * 1000, now]);
  const [consumerListLoading, setConsumerListLoading] = useState(true);
  const [consumerList, setConsumerList] = useState([]);
  const [groupMetricsData, setGroupMetricsData] = useState<Array<MetricData>>([]);
  const clusterId = Number(params.clusterId);
  const [pagination, setPagination] = useState<any>({
    current: 1,
    pageSize: 5,
    simple: true,
    hideOnSinglePage: false,
  });
  const columns = [
    {
      title: 'Partition',
      dataIndex: 'partitionId',
      key: 'partitionId',
      lineClampOne: true,
      needTooltip: true,
      width: 180,
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

  const getTopicGroupMetric = ({ pagination = { current: 1, pageSize: 10 }, sorter = {} }: { pagination?: any; sorter?: any }) => {
    setConsumerListLoading(true);
    const params: any = {
      // metricRealTimes: metricWithType,
      latestMetricNames: metricConsts,
      pageNo: pagination.current,
      pageSize: pagination.pageSize,
      sortField: sorter.field || undefined,
      sortType: sorter.order ? sorter.order.substring(0, sorter.order.indexOf('end')) : undefined,
    };

    return Utils.post(
      API.getTopicGroupMetric({
        clusterId,
        groupName: groupName,
        topicName: record.topicName,
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
        setConsumerList(data?.bizData || []);
      })
      .finally(() => {
        setConsumerListLoading(false);
      });
  };

  const getTopicGroupMetricHistory = (partitions: Array<any>) => {
    const params = {
      aggType: 'sum',
      groupTopics: partitions.map((p: any) => ({
        partition: p.value,
        topic: record.topicName,
      })),
      group: groupName,
      metricsNames: metricWithType.map((item) => item.metricName),
      startTime: timeRange[0],
      endTime: timeRange[1],
      topNu: 0,
    };
    Utils.post(API.getTopicGroupMetricHistory(clusterId), params, { timeout: 300000 }).then((data: Array<MetricData>) => {
      // ! 替换接口返回
      setAllGroupMetricsData(data);
    });
  };

  const getConsumersMetadata = (hashData: HashData) => {
    return Utils.request(API.getConsumersMetadata(params.clusterId, groupName, record.topicName));
  };

  const getTopicsMetaData = () => {
    return Utils.request(API.getTopicsMetaData(record?.topicName, +params.clusterId));
  };
  const onTableChange = (pagination: any, filters: any, sorter: any) => {
    getTopicGroupMetric({ pagination, sorter });
  };

  useEffect(() => {
    const hashData = hashDataParse(location.hash);
    // if (!hashData.groupName) return;
    // 获取分区列表 为图表模式做准备

    getConsumersMetadata(hashData).then((res: any) => {
      if (!res.exist) {
        history.push(`/cluster/${params?.clusterId}/consumers`);
        return;
      }
      getTopicsMetaData()
        .then((data: any) => {
          const partitionLists = (data?.partitionIdList || []).map((item: any) => {
            return {
              label: item,
              value: item,
            };
          });
          setCurPartition(partitionLists?.[0]?.value);
          setPartitionList(partitionLists);
          getTopicGroupMetricHistory(partitionLists);
        })
        .catch((e) => {
          history.push(`/cluster/${params?.clusterId}/consumers`);
        });
      // 获取Consumer列表 表格模式
      getTopicGroupMetric({});
    });
  }, [hashDataParse(location.hash).groupName, refreshKey]);

  useEffect(() => {
    if (partitionList.length === 0) return;
    getTopicGroupMetricHistory(partitionList);
  }, [timeRange]);

  useEffect(() => {
    if (curPartition === '' || allGroupMetricsData.length === 0) return;
    const filteredData = allGroupMetricsData.map((item) => {
      const allData = item.metricLines.reduce(
        (acc, cur) => {
          if (acc.metricLine.metricPoints.length === 0) {
            acc.metricLine.metricPoints = cur.metricPoints.map((p: any) => ({
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

  return (
    <div
      key={record.key}
      style={{ position: 'relative', padding: '12px 16px', border: '1px solid #EFF2F7', borderRadius: '8px', backgroundColor: '#ffffff' }}
    >
      <div className="consumer-group-detail">
        <div className="title-and-mode" style={{ height: '30px', paddingBottom: '12px' }}>
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
          <ProTable
            showQueryForm={false}
            tableProps={{
              isCustomPg: false,
              loading: consumerListLoading,
              showHeader: false,
              rowKey: 'partitionId',
              columns: columns,
              dataSource: consumerList || [],
              paginationProps: { ...pagination },
              attrs: {
                sortDirections: ['descend', 'ascend', 'default'],
                scroll: { x: 1000 },
                size: 'small',
                bordered: false,
                className: 'expanded-table',
                rowClassName: 'table-small-bgcolor',
                // className: 'frameless-table', // 纯无边框表格类名
                onChange: onTableChange,
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
              connectEventName=""
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
    </div>
  );
};
