import React, { useEffect } from 'react';
import { AppContainer, Utils } from 'knowdesign';
import { useParams, useHistory } from 'react-router-dom';
import API, { MetricType } from '../api';
import CommonConfig from './CommonConfig';

interface ControlInfo {
  type: MetricType.Controls;
  name: ControlStatusMap;
  desc: string;
  support: boolean;
  unit: string | null;
  minVersion: number;
  maxVersion: number;
}

export enum ControlStatusMap {
  // Broker
  BROKER_DETAIL_CONFIG = 'FEBrokersSpecifiedBrokerConfigList',
  BROKER_DETAIL_DATALOGS = 'FEBrokersSpecifiedBrokerDataLogsList',
  // Testing
  TESTING_PRODUCER_HEADER = 'FETestingProducerHeader',
  TESTING_PRODUCER_COMPRESSION_TYPE_ZSTD = 'FETestingProducerCompressionTypeZSTD',
  TESTING_CONSUMER_HEADER = 'FETestingConsumerHeader',
  // Topic
  CREATE_TOPIC_CLEANUP_POLICY = 'FECreateTopicCleanupPolicy',
}

const CommonRoute: React.FC = (props: any) => {
  const [global, setGlobal] = AppContainer.useGlobalValue();
  const { clusterId } = useParams<{ clusterId: string }>();
  const history = useHistory();
  const { Consumer } = AppContainer.context;

  useEffect(() => {
    getPhyClusterInfo();
    getControlsStatus();
  }, []);

  const getPhyClusterInfo = () => {
    Utils.request(API.getPhyClusterBasic(+clusterId), {
      init: {
        // needCode: true,
        errorNoTips: true,
      },
      retryTimes: 2,
      // timeout: 200,
    })
      .then((res: any) => {
        if (res) {
          setGlobal((curState: any) => ({ ...curState, clusterInfo: res }));
        } else {
          history.replace('/');
        }
      })
      .catch((err) => {
        history.replace('/');
        // notification.error({
        //   message: '错误',
        //   duration: 3,
        //   description: `${'集群不存在或集群ID有误'}`,
        // });
      });
  };

  // 获取控件状态
  const getControlsStatus = () => {
    Utils.request(API.getSupportKafkaVersions(clusterId, MetricType.Controls), {
      retryTimes: 2,
      init: {
        errorNoTips: true,
      },
    }).then((controlsStatus: ControlInfo[]) => {
      const isShowControl = (controlName: ControlStatusMap) => {
        const controlInfo = controlName && controlsStatus.find(({ name }) => name === controlName);
        return controlInfo ? controlInfo.support : false;
      };
      setGlobal((curState: any) => ({ ...curState, controlsStatus, isShowControl }));
    });
  };

  return (
    <>
      <CommonConfig />
    </>
  );
};

export default CommonRoute;
