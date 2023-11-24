import React, { useState, useEffect, memo } from 'react';
import { useParams } from 'react-router-dom';
import { ProTable, Utils, AppContainer, SearchInput, Divider, Form, Input, Button } from 'knowdesign';
import { IconFont } from '@knowdesign/icons';
import API from '../../api';
import { getGroupColumns, defaultPagination } from './config';
import { tableHeaderPrefix } from '@src/constants/common';
import BrokerDetail from '../BrokerDetail';
import DBreadcrumb from 'knowdesign/es/extend/d-breadcrumb';
import ConsumerGroupHealthCheck from '@src/components/CardBar/ConsumerGroupHealthCheck';
import GroupDetail from './Detail';
import './index.less';

const { request } = Utils;

const BrokerList: React.FC = (props: any) => {
  const [global] = AppContainer.useGlobalValue();
  const [form] = Form.useForm();
  const urlParams = useParams<any>(); // 获取地址栏参数
  const [loading, setLoading] = useState(false);
  const [data, setData] = useState([]);
  const [searchKeywords, setSearchKeywords] = useState('');
  const [pagination, setPagination] = useState<any>(defaultPagination);
  const [searchWords, setSearchWords] = useState<{ groupName: string; topicName: string }>({ groupName: '', topicName: '' });
  // 请求接口获取数据
  const genData = async ({ pageNo, pageSize }: any) => {
    if (urlParams?.clusterId === undefined) return;

    setLoading(true);
    const params = {
      searchGroupName: searchWords.groupName ? searchWords.groupName.slice(0, 128) : undefined,
      searchTopicName: searchWords.topicName ? searchWords.topicName.slice(0, 128) : undefined,
      pageNo,
      pageSize,
    };

    request(API.getOperatingStateList(urlParams?.clusterId), { params })
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
        setLoading(false);
      });
  };

  // 查询
  const onFinish = (formData: any) => {
    setSearchWords(formData);
  };

  const onTableChange = (pagination: any, filters: any, sorter: any) => {
    genData({ pageNo: pagination.current, pageSize: pagination.pageSize, filters, sorter });
  };

  // 删除Group
  const deleteTesk = () => {
    genData({ pageNo: 1, pageSize: pagination.pageSize });
  };

  useEffect(() => {
    genData({
      pageNo: 1,
      pageSize: pagination.pageSize,
    });
  }, [searchWords]);

  return (
    <div key="groupList" className="groupList">
      <div className="breadcrumb" style={{ marginBottom: '10px' }}>
        <DBreadcrumb
          breadcrumbs={[
            { label: '多集群管理', aHref: '/' },
            { label: global?.clusterInfo?.name, aHref: `/cluster/${global?.clusterInfo?.id}` },
            { label: 'Consumers', aHref: `` },
          ]}
        />
      </div>
      <div style={{ margin: '12px 0' }}>
        <ConsumerGroupHealthCheck />
      </div>
      <div className="custom-table-content">
        <div className={tableHeaderPrefix}>
          <div className={`${tableHeaderPrefix}-left`}>
            <div
              className={`${tableHeaderPrefix}-left-refresh`}
              onClick={() => genData({ pageNo: pagination.current, pageSize: pagination.pageSize })}
            >
              <IconFont className={`${tableHeaderPrefix}-left-refresh-icon`} type="icon-shuaxin1" />
            </div>
            <Divider type="vertical" className={`${tableHeaderPrefix}-divider`} />
            <Form form={form} layout="inline">
              <Form.Item name="groupName">
                <Input allowClear style={{ width: '190px' }} placeholder="请输入Consumer Group" />
              </Form.Item>
              <Form.Item name="topicName">
                <Input allowClear style={{ width: '190px' }} placeholder="请输入Topic name" />
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
        <ProTable
          key="groupTable"
          showQueryForm={false}
          tableProps={{
            showHeader: false,
            rowKey: 'group_list',
            loading: loading,
            columns: getGroupColumns(deleteTesk),
            dataSource: data,
            paginationProps: { ...pagination },
            attrs: {
              onChange: onTableChange,
              scroll: { y: 'calc(100vh - 400px)' },
              bordered: false,
            },
          }}
        />
      </div>
      {<GroupDetail />}
    </div>
  );
};

export default BrokerList;
