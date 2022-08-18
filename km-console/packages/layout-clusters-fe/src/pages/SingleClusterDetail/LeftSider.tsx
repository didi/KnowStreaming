import { AppContainer, Divider, IconFont, Progress, Tooltip, Utils } from 'knowdesign';
import React, { useEffect, useState } from 'react';
import AccessClusters from '../MutliClusterPage/AccessCluster';
import './index.less';
import API from '../../api';
import HealthySetting from './HealthySetting';
import CheckDetail from './CheckDetail';
import { Link, useHistory, useParams } from 'react-router-dom';
import { getHealthClassName, getHealthProcessColor, getHealthState, getHealthText, renderToolTipValue } from './config';
import { ClustersPermissionMap } from '../CommonConfig';

const LeftSider = () => {
  const [global] = AppContainer.useGlobalValue();
  const history = useHistory();
  const [kafkaVersion, setKafkaVersion] = useState({});
  const [clusterInfo, setClusterInfo] = useState({} as any);
  const [loading, setLoading] = React.useState(false);
  const [clusterMetrics, setClusterMetrics] = useState({} as any);
  const [brokerState, setBrokerState] = useState({} as any);

  const detailDrawerRef: any = React.createRef();
  const healthyDrawerRef: any = React.createRef();
  const { clusterId } = useParams<{ clusterId: string }>();
  const [visible, setVisible] = React.useState(false);

  const getSupportKafkaVersion = () => {
    Utils.request(API.supportKafkaVersion).then((res) => {
      setKafkaVersion(res || {});
    });
  };

  const getBrokerState = () => {
    return Utils.request(API.getBrokersState(clusterId)).then((res) => {
      setBrokerState(res);
    });
  };

  const getPhyClusterMetrics = () => {
    return Utils.post(
      API.getPhyClusterMetrics(+clusterId),
      [
        'HealthScore',
        'HealthCheckPassed',
        'HealthCheckTotal',
        'Topics',
        'PartitionURP',
        'PartitionNoLeader', // > 0 error
        'PartitionMinISR_S', // > 0 error
        'Groups',
        'GroupDeads',
        'Alive',
      ].concat(process.env.BUSINESS_VERSION ? ['LoadReBalanceEnable', 'LoadReBalanceNwIn', 'LoadReBalanceNwOut', 'LoadReBalanceDisk'] : [])
    ).then((res: any) => {
      setClusterMetrics(res?.metrics || {});
    });
  };

  const getPhyClusterInfo = () => {
    setLoading(true);
    Utils.request(API.getPhyClusterBasic(+clusterId))
      .then((res: any) => {
        let jmxProperties = null;
        try {
          jmxProperties = JSON.parse(res?.jmxProperties);
        } catch (err) {
          console.error(err);
        }

        // 转化值对应成表单值
        if (jmxProperties?.openSSL) {
          jmxProperties.security = 'Password';
        }

        if (jmxProperties) {
          res = Object.assign({}, res || {}, jmxProperties);
        }
        setClusterInfo(res);
        setLoading(false);
      })
      .catch((err) => {
        setLoading(false);
      });
  };

  useEffect(() => {
    getBrokerState();
    getPhyClusterMetrics();
    getSupportKafkaVersion();
    getPhyClusterInfo();
  }, []);

  const renderIcon = (type: string) => {
    return (
      <span className={`icon`}>
        <IconFont type={type === 'green' ? 'icon-zhengchang' : type === 'warning' ? 'icon-yichang' : 'icon-warning'} />
      </span>
    );
  };

  return (
    <>
      <div className="left-sider">
        <div className="state-card">
          <Progress
            type="circle"
            status="active"
            strokeWidth={4}
            strokeColor={getHealthProcessColor(clusterMetrics?.HealthScore, clusterMetrics?.Alive)}
            percent={clusterMetrics?.HealthScore ?? '-'}
            format={() => (
              <div className={`healthy-percent ${getHealthClassName(clusterMetrics?.HealthScore, clusterMetrics?.Alive)}`}>
                {getHealthText(clusterMetrics?.HealthScore, clusterMetrics?.Alive)}
              </div>
            )}
            width={75}
          />
          <div className="healthy-state">
            <div className="healthy-state-status">
              <span>{getHealthState(clusterMetrics?.HealthScore, clusterMetrics?.Alive)}</span>
              {/* 健康分设置 */}
              {global.hasPermission && global.hasPermission(ClustersPermissionMap.CLUSTER_CHANGE_HEALTHY) ? (
                <span
                  className="icon"
                  onClick={() => {
                    healthyDrawerRef.current.getHealthconfig().then(() => {
                      healthyDrawerRef.current.setVisible(true);
                    });
                  }}
                >
                  <IconFont type="icon-shezhi" size={13} />
                </span>
              ) : (
                <></>
              )}
            </div>
            <div>
              <span className="healthy-state-num">
                {clusterMetrics?.HealthCheckPassed}/{clusterMetrics?.HealthCheckTotal}
              </span>
              {/* 健康度详情 */}
              <span
                className="healthy-state-btn"
                onClick={() => {
                  detailDrawerRef.current.setVisible(true);
                }}
              >
                查看详情
              </span>
            </div>
          </div>
        </div>
        <Divider />
        <div className="title">
          <div className="name">{renderToolTipValue(clusterInfo?.name, 35)}</div>
          {global.hasPermission && global.hasPermission(ClustersPermissionMap.CLUSTER_CHANGE_INFO) ? (
            <div className="edit-icon-box" onClick={() => setVisible(true)}>
              <IconFont className="edit-icon" type="icon-bianji2" />
            </div>
          ) : (
            <></>
          )}
        </div>
        <div className="tag-panel">
          <div className="tag default">{clusterInfo?.kafkaVersion ?? '-'}</div>
          {clusterMetrics?.LoadReBalanceEnable !== undefined &&
            [
              ['BytesIn', clusterMetrics?.LoadReBalanceEnable && clusterMetrics?.LoadReBalanceNwIn],
              ['BytesOut', clusterMetrics?.LoadReBalanceEnable && clusterMetrics?.LoadReBalanceNwOut],
              ['Disk', clusterMetrics?.LoadReBalanceEnable && clusterMetrics?.LoadReBalanceDisk],
            ].map(([name, isBalanced]) => {
              return isBalanced ? (
                <div className="tag balanced">{name} 已均衡</div>
              ) : clusterMetrics?.LoadReBalanceEnable ? (
                <div className="tag unbalanced">{name} 未均衡</div>
              ) : (
                <Tooltip
                  title={
                    <span>
                      尚未开启 {name} 均衡策略，
                      <Link to={`/cluster/${clusterId}/cluster/balance`}>前往开启</Link>
                    </span>
                  }
                >
                  <div className="tag unbalanced">{name} 未均衡</div>
                </Tooltip>
              );
            })}
        </div>
        <div className="desc">{renderToolTipValue(clusterInfo?.description, 35)}</div>
        <div className="card-panel">
          <div className="card-item" onClick={() => history.push(`/cluster/${clusterId}/broker`)}>
            <div className="title">Brokers总数</div>
            <div className="count">
              <span className="num">{brokerState?.brokerCount ?? '-'}</span>
              <span className="unit">个</span>
            </div>
            <div className="metric">
              <span className="type">Controller</span>
              {renderIcon(brokerState?.kafkaControllerAlive ? 'green' : 'red')}
            </div>
            <div className="metric">
              <span className="type">Similar Config</span>
              {renderIcon(brokerState?.configSimilar ? 'green' : 'warning')}
            </div>
          </div>
          <div className="card-item" onClick={() => history.push(`/cluster/${clusterId}/topic`)}>
            <div className="title">Topics总数</div>
            <div className="count">
              <span className="num">{clusterMetrics?.Topics ?? '-'}</span>
              <span className="unit">个</span>
            </div>
            <div className="metric">
              <span className="type">No leader</span>
              {renderIcon(clusterMetrics?.PartitionNoLeader === 0 ? 'green' : 'red')}
            </div>
            <div className="metric">
              <span className="type">{'< Min ISR'}</span>
              {renderIcon(clusterMetrics?.PartitionMinISR_S === 0 ? 'green' : 'red')}
            </div>
            <div className="metric">
              <span className="type">URP</span>
              {renderIcon(clusterMetrics?.PartitionURP === 0 ? 'green' : 'red')}
            </div>
          </div>
          <div className="card-item" onClick={() => history.push(`/cluster/${clusterId}/consumers`)}>
            <div className="title">ConsumerGroup总数</div>
            <div className="count">
              <span className="num">{clusterMetrics?.Groups ?? '-'}</span>
              <span className="unit">个</span>
            </div>
            <div className="metric">
              <span className="type">Dead</span>
              {renderIcon(clusterMetrics?.GroupDeads === 0 ? 'green' : 'red')}
            </div>
          </div>
        </div>
      </div>
      <AccessClusters
        visible={visible}
        setVisible={setVisible}
        title={'edit.cluster'}
        infoLoading={loading}
        afterSubmitSuccess={getPhyClusterInfo}
        clusterInfo={clusterInfo}
        kafkaVersion={Object.keys(kafkaVersion)}
      />
      <HealthySetting ref={healthyDrawerRef} />
      <CheckDetail ref={detailDrawerRef} />
    </>
  );
};

export default LeftSider;
