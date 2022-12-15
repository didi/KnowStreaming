import React, { useState, useEffect, memo } from 'react';
import { useParams, useHistory, useLocation } from 'react-router-dom';
import { ProTable, Drawer, Utils, AppContainer, Form, Select, Input, Button, Modal, Divider } from 'knowdesign';
import message from '@src/components/Message';
import { IconFont } from '@knowdesign/icons';
import API from '../../api';
import { getJobsListColumns, defaultPagination, runningStatus, jobType } from './config';
import JobsCheck from '@src/components/CardBar/JobsCheck';
import DBreadcrumb from 'knowdesign/es/extend/d-breadcrumb';
import { ViewJobsProgress } from './ViewJobsProgress';
import './index.less';
import ReplicaChange from '@src/components/TopicJob/ReplicaChange';
import ReplicaMove from '@src/components/TopicJob/ReplicaMove';
import BalanceDrawer from '../LoadRebalance/BalanceDrawer';
import { tableHeaderPrefix } from '@src/constants/common';
const { request } = Utils;

const JobsList: React.FC = (props: any) => {
  const [global] = AppContainer.useGlobalValue();
  const [form] = Form.useForm();
  const urlParams = useParams<any>(); // 获取地址栏参数
  const history = useHistory();
  const [loading, setLoading] = useState(false);
  const [data, setData] = useState([]);
  const [searchWords, setSearchWords] = useState<{ type: any; jobTarget: any; status: any }>();
  // const [filteredInfo, setFilteredInfo] = useState(null);
  const [pagination, setPagination] = useState<any>(defaultPagination);
  const [viewVisble, setViewVisble] = useState<any>(false);
  const [record, setRecord] = useState(null); // 获取当前点击行的数据；
  const [changeVisible, setChangeVisible] = useState(false);
  const [moveVisible, setMoveVisible] = useState(false);
  const [balanceVisible, setBalanceVisible] = useState(false);
  const [balanceFromData, setBalanceFormData] = useState(false);

  // 默认排序
  const defaultSorter = {
    sortField: 'brokerId',
    sortType: 'asc',
  };

  // 请求接口获取数据
  const genData = async ({ pageNo, pageSize }: any) => {
    if (urlParams?.clusterId === undefined) return;
    // filters = filters || filteredInfo;
    setLoading(true);
    const params = {
      ...searchWords,
      pageNo,
      pageSize,
      type: searchWords?.type === 0 || searchWords?.type ? searchWords?.type : -1,
    };

    request(API.getJobsList(urlParams?.clusterId), { method: 'POST', data: params })
      .then((res: any) => {
        setPagination({
          current: res.pagination?.pageNo,
          pageSize: res.pagination?.pageSize,
          total: res.pagination?.total,
        });
        setData(res?.bizData || []);
        setLoading(false);
      })
      .catch((err) => {
        setData([]);
        setLoading(false);
      });
  };

  // 获取立即均衡formData
  const getBalanceFormData = (jobId: any, clusterId: any) => {
    const params = {
      clusterId,
      jobId,
    };
    return request(API.getJobsTaskData(params.clusterId, params.jobId), params);
  };

  // 查询
  const onFinish = (formData: any) => {
    setSearchWords(formData);
  };

  const onTableChange = (pagination: any, filters: any, sorter: any) => {
    // setFilteredInfo(filters);
    genData({ pageNo: pagination.current, pageSize: pagination.pageSize, filters, sorter });
  };

  const getSearchKeywords = (value: string) => {
    // setSearchKeywords(value);
  };

  // 删除modal
  const onDelete = (record: any) => {
    Modal.confirm({
      title: `确认删除"任务【${record.id}】"吗？`,
      okText: '删除',
      okType: 'primary',
      centered: true,
      cancelText: '取消',
      okButtonProps: {
        // disabled: record.status === 1,
        size: 'small',
        danger: true,
      },
      cancelButtonProps: {
        size: 'small',
      },
      maskClosable: false,
      onOk(close) {
        // 缺少判断当前任务是否是doing的状态
        return Utils.delete(API.getJobsDelete(urlParams?.clusterId, record?.id)).then((_) => {
          message.success('删除任务成功');
          // getConfigList();
          genData({
            pageNo: 1,
            pageSize: pagination.pageSize,
          });
          close();
        });
      },
    });
  };

  const setViewProgress = (record: any, visibleType?: number) => {
    setRecord(record);
    if (visibleType === 0) {
      setMoveVisible(true);
    } else if (visibleType === 1) {
      setChangeVisible(true);
    } else if (visibleType === 2) {
      getBalanceFormData(record.id, urlParams.clusterId).then((res: any) => {
        const jobData = (res && JSON.parse(res?.jobData)) || {};
        setBalanceFormData({
          ...jobData,
          jobId: record.id,
          record,
          jobData,
        });
        setBalanceVisible(true);
      });
    } else {
      setViewVisble(true);
    }
  };

  const onclose = () => {
    setChangeVisible(false);
    setMoveVisible(false);
    setBalanceVisible(false);
    setRecord(null);
  };

  useEffect(() => {
    genData({
      pageNo: 1,
      pageSize: pagination.pageSize,
      // sorter: defaultSorter
    });
  }, [searchWords]);

  return (
    <div key="brokerList">
      <div className="breadcrumb">
        <DBreadcrumb
          breadcrumbs={[
            { label: '多集群管理', aHref: '/' },
            { label: global?.clusterInfo?.name, aHref: `/cluster/${global?.clusterInfo?.id}` },
            { label: 'Job', aHref: `` },
          ]}
        />
      </div>
      <div style={{ margin: '12px 0' }}>
        <JobsCheck />
      </div>
      {/* <Form form={form} layout="inline" onFinish={onFinish}> */}
      <div className="custom-table-content jobs-self">
        <div className={tableHeaderPrefix}>
          <div className={`${tableHeaderPrefix}-left`}>
            <div
              className={`${tableHeaderPrefix}-left-refresh`}
              onClick={() => genData({ pageNo: pagination.current, pageSize: pagination.pageSize })}
            >
              <IconFont className={`${tableHeaderPrefix}-left-refresh-icon`} type="icon-shuaxin1" />
            </div>
            <Divider type="vertical" className={`${tableHeaderPrefix}-divider`} />
            <Form form={form} layout="inline" onFinish={onFinish}>
              <Form.Item name="type">
                <Select options={jobType} style={{ width: '190px' }} className={'detail-table-select'} placeholder="选择任务类型" />
              </Form.Item>
              <Form.Item name="jobTarget">
                <Input allowClear style={{ width: '190px' }} placeholder="请输入执行任务对象" />
              </Form.Item>
              <Form.Item name="status">
                <Select
                  mode="multiple"
                  maxTagCount={'responsive'}
                  options={runningStatus}
                  style={{ width: '190px' }}
                  className={'detail-table-select'}
                  placeholder="选择运行状态"
                  showArrow
                  allowClear
                />
              </Form.Item>
            </Form>
            <div>
              <Form style={{ justifyContent: 'flex-end' }} form={form} layout="inline" onFinish={onFinish}>
                <Form.Item style={{ marginRight: 0 }}>
                  <Button type="primary" ghost htmlType="submit">
                    查询
                  </Button>
                </Form.Item>
              </Form>
            </div>
          </div>
        </div>
        {/* </Form> */}
        <ProTable
          key="brokerTable"
          showQueryForm={false}
          tableProps={{
            tableId: 'jobs_list',
            showHeader: false,
            rowKey: 'id',
            loading: loading,
            columns: getJobsListColumns({ onDelete, setViewProgress }),
            dataSource: data,
            paginationProps: { ...pagination },
            attrs: {
              // className: 'frameless-table', // 纯无边框表格类名
              onChange: onTableChange,
              scroll: { x: 'max-content', y: 'calc(100vh - 400px)' }, // calc(100vh - 270px)
              bordered: false,
            },
          }}
        />
      </div>
      {viewVisble && <ViewJobsProgress visible={viewVisble} setVisble={setViewVisble} record={record} />}
      {/* 批量扩缩副本 */}
      {changeVisible && (
        <ReplicaChange
          drawerVisible={changeVisible}
          jobId={record?.id}
          jobStatus={record?.jobStatus}
          topics={record?.target?.split(',') || []}
          onClose={onclose}
          genData={() =>
            genData({
              pageNo: 1,
              pageSize: pagination.pageSize,
              // sorter: defaultSorter
            })
          }
        ></ReplicaChange>
      )}
      {/* 批量迁移 */}
      {moveVisible && (
        <ReplicaMove
          drawerVisible={moveVisible}
          jobId={record?.id}
          jobStatus={record?.jobStatus}
          topics={record?.target?.split(',') || []}
          onClose={onclose}
          genData={() =>
            genData({
              pageNo: 1,
              pageSize: pagination.pageSize,
              // sorter: defaultSorter
            })
          }
        ></ReplicaMove>
      )}
      {/* 立即均衡 */}
      {balanceVisible && (
        <BalanceDrawer
          visible={balanceVisible}
          formData={balanceFromData}
          onClose={onclose}
          genData={() =>
            genData({
              pageNo: 1,
              pageSize: pagination.pageSize,
            })
          }
        />
      )}
    </div>
  );
};

export default JobsList;
