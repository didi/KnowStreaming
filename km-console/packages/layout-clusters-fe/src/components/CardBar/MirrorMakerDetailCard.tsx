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

const ConnectDetailCard = (props: { record: any; tabSelectType: string }) => {
  const { record, tabSelectType } = props;
  const urlParams = useParams<{ clusterId: string; brokerId: string }>();
  const urlLocation = useLocation<any>();
  const [loading, setLoading] = useState(false);
  const [cardData, setCardData] = useState([]);
  const [healthData, setHealthData] = useState<healthDataProps>({
    state: HealthStateEnum.UNKNOWN,
    passed: 0,
    total: 0,
  });

  const getHealthData = (tabSelectTypeName: string) => {
    return Utils.post(Api.getMirrorMakerMetricPoints(tabSelectTypeName, record?.connectClusterId), [
      'HealthState',
      'HealthCheckPassed',
      'HealthCheckTotal',
    ]).then((data: any) => {
      setHealthData({
        state: data?.metrics?.['HealthState'],
        passed: data?.metrics?.['HealthCheckPassed'] || 0,
        total: data?.metrics?.['HealthCheckTotal'] || 0,
      });
    });
  };

  const getCardInfo = (tabSelectTypeName: string) => {
    return Utils.request(Api.getConnectDetailState(tabSelectTypeName, record?.connectClusterId)).then((res: any) => {
      const { type, aliveTaskCount, state, totalTaskCount, totalWorkerCount } = res || {};
      const cordRightMap = [
        {
          title: 'Status',
          // value: Utils.firstCharUppercase(state) || '-',
          value: () => {
            return (
              <>
                {
                  <span style={{ fontFamily: 'HelveticaNeue-Medium', fontSize: 32, color: stateEnum[state].color }}>
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

  const noDataCardInfo = () => {
    const cordRightMap = [
      {
        title: 'Status',
        // value: Utils.firstCharUppercase(state) || '-',
        value() {
          return <span>-</span>;
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
          return <span>-/-</span>;
        },
      },
      {
        title: 'Workers',
        value() {
          return <span>-</span>;
        },
      },
    ];
    setCardData(cordRightMap);
  };

  useEffect(() => {
    setLoading(true);

    const filterCardInfo =
      tabSelectType === 'MirrorCheckpoint' && record.checkpointConnector
        ? getCardInfo(record.checkpointConnector)
        : tabSelectType === 'MirrorHeatbeat' && record.heartbeatConnector
        ? getCardInfo(record.heartbeatConnector)
        : tabSelectType === 'MirrorSource' && record.connectorName
        ? getCardInfo(record.connectorName)
        : noDataCardInfo();
    Promise.all([getHealthData(record.connectorName), filterCardInfo]).finally(() => {
      setLoading(false);
    });
  }, [record, tabSelectType]);
  return (
    <CardBar record={record} scene="mm2" healthData={healthData} cardColumns={cardData} showCardBg={false} loading={loading}></CardBar>
  );
};

export default ConnectDetailCard;
