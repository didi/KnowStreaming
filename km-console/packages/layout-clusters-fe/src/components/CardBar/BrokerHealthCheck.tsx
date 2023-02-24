/* eslint-disable react/display-name */
import React, { useState, useEffect } from 'react';
import { useParams } from 'react-router-dom';
import CardBar from '@src/components/CardBar';
import { healthDataProps } from '.';
import { Tag, Tooltip, Utils } from 'knowdesign';
import api from '@src/api';
import { QuestionCircleOutlined } from '@ant-design/icons';
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
  const healthItems = ['HealthCheckPassed_Brokers', 'HealthCheckTotal_Brokers', 'HealthState'];

  useEffect(() => {
    setLoading(true);
    // 获取左侧健康度
    const brokerMetric = Utils.post(api.getBrokerMetricPoints(Number(routeParams.clusterId)), healthItems).then((data: any) => {
      setHealthData({
        state: data?.metrics?.['HealthState'],
        passed: data?.metrics?.['HealthCheckPassed_Brokers'] || 0,
        total: data?.metrics?.['HealthCheckTotal_Brokers'] || 0,
      });
    });
    // 获取右侧状态
    const brokersState = Utils.request(api.getBrokersState(routeParams?.clusterId)).then((data) => {
      const rightData = JSON.parse(JSON.stringify(data));
      const cordRightMap = [
        {
          title: 'Brokers',
          value: () => {
            return (
              <div style={{ width: '100%', display: 'flex', alignItems: 'end' }}>
                <span>{rightData?.brokerCount}</span>
                <span style={{ display: 'flex', fontSize: '13px' }}>
                  {rightData?.brokerVersionList?.map((item: any, key: number) => {
                    return (
                      <Tag
                        style={{
                          padding: '2px 5px',
                          marginLeft: '8px',
                          backgroundColor: '#ECECF6',
                          fontFamily: 'Helvetica Neue, PingFangSC',
                        }}
                        key={key}
                      >
                        {item}
                      </Tag>
                    );
                  })}
                </span>
              </div>
            );
          },
        },
        {
          title: 'Controller',
          value: () => {
            return rightData?.kafkaController && rightData?.kafkaControllerAlive ? (
              <div style={{ width: '100%', display: 'flex', alignItems: 'end' }}>
                <span>{rightData?.kafkaController.brokerId}</span>
                <span style={{ display: 'flex', fontSize: '13px' }}>
                  <Tag
                    style={{ padding: '2px 5px', marginLeft: '8px', backgroundColor: '#ECECF6', fontFamily: 'Helvetica Neue, PingFang SC' }}
                  >
                    {rightData?.kafkaController.brokerHost}
                  </Tag>
                </span>
              </div>
            ) : (
              <span style={{ fontFamily: 'Helvetica Neue' }}>None</span>
            );
          },
        },
        {
          title() {
            return (
              <div>
                <span style={{ display: 'inline-block', marginRight: '8px' }}>Similar Config</span>
                <Tooltip overlayClassName="rebalance-tooltip" title="所有Broker配置是否一致">
                  <QuestionCircleOutlined />
                </Tooltip>
              </div>
            );
          },
          value: () => {
            return (
              <>
                {
                  <span style={{ fontFamily: 'Helvetica Neue', fontSize: 36, color: rightData?.configSimilar ? '' : '#F58342' }}>
                    {rightData?.configSimilar ? 'YES' : 'NO'}
                  </span>
                }
              </>
            );
          },
        },
      ];
      setCardData(cordRightMap);
    });
    Promise.all([brokerMetric, brokersState]).finally(() => {
      setLoading(false);
    });
  }, [routeParams.clusterId]);

  return <CardBar scene="brokers" healthData={healthData} cardColumns={cardData} loading={loading}></CardBar>;
};
