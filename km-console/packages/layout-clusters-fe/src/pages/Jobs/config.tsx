/* eslint-disable @typescript-eslint/explicit-module-boundary-types */
import React from 'react';
import { timeFormat, getSizeAndUnit } from '../../constants/common';
import moment from 'moment';
import { Tooltip, Tag, Badge, Utils, Progress } from 'knowdesign';
import { renderTableOpts } from 'knowdesign/es/common-pages/render-table-opts';
import TagsWithHide from '@src/components/TagsWithHide';
import { PopoverBroker } from './PopoverBroker';

// 任务类型下拉
export const jobType = [
  {
    label: 'Topic迁移',
    value: 0,
  },
  {
    label: '扩缩副本',
    value: 1,
  },
  process.env.BUSSINESS_VERSION
    ? {
        label: '集群均衡',
        value: 2,
      }
    : undefined,
].filter((t) => t);

//
export const jobTypeEnum: any = {
  0: 'Topic迁移',
  1: '扩缩副本',
  2: '集群均衡',
};

// 运行状态下拉
export const runningStatus = [
  {
    label: 'Doing',
    value: 1,
  },
  {
    label: 'Prepare',
    value: 2,
  },
  {
    label: 'Success',
    value: 3,
  },
  {
    label: 'Failed',
    value: 4,
  },
  {
    label: 'Canceled',
    value: 5,
  },
];

export const runningStatusEnum: any = {
  1: 'Doing',
  2: 'Prepare',
  3: 'Success',
  4: 'Failed',
  5: 'Canceled',
};

export const getJobsListColumns = (arg?: any) => {
  const columns = [
    // {
    //   title: '任务队列',
    //   dataIndex: 'taskQueue',
    //   key: 'taskQueue',
    // },
    {
      title: '任务ID',
      dataIndex: 'id',
      key: 'id',
      width: 70,
    },
    {
      title: '任务类型',
      dataIndex: 'jobType',
      key: 'jobType',
      render(t: any, r: any) {
        return jobTypeEnum[t];
      },
    },
    {
      title: '任务执行对象',
      dataIndex: 'target',
      key: 'target',
      width: 232,
      render(t: any, r: any) {
        return <TagsWithHide placement="bottom" list={t.split(',')} expandTagContent={(num: any) => `共有${num}个`} />;
      },
    },
    {
      title: '运行状态',
      dataIndex: 'jobStatus',
      key: 'jobStatus',
      render(t: any, r: any) {
        return (
          <Tag
            className="12312512512"
            style={{
              background: t === 1 ? 'rgba(85,110,230,0.10)' : t === 4 ? '#fff3e4' : t === 3 ? 'rgba(0,192,162,0.10)' : '#ebebf6',
              color: t === 1 ? '#556EE6' : t === 4 ? '#F58342' : t === 3 ? '#00C0A2' : '#495057',
              padding: '3px 6px',
            }}
          >
            {runningStatusEnum[t]}
          </Tag>
        );
      },
    },
    {
      title: '运行进度',
      dataIndex: 'progress',
      key: 'progress',
      width: 90,
      render: (_t: any, r: any) => {
        const { success, total } = r;
        return (success || 0) + '/' + (total || 0);
      },
    },
    {
      title: '运行结果',
      dataIndex: 'result',
      key: 'result',
      // eslint-disable-next-line react/display-name
      render: (_t: any, r: any) => {
        const { success, fail, waiting, doing } = r;
        return (
          <div className="run-result">
            <span>
              成功:
              <span>
                {success === 0 || success ? (
                  (success + '').length < 3 ? (
                    success
                  ) : (
                    <Tooltip title={success}>{(success + '').slice(0, 2) + '...'}</Tooltip>
                  )
                ) : (
                  '-'
                )}
              </span>
            </span>
            <span>
              失败:
              {fail === 0 || fail ? (fail + '').length < 3 ? fail : <Tooltip title={fail}>{(fail + '').slice(0, 2) + '...'}</Tooltip> : '-'}
            </span>
            <span>
              运行中:
              {doing === 0 || doing ? (
                (doing + '').length < 3 ? (
                  doing
                ) : (
                  <Tooltip title={doing}>{(doing + '').slice(0, 2) + '...'}</Tooltip>
                )
              ) : (
                '-'
              )}
            </span>
            <span>
              待运行:
              {waiting === 0 || waiting ? (
                (waiting + '').length < 3 ? (
                  waiting
                ) : (
                  <Tooltip title={waiting}>{(waiting + '').slice(0, 2) + '...'}</Tooltip>
                )
              ) : (
                '-'
              )}
            </span>
          </div>
        );
      },
    },
    {
      title: '描述',
      dataIndex: 'jobDesc',
      key: 'jobDesc',
      width: 150,
      needTooltip: true,
    },
    {
      title: '提交人',
      dataIndex: 'creator',
      key: 'creator',
    },
    {
      title: '计划执行时间',
      dataIndex: 'planTime',
      width: 160,
      key: 'planTime',
      render: (t: any, r: any) => {
        return t ? moment(t).format(timeFormat) : '-';
      },
    },
    {
      title: '实际执行时间',
      dataIndex: 'startTime',
      key: 'startTime',
      width: 160,
      render: (t: any, r: any) => {
        if (moment(t).format('x') < moment(r.planTime).format('x')) {
          return '已逾期';
        }
        // 判断是否为 mysql 默认的1971-01-01 00:00:00
        if (+moment(t).format('x') === 31507200000) {
          return '-';
        }
        return t ? moment(t).format(timeFormat) : '-';
      },
    },
    {
      title: '操作',
      dataIndex: 'options',
      key: 'options',
      width: 150,
      filterTitle: true,
      fixed: 'right',
      // eslint-disable-next-line react/display-name
      render: (_t: any, r: any) => {
        return (
          <div>
            {r.jobStatus !== 2 && r.jobStatus !== 5 ? <a onClick={() => arg.setViewProgress(r)}>查看进度</a> : null}
            {/* 编辑任务 */}
            {r.jobStatus === 2 ? (
              <a style={{ marginRight: '16px' }} onClick={() => arg.setViewProgress(r, r.jobType)}>
                编辑任务
              </a>
            ) : null}
            {r.jobStatus === 2 ? <a onClick={() => arg.onDelete(r)}>删除</a> : null}
          </div>
        );
      },
    },
  ];
  return columns;
};
// * 获取任务明细-扩缩副本列表配置
export const getTaskDetailsColumns = (arg?: any) => {
  const columns = [
    {
      title: 'Topic',
      dataIndex: 'topicName',
      key: 'topicName',
      fixed: 'left',
      width: 102,
      render: (t: any, r: any) => {
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
      title: '当前副本数',
      dataIndex: 'oldReplicaNu',
      key: 'oldReplicaNu',
    },
    {
      title: '源BrokerID',
      dataIndex: 'sourceBrokers',
      key: 'sourceBrokers',
      render: (t: any, r: any) => {
        return t && t.length > 0 ? t.join('、') : '-';
      },
      // render(t: any, r: any) {
      //   return (
      //     <div style={{ width: '100px' }}>
      //       <TagsWithHide list={t} expandTagContent={(num: any) => `共有${num}个`} />
      //     </div>
      //   );
      // },
    },
    {
      title: '新副本数',
      dataIndex: 'newReplicaNu',
      key: 'newReplicaNu',
    },
    {
      title: '目标BrokerID',
      dataIndex: 'desBrokers',
      key: 'desBrokers',
      render: (t: any, r: any) => {
        return t && t.length > 0 ? t.join('、') : '-';
      },
      // render(t: any, r: any) {
      //   return (
      //     <div style={{ width: '100px' }}>
      //       <TagsWithHide list={t} expandTagContent={(num: any) => `共有${num}个`} />
      //     </div>
      //   );
      // },
    },
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
              title={
                (r.success === r.total && r.total > 0 ? 100 : movedSize > 0 && totalSize > 0 ? (movedSize / totalSize) * 100 : 0) + '%'
              }
            >
              <Progress
                percent={r.success === r.total && r.total > 0 ? 100 : movedSize > 0 && totalSize > 0 ? (movedSize / totalSize) * 100 : 0}
                strokeColor="#556EE6"
                showInfo={false}
              />
            </Tooltip>
            <span>{movedSize + '/' + totalSize}</span>
          </div>
        );
      },
    },
    {
      title: '分区进度',
      dataIndex: 'partitionRate',
      key: 'partitionRate',
      render: (t: any, r: any) => {
        return (r.success || r.success === 0 ? r.success : '-') + '/' + (r.total || r.total === 0 ? r.total : '-');
      },
    },
    {
      title: '预计剩余时长',
      dataIndex: 'remainTime',
      key: 'remainTime',
      render(t: any, r: any) {
        return t ? Utils.transUnitTime(t) : t === 0 ? t : '-';
      },
    },
    // {
    //   title: '当前副本数',
    //   dataIndex: 'progress',
    //   key: 'progress',
    //   render: (_t: any, r: any) => {
    //     return r.success + '/' + (r.success + r.doing + r.fail);
    //   },
    // },
  ];
  return columns;
};

// * 获取任务明细-Topic迁移列表配置
export const getMoveBalanceColumns = (arg?: any) => {
  const columns = [
    {
      title: 'Topic',
      dataIndex: 'topicName',
      key: 'topicName',
      fixed: 'left',
      width: 102,
      render: (t: any, r: any) => {
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
      title: '分区',
      dataIndex: 'partitions',
      key: 'partitions',
      render: (t: any, r: any) => {
        return t && t.length > 0 ? t.join('、') : '-';
      },
    },
    {
      title: '源BrokerID',
      dataIndex: 'sourceBrokers',
      key: 'sourceBrokers',
      render: (t: any, r: any) => {
        return t && t.length > 0 ? t.join('、') : '-';
      },
      // render(t: any, r: any) {
      //   return (
      //     <div style={{ width: '100px' }}>
      //       <TagsWithHide list={t} expandTagContent={(num: any) => `共有${num}个`} />
      //     </div>
      //   );
      // },
    },
    {
      title: '目标BrokerID',
      dataIndex: 'desBrokers',
      key: 'desBrokers',
      render: (t: any, r: any) => {
        return t && t.length > 0 ? t.join('、') : '-';
      },
      // render(t: any, r: any) {
      //   return (
      //     <div style={{ width: '100px' }}>
      //       <TagsWithHide list={t} expandTagContent={(num: any) => `共有${num}个`} />
      //     </div>
      //   );
      // },
    },
    {
      title: '当前数据保存时间 （h）',
      dataIndex: 'currentTimeSpent',
      key: 'currentTimeSpent',
      render: (t: any, r: any) => {
        return t || t === 0 ? Utils.transMSecondToHour(+t) : '-';
      },
    },
    {
      title: '迁移数据时间范围 （h）',
      dataIndex: 'moveTimeSpent',
      key: 'moveTimeSpent',
      render: (t: any, r: any) => {
        return t || t === 0 ? Utils.transMSecondToHour(+t) : '-';
      },
    },
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
              title={
                (r.success === r.total && r.total > 0 ? 100 : movedSize > 0 && totalSize > 0 ? (movedSize / totalSize) * 100 : 0) + '%'
              }
            >
              <Progress
                percent={r.success === r.total && r.total > 0 ? 100 : movedSize > 0 && totalSize > 0 ? (movedSize / totalSize) * 100 : 0}
                strokeColor="#556EE6"
                showInfo={false}
              />
            </Tooltip>
            <span>{movedSize + '/' + totalSize}</span>
          </div>
        );
      },
    },
    // {
    //   title: '需迁移MessageSize（MB）',
    //   dataIndex: 'totalSize',
    //   key: 'totalSize',
    //   width: 100,
    //   render: (t: any, r: any) => {
    //     return t || t === 0 ? formatAssignSize(t, 'MB') : '-';
    //   },
    // },
    {
      title: '分区进度',
      dataIndex: 'partitionProgress',
      key: 'partitionProgress',
      render: (t: any, r: any) => {
        return (r.success || r.success === 0 ? r.success : '-') + '/' + (r.total || r.total === 0 ? r.total : '-');
      },
    },
    // {
    //   title: '已完成MessageSize （MB）',
    //   dataIndex: 'movedSize',
    //   key: 'movedSize',
    //   width: 100,
    //   render: (t: any, r: any) => {
    //     return t || t === 0 ? formatAssignSize(t, 'MB') : '-';
    //   },
    // },
    // {
    //   title: '状态',
    //   dataIndex: 'status',
    //   key: 'status',
    //   width: 100,
    //   render: (t: any) => {
    //     return (
    //       <span>
    //         <Badge status={t === 1 ? 'warning' : t === 4 ? 'error' : t === 3 ? 'success' : 'warning'} />
    //         {runningStatusEnum[t]}
    //       </span>
    //     );
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
    // {
    //   title: '当前副本数',
    //   dataIndex: 'progress',
    //   key: 'progress',
    //   render: (_t: any, r: any) => {
    //     return r.success + '/' + (r.success + r.doing + r.fail);
    //   },
    // },
  ];
  return columns;
};

// * 获取任务明细-集群均衡列表配置
export const getClusterBalanceColumns = (arg?: any) => {
  const columns = [
    {
      title: 'Topic',
      dataIndex: 'topicName',
      key: 'topicName',
      width: 102,
      needTooltip: true,
    },
    {
      title: '分区',
      dataIndex: 'partition',
      key: 'partition',
    },
    {
      title: '源BrokerID',
      dataIndex: 'sourceBrokerIds',
      key: 'sourceBrokerIds',
    },
    {
      title: '目标BrokerID',
      dataIndex: 'desBrokerIds',
      key: 'desBrokerIds',
    },
    {
      title: '当前数据保存时间 （h）',
      dataIndex: 'currentTimeSpent',
      key: 'currentTimeSpent',
    },
    {
      title: '迁移数据时间范围 （h）',
      dataIndex: 'currentTimeSpent',
      key: 'currentTimeSpent',
    },
    {
      title: '需迁移MessageSize（MB）',
      dataIndex: 'totalSize',
      key: 'totalSize',
      width: 100,
    },
    {
      title: '分区进度',
      dataIndex: 'partitionProgress',
      key: 'partitionProgress',
    },
    {
      title: '已完成MessageSize （MB）',
      dataIndex: 'movedSize',
      key: 'movedSize',
      width: 100,
    },
    {
      title: '状态',
      dataIndex: 'status',
      key: 'status',
      render(t: any, r: any) {
        return runningStatusEnum[t];
      },
      width: 100,
    },
    {
      title: '预计剩余时长',
      dataIndex: 'remainTime',
      key: 'remainTime',
    },
    // {
    //   title: '当前副本数',
    //   dataIndex: 'progress',
    //   key: 'progress',
    //   render: (_t: any, r: any) => {
    //     return r.success + '/' + (r.success + r.doing + r.fail);
    //   },
    // },
  ];
  return columns;
};

export const getNodeTrafficColumns = (arg?: any) => {
  const columns = [
    {
      title: 'Broker',
      dataIndex: 'brokerId',
      key: 'brokerId',
    },
    {
      title: 'Host',
      dataIndex: 'brokerHost',
      key: 'brokerHost',
    },
    {
      title: 'Job Bytes In（MB/s）',
      dataIndex: 'byteInJob',
      key: 'byteInJob',
      render: (t: any, r: any) => {
        return (
          <div>
            <span>{t || t === 0 ? Utils.transBToMB(t) : '-'}</span>
            {<PopoverBroker title="Job Bytes In" data={r?.inBrokers} />}
          </div>
        );
      },
    },
    {
      title: 'Job Bytes Out（MB/s）',
      dataIndex: 'byteOutJob',
      key: 'byteOutJob',
      render: (t: any, r: any) => {
        return (
          <div>
            <span>{t || t === 0 ? Utils.transBToMB(t) : '-'}</span>
            {r?.outBrokers && <PopoverBroker title="Job Bytes Out" data={r?.outBrokers} />}
          </div>
        );
      },
    },
    {
      title: 'Total  BytesIn（MB/s）',
      dataIndex: 'byteInTotal',
      key: 'byteInTotal',
      render: (t: any, r: any) => {
        return t || t === 0 ? Utils.transBToMB(t) : '-';
      },
    },
    {
      title: 'Total Bytes Out（MB/s）',
      dataIndex: 'byteOutTotal',
      key: 'byteOutTotal',
      render: (t: any, r: any) => {
        return t || t === 0 ? Utils.transBToMB(t) : '-';
      },
    },
  ];
  return columns;
};

export const defaultPagination = {
  current: 1,
  pageSize: 10,
  position: 'bottomRight',
  showSizeChanger: true,
  pageSizeOptions: ['10', '20', '50', '100', '200', '500'],
};

const KB = 1024;
const MB = KB * KB;
const GB = MB * KB;
const TB = GB * KB;

export const formatAssignSize = (size: number, convertTarget: string, fix = 2) => {
  if (size === undefined || size === null) return '';
  if (convertTarget === undefined || convertTarget === null) return size;
  if (convertTarget === 'KB') return `${(size / KB).toFixed(fix)}`;
  if (convertTarget === 'MB') return `${(size / MB).toFixed(fix)}`;
  if (convertTarget === 'GB') return `${(size / GB).toFixed(fix)}`;

  return `${(size / TB).toFixed(fix)}TB`;
};
