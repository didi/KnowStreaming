import React, { useState, useEffect } from 'react';
import { useParams } from 'react-router-dom';
import CardBar from './index';
import { Tag, Utils } from 'knowdesign';
import api from '@src/api';

const ACLsCardBar = () => {
  const { clusterId } = useParams<{
    clusterId: string;
  }>();
  const [loading, setLoading] = useState(false);
  const [cardData, setCardData] = useState([]);
  const cardItems = ['AclEnable', 'Acls', 'AclUsers', 'AclTopics', 'AclGroups'];

  const getCartInfo = () => {
    return Utils.request(api.getMetricPointsLatest(Number(clusterId)), {
      method: 'POST',
      data: cardItems,
    });
  };
  useEffect(() => {
    setLoading(true);
    // 获取右侧状态
    getCartInfo().then(
      (res: {
        clusterPhyId: number;
        metrics: {
          [metric: string]: number;
        };
      }) => {
        const { AclEnable, Acls, AclUsers, AclTopics, AclGroups } = res.metrics;
        const cardMap = [
          {
            title: 'Enable',
            value() {
              return (
                <span style={{ fontFamily: 'HelveticaNeue', fontSize: 35, color: AclEnable ? '#00C0A2' : '#F58342' }}>
                  {AclEnable ? 'Yes' : 'No'}
                </span>
              );
            },
            customStyle: {
              // 自定义cardbar样式
              marginLeft: 0,
            },
          },
          {
            title: 'ACLs',
            value: Acls,
          },
          {
            title: 'Users',
            value: AclUsers,
          },
          {
            title: 'Topics',
            value: AclTopics,
          },
          {
            title: 'Consumer Groups',
            value: AclGroups,
          },
        ];
        setCardData(cardMap);
        setLoading(false);
      }
    );
  }, [clusterId]);
  return <CardBar scene="broker" cardColumns={cardData} loading={loading}></CardBar>;
};

export default ACLsCardBar;
