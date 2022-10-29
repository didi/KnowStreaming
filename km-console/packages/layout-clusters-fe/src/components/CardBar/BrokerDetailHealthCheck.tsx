/* eslint-disable react/display-name */
import React, { useState, useEffect } from 'react';
import { useLocation, useParams } from 'react-router-dom';
import CardBar from '@src/components/CardBar';
import { healthDataProps } from '.';
import { Utils } from 'knowdesign';
import Api from '@src/api';
import { hashDataParse } from '@src/constants/common';
import { HealthStateEnum } from '../HealthState';

export default (props: { record: any }) => {
  const { record } = props;
  const urlParams = useParams<{ clusterId: string; brokerId: string }>();
  const urlLocation = useLocation<any>();
  const [loading, setLoading] = useState(false);
  const [cardData, setCardData] = useState([]);
  const [healthData, setHealthData] = useState<healthDataProps>({
    state: HealthStateEnum.UNKNOWN,
    passed: 0,
    total: 0,
  });

  useEffect(() => {
    setLoading(true);
    Utils.post(Api.getBrokerDetailMetricPoints(hashDataParse(urlLocation.hash)?.brokerId, urlParams?.clusterId), [
      'Partitions',
      'Leaders',
      'PartitionURP',
      'HealthCheckPassed',
      'HealthCheckTotal',
      'HealthState',
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
      setCardData(cordRightMap);
      setHealthData({
        state: data?.metrics?.['HealthState'],
        passed: data?.metrics?.['HealthCheckPassed'] || 0,
        total: data?.metrics?.['HealthCheckTotal'] || 0,
      });
    });
  }, []);
  return (
    <CardBar record={record} scene="broker" healthData={healthData} cardColumns={cardData} showCardBg={false} loading={loading}></CardBar>
  );
};
