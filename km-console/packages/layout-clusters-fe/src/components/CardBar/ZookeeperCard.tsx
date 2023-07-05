import React, { useState, useEffect } from 'react';
import { useParams } from 'react-router-dom';
import CardBar, { healthDataProps } from './index';
import { Tooltip, Utils } from 'knowdesign';
import api from '@src/api';
import { HealthStateEnum } from '../HealthState';

interface ZookeeperState {
  aliveFollowerCount: number;
  aliveObserverCount: number;
  aliveServerCount: number;
  healthCheckPassed: number;
  healthCheckTotal: number;
  healthState: number;
  leaderNode: string;
  totalFollowerCount: number;
  totalObserverCount: number;
  totalServerCount: number;
  watchCount: number;
}

const getVal = (val: string | number | undefined | null) => {
  return val === undefined || val === null || val === '' ? '-' : val;
};

const ZookeeperCard = () => {
  const { clusterId } = useParams<{
    clusterId: string;
  }>();
  const [loading, setLoading] = useState(false);
  const [cardData, setCardData] = useState([]);
  const [healthData, setHealthData] = useState<healthDataProps>({
    state: HealthStateEnum.UNKNOWN,
    passed: 0,
    total: 0,
  });

  const getHealthData = () => {
    return Utils.post(api.getZookeeperMetricsInfo(Number(clusterId)), ['HealthCheckPassed', 'HealthCheckTotal', 'HealthState']).then(
      (data: any) => {
        setHealthData({
          state: data?.metrics?.['HealthState'],
          passed: data?.metrics?.['HealthCheckPassed'] || 0,
          total: data?.metrics?.['HealthCheckTotal'] || 0,
        });
      }
    );
  };

  const getCardInfo = () => {
    return Utils.request(api.getZookeeperState(clusterId)).then((res: ZookeeperState) => {
      const {
        aliveFollowerCount,
        aliveObserverCount,
        aliveServerCount,
        totalFollowerCount,
        totalObserverCount,
        totalServerCount,
        watchCount,
        leaderNode,
      } = res || {};
      const cardMap = [
        {
          title: 'Node Count',
          value() {
            return (
              <span>
                {aliveServerCount || '-'}/{totalServerCount || '-'}
              </span>
            );
          },
          customStyle: {
            // 自定义cardbar样式
            marginLeft: 0,
          },
        },
        {
          title: 'Watch Count',
          value: getVal(watchCount),
        },
        {
          title: 'Leader',
          value() {
            return (
              <Tooltip title={leaderNode}>
                <span
                  style={{
                    fontSize: 24,
                    overflow: 'hidden',
                    display: 'block',
                    width: '100%',
                    whiteSpace: 'nowrap',
                    textOverflow: 'ellipsis',
                  }}
                >
                  {leaderNode || '-'}
                </span>
              </Tooltip>
            );
          },
        },
        {
          title: 'Follower',
          value() {
            return (
              <span>
                {getVal(aliveFollowerCount)}/{getVal(totalFollowerCount)}
              </span>
            );
          },
        },
        {
          title: 'Observer',
          value() {
            return (
              <span>
                {getVal(aliveObserverCount)}/{getVal(totalObserverCount)}
              </span>
            );
          },
        },
      ];
      setCardData(cardMap);
    });
  };
  useEffect(() => {
    setLoading(true);
    Promise.all([getHealthData(), getCardInfo()]).finally(() => {
      setLoading(false);
    });
  }, [clusterId]);
  return <CardBar scene="zookeeper" healthData={healthData} cardColumns={cardData} loading={loading}></CardBar>;
};

export default ZookeeperCard;
