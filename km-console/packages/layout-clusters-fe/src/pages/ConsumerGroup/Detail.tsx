import React, { useState, useEffect } from 'react';
import { useParams, useHistory } from 'react-router-dom';
import { Button, Space, Divider, Drawer, ProTable, Utils, notification } from 'knowdesign';
import { IconFont } from '@knowdesign/icons';
import API from '@src/api/index';
import { defaultPagination, hashDataParse } from '@src/constants/common';
import { getGtoupTopicColumns } from './config';
import { ExpandedRow } from './ExpandedRow';
import ResetOffsetDrawer from './ResetOffsetDrawer';
import { useForceRefresh } from '@src/components/utils';
const { request } = Utils;

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

const GroupDetail = (props: any) => {
  const { scene } = props;
  const urlParams = useParams<{
    clusterId: string;
  }>();
  const now = Date.now();
  const history = useHistory();
  const [hashData, setHashData] = useState<HashData>({ groupName: '', topicName: '' });
  const [visible, setVisible] = useState(false);

  const [topicData, setTopicData] = useState([]);
  const [loading, setLoading] = useState(true);
  const [pagination, setPagination] = useState<any>(defaultPagination);
  const [expandedData, setExpandedData] = useState([]);
  const [chartData, setChartData] = useState<Array<MetricData>>([]);
  const [loadingObj, setLoadingObj] = useState<any>({});
  const [timeRange, setTimeRange] = useState([now - 24 * 60 * 60 * 1000, now]);
  const [curPartition, setCurPartition] = useState<string>('');
  const [groupMetricsData, setGroupMetricsData] = useState<Array<MetricData>>([]);
  const [openKeys, setOpenKeys] = useState();
  const [resetOffsetVisible, setResetOffsetVisible] = useState(false);
  const [resetOffsetArg, setResetOffsetArg] = useState({});
  const [refreshKey, forceRefresh] = useForceRefresh();

  const genData = async ({ pageNo, pageSize, groupName }: any) => {
    if (urlParams?.clusterId === undefined) return;

    setLoading(true);
    const params = {
      // searchKeywords: '',
      pageNo,
      pageSize,
    };

    request(API.getGroupTopicList(+urlParams?.clusterId, groupName), { params })
      .then((res: any) => {
        setVisible(true);
        setPagination({
          current: res.pagination?.pageNo,
          pageSize: res.pagination?.pageSize,
          total: res.pagination?.total,
        });
        const newTopicListData = res?.bizData.map((item: any) => {
          return {
            ...item,
            key: item.topicName,
          };
        });
        setTopicData(newTopicListData || []);
        setLoading(false);
      })
      .catch((err) => {
        setLoading(false);
      });
  };

  const onClose = () => {
    setVisible(false);
    // clean hash'
    scene === 'topicDetail' && history.goBack();
    scene !== 'topicDetail' && window.history.pushState('', '', location.pathname);
  };

  const resetOffset = (record: any) => {
    setResetOffsetVisible(true);
    setResetOffsetArg({
      topicName: record?.topicName,
      groupName: record?.groupName,
    });
  };
  // 删除消费组Topic
  const deleteOffset = (record: any) => {
    const params = {
      clusterPhyId: +urlParams?.clusterId,
      deleteType: 1, // 0:group纬度，1：Topic纬度，2：Partition纬度
      groupName: record.groupName,
      topicName: record.topicName,
    };
    Utils.delete(API.deleteGroupOffset(), { data: params }).then((data: any) => {
      if (data === null) {
        notification.success({
          message: '删除Topic成功!',
        });
        genData({ pageNo: 1, pageSize: pagination.pageSize, groupName: hashData.groupName });
      }
    });
  };

  const onTableChange = (pagination: any, filters: any, sorter: any) => {
    genData({ pageNo: pagination.current, pageSize: pagination.pageSize, filters, sorter, groupName: hashData.groupName });
  };

  const onClickExpand = (expanded: any, record: any) => {
    const key = record?.key;
    // 之前展开过
    if (expandedData[key]?.length) return;
    // 第一次展开
    setOpenKeys(key);
    // const loading = { ...loadingObj };
    // loading[key] = true;
    // setLoadingObj(loading);
    // expanded && queryExpandedData(record, key);
  };

  useEffect(() => {
    const hashData = hashDataParse(location.hash);
    if (!hashData.groupName) {
      setVisible(false);
    }
    setHashData(hashData);
    // 获取分区列表 为图表模式做准备
    hashData.groupName && genData({ pageNo: 1, pageSize: pagination.pageSize, groupName: hashData.groupName });
    // getConsumersMetadata(hashData).then((res: any) => {
    //   if (res.exist) {
    //     setVisible(false);
    //     history.push(`/cluster/${params?.clusterId}/consumers`);
    //     return;
    //   }
    //   setVisible(true);
    //   getTopicGroupPartitionsHistory(hashData)
    //     .then((data: any) => {
    //       if (data.length > 0) {
    //         setCurPartition(data[0].partition);
    //       }
    //       setPartitionList(data);
    //       return data;
    //     })
    //     .then((data) => {
    //       getTopicGroupMetricHistory(data, hashData);
    //     })
    //     .catch((e) => {
    //       history.push(`/cluster/${params?.clusterId}/consumers`);
    //       setVisible(false);
    //     });
    //   // 获取Consumer列表 表格模式
    //   getTopicGroupMetric(hashData);
    // });
  }, [hashDataParse(location.hash).groupName, refreshKey]);

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
      // extra={
      //   <Space>
      //     {global.hasPermission &&
      //       global.hasPermission(
      //         scene === 'topicDetail' ? ClustersPermissionMap.TOPIC_RESET_OFFSET : ClustersPermissionMap.CONSUMERS_RESET_OFFSET
      //       ) && <ResetOffsetDrawer record={hashData}></ResetOffsetDrawer>}
      //     <Divider type="vertical" />
      //   </Space>
      // }
      extra={
        <Space>
          <span style={{ display: 'inline-block', fontSize: '15px' }} onClick={forceRefresh as () => void}>
            <i className="iconfont icon-shuaxin1" style={{ cursor: 'pointer' }} />
          </span>
          <Divider type="vertical" />
        </Space>
      }
    >
      <ProTable
        showQueryForm={false}
        tableProps={{
          showHeader: false,
          rowKey: 'key',
          loading: loading,
          columns: getGtoupTopicColumns({ resetOffset, deleteOffset }),
          dataSource: topicData,
          paginationProps: { ...pagination },
          // noPagination: true,
          attrs: {
            className: 'consumer-group-detail-drawer-table',
            bordered: false,
            onChange: onTableChange,
            tableLayout: 'auto',
            scroll: { x: 'max-content' },
            expandable: {
              expandedRowRender: (record: any, index: number, indent: any, expanded: boolean) => (
                <ExpandedRow
                  record={record}
                  openKeys={openKeys}
                  expanded={expanded}
                  tableData={expandedData}
                  chartData={chartData}
                  groupName={hashDataParse(location.hash).groupName}
                  loading={loadingObj}
                  refreshKey={refreshKey}
                />
              ),
              // expandedRowRender,
              onExpand: onClickExpand,
              columnWidth: '20px',
              fixed: 'left',
              expandIcon: ({ expanded, onExpand, record }: any) => {
                return expanded ? (
                  <IconFont
                    style={{ fontSize: '16px' }}
                    type="icon-xia"
                    onClick={(e: any) => {
                      onExpand(record, e);
                    }}
                  />
                ) : (
                  <IconFont
                    style={{ fontSize: '16px' }}
                    type="icon-jiantou_1"
                    onClick={(e: any) => {
                      onExpand(record, e);
                    }}
                  />
                );
              },
            },
            style: {
              width: '1032px',
            },
          },
        }}
      />
      <ResetOffsetDrawer
        visible={resetOffsetVisible}
        setVisible={setResetOffsetVisible}
        record={resetOffsetArg}
        resetOffsetFn={forceRefresh}
      ></ResetOffsetDrawer>
    </Drawer>
  );
};

export default GroupDetail;
