/* eslint-disable react/display-name */
import React, { useState, useEffect } from 'react';
import { AppContainer, Divider, Tooltip, Input, ProTable, Select, Utils } from 'knowdesign';
import { IconFont } from '@knowdesign/icons';
import './index.less';
import Api from '@src/api/index';
import { getOperatingStateListParams } from './interface';
import { useParams } from 'react-router-dom';
import ConsumerGroupDetail from './ConsumerGroupDetail';
import ConsumerGroupHealthCheck from '@src/components/CardBar/ConsumerGroupHealthCheck';
import DBreadcrumb from 'knowdesign/es/extend/d-breadcrumb';
import { hashDataParse, tableHeaderPrefix } from '@src/constants/common';

const { Option } = Select;

const AutoPage = (props: any) => {
  const { scene, detailParams = { searchKeywords: '' } } = props;
  const routeParams = useParams<{
    clusterId: string;
  }>();
  const [global] = AppContainer.useGlobalValue();
  const [consumerGroupList, setConsumerGroupList] = useState([]);
  const [showMode, setShowMode] = useState('list');
  const [searchGroupName, setSearchGroupName] = useState(detailParams.searchKeywords || '');
  const [searchTopicName, setSearchTopicName] = useState('');
  const [pageIndex, setPageIndex] = useState(1);
  const [pageTotal, setPageTotal] = useState(1);
  const [pageSize, setPageSize] = useState(10);
  const [consumersListLoading, setConsumersListLoading] = useState(false);
  const clusterPhyId = Number(routeParams.clusterId);

  const searchFn = () => {
    const params: getOperatingStateListParams = {
      pageNo: 1,
      pageSize,
      fuzzySearchDTOList: [],
    };
    if (searchGroupName) {
      // params.groupName = searchGroupName;
      params.fuzzySearchDTOList.push({ fieldName: 'groupName', fieldValue: searchGroupName });
    }
    if (searchTopicName) {
      params.fuzzySearchDTOList.push({ fieldName: 'topicName', fieldValue: searchTopicName });
    }
    const topicName = hashDataParse(location.hash)?.topicName;
    if (topicName) {
      params.topicName = topicName;
    }
    getOperatingStateList(params);
  };

  useEffect(() => {
    searchFn();
  }, []);

  useEffect(() => {
    setSearchGroupName(detailParams.searchKeywords);
  }, [detailParams.searchKeywords]);

  const getOperatingStateList = (params: getOperatingStateListParams) => {
    setConsumersListLoading(true);
    Utils.post(Api.getOperatingStateList(clusterPhyId), params).then((data: any) => {
      setConsumersListLoading(false);
      const newData = data?.bizData.map((item: any, key: number) => {
        return { ...item, unique: key * pageIndex * pageSize + item?.groupName };
      });
      setConsumerGroupList(newData || []);
      setPageIndex(data.pagination.pageNo);
      setPageTotal(data.pagination.total);
      setPageSize(data.pagination.pageSize);
    });
  };

  const columns = () => {
    const baseColumns: any = [
      {
        title: 'ConsumerGroup',
        dataIndex: 'groupName',
        key: 'groupName',
        render: (v: any, r: any) => {
          return (
            <a
              onClick={() => {
                window.location.hash = `groupName=${v || ''}&topicName=${r.topicName}`;
              }}
            >
              {v}
            </a>
          );
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

    // if (
    //   global.hasPermission &&
    //   global.hasPermission(
    //     scene === 'topicDetail' ? ClustersPermissionMap.TOPIC_RESET_OFFSET : ClustersPermissionMap.CONSUMERS_RESET_OFFSET
    //   )
    // ) {
    //   baseColumns.push({
    //     title: '操作',
    //     dataIndex: 'desc',
    //     key: 'desc',
    //     render: (txt: any, record: any) => {
    //       return <ResetOffsetDrawer record={record}></ResetOffsetDrawer>;
    //     },
    //   });
    // }

    return baseColumns;
  };

  const onTableChange = (pagination: any, filters: any, sorter: any) => {
    const params: getOperatingStateListParams = {
      pageNo: pagination.current,
      pageSize: pagination.pageSize,
      fuzzySearchDTOList: [],
    };
    // setFilteredInfo(filters);
    if (searchGroupName) {
      // params.groupName = searchGroupName;
      params.fuzzySearchDTOList.push({ fieldName: 'groupName', fieldValue: searchGroupName });
    }
    if (searchTopicName) {
      params.fuzzySearchDTOList.push({ fieldName: 'topicName', fieldValue: searchTopicName });
    }
    const topicName = hashDataParse(location.hash)?.topicName;
    if (topicName) {
      params.topicName = topicName;
    }
    getOperatingStateList(params);
  };

  const showModes = [
    { label: '列表模式', value: 'list' },
    { label: '关系图模式', value: 'graph' },
  ];
  return (
    <>
      {scene !== 'topicDetail' && (
        <div className="breadcrumb" style={{ marginBottom: '10px' }}>
          <DBreadcrumb
            breadcrumbs={[
              { label: '多集群管理', aHref: '/' },
              { label: global?.clusterInfo?.name, aHref: `/cluster/${global?.clusterInfo?.id}` },
              { label: 'Consumers', aHref: `` },
            ]}
          />
        </div>
      )}
      {scene !== 'topicDetail' && (
        <div style={{ margin: '12px 0' }}>
          {' '}
          <ConsumerGroupHealthCheck></ConsumerGroupHealthCheck>
        </div>
      )}
      <div className={`operating-state ${scene !== 'topicDetail' && 'custom-table-content'}`}>
        {/* <CardBar cardColumns={data}></CardBar> */}
        {scene !== 'topicDetail' && (
          <div className={tableHeaderPrefix}>
            <div className={`${tableHeaderPrefix}-left`}>
              <Tooltip placement="topLeft" arrowPointAtCenter title="数据刷新间隔为1min，可能会有延迟">
                <div className={`${tableHeaderPrefix}-left-refresh`} onClick={() => searchFn()}>
                  <IconFont className={`${tableHeaderPrefix}-left-refresh-icon`} type="icon-shuaxin1" />
                </div>
              </Tooltip>
              <Divider type="vertical" className={`${tableHeaderPrefix}-divider`} />
              <div className="consumers-search">
                <Input
                  className="search-input-short"
                  placeholder="请输入Consumer Group"
                  // suffix={<SearchOutlined />}
                  onChange={(e) => {
                    setSearchGroupName(e.target.value);
                  }}
                  onPressEnter={searchFn}
                />
                <Input
                  className="search-input-short"
                  placeholder="请输入Topic name"
                  // suffix={<SearchOutlined />}
                  onChange={(e) => {
                    setSearchTopicName(e.target.value);
                  }}
                  onPressEnter={searchFn}
                />
              </div>
              {/* <Button type="primary" className="add-btn" onClick={searchFn}>
                查询
              </Button> */}
            </div>
            {/* <div className="right"></div> */}
          </div>
        )}
        {/* <Table columns={columns} dataSource={consumerGroupList} scroll={{  x: 1500 }} />
        {pageTotal > 0 && <Pagination
        className='pro-table-pagination-custom'
        defaultCurrent={1}
        current={pageIndex}
        total={pageTotal}
        pageSize={pageSize} />} */}
        <div className="pro-table-wrap">
          <ProTable
            showQueryForm={false}
            tableProps={{
              loading: consumersListLoading,
              showHeader: false,
              rowKey: 'unique',
              columns: columns(),
              dataSource: consumerGroupList,
              paginationProps:
                pageTotal > 0
                  ? {
                      current: pageIndex,
                      total: pageTotal,
                      pageSize: pageSize,
                    }
                  : null,
              attrs: {
                // expandable: {
                //   expandedRowRender: (record: any) => {
                //     return <ConsumerGroupDetail record={record}></ConsumerGroupDetail>;
                //   },
                //   rowExpandable: (record: any) => true,
                // },
                onChange: onTableChange,
                scroll: { y: 'calc(100vh - 400px)' },
              },
            }}
          />
        </div>
      </div>
      {<ConsumerGroupDetail scene={scene}></ConsumerGroupDetail>}
    </>
  );
};

export default AutoPage;
