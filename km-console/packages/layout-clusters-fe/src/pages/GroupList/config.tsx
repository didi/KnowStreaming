import React from 'react';
import { timeFormat } from '../../constants/common';
import moment from 'moment';
import { getSizeAndUnit } from '../../constants/common';

export const getGroupListColumns = (arg: any) => {
  const columns = [
    {
      title: 'ConsumerGroup',
      dataIndex: 'resourceName',
      key: 'resourceName',
    },
    {
      title: 'Operations',
      dataIndex: 'aclOperation',
      key: 'aclOperation',
    },
    {
      title: 'Permission Type',
      dataIndex: 'aclPermissionType',
      key: 'aclPermissionType',
    },
    {
      title: 'Pattern Type',
      dataIndex: 'resourcePatternType',
      key: 'resourcePatternType',
    },
    {
      title: 'Principle',
      dataIndex: 'kafkaUser',
      key: 'kafkaUser',
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
