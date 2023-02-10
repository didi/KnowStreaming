import React, { useState, useEffect } from 'react';
import { AppContainer, ProTable, Utils, Tag, Modal, Tooltip } from 'knowdesign';
import Api from '@src/api';
import { useParams } from 'react-router-dom';
import { getDataUnit } from '@src/constants/chartConfig';
import message from '@src/components/Message';
import { ClustersPermissionMap } from '../CommonConfig';
import { ControlStatusMap } from '../CommonRoute';
const { request } = Utils;

const getColmns = (arg: any) => {
  const formattedBytes = (v: number) => {
    const [unit, size] = getDataUnit['Memory'](v);
    return `${(v / size).toFixed(2)}${unit}/s`;
  };
  const tagEle = (
    <Tag
      style={{
        color: '#5664FF',
        padding: '2px 5px',
        background: '#eff1fd',
        marginLeft: '-4px',
        transform: 'scale(0.83,0.83)',
      }}
    >
      当前集群
    </Tag>
  );
  const baseColumns: any = [
    {
      title: '源集群',
      dataIndex: 'sourceClusterName',
      key: 'sourceClusterName',
      render: (t: string, record: any) => (
        <>
          <span>{t || '-'}</span>
          {record.sourceClusterId == arg.clusterId && tagEle}
        </>
      ),
    },
    {
      title: '目标集群',
      dataIndex: 'destClusterName',
      key: 'destClusterName',
      render: (t: string, record: any) => (
        <>
          <span>{t || '-'}</span>
          {record.destClusterId == arg.clusterId && tagEle}
        </>
      ),
    },
    {
      title: '消息写入速率',
      dataIndex: 'bytesIn',
      key: 'bytesIn',
      width: 150,
      render: (t: number) => (t !== null && t !== undefined ? formattedBytes(t) : '-'),
    },
    {
      title: '消息复制速率',
      dataIndex: 'replicationBytesIn',
      key: 'replicationBytesIn',
      width: 150,
      render: (t: number) => (t !== null && t !== undefined ? formattedBytes(t) : '-'),
    },
    {
      title: '延迟（个消息）',
      dataIndex: 'lag',
      key: 'lag',
      width: 150,
    },
    {
      title: '操作',
      dataIndex: 'option',
      key: 'option',
      width: 100,
      render: (_t: any, r: any) => {
        return arg.global.hasPermission(ClustersPermissionMap.TOPIC_CANCEL_REPLICATOR) ? (
          <a onClick={() => arg.cancelSync(r)}>取消同步</a>
        ) : (
          '-'
        );
      },
    },
  ];

  return baseColumns;
};

const Replicator = (props: any) => {
  const { hashData } = props;
  const urlParams = useParams<any>(); // 获取地址栏参数
  const [global] = AppContainer.useGlobalValue();
  const [loading, setLoading] = useState(false);
  const [data, setData] = useState([]);
  const [pagination, setPagination] = useState<any>({
    current: 1,
    pageSize: 10,
    position: 'bottomRight',
    showSizeChanger: true,
    pageSizeOptions: ['10', '20', '50', '100', '200', '500'],
  });

  const genData = () => {
    if (urlParams?.clusterId === undefined || hashData?.topicName === undefined) return;
    setLoading(true);
    request(Api.getTopicMirrorList(urlParams?.clusterId, hashData?.topicName))
      .then((res: any = []) => {
        setData(res);
      })
      .finally(() => setLoading(false));
  };

  const cancelSync = (item: any) => {
    Modal.confirm({
      title: `确认取消此Topic同步吗？`,
      okType: 'primary',
      centered: true,
      okButtonProps: {
        size: 'small',
        danger: true,
      },
      cancelButtonProps: {
        size: 'small',
      },
      maskClosable: false,
      onOk(close) {
        close();
        const data = [
          {
            destClusterPhyId: item.destClusterId,
            sourceClusterPhyId: item.sourceClusterId,
            topicName: item.topicName,
          },
        ];
        Utils.delete(Api.handleTopicMirror(), { data }).then(() => {
          message.success('成功取消Topic同步');
          genData();
        });
      },
    });
  };

  const onTableChange = (pagination: any, filters: any, sorter: any, extra: any) => {
    setPagination(pagination);
  };

  useEffect(() => {
    props.positionType === 'Replicator' && genData();
  }, []);

  return (
    <div className="pro-table-wrap">
      <ProTable
        showQueryForm={false}
        tableProps={{
          loading,
          showHeader: false,
          rowKey: 'unique',
          columns: getColmns({ global, cancelSync, clusterId: urlParams?.clusterId }),
          dataSource: data,
          paginationProps: pagination,
          attrs: {
            onChange: onTableChange,
            scroll: { y: 'calc(100vh - 400px)' },
          },
        }}
      />
    </div>
  );
};

export default Replicator;
