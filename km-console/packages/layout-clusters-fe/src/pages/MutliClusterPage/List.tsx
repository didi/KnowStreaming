import { AppContainer, Divider, Form, Input, List, Modal, Progress, Spin, Tooltip, Utils } from 'knowdesign';
import message from '@src/components/Message';
import { IconFont } from '@knowdesign/icons';
import moment from 'moment';
import API from '@src/api';
import React, { useEffect, useImperativeHandle, useMemo, useRef, useState } from 'react';
import InfiniteScroll from 'react-infinite-scroll-component';
import { Link, useHistory } from 'react-router-dom';
import { timeFormat, oneDayMillims } from '@src/constants/common';
import { IMetricPoint, linesMetric, pointsMetric } from './config';
import { useIntl } from 'react-intl';
import api, { MetricType } from '@src/api';
import { ClustersPermissionMap } from '../CommonConfig';
import { getDataUnit } from '@src/constants/chartConfig';
import SmallChart from '@src/components/SmallChart';
import HealthState, { HealthStateEnum } from '@src/components/HealthState';
import { SearchParams } from './HomePage';

const DEFAULT_PAGE_SIZE = 10;

export enum ClusterRunState {
  Raft = 2,
}

const DeleteCluster = React.forwardRef((_, ref) => {
  const intl = useIntl();
  const [form] = Form.useForm();
  const [visible, setVisible] = useState<boolean>(false);
  const [clusterInfo, setClusterInfo] = useState<any>({});
  const callback = useRef(() => {
    return;
  });

  const onFinish = () => {
    form.validateFields().then(() => {
      Utils.delete(api.phyCluster, {
        params: {
          clusterPhyId: clusterInfo.id,
        },
      }).then(() => {
        message.success('删除成功');
        callback.current();
        setVisible(false);
      });
    });
  };

  useImperativeHandle(ref, () => ({
    onOpen: (clusterInfo: any, cbk: () => void) => {
      setClusterInfo(clusterInfo);
      callback.current = cbk;
      setVisible(true);
    },
  }));

  useEffect(() => {
    if (visible) {
      form.resetFields();
    }
  }, [visible]);

  return (
    <Modal
      width={570}
      destroyOnClose={true}
      centered={true}
      className="custom-modal"
      wrapClassName="del-topic-modal delete-modal"
      title={intl.formatMessage({
        id: 'delete.cluster.confirm.title',
      })}
      visible={visible}
      onOk={onFinish}
      okText={intl.formatMessage({
        id: 'btn.delete',
      })}
      cancelText={intl.formatMessage({
        id: 'btn.cancel',
      })}
      onCancel={() => setVisible(false)}
      okButtonProps={{
        style: {
          width: 56,
        },
        danger: true,
        size: 'small',
      }}
      cancelButtonProps={{
        style: {
          width: 56,
        },
        size: 'small',
      }}
    >
      <div className="tip-info">
        <IconFont type="icon-warning-circle"></IconFont>
        <span>
          {intl.formatMessage({
            id: 'delete.cluster.confirm.tip',
          })}
        </span>
      </div>
      <Form form={form} className="form" labelCol={{ span: 4 }} wrapperCol={{ span: 16 }} autoComplete="off">
        <Form.Item label="集群名称" name="name">
          <span>{clusterInfo.name}</span>
        </Form.Item>
        <Form.Item
          label="集群名称"
          name="clusterName"
          rules={[
            {
              required: true,
              message: intl.formatMessage({
                id: 'delete.cluster.confirm.cluster',
              }),
              validator: (rule: any, value: string) => {
                value = value || '';
                if (!value.trim() || value.trim() !== clusterInfo.name)
                  return Promise.reject(
                    intl.formatMessage({
                      id: 'delete.cluster.confirm.cluster',
                    })
                  );
                return Promise.resolve();
              },
            },
          ]}
        >
          <Input />
        </Form.Item>
      </Form>
    </Modal>
  );
});

const ClusterList = (props: { searchParams: SearchParams; showAccessCluster: any; getPhyClusterState: any; getExistKafkaVersion: any }) => {
  const { searchParams, showAccessCluster, getPhyClusterState, getExistKafkaVersion } = props;
  const history = useHistory();
  const [global] = AppContainer.useGlobalValue();
  const [isReload, setIsReload] = useState<boolean>(false);
  const [list, setList] = useState<[]>([]);
  const [clusterLoading, setClusterLoading] = useState<boolean>(true);
  const [isLoadingMore, setIsLoadingMore] = useState(false);
  const [pagination, setPagination] = useState({
    pageNo: 1,
    pageSize: DEFAULT_PAGE_SIZE,
    total: 0,
  });
  const deleteModalRef = useRef(null);

  const getClusterList = (pageNo: number, pageSize: number) => {
    const endTime = new Date().getTime();
    const startTime = endTime - oneDayMillims;
    const params = {
      metricLines: {
        endTime,
        metricsNames: linesMetric,
        startTime,
      },
      latestMetricNames: pointsMetric,
      pageNo: pageNo,
      pageSize: pageSize,
      preciseFilterDTOList: [
        {
          fieldName: 'kafkaVersion',
          fieldValueList: searchParams.checkedKafkaVersions as (string | number)[],
        },
        {
          fieldName: 'HealthState',
          fieldValueList: searchParams.healthState,
        },
      ],
      searchKeywords: searchParams.keywords,
      ...searchParams.sortInfo,
    };

    if (searchParams.clusterStatus.length === 1) {
      params.preciseFilterDTOList.push({
        fieldName: 'Alive',
        fieldValueList: searchParams.clusterStatus,
      });
    }
    return Utils.post(API.phyClustersDashbord, params);
  };

  // 重置集群列表
  const reloadClusterList = (pageSize = DEFAULT_PAGE_SIZE) => {
    setClusterLoading(true);
    getClusterList(1, pageSize)
      .then((res: any) => {
        setList(res?.bizData || []);
        setPagination(res.pagination);
      })
      .finally(() => setClusterLoading(false));
  };

  // 加载更多列表
  const loadMoreData = async () => {
    if (isLoadingMore) {
      return;
    }
    setIsLoadingMore(true);

    const res: any = await getClusterList(pagination.pageNo + 1, pagination.pageSize);
    const _data = list.concat(res.bizData || []) as any;
    setList(_data);
    setPagination(res.pagination);
    setIsLoadingMore(false);
  };

  // 重载列表
  useEffect(
    () => (searchParams.isReloadAll ? reloadClusterList(pagination.pageNo * pagination.pageSize) : reloadClusterList()),
    [searchParams]
  );

  const RenderItem = (itemData: any) => {
    itemData = itemData || {};
    const metrics = linesMetric;
    const metricPoints = [] as IMetricPoint[];
    metrics.forEach((item) => {
      const line = {
        metricName: item,
        value: itemData.latestMetrics?.metrics?.[item] || 0,
        unit: (global.getMetricDefine && global.getMetricDefine(MetricType.Cluster, item).unit) || '',
        metricLines: {
          name: item,
          data: itemData.metricLines
            .find((metric: any) => metric.metricName === item)
            ?.metricPoints.map((point: IMetricPoint) => [point.timeStamp, point.value]),
        },
      } as IMetricPoint;

      // 如果单位是 字节 ，进行单位换算
      if (line.unit.toLowerCase().includes('byte')) {
        const [unit, size] = getDataUnit['Memory'](line.value);
        line.value = Number((line.value / size).toFixed(2));
        line.unit = line.unit.toLowerCase().replace('byte', unit);
      }

      // Messages 指标值特殊处理
      if (line.metricName === 'LeaderMessages') {
        const [unit, size] = getDataUnit['Num'](line.value);
        line.value = Number((line.value / size).toFixed(2));
        line.unit = unit + line.unit;
      }

      metricPoints.push(line);
    });

    const runState = itemData.runState;
    const {
      Brokers: brokers,
      Zookeepers: zks,
      HealthCheckPassed: healthCheckPassed,
      HealthCheckTotal: healthCheckTotal,
      HealthState: healthState,
      ZookeepersAvailable: zookeepersAvailable,
      LoadReBalanceCpu: loadReBalanceCpu,
      LoadReBalanceDisk: loadReBalanceDisk,
      LoadReBalanceEnable: loadReBalanceEnable,
      LoadReBalanceNwIn: loadReBalanceNwIn,
      LoadReBalanceNwOut: loadReBalanceNwOut,
    } = itemData.latestMetrics?.metrics || {};

    return (
      <List.Item
        onClick={() => {
          history.push(`/cluster/${itemData.id}/cluster`);
        }}
      >
        <div className="multi-cluster-list-item">
          <div className="multi-cluster-list-item-healthy">
            <div className="healthy-box">
              <HealthState state={healthState} width={70} height={70} />
              <div className="healthy-degree">
                <span className="healthy-degree-status">通过</span>
                <span className="healthy-degree-proportion">
                  {healthCheckPassed}/{healthCheckTotal}
                </span>
              </div>
            </div>
          </div>
          <div className="multi-cluster-list-item-right">
            <div className="multi-cluster-list-item-base">
              <div className="multi-cluster-list-item-base-left">
                <div className="base-name">{itemData.name ?? '-'}</div>
                <span className="base-version">{itemData.kafkaVersion ?? '-'}</span>
                {loadReBalanceEnable !== undefined && (
                  <div
                    style={{
                      display: 'flex',
                    }}
                    onClick={(e) => {
                      e.preventDefault();
                      e.stopPropagation();
                    }}
                  >
                    {[
                      ['BytesIn', loadReBalanceNwIn === 1],
                      ['BytesOut', loadReBalanceNwOut === 1],
                      ['Disk', loadReBalanceDisk === 1],
                    ].map(([name, isBalanced]) => {
                      return isBalanced ? (
                        <div className="balance-box balanced">{name} 已均衡</div>
                      ) : loadReBalanceEnable ? (
                        <div className="balance-box unbalanced">{name} 未均衡</div>
                      ) : (
                        <Tooltip
                          title={
                            <span>
                              尚未开启 {name} 均衡策略，
                              <Link to={`/cluster/${itemData.id}/operation/balance`}>前往开启</Link>
                            </span>
                          }
                        >
                          <div className="balance-box unbalanced">{name} 未均衡</div>
                        </Tooltip>
                      );
                    })}
                  </div>
                )}
              </div>
              <div className="multi-cluster-list-item-base-date">{moment(itemData.createTime).format(timeFormat)}</div>
            </div>
            <div className="multi-cluster-list-item-Indicator">
              <div className="indicator-left">
                <div className="indicator-left-item">
                  <div className="indicator-left-item-title">
                    <span
                      className="indicator-left-item-title-dot"
                      style={{
                        background: itemData.latestMetrics?.metrics?.BrokersNotAlive ? '#FF7066' : '#34C38F',
                      }}
                    ></span>
                    Brokers
                  </div>
                  <div className="indicator-left-item-value">{brokers}</div>
                </div>
                {/* 2: raft 模式 无zk */}
                {runState !== ClusterRunState.Raft && (
                  <div className="indicator-left-item">
                    <div className="indicator-left-item-title">
                      <span
                        className="indicator-left-item-title-dot"
                        style={{
                          background: zookeepersAvailable === -1 ? '#e9e7e7' : zookeepersAvailable === 0 ? '#FF7066' : '#34C38F',
                        }}
                      ></span>
                      ZK
                    </div>
                    <div className="indicator-left-item-value">{zookeepersAvailable === -1 ? '-' : zks}</div>
                  </div>
                )}
              </div>
              <div className="indicator-right">
                {metricPoints.map((row, index) => {
                  return (
                    <div
                      key={row.metricName + index}
                      className={`indicator-right-item ${row.metricName === 'LeaderMessages' ? 'first-right-item' : ''}`}
                    >
                      <div className="indicator-right-item-total">
                        <div className="indicator-right-item-total-name">
                          {row.metricName === 'TotalLogSize' ? 'MessageSize' : row.metricName}
                        </div>
                        <div className="indicator-right-item-total-value">
                          {row.value}
                          <span className="total-value-unit">{row.unit}</span>
                        </div>
                      </div>
                      <div className="indicator-right-item-chart">
                        <SmallChart width={79} height={40} chartData={row.metricLines} />
                      </div>
                    </div>
                  );
                })}
              </div>
            </div>
          </div>
          {global.hasPermission ? (
            <div className="multi-cluster-list-item-btn">
              {global.hasPermission(ClustersPermissionMap.CLUSTER_CHANGE_INFO) && (
                <div
                  className="icon"
                  onClick={(e) => {
                    e.stopPropagation();
                    showAccessCluster(itemData);
                  }}
                >
                  <IconFont type="icon-duojiqunbianji" />
                </div>
              )}
              {global.hasPermission(ClustersPermissionMap.CLUSTER_DEL) && (
                <div
                  className="icon"
                  onClick={(e) => {
                    e.stopPropagation();
                    deleteModalRef.current.onOpen(itemData, () => {
                      getPhyClusterState();
                      getExistKafkaVersion(true);
                      reloadClusterList(pagination.pageNo * pagination.pageSize);
                    });
                  }}
                >
                  <IconFont type="icon-duojiqunshanchu" />
                </div>
              )}
            </div>
          ) : (
            <></>
          )}
        </div>
      </List.Item>
    );
  };

  return (
    <Spin spinning={clusterLoading}>
      {useMemo(
        () => (
          <InfiniteScroll
            dataLength={list.length}
            next={loadMoreData}
            hasMore={list.length < pagination.total}
            loader={<Spin style={{ paddingLeft: '50%', paddingTop: 15 }} spinning={true} />}
            endMessage={
              !pagination.total ? (
                ''
              ) : (
                <Divider className="load-completed-tip" plain>
                  加载完成 共 {pagination.total} 条
                </Divider>
              )
            }
            scrollableTarget="scrollableDiv"
          >
            <List
              bordered={false}
              split={false}
              className="multi-cluster-list"
              itemLayout="horizontal"
              dataSource={list}
              renderItem={RenderItem}
            />
          </InfiniteScroll>
        ),
        [list, pagination, isLoadingMore]
      )}

      <DeleteCluster ref={deleteModalRef} />
    </Spin>
  );
};
export default ClusterList;
