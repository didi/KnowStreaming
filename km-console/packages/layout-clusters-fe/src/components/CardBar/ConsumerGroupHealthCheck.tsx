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
  const cardItems = ['Groups', 'GroupActives', 'GroupEmptys', 'GroupRebalances', 'GroupDeads'];
  const healthItems = ['HealthCheckPassed_Groups', 'HealthCheckTotal_Groups', 'HealthState'];

  useEffect(() => {
    setLoading(true);
    Utils.post(api.getMetricPointsLatest(Number(routeParams.clusterId)), cardItems.concat(healthItems)).then((data: any) => {
      setLoading(false);
      setCardData(
        cardItems.map((item) => {
          if (item === 'GroupDeads') {
            return { title: item, value: <span style={{ color: data.metrics[item] !== 0 ? '#F58342' : '' }}>{data.metrics[item]}</span> };
          }
          return { title: item, value: data.metrics[item] };
        })
      );
      setHealthData({
        state: data?.metrics?.['HealthState'],
        passed: data?.metrics?.['HealthCheckPassed_Groups'] || 0,
        total: data?.metrics?.['HealthCheckTotal_Groups'] || 0,
      });
    });
  }, []);
  return <CardBar scene="group" healthData={healthData} cardColumns={cardData} loading={loading}></CardBar>;
};
