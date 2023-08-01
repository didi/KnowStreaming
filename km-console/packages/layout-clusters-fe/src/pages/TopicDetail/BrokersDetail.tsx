import React, { useCallback } from 'react';
import { useEffect, useState } from 'react';
import { AppContainer, Button, Empty, List, Popover, ProTable, Radio, Spin, Utils } from 'knowdesign';
import { IconFont } from '@knowdesign/icons';
import { CloseOutlined } from '@ant-design/icons';
import api, { MetricType } from '@src/api';
import { useParams } from 'react-router-dom';
import TagsWithHide from '@src/components/TagsWithHide';
import SwitchTab from '@src/components/SwitchTab';
import RenderEmpty from '@src/components/RenderEmpty';
import { useForceRefresh } from '@src/components/utils';

interface PropsType {
  hashData: any;
  // searchKeywords: string;
}

interface PartitionsSummary {
  brokerCount: number;
  deadBrokerCount: number;
  liveBrokerCount: number;
  noLeaderPartitionCount: number;
  partitionCount: number;
  underReplicatedPartitionCount: number;
}

const PARTITION_DETAIL_METRICS = ['LogEndOffset', 'LogStartOffset', 'Messages', 'LogSize'] as const;

interface PartitionDetail {
  partitionId: number;
  topicName: string;
  leaderBrokerId: number;
  assignReplicas: number[];
  inSyncReplicas: number[];
  latestMetrics: {
    clusterPhyId: number;
    metrics: {
      [K in typeof PARTITION_DETAIL_METRICS[number]]: number;
    };
    timestamp: number;
  };
}

interface BrokersDetail {
  live: number;
  dead: number;
  total: number;
  partitionCount: number;
  noLeaderPartitionIdList: number[];
  underReplicatedPartitionIdList: number[];
  brokerPartitionStateList: brokerPartitionState[];
}

interface brokerPartitionState {
  alive: boolean;
  brokerId: number;
  bytesInOneMinuteRate: number;
  bytesOutOneMinuteRate: number;
  host: string;
  replicaList: brokerReplica[];
}

interface brokerReplica {
  leaderBrokerId: number;
  partitionId: number;
  topicName: string;
  isLeaderReplace: boolean;
  inSync: boolean;
}

function getTranformedBytes(bytes: number) {
  const unit = ['B', 'KB', 'MB', 'GB', 'TB'];
  let i = 0;
  let isUp = true,
    outBytes = bytes;
  if (typeof outBytes !== 'number') {
    outBytes = Number(outBytes);
    if (isNaN(outBytes)) return [outBytes, unit[i]];
  }

  while (isUp) {
    if (outBytes / 1024 >= 1) {
      outBytes /= 1024;
      i++;
    } else {
      isUp = false;
    }
  }
  return [outBytes.toFixed(2), unit[i]];
}

const PartitionPopoverContent = (props: {
  clusterId: string;
  hashData: any;
  record: brokerReplica;
  brokerId: brokerPartitionState['brokerId'];
  close: () => void;
}) => {
  const [global] = AppContainer.useGlobalValue();
  const { clusterId, hashData, record, brokerId, close } = props;
  const [loading, setLoading] = useState<boolean>(true);
  const [partitionInfo, setPartitionInfo] = useState<{ label: string; value: string | number }[]>([]);

  // 获取单个 Partition 详情
  const getDetail = () => {
    const { partitionId, leaderBrokerId, inSync } = record;

    Utils.request(api.getPartitionMetricInfo(clusterId, hashData.topicName, brokerId, partitionId), {
      method: 'POST',
      data: PARTITION_DETAIL_METRICS,
    }).then((res: any) => {
      const type = MetricType.Replication;
      const metricsData = res?.metrics || {};

      const partitionInfo = [
        { label: 'LeaderBroker', value: leaderBrokerId },
        {
          label: 'BeginningOffset',
          value: `${metricsData.LogStartOffset === undefined ? '-' : metricsData.LogStartOffset} ${
            global.getMetricDefine(type, 'LogStartOffset')?.unit || ''
          }`,
        },
        {
          label: 'EndOffset',
          value: `${metricsData.LogEndOffset === undefined ? '-' : metricsData.LogEndOffset} ${
            global.getMetricDefine(type, 'LogEndOffset')?.unit || ''
          }`,
        },
        {
          label: 'MsgNum',
          value: `${metricsData.Messages === undefined ? '-' : metricsData.Messages} ${
            global.getMetricDefine(type, 'Messages')?.unit || ''
          }`,
        },
        {
          label: 'LogSize',
          value: `${metricsData.LogSize === undefined ? '-' : Utils.formatAssignSize(metricsData.LogSize, 'MB')} MB`,
        },
        { label: '是否同步', value: inSync ? '是' : '否' },
      ];
      setPartitionInfo(partitionInfo);
      setLoading(false);
    });
  };

  useEffect(() => {
    getDetail();
  }, []);

  return (
    <Spin spinning={loading}>
      <div className="header">
        <div className="title">分区详情</div>
        <Button
          type="text"
          icon={<CloseOutlined className="close-icon" />}
          onClick={(e) => {
            e.preventDefault();
            e.stopPropagation();
            close();
          }}
        />
      </div>
      <div className="main">
        <List
          itemLayout="vertical"
          size="small"
          dataSource={partitionInfo}
          renderItem={(item) => (
            <List.Item key={item.label} extra={item.value}>
              {item.label}
            </List.Item>
          )}
        />
      </div>
    </Spin>
  );
};

const PartitionSummary = (props: { clusterId: string; topicName: string }) => {
  const { clusterId, topicName } = props;
  const [partitionsSummary, setPartitionsSummary] = useState<PartitionsSummary>(null);

  useEffect(() => {
    // 获取统计信息
    Utils.request(api.getTopicPartitionsSummary(clusterId, topicName)).then((res: PartitionsSummary) => {
      setPartitionsSummary(res);
    });
  }, []);

  return (
    <>
      <div className="info-box">
        <div className="info-detail info-detail-title">
          <span className="desc">Brokers 总数</span>
          <span className="num">{partitionsSummary?.brokerCount}</span>
        </div>
        <div className="info-detail">
          <span className="desc">live</span>
          <span className="num">{partitionsSummary?.liveBrokerCount}</span>
        </div>
        <div className="info-detail">
          <span className="desc">down</span>
          <span className="num">{partitionsSummary?.deadBrokerCount}</span>
        </div>
      </div>
      <div className="info-box">
        <div className="info-detail info-detail-title">
          <span className="desc">Partition 总数</span>
          <span className="num">{partitionsSummary?.partitionCount}</span>
        </div>
        <div className="info-detail">
          <span className="desc">No Leader</span>
          <span className="num">{partitionsSummary?.noLeaderPartitionCount}</span>
        </div>
        <div className="info-detail">
          <span className="desc">URP</span>
          <span className="num">{partitionsSummary?.underReplicatedPartitionCount}</span>
        </div>
      </div>
    </>
  );
};

const PartitionCard = (props: { clusterId: string; hashData: any }) => {
  const { clusterId, hashData } = props;
  const [brokersDetail, setBrokersDetail] = useState<BrokersDetail>();
  const [hoverPartitionId, setHoverPartitionId] = useState<brokerReplica['partitionId']>(-1);
  const [clickPartition, setClickPartition] = useState<string>('');
  const [loading, setLoading] = useState<boolean>(true);

  const closePartitionDetail = useCallback(() => setClickPartition(''), []);

  useEffect(() => {
    Utils.request(api.getTopicBrokersList(clusterId, hashData.topicName)).then(
      (res: any) => {
        setBrokersDetail(res);
        setLoading(false);
      },
      () => setLoading(false)
    );
  }, []);

  return (
    <Spin spinning={loading}>
      <div className="broker-container">
        {brokersDetail?.brokerPartitionStateList?.length ? (
          brokersDetail?.brokerPartitionStateList.map((partitionState) => {
            return (
              <div className="broker-container-box" key={partitionState.brokerId}>
                <div className="broker-container-box-header">
                  <div className="header-info">
                    <div className="label">Broker</div>
                    <div className="value">{partitionState.brokerId}</div>
                  </div>
                  <div className="header-info">
                    <div className="label">Host ID</div>
                    <div className="value">{partitionState.host || '-'}</div>
                  </div>
                  {['BytesIn', 'BytesOut'].map((type) => {
                    return (
                      <div className="header-info" key={type}>
                        <div className="label">{type}</div>
                        <div className="value">
                          {getTranformedBytes(
                            type === 'BytesIn' ? partitionState.bytesInOneMinuteRate : partitionState.bytesOutOneMinuteRate
                          ).map((val, i) => {
                            return i ? <span className="unit">{val}/s</span> : <span>{val}</span>;
                          })}
                        </div>
                      </div>
                    );
                  })}
                </div>
                <div className="broker-container-box-detail">
                  {partitionState.alive ? (
                    partitionState?.replicaList?.length ? (
                      <div className={`partition-list ${hoverPartitionId !== -1 ? 'partition-list-hover-state' : ''}`}>
                        {partitionState?.replicaList?.map((partition) => {
                          return (
                            <div
                              key={partition.partitionId}
                              className={`partition-list-item partition-list-item-${
                                partition.isLeaderReplace ? 'leader' : partition.inSync ? 'isr' : 'osr'
                              } ${partition.partitionId === hoverPartitionId ? 'partition-active' : ''}`}
                              onMouseEnter={() => setHoverPartitionId(partition.partitionId)}
                              onMouseLeave={() => setHoverPartitionId(-1)}
                              onClick={() => setClickPartition(`${partitionState.brokerId}&${partition.partitionId}`)}
                            >
                              <Popover
                                visible={clickPartition === `${partitionState.brokerId}&${partition.partitionId}`}
                                onVisibleChange={(v) => !v && closePartitionDetail()}
                                overlayClassName="broker-partition-popover"
                                content={
                                  <PartitionPopoverContent
                                    clusterId={clusterId}
                                    hashData={hashData}
                                    record={partition}
                                    brokerId={partitionState.brokerId}
                                    close={closePartitionDetail}
                                  />
                                }
                                destroyTooltipOnHide={true}
                                trigger="click"
                                placement="rightTop"
                              >
                                {partition.partitionId}
                              </Popover>
                            </div>
                          );
                        })}
                      </div>
                    ) : (
                      <RenderEmpty message="暂无数据" height="unset" />
                    )
                  ) : (
                    <RenderEmpty message="暂无数据" height="unset" />
                  )}
                </div>
              </div>
            );
          })
        ) : loading ? (
          <></>
        ) : (
          <Empty style={{ margin: '0 auto', marginTop: 100 }} />
        )}
      </div>
    </Spin>
  );
};

const PartitionTable = (props: { clusterId: string; hashData: any }) => {
  const { clusterId, hashData } = props;
  const [loading, setLoading] = useState<boolean>(true);
  const [partitionsDetail, setPartitionsDetail] = useState<PartitionDetail[]>([]);

  const columns = [
    {
      title: 'Partition ID',
      dataIndex: 'partitionId',
    },
    {
      title: 'StartOffset',
      dataIndex: ['latestMetrics', 'metrics', 'LogStartOffset'],
    },
    {
      title: 'EndOffset',
      dataIndex: ['latestMetrics', 'metrics', 'LogEndOffset'],
    },
    {
      title: 'MsgNum',
      dataIndex: ['latestMetrics', 'metrics', 'Messages'],
    },
    {
      title: 'Leader Broker',
      dataIndex: 'leaderBrokerId',
    },
    {
      title: 'LogSize(MB)',
      dataIndex: ['latestMetrics', 'metrics', 'LogSize'],
      render: (size: number | undefined) => (size === undefined ? '-' : Utils.formatAssignSize(size, 'MB')),
    },
    {
      title: 'AR',
      dataIndex: 'assignReplicas',
      width: 180,
      render: (arr: PartitionDetail['assignReplicas']) => <TagsWithHide list={arr} expandTagContent={(len) => `共有${len}个`} />,
    },
    {
      title: 'ISR',
      dataIndex: 'inSyncReplicas',
      width: 180,
      render: (arr: PartitionDetail['inSyncReplicas']) => <TagsWithHide list={arr} expandTagContent={(len) => `共有${len}个`} />,
    },
  ];

  useEffect(() => {
    Utils.request(api.getTopicPartitionsDetail(clusterId, hashData.topicName), {
      method: 'POST',
      data: PARTITION_DETAIL_METRICS,
    }).then((res: PartitionDetail[]) => {
      setPartitionsDetail(res);
      setLoading(false);
    });
  }, []);
  return (
    <ProTable
      tableProps={{
        loading,
        rowKey: 'partitionId',
        columns: columns as any,
        dataSource: partitionsDetail,
        showHeader: false,
      }}
    />
  );
};

export default (props: PropsType) => {
  const { clusterId } = useParams<{ clusterId: string }>();
  const { hashData } = props;
  const [showMode, setShowMode] = useState<string>('card');

  const [refreshKey, forceRefresh] = useForceRefresh();
  return (
    <>
      <div className="brokers-tab-container" key={`${refreshKey}`}>
        <div className="overview">
          <div className="left">
            <span
              style={{ display: 'inline-block', padding: '0 10px', marginRight: '10px', borderRight: '1px solid #ccc', fontSize: '15px' }}
              onClick={forceRefresh as () => void}
            >
              <i className="iconfont icon-shuaxin1" style={{ cursor: 'pointer' }} />
            </span>
            <PartitionSummary clusterId={clusterId} topicName={hashData.topicName} />
          </div>
          <div className="cases-box">
            {showMode === 'card' && (
              <div className="broker-cases">
                <div className="case case-leader">
                  <div className="icon"></div>
                  <div className="desc">Leader</div>
                </div>
                <div className="case case-isr">
                  <div className="icon"></div>
                  <div className="desc">ISR</div>
                </div>
                <div className="case case-osr">
                  <div className="icon"></div>
                  <div className="desc">OSR</div>
                </div>
              </div>
            )}
            <SwitchTab
              defaultKey="card"
              onChange={(key) => {
                setShowMode(key);
              }}
            >
              <SwitchTab.TabItem key="card">
                <div style={{ width: 34, height: 23 }}>
                  <IconFont type="icon-tubiao" />
                </div>
              </SwitchTab.TabItem>
              <SwitchTab.TabItem key="table">
                <div style={{ width: 34, height: 23 }}>
                  <IconFont type="icon-biaoge" />
                </div>
              </SwitchTab.TabItem>
            </SwitchTab>
          </div>
        </div>

        {showMode === 'card' ? (
          <PartitionCard clusterId={clusterId} hashData={hashData} />
        ) : (
          <PartitionTable clusterId={clusterId} hashData={hashData} />
        )}
      </div>
    </>
  );
};
