/* eslint-disable react/display-name */
import { Drawer, Spin, Table, Utils } from 'knowdesign';
import React, { useEffect, useState, forwardRef, useImperativeHandle } from 'react';
import { getDetailColumn } from './config';
import API from '../../api';
import { useParams } from 'react-router-dom';

const CheckDetail = forwardRef((props: any, ref): JSX.Element => {
  const [visible, setVisible] = useState(false);
  const [loading, setLoading] = useState(false);
  const [data, setData] = useState([]);
  const { clusterId } = useParams<{ clusterId: string }>();

  useImperativeHandle(ref, () => ({
    setVisible,
    getHealthDetail,
  }));

  const getHealthDetail = () => {
    setLoading(true);
    return Utils.request(API.getResourceListHealthDetail(+clusterId)).then((res: any) => {
      res.sort((a: any, b: any) => a.dimension - b.dimension);
      setData(res);
      setLoading(false);
    });
  };

  const onCancel = () => {
    setVisible(false);
  };

  useEffect(() => {
    if (visible) {
      getHealthDetail();
    }
  }, [visible]);

  return (
    <Drawer
      className="drawer-content"
      onClose={onCancel}
      maskClosable={false}
      title="Cluster健康状态详情"
      // title={intl.formatMessage({ id: 'check.detail' })}
      visible={visible}
      placement="right"
      width={1080}
    >
      <Spin spinning={loading}>
        <Table dataSource={data} columns={getDetailColumn(+clusterId)} pagination={false} />
      </Spin>
    </Drawer>
  );
});

export default CheckDetail;
