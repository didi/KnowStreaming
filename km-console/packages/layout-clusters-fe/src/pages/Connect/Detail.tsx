import React, { useState, useEffect } from 'react';
import { Drawer, Utils, AppContainer, ProTable } from 'knowdesign';
import API from '@src/api';
import ConnectDetailCard from '@src/components/CardBar/ConnectDetailCard';
import { defaultPagination, getConnectorsDetailColumns } from './config';
import notification from '@src/components/Notification';
import './index.less';

const prefix = 'connect-detail';
const { request } = Utils;
const ConnectorDetail = (props: any) => {
  const { visible, setVisible, record } = props;
  const [global] = AppContainer.useGlobalValue();
  const [loading, setLoading] = useState(false);
  const [data, setData] = useState([]);
  const [pagination, setPagination] = useState<any>(defaultPagination);
  const onClose = () => {
    setVisible(false);
    setPagination(defaultPagination);
    // clean hash
  };

  // 请求接口获取数据
  const genData = async () => {
    if (global?.clusterInfo?.id === undefined) return;

    setLoading(true);

    request(API.getConnectDetailTasks(record.connectorName, record.connectClusterId))
      .then((res: any) => {
        setData(res || []);
        setLoading(false);
      })
      .catch((err) => {
        setLoading(false);
      });
  };

  const onTableChange = (pagination: any, filters: any, sorter: any) => {
    setPagination(pagination);
  };

  const optionFn: any = (taskId: any) => {
    const params = {
      action: 'restart',
      connectClusterId: record?.connectClusterId,
      connectorName: record?.connectorName,
      taskId,
    };

    request(API.optionTasks(), { method: 'PUT', data: params }).then((res: any) => {
      if (res === null) {
        notification.success({
          message: `任务重试成功`,
        });
        genData();
      } else {
        notification.error({
          message: `任务重试失败`,
        });
      }
    });
  };

  const retryOption = Utils.useDebounce(optionFn, 500);

  useEffect(() => {
    visible && record && genData();
  }, [visible]);

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
      <ConnectDetailCard record={record} />
      <div className={`${prefix}-drawer-title`}>Tasks</div>
      <ProTable
        key="connector-detail-table"
        showQueryForm={false}
        tableProps={{
          showHeader: false,
          rowKey: 'taskId',
          loading: loading,
          columns: getConnectorsDetailColumns({ retryOption }),
          dataSource: data,
          paginationProps: { ...pagination },
          attrs: {
            onChange: onTableChange,
            // scroll: { x: 'max-content' },
            bordered: false,
          },
        }}
      />
      {/* <BrokerDetailHealthCheck record={{ brokerId: hashData?.brokerId }} /> */}
    </Drawer>
  );
};

export default ConnectorDetail;
