import React, { useEffect, useState } from 'react';
import { Button, Form, Input, Select, ProTable, DatePicker, Utils, Tooltip, Divider } from 'knowdesign';
import { IconFont } from '@knowdesign/icons';
import api from '@src/api';
import { defaultPagination } from '@src/constants/common';
import TypicalListCard from '../../components/TypicalListCard';
import './index.less';
import moment from 'moment';

const { request } = Utils;
const { RangePicker } = DatePicker;

export default () => {
  const [loading, setLoading] = useState(true);
  const [configGroupList, setConfigGroupList] = useState<{ label: string; value: string }[]>([]);
  const [data, setData] = useState([]);
  const [pagination, setPagination] = useState<any>(defaultPagination);
  const [form] = Form.useForm();

  const columns = [
    {
      title: '模块',
      dataIndex: 'targetType',
      lineClampOne: true,
    },
    {
      title: '操作对象',
      dataIndex: 'target',
      width: 350,
      lineClampOne: true,
    },
    {
      title: '行为',
      dataIndex: 'operateType',
      width: 80,
      lineClampOne: true,
    },
    {
      title: '操作内容',
      dataIndex: 'detail',
      width: 350,
      lineClampOne: true,
      render(content) {
        return (
          <Tooltip placement="bottomLeft" title={content}>
            {content}
          </Tooltip>
        );
      },
    },
    {
      title: '操作时间',
      dataIndex: 'updateTime',
      width: 200,
      render: (date) => moment(date).format('YYYY-MM-DD HH:mm:ss'),
    },
    {
      title: '操作人',
      dataIndex: 'operator',
    },
  ];

  const getData = (query = {}) => {
    const formData = form.getFieldsValue();
    if (formData.time) {
      formData.startTime = moment(formData.time[0]).valueOf();
      formData.endTime = moment(formData.time[1]).valueOf();
    }
    delete formData.time;
    const data = {
      page: pagination.current,
      size: pagination.pageSize,
      ...formData,
      ...query,
    };

    setLoading(true);
    request(api.oplogList, {
      method: 'POST',
      data,
    }).then(
      (res: any) => {
        const { pageNo, pageSize, pages, total } = res.pagination;
        if (pageNo > pages && pages !== 0) {
          getData({ page: pages });
          return false;
        }

        setPagination({
          ...pagination,
          current: pageNo,
          pageSize,
          total,
        });
        setData(res.bizData);
        setLoading(false);
      },
      () => setLoading(false)
    );
  };

  const onTableChange = (curPagination) => {
    getData({ page: curPagination.current, size: curPagination.pageSize });
  };

  useEffect(() => {
    // 获取模块列表
    request(api.oplogTypeList).then((res: string[]) => {
      const options = res.map((item) => ({
        label: item,
        value: item,
      }));
      setConfigGroupList(options);
    });
    // 获取数据
    getData();
  }, []);

  return (
    <>
      <TypicalListCard title="操作记录">
        <div className="operate-bar">
          <div className="left">
            <div className="refresh-icon" onClick={() => getData()}>
              <IconFont className="icon" type="icon-shuaxin1" />
            </div>
            <Divider type="vertical" style={{ height: 20, top: 0 }} />

            <Form form={form} layout="inline" onFinish={() => getData({ page: 1 })}>
              <Form.Item name="targetType">
                <Select placeholder="请选择模块" options={configGroupList} style={{ width: 160 }} />
              </Form.Item>
              <Form.Item name="target">
                <Input placeholder="请输入操作对象" />
              </Form.Item>
              <Form.Item name="detail">
                <Input placeholder="请输入操作内容" />
              </Form.Item>
              <Form.Item name="time">
                <RangePicker showTime />
              </Form.Item>
              <Form.Item>
                <Button type="primary" ghost htmlType="submit">
                  查询
                </Button>
              </Form.Item>
            </Form>
          </div>
        </div>

        <ProTable
          tableProps={{
            showHeader: false,
            loading,
            rowKey: 'id',
            dataSource: data,
            paginationProps: pagination,
            columns,
            lineFillColor: true,
            attrs: {
              onChange: onTableChange,
              scroll: {
                scrollToFirstRowOnChange: true,
                x: true,
                y: 'calc(100vh - 270px)',
              },
            },
          }}
        />
      </TypicalListCard>
    </>
  );
};
