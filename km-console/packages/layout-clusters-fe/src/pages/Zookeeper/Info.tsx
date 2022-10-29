import React from 'react';
import { Descriptions } from 'knowdesign';
import moment from 'moment';
const ZKInfo = ({ nodeInfo, siderWidth }: any) => {
  const smallStyle = {
    width: 82,
  };
  return (
    <Descriptions
      style={{ fontSize: '13px' }}
      column={siderWidth >= 270 ? 2 : 3}
      labelStyle={{
        display: 'flex',
        textAlign: 'right',
        justifyContent: 'end',
        color: '#74788D',
        fontSize: '13px',
        width: siderWidth >= 270 ? 144 : 100,
      }}
      contentStyle={{ fontSize: '13px' }}
    >
      <Descriptions.Item labelStyle={siderWidth < 270 && smallStyle} label="aversion">
        {nodeInfo.aversion || nodeInfo.aversion === 0 ? nodeInfo.aversion : '-'}
      </Descriptions.Item>
      <Descriptions.Item label="ctime">{nodeInfo.ctime || nodeInfo.ctime === 0 ? nodeInfo.ctime : '-'}</Descriptions.Item>
      <Descriptions.Item label="ctime-pretty">
        {nodeInfo.ctime ? moment(nodeInfo.ctime).format('YYYY-MM-DD HH:mm:ss') : '-'}
      </Descriptions.Item>
      <Descriptions.Item labelStyle={siderWidth < 270 && smallStyle} label="cversion">
        {nodeInfo.cversion || nodeInfo.cversion === 0 ? nodeInfo.cversion : '-'}
      </Descriptions.Item>
      <Descriptions.Item label="czxid">{nodeInfo.czxid || nodeInfo.czxid === 0 ? nodeInfo.czxid : '-'}</Descriptions.Item>
      <Descriptions.Item label="dataLength">
        {nodeInfo.dataLength || nodeInfo.dataLength === 0 ? nodeInfo.dataLength : '-'}
      </Descriptions.Item>
      <Descriptions.Item labelStyle={siderWidth < 270 && smallStyle} label="mtime">
        {nodeInfo.mtime || nodeInfo.mtime === 0 ? nodeInfo.mtime : '-'}
      </Descriptions.Item>
      <Descriptions.Item label="mtime-pretty">
        {nodeInfo.mtime ? moment(nodeInfo.mtime).format('YYYY-MM-DD HH:mm:ss') : '-'}
      </Descriptions.Item>
      <Descriptions.Item label="numChildren">
        {nodeInfo.numChildren || nodeInfo.numChildren === 0 ? nodeInfo.numChildren : '-'}
      </Descriptions.Item>
      <Descriptions.Item labelStyle={siderWidth < 270 && smallStyle} label="mzxid">
        {nodeInfo.mzxid || nodeInfo.mzxid === 0 ? nodeInfo.mzxid : '-'}
      </Descriptions.Item>
      <Descriptions.Item label="ephemeralOwner">
        {nodeInfo.ephemeralOwner || nodeInfo.ephemeralOwner === 0 ? nodeInfo.ephemeralOwner : '-'}
      </Descriptions.Item>
      <Descriptions.Item style={{ paddingBottom: '12px' }} label="pzxid">
        {nodeInfo.pzxid || nodeInfo.pzxid === 0 ? nodeInfo.pzxid : '-'}
      </Descriptions.Item>
      <Descriptions.Item labelStyle={siderWidth < 270 && smallStyle} style={{ paddingBottom: '12px' }} label="version">
        {nodeInfo.version || nodeInfo.version === 0 ? nodeInfo.version : '-'}
      </Descriptions.Item>
    </Descriptions>
  );
};

export default ZKInfo;
