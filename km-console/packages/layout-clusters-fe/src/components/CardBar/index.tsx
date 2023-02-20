import React, { useEffect, useState } from 'react';
import { useParams } from 'react-router-dom';
import { Drawer, Spin, Table, Utils } from 'knowdesign';
import { IconFont } from '@knowdesign/icons';
import './index.less';
import api from '@src/api';
import moment from 'moment';
import TagsWithHide from '../TagsWithHide/index';
import HealthState, { getHealthStateDesc, getHealthStateEmoji, HealthStateEnum } from '../HealthState';
import { getConfigItemDetailDesc } from '@src/pages/SingleClusterDetail/config';

export interface healthDataProps {
  state: HealthStateEnum;
  passed: number;
  total: number;
}
export interface CardBarProps {
  cardColumns?: any[];
  healthData?: healthDataProps;
  showCardBg?: boolean;
  scene: 'topics' | 'brokers' | 'topic' | 'broker' | 'group' | 'zookeeper' | 'connect' | 'connector' | 'mm2';
  record?: any;
  loading?: boolean;
  needProgress?: boolean;
}
const renderValue = (v: string | number | ((visibleType?: boolean) => JSX.Element), visibleType?: boolean) => {
  return typeof v === 'function' ? v(visibleType) : v;
};
const sceneCodeMap = {
  brokers: {
    code: 1,
    fieldName: 'brokerId',
    alias: 'Brokers',
  },
  broker: {
    code: 1,
    fieldName: 'brokerId',
    alias: 'Broker',
  },
  topics: {
    code: 2,
    fieldName: 'topicName',
    alias: 'Topics',
  },
  topic: {
    code: 2,
    fieldName: 'topicName',
    alias: 'Topic',
  },
  group: {
    code: 3,
    fieldName: 'groupName',
    alias: 'Consumers',
  },
  zookeeper: {
    code: 4,
    fieldName: 'zookeeperId',
    alias: 'Zookeeper',
  },
  connect: {
    code: 5,
    fieldName: 'connectClusterId',
    alias: 'Connect',
  },
  connector: {
    code: 6,
    fieldName: 'connectorName',
    alias: 'Connector',
  },
  mm2: {
    code: 7,
    fieldName: 'connectorName',
    alias: 'MM2',
  },
};
const CardColumnsItem: any = (cardItem: any) => {
  const { cardColumnsItemData, showCardBg } = cardItem;
  const [visibleType, setVisibleType] = useState(false);
  return (
    <div
      onMouseEnter={() => setVisibleType(true)}
      onMouseLeave={() => setVisibleType(false)}
      className={`card-bar-colunms ${cardColumnsItemData.className}`}
      style={{ backgroundColor: showCardBg ? 'rgba(86, 110, 230,0.04)' : 'transparent', ...cardColumnsItemData?.customStyle }}
    >
      <div className="card-bar-colunms-header">
        <span>{cardColumnsItemData.icon}</span>
        <span>{renderValue(cardColumnsItemData.title)}</span>
      </div>
      <div className={`card-bar-colunms-body ${cardColumnsItemData?.valueClassName}`}>
        <div style={{ marginRight: 12 }} className="num">
          {cardColumnsItemData.value === '-' ? (
            <div style={{ fontSize: 20 }}>-</div>
          ) : renderValue(cardColumnsItemData.value) !== undefined ? (
            renderValue(cardColumnsItemData.value, visibleType)
          ) : (
            <div style={{ fontSize: 20 }}>-</div>
          )}
        </div>
      </div>
    </div>
  );
};
const CardBar = (props: CardBarProps) => {
  const routeParams = useParams<{
    clusterId: string;
  }>();
  const { healthData, cardColumns, showCardBg = true, scene, record, loading, needProgress = true } = props;
  const [detailDrawerVisible, setDetailDrawerVisible] = useState(false);
  const [healthCheckDetailList, setHealthCheckDetailList] = useState([]);

  useEffect(() => {
    const sceneObj = sceneCodeMap[scene];
    const path = record
      ? api.getResourceHealthDetail(
          scene === 'connector' || scene === 'mm2' ? Number(record?.connectClusterId) : Number(routeParams.clusterId),
          sceneObj.code,
          record[sceneObj.fieldName]
        )
      : api.getResourceListHealthDetail(Number(routeParams.clusterId));
    const promise = record
      ? Utils.request(path)
      : Utils.request(path, {
          method: 'POST',
          data: scene === 'connect' ? JSON.parse(JSON.stringify([5, 6])) : JSON.parse(JSON.stringify([sceneObj.code])),
        });
    promise.then((data: any[]) => {
      setHealthCheckDetailList(data);
    });
  }, []);
  const columns = [
    {
      title: '检查项',
      dataIndex: 'checkConfig',
      width: '40%',
      render(config: any, record: any) {
        let valueGroup = {};
        try {
          valueGroup = JSON.parse(config.value);
        } catch (e) {
          //
        }
        return (
          getConfigItemDetailDesc(record.configItem, valueGroup) ||
          getConfigItemDetailDesc(config.configItem, valueGroup) ||
          record.configDesc ||
          '-'
        );
      },
    },
    // {
    //   title: '得分',
    //   dataIndex: 'score',
    // },
    {
      title: '检查时间',
      dataIndex: 'updateTime',
      width: '30%',
      render: (value: number) => {
        return value ? moment(value).format('YYYY-MM-DD HH:mm:ss') : '-';
      },
    },
    {
      title: '检查结果',
      dataIndex: 'passed',
      width: '30%',
      render(value: boolean, record: any) {
        if (record?.updateTime) {
          const icon = value ? <IconFont type="icon-zhengchang"></IconFont> : <IconFont type="icon-yichang"></IconFont>;
          const txt = value ? '已通过' : '未通过';
          const notPassedResNameList = record.notPassedResNameList || [];
          return (
            <div style={{ display: 'flex', width: 240 }}>
              <div style={{ marginRight: 6 }}>
                {icon} {txt}
              </div>
              {<TagsWithHide list={notPassedResNameList} expandTagContent="更多" />}
            </div>
          );
        } else {
          return '-';
        }
      },
    },
  ];
  return (
    <Spin spinning={loading}>
      <div className="card-bar-container">
        <div className="card-bar-content">
          {healthData && needProgress && (
            <div className="card-bar-health">
              <div className="card-bar-health-process">
                <HealthState state={healthData?.state} width={74} height={74} />
              </div>
              <div>
                <div className="state">
                  {getHealthStateEmoji(healthData?.state)}
                  &nbsp;{sceneCodeMap[scene].alias}
                  {getHealthStateDesc(healthData?.state)}
                </div>
                <div className="value-bar">
                  <div className="value">{`${healthData?.passed}/${healthData?.total}`}</div>
                  <div className="check-detail" onClick={(_) => setDetailDrawerVisible(true)}>
                    查看详情
                  </div>
                </div>
              </div>
            </div>
          )}
          {cardColumns &&
            cardColumns?.length != 0 &&
            cardColumns?.map((item: any, index: any) => {
              return <CardColumnsItem key={index} cardColumnsItemData={item} showCardBg={showCardBg}></CardColumnsItem>;
            })}
        </div>
      </div>
      <Drawer
        className="health-check-res-drawer"
        maskClosable={false}
        title={`${sceneCodeMap[scene].alias}健康状态详情`}
        placement="right"
        width={1080}
        onClose={(_) => setDetailDrawerVisible(false)}
        visible={detailDrawerVisible}
      >
        <Table rowKey={'configName'} columns={columns} dataSource={healthCheckDetailList} pagination={false} />
      </Drawer>
    </Spin>
  );
};
export default CardBar;
