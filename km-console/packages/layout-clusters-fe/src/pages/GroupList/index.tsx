import React, { useState, useEffect } from 'react';
import { ProTable, Drawer, Utils, Input } from 'knowdesign';
import API from '../../api';
import { getGroupListColumns, defaultPagination } from './config';
import { dealTableRequestParams } from '../../constants/common';

const { request } = Utils;
const ControllerChangeLogList: React.FC = (props: any) => {
  const [loading, setLoading] = useState(false);
  const [data, setData] = useState([]);
  const [searchKeywords, setSearchKeywords] = useState('');
  const [filteredInfo, setFilteredInfo] = useState(null);
  const [pagination, setPagination] = useState<any>(defaultPagination);
  // 默认排序
  // const defaultSorter = {
  //   field: 'brokerId',
  //   order: 'ascend',
  // };

  const solveClick = (record: any) => {};

  // 请求接口获取数据
  const genData = async ({ pageNo, pageSize, filters = null, sorter = null }: any) => {
    // if (clusterId === undefined) return;
    filters = filters || filteredInfo;
    setLoading(true);
    const params = dealTableRequestParams({ searchKeywords, pageNo, pageSize, sorter, filters });

    request(API.getGroupACLBindingList(2), { params })
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
        setPagination({
          current: 1,
          pageSize: 10,
          total: 20,
        });
        // mock
        setData([
          {
            aclClientHost: '127.0.0.1',
            aclOperation: 1,
            aclPermissionType: 2,
            kafkaUser: 'know-streaming',
            resourceName: 'know-streaming',
            resourcePatternType: 2,
            resourceType: 2,
          },
        ]);
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
    <>
      <ProTable
        showQueryForm={false}
        tableProps={{
          showHeader: true,
          rowKey: 'path',
          loading: loading,
          columns: getGroupListColumns(solveClick),
          dataSource: data,
          paginationProps: { ...pagination },
          tableHeaderSearchInput: {
            // 搜索配置
            submit: getSearchKeywords,
            placeholder: '请输入Broker Host',
            width: '248px',
            searchTrigger: 'enter',
          },
          attrs: {
            className: 'frameless-table', // 纯无边框表格类名
            onChange: onTableChange,
          },
        }}
      />
    </>
  );
};

export default ControllerChangeLogList;
