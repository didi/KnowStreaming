import React, { useState, useEffect } from 'react';
import { useParams } from 'react-router-dom';
import CardBar from '@src/components/CardBar';
import { healthDataProps } from '.';
import { Utils } from 'knowdesign';
import api from '@src/api';
import { HealthStateEnum } from '../HealthState';

export default () => {
  const routeParams = useParams<{
    clusterId: string;
  }>();
  const [loading, setLoading] = useState(false);
  const [cardData, setCardData] = useState([]);
  const [healthData, setHealthData] = useState<healthDataProps>({
    state: HealthStateEnum.UNKNOWN,
    passed: 0,
    total: 0,
  });
  const cardItems = ['Topics', 'Partitions', 'PartitionNoLeader', 'PartitionMinISR_S', 'PartitionMinISR_E', 'PartitionURP'];
  const healthItems = ['HealthCheckPassed_Topics', 'HealthCheckTotal_Topics', 'HealthState'];
  useEffect(() => {
    setLoading(true);
    Utils.post(api.getMetricPointsLatest(Number(routeParams.clusterId)), cardItems.concat(healthItems)).then((data: any) => {
      setLoading(false);
      const metricElmMap: any = {
        PartitionMinISR_S: () => {
          return (
            <>
              <span style={{ color: '#FF8B56', fontSize: 20, fontWeight: 'bold' }}>&lt;</span> Min ISR
            </>
          );
        },
        PartitionMinISR_E: () => {
          return (
            <>
              <span style={{ color: '#556EE6', fontSize: 20, fontWeight: 'bold' }}>&lt;</span> Min ISR
            </>
          );
        },
        PartitionURP: 'URP',
        PartitionNoLeader: 'No Leader',
      };
      setCardData(
        cardItems.map((item) => {
          let title = item;
          if (title === 'PartitionMinISR_E') {
            title = '= Min ISR';
          }
          if (title === 'PartitionMinISR_S') {
            return {
              title: '< Min ISR',
              value: <span style={{ color: data.metrics[item] !== 0 ? '#F58342' : '' }}>{data.metrics[item]}</span>,
            };
          }
          if (title === 'PartitionNoLeader' || title === 'PartitionURP') {
            return { title, value: <span style={{ color: data.metrics[item] !== 0 ? '#F58342' : '' }}>{data.metrics[item]}</span> };
          }
          return { title, value: data.metrics[item] };
        })
      );
      setHealthData({
        state: data.metrics['HealthState'],
        passed: data.metrics['HealthCheckPassed_Topics'] || 0,
        total: data.metrics['HealthCheckTotal_Topics'] || 0,
      });
    });
  }, []);
  return <CardBar scene="topics" healthData={healthData} cardColumns={cardData} loading={loading}></CardBar>;
};
