import React, { useState, useEffect } from 'react';
import { Alert, Badge, Dropdown, ProTable, Space, Table, Utils } from 'knowdesign';
import { useParams } from 'react-router-dom';
import Api from '@src/api';
import { getNodeTrafficColumns } from './config';

const { request, post } = Utils;

const NodeTraffic = (props: any) => {
  const { hashData } = props;
  const urlParams = useParams<any>(); // 获取地址栏参数
  const [loading, setLoading] = useState(false);

  const [data, setData] = useState([]);
  const [pagination, setPagination] = useState<any>({
    current: 1,
    pageSize: 10,
    position: 'bottomRight',
    showSizeChanger: true,
    pageSizeOptions: ['10', '20', '50', '100', '200', '500'],
  });

  const [popoverVisible, setPopoverVisible] = useState(false);

  // 获取
  const genData = async ({ pageNo, pageSize }: any) => {
    if (urlParams?.clusterId === undefined) return;
    setData([]);
    setLoading(true);
    post(Api.getJobNodeTraffic(urlParams?.clusterId, props?.jobId))
      .then((res: any) => {
        // setPagination({
        //   current: res.pagination?.pageNo,
        //   pageSize: res.pagination?.pageSize,
        //   total: res.pagination?.total,
        // });
        const mockData = [];
        for (let i = 0; i < 20; i++) {
          mockData.push({
            byteInJob: 0,
            byteInTotal: 0,
            byteOutJob: 0,
            byteOutTotal: 0,
            createTime: 1645608135717,
            host: 'string',
            id: 0,
            updateTime: 1645608135717,
          });
        }
        setData(res || mockData);
        // setData(res?.bizData || []);
        setLoading(false);
      })
      .catch((err) => {
        setLoading(false);
      });
  };

  const onTableChange = (pagination: any, filters: any, sorter: any) => {
    // setPagination(pagination);
    // const asc = sorter?.order && sorter?.order === 'ascend' ? true : false;
    // const sortColumn = sorter.field && toLine(sorter.field);
    genData({ pageNo: pagination.current, pageSize: pagination.pageSize });
  };

  useEffect(() => {
    genData({
      pageNo: 1,
      pageSize: pagination.pageSize,
      // sorter: defaultSorter
    });
  }, []);

  return (
    <>
      <div style={{ maxWidth: '1032px' }}>
        <ProTable
          showQueryForm={false}
          tableProps={{
            showHeader: false,
            rowKey: 'key',
            loading: loading,
            columns: getNodeTrafficColumns({ popoverVisible, setPopoverVisible }),
            dataSource: data,
            paginationProps: { ...pagination },
            attrs: {
              bordered: false,
              // className: 'frameless-table', // 纯无边框表格类名
              onChange: onTableChange,
              size: 'middle',
            },
          }}
        />
      </div>
    </>
  );
};

export default NodeTraffic;
