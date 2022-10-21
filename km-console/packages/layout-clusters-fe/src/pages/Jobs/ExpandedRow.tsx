import React, { useEffect, useState } from 'react';
import { Badge, ProTable, Tooltip, Utils, Progress } from 'knowdesign';
import { formatAssignSize, runningStatusEnum } from './config';

const columns: any = [
  {
    title: 'Partition',
    dataIndex: 'partitionId',
    key: 'partitionId',
    render: (t: any, r: any) => {
      // return runningStatusEnum[r?.status];
      return (
        <span>
          {t}
          <Badge
            style={{ marginLeft: '6px' }}
            status={r?.status === 1 ? 'warning' : r?.status === 4 ? 'error' : r?.status === 3 ? 'success' : 'warning'}
          />
        </span>
      );
    },
  },
  {
    title: '源BrokerID',
    dataIndex: 'sourceBrokerIds',
    key: 'sourceBrokerIds',
    render: (t: any, r: any) => {
      return t && t.length > 0 ? t.join('、') : '-';
    },
  },
  {
    title: '目标BrokerID',
    dataIndex: 'desBrokerIds',
    key: 'desBrokerIds',
    render(t: any, r: any) {
      return t && t.length > 0 ? t.join('、') : '-';
    },
  },
  // {
  //   title: '需迁移MessageSize（MB）',
  //   dataIndex: 'totalSize',
  //   key: 'totalSize',
  //   render(t: any, r: any) {
  //     return t || t === 0 ? formatAssignSize(t, 'MB') : '-';
  //   },
  // },
  // {
  //   title: '已完成MessageSize （MB）',
  //   dataIndex: 'movedSize',
  //   key: 'movedSize',
  //   render(t: any, r: any) {
  //     return t || t === 0 ? formatAssignSize(t, 'MB') : '-';
  //   },
  // },
  {
    title: 'MessageSize迁移进度(MB)',
    dataIndex: 'movedSize',
    key: 'movedSize',
    render(t: any, r: any) {
      const movedSize = r.movedSize ? Number(Utils.formatAssignSize(t, 'MB')) : 0;
      const totalSize = r.totalSize ? Number(Utils.formatAssignSize(t, 'MB')) : 0;
      return (
        <div className="message-size">
          <Tooltip
            title={(movedSize === 0 && totalSize === 0 ? 100 : movedSize > 0 && totalSize > 0 ? (movedSize / totalSize) * 100 : 0) + '%'}
          >
            <Progress
              percent={movedSize === 0 && totalSize === 0 ? 100 : movedSize > 0 && totalSize > 0 ? (movedSize / totalSize) * 100 : 0}
              strokeColor="#556EE6"
              trailColor="#ECECF1"
              showInfo={false}
            />
          </Tooltip>
          <span>{movedSize + '/' + totalSize}</span>
        </div>
      );
    },
  },
  // {
  //   title: '状态',
  //   key: 'status',
  //   width: 100,
  //   render: (t: any, r: any) => {
  //     return (
  //       <span>
  //         <Badge status={r?.status === 1 ? 'warning' : r?.status === 4 ? 'error' : r?.status === 3 ? 'success' : 'warning'} />
  //         {runningStatusEnum[r?.status]}
  //       </span>
  //     );
  //   },
  // },
  // {
  //   title: 'BytesIn（MB/s）',
  //   dataIndex: 'byteIn',
  //   key: 'byteIn',
  //   render(t: any, r: any) {
  //     return t ? t : '-';
  //   },
  // },
  // {
  //   title: '同步速率（MB/s）',
  //   dataIndex: 'byteMove',
  //   key: 'byteMove',
  //   render(t: any, r: any) {
  //     return t ? t : '-';
  //   },
  // },
  {
    title: '预计剩余时长',
    dataIndex: 'remainTime',
    key: 'remainTime',
    render(t: any, r: any) {
      return t ? Utils.transUnitTime(t) : t === 0 ? t : '-';
    },
  },
];

export const ExpandedRow: any = ({ record, data, loading }: any) => {
  const [pagination, setPagination] = useState<any>({
    current: 1,
    pageSize: 5,
    // total: data[record.key]?.length,
    simple: true,
    hideOnSinglePage: false,
  });
  const [status, setStatus] = useState({
    total: 0,
    success: 0,
    doing: 0,
    fail: 0,
  });
  const onTableChange = (pagination: any, filters: any) => {
    setPagination(pagination);
  };

  const calcStatus = () => {
    const success = data[record.key]?.filter((item: any) => runningStatusEnum[item.status] === 'Success').length || 0;
    const doing = data[record.key]?.filter((item: any) => runningStatusEnum[item.status] === 'Doing').length || 0;
    const fail = data[record.key]?.filter((item: any) => runningStatusEnum[item.status] === 'Fail').length || 0;
    const total = data[record.key]?.length || 0;
    setStatus({ total, success, doing, fail });
  };

  useEffect(() => {
    calcStatus();
  }, [data[record.key]]);

  return (
    <div
      key={record.key}
      style={{ position: 'relative', padding: '12px 16px', border: '1px solid #EFF2F7', borderRadius: '8px', backgroundColor: '#ffffff' }}
    >
      <ProTable
        // bordered
        tableProps={{
          isCustomPg: false,
          showHeader: false,
          loading: loading[record.key],
          rowKey: 'key',
          dataSource: data[record.key] || [],
          columns,
          lineFillColor: true,
          // noPagination: true,
          paginationProps: pagination,
          attrs: {
            className: 'expanded-table',
            onChange: onTableChange,
            scroll: { x: 'max-content' },
            size: 'small',
            bordered: false,
            rowClassName: 'table-small-bgcolor',
          },
        }}
      />
      <div style={{ position: 'absolute', bottom: '12px', left: '16px' }}>
        <span>执行情况：</span>
        <span>Partition总数 {(status?.success || 0) + (status?.doing || 0) + (status?.fail || 0)}</span>
        <span style={{ marginLeft: '34px', display: 'inline-block' }}>
          <Badge status="success" />
          Success {status?.success || 0}
        </span>
        <span style={{ marginLeft: '34px', display: 'inline-block' }}>
          <Badge status="error" />
          Fail {status?.fail || 0}
        </span>
        <span style={{ marginLeft: '34px', display: 'inline-block' }}>
          <Badge status="warning" />
          Doing {status?.doing || 0}
        </span>
        {/* <Pagination size="small" onChange={onTableChange} simple total={data[record.key]?.length} /> */}
      </div>
    </div>
  );
};
