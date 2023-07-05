/* eslint-disable react/display-name */
import React, { useState, useEffect } from 'react';
import { useParams } from 'react-router-dom';
import CardBar from '@src/components/CardBar';
import { Utils } from 'knowdesign';
import Api from '@src/api';

export default () => {
  const routeParams = useParams<{
    clusterId: string;
  }>();
  const [loading, setLoading] = useState(false);
  const [cardData, setCardData] = useState([]);

  const getCordRightMap = (data: any) => {
    const cordRightMap = [
      {
        title: 'Jobs',
        value: data?.jobNu === 0 || data?.jobNu ? data?.jobNu : '-',
        customStyle: {
          // 自定义cardbar样式
          marginLeft: 0,
        },
      },
      {
        title: 'Doing',
        value: data?.runningNu === 0 || data?.runningNu ? data?.runningNu : '-',
      },
      {
        title: 'Prepare',
        value: data?.waitingNu === 0 || data?.waitingNu ? data?.waitingNu : '-',
      },
      {
        title: 'Success',
        value: data?.successNu === 0 || data?.successNu ? data?.successNu : '-',
      },
      {
        title: 'Fail',
        value: data?.failedNu === 0 || data?.failedNu ? data?.failedNu : '-',
      },
    ];
    return cordRightMap;
  };

  useEffect(() => {
    setLoading(true);
    // 获取状态
    Utils.request(Api.getJobsState(routeParams?.clusterId))
      .then((data) => {
        const rightData = JSON.parse(JSON.stringify(data));
        setCardData(getCordRightMap(rightData));
        setLoading(false);
      })
      .catch((err) => {
        setCardData(getCordRightMap({}));
        setLoading(false);
      });
  }, [routeParams.clusterId]);
  return <CardBar scene="broker" cardColumns={cardData} loading={loading}></CardBar>;
};
