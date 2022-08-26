import React, { useEffect, useState } from 'react';
import { useParams } from 'react-router-dom';
import { Drawer, IconFont, Select, Spin, Table } from 'knowdesign';
import { Utils, Progress } from 'knowdesign';
import './index.less';
import api from '@src/api';
import moment from 'moment';
import TagsWithHide from '../TagsWithHide/index';
import { getHealthProcessColor } from '@src/pages/SingleClusterDetail/config';

export interface healthDataProps {
  score: number;
  passed: number;
  total: number;
  alive: number;
}
export interface CardBarProps {
  cardColumns?: any[];
  healthData?: healthDataProps;
  showCardBg?: boolean;
  scene: 'topic' | 'broker' | 'group';
  record?: any;
  loading?: boolean;
  needProgress?: boolean;
}
const renderValue = (v: string | number | ((visibleType?: boolean) => JSX.Element), visibleType?: boolean) => {
  return typeof v === 'function' ? v(visibleType) : v;
};
const statusTxtEmojiMap = {
  success: {
    emoji: '👍',
    txt: '优异',
  },
  normal: {
    emoji: '😊',
    txt: '正常',
  },
  exception: {
    emoji: '👻',
    txt: '异常',
  },
};
const sceneCodeMap = {
  topic: {
    code: 2,
    fieldName: 'topicName',
    alias: 'Topics',
  },
  broker: {
    code: 1,
    fieldName: 'brokerId',
    alias: 'Brokers',
  },
  group: {
    code: 3,
    fieldName: 'groupName',
    alias: 'Consumers',
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
  const [progressStatus, setProgressStatus] = useState<'success' | 'exception' | 'normal'>('success');
  const [healthCheckDetailList, setHealthCheckDetailList] = useState([]);
  const [isAlive, setIsAlive] = useState(true);

  useEffect(() => {
    if (healthData) {
      setProgressStatus(!isAlive ? 'exception' : healthData.score >= 90 ? 'success' : 'normal');
      setIsAlive(healthData.alive === 1);
    }
  }, [healthData, isAlive]);

  useEffect(() => {
    const sceneObj = sceneCodeMap[scene];
    const path = record
      ? api.getResourceHealthDetail(Number(routeParams.clusterId), sceneObj.code, record[sceneObj.fieldName])
      : api.getResourceListHealthDetail(Number(routeParams.clusterId));
    const promise = record
      ? Utils.request(path)
      : Utils.request(path, {
        params: { dimensionCode: sceneObj.code },
      });
    promise.then((data: any[]) => {
      setHealthCheckDetailList(data);
    });
  }, []);
  const columns = [
    {
      title: '检查项',
      dataIndex: 'configDesc',
      key: 'configDesc',
    },
    {
      title: '权重',
      dataIndex: 'weightPercent',
      key: 'weightPercent',
    },
    {
      title: '得分',
      dataIndex: 'score',
      key: 'score',
    },
    {
      title: '检查时间',
      dataIndex: 'updateTime',
      key: 'updateTime',
      render: (value: number) => {
        return moment(value).format('YYYY-MM-DD hh:mm:ss');
      },
    },
    {
      title: '检查结果',
      dataIndex: 'passed',
      key: 'passed',
      width: 280,
      render(value: boolean, record: any) {
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
      },
    },
  ];
  return (
    <Spin spinning={loading}>
      <div className="card-bar-container">
        <div className="card-bar-content">
          {!loading && healthData && needProgress && (
            <div className="card-bar-health">
              <div className="card-bar-health-process">
                <Progress
                  width={70}
                  type="circle"
                  percent={!isAlive ? 100 : healthData.score}
                  status={progressStatus}
                  format={(percent, successPercent) => {
                    return !isAlive ? (
                      <div
                        style={{
                          fontFamily: 'HelveticaNeue-Medium',
                          fontSize: 22,
                          color: getHealthProcessColor(healthData.score, healthData.alive),
                        }}
                      >
                        Down
                      </div>
                    ) : (
                      <div
                        style={{
                          textIndent: Math.round(percent) >= 100 ? '-4px' : '',
                          color: getHealthProcessColor(healthData.score, healthData.alive),
                        }}
                      >
                        {Math.round(percent)}
                      </div>
                    );
                  }}
                  strokeWidth={3}
                />
              </div>
              <div>
                <div className="state">
                  <div className={`health-status-image health-status-image-${progressStatus}`}></div>
                  &nbsp;{sceneCodeMap[scene].alias}状态{statusTxtEmojiMap[progressStatus].txt}
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
        <Table rowKey={'topicName'} columns={columns} dataSource={healthCheckDetailList} pagination={false} />
      </Drawer>
    </Spin>
  );
};
export default CardBar;
