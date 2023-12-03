import SmallChart from '@src/components/SmallChart';
import TagsWithHide from '@src/components/TagsWithHide';
import { Button, Tag, Tooltip, Utils, Popconfirm, AppContainer } from 'knowdesign';
import React from 'react';
import Delete from './Delete';
import { ClustersPermissionMap } from '../CommonConfig';
export const defaultPagination = {
  current: 1,
  pageSize: 10,
  position: 'bottomRight',
  showSizeChanger: true,
  pageSizeOptions: ['10', '20', '50', '100', '200', '500'],
};

export const optionType: { [name: string]: string } = {
  ['stop']: '暂停',
  ['restart']: '重启',
  ['resume']: '继续',
};

export const stateEnum: any = {
  ['UNASSIGNED']: {
    // 未分配
    name: 'Unassigned',
    color: '#556EE6',
    bgColor: '#EBEEFA',
  },
  ['RUNNING']: {
    // 运行
    name: 'Running',
    color: '#00C0A2',
    bgColor: 'rgba(0,192,162,0.10)',
  },
  ['PAUSED']: {
    // 暂停
    name: 'Paused',
    color: '#495057',
    bgColor: '#ECECF6',
  },
  ['FAILED']: {
    // 失败
    name: 'Failed',
    color: '#F58342',
    bgColor: '#fef3e5',
  },
  ['DESTROYED']: {
    // 销毁
    name: 'Destroyed',
    color: '#FF7066',
    bgColor: '#fdefee',
  },
  ['RESTARTING']: {
    // 重新启动
    name: 'Restarting',
    color: '#3991FF',
    bgColor: '#e9f5ff',
  },
};

const calcCurValue = (record: any, metricName: string) => {
  // const item = (record.metricPoints || []).find((item: any) => item.metricName === metricName);
  // return item?.value || '';
  // TODO 替换record
  const orgVal = record?.latestMetrics?.metrics?.[metricName];
  if (orgVal !== undefined) {
    if (metricName === 'TotalRecordErrors') {
      return Math.round(orgVal).toLocaleString();
    } else {
      return Number(Utils.formatAssignSize(orgVal, 'KB', orgVal > 1000 ? 2 : 3)).toLocaleString();
      // return Utils.formatAssignSize(orgVal, 'KB');
    }
  }
  return '-';
  // return orgVal !== undefined ? (metricName !== 'HealthScore' ? formatAssignSize(orgVal, 'KB') : orgVal) : '-';
};

const renderLine = (record: any, metricName: string) => {
  const points = record.metricLines?.find((item: any) => item.metricName === metricName)?.metricPoints || [];
  return points.length ? (
    <div className="metric-data-wrap">
      <SmallChart
        width={'100%'}
        height={30}
        chartData={{
          name: record.metricName,
          data: points.map((item: any) => ({ time: item.timeStamp, value: item.value })),
        }}
      />
      <span className="cur-val">{calcCurValue(record, metricName)}</span>
    </div>
  ) : (
    <span className="cur-val">{calcCurValue(record, metricName)}</span>
  );
};

export const getConnectorsColumns = (arg?: any) => {
  const [global] = AppContainer.useGlobalValue();
  const columns: any = [
    {
      title: 'Connector Name',
      dataIndex: 'connectorName',
      key: 'connectorName',
      width: 160,
      fixed: 'left',
      lineClampOne: true,
      render: (t: string, r: any) => {
        return t ? (
          <>
            <Tooltip placement="bottom" title={t}>
              <a
                onClick={() => {
                  arg.getDetailInfo(r);
                }}
              >
                {t}
              </a>
            </Tooltip>
          </>
        ) : (
          '-'
        );
      },
    },
    {
      title: 'Connect集群',
      dataIndex: 'connectClusterName',
      key: 'connectClusterName',
      width: 200,
      lineClampOne: true,
      needTooltip: true,
      // render: (t: string, r: any) => {
      //   return (
      //     <span>
      //       {t}
      //       {r?.status ? <Tag className="tag-success">Live</Tag> : <Tag className="tag-error">Down</Tag>}
      //     </span>
      //   );
      // },
    },

    {
      title: 'State',
      dataIndex: 'state',
      key: 'state',
      width: 120,
      render: (t: string, r: any) => {
        return t ? (
          <Tag
            style={{
              background: stateEnum[t]?.bgColor,
              color: stateEnum[t]?.color,
              padding: '3px 6px',
            }}
          >
            {stateEnum[t]?.name}
          </Tag>
        ) : (
          '-'
        );
      },
    },
    {
      title: 'Class',
      dataIndex: 'connectorClassName',
      key: 'connectorClassName',
      width: 150,
      lineClampOne: true,
      needTooltip: true,
    },
    {
      title: 'Type',
      dataIndex: 'connectorType',
      key: 'connectorType',
      width: 100,
      render: (value: any, record: any) => Utils.firstCharUppercase(value),
    },
    {
      title: 'Tasks',
      dataIndex: 'taskCount',
      key: 'taskCount',
      width: 100,
    },
    {
      title: '消息读取速率(KB/s)',
      dataIndex: 'readRate',
      key: 'readRate',
      sorter: true,
      width: 170,
      render: (value: any, record: any) =>
        renderLine(record, record.connectorType === 'SINK' ? 'SinkRecordReadRate' : 'SourceRecordPollRate'),
    },
    {
      title: '消息写入速率(KB/s)',
      dataIndex: 'writeRate',
      key: 'writeRate',
      sorter: true,
      width: 170,
      render: (value: any, record: any) =>
        renderLine(record, record.connectorType === 'SINK' ? 'SinkRecordSendRate' : 'SourceRecordWriteRate'),
    },
    {
      title: '消息处理错误次数(次)',
      dataIndex: 'recordErrors',
      key: 'recordErrors',
      sorter: true,
      width: 170,
      render: (value: any, record: any) => renderLine(record, 'TotalRecordErrors'),
    },
    {
      title: 'Topics',
      dataIndex: 'topicNameList',
      key: 'topicNameList',
      width: 200,
      render(t: any, r: any) {
        return t && t.length > 0 ? <TagsWithHide placement="bottom" list={t} expandTagContent={(num: any) => `共有${num}个`} /> : '-';
      },
    },
  ];

  if (global.hasPermission) {
    columns.push({
      title: '操作',
      dataIndex: 'options',
      key: 'options',
      width: 200,
      filterTitle: true,
      fixed: 'right',
      // eslint-disable-next-line react/display-name
      render: (_t: any, r: any) => {
        return (
          <div>
            {global.hasPermission(ClustersPermissionMap.CONNECTOR_RESTART) ? (
              <Popconfirm
                title="是否重启当前任务？"
                onConfirm={() => arg?.optionConnect(r, 'restart')}
                // onCancel={cancel}
                okText="是"
                cancelText="否"
                overlayClassName="connect-popconfirm"
              >
                <Button key="restart" type="link" size="small">
                  重启
                </Button>
              </Popconfirm>
            ) : (
              <></>
            )}

            {global.hasPermission(ClustersPermissionMap.CONNECTOR_STOP_RESUME) && (r.state === 'RUNNING' || r.state === 'PAUSED') && (
              <Popconfirm
                title={`是否${r.state === 'RUNNING' ? '暂停' : '继续'}当前任务？`}
                onConfirm={() => arg?.optionConnect(r, r.state === 'RUNNING' ? 'stop' : 'resume')}
                // onCancel={cancel}
                okText="是"
                cancelText="否"
                overlayClassName="connect-popconfirm"
              >
                <Button key="stopResume" type="link" size="small">
                  {/* {r?.state !== 1 ? '继续' : '暂停'} */}
                  {r.state === 'RUNNING' ? '暂停' : '继续'}
                </Button>
              </Popconfirm>
            )}
            {global.hasPermission(ClustersPermissionMap.CONNECTOR_CHANGE_CONFIG) ? (
              <Button type="link" size="small" onClick={() => arg?.editConnector(r)}>
                编辑
              </Button>
            ) : (
              <></>
            )}

            {global.hasPermission(ClustersPermissionMap.CONNECTOR_DELETE) ? (
              <Delete record={r} onConfirm={arg?.deleteTesk}></Delete>
            ) : (
              <></>
            )}
          </div>
        );
      },
    });
  }
  return columns;
};

// Workers
export const getWorkersColumns = (arg?: any) => {
  const columns = [
    {
      title: 'Worker Host',
      dataIndex: 'workerHost',
      key: 'workerHost',
      width: 200,
    },
    {
      title: '所属集群',
      dataIndex: 'connectClusterName',
      key: 'connectClusterName',
      width: 200,
    },
    {
      title: 'Connectors',
      dataIndex: 'connectorCount',
      key: 'connectorCount',
      width: 200,
    },
    {
      title: 'Tasks',
      dataIndex: 'taskCount',
      key: 'taskCount',
      width: 200,
    },
  ];
  return columns;
};

// Detail
export const getConnectorsDetailColumns = (arg?: any) => {
  const [global] = AppContainer.useGlobalValue();
  const columns = [
    {
      title: 'Task ID',
      dataIndex: 'taskId',
      key: 'taskId',
      width: 240,
      render: (t: any, r: any) => {
        return (
          <span>
            {t}
            {
              <Tag
                style={{
                  background: stateEnum[r?.state]?.bgColor,
                  color: stateEnum[r?.state]?.color,
                  padding: '3px 6px',
                  marginLeft: '5px',
                }}
              >
                {Utils.firstCharUppercase(r?.state as string)}
              </Tag>
            }
          </span>
        );
      },
    },
    {
      title: 'Worker',
      dataIndex: 'workerId',
      key: 'workerId',
      width: 240,
    },
    {
      title: '错误原因',
      dataIndex: 'trace',
      key: 'trace',
      width: 400,
      needTooltip: true,
      lineClampOne: true,
    },
    {
      title: '操作',
      dataIndex: 'role',
      key: 'role',
      width: 100,
      render: (_t: any, r: any) => {
        return (
          <div>
            {global.hasPermission(ClustersPermissionMap.CONNECTOR_RESTART) ? (
              <Popconfirm
                title="是否重试当前任务？"
                onConfirm={() => arg?.retryOption(r.taskId)}
                // onCancel={cancel}
                okText="是"
                cancelText="否"
                overlayClassName="connect-popconfirm"
              >
                <a>重试</a>
              </Popconfirm>
            ) : (
              <></>
            )}
          </div>
        );
      },
    },
  ];
  return columns;
};
