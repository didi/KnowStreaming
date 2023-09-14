import React, { useState, useEffect, useRef } from 'react';
import { ProTable, Dropdown, Button, Utils, AppContainer, SearchInput, Menu } from 'knowdesign';
import { IconFont } from '@knowdesign/icons';
import API from '../../api';
import { getConnectorsColumns, defaultPagination, optionType } from './config';
import { tableHeaderPrefix } from '@src/constants/common';
import ConnectCard from '@src/components/CardBar/ConnectCard';
import DBreadcrumb from 'knowdesign/es/extend/d-breadcrumb';
import AddConnector, { OperateInfo } from './AddConnector';
import ConnectorDetail from './Detail';
import notification from '@src/components/Notification';
import './index.less';
import AddConnectorUseJSON from './AddConnectorUseJSON';
import HasConnector from './HasConnector';
import { ClustersPermissionMap } from '../CommonConfig';
const { request } = Utils;

const rateMap: any = {
  readRate: ['SinkRecordReadRate', 'SourceRecordPollRate'],
  writeRate: ['SinkRecordSendRate', 'SourceRecordWriteRate'],
  recordErrors: ['TotalRecordErrors'],
};

const Connectors: React.FC = () => {
  const [global] = AppContainer.useGlobalValue();
  const [loading, setLoading] = useState(false);
  const [detailVisible, setDetailVisible] = useState(false);
  const [data, setData] = useState([]);
  const [searchKeywords, setSearchKeywords] = useState('');
  const [pagination, setPagination] = useState<any>(defaultPagination);
  const [sortInfo, setSortInfo] = useState({});
  const [detailRecord, setDetailRecord] = useState('');
  const [healthType, setHealthType] = useState(true);
  const addConnectorRef = useRef(null);
  const addConnectorJsonRef = useRef(null);

  const getRecent1DayTimeStamp = () => [Date.now() - 24 * 60 * 60 * 1000, Date.now()];
  // 请求接口获取数据
  const genData = async ({ pageNo, pageSize, filters, sorter }: any) => {
    const [startStamp, endStamp] = getRecent1DayTimeStamp();
    if (global?.clusterInfo?.id === undefined) return;
    setLoading(true);
    const params = {
      metricLines: {
        aggType: 'avg',
        endTime: endStamp,
        metricsNames: ['SourceRecordPollRate', 'SourceRecordWriteRate', 'SinkRecordReadRate', 'SinkRecordSendRate', 'TotalRecordErrors'],
        startTime: startStamp,
        topNu: 0,
      },
      searchKeywords: searchKeywords.slice(0, 128),
      pageNo,
      pageSize,
      latestMetricNames: ['SourceRecordPollRate', 'SourceRecordWriteRate', 'SinkRecordReadRate', 'SinkRecordSendRate', 'TotalRecordErrors'],
      sortType: sorter?.order ? sorter.order.substring(0, sorter.order.indexOf('end')) : 'desc',
      sortMetricNameList: rateMap[sorter?.field] || [],
    };

    request(API.getConnectorsList(global?.clusterInfo?.id), { method: 'POST', data: params })
      .then((res: any) => {
        setPagination({
          current: res.pagination?.pageNo,
          pageSize: res.pagination?.pageSize,
          total: res.pagination?.total,
        });
        const newData =
          res?.bizData.map((item: any) => {
            return {
              ...item,
              ...item?.latestMetrics?.metrics,
              key: item.connectClusterName + item.connectorName,
            };
          }) || [];
        setData(newData);
        setLoading(false);
      })
      .catch((err) => {
        setLoading(false);
      });
  };

  const onTableChange = (pagination: any, filters: any, sorter: any) => {
    setSortInfo(sorter);
    genData({ pageNo: pagination.current, pageSize: pagination.pageSize, filters, sorter });
  };

  const menu = (
    <Menu className="">
      <Menu.Item>
        <span onClick={() => addConnectorJsonRef.current?.onOpen('create')}>JSON 新增Connector</span>
      </Menu.Item>
    </Menu>
  );

  const getDetailInfo = (record: any) => {
    setDetailRecord(record);
    setDetailVisible(true);
  };

  // 编辑
  const editConnector = (detail: OperateInfo['detail']) => {
    addConnectorRef.current?.onOpen('edit', addConnectorJsonRef.current, detail);
  };

  // 重启、暂停/继续 操作
  const optionConnect = (record: any, action: string) => {
    const params = {
      action,
      connectClusterId: record?.connectClusterId,
      connectorName: record?.connectorName,
    };

    request(API.connectorsOperates, { method: 'PUT', data: params }).then((res: any) => {
      if (res === null) {
        notification.success({
          message: `任务已${optionType[params.action]}`,
          description: `任务状态更新会有至多1min延迟`,
        });
        genData({ pageNo: pagination.current, pageSize: pagination.pageSize, sorter: sortInfo });
        setHealthType(!healthType);
      } else {
        notification.error({
          message: `${optionType[params.action]}任务失败`,
        });
      }
    });
  };

  // 删除任务
  const deleteTesk = () => {
    genData({ pageNo: 1, pageSize: pagination.pageSize });
  };

  useEffect(() => {
    genData({
      pageNo: 1,
      pageSize: pagination.pageSize,
      sorter: sortInfo,
    });
  }, [searchKeywords]);

  return (
    <>
      <div className="breadcrumb" style={{ marginBottom: '10px' }}>
        <DBreadcrumb
          breadcrumbs={[
            { label: '多集群管理', aHref: '/' },
            { label: global?.clusterInfo?.name, aHref: `/cluster/${global?.clusterInfo?.id}` },
            { label: 'Connect', aHref: `/cluster/${global?.clusterInfo?.id}/connect` },
            { label: 'Connectors', aHref: `` },
          ]}
        />
      </div>
      <HasConnector>
        <>
          <div style={{ margin: '12px 0' }}>
            <ConnectCard state={healthType} />
          </div>
          <div className="custom-table-content">
            <div className={tableHeaderPrefix}>
              <div className={`${tableHeaderPrefix}-left`}>
                <div
                  className={`${tableHeaderPrefix}-left-refresh`}
                  onClick={() => genData({ pageNo: pagination.current, pageSize: pagination.pageSize })}
                >
                  <IconFont className={`${tableHeaderPrefix}-left-refresh-icon`} type="icon-shuaxin1" />
                </div>
              </div>
              <div className={`${tableHeaderPrefix}-right`}>
                <SearchInput
                  onSearch={setSearchKeywords}
                  attrs={{
                    placeholder: '请输入Connector',
                    style: { width: '248px', borderRiadus: '8px' },
                    maxLength: 128,
                  }}
                />
                {global.hasPermission && global.hasPermission(ClustersPermissionMap.CONNECTOR_ADD) ? (
                  <span className="add-connect">
                    <Button
                      className="add-connect-btn"
                      icon={<IconFont type="icon-jiahao" />}
                      type="primary"
                      onClick={() => addConnectorRef.current?.onOpen('create', addConnectorJsonRef.current)}
                    >
                      新增Connector
                    </Button>
                    <Dropdown overlayClassName="add-connect-dropdown-menu" overlay={menu}>
                      <Button className="add-connect-json" type="primary">
                        <IconFont type="icon-guanwangxiala" />
                      </Button>
                    </Dropdown>
                  </span>
                ) : (
                  <></>
                )}
              </div>
            </div>
            <ProTable
              key="connector-table"
              showQueryForm={false}
              tableProps={{
                showHeader: false,
                rowKey: 'key',
                loading: loading,
                columns: getConnectorsColumns({ getDetailInfo, deleteTesk, optionConnect, editConnector }),
                dataSource: data,
                paginationProps: { ...pagination },
                attrs: {
                  onChange: onTableChange,
                  scroll: { x: 'max-content', y: 'calc(100vh - 400px)' },
                  bordered: false,
                },
              }}
            />
          </div>
        </>
      </HasConnector>

      <ConnectorDetail visible={detailVisible} setVisible={setDetailVisible} record={detailRecord} />
      <AddConnector
        ref={addConnectorRef}
        refresh={() => genData({ pageNo: pagination.current, pageSize: pagination.pageSize, sorter: sortInfo })}
      />
      <AddConnectorUseJSON
        ref={addConnectorJsonRef}
        refresh={() => genData({ pageNo: pagination.current, pageSize: pagination.pageSize, sorter: sortInfo })}
      />
    </>
  );
};

export default Connectors;
