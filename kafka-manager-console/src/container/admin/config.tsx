import * as React from 'react';
import { IUser, IUploadFile, IConfigure, IConfigGateway, IMetaData } from 'types/base-type';
import { users } from 'store/users';
import { version } from 'store/version';
import { showApplyModal, showApplyModalModifyPassword, showModifyModal, showConfigureModal, showConfigGatewayModal } from 'container/modal/admin';
import { Popconfirm, Tooltip } from 'component/antd';
import { admin } from 'store/admin';
import { cellStyle } from 'constants/table';
import { timeFormat } from 'constants/strategy';
import { urlPrefix } from 'constants/left-menu';
import moment = require('moment');

export const getUserColumns = () => {
  const columns = [
    {
      title: '用户名',
      dataIndex: 'username',
      key: 'username',
      width: '35%',
    },
    {
      title: '操作',
      dataIndex: 'operation',
      key: 'operation',
      width: '30%',
      render: (text: string, record: IUser) => {
        return (
          <span className="table-operation">
            <a onClick={() => showApplyModal(record)}>编辑</a>
            <a onClick={() => showApplyModalModifyPassword(record)}>修改密码</a>
            {record.username == users.currentUser.username ? "" :
                <Popconfirm
                  title="确定删除？"
                  onConfirm={() => users.deleteUser(record.username)}
                  cancelText="取消"
                  okText="确认"
                >
                  <a>删除</a>
                </Popconfirm>
            }
          </span>);
      },
    },
  ];
  return columns;
};

export const getVersionColumns = () => {
  const columns = [
    {
      title: 'ID',
      dataIndex: 'id',
      key: 'id',
    },
    {
      title: '文件名称',
      dataIndex: 'fileName',
      key: 'fileName',
      render: (text: string, record: IUploadFile) => {
        return (
          <Tooltip placement="topLeft" title={text} >
            <a href={`${window.origin}/api/v1/rd/kafka-files/${record.id}/config-files?dataCenter=cn`} target="_blank">{text}</a>
          </Tooltip>);
      },
    }, {
      title: 'MD5',
      dataIndex: 'fileMd5',
      key: 'fileMd5',
      onCell: () => ({
        style: {
          maxWidth: 120,
          ...cellStyle,
        },
      }),
      render: (text: string) => {
        return (
          <Tooltip placement="bottomLeft" title={text} >
            {text.substring(0, 8)}
          </Tooltip>);
      },
    }, {
      title: '更新时间',
      dataIndex: 'gmtModify',
      key: 'gmtModify',
      render: (t: number) => moment(t).format(timeFormat),
    }, {
      title: '更新人',
      dataIndex: 'operator',
      key: 'operator',
    }, {
      title: '备注',
      dataIndex: 'description',
      key: 'description',
      onCell: () => ({
        style: {
          maxWidth: 200,
          ...cellStyle,
        },
      }),
      render: (text: string) => {
        return (
          <Tooltip placement="topLeft" title={text} >
            {text}
          </Tooltip>);
      },
    }, {
      title: '操作',
      dataIndex: 'operation',
      key: 'operation',
      render: (text: string, record: IUploadFile) => {
        return (
          <span className="table-operation">
            <a onClick={() => showModifyModal(record)}>编辑</a>
            <Popconfirm
              title="确定删除？"
              onConfirm={() => version.deleteFile(record.id)}
              cancelText="取消"
              okText="确认"
            >
              <a>删除</a>
            </Popconfirm>
          </span>);
      },
    },
  ];
  return columns;
};

export const getConfigureColumns = () => {
  const columns = [
    {
      title: '配置键',
      dataIndex: 'configKey',
      key: 'configKey',
      width: '20%',
      sorter: (a: IConfigure, b: IConfigure) => a.configKey.charCodeAt(0) - b.configKey.charCodeAt(0),
    },
    {
      title: '配置值',
      dataIndex: 'configValue',
      key: 'configValue',
      width: '30%',
      sorter: (a: IConfigure, b: IConfigure) => a.configValue.charCodeAt(0) - b.configValue.charCodeAt(0),
      render: (t: string) => {
        return t.substr(0, 1) === '{' && t.substr(0, -1) === '}' ? JSON.stringify(JSON.parse(t), null, 4) : t;
      },
    },
    {
      title: '修改时间',
      dataIndex: 'gmtModify',
      key: 'gmtModify',
      width: '20%',
      sorter: (a: IConfigure, b: IConfigure) => b.gmtModify - a.gmtModify,
      render: (t: number) => moment(t).format(timeFormat),
    },
    {
      title: '描述信息',
      dataIndex: 'configDescription',
      key: 'configDescription',
      width: '20%',
      onCell: () => ({
        style: {
          maxWidth: 180,
          ...cellStyle,
        },
      }),
    },
    {
      title: '操作',
      width: '10%',
      render: (text: string, record: IConfigure) => {
        return (
          <span className="table-operation">
            <a onClick={() => showConfigureModal(record)}>编辑</a>
            <Popconfirm
              title="确定删除？"
              onConfirm={() => admin.deleteConfigure(record.configKey)}
              cancelText="取消"
              okText="确认"
            >
              <a>删除</a>
            </Popconfirm>
          </span>);
      },
    },
  ];
  return columns;
};

// 网关配置
export const getConfigColumns = () => {
  const columns = [
    {
      title: '配置类型',
      dataIndex: 'type',
      key: 'type',
      width: '25%',
      ellipsis: true,
      sorter: (a: IConfigGateway, b: IConfigGateway) => a.type.charCodeAt(0) - b.type.charCodeAt(0),
    },
    {
      title: '配置键',
      dataIndex: 'name',
      key: 'name',
      width: '15%',
      ellipsis: true,
      sorter: (a: IConfigGateway, b: IConfigGateway) => a.name.charCodeAt(0) - b.name.charCodeAt(0),
    },
    {
      title: '配置值',
      dataIndex: 'value',
      key: 'value',
      width: '20%',
      ellipsis: true,
      sorter: (a: IConfigGateway, b: IConfigGateway) => a.value.charCodeAt(0) - b.value.charCodeAt(0),
      render: (t: string) => {
        return t.substr(0, 1) === '{' && t.substr(0, -1) === '}' ? JSON.stringify(JSON.parse(t), null, 4) : t;
      },
    },
    {
      title: '修改时间',
      dataIndex: 'modifyTime',
      key: 'modifyTime',
      width: '15%',
      sorter: (a: IConfigGateway, b: IConfigGateway) => b.modifyTime - a.modifyTime,
      render: (t: number) => moment(t).format(timeFormat),
    },
    {
      title: '版本号',
      dataIndex: 'version',
      key: 'version',
      width: '10%',
      ellipsis: true,
      sorter: (a: IConfigGateway, b: IConfigGateway) => b.version.charCodeAt(0) - a.version.charCodeAt(0),
    },
    {
      title: '描述信息',
      dataIndex: 'description',
      key: 'description',
      width: '20%',
      ellipsis: true,
      onCell: () => ({
        style: {
          maxWidth: 180,
          ...cellStyle,
        },
      }),
    },
    {
      title: '操作',
      width: '10%',
      render: (text: string, record: IConfigGateway) => {
        return (
          <span className="table-operation">
            <a onClick={() => showConfigGatewayModal(record)}>编辑</a>
            <Popconfirm
              title="确定删除？"
              onConfirm={() => admin.deleteConfigGateway({ id: record.id })}
              cancelText="取消"
              okText="确认"
            >
              <a>删除</a>
            </Popconfirm>
          </span>);
      },
    },
  ];
  return columns;
};

const renderClusterHref = (value: number | string, item: IMetaData, key: number) => {
  return ( // 0 暂停监控--不可点击  1 监控中---可正常点击
    <>
      {item.status === 1 ? <a href={`${urlPrefix}/admin/cluster-detail?clusterId=${item.clusterId}#${key}`}>{value}</a>
        : <a style={{ cursor: 'not-allowed', color: '#999' }}>{value}</a>}
    </>
  );
};

export const getAdminClusterColumns = () => {
  return [
    {
      title: '物理集群ID',
      dataIndex: 'clusterId',
      key: 'clusterId',
      sorter: (a: IMetaData, b: IMetaData) => b.clusterId - a.clusterId,
    },
    {
      title: '物理集群名称',
      dataIndex: 'clusterName',
      key: 'clusterName',
      sorter: (a: IMetaData, b: IMetaData) => a.clusterName.charCodeAt(0) - b.clusterName.charCodeAt(0),
      render: (text: string, item: IMetaData) => renderClusterHref(text, item, 1),
    },
    {
      title: 'Topic数',
      dataIndex: 'topicNum',
      key: 'topicNum',
      sorter: (a: any, b: IMetaData) => b.topicNum - a.topicNum,
      render: (text: number, item: IMetaData) => renderClusterHref(text, item, 2),
    },
    {
      title: 'Broker数',
      dataIndex: 'brokerNum',
      key: 'brokerNum',
      sorter: (a: IMetaData, b: IMetaData) => b.brokerNum - a.brokerNum,
      render: (text: number, item: IMetaData) => renderClusterHref(text, item, 3),
    },
    {
      title: 'Consumer数',
      dataIndex: 'consumerGroupNum',
      key: 'consumerGroupNum',
      sorter: (a: IMetaData, b: IMetaData) => b.consumerGroupNum - a.consumerGroupNum,
      render: (text: number, item: IMetaData) => renderClusterHref(text, item, 4),
    },
    {
      title: 'Region数',
      dataIndex: 'regionNum',
      key: 'regionNum',
      sorter: (a: IMetaData, b: IMetaData) => b.regionNum - a.regionNum,
      render: (text: number, item: IMetaData) => renderClusterHref(text, item, 5),
    },
    {
      title: 'Controllerld',
      dataIndex: 'controllerId',
      key: 'controllerId',
      sorter: (a: IMetaData, b: IMetaData) => b.controllerId - a.controllerId,
      render: (text: number, item: IMetaData) => renderClusterHref(text, item, 7),
    },
    {
      title: '监控中',
      dataIndex: 'status',
      key: 'status',
      sorter: (a: IMetaData, b: IMetaData) => b.key - a.key,
      render: (value: number) => value === 1 ?
        <span className="success">是</span > : <span className="fail">否</span>,
    },
  ];
};

export const getPartitionInfoColumns = () => {
  return [{
    title: 'Topic',
    dataIndex: 'topicName',
    key: 'topicName',
    width: '21%',
    render: (val: string) => <Tooltip placement="bottomLeft" title={val}> {val} </Tooltip>,
  }, {
    title: 'Leader',
    dataIndex: 'leaderPartitionList',
    width: '20%',
    onCell: () => ({
      style: {
        maxWidth: 250,
        overflow: 'hidden',
        whiteSpace: 'nowrap',
        textOverflow: 'ellipsis',
        cursor: 'pointer',
      },
    }),
    render: (value: number[]) => {
      return (
        <Tooltip placement="bottomLeft" title={value.join('、')}>
          {value.map(i => <span key={i} className="p-params">{i}</span>)}
        </Tooltip>
      );
    },
  }, {
    title: '副本',
    dataIndex: 'followerPartitionIdList',
    width: '22%',
    onCell: () => ({
      style: {
        maxWidth: 250,
        overflow: 'hidden',
        whiteSpace: 'nowrap',
        textOverflow: 'ellipsis',
        cursor: 'pointer',
      },
    }),
    render: (value: number[]) => {
      return (
        <Tooltip placement="bottomLeft" title={value.join('、')}>
          {value.map(i => <span key={i} className="p-params">{i}</span>)}
        </Tooltip>
      );
    },
  }, {
    title: '未同步副本',
    dataIndex: 'notUnderReplicatedPartitionIdList',
    width: '22%',
    onCell: () => ({
      style: {
        maxWidth: 250,
        overflow: 'hidden',
        whiteSpace: 'nowrap',
        textOverflow: 'ellipsis',
        cursor: 'pointer',
      },
    }),
    render: (value: number[]) => {
      return (
        <Tooltip placement="bottomLeft" title={value.join('、')}>
          {value.map(i => <span key={i} className="p-params p-params-unFinished">{i}</span>)}
        </Tooltip>
      );
    },
  }];
};
