import { AppContainer, Divider, Form, IconFont, Input, List, message, Modal, Progress, Spin, Tooltip, Utils } from 'knowdesign';
import moment from 'moment';
import React, { useEffect, useMemo, useState, useReducer } from 'react';
import InfiniteScroll from 'react-infinite-scroll-component';
import { Link, useHistory } from 'react-router-dom';
import { timeFormat } from '../../constants/common';
import { IMetricPoint, linesMetric } from './config';
import { useIntl } from 'react-intl';
import api, { MetricType } from '../../api';
import { getHealthClassName, getHealthProcessColor, getHealthText } from '../SingleClusterDetail/config';
import { ClustersPermissionMap } from '../CommonConfig';
import { getUnit, getDataNumberUnit } from '@src/constants/chartConfig';
import SmallChart from '@src/components/SmallChart';

const ListScroll = (props: { loadMoreData: any; list: any; pagination: any; getPhyClusterState: any }) => {
  const history = useHistory();
  const [global] = AppContainer.useGlobalValue();
  const [form] = Form.useForm();
  const [list, setList] = useState<[]>(props.list || []);
  const [loading, setLoading] = useState(false);
  const [visible, setVisible] = useState(false);
  const [clusterInfo, setClusterInfo] = useState({} as any);
  const [pagination, setPagination] = useState(
    props.pagination || {
      pageNo: 1,
      pageSize: 10,
      total: 0,
    }
  );
  const intl = useIntl();

  useEffect(() => {
    setList(props.list || []);
    setPagination(props.pagination || {});
  }, [props.list, props.pagination]);

  useEffect(() => {
    if (visible) {
      form.resetFields();
    }
  }, [visible]);

  const loadMoreData = async () => {
    if (loading) {
      return;
    }
    setLoading(true);

    const res = await props.loadMoreData(pagination.pageNo + 1, pagination.pageSize);
    const _data = list.concat(res.bizData || []) as any;
    setList(_data);
    setPagination(res.pagination);
    setLoading(false);
  };

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
        const [unit, size] = getUnit(line.value);
        line.value = Number((line.value / size).toFixed(2));
        line.unit = line.unit.toLowerCase().replace('byte', unit);
      }

      // Messages 指标值特殊处理
      if (line.metricName === 'LeaderMessages') {
        const [unit, size] = getDataNumberUnit(line.value);
        line.value = Number((line.value / size).toFixed(2));
        line.unit = unit + line.unit;
      }

      metricPoints.push(line);
    });

    const {
      Brokers: brokers,
      Zookeepers: zks,
      HealthCheckPassed: healthCheckPassed,
      HealthCheckTotal: healthCheckTotal,
      HealthScore: healthScore,
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
        <div className={'multi-cluster-list-item'}>
          <div className="multi-cluster-list-item-healthy">
            <Progress
              type="circle"
              status={!itemData.alive ? 'exception' : healthScore >= 90 ? 'success' : 'normal'}
              strokeWidth={4}
              // className={healthScore > 90 ? 'green-circle' : ''}
              className={+itemData.alive <= 0 ? 'red-circle' : +healthScore < 90 ? 'blue-circle' : 'green-circle'}
              strokeColor={getHealthProcessColor(healthScore, itemData.alive)}
              percent={itemData.alive ? healthScore : 100}
              format={() => (
                <div className={`healthy-percent ${getHealthClassName(healthScore, itemData?.alive)}`}>
                  {getHealthText(healthScore, itemData?.alive)}
                </div>
              )}
              width={70}
            />
            <div className="healthy-degree">
              <span className="healthy-degree-status">通过</span>
              <span className="healthy-degree-proportion">
                {healthCheckPassed}/{healthCheckTotal}
              </span>
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
                      ['BytesIn', loadReBalanceEnable && loadReBalanceNwIn],
                      ['BytesOut', loadReBalanceEnable && loadReBalanceNwOut],
                      ['Disk', loadReBalanceEnable && loadReBalanceDisk],
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
                              <Link to={`/cluster/${itemData.id}/cluster/balance`}>前往开启</Link>
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
          {global.hasPermission && global.hasPermission(ClustersPermissionMap.CLUSTER_DEL) ? (
            <div className="multi-cluster-list-item-btn">
              <div className="icon" onClick={(event) => onClickDeleteBtn(event, itemData)}>
                <IconFont type="icon-shanchu1" />
              </div>
            </div>
          ) : (
            <></>
          )}
        </div>
      </List.Item>
    );
  };

  const onFinish = () => {
    form.validateFields().then((formData) => {
      Utils.delete(api.phyCluster, {
        params: {
          clusterPhyId: clusterInfo.id,
        },
      }).then((res) => {
        message.success('删除成功');
        setVisible(false);
        props?.getPhyClusterState();
        const fliterList: any = list.filter((item: any) => {
          return item?.id !== clusterInfo.id;
        });
        setList(fliterList || []);
      });
    });
  };

  const onClickDeleteBtn = (event: any, clusterInfo: any) => {
    event.stopPropagation();
    setClusterInfo(clusterInfo);
    setVisible(true);
  };

  return (
    <>
      {useMemo(
        () => (
          <InfiniteScroll
            dataLength={list.length}
            next={loadMoreData}
            hasMore={list.length < pagination.total}
            loader={<Spin style={{ paddingLeft: '50%', paddingTop: 15 }} spinning={loading} />}
            endMessage={
              !pagination.total ? (
                ''
              ) : (
                <Divider className="load-completed-tip" plain>
                  加载完成 共{pagination.total}条
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
        [list, pagination, loading]
      )}

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
          <Form.Item label="集群名称" name="name" rules={[{ required: false, message: '' }]}>
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
    </>
  );
};

export default ListScroll;
