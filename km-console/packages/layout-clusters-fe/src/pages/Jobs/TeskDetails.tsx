/* eslint-disable react/display-name */
import React, { useState, useEffect } from 'react';
import { Alert, Badge, Dropdown, ProTable, Space, Table, Utils } from 'knowdesign';
import { IconFont } from '@knowdesign/icons';
import { useParams } from 'react-router-dom';
import Api from '@src/api';
import { getTaskDetailsColumns, getMoveBalanceColumns } from './config';
import { ExpandedRow } from './ExpandedRow';
const { request, post } = Utils;

const TaskDetails = (props: any) => {
  const { hashData } = props;
  const urlParams = useParams<any>(); // 获取地址栏参数
  const [loading, setLoading] = useState(false);
  const [data, setData] = useState([]);
  const [expandedData, setExpandedData] = useState([]);
  const [loadingObj, setLoadingObj] = useState<any>({});
  const [pagination, setPagination] = useState<any>({
    current: 1,
    pageSize: 10,
    position: 'bottomRight',
    showSizeChanger: true,
    pageSizeOptions: ['10', '20', '50', '100'],
  });

  // TODO 获取行详情数据接口
  const getRowDetailData = (topicName: string, key?: number) => {
    return post(Api.getJobPartitionDetail(urlParams?.clusterId, props?.jobId, topicName));
  };
  // TODO 获取行详情数据接口
  const queryExpandedData = async (record: any, key: any) => {
    if (urlParams?.clusterId === undefined) return;
    try {
      const table = { ...expandedData };
      const loading = { ...loadingObj };
      getRowDetailData(record.topicName).then((res) => {
        table[key] = res;
        loading[key] = false;
        setExpandedData(table);
        setLoadingObj(loading);
      });
    } catch (error) {
      console.log(error);
    }
  };

  const onClickExpand = (expanded: any, record: any) => {
    const key = record?.key;
    // 之前展开过
    if (expandedData[key]?.length) return;
    // 第一次展开
    const loading = { ...loadingObj };
    loading[key] = true;
    setLoadingObj(loading);
    queryExpandedData(record, key);
  };

  const onTableChange = (pagination: any, filters: any, sorter: any) => {
    setPagination(pagination);
    // const asc = sorter?.order && sorter?.order === 'ascend' ? true : false;
    // const sortColumn = sorter.field && toLine(sorter.field);
    // genData({ pageNo: pagination.current, pageSize: pagination.pageSize });
  };

  const newData =
    props?.detailData?.subJobs?.map((item: any, index: number) => {
      return {
        ...item,
        key: index,
      };
    }) || [];

  return (
    <>
      <Alert
        message={
          <div style={{ color: '#592D00', fontSize: '14px' }}>
            <span>执行情况：</span>
            <span>Topic总数 {newData?.length}</span>
            <span style={{ marginLeft: '34px', display: 'inline-block' }}>
              <Badge status="success" />
              Success {props?.detailData?.success}
            </span>
            <span style={{ marginLeft: '34px', display: 'inline-block' }}>
              <Badge status="error" />
              Fail {props?.detailData?.fail}
            </span>
            <span style={{ marginLeft: '34px', display: 'inline-block' }}>
              <Badge status="warning" />
              Doing {props?.detailData?.doing}
            </span>
          </div>
        }
        style={{ background: '#FFF9E6', padding: '6px 12px', border: 'none', marginBottom: '12px', borderRadius: '4px' }}
      />
      <div className="job-detail">
        <ProTable
          showQueryForm={false}
          tableProps={{
            showHeader: false,
            rowKey: 'key',
            loading: loading,
            columns: props?.detailData?.jobType === 1 ? getTaskDetailsColumns() : getMoveBalanceColumns(),
            dataSource: newData,
            paginationProps: { ...pagination },
            // noPagination: true,
            attrs: {
              bordered: false,
              onChange: onTableChange,
              tableLayout: 'auto',
              scroll: { x: 'max-content' },
              expandable: {
                expandedRowRender: (r: any) => <ExpandedRow record={r} data={expandedData} loading={loadingObj} />,
                // expandedRowRender,
                onExpand: onClickExpand,
                columnWidth: '20px',
                fixed: 'left',
                expandIcon: ({ expanded, onExpand, record }: any) => {
                  return expanded ? (
                    <IconFont
                      style={{ fontSize: '16px' }}
                      type="icon-xia"
                      onClick={(e: any) => {
                        onExpand(record, e);
                      }}
                    />
                  ) : (
                    <IconFont
                      style={{ fontSize: '16px' }}
                      type="icon-jiantou_1"
                      onClick={(e: any) => {
                        onExpand(record, e);
                      }}
                    />
                  );
                },
              },
              style: {
                width: '1032px',
              },
            },
          }}
        />
      </div>
    </>
  );
};

export default TaskDetails;
