import React, { useState, useEffect } from 'react';
import { useParams } from 'react-router-dom';
import CardBar from './index';
import { Tag, Utils, Tooltip, Popover, AppContainer } from 'knowdesign';
import { IconFont } from '@knowdesign/icons';
import api from '@src/api';
import StateChart from './StateChart';
import ClusterNorms from '@src/pages/LoadRebalance/ClusterNorms';
import { QuestionCircleOutlined } from '@ant-design/icons';
import moment from 'moment';
import { ClustersPermissionMap } from '@src/pages/CommonConfig';

const transUnitTimePro = (ms: number, num = 0) => {
  if (!ms) return '';
  if (ms < 60000) {
    return { value: 0, unit: `分钟` };
  }
  if (ms >= 60000 && ms < 3600000) {
    return { value: (ms / 1000 / 60).toFixed(num), unit: `分钟` };
  }
  if (ms >= 3600000 && ms < 86400000) {
    return { value: (ms / 1000 / 60 / 60).toFixed(num), unit: `小时` };
  }
  return { value: (ms / 1000 / 60 / 60 / 24).toFixed(num), unit: `天` };
};

const LoadRebalanceCardBar = (props: any) => {
  const [global] = AppContainer.useGlobalValue();
  const { clusterId } = useParams<{
    clusterId: string;
  }>();
  const [loading, setLoading] = useState(false);
  const [cardData, setCardData] = useState([]);
  const [normsVisible, setNormsVisible] = useState(null);
  const onClose = () => {
    setNormsVisible(false);
  };
  const getCartInfo = () => {
    // /api/v3/clusters/${clusterId}/balance-state /ks-km/api/v3/clusters/{clusterPhyId}/balance-state
    return Utils.request(api.getCartInfo(+clusterId));
  };
  useEffect(() => {
    setLoading(true);
    // 获取右侧状态
    getCartInfo()
      .then((res: any) => {
        const { next, sub, status } = res;
        const { cpu, disk, bytesIn, bytesOut } = sub;
        const newNextDate: any = transUnitTimePro(moment(next).valueOf() - moment().valueOf());
        const cardMap = [
          {
            title() {
              return (
                <div style={{ height: '20px' }}>
                  <span style={{ display: 'inline-block', marginRight: '8px' }}>State</span>
                  {global.hasPermission(ClustersPermissionMap.REBALANCE_SETTING) && (
                    <IconFont
                      className="cutomIcon-config"
                      style={{ fontSize: '15px' }}
                      onClick={() => setNormsVisible(true)}
                      type="icon-shezhi"
                    ></IconFont>
                  )}
                </div>
              );
            },
            value() {
              return (
                <div style={{ display: 'inline-block', width: '100%' }}>
                  <div style={{ margin: '3px 0 8px' }}>
                    <Tag
                      style={{
                        padding: '2px 4px',
                        backgroundColor: !status ? 'rgba(85,110,230,0.10)' : '#fff3e4',
                        color: !status ? '#556EE6' : '#F58342',
                      }}
                    >
                      {!status ? '已均衡' : '未均衡'}
                    </Tag>
                  </div>
                  <div style={{ display: 'flex', justifyContent: 'space-between' }}>
                    <span>
                      周期均衡 <IconFont className="cutomIcon" type={`${!status ? 'icon-zhengchang' : 'icon-warning'}`} />
                    </span>
                    <span>
                      距下次均衡还剩{newNextDate?.value || 0}
                      {newNextDate?.unit || '分钟'}
                    </span>
                  </div>
                </div>
              );
            },
            className: '',
            valueClassName: 'custom-card-bar-value', // cardbar value类名
            customStyle: {
              // 自定义cardbar样式
              marginLeft: 0,
              padding: '12px 12px 8px 12px',
            },
          },
          {
            title() {
              return (
                <div>
                  <span style={{ display: 'inline-block', marginRight: '8px' }}>Disk AVG</span>
                  {!disk?.interval && disk?.interval !== 0 && (
                    <Tooltip overlayClassName="rebalance-tooltip" title="未设置均衡策略">
                      <QuestionCircleOutlined />
                    </Tooltip>
                  )}
                </div>
              );
            },
            value(visibleType: boolean) {
              return (
                <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'flex-end' }}>
                  <div style={{ display: 'inline-block' }}>
                    <div style={{ margin: '5px 0', fontFamily: 'DIDIFD-Medium' }}>
                      <span style={{ fontSize: '24px' }}>{Utils.transBToGB(disk?.avg)}</span>
                      <span style={{ fontSize: '14px', fontFamily: 'HelveticaNeue', display: 'inline-block', marginLeft: '4px' }}>GB</span>
                    </div>
                    <div style={{ marginTop: '-4px', display: 'flex', justifyContent: 'space-between' }}>
                      <span style={{ color: '#74788D' }}>均衡区间: ±{disk?.interval || 0}%</span>
                    </div>
                  </div>
                  <Popover
                    overlayClassName="custom-popover"
                    content={
                      <div style={{ color: '#495057' }}>
                        <div>
                          <IconFont className="cutomIcon cutomIcon-red" type="icon-chaoguo" />
                          超过均衡区间的有: {disk?.bigNu || 0}
                        </div>
                        <div style={{ margin: '6px 0' }}>
                          <IconFont className="cutomIcon cutomIcon-green" type="icon-qujian" />
                          在均衡区间内的有: {disk?.betweenNu || 0}
                        </div>
                        <div>
                          <IconFont className="cutomIcon cutomIcon-red" type="icon-diyu" />
                          低于均衡区间的有: {disk?.smallNu || 0}
                        </div>
                      </div>
                    }
                    getPopupContainer={(triggerNode: any) => {
                      return triggerNode;
                    }}
                  >
                    <div style={{ width: '44px', height: '30px' }}>
                      <StateChart
                        data={[
                          { name: 'bigNu', value: disk?.bigNu || 0 },
                          { name: 'betweenNu', value: disk?.betweenNu || 0 },
                          { name: 'smallNu', value: disk?.smallNu || 0 },
                        ]}
                      />
                    </div>
                  </Popover>
                </div>
              );
            },
            className: 'custom-card-bar',
            valueClassName: 'custom-card-bar-value',
          },
          {
            title() {
              return (
                <div>
                  <span style={{ display: 'inline-block', marginRight: '8px' }}>BytesIn AVG</span>
                  {!bytesIn?.interval && bytesIn?.interval !== 0 && (
                    <Tooltip overlayClassName="rebalance-tooltip" title="未设置均衡策略">
                      <QuestionCircleOutlined />
                    </Tooltip>
                  )}
                </div>
              );
            },
            value(visibleType: boolean) {
              return (
                <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'flex-end' }}>
                  <div style={{ display: 'inline-block' }}>
                    <div style={{ margin: '5px 0', fontFamily: 'DIDIFD-Medium' }}>
                      <span style={{ fontSize: '24px' }}>{Utils.transBToMB(bytesIn?.avg || 0)}</span>
                      <span style={{ fontSize: '14px', fontFamily: 'HelveticaNeue', display: 'inline-block', marginLeft: '4px' }}>
                        MB/s
                      </span>
                    </div>
                    <div style={{ marginTop: '-4px', display: 'flex', justifyContent: 'space-between' }}>
                      <span style={{ color: '#74788D' }}>均衡区间: ±{bytesIn?.interval || 0}%</span>
                    </div>
                  </div>
                  <Popover
                    overlayClassName="custom-popover"
                    content={
                      <div style={{ color: '#495057' }}>
                        <div>
                          <IconFont className="cutomIcon cutomIcon-red" type="icon-chaoguo" />
                          超过均衡区间的有: {bytesIn?.bigNu || 0}
                        </div>
                        <div style={{ margin: '6px 0' }}>
                          <IconFont className="cutomIcon cutomIcon-green" type="icon-qujian" />
                          在均衡区间内的有: {bytesIn?.betweenNu || 0}
                        </div>
                        <div>
                          <IconFont className="cutomIcon cutomIcon-red" type="icon-diyu" />
                          低于均衡区间的有: {bytesIn?.smallNu || 0}
                        </div>
                      </div>
                    }
                    getPopupContainer={(triggerNode: any) => {
                      return triggerNode;
                    }}
                  >
                    <div style={{ width: '44px', height: '30px' }}>
                      <StateChart
                        data={[
                          { name: 'bigNu', value: bytesIn?.bigNu || 0 },
                          { name: 'betweenNu', value: bytesIn?.betweenNu || 0 },
                          { name: 'smallNu', value: bytesIn?.smallNu || 0 },
                        ]}
                      />
                    </div>
                  </Popover>
                </div>
              );
            },
            className: 'custom-card-bar',
            valueClassName: 'custom-card-bar-value',
          },
          {
            title() {
              return (
                <div>
                  <span style={{ display: 'inline-block', marginRight: '8px' }}>BytesOut AVG</span>
                  {!bytesOut?.interval && bytesOut?.interval !== 0 && (
                    <Tooltip overlayClassName="rebalance-tooltip" title="未设置均衡策略">
                      <QuestionCircleOutlined />
                    </Tooltip>
                  )}
                </div>
              );
            },
            value(visibleType: boolean) {
              return (
                <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'flex-end' }}>
                  <div style={{ display: 'inline-block' }}>
                    <div style={{ margin: '5px 0', fontFamily: 'DIDIFD-Medium' }}>
                      <span style={{ fontSize: '24px' }}>{Utils.transBToMB(bytesOut?.avg || 0)}</span>
                      <span style={{ fontSize: '14px', fontFamily: 'HelveticaNeue', display: 'inline-block', marginLeft: '4px' }}>
                        MB/s
                      </span>
                    </div>
                    <div style={{ marginTop: '-4px', display: 'flex', justifyContent: 'space-between' }}>
                      <span style={{ color: '#74788D' }}>均衡区间: ±{bytesOut?.interval || 0}%</span>
                    </div>
                  </div>
                  <Popover
                    overlayClassName="custom-popover"
                    content={
                      <div style={{ color: '#495057' }}>
                        <div>
                          <IconFont className="cutomIcon cutomIcon-red" type="icon-chaoguo" />
                          超过均衡区间的有: {bytesOut?.bigNu || 0}
                        </div>
                        <div style={{ margin: '6px 0' }}>
                          <IconFont className="cutomIcon cutomIcon-green" type="icon-qujian" />
                          在均衡区间内的有: {bytesOut?.betweenNu || 0}
                        </div>
                        <div>
                          <IconFont className="cutomIcon cutomIcon-red" type="icon-diyu" />
                          低于均衡区间的有: {bytesOut?.smallNu || 0}
                        </div>
                      </div>
                    }
                    getPopupContainer={(triggerNode: any) => {
                      return triggerNode;
                    }}
                  >
                    <div style={{ width: '44px', height: '30px' }}>
                      <StateChart
                        data={[
                          { name: 'bigNu', value: bytesOut?.bigNu || 0 },
                          { name: 'betweenNu', value: bytesOut?.betweenNu || 0 },
                          { name: 'smallNu', value: bytesOut?.smallNu || 0 },
                        ]}
                      />
                    </div>
                  </Popover>
                </div>
              );
            },
            className: 'custom-card-bar',
            valueClassName: 'custom-card-bar-value',
          },
        ];
        setCardData(cardMap);
      })
      .catch((err) => {
        const cardMap = [
          {
            title() {
              return (
                <div>
                  <span style={{ display: 'inline-block', marginRight: '17px' }}>State</span>
                  <IconFont className="cutomIcon-config" onClick={() => setNormsVisible(true)} type="icon-shezhi"></IconFont>
                </div>
              );
            },
            value: '-',
            customStyle: {
              // 自定义cardbar样式
              marginLeft: 0,
            },
          },
          {
            title: 'CPU AVG',
            value: '-',
          },
          {
            title: 'Disk AVG',
            value: '-',
          },
          {
            title: 'BytesIn AVG',
            value: '-',
          },
          {
            title: 'BytesOut AVG',
            value: '-',
          },
        ];
        setCardData(cardMap);
      })
      .finally(() => {
        setLoading(false);
      });
  }, [clusterId, props?.trigger]);
  return (
    <>
      <CardBar scene="broker" cardColumns={cardData} loading={loading}></CardBar>
      {<ClusterNorms genData={props?.genData} visible={normsVisible} onClose={onClose} />}
    </>
  );
};

export default LoadRebalanceCardBar;
