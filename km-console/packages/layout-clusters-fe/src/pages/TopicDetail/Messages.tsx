import React, { useState, useEffect } from 'react';
import { Alert, Button, Checkbox, Form, Input, ProTable, Select, Tooltip, Utils } from 'knowdesign';
import { IconFont } from '@knowdesign/icons';
import Api from '@src/api';
import { useParams, useHistory } from 'react-router-dom';
import { getTopicMessagesColmns } from './config';

const { request } = Utils;
const defaultParams: any = {
  truncate: true,
  maxRecords: 100,
  pullTimeoutUnitMs: 5000,
  // filterPartitionId: 1,
  filterOffsetReset: 0,
};
const defaultpaPagination = {
  current: 1,
  pageSize: 10,
  position: 'bottomRight',
  showSizeChanger: true,
  pageSizeOptions: ['10', '20', '50', '100'],
};
const TopicMessages = (props: any) => {
  const { hashData } = props;
  const urlParams = useParams<any>(); // 获取地址栏参数
  const history = useHistory();
  const [loading, setLoading] = useState(false);
  const [data, setData] = useState([]);
  const [params, setParams] = useState(defaultParams);
  const [partitionIdList, setPartitionIdList] = useState([]);
  const [pagination, setPagination] = useState<any>(defaultpaPagination);
  const [form] = Form.useForm();

  // 获取消息开始位置
  const offsetResetList = [
    { label: 'latest', value: 0 },
    { label: 'earliest', value: 1 },
  ];

  // 默认排序
  const defaultSorter = {
    sortField: 'timestampUnitMs',
    sortType: 'desc',
  };

  const [sorter, setSorter] = useState<any>(defaultSorter);

  // 请求接口获取数据
  const genData = async () => {
    if (urlParams?.clusterId === undefined || hashData?.topicName === undefined) return;
    setLoading(true);
    request(Api.getTopicMessagesMetadata(hashData?.topicName, urlParams?.clusterId)).then((res: any) => {
      // console.log(res, 'metadata');
      const newPartitionIdList = res?.partitionIdList.map((item: any) => {
        return {
          label: item,
          value: item,
        };
      });
      setPartitionIdList(newPartitionIdList || []);
    });
    request(Api.getTopicMessagesList(hashData?.topicName, urlParams?.clusterId), { data: { ...params, ...sorter }, method: 'POST' })
      .then((res: any) => {
        // setPagination({
        //   current: res.pagination?.pageNo,
        //   pageSize: res.pagination?.pageSize,
        //   total: res.pagination?.total,
        // });
        setData(res || []);
        setLoading(false);
      })
      .catch((err) => {
        setLoading(false);
      });
  };

  // 查询
  const onFinish = (formData: any) => {
    setParams({ ...params, ...formData, filterKey: formData?.filterKey ? formData?.filterKey : undefined });
  };

  // 截断
  const checkBoxChange = (e: any) => {
    setParams({ ...params, truncate: e.target.checked });
  };

  // 刷新
  const refreshClick = () => {
    // genData();
    // form.resetFields();
    setPagination(defaultpaPagination);
    genData();
  };

  // 跳转Consume
  const jumpConsume = () => {
    history.push(`/cluster/${urlParams?.clusterId}/testing/consumer`);
  };

  const onTableChange = (pagination: any, filters: any, sorter: any, extra: any) => {
    setPagination(pagination);
    // 只有排序事件时，触发重新请求后端数据
    if (extra.action === 'sort') {
      setSorter({
        sortField: sorter.field || '',
        sortType: sorter.order ? sorter.order.substring(0, sorter.order.indexOf('end')) : '',
      });
    }
    // const asc = sorter?.order && sorter?.order === 'ascend' ? true : false;
    // const sortColumn = sorter.field && toLine(sorter.field);
    // genData({ pageNo: pagination.current, pageSize: pagination.pageSize, filters, asc, sortColumn, queryTerm: searchResult, ...allParams });
  };

  useEffect(() => {
    props.positionType === 'Messages' && genData();
  }, [props, params, sorter]);

  return (
    <>
      <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
        <div>
          <span
            style={{ display: 'inline-block', padding: '0 10px', marginRight: '10px', borderRight: '1px solid #ccc', fontSize: '13px' }}
            onClick={refreshClick}
          >
            <i className="iconfont icon-shuaxin1" style={{ fontSize: '13px', cursor: 'pointer' }} />
          </span>
          <span style={{ fontSize: '13px' }}>
            <Checkbox checked={params.truncate} onChange={checkBoxChange}>
              是否要截断数据
            </Checkbox>
          </span>
          <Tooltip title={'截断数据后只展示前1024字符的数据'}>
            <IconFont style={{ fontSize: '14px' }} type="icon-zhushi" />
          </Tooltip>
        </div>
        <div className="messages-query">
          <Form form={form} layout="inline" onFinish={onFinish}>
            <Form.Item name="filterOffsetReset">
              <Select
                options={offsetResetList}
                size="small"
                style={{ width: '120px' }}
                className={'detail-table-select'}
                placeholder="请选择offset"
              />
            </Form.Item>
            <Form.Item name="filterPartitionId">
              <Select
                options={partitionIdList}
                size="small"
                style={{ width: '140px' }}
                className={'detail-table-select'}
                placeholder="请选择分区"
              />
            </Form.Item>
            <Form.Item name="filterKey">
              <Input size="small" style={{ width: '140px' }} className={'detail-table-input'} placeholder="请输入key" />
            </Form.Item>
            <Form.Item name="filterValue">
              <Input size="small" style={{ width: '140px' }} className={'detail-table-input'} placeholder="请输入value" />
            </Form.Item>
            <Form.Item>
              <Button size="small" type="primary" ghost htmlType="submit">
                查询
              </Button>
            </Form.Item>
          </Form>
        </div>
      </div>
      <div>
        <Alert
          style={{ margin: '12px 0 4px', padding: '7px 12px', background: '#FFF9E6' }}
          message={
            <div>
              此处展示 Topic 的 100 条 messages。
              {process.env.BUSINESS_VERSION ? (
                <span>
                  若想获取其他 messages，可前往 <a onClick={jumpConsume}>Produce&Consume</a> 进行操作
                </span>
              ) : (
                ''
              )}
            </div>
          }
          type="warning"
          closable
        />
      </div>
      <ProTable
        showQueryForm={false}
        tableProps={{
          showHeader: false,
          rowKey: 'offset',
          loading: loading,
          columns: getTopicMessagesColmns(),
          dataSource: data,
          paginationProps: pagination,
          // noPagination: true,
          attrs: {
            // className: 'frameless-table', // 纯无边框表格类名
            bordered: false,
            onChange: onTableChange,
            scroll: { x: 'max-content' },
            sortDirections: ['descend', 'ascend', 'default'],
          },
        }}
      />
    </>
  );
};

export default TopicMessages;
