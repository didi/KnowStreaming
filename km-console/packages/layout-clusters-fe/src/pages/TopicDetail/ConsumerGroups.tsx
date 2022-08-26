import React, { useState, useEffect } from 'react';
import { ProTable, Utils } from 'knowdesign';
import Api from '@src/api';
const { request } = Utils;
const getColmns = (solveClick: any) => {
  const columns = [
    {
      title: 'ConsumerGroup',
      dataIndex: 'ConsumerGroup',
      key: 'ConsumerGroup',
    },
    {
      title: '关联KafkaUser',
      dataIndex: 'kafkaUser',
      key: 'kafkaUser',
    },
    {
      title: 'Status',
      dataIndex: 'status',
      key: 'status',
    },
    {
      title: 'Max Lag',
      dataIndex: 'maxLag',
      key: 'maxLag',
    },
    {
      title: 'Member数',
      dataIndex: 'member',
      key: 'member',
    },
    {
      title: '操作',
      dataIndex: 'option',
      key: 'option',
      // eslint-disable-next-line react/display-name
      render: (_t: any, r: any) => {
        return <a onClick={() => solveClick(r)}>解决</a>;
      },
    },
  ];
  return columns;
};

const TopicGroup = (props: any) => {
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
  const solveClick = (record: any) => {};

  // 请求接口获取数据
  const genData = async ({ pageNo, pageSize, filters = null, sorter = null }: any) => {
    // if (clusterId === undefined) return;

    // filters = filters || filteredInfo;

    setLoading(true);
    // const params = dealTableRequestParams({ searchKeywords, pageNo, pageSize, sorter, filters, isPhyId: true });

    const params = {
      filterKey: 'string',
      filterPartitionId: 1,
      filterValue: 'string',
      maxRecords: 100,
      pullTimeoutUnitMs: 10000,
      truncate: true,
    };

    request(Api.getTopicMessagesList('你好', 2), { params })
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
        setLoading(false);
      });
  };

  const onTableChange = (pagination: any, filters: any, sorter: any) => {
    setPagination(pagination);
    // const asc = sorter?.order && sorter?.order === 'ascend' ? true : false;
    // const sortColumn = sorter.field && toLine(sorter.field);
    // genData({ pageNo: pagination.current, pageSize: pagination.pageSize, filters, asc, sortColumn, queryTerm: searchResult, ...allParams });
  };

  // useEffect(() => {
  //   genData({
  //     pageNo: 1,
  //     pageSize: pagination.pageSize,
  //     // sorter: defaultSorter
  //   });
  // }, [props]);

  return (
    <ProTable
      showQueryForm={false}
      tableProps={{
        showHeader: false,
        rowKey: 'path',
        loading: loading,
        columns: getColmns(solveClick),
        dataSource: data,
        paginationProps: { ...pagination },
        attrs: {
          // className: 'frameless-table', // 纯无边框表格类名
          bordered: false,
          onChange: onTableChange,
        },
      }}
    />
  );
};

export default TopicGroup;
