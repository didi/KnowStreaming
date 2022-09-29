import React, { useState, useEffect } from 'react';
import { useParams } from 'react-router-dom';
import CardBar from '@src/components/CardBar';
import { healthDataProps } from '.';
import { Utils } from 'knowdesign';
import { IconFont } from '@knowdesign/icons';
import api from '@src/api';
import { healthScoreCondition } from './const';
import { hashDataParse } from '@src/constants/common';

const renderValue = (v: string | number | ((visibleType?: boolean) => JSX.Element), visibleType?: boolean) => {
  return typeof v === 'function' ? v(visibleType) : v;
};
export default (props: { record: any }) => {
  const { record } = props;
  const routeParams = useParams<{
    clusterId: string;
  }>();
  const [loading, setLoading] = useState(false);
  const [cardData, setCardData] = useState([]);
  const [healthData, setHealthData] = useState<healthDataProps>({
    score: 0,
    passed: 0,
    total: 0,
    alive: 0,
  });
  const [healthDetail, setHealthDetail] = useState([]);
  const [clusterAlive, setClusterAlive] = useState(0);
  const healthItems = ['HealthScore', 'HealthCheckPassed', 'HealthCheckTotal', 'alive'];
  const getNumAndSubTitles = (cardColumnsItemData: any) => {
    return (
      <div style={{ width: '100%', display: 'flex', alignItems: 'end' }}>
        <span>{cardColumnsItemData.value}</span>
        <div className="sub-title" style={{ transform: 'scale(0.83) translateY(14px)' }}>
          <span className="txt">{renderValue(cardColumnsItemData.subTitle)}</span>
          <span className="icon-wrap">
            {cardColumnsItemData.subTitleStatus ? <IconFont type="icon-zhengchang"></IconFont> : <IconFont type="icon-yichang"></IconFont>}
          </span>
        </div>
      </div>
    );
  };
  useEffect(() => {
    setLoading(true);
    const topicName = hashDataParse(location.hash)['topicName'];
    let detailHealthPromise = Utils.post(api.getTopicMetricPointsLatest(Number(routeParams.clusterId), topicName), healthItems).then(
      (data: any) => {
        let healthResData: any = {};
        healthResData.score = data.metrics['HealthScore'] || 0;
        healthResData.passed = data.metrics['HealthCheckPassed'] || 0;
        healthResData.total = data.metrics['HealthCheckTotal'] || 0;
        // healthResData.alive = data.metrics['alive'] || 0
        setHealthData(healthResData);
      }
    );

    let detailStatePromise = Utils.request(api.getTopicState(Number(routeParams.clusterId), topicName)).then((topicHealthState: any) => {
      setCardData([
        {
          title: 'Partitions',
          value: () => {
            return getNumAndSubTitles({
              value: topicHealthState.partitionCount || '-',
              subTitle: 'All have a leader',
              subTitleStatus: topicHealthState.allPartitionHaveLeader,
            });
          },
        },
        {
          title: 'Replications',
          value: topicHealthState.replicaFactor || '-',
          subTitle: `All ISRs = ${topicHealthState.replicaFactor}`,
          subTitleStatus: topicHealthState.allReplicaInSync,
        },
        {
          title: 'Min ISR',
          value: topicHealthState.minimumIsr || '-',
          subTitle: `All ISRs ≥ ${topicHealthState.minimumIsr}`,
          subTitleStatus: topicHealthState.allPartitionMatchAtMinIsr,
        },
        {
          title: 'Is Compacted',
          value: () => {
            return <span style={{ fontFamily: 'HelveticaNeue' }}>{topicHealthState.compacted ? 'YES' : 'NO'}</span>;
          },
        },
      ]);
    });
    // 获取集群维度的指标信息
    let clusterStatePromise = Utils.post(api.getMetricPointsLatest(Number(routeParams.clusterId)), ['Alive']).then(
      (clusterHealthState: any) => {
        let clusterAlive = clusterHealthState?.metrics?.Alive || 0;
        setClusterAlive(clusterAlive);
      }
    );
    Promise.all([detailHealthPromise, detailStatePromise, clusterStatePromise]).then((res) => {
      setLoading(false);
    });
  }, []);
  return (
    <CardBar
      record={record}
      scene="topic"
      healthData={{ ...healthData, alive: clusterAlive }}
      cardColumns={cardData}
      showCardBg={false}
      loading={loading}
    ></CardBar>
  );
};
