import React, { useState, useEffect } from 'react';
import { useParams, useHistory, useLocation } from 'react-router-dom';
import { Tabs, Drawer, Tag, Utils, AppContainer, SearchInput, Empty } from 'knowdesign';
import Api from '@src/api';
import { hashDataParse } from '../../constants/common';
import DataLogs from './DataLogs';
import Configuration from './Configuration';
import BrokerDetailHealthCheck from '@src/components/CardBar/BrokerDetailHealthCheck';

import './index.less';
import { ControlStatusMap } from '../CommonRoute';
const { TabPane } = Tabs;

const OperationsSlot: any = {
  // eslint-disable-next-line react/display-name
  ['Configuration']: (arg: any) => {
    return (
      <SearchInput
        onSearch={arg.setSearchKeywords}
        attrs={{
          value: arg.searchValue,
          onChange: arg.setSearchValue,
          placeholder: '请输入配置名',
          size: 'small',
          style: { width: '210px', marginRight: '2px' },
          maxLength: 128,
        }}
      />
    );
  },
  // eslint-disable-next-line react/display-name
  ['DataLogs']: (arg: any) => {
    return (
      <SearchInput
        onSearch={arg.setSearchKeywords}
        attrs={{
          value: arg.searchValue,
          onChange: arg.setSearchValue,
          placeholder: '请输入TopicName',
          size: 'small',
          style: { width: '210px', marginRight: '2px' },
          maxLength: 128,
        }}
      />
    );
  },
};

const BrokerDetail = (props: any) => {
  const [global] = AppContainer.useGlobalValue();
  const urlParams = useParams<{ clusterId: string; brokerId: string }>();
  const urlLocation = useLocation<any>();
  const history = useHistory();
  const [positionType, setPositionType] = useState<string>('Configuration');
  const [searchKeywords, setSearchKeywords] = useState<string>('');
  const [searchValue, setSearchValue] = useState<string>('');
  const [visible, setVisible] = useState(false);
  const [hashData, setHashData] = useState<any>({});
  const callback = (key: any) => {
    setSearchValue('');
    setSearchKeywords('');
    setPositionType(key);
  };

  const onClose = () => {
    setVisible(false);
    setSearchValue('');
    setSearchKeywords('');
    setPositionType('Configuration');
    // clean hash
    history.push(urlLocation.pathname);
  };

  useEffect(() => {
    global?.clusterInfo?.id && hashDataParse(urlLocation.hash).brokerId
      ? Utils.request(Api.getBrokerMetadata(hashDataParse(urlLocation.hash).brokerId, global?.clusterInfo?.id), {
          init: {
            errorNoTips: true,
          },
        })
          .then((brokerData: any) => {
            if (brokerData?.exist && brokerData?.alive) {
              setHashData(brokerData);
              setVisible(true);
            } else {
              history.replace(urlLocation.pathname);
              setVisible(false);
            }
          })
          .catch((err) => {
            history.replace(urlLocation.pathname);
            setVisible(false);
          })
      : setVisible(false);
  }, [hashDataParse(urlLocation.hash).brokerId, global?.clusterInfo, urlLocation]);

  return (
    <Drawer
      push={false}
      title={
        <span>
          <span style={{ fontSize: '18px', fontFamily: 'PingFangSC-Semibold', color: '#495057' }}>Broker {hashData?.brokerId}</span>
          {hashData?.host && (
            <Tag
              style={{
                fontSize: '10px',
                color: '#495057',
                textAlign: 'center',
                background: '#ECECF6',
                borderRadius: '4px',
                marginLeft: '10px',
                padding: '1px 10px',
              }}
            >
              {hashData?.host}
            </Tag>
          )}
          {global?.clusterInfo?.name && (
            <Tag
              style={{
                fontSize: '10px',
                color: '#495057',
                textAlign: 'center',
                background: '#ECECF6',
                borderRadius: '4px',
                marginLeft: '10px',
                padding: '1px 10px',
              }}
            >
              {global?.clusterInfo?.name}
            </Tag>
          )}
        </span>
      }
      width={1080}
      placement="right"
      onClose={onClose}
      visible={visible}
      className="broker-detail-drawer"
      destroyOnClose
      maskClosable={false}
    >
      <BrokerDetailHealthCheck record={{ brokerId: hashData?.brokerId }} />
      {hashData && positionType && (
        <Tabs
          className={'custom_tabs_class'}
          defaultActiveKey="Configuration"
          // activeKey={positionType}
          onChange={callback}
          tabBarExtraContent={
            OperationsSlot[positionType] &&
            global.isShowControl &&
            global.isShowControl(
              positionType === 'Configuration' ? ControlStatusMap.BROKER_DETAIL_CONFIG : ControlStatusMap.BROKER_DETAIL_DATALOGS
            ) &&
            OperationsSlot[positionType]({ setSearchKeywords, setSearchValue, searchValue, positionType })
          }
          destroyInactiveTabPane
        >
          <TabPane tab="Configuration" key="Configuration">
            {global.isShowControl && global.isShowControl(ControlStatusMap.BROKER_DETAIL_CONFIG) ? (
              <Configuration searchKeywords={searchKeywords} positionType={positionType} hashData={hashData} />
            ) : (
              <Empty description="当前版本过低，不支持该功能！" />
            )}
          </TabPane>
          <TabPane tab="DataLogs" key="DataLogs">
            {global.isShowControl && global.isShowControl(ControlStatusMap.BROKER_DETAIL_DATALOGS) ? (
              <DataLogs searchKeywords={searchKeywords} positionType={positionType} hashData={hashData} />
            ) : (
              <Empty description="当前版本过低，不支持该功能！" />
            )}
          </TabPane>
        </Tabs>
      )}
    </Drawer>
  );
};

export default BrokerDetail;
