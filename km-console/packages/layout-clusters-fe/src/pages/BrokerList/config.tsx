/* eslint-disable @typescript-eslint/explicit-module-boundary-types */
import React from 'react';
import { timeFormat, getSizeAndUnit } from '../../constants/common';
import moment from 'moment';
import { Tag, Tooltip } from 'knowdesign';
import { IconFont } from '@knowdesign/icons';

export const getBrokerListColumns = (arg?: any) => {
  const columns = [
    {
      title: 'Broker ID',
      dataIndex: 'brokerId',
      key: 'brokerId',
      sorter: true,
      // eslint-disable-next-line react/display-name
      render: (t: number, r: any) => {
        return r?.alive ? (
          <>
            <a
              onClick={() => {
                window.location.hash = `brokerId=${t || t === 0 ? t : ''}&host=${r.host || ''}`;
              }}
            >
              {t}
            </a>
            {r?.kafkaRoleList?.includes('controller') && (
              <Tag
                style={{
                  color: '#556EE6',
                  padding: '2px 5px',
                  background: '#eff1fd',
                  marginLeft: '4px',
                  transform: 'scale(0.83,0.83)',
                }}
              >
                Controller
              </Tag>
            )}
          </>
        ) : (
          <>
            <span>{t}</span>
            {r?.kafkaRoleList?.includes('controller') && (
              <Tag
                style={{
                  color: '#556EE6',
                  padding: '2px 5px',
                  background: '#eff1fd',
                  marginLeft: '4px',
                  transform: 'scale(0.83,0.83)',
                }}
              >
                Controller
              </Tag>
            )}
          </>
        );
      },
      fixed: 'left',
      width: 150,
    },
    // {
    //   title: 'Rack',
    //   dataIndex: 'rack',
    //   key: 'rack',
    // },
    {
      title: 'Broker Host',
      dataIndex: 'host',
      key: 'host',
      width: 180,
      render: (t: any, r: any) => {
        return (
          <span>
            {t}
            {r?.alive ? <Tag className="tag-success">Live</Tag> : <Tag className="tag-error">Down</Tag>}
          </span>
        );
      },
    },
    {
      title: ' JMX Port',
      dataIndex: 'jmxPort',
      key: 'jmxPort',
      width: 100,
      render: (t: string, r: any) => {
        return (
          <span>
            <IconFont type={r?.jmxConnected ? 'icon-zhengchang' : 'icon-yichang'} />
            <span style={{ marginLeft: 4 }}>{t}</span>
          </span>
        );
      },
    },
    {
      title: 'Partitions',
      dataIndex: 'Partitions',
      key: 'Partitions',
      width: 100,
    },
    {
      title: 'Partitions Skew',
      dataIndex: 'PartitionsSkew',
      key: 'PartitionsSkew',
      width: 140,
      render: (t: number) => {
        return t || t === 0 ? (t * 100).toFixed(2) + '%' : '-';
      },
    },
    {
      title: 'Leaders Partition',
      dataIndex: 'Leaders',
      key: 'Leaders',
      width: 140,
    },
    {
      title: 'Leaders Skew',
      dataIndex: 'LeadersSkew',
      key: 'LeadersSkew',
      width: 120,
      render: (t: number) => {
        return t || t === 0 ? (t * 100).toFixed(2) + '%' : '-';
      },
    },
    {
      title: 'Message Size',
      dataIndex: 'LogSize',
      key: 'LogSize',
      sorter: true,
      width: 150,
      render: (t: number) => {
        return getSizeAndUnit(t, 'B').valueWithUnit;
      },
    },
    // {
    //   title: 'Status',
    //   dataIndex: 'alive',
    //   key: 'alive',
    //   width: 100,
    //   // eslint-disable-next-line react/display-name
    //   render: (t: boolean) => {
    //     return <span className={t ? 'success' : 'error'}>{t ? 'Live' : 'Down'}</span>;
    //   },
    // },
    {
      title: 'Roles',
      dataIndex: 'kafkaRoleList',
      key: 'kafkaRoleList',
      width: 100,
      // eslint-disable-next-line react/display-name
      render: (t: any[]) => {
        if (t) {
          const newContant = t
            ?.filter((item: string) => item.slice(0, 3) == '__c' || item.slice(0, 3) == '__t')
            ?.map((subItem: any) => {
              if (subItem.slice(0, 3) == '__c') {
                return 'GC';
              } else if (subItem.slice(0, 3) == '__t') {
                return 'TC';
              }
              return null;
            });
          const newTooltip = newContant.map((item: string) => {
            if (item === 'GC') {
              return 'GroupCoordinator';
            } else if (item === 'TC') {
              return 'TransactionCoordinator';
            }
            return null;
          });
          return (
            <Tooltip title={newTooltip.join()}>
              <span>{newContant.length > 0 ? newContant.join('、') : '-'}</span>
            </Tooltip>
          );
        }
        return '-';
      },
    },
    {
      title: 'Start Time',
      dataIndex: 'startTimeUnitMs',
      key: 'startTimeUnitMs',
      width: 200,
      render: (t: number) => (t ? moment(t).format(timeFormat) : '-'),
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
