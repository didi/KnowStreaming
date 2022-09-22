import React, { useState, useEffect } from 'react';
import { Badge, Descriptions, ProTable, Utils, Spin, Tag } from 'knowdesign';
import { useParams } from 'react-router-dom';
import Api from '@src/api';
import { getSizeAndUnit } from '@src/constants/common';
import TagsWithHide from '@src/components/TagsWithHide';
interface PropsType {
  jobId?: any;
  balanceData?: any;
  status?: any;
  [name: string]: any;
  // [clasgfag: any]: any;
}

const typeObj: any = {
  1: '立即均衡',
  2: '周期均衡',
};

const { request, post } = Utils;

const RebalancePlan = (props: PropsType) => {
  const { jobId, balanceData, status } = props;
  const urlParams = useParams<any>(); // 获取地址栏参数
  const [loading, setLoading] = useState(false);

  const [data, setData] = useState<any>({});
  const [pagination, setPagination] = useState<any>({
    current: 1,
    pageSize: 10,
    position: 'bottomRight',
    showSizeChanger: true,
    pageSizeOptions: ['10', '20', '50', '100', '200', '500'],
    // hideOnSinglePage: false,
    // showTotal: (total: number) => `共 ${total} 条目`,
  });

  // 获取
  const genData = async ({ pageNo, pageSize }: any) => {
    if (urlParams?.clusterId === undefined) return;
    setLoading(true);
    if (balanceData) {
      setData(balanceData);
      setLoading(false);
      return;
    }
    setData([]);
    request(Api.getJobsPlanRebalance(urlParams?.clusterId, jobId))
      .then((res: any) => {
        // setPagination({
        //   current: res.pagination?.pageNo,
        //   pageSize: res.pagination?.pageSize,
        //   total: res.pagination?.total,
        // });

        // setData(mockData);
        setData(res || []);
        setLoading(false);
      })
      .catch((err) => {
        setLoading(false);
      });
  };

  const onTableChange = (pagination: any, filters: any, sorter: any) => {
    setPagination(pagination);
    // const asc = sorter?.order && sorter?.order === 'ascend' ? true : false;
    // const sortColumn = sorter.field && toLine(sorter.field);
    // genData({ pageNo: pagination.current, pageSize: pagination.pageSize });
  };

  useEffect(() => {
    genData({
      pageNo: 1,
      pageSize: pagination.pageSize,
      // sorter: defaultSorter
    });
  }, []);

  const columns: any = [
    {
      title: '节点',
      dataIndex: 'host',
      key: 'host',
      fixed: 'left',
      render: (t: any, r: any) => {
        return (
          <span>
            {t}
            <Badge style={{ marginLeft: '4px' }} status={r?.status === 0 ? 'success' : 'error'} />
          </span>
        );
      },
    },
    {
      title: (
        <div>
          Disk使用率
          <span className="balance-list-title">
            {`(`}当前
            <span className="titleImg"></span>
            均衡后{`)`}
          </span>
        </div>
      ),
      dataIndex: 'diskBefore',
      key: 'diskBefore',
      width: 140,
      render: (t: any, r: any) => {
        const befaore = r?.diskBefore || r?.diskBefore === 0 ? r?.diskBefore.toFixed(2) + '%' : '0%';
        const after = r?.diskAfter || r?.diskAfter === 0 ? r?.diskAfter.toFixed(2) + '%' : '0%';
        return (
          <span className="balance-list-contert">
            <span className="balance-list-contert-text">{befaore}</span>
            <span className="balance-list-contert-img"></span>
            <span className="balance-list-contert-text-right">{after}</span>
          </span>
        );
      },
    },
    {
      title: (
        <div>
          BytesIn使用率
          <span className="balance-list-title">
            {`(`}当前
            <span className="titleImg"></span>
            均衡后{`)`}
          </span>
        </div>
      ),
      dataIndex: 'byteInBefore',
      key: 'byteInBefore',
      width: 140,
      render: (t: any, r: any) => {
        const befaore = r?.byteInBefore || r?.byteInBefore === 0 ? r?.byteInBefore.toFixed(2) + '%' : '0%';
        const after = r?.byteInAfter || r?.byteInAfter === 0 ? r?.byteInAfter.toFixed(2) + '%' : '0%';
        return (
          <span className="balance-list-contert">
            <span className="balance-list-contert-text">{befaore}</span>
            <span className="balance-list-contert-img"></span>
            <span className="balance-list-contert-text-right">{after}</span>
          </span>
        );
      },
    },
    {
      title: (
        <div>
          BytesOut使用率
          <span className="balance-list-title">
            {`(`}当前
            <span className="titleImg"></span>
            均衡后{`)`}
          </span>
        </div>
      ),
      dataIndex: 'byteOutBefore',
      key: 'byteOutBefore',
      width: 140,
      render: (t: any, r: any) => {
        const befaore = r?.byteOutBefore || r?.byteOutBefore === 0 ? r?.byteOutBefore.toFixed(2) + '%' : '0%';
        const after = r?.byteOutAfter || r?.byteOutAfter === 0 ? r?.byteOutAfter.toFixed(2) + '%' : '0%';
        return (
          <span className="balance-list-contert">
            <span className="balance-list-contert-text">{befaore}</span>
            <span className="balance-list-contert-img"></span>
            <span className="balance-list-contert-text-right">{after}</span>
          </span>
        );
      },
    },
    {
      title: '移入情况',
      dataIndex: 'inReplica',
      key: 'inReplica',
      render: (t: any, r: any) => {
        return (t || t === 0 ? t : 0) + '/' + (r?.inSize || r?.inSize === 0 ? getSizeAndUnit(r?.inSize, 'B', 0).valueWithUnit : 0);
      },
    },
    {
      title: '移出情况',
      dataIndex: 'outReplica',
      key: 'outReplica',
      render: (t: any, r: any) => {
        return (t || t === 0 ? t : 0) + '/' + (r?.outSize || r?.outSize === 0 ? getSizeAndUnit(r?.outSize, 'B', 0).valueWithUnit : 0);
      },
    },
  ];

  const mockdata: any = [];
  for (let i = 0; i < 100; i++) {
    mockdata.push({
      key: i,
      name: 'John Brown',
      age: i + 1,
      street: 'Lake Park',
      building: 'C',
      number: 2035,
      companyAddress: 'Lake Street 42',
      companyName: 'SoftLake Co',
      gender: 'M',
    });
  }

  // const download= (url:any, name:any)=>{
  //   const a:any = document.createElement(\'a\')
  //   a.download = name
  //   a.rel = \'noopener\'
  //   a.href = url
  //   // 触发模拟点击
  //   // a.dispatchEvent(new MouseEvent(\'click\'))
  //   // 或者模拟点击
  //   a.click()
  // }

  return (
    <>
      <h3 style={{ fontSize: '16px' }}>计划概览</h3>
      <Spin spinning={loading}>
        <Descriptions
          style={{ fontSize: '13px' }}
          column={3}
          labelStyle={{
            display: 'flex',
            textAlign: 'right',
            justifyContent: 'end',
            color: '#74788D',
            fontSize: '13px',
          }}
          contentStyle={{ fontSize: '13px' }}
        >
          <Descriptions.Item label="任务类型">{typeObj[data?.type] || '-'}</Descriptions.Item>
          <Descriptions.Item labelStyle={{ width: '100px' }} label="总迁移大小">
            {Utils.getSizeAndUnit(data?.moveSize, 'B').valueWithUnit}
          </Descriptions.Item>
          <Descriptions.Item labelStyle={{ width: '100px' }} label="迁移副本数">
            {data?.replicas || '-'}
          </Descriptions.Item>
          <Descriptions.Item label="均衡区间">
            {data?.clusterBalanceIntervalList
              ? data?.clusterBalanceIntervalList?.map((item: any) => {
                  return (
                    <Tag style={{ padding: '4px 5px', backgroundColor: 'rgba(33,37,41,0.08)', marginRight: '4px' }} key={item?.priority}>
                      {item.type?.slice(0, 1).toUpperCase() + item.type?.slice(1) + ':' + ' ±' + item.intervalPercent + '%'}
                    </Tag>
                  );
                })
              : '-'}
          </Descriptions.Item>
          <Descriptions.Item labelStyle={{ width: '100px' }} label="Topic黑名单">
            {data?.blackTopics && data?.blackTopics?.length > 0 ? (
              <TagsWithHide placement="bottomLeft" list={data?.blackTopics} expandTagContent={(num: any) => `共有${num}个`} />
            ) : (
              '-'
            )}
          </Descriptions.Item>
        </Descriptions>
      </Spin>
      <h3 style={{ fontSize: '16px' }}>计划明细</h3>
      <div style={{ maxWidth: '1032px' }}>
        {/* <Table columns={columns} dataSource={mockdata} bordered scroll={{ x: 'max-content' }}></Table> */}
        <ProTable
          tableProps={{
            showHeader: false,
            loading,
            rowKey: 'key',
            dataSource: data?.detail,
            paginationProps: pagination,
            columns,
            lineFillColor: true,
            attrs: {
              onChange: onTableChange,
              scroll: { x: 'max-content' },
              // className: 'remove-last-border',
            },
          }}
        />
      </div>
      {data?.reassignmentJson && (
        <>
          <h3 style={{ fontSize: '16px' }}>执行文件</h3>
          <div>
            <a href={`data:,${data?.reassignmentJson}`} rel="noopener" download="reassignment.json">
              Reassignment json file（点击下载）
            </a>
          </div>
        </>
      )}
    </>
  );
};

export default RebalancePlan;
