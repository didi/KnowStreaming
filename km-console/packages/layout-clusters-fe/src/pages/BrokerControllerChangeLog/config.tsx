import React from 'react';
import { timeFormat } from '../../constants/common';
import moment from 'moment';
import { getSizeAndUnit } from '../../constants/common';

export const getControllerChangeLogListColumns = (arg?: any) => {
  const columns = [
    {
      title: 'Change Time',
      dataIndex: 'timestamp',
      key: 'timestamp',
      render: (t: number) => (t ? moment(t).format(timeFormat) : '-'),
    },
    {
      title: 'Broker ID',
      dataIndex: 'brokerId',
      key: 'brokerId',
      // eslint-disable-next-line react/display-name
      render: (t: number, r: any) => {
        return t === -1 ? (
          '-'
        ) : (
          <a
            onClick={() => {
              window.location.hash = `brokerId=${t || ''}&host=${r.brokerHost || ''}`;
            }}
          >
            {t}
          </a>
        );
      },
    },
    {
      title: 'Broker Host',
      dataIndex: 'brokerHost',
      key: 'brokerHost',
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
