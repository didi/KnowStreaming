import React, { useState, useEffect } from 'react';
import { Drawer, Utils, AppContainer, ProTable, Tabs, Empty, Spin } from 'knowdesign';
import API from '@src/api';
import MirrorMakerDetailCard from '@src/components/CardBar/MirrorMakerDetailCard';
import { defaultPagination, getMM2DetailColumns } from './config';
import notification from '@src/components/Notification';
import './index.less';
const { TabPane } = Tabs;
const prefix = 'mm2-detail';
const { request } = Utils;

const DetailTable = ({ loading, retryOption, data }: { loading: boolean; retryOption: any; data: any[] }) => {
  const [pagination, setPagination] = useState<any>(defaultPagination);
  const onTableChange = (pagination: any, filters: any, sorter: any) => {
    setPagination(pagination);
  };
  return (
    <Spin spinning={loading}>
      {data.length ? (
        <ProTable
          key="mm2-detail-table"
          showQueryForm={false}
          tableProps={{
            showHeader: false,
            rowKey: 'taskId',
            // loading: loading,
            columns: getMM2DetailColumns({ retryOption }),
            dataSource: data,
            paginationProps: { ...pagination },
            attrs: {
              onChange: onTableChange,
              // scroll: { x: 'max-content' },
              bordered: false,
            },
          }}
        />
      ) : (
        <Empty description="暂无数据" image={Empty.PRESENTED_IMAGE_CUSTOM} style={{ padding: '100px 0' }} />
      )}
    </Spin>
  );
};

const MM2Detail = (props: any) => {
  const { visible, setVisible, record } = props;
  const [global] = AppContainer.useGlobalValue();
  const [loading, setLoading] = useState(false);
  const [data, setData] = useState([]);

  const [tabSelectType, setTabSelectType] = useState<string>('MirrorSource');
  const onClose = () => {
    setVisible(false);
    setTabSelectType('MirrorSource');
    // setPagination(defaultPagination);
    // clean hash
  };
  const callback = (key: any) => {
    setTabSelectType(key);
  };

  const genData: any = {
    MirrorSource: async () => {
      if (global?.clusterInfo?.id === undefined) return;
      setData([]);
      setLoading(true);
      if (record.connectorName) {
        request(API.getConnectDetailTasks(record.connectorName, record.connectClusterId))
          .then((res: any) => {
            setData(res || []);
            setLoading(false);
          })
          .catch((err) => {
            setLoading(false);
          });
      } else {
        setLoading(false);
      }
    },
    MirrorCheckpoint: async () => {
      if (global?.clusterInfo?.id === undefined) return;
      setData([]);
      setLoading(true);
      if (record.checkpointConnector) {
        request(API.getConnectDetailTasks(record.checkpointConnector, record.connectClusterId))
          .then((res: any) => {
            setData(res || []);
            setLoading(false);
          })
          .catch((err) => {
            setLoading(false);
          });
      } else {
        setLoading(false);
      }
    },
    MirrorHeatbeat: async () => {
      if (global?.clusterInfo?.id === undefined) return;
      setData([]);
      setLoading(true);
      if (record.heartbeatConnector) {
        request(API.getConnectDetailTasks(record.heartbeatConnector, record.connectClusterId))
          .then((res: any) => {
            setData(res || []);
            setLoading(false);
          })
          .catch((err) => {
            setLoading(false);
          });
      } else {
        setLoading(false);
      }
    },
  };

  const retryOption = (taskId: any) => {
    const params = {
      action: 'restart',
      connectClusterId: record?.connectClusterId,
      connectorName: record?.connectorName,
      taskId,
    };
    // 需要区分 tabSelectType
    request(API.optionTasks(), { method: 'PUT', data: params }).then((res: any) => {
      if (res === null) {
        notification.success({
          message: `任务重试成功`,
        });
        genData[tabSelectType]();
      } else {
        notification.error({
          message: `任务重试失败`,
        });
      }
    });
  };

  useEffect(() => {
    visible && record && genData[tabSelectType]();
  }, [visible, tabSelectType]);

  return (
    <Drawer
      // push={false}
      title={
        <span>
          <span style={{ fontSize: '18px', fontFamily: 'PingFangSC-Semibold', color: '#495057' }}>{record.connectorName ?? '-'}</span>
        </span>
      }
      width={1080}
      placement="right"
      onClose={onClose}
      visible={visible}
      className={`${prefix}-drawer`}
      destroyOnClose
      maskClosable={false}
    >
      <MirrorMakerDetailCard record={record} tabSelectType={tabSelectType} />
      <Tabs
        className={'custom_tabs_class'}
        defaultActiveKey="Configuration"
        // activeKey={tabSelectType}
        onChange={callback}
        destroyInactiveTabPane
      >
        <TabPane tab="MirrorSource" key="MirrorSource">
          <DetailTable loading={loading} retryOption={retryOption} data={data} />
          {/* {global.isShowControl && global.isShowControl(ControlStatusMap.BROKER_DETAIL_CONFIG) ? (
              <Configuration searchKeywords={searchKeywords} tabSelectType={tabSelectType} hashData={hashData} />
            ) : (
              <Empty description="当前版本过低，不支持该功能！" />
            )} */}
        </TabPane>
        <TabPane tab="MirrorCheckpoint" key="MirrorCheckpoint">
          <DetailTable loading={loading} retryOption={retryOption} data={data} />
        </TabPane>
        <TabPane tab="MirrorHeatbeat" key="MirrorHeatbeat">
          <DetailTable loading={loading} retryOption={retryOption} data={data} />
        </TabPane>
      </Tabs>
      {/* <BrokerDetailHealthCheck record={{ brokerId: hashData?.brokerId }} /> */}
    </Drawer>
  );
};

export default MM2Detail;
