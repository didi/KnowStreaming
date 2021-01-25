import * as React from 'react';
import { clusterTypeMap } from 'constants/status-map';
import { notification, Tooltip, Modal, Table, message, Icon } from 'component/antd';
import { IClusterData } from 'types/base-type';
import { showCpacityModal } from 'container/modal';
import moment = require('moment');
import { cellStyle } from 'constants/table';
import { timeFormat } from 'constants/strategy';
import { modal } from 'store/modal';

const { confirm } = Modal;

export const getClusterColumns = (urlPrefix: string) => {
  return [
    {
      title: '集群ID',
      dataIndex: 'clusterId',
      key: 'clusterId',
      width: '9%',
      sorter: (a: IClusterData, b: IClusterData) => b.clusterId - a.clusterId,
    },
    {
      title: '集群名称',
      dataIndex: 'clusterName',
      key: 'clusterName',
      width: '13%',
      onCell: () => ({
        style: {
          maxWidth: 120,
          ...cellStyle,
        },
      }),
      sorter: (a: IClusterData, b: IClusterData) => a.clusterName.charCodeAt(0) - b.clusterName.charCodeAt(0),
      render: (text: string, record: IClusterData) => (
        <Tooltip placement="bottomLeft" title={text} >
          <a href={`${urlPrefix}/cluster/cluster-detail?clusterId=${record.clusterId}`}> {text} </a>
        </Tooltip>
      ),
    },
    // {
    //   title: '逻辑集群英文名称',
    //   dataIndex: 'clusterName',
    //   key: 'clusterName',
    //   width: '13%',
    //   onCell: () => ({
    //     style: {
    //       maxWidth: 120,
    //       ...cellStyle,
    //     },
    //   }),
    //   sorter: (a: IClusterData, b: IClusterData) => a.clusterName.charCodeAt(0) - b.clusterName.charCodeAt(0),
    //   render: (text: string, record: IClusterData) => (
    //     <Tooltip placement="bottomLeft" title={text} >
    //       <a href={`${urlPrefix}/cluster/cluster-detail?clusterId=${record.clusterId}`}> {text} </a>
    //     </Tooltip>
    //   ),
    // },
    {
      title: 'Topic数量',
      dataIndex: 'topicNum',
      key: 'topicNum',
      width: '9%',
      sorter: (a: IClusterData, b: IClusterData) => b.topicNum - a.topicNum,
    },
    {
      title: '集群类型',
      dataIndex: 'mode',
      key: 'mode',
      width: '9%',
      render: (text: number) => (clusterTypeMap[text] || ''),
    },
    {
      title: '集群版本',
      dataIndex: 'clusterVersion',
      key: 'clusterVersion',
      width: '9%',
      onCell: () => ({
        style: {
          maxWidth: 200,
          ...cellStyle,
        },
      }),
      render: (text: string) => <Tooltip placement="bottomLeft" title={text} >{text}</Tooltip>,
    }, {
      title: '接入时间',
      dataIndex: 'gmtCreate',
      key: 'gmtCreate',
      width: '13%',
      sorter: (a: IClusterData, b: IClusterData) => b.gmtCreate - a.gmtCreate,
      render: (t: number) => moment(t).format(timeFormat),
    }, {
      title: '修改时间',
      dataIndex: 'gmtModify',
      key: 'gmtModify',
      width: '13%',
      sorter: (a: IClusterData, b: IClusterData) => b.gmtModify - a.gmtModify,
      render: (t: number) => moment(t).format(timeFormat),
    },
    {
      title: '操作',
      dataIndex: 'action',
      key: 'action',
      width: '20%',
      render: (val: string, record: IClusterData) => (
        <>
          {
            record.mode !== 0 ? <>
              <a onClick={() => showConfirm(record)} className="action-button">申请下线</a>
              <a onClick={() => showCpacityModal(record)}>扩缩容</a>
            </> : null
          }
        </>
      ),
    },
  ];
};

export const showConfirm = (record: IClusterData) => {
  modal.showOfflineClusterModal(record.clusterId);
};
