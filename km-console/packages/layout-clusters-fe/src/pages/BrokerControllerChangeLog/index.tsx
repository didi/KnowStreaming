import React, { useState, useEffect } from 'react';
import { useParams, useHistory, useLocation } from 'react-router-dom';
import { ProTable, Utils, AppContainer, SearchInput } from 'knowdesign';
import { IconFont } from '@knowdesign/icons';
import API from '../../api';
import { getControllerChangeLogListColumns, defaultPagination } from './config';
import BrokerDetail from '../BrokerDetail';
import BrokerHealthCheck from '@src/components/CardBar/BrokerHealthCheck';
import DBreadcrumb from 'knowdesign/es/extend/d-breadcrumb';
import './index.less';
import { tableHeaderPrefix } from '@src/constants/common';

const { request } = Utils;
const ControllerChangeLogList: React.FC = (props: any) => {
  const [global] = AppContainer.useGlobalValue();
  const urlParams = useParams<any>(); // 获取地址栏参数
  const history = useHistory();
  const [loading, setLoading] = useState(false);
  const [data, setData] = useState([]);
  const [searchKeywords, setSearchKeywords] = useState('');
  const [filteredInfo, setFilteredInfo] = useState(null);
  const [pagination, setPagination] = useState<any>(defaultPagination);
  const [clusterName, setClusterName] = useState<any>(null);
  // const [visible, setVisible] = useState(false);
  // const [record, setRecord] = useState(null); // 获取当前点击行的数据；
  // 默认排序

  const defaultSorter = {
    sortField: 'changeTime',
    sortType: 'desc',
  };

  // 请求接口获取数据
  const genData = ({ pageNo, pageSize, filters = null, sorter = null }: any) => {
    if (urlParams?.clusterId === undefined) return;
    filters = filters || filteredInfo;
    setLoading(true);
    // const params = dealTableRequestParams({ searchKeywords, pageNo, pageSize, sorter, filters });
    const params = {
      searchKeywords: searchKeywords.slice(0, 128),
      pageNo,
      pageSize,
    };

    request(API.getChangeLogList(urlParams?.clusterId), { params: { ...params, ...defaultSorter } })
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
        // mock
        setLoading(false);
      });
  };

  const onTableChange = (pagination: any, filters: any, sorter: any) => {
    setFilteredInfo(filters);
    genData({ pageNo: pagination.current, pageSize: pagination.pageSize, filters, sorter });
  };

  const getSearchKeywords = (value: string) => {
    setSearchKeywords(value);
  };

  useEffect(() => {
    genData({
      pageNo: 1,
      pageSize: pagination.pageSize,
      // sorter: defaultSorter
    });
  }, [searchKeywords]);

  return (
    <div className="controllerList">
      <div className="breadcrumb" style={{ marginBottom: '10px' }}>
        <DBreadcrumb
          breadcrumbs={[
            { label: '多集群管理', aHref: '/' },
            { label: global?.clusterInfo?.name, aHref: `/cluster/${global?.clusterInfo?.id}` },
            { label: 'Broker', aHref: `/cluster/${urlParams?.clusterId}/broker` },
            { label: 'Controller', aHref: `` },
          ]}
        />
      </div>
      <div style={{ margin: '12px 0' }}>
        <BrokerHealthCheck />
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
                placeholder: '请输入Broker Host',
                style: { width: '248px', borderRiadus: '8px' },
                maxLength: 128,
              }}
            />
          </div>
        </div>
        <ProTable
          showQueryForm={false}
          tableProps={{
            showHeader: false,
            rowKey: 'path',
            loading: loading,
            columns: getControllerChangeLogListColumns(),
            dataSource: data,
            paginationProps: { ...pagination },
            attrs: {
              onChange: onTableChange,
              bordered: false,
              scroll: { y: 'calc(100vh - 400px)' },
            },
          }}
        />
      </div>
      {<BrokerDetail />}
    </div>
  );
};

export default ControllerChangeLogList;
