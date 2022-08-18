import React, { useState, useEffect } from 'react';
import { useParams } from 'react-router-dom';
import CardBar from '@src/components/CardBar';
import { healthDataProps } from '.';
import { Utils } from 'knowdesign';
import api from '@src/api';

export default () => {
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
  const cardItems = ['Groups', 'GroupActives', 'GroupEmptys', 'GroupRebalances', 'GroupDeads'];
  const healthItems = ['HealthScore_Groups', 'HealthCheckPassed_Groups', 'HealthCheckTotal_Groups', 'Alive'];
  useEffect(() => {
    setLoading(true);
    Utils.post(api.getMetricPointsLatest(Number(routeParams.clusterId)), cardItems.concat(healthItems)).then((data: any) => {
      setLoading(false);
      // setCardData(data
      //   .filter((item: any) => cardItems.indexOf(item.metricName) >= 0)
      //   .map((item: any) => ({ title: item.metricName, value: item.value }))
      // )
      setCardData(
        cardItems.map((item) => {
          if (item === 'GroupDeads') {
            return { title: item, value: <span style={{ color: data.metrics[item] !== 0 ? '#F58342' : '' }}>{data.metrics[item]}</span> };
          }
          return { title: item, value: data.metrics[item] };
        })
      );
      const healthResData: any = {};
      healthResData.score = data.metrics['HealthScore_Groups'] || 0;
      healthResData.passed = data.metrics['HealthCheckPassed_Groups'] || 0;
      healthResData.total = data.metrics['HealthCheckTotal_Groups'] || 0;
      healthResData.alive = data.metrics['Alive'] || 0;
      setHealthData(healthResData);
    });
  }, []);
  return <CardBar scene="group" healthData={healthData} cardColumns={cardData} loading={loading}></CardBar>;
};
