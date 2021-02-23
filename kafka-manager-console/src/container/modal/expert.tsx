import { wrapper } from 'store';
import { IXFormWrapper } from 'types/base-type';

import * as React from 'react';
import { WrappedDataMigrationFormTable } from 'container/drawer/data-migration';

export interface IRenderData {
  brokerIdList: number[];
  partitionIdList: number[];
  topicName: string;
  clusterId?: number;
  clusterName?: string;
  throttle: number;
  maxThrottle: number;
  minThrottle: number;
  originalRetentionTime: number;
  reassignRetentionTime: number;
  retentionTime: number;
  key?: string | number;
}

export const migrationModal = (renderData: IRenderData[]) => {
  const xFormWrapper = {
    type: 'drawer',
    visible: true,
    width: 1000,
    title: '新建迁移任务',
    customRenderElement: <WrappedDataMigrationFormTable data={renderData} />,
    nofooter: true,
    noform: true,
  };
  wrapper.open(xFormWrapper as IXFormWrapper);
};
