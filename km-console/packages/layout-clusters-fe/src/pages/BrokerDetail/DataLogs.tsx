import React, { useState, useEffect } from 'react';
import { ProTable, Utils } from 'knowdesign';
import { useParams } from 'react-router-dom';
import Api from '@src/api';
import { getDataLogsColmns } from './config';
const { request } = Utils;

const BrokerDataLogs = (props: any) => {
  const { hashData } = props;
  const urlParams = useParams<any>(); // 获取地址栏参数
  const [loading, setLoading] = useState(false);
  const [data, setData] = useState([]);
  const [pagination, setPagination] = useState<any>({
    current: 1,
    pageSize: 10,
    position: 'bottomRight',
    showSizeChanger: true,
    pageSizeOptions: ['10', '20', '50', '100', '200', '500'],
    showTotal: (total: number) => `共 ${total} 条目`,
    // locale: {
    //   items_per_page: '条',
    // },
    // selectComponentClass: CustomSelect,
  });

  // 请求接口获取数据
  const genData = async ({ pageNo, pageSize }: any) => {
    if (urlParams?.clusterId === undefined || hashData?.brokerId === undefined) return;
    setData([]);
    setLoading(true);

    const params = {
      searchKeywords: props.searchKeywords ? props.searchKeywords.slice(0, 128) : '',
      pageNo,
      pageSize,
    };

    request(Api.getBrokerDataLogs(hashData?.brokerId, urlParams?.clusterId), { params })
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
    // setPagination(pagination);
    // const asc = sorter?.order && sorter?.order === 'ascend' ? true : false;
    // const sortColumn = sorter.field && toLine(sorter.field);
    genData({ pageNo: pagination.current, pageSize: pagination.pageSize });
  };

  useEffect(() => {
    genData({
      pageNo: 1,
      pageSize: pagination.pageSize,
      // sorter: defaultSorter
    });
  }, [props.searchKeywords, hashData?.brokerId]);

  return (
    <ProTable
      showQueryForm={false}
      tableProps={{
        showHeader: false,
        rowKey: 'path',
        loading: loading,
        columns: getDataLogsColmns(),
        dataSource: data,
        paginationProps: { ...pagination },
        attrs: {
          // className: 'frameless-table', // 纯无边框表格类名
          // bordered: true,   // 表格边框
          onChange: onTableChange,
          bordered: false,
        },
      }}
    />
  );
};

export default BrokerDataLogs;
