import React, { useState, useEffect } from 'react';
import { useHistory, useParams } from 'react-router-dom';
import { Tabs, Utils, Drawer, Tag, AppContainer, SearchInput } from 'knowdesign';
import notification from '@src/components/Notification';

import Api from '@src/api';
import BrokersDetail from './BrokersDetail';
import Messages from './Messages';
import ConsumerGroups from './ConsumerGroups';
import ACLs from './ACLs';
import Configuration from './Configuration';
import Consumers from './ConsumerGroups';
import Replicator from './Replicator';
// import Consumers from '@src/pages/Consumers';
import './index.less';
import TopicDetailHealthCheck from '@src/components/CardBar/TopicDetailHealthCheck';
import { hashDataParse } from '@src/constants/common';
import { useForceRefresh } from '@src/components/utils';

const { TabPane } = Tabs;

const Reload = (props: any) => {
  return (
    <span
      style={{ display: 'inline-block', padding: '0 10px', marginRight: '10px', borderRight: '1px solid #ccc', fontSize: '15px' }}
      onClick={props.forceRefresh as () => void}
    >
      <i className="iconfont icon-shuaxin1" style={{ cursor: 'pointer' }} />
    </span>
  );
};

const OperationsSlot: any = {
  // eslint-disable-next-line react/display-name
  // ['Partitions']: (arg: any) => {
  //   return (
  //     <SearchInput
  //       onSearch={arg.setSearchKeywords}
  //       attrs={{
  //         value: arg.searchValue,
  //         onChange: arg.setSearchValue,
  //         placeholder: '请输入 Broker Host',
  //         size: 'small',
  //         style: { width: '210px', marginRight: '2px' },
  //         maxLength: 128,
  //       }}
  //     />
  //   );
  // },
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
  ['ACLs']: (arg: any) => {
    return (
      <SearchInput
        onSearch={arg.setSearchKeywords}
        attrs={{
          value: arg.searchValue,
          onChange: arg.setSearchValue,
          placeholder: '请输入Principle',
          size: 'small',
          style: { width: '210px', marginRight: '2px' },
          maxLength: 128,
        }}
      />
    );
  },
  // eslint-disable-next-line react/display-name
  ['ConsumerGroups']: (arg: any) => {
    return (
      <>
        <Reload {...arg} />
        <SearchInput
          onSearch={arg.setSearchKeywords}
          attrs={{
            value: arg.searchValue,
            onChange: arg.setSearchValue,
            placeholder: '请输入Consumer Group',
            size: 'small',
            style: { width: '210px', marginRight: '2px' },
            maxLength: 128,
          }}
        />
      </>
    );
  },
};

const TopicDetail = (props: any) => {
  const [global] = AppContainer.useGlobalValue();
  const urlParams = useParams<{ clusterId: string }>();
  const history = useHistory();
  const [positionType, setPositionType] = useState<string>('Partitions');
  const [searchKeywords, setSearchKeywords] = useState<string>('');
  const [searchValue, setSearchValue] = useState<string>('');
  const [visible, setVisible] = useState(false);
  const [hashData, setHashData] = useState<any>({});
  const [refreshKey, forceRefresh] = useForceRefresh();

  const callback = (key: any) => {
    setSearchValue('');
    setSearchKeywords('');
    setPositionType(key);
  };

  const onClose = () => {
    setVisible(false);
    setPositionType('Partitions');
    history.push(`/cluster/${urlParams?.clusterId}/topic/list`);
  };

  useEffect(() => {
    setSearchValue('');
    setSearchKeywords('');
  }, [positionType]);

  // useEffect(() => {
  //   urlParams.clusterId  && setVisible(true);
  // }, [urlParams]);

  useEffect(() => {
    global?.clusterInfo?.id && hashDataParse(location.hash).topicName
      ? Utils.request(Api.getTopicMetadata(+global?.clusterInfo?.id, hashDataParse(location.hash)?.topicName), {
          init: {
            errorNoTips: true,
          },
        })
          .then((topicData: any) => {
            if (topicData?.exist && !hashDataParse(location.hash).groupName) {
              setHashData(topicData);
              setVisible(true);
            } else {
              history.replace(`/cluster/${urlParams?.clusterId}/topic/list`);
              // history.push(`/`);
              setVisible(false);
            }
          })
          .catch((err) => {
            history.replace(`/cluster/${urlParams?.clusterId}/topic/list`);
            setVisible(false);
            notification.error({
              message: '错误',
              description: 'Topic不存在或Topic名称有误',
            });
          })
      : setVisible(false);
  }, [hashDataParse(location.hash).topicName, global?.clusterInfo]);

  return (
    <Drawer
      // push={false}
      title={
        <span>
          <span style={{ fontSize: '18px', fontFamily: 'PingFangSC-Semibold', color: '#495057' }}>{hashData?.topicName}</span>
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
      className={'topic-detail-drawer'}
      destroyOnClose
      maskClosable={false}
    >
      {/* <CardBar cardColumns={stateData}></CardBar> */}
      <TopicDetailHealthCheck record={{ topicName: hashData?.topicName }}></TopicDetailHealthCheck>

      <Tabs
        className={'custom_tabs_class'}
        defaultActiveKey="Partitions"
        onChange={callback}
        tabBarExtraContent={
          OperationsSlot[positionType] &&
          OperationsSlot[positionType]({ ...props, setSearchKeywords, setSearchValue, searchValue, positionType, forceRefresh })
        }
        destroyInactiveTabPane
      >
        <TabPane tab="Partitions" key="Partitions">
          {positionType === 'Partitions' && <BrokersDetail hashData={hashData} />}
        </TabPane>
        <TabPane tab="Messages" key="Messages">
          {positionType === 'Messages' && <Messages searchKeywords={searchKeywords} positionType={positionType} hashData={hashData} />}
        </TabPane>
        <TabPane tab="ConsumerGroups" key="ConsumerGroups">
          {positionType === 'ConsumerGroups' && (
            <Consumers searchKeywords={searchKeywords} positionType={positionType} hashData={hashData} key={`${refreshKey}`} />
          )}
        </TabPane>
        <TabPane tab="ACLs" key="ACLs">
          {positionType === 'ACLs' && <ACLs searchKeywords={searchKeywords} positionType={positionType} hashData={hashData} />}
        </TabPane>
        <TabPane tab="Configuration" key="Configuration">
          {positionType === 'Configuration' && (
            <Configuration searchKeywords={searchKeywords} positionType={positionType} hashData={hashData} />
          )}
        </TabPane>
        <TabPane tab="Replicator" key="Replicator">
          {positionType === 'Replicator' && <Replicator searchKeywords={searchKeywords} positionType={positionType} hashData={hashData} />}
        </TabPane>
      </Tabs>
    </Drawer>
  );
};

export default TopicDetail;
