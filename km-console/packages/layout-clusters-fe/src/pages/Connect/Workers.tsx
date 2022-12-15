import React, { useState, useEffect, memo } from 'react';
import { useParams, useHistory, useLocation } from 'react-router-dom';
import { ProTable, Button, Utils, AppContainer, SearchInput } from 'knowdesign';
import { IconFont } from '@knowdesign/icons';
import API from '../../api';
import { getWorkersColumns, defaultPagination } from './config';
import { tableHeaderPrefix } from '@src/constants/common';
import ConnectCard from '@src/components/CardBar/ConnectCard';
import DBreadcrumb from 'knowdesign/es/extend/d-breadcrumb';
import './index.less';
import HasConnector from './HasConnector';
const { request } = Utils;

const Workers: React.FC = () => {
  const [global] = AppContainer.useGlobalValue();
  const [loading, setLoading] = useState(false);
  const [data, setData] = useState([]);
  const [searchKeywords, setSearchKeywords] = useState('');
  const [pagination, setPagination] = useState<any>(defaultPagination);

  // 请求接口获取数据
  const genData = async ({ pageNo, pageSize, filters, sorter }: any) => {
    if (global?.clusterInfo?.id === undefined) return;

    setLoading(true);
    const params = {
      searchKeywords: searchKeywords.slice(0, 128),
      pageNo,
      pageSize,
    };

    request(API.getWorkersList(global?.clusterInfo?.id), { params })
      .then((res: any) => {
        setPagination({
          current: res.pagination?.pageNo,
          pageSize: res.pagination?.pageSize,
          total: res.pagination?.total,
        });
        setData(res?.bizData || []);
        setLoading(false);
      })
      .catch((err) => {
        setLoading(false);
      });
  };

  const onTableChange = (pagination: any, filters: any, sorter: any) => {
    genData({ pageNo: pagination.current, pageSize: pagination.pageSize, filters, sorter });
  };

  useEffect(() => {
    genData({
      pageNo: 1,
      pageSize: pagination.pageSize,
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
            { label: 'Workers', aHref: `` },
          ]}
        />
      </div>
      <HasConnector>
        <>
          <div style={{ margin: '12px 0' }}>
            <ConnectCard />
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
                    placeholder: '请输入Host',
                    style: { width: '248px', borderRiadus: '8px' },
                    maxLength: 128,
                  }}
                />
              </div>
            </div>
            <ProTable
              key="workers-table"
              showQueryForm={false}
              tableProps={{
                showHeader: false,
                rowKey: 'workers_list',
                loading: loading,
                columns: getWorkersColumns(),
                dataSource: data,
                paginationProps: { ...pagination },
                attrs: {
                  onChange: onTableChange,
                  scroll: { y: 'calc(100vh - 400px)' },
                  bordered: false,
                },
              }}
            />
          </div>
        </>
      </HasConnector>
    </>
  );
};

export default Workers;
