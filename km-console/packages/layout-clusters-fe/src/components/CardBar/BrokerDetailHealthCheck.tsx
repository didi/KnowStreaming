/* eslint-disable react/display-name */
import React, { useState, useEffect } from 'react';
import { useHistory, useLocation, useParams } from 'react-router-dom';
import CardBar from '@src/components/CardBar';
import { healthDataProps } from '.';
import { Tag, Utils } from 'knowdesign';
import Api from '@src/api';
import { hashDataParse } from '@src/constants/common';

export default (props: { record: any }) => {
  const { record } = props;
  const urlParams = useParams<{ clusterId: string; brokerId: string }>();
  const urlLocation = useLocation<any>();
  const [loading, setLoading] = useState(false);
  const [cardData, setCardData] = useState([]);
  const [healthData, setHealthData] = useState<healthDataProps>({
    score: 0,
    passed: 0,
    total: 0,
    alive: 0,
  });
  const healthItems = ['HealthScore_Topics', 'HealthCheckPassed_Topics', 'HealthCheckTotal_Topics', 'live'];
  useEffect(() => {
    setLoading(true);
    Utils.post(Api.getBrokerDetailMetricPoints(hashDataParse(urlLocation.hash)?.brokerId, urlParams?.clusterId), [
      'Partitions',
      'Leaders',
      'PartitionURP',
      'HealthScore',
      'HealthCheckPassed',
      'HealthCheckTotal',
      'Alive',
    ]).then((data: any) => {
      setLoading(false);
      const rightData = JSON.parse(JSON.stringify(data.metrics));
      const cordRightMap = [
        {
          title: 'Partitions',
          value: rightData['Partitions'] || '-',
        },
        {
          title: 'Leaders',
          value: rightData['Leaders'] || '-',
        },
        {
          title: 'Under Replicated Partitions',
          value: rightData['PartitionURP'] || '-',
        },
      ];
      const healthResData: any = {};
      healthResData.score = data?.metrics?.['HealthScore'] || 0;
      healthResData.passed = data?.metrics?.['HealthCheckPassed'] || 0;
      healthResData.total = data?.metrics?.['HealthCheckTotal'] || 0;
      healthResData.alive = data?.metrics?.['Alive'] || 0;
      setCardData(cordRightMap);
      setHealthData(healthResData);
      // setCardData(data.metrics)
    });
  }, []);
  return (
    <CardBar record={record} scene="broker" healthData={healthData} cardColumns={cardData} showCardBg={false} loading={loading}></CardBar>
  );
};
