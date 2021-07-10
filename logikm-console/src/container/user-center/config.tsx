import { IOrderInfo, ILabelValue, IBillDetail } from 'types/base-type';
import { clusterTypeMap, authStatusMap } from 'constants/status-map';
import * as React from 'react';
import { IBill } from 'types/base-type';
import { transBToMB } from 'lib/utils';

export const getInfoRenderItem = (orderInfo: IOrderInfo, result: boolean) => {
  // 纯粹为了适配后端字段

  const cluster = '集群名称';
  const appsInfoList: ILabelValue[] = [{
    label: '应用名称',
    value: orderInfo.detail.appName,
  }, {
    label: '应用ID',
    value: orderInfo.detail.appId,
  }, {
    label: '应用负责人',
    value: orderInfo.detail.appPrincipals,
  }];

  const appOfflineList: ILabelValue[] = [{
    label: '应用名称',
    value: orderInfo.detail.name,
  }, {
    label: '应用ID',
    value: orderInfo.detail.appId,
  }, {
    label: '应用负责人',
    value: orderInfo.detail.principals,
  }];

  const clusterTypelist: ILabelValue[] = [{
    label: '物理集群名称',
    value: orderInfo.detail.physicalClusterName,
  }, {
    label: '逻辑集群名称',
    value: orderInfo.detail.logicalClusterName,
  }];

  const appInfoList: ILabelValue[] = appOfflineList.concat([{
    label: '应用密码',
    value: orderInfo.detail.password,
  }]);

  const expansionList: ILabelValue[] = [{
    label: '集群名称',
    value: orderInfo.detail.logicalClusterName,
  }];

  const baseInfoList: ILabelValue[] = appsInfoList.concat(expansionList).concat([{
    label: 'Topic名称',
    value: orderInfo.detail.topicName,
  }]);

  const topicInfoList: ILabelValue[] = baseInfoList.concat([{
    label: '流量上限',
    value: `${transBToMB(orderInfo.detail.peakBytesIn)} MB/s`,
  }]);

  const phyTopicInfoList: ILabelValue[] = topicInfoList.filter(i => !cluster.includes(i.label));
  phyTopicInfoList.splice(3, 0, ...clusterTypelist);

  const phyTopicOfflineList: ILabelValue[] = baseInfoList.filter(i => !cluster.includes(i.label));
  phyTopicOfflineList.splice(3, 0, ...clusterTypelist);

  const topicOfflineList: ILabelValue[] = baseInfoList;

  const authInfoList: ILabelValue[] = baseInfoList.concat({
    label: '申请权限',
    value: authStatusMap[orderInfo.detail.access],
  });

  const authOfflineList: ILabelValue[] = baseInfoList.concat({
    label: '取消权限',
    value: authStatusMap[orderInfo.detail.access],
  });

  const phyAuthOfflineList: ILabelValue[] = authOfflineList.filter(i => !cluster.includes(i.label));
  phyAuthOfflineList.splice(3, 0, ...clusterTypelist);

  const clusterInfoList: ILabelValue[] = [{
    label: '流入流量',
    value: `${transBToMB(orderInfo.detail.bytesIn)} MB/s`,
  },
  // {
  //   label: '数据中心',
  //   value: orderInfo.detail.idc,
  // },
  {
    label: '集群类型',
    value: clusterTypeMap[orderInfo.detail.mode],
  }, {
    label: '应用ID',
    value: orderInfo.detail.appId,
  },
  ];

  const clusterOfflineList: ILabelValue[] = expansionList;
  const phyClusterOfflineList: ILabelValue[] = clusterTypelist;
  const maxAvgBytesIn = orderInfo.detail.maxAvgBytesInList && orderInfo.detail.maxAvgBytesInList.map(item => {
    const val = `${transBToMB(item)} MB/s`;
    return val;
  });

  const quotaInfoList: ILabelValue[] = baseInfoList.concat({
    label: '旧发送配额',
    value: `${transBToMB(orderInfo.detail.oldProduceQuota)} MB/s`,
  }, {
    label: '新发送配额',
    value: `${transBToMB(orderInfo.detail.produceQuota)} MB/s`,
  }, {
    label: '当前流入流量',
    value: `${transBToMB(orderInfo.detail.bytesIn)} MB/s`,
  }, {
    label: '旧消费配额',
    value: `${transBToMB(orderInfo.detail.oldConsumeQuota)} MB/s`,
  }, {
    label: '新消费配额',
    value: `${transBToMB(orderInfo.detail.consumeQuota)} MB/s`,
  }, {
    label: '近三天峰值流入流量',
    value: maxAvgBytesIn && maxAvgBytesIn.join('、'),
  });

  const phyQuotaInfoList: ILabelValue[] = quotaInfoList.filter(i => !cluster.includes(i.label));
  phyQuotaInfoList.splice(3, 0, ...clusterTypelist);

  const partitionList: ILabelValue[] = expansionList.concat([{
    label: 'Topic名称',
    value: orderInfo.detail.topicName,
  }, {
    label: '申请分区数',
    value: orderInfo.detail.needIncrPartitionNum,
  }, {
    label: '当前流入流量',
    value: `${transBToMB(orderInfo.detail.bytesIn)} MB/s`,
  }, {
    label: '近三天峰值流入流量',
    value: maxAvgBytesIn && maxAvgBytesIn.join('、'),
  },
  ]);

  const phyPartitionList: ILabelValue[] = partitionList.filter(i => !cluster.includes(i.label));
  phyPartitionList.splice(3, 0, ...clusterTypelist);

  if (orderInfo.type === 0) {
    return result ? phyTopicInfoList : topicInfoList;
  }

  if (orderInfo.type === 10) {
    return result ? phyTopicOfflineList : topicOfflineList;
  }

  if (orderInfo.type === 1) {
    return appInfoList;
  }

  if (orderInfo.type === 11) {
    return appOfflineList;
  }

  if (orderInfo.type === 2) {
    return result ? phyQuotaInfoList : quotaInfoList;
  }

  if (orderInfo.type === 12) {
    return result ? phyPartitionList : partitionList;
  }

  if (orderInfo.type === 3) {
    return authInfoList;
  }

  if (orderInfo.type === 13) {
    return result ? phyAuthOfflineList : authOfflineList;
  }

  if (orderInfo.type === 4) {
    return clusterInfoList;
  }

  if (orderInfo.type === 14) {
    return result ? phyClusterOfflineList : clusterOfflineList;
  }

  if (orderInfo.type === 5 || orderInfo.type === 15) {
    return result ? clusterTypelist : expansionList;
  }
};

export const billDetailCols = [
  {
    title: '集群ID',
    dataIndex: 'clusterId',
    key: 'clusterId',
  }, {
    title: '集群名称',
    dataIndex: 'clusterName',
    key: 'clusterName',
  }, {
    title: 'Quota（MB/s）',
    dataIndex: 'quota',
    key: 'quota',
    sorter: (a: IBillDetail, b: IBillDetail) => b.quota - a.quota,
  }, {
    title: 'Topic名称',
    dataIndex: 'topicName',
    key: 'topicName',
  }, {
    title: '金额（元）',
    dataIndex: 'cost',
    key: 'cost',
    sorter: (a: IBillDetail, b: IBillDetail) => b.cost - a.cost,
  },
];

export const getBillListColumns = (urlFront: string) => {
  const columns = [
    {
      title: '月份',
      dataIndex: 'gmtMonth',
      key: 'gmtMonth',
      sorter: (a: IBill, b: IBill) => b.timestamp - a.timestamp,
      render: (text: string, record: IBill) => {
        return (
          <a href={`${urlFront}?timestamp=${record.timestamp}`}>{text}</a>);
      },
    }, {
      title: 'Topic数量',
      dataIndex: 'topicNum',
      key: 'topicNum',
    }, {
      title: 'Quota（MB/s）',
      dataIndex: 'quota',
      key: 'quota',
      sorter: (a: IBill, b: IBill) => b.quota - a.quota,
    }, {
      title: '金额（元）',
      dataIndex: 'cost',
      key: 'cost',
      sorter: (a: IBill, b: IBill) => b.cost - a.cost,
    },
  ];

  return columns;
};
