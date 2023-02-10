import React, { useLayoutEffect, useState } from 'react';
import api from '@src/api';
import { Spin, Utils } from 'knowdesign';
import { useParams } from 'react-router-dom';
import NodataImg from '@src/assets/no-data.png';

interface Props {
  children: any;
}

const NoConnector = () => {
  return (
    <div
      style={{
        display: 'flex',
        flexDirection: 'column',
        justifyContent: 'center',
        alignItems: 'center',
        height: 'calc(100vh - 118px)',
        boxShadow: '0 2px 4px 0 rgba(0,0,0,0.01), 0 3px 6px 3px rgba(0,0,0,0.01), 0 2px 6px 0 rgba(0,0,0,0.03)',
        borderRadius: 12,
        background: '#fff',
      }}
    >
      <img src={NodataImg} style={{ width: 100, height: 162 }} />
      <span style={{ fontSize: 13, color: '#919AAC', paddingTop: 16 }}>暂无数据，请先创建 MM2 任务</span>
    </div>
  );
};

export default (props: Props) => {
  const { clusterId } = useParams<{
    clusterId: string;
  }>();
  const [loading, setLoading] = useState(true);
  const [disabled, setDisabled] = useState(true);

  useLayoutEffect(() => {
    Utils.request(api.getMirrorMakerMetadata(clusterId))
      .then((res: any[]) => {
        res?.length && setDisabled(false);
      })
      .finally(() => setLoading(false));
  }, []);

  return disabled ? (
    <Spin spinning={loading}>{loading ? <div style={{ height: 'calc(100vh - 118px)' }} /> : <NoConnector />}</Spin>
  ) : (
    props.children
  );
};
