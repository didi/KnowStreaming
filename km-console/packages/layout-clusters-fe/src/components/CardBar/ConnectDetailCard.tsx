/* eslint-disable react/display-name */
import React, { useState, useEffect } from 'react';
import { useLocation, useParams } from 'react-router-dom';
import CardBar from '@src/components/CardBar';
import { healthDataProps } from '.';
import { Tooltip, Utils } from 'knowdesign';
import Api from '@src/api';
import { hashDataParse } from '@src/constants/common';
import { HealthStateEnum } from '../HealthState';
import { InfoCircleOutlined } from '@ant-design/icons';
import { stateEnum } from '@src/pages/Connect/config';
const getVal = (val: string | number | undefined | null) => {
  return val === undefined || val === null || val === '' ? '0' : val;
};

const ConnectDetailCard = (props: { record: any }) => {
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

  const getHealthData = () => {
    return Utils.post(Api.getConnectDetailMetricPoints(record.connectorName, record?.connectClusterId), [
      'HealthCheckPassed',
      'HealthCheckTotal',
      'HealthState',
    ]).then((data: any) => {
      setHealthData({
        state: data?.metrics?.['HealthState'],
        passed: data?.metrics?.['HealthCheckPassed'] || 0,
        total: data?.metrics?.['HealthCheckTotal'] || 0,
      });
    });
  };

  const getCardInfo = () => {
    return Utils.request(Api.getConnectDetailState(record.connectorName, record?.connectClusterId)).then((res: any) => {
      const { type, aliveTaskCount, state, totalTaskCount, totalWorkerCount } = res || {};
      const cordRightMap = [
        {
          title: 'Type',
          value: () => {
            return (
              <>
                {
                  <span style={{ fontFamily: 'HelveticaNeue-Medium', fontSize: 28, color: '#212529' }}>
                    {Utils.firstCharUppercase(type) || '-'}
                  </span>
                }
              </>
            );
          },
        },
        {
          title: 'Status',
          // value: Utils.firstCharUppercase(state) || '-',
          value: () => {
            return (
              <>
                {
                  <span style={{ fontFamily: 'HelveticaNeue-Medium', fontSize: 28, color: stateEnum[state].color }}>
                    {Utils.firstCharUppercase(state) || '-'}
                  </span>
                }
              </>
            );
          },
        },

        {
          title() {
            return (
              <div>
                <span style={{ display: 'inline-block', marginRight: '8px' }}>Tasks</span>
                <Tooltip overlayClassName="rebalance-tooltip" title="Task运行数/总数">
                  <InfoCircleOutlined />
                </Tooltip>
              </div>
            );
          },
          value() {
            return (
              <span>
                {getVal(aliveTaskCount)}/{getVal(totalTaskCount)}
              </span>
            );
          },
        },
        {
          title: 'Workers',
          value: getVal(totalWorkerCount),
        },
      ];
      setCardData(cordRightMap);
    });
  };

  useEffect(() => {
    setLoading(true);
    Promise.all([getHealthData(), getCardInfo()]).finally(() => {
      setLoading(false);
    });
  }, [record]);
  return (
    <CardBar
      record={record}
      scene="connector"
      healthData={healthData}
      cardColumns={cardData}
      showCardBg={false}
      loading={loading}
    ></CardBar>
  );
};

export default ConnectDetailCard;
