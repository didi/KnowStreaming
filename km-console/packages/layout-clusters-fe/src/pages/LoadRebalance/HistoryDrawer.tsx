import React, { useState, useEffect } from 'react';
import { Utils, Drawer, Button, ProTable, Space, Divider, AppContainer } from 'knowdesign';
import { CloseOutlined } from '@ant-design/icons';
import moment from 'moment';
import api from '../../api';
import { defaultPagination } from './index';
import PlanDrawer from './PlanDrawer';
import { timeFormat } from '../../constants/common';

interface PropsType extends React.HTMLAttributes<HTMLDivElement> {
  onClose: () => void;
  visible: boolean;
}

const HistoryDrawer: React.FC<PropsType> = ({ onClose, visible }) => {
  const [global] = AppContainer.useGlobalValue();
  const [loading, setLoading] = useState<boolean>(true);
  const [pagination, setPagination] = useState<any>(defaultPagination);
  const [data, setData] = useState([]);
  const [planDetailData, setPlanDetailData] = useState({});
  const [planVisible, setPlanVisible] = useState<boolean>(false);

  useEffect(() => {
    getList();
  }, []);

  const columns = () => [
    {
      title: '执行时间',
      dataIndex: 'begin',
      key: 'begin',
      render: (t: number) => (t ? moment(t).format(timeFormat) : '-'),
    },
    {
      title: '结束时间',
      dataIndex: 'end',
      key: 'end',
      render: (t: number) => (t ? moment(t).format(timeFormat) : '-'),
    },
    // {
    //   title: 'CPU均衡率',
    //   dataIndex: 'cpu',
    //   render: (text: any, row: any) => {
    //     return `${row?.sub?.cpu?.successNu} (已均衡) / ${row?.sub?.cpu?.failedNu} (未均衡)`
    //   }
    // },
    {
      title: (
        <span>
          Disk<span style={{ fontSize: '12px', color: '#74788D' }}>{'(已均衡丨未均衡)'}</span>
        </span>
      ),
      dataIndex: 'disk',
      render: (text: any, row: any) => {
        // return `${row?.sub?.disk?.successNu} 丨 ${row?.sub?.disk?.failedNu}`;
        return (
          <div className="balance-history-column">
            <span>{row?.sub?.disk?.successNu}</span>
            <span>丨</span>
            <span>{row?.sub?.disk?.failedNu}</span>
          </div>
        );
      },
    },
    {
      title: (
        <span>
          BytesIn<span style={{ fontSize: '12px', color: '#74788D' }}>{'(已均衡丨未均衡)'}</span>
        </span>
      ),
      dataIndex: 'bytesIn',
      render: (text: any, row: any) => {
        // return `${row?.sub?.bytesIn?.successNu} 丨 ${row?.sub?.bytesIn?.failedNu}`;
        return (
          <div className="balance-history-column">
            <span>{row?.sub?.bytesIn?.successNu}</span>
            <span>丨</span>
            <span>{row?.sub?.bytesIn?.failedNu}</span>
          </div>
        );
      },
    },
    {
      title: (
        <span>
          BytesOut<span style={{ fontSize: '12px', color: '#74788D' }}>{'(已均衡丨未均衡)'}</span>
        </span>
      ),
      dataIndex: 'bytesOut',
      render: (text: any, row: any) => {
        // return `${row?.sub?.bytesOut?.successNu} 丨 ${row?.sub?.bytesOut?.failedNu}`;
        return (
          <div className="balance-history-column">
            <span>{row?.sub?.bytesOut?.successNu}</span>
            <span>丨</span>
            <span>{row?.sub?.bytesOut?.failedNu}</span>
          </div>
        );
      },
    },
    {
      title: '操作',
      width: 150,
      fixed: 'right',
      render: (text: any, row: any) => {
        return (
          <Button type="link" size="small" onClick={() => CheckDetail(row.jobId)}>
            查看详情
          </Button>
        );
      },
    },
  ];

  const getList = (query = {}) => {
    const queryParams = {
      pageNo: pagination.current,
      pageSize: pagination.pageSize,
      ...query,
    };

    setLoading(true);
    Utils.request(api.getBalanceHistory(global?.clusterInfo?.id), {
      method: 'POST',
      data: queryParams,
    }).then(
      (res: any) => {
        const { pageNo, pageSize, pages, total } = res.pagination;

        setPagination({
          ...pagination,
          current: pageNo,
          pageSize,
          total,
        });
        const dataDe = res?.bizData || [];
        const dataHandle = dataDe.map((item: any) => {
          return {
            ...item,
          };
        });
        setData(dataHandle);
        setLoading(false);
      },
      () => setLoading(false)
    );
  };

  const CheckDetail = (jobId: any) => {
    Utils.request(api.getBalancePlan(global?.clusterInfo?.id, jobId), {
      method: 'GET',
    }).then((res: any) => {
      const dataDe = res || {};
      setPlanDetailData(dataDe);
      setPlanVisible(true);
    });
  };

  const onTableChange = (curPagination: any) => {
    getList({ pageNo: curPagination.current, pageSize: curPagination.pageSize });
  };

  return (
    <>
      <PlanDrawer visible={planVisible} onClose={() => setPlanVisible(false)} detailData={planDetailData} isPrevew={false} />
      <Drawer
        title={'均衡历史'}
        width="1080px"
        destroyOnClose={true}
        className="plan-drawer"
        onClose={onClose}
        visible={visible}
        closable={false}
        maskClosable={false}
        extra={
          <Space>
            <Button type="text" size="small" icon={<CloseOutlined />} onClick={onClose} />
          </Space>
        }
      >
        <ProTable
          tableProps={{
            showHeader: false,
            loading,
            rowKey: 'jobId',
            dataSource: data,
            paginationProps: pagination,
            columns: columns() as any,
            lineFillColor: true,
            attrs: {
              onChange: onTableChange,
              scroll: {
                x: 'max-content',
              },
            },
          }}
        />
      </Drawer>
    </>
  );
};

export default HistoryDrawer;
