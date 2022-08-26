import React, { useState, useEffect } from 'react';
import { ProTable, Utils } from 'knowdesign';
import Api from '@src/api';
import { useParams } from 'react-router-dom';
// import { dealTableRequestParams } from '../../constants/common';
import { getTopicACLsColmns } from './config';
const { request } = Utils;

const TopicACLs = (props: any) => {
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
  // 默认排序
  const defaultSorter = {
    sortField: 'kafkaUser',
    sortType: 'asc',
  };

  // 请求接口获取数据
  const genData = async ({ pageNo, pageSize, filters = null, sorter = null }: any) => {
    if (urlParams?.clusterId === undefined || hashData?.topicName === undefined) return;

    // filters = filters || filteredInfo;

    setLoading(true);
    const params = {
      searchKeywords: props.searchKeywords ? props.searchKeywords.slice(0, 128) : '',
      pageNo,
      pageSize,
      ...defaultSorter,
    };

    request(Api.getTopicACLsList(hashData?.topicName, urlParams?.clusterId), { params })
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
    props.positionType === 'ACLs' &&
      genData({
        pageNo: 1,
        pageSize: pagination.pageSize,
        // sorter: defaultSorter
      });
  }, [props.searchKeywords, props.positionType]);

  return (
    <ProTable
      showQueryForm={false}
      tableProps={{
        showHeader: false,
        rowKey: 'path',
        loading: loading,
        columns: getTopicACLsColmns(),
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

export default TopicACLs;
