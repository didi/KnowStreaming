import React, { useState, useEffect, useRef } from 'react';
import { Select, Form, Utils, AppContainer, Input, Button, ProTable, Badge, Tag, SearchInput, Divider } from 'knowdesign';
import { IconFont } from '@knowdesign/icons';
import BalanceDrawer from './BalanceDrawer';
import HistoryDrawer from './HistoryDrawer';
import DBreadcrumb from 'knowdesign/es/extend/d-breadcrumb';
import { getSizeAndUnit } from '../../constants/common';
import api from '../../api';
import './index.less';
import LoadRebalanceCardBar from '@src/components/CardBar/LoadRebalanceCardBar';
import { BalanceFilter } from './BalanceFilter';
import { ClustersPermissionMap } from '../CommonConfig';
import { tableHeaderPrefix } from '@src/constants/common';

const Balance_Status_OPTIONS = [
  {
    label: '全部',
    value: null,
  },
  {
    label: '已均衡',
    value: 0,
  },
  {
    label: '未均衡',
    value: 2,
  },
];

const balanceStatus: any = {
  0: '已均衡',
  2: '未均衡',
};

const filterNorms: any = {
  ['disk']: 'Disk',
  ['bytesIn']: 'Byte In',
  ['bytesOut']: 'Byte Out',
};

export const defaultPagination = {
  current: 1,
  pageSize: 10,
  position: 'bottomRight',
  showSizeChanger: true,
  pageSizeOptions: ['10', '20', '50', '100'],
};

const LoadBalance: React.FC = (props: any) => {
  const [global] = AppContainer.useGlobalValue();
  const [form] = Form.useForm();
  const [pagination, setPagination] = useState<any>(defaultPagination);
  const [loading, setLoading] = useState<boolean>(true);
  const [data, setData] = useState([]);
  const [visible, setVisible] = useState<boolean>(false);
  const [isCycle, setIsCycle] = useState<boolean>(false);
  const [planVisible, setPlanVisible] = useState<boolean>(false);
  const [circleFormData, setCircleFormData] = useState(null);
  const [trigger, setTrigger] = useState(false);
  const [filterList, setFilterList] = useState<any>(null);
  const [balanceList, setBalanceList] = useState<string>(null);

  const [searchKeywords, setSearchKeywords] = useState<string>('');
  const [searchValue, setSearchValue] = useState<string>('');

  const columns = () => [
    {
      title: 'Broker ID',
      dataIndex: 'brokerId',
      key: 'brokerId',
      fixed: 'left',
      width: 140,
    },
    {
      title: 'Host',
      dataIndex: 'host',
      key: 'host',
      width: 140,
    },
    {
      title: 'Leader',
      dataIndex: 'leader',
      key: 'leader',
      width: 100,
    },
    {
      title: 'Replicas',
      dataIndex: 'replicas',
      key: 'replicas',
      width: 100,
    },
    // {
    //   title: 'CPU',
    //   children: [
    //     {
    //       title: '规格',
    //       dataIndex: 'cpu_spec',
    //       key: 'cpu_spec',
    //       render: (text: any, row: any) => {
    //         return text !== null ? `${text}` : '-';
    //       },
    //     },
    //     {
    //       title: 'AVG',
    //       dataIndex: 'cpu_avg',
    //       key: 'cpu_avg',
    //       render: (text: any, row: any) => {
    //         return text !== null ? `${text} (${(row.cpu_avg * 100 / row.cpu_spec).toFixed(2)}%)` : '-';
    //       },
    //     },
    //     {
    //       title: '是否均衡',
    //       dataIndex: 'cpu_status',
    //       key: 'cpu_status',

    //       render: (text: any, row: any) => {
    //         // 0:已均衡，非0:未均衡
    //         return text !== null ? (text === 0 ? (
    //           <span className="isbalance">
    //             <span className="dot"></span>已均衡
    //           </span>
    //         ) : (
    //           <span className="noBalance">
    //             <span className="dot"></span>未均衡
    //           </span>
    //         )) : '-';
    //       },
    //     },
    //   ],
    // },
    {
      title: 'Disk规格',
      dataIndex: 'disk_spec',
      key: 'disk_spec',
      width: '150px',
      render: (text: any, row: any) => {
        return text !== null ? `${text.toLocaleString()}GB` : '-';
      },
    },
    {
      title: 'Disk AVG',
      dataIndex: 'disk_avg',
      key: 'disk_avg',
      width: '200px',
      render: (text: any, row: any) => {
        return text !== null ? (
          <span>
            <Badge status={row?.disk_status === 0 ? 'success' : 'error'} />
            {`${getSizeAndUnit(text, 'B').valueWithUnit.toLocaleString()} (${(
              (row.disk_avg * 100) /
              Utils.transGBToB(row.disk_spec)
            ).toFixed(2)}%)`}
          </span>
        ) : (
          '-'
        );
      },
    },
    {
      title: 'BytesIn规格',
      dataIndex: 'bytesIn_spec',
      key: 'bytesIn_spec',
      width: '150px',
      render: (text: any, row: any) => {
        return text !== null ? `${text.toLocaleString()}MB/s` : '-';
      },
    },
    {
      title: 'BytesIn AVG',
      dataIndex: 'bytesIn_avg',
      key: 'bytesIn_avg',
      width: '200px',
      render: (text: any, row: any) => {
        return text !== null ? (
          <span>
            <Badge status={row?.bytesIn_status === 0 ? 'success' : 'error'} />
            {`${getSizeAndUnit(text, 'B/s').valueWithUnit.toLocaleString()} (${(
              (row.bytesIn_avg * 100) /
              (row.bytesIn_spec * 1024 * 1024)
            ).toFixed(2)}%)`}
          </span>
        ) : (
          '-'
        );
      },
    },
    {
      title: 'BytesOut规格',
      dataIndex: 'bytesOut_spec',
      key: 'bytesOut_spec',
      width: '150px',
      render: (text: any, row: any) => {
        return text !== null ? `${text.toLocaleString()}MB/s` : '-';
      },
    },
    {
      title: 'BytesOut AVG',
      dataIndex: 'bytesOut_avg',
      key: 'bytesOut_avg',
      width: '200px',
      // eslint-disable-next-line react/display-name
      render: (text: any, row: any) => {
        return text !== null ? (
          <span>
            <Badge status={row?.bytesOut_status === 0 ? 'success' : 'error'} />
            {`${getSizeAndUnit(text, 'B/s').valueWithUnit.toLocaleString()} (${(
              (row.bytesOut_avg * 100) /
              (row.bytesOut_spec * 1024 * 1024)
            ).toFixed(2)}%)`}
          </span>
        ) : (
          '-'
        );
      },
    },
  ];

  useEffect(() => {
    getList();
  }, []);

  const onTableChange = (curPagination: any) => {
    setPagination({
      ...curPagination,
    });
    getList({ pageNo: curPagination.current, pageSize: curPagination.pageSize });
  };

  const resetList = () => {
    setPagination({
      ...pagination,
      pageNo: 1,
    });
    getList();
  };

  const hostSearch = (e: any) => {
    setFilterList([]);
    setPagination({
      ...pagination,
      pageNo: 1,
    });
    setSearchKeywords(e);
    getList({ searchKeywords: e, stateParam: balanceList });
  };

  const getList = (query = {}) => {
    const formData = form.getFieldsValue();
    const queryParams = {
      pageNo: pagination.current,
      pageSize: pagination.pageSize,
      ...formData,
      ...query,
    };

    setLoading(true);
    Utils.request(api.getBalanceList(global?.clusterInfo?.id), {
      method: 'POST',
      data: queryParams,
    }).then(
      (res: any) => {
        const { pageNo, pageSize, pages, total } = res.pagination;

        setPagination({
          ...pagination,
          current: pageNo,
          pageSize,
          total,
        });
        const dataDe = res?.bizData || [];
        const dataHandle = dataDe.map((item: any) => {
          return {
            ...item,
            cpu_spec: item?.sub?.cpu?.spec,
            cpu_avg: item?.sub?.cpu?.avg,
            cpu_status: item?.sub?.cpu?.status,
            disk_spec: item?.sub?.disk?.spec,
            disk_avg: item?.sub?.disk?.avg,
            disk_status: item?.sub?.disk?.status,
            bytesIn_spec: item?.sub?.bytesIn?.spec,
            bytesIn_avg: item?.sub?.bytesIn?.avg,
            bytesIn_status: item?.sub?.bytesIn?.status,
            bytesOut_spec: item?.sub?.bytesOut?.spec,
            bytesOut_avg: item?.sub?.bytesOut?.avg,
            bytesOut_status: item?.sub?.bytesOut?.status,
          };
        });
        setData(dataHandle);
        setLoading(false);
      },
      () => setLoading(false)
    );
  };

  const drawerClose = (sure?: boolean) => {
    if (sure) {
      setTrigger(!trigger);
    }

    setVisible(false);
  };

  const balanceClick = (val: boolean) => {
    Utils.request(api.getBalanceForm(global?.clusterInfo?.id), {
      method: 'GET',
    })
      .then((res: any) => {
        const dataDe = res || {};
        setCircleFormData(dataDe);
      })
      .catch(() => {
        setCircleFormData(null);
      });
    setIsCycle(val);
    setVisible(true);
  };

  const getNorms = (stateParamArr: any) => {
    const stateParam: any = {};
    stateParamArr.forEach((item: any) => {
      stateParam[item.firstLevel] = item.secondLevel;
    });
    setFilterList(stateParamArr);
    setPagination({
      ...pagination,
      pageNo: 1,
    });
    setBalanceList(stateParam);
    getList({ searchKeywords, stateParam });
  };

  const filterNormsClose = (rowId: any) => {
    const newFilterList = filterList.filter((item: any) => item.id !== rowId);
    getNorms(newFilterList);
  };

  return (
    <>
      <div className="breadcrumb" style={{ marginBottom: '10px' }}>
        <DBreadcrumb
          breadcrumbs={[
            { label: '多集群管理', aHref: '/' },
            { label: global?.clusterInfo?.name, aHref: `/cluster/${global?.clusterInfo?.id}` },
            { label: 'Rebalance', aHref: `` },
          ]}
        />
      </div>
      <div style={{ margin: '12px 0' }}>
        <LoadRebalanceCardBar trigger={trigger} genData={resetList} filterList={filterList} />
      </div>
      <div className="load-rebalance-container">
        <div className="balance-main custom-table-content">
          <div className={tableHeaderPrefix}>
            <div className={`${tableHeaderPrefix}-left`}>
              <div
                className={`${tableHeaderPrefix}-left-refresh`}
                onClick={() => getList({ searchKeywords: searchValue, stateParam: balanceList })}
              >
                <IconFont className={`${tableHeaderPrefix}-left-refresh-icon`} type="icon-shuaxin1" />
              </div>
              <Divider type="vertical" className={`${tableHeaderPrefix}-divider`} />
              <BalanceFilter title="负载均衡列表筛选" data={[]} getNorms={getNorms} filterList={filterList} />
            </div>
            {/* <Form form={form} layout="inline" onFinish={resetList}>
              <Form.Item name="status">
                <Select className="grid-select" placeholder="请选择状态" style={{ width: '180px' }} options={Balance_Status_OPTIONS} />
              </Form.Item>

              <Form.Item name="searchKeywords">
                <Input placeholder="请输入Host" style={{ width: '180px' }} />
              </Form.Item>
              <Form.Item>
                <Button type="primary" ghost htmlType="submit">
                  查询
                </Button>
              </Form.Item>
            </Form> */}
            <div className={`${tableHeaderPrefix}-right`}>
              <SearchInput
                onSearch={hostSearch}
                attrs={{
                  value: searchValue,
                  onChange: setSearchValue,
                  placeholder: '请输入 Host',
                  style: { width: '248px' },
                  maxLength: 128,
                }}
              />
              <Button type="primary" ghost onClick={() => setPlanVisible(true)}>
                均衡历史
              </Button>
              {global.hasPermission(ClustersPermissionMap.REBALANCE_CYCLE) && (
                <Button type="primary" ghost onClick={() => balanceClick(true)}>
                  周期均衡
                </Button>
              )}
              {global.hasPermission(ClustersPermissionMap.REBALANCE_IMMEDIATE) && (
                <Button type="primary" onClick={() => balanceClick(false)}>
                  立即均衡
                </Button>
              )}
            </div>
          </div>
          {filterList && filterList.length > 0 && (
            <div style={{ marginBottom: '12px' }}>
              <span style={{ marginRight: '6px' }}>筛选结果:{pagination?.total || 0}条</span>
              {filterList.map((item: any) => {
                return (
                  <Tag
                    style={{ padding: '6px 10px', backgroundColor: 'rgba(33,37,41,0.08)', color: '#495057', borderRadius: '6px' }}
                    key={item.id}
                    closable
                    onClose={() => filterNormsClose(item.id)}
                  >
                    <span>{filterNorms[item.firstLevel]}:</span>
                    <span>{balanceStatus[item.secondLevel]}</span>
                  </Tag>
                );
              })}
            </div>
          )}
          <ProTable
            tableProps={{
              showHeader: false,
              loading,
              rowKey: 'brokerId',
              dataSource: data,
              paginationProps: pagination,
              columns: columns() as any,
              lineFillColor: true,
              attrs: {
                onChange: onTableChange,
                // bordered: false,
                // className: 'remove-last-border',
                scroll: { x: 'max-content', y: 'calc(100vh - 440px)' },
              },
            }}
          />
        </div>
        <BalanceDrawer visible={visible} isCycle={isCycle} onClose={drawerClose} formData={circleFormData} genData={getList} />
        {planVisible && (
          <HistoryDrawer
            visible={planVisible}
            onClose={() => {
              setPlanVisible(false);
            }}
          />
        )}
      </div>
    </>
  );
};

export default LoadBalance;
