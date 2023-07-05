import React, { useState, useEffect } from 'react';
import { useParams } from 'react-router-dom';
import CardBar, { healthDataProps } from './index';
import { Tooltip, Utils } from 'knowdesign';
import api from '@src/api';
import { HealthStateEnum } from '../HealthState';
import { InfoCircleOutlined } from '@ant-design/icons';

interface MM2State {
  workerCount: number;
  aliveConnectorCount: number;
  aliveTaskCount: number;
  healthCheckPassed: number;
  healthCheckTotal: number;
  healthState: number;
  totalConnectorCount: string;
  totalTaskCount: number;
  totalServerCount: number;
  mirrorMakerCount: number;
}

const getVal = (val: string | number | undefined | null) => {
  return val === undefined || val === null || val === '' ? '0' : val;
};

const ConnectCard = ({ state }: { state?: boolean }) => {
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
    return Utils.post(api.getMetricPointsLatest(Number(clusterId)), [
      'HealthCheckPassed_MirrorMaker',
      'HealthCheckTotal_MirrorMaker',
      'HealthState_MirrorMaker',
    ]).then((data: any) => {
      setHealthData({
        state: data?.metrics?.['HealthState_MirrorMaker'],
        passed: data?.metrics?.['HealthCheckPassed_MirrorMaker'] || 0,
        total: data?.metrics?.['HealthCheckTotal_MirrorMaker'] || 0,
      });
    });
  };

  const getCardInfo = () => {
    return Utils.request(api.getMirrorMakerState(clusterId)).then((res: MM2State) => {
      const { mirrorMakerCount, aliveConnectorCount, aliveTaskCount, totalConnectorCount, totalTaskCount, workerCount } = res || {};
      const cardMap = [
        {
          title: 'MM2s',
          value: getVal(mirrorMakerCount),
          customStyle: {
            // 自定义cardbar样式
            marginLeft: 0,
          },
        },
        {
          title: 'Workers',
          value: getVal(workerCount),
        },
        {
          title() {
            return (
              <div>
                <span style={{ display: 'inline-block', marginRight: '8px' }}>Connectors</span>
                <Tooltip overlayClassName="rebalance-tooltip" title="conector运行数/总数">
                  <InfoCircleOutlined />
                </Tooltip>
              </div>
            );
          },
          value() {
            return (
              <span>
                {getVal(aliveConnectorCount)}/{getVal(totalConnectorCount)}
              </span>
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
      ];
      setCardData(cardMap);
    });
  };
  useEffect(() => {
    setLoading(true);
    Promise.all([getHealthData(), getCardInfo()]).finally(() => {
      setLoading(false);
    });
  }, [clusterId, state]);
  return <CardBar scene="mm2" healthData={healthData} cardColumns={cardData} loading={loading}></CardBar>;
};

export default ConnectCard;
