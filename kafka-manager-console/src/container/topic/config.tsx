import * as React from 'react';
import { IBtn, ITopic, IQuotaQuery, IConnectionInfo } from 'types/base-type';
import { showAllPermissionModal, showPermissionModal, showTopicEditModal, showApplyQuatoModal, applyExpandModal, showTopicApplyQuatoModal } from 'container/modal';
import { applyOnlineModal } from 'container/modal';
import { Tooltip } from 'component/antd';
import { topic } from 'store/topic';
import { region } from 'store/region';
import { cellStyle } from 'constants/table';
import { MoreBtns } from 'container/custom-component';
import { app } from 'store/app';
import './index.less';

/**
 * 0: 无权限      申请权限
 * 1: 可消费      申请配额，只能申请消费配额，	申请权限（申请发送）
 * 2: 可发送      申请配额，只能申请发送配额，	申请权限（申请消费）
 * 3: 可消费发送   申请配额，可以申请发送及消费配额
 * 4: 可管理      可编辑，可以申请发送及消费配额
 */

export const renderMyTopicOperation = (record: ITopic) => {
  const twoBtns = getTopicBtn(record).splice(0, 2);
  const leftBtns = getTopicBtn(record).splice(2);
  return (
    <>
      <span className="table-operation">
        {twoBtns.map((item, index) => (
          item.clickFunc ? <a type="javascript;" key={index} onClick={() => item.clickFunc(record)}>{item.label}</a> :
            <span key={index} className="mr-10">{item.label}</span>
        ))}
        {getTopicBtn(record).length > 2 && <MoreBtns btns={leftBtns} data={record} />}
      </span>
    </>);
};

const getTopicBtn = (record: ITopic) => {
  const btnList: IBtn[] = [{
    label: '申请权限',
    show: [0, 1, 2].indexOf(record.access) > -1,
    clickFunc: (item: ITopic) => {
      showPermissionModal(item);
    },
  }, {
    label: '申请配额',
    show: [1, 2, 3, 4].indexOf(record.access) > -1,
    clickFunc: (item: ITopic) => {
      applyQuotaQuery(item);
    },
  }, {
    label: '申请分区',
    show: true,
    clickFunc: (item: ITopic) => {
      applyExpandModal(item);
    },
  }, {
    label: '申请下线',
    show: record.access === 4,
    clickFunc: (item: ITopic) => {
      applyOnlineModal(item);
    },
  }, {
    label: '编辑',
    show: record.access === 4,
    clickFunc: (item: ITopic) => {
      showTopicEditModal(item);
    },
  }];
  const origin = btnList.filter((item: IBtn) => item.show);
  return origin;
};

export const renderAllTopicOperation = (item: ITopic) => {
  return (
    <>
      {item.needAuth && <a className="mr-10" onClick={() => showAllPermissionModal(item)}>申请权限</a>}
    </>
  );
};

export const applyQuotaQuery = (item: ITopic) => {
  topic.getQuotaQuery(item.appId, item.clusterId, item.topicName).then((data) => {
    const record = data && data.length ? data[0] : {} as IQuotaQuery;
    showApplyQuatoModal(item, record);
  });
};

export const applyTopicQuotaQuery = async (item: ITopic) => {
  await app.getTopicAppQuota(item.clusterId, item.topicName);
  await showTopicApplyQuatoModal(item);
};

export const getOnlineColumns = () => {
  const columns = [
    {
      title: 'AppID',
      dataIndex: 'appId',
      key: 'appId',
    },
    {
      title: 'HostName',
      dataIndex: 'hostname',
      key: 'hostname',
      onCell: () => ({
        style: {
          maxWidth: 120,
          ...cellStyle,
        },
      }),
      render: (text: string) => {
        return (
          <Tooltip placement="bottomLeft" title={text} >
            {text}
          </Tooltip>);
      },
    },
    {
      title: 'ClientType',
      dataIndex: 'clientType',
      key: 'clientType',
    },
  ];
  return columns;
};

export const getAllTopicColumns = (urlPrefix: string) => {
  const columns = [
    {
      title: 'Topic名称',
      dataIndex: 'topicName',
      key: 'topicName',
      width: '25%',
      onCell: () => ({
        style: {
          maxWidth: 250,
          ...cellStyle,
        },
      }),
      sorter: (a: ITopic, b: ITopic) => a.topicName.charCodeAt(0) - b.topicName.charCodeAt(0),
      render: (text: string, record: ITopic) => {
        return (
          <Tooltip placement="bottomLeft" title={record.topicName} >
            <a
              // tslint:disable-next-line:max-line-length
              href={`${urlPrefix}/topic/topic-detail?clusterId=${record.clusterId}&topic=${record.topicName}&region=${region.currentRegion}&needAuth=${record.needAuth}`}
            >{text}</a>
          </Tooltip>);
      },
    }, {
      title: '集群名称',
      dataIndex: 'clusterName',
      key: 'clusterName',
      width: '20%',
    }, {
      title: 'Topic描述',
      dataIndex: 'description',
      key: 'description',
      width: '25%',
      onCell: () => ({
        style: {
          maxWidth: 250,
          ...cellStyle,
        },
      }),
      render: (text: string) => <Tooltip placement="bottomLeft" title={text} >{text}</Tooltip>,
    }, {
      title: '负责人',
      dataIndex: 'appPrincipals',
      key: 'appPrincipals',
      width: '20%',
      onCell: () => ({
        style: {
          maxWidth: 100,
          ...cellStyle,
        },
      }),
      render: (text: string) => <Tooltip placement="bottomLeft" title={text} >{text}</Tooltip>,
    }, {
      title: '操作',
      dataIndex: 'operation',
      key: 'operation',
      width: '10%',
      render: (text: string, item: ITopic) => (
        renderAllTopicOperation(item)
      ),
    },
  ];
  return columns;
};

export const getExpireColumns = (urlPrefix: string) => {
  return [
    {
      title: 'Topic名称',
      dataIndex: 'topicName',
      key: 'topicName',
      width: '35%',
      sorter: (a: ITopic, b: ITopic) => a.topicName.charCodeAt(0) - b.topicName.charCodeAt(0),
      render: (t: string, r: ITopic) => {
        return (
          <Tooltip placement="bottomLeft" title={r.topicName} >
            <a
              // tslint:disable-next-line:max-line-length
              href={`${urlPrefix}/topic/topic-detail?clusterId=${r.clusterId}&topic=${r.topicName}&region=${region.currentRegion}`}
            >
              {t}
            </a>
          </Tooltip>);
      },
    },
    {
      title: '所属集群',
      dataIndex: 'clusterName',
      key: 'clusterName',
      width: '20%',
    },
    {
      title: '关联应用',
      dataIndex: 'appName',
      key: 'appName',
      width: '20%',
    },
    {
      title: '消费者个数',
      dataIndex: 'fetchConnectionNum',
      key: 'fetchConnectionNum',
      width: '10%',
    },
  ];
};

export const getMyTopicColumns = (urlPrefix: string) => {
  return [
    {
      title: 'Topic名称',
      dataIndex: 'topicName',
      key: 'topicName',
      width: '21%',
      sorter: (a: ITopic, b: ITopic) => a.topicName.charCodeAt(0) - b.topicName.charCodeAt(0),
      render: (t: string, r: ITopic) => {
        return (
          <Tooltip placement="bottomLeft" title={r.topicName} >
            <a
              // tslint:disable-next-line:max-line-length
              href={`${urlPrefix}/topic/topic-detail?clusterId=${r.clusterId}&topic=${r.topicName}&region=${region.currentRegion}`}
            >
              {t}
            </a>
          </Tooltip>);
      },
    }, {
      title: 'Bytes in(KB/s)',
      dataIndex: 'bytesIn',
      key: 'bytesIn',
      width: '12%',
      sorter: (a: ITopic, b: ITopic) => b.bytesIn - a.bytesIn,
      render: (t: number) => t === null ? '' : (t / 1024).toFixed(2),
    }, {
      title: 'Bytes out(KB/s)',
      dataIndex: 'bytesOut',
      key: 'bytesOut',
      width: '12%',
      sorter: (a: ITopic, b: ITopic) => b.bytesOut - a.bytesOut,
      render: (t: number) => t === null ? '' : (t / 1024).toFixed(2),
    }, {
      title: '所属集群',
      dataIndex: 'clusterName',
      key: 'clusterName',
      width: '15%',
    }, {
      title: '关联应用',
      dataIndex: 'appName',
      key: 'appName',
      width: '12%',
      render: (text: string, record: ITopic) => (
        <>
          <Tooltip placement="bottomLeft" title={record.appId} >
            {text}
          </Tooltip>
        </>
      ),
    },
    {
      title: '操作',
      dataIndex: 'action',
      key: 'action',
      width: '35%',
      render: (val: string, item: ITopic, index: number) => (
        renderMyTopicOperation(item)
      ),
    },
  ];
};

export const getApplyOnlineColumns = () => {
  const columns = [
    {
      title: 'AppID',
      dataIndex: 'appId',
      key: 'appId',
      width: '22%',
      sorter: (a: IConnectionInfo, b: IConnectionInfo) => a.appId.charCodeAt(0) - b.appId.charCodeAt(0),
    },
    {
      title: '主机名',
      dataIndex: 'hostname',
      key: 'hostname',
      width: '40%',
      onCell: () => ({
        style: {
          maxWidth: 250,
          ...cellStyle,
        },
      }),
      render: (t: string) => {
        return (
          <Tooltip placement="bottomLeft" title={t} >{t}</Tooltip>
        );
      },
    },
    {
      title: '客户端版本',
      dataIndex: 'clientVersion',
      key: 'clientVersion',
      width: '20%',
    },
    {
      title: '客户端类型',
      dataIndex: 'clientType',
      key: 'clientType',
      width: '18%',
      render: (t: string) => <span>{t === 'consumer' ? '消费' : '生产'}</span>,
    },
  ];
  return columns;
};
