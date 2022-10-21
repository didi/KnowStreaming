import React, { useState, useEffect } from 'react';
import { ProTable, Utils } from 'knowdesign';
import Api from '@src/api';
import { useParams } from 'react-router-dom';
import ConsumerGroupDetail from './ConsumerGroupDetail';
const { request } = Utils;

const getColmns = (arg: any) => {
  const baseColumns: any = [
    {
      title: 'ConsumerGroup',
      dataIndex: 'groupName',
      key: 'groupName',
      render: (v: any, r: any) => {
        return <a onClick={() => arg.getGroupInfo(v)}>{v}</a>;
      },
    },
    {
      title: '消费的Topic',
      dataIndex: 'topicName',
      key: 'topicName',
    },
    // {
    //   title: 'Principle',
    //   dataIndex: 'kafkaUser',
    //   key: 'kafkaUser',
    // },
    {
      title: 'Status',
      dataIndex: 'state',
      key: 'state',
    },
    {
      title: 'Max Lag',
      dataIndex: 'maxLag',
      key: 'maxLag',
      render: (t: number) => (t ? t.toLocaleString() : '-'),
    },
    {
      title: 'Member数',
      dataIndex: 'memberCount',
      key: 'memberCount',
      render: (t: number) => (t ? t.toLocaleString() : '-'),
    },
  ];

  return baseColumns;
};

const TopicGroup = (props: any) => {
  const { hashData } = props;
  const urlParams = useParams<any>(); // 获取地址栏参数
  const [loading, setLoading] = useState(false);
  const [data, setData] = useState([]);
  const [visible, setVisible] = useState(false);
  const [pagination, setPagination] = useState<any>({
    current: 1,
    pageSize: 10,
    position: 'bottomRight',
    showSizeChanger: true,
    pageSizeOptions: ['10', '20', '50', '100', '200', '500'],
    showTotal: (total: number) => `共 ${total} 条目`,
  });
  const [groupName, setGroupName] = useState('');

  // 请求接口获取数据
  const genData = async ({ pageNo, pageSize, filters = null, sorter = null }: any) => {
    if (urlParams?.clusterId === undefined || hashData?.topicName === undefined) return;

    setLoading(true);

    const params = {
      searchGroupName: props.searchKeywords ? props.searchKeywords.slice(0, 128) : undefined,
      pageNo,
      pageSize,
    };

    request(Api.getTopicGroupList(hashData?.topicName, urlParams?.clusterId), { params })
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

  const getGroupInfo = (groupName: string) => {
    setVisible(true);
    setGroupName(groupName);
  };

  const onTableChange = (pagination: any, filters: any, sorter: any) => {
    // setPagination(pagination);
    // const asc = sorter?.order && sorter?.order === 'ascend' ? true : false;
    // const sortColumn = sorter.field && toLine(sorter.field);
    genData({ pageNo: pagination.current, pageSize: pagination.pageSize });
  };

  useEffect(() => {
    props.positionType === 'ConsumerGroups' &&
      genData({
        pageNo: 1,
        pageSize: pagination.pageSize,
        // sorter: defaultSorter
      });
  }, [props.searchKeywords]);

  return (
    <div className="pro-table-wrap">
      <ProTable
        showQueryForm={false}
        tableProps={{
          loading,
          showHeader: false,
          rowKey: 'unique',
          columns: getColmns({ getGroupInfo }),
          dataSource: data,
          paginationProps: { ...pagination },
          attrs: {
            onChange: onTableChange,
            scroll: { y: 'calc(100vh - 400px)' },
          },
        }}
      />
      {<ConsumerGroupDetail scene={'topicDetail'} visible={visible} setVisible={setVisible} hashData={{ ...hashData, groupName }} />}
    </div>
    // <ProTable
    //   showQueryForm={false}
    //   tableProps={{
    //     showHeader: false,
    //     rowKey: 'path',
    //     loading: loading,
    //     columns: getColmns(solveClick),
    //     dataSource: data,
    //     paginationProps: { ...pagination },
    //     attrs: {
    //       // className: 'frameless-table', // 纯无边框表格类名
    //       bordered: false,
    //       onChange: onTableChange,
    //     },
    //   }}
    // />
  );
};

export default TopicGroup;
