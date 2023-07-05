import React from 'react';
import { MetricType } from '@src/api';
import DraggableCharts from '@src/components/DraggableCharts';
import DBreadcrumb from 'knowdesign/es/extend/d-breadcrumb';
import { AppContainer } from 'knowdesign';
import ZookeeperCard from '@src/components/CardBar/ZookeeperCard';

const ZookeeperDashboard = (): JSX.Element => {
  const [global] = AppContainer.useGlobalValue();
  return (
    <>
      <div className="breadcrumb" style={{ marginBottom: '10px' }}>
        <DBreadcrumb
          breadcrumbs={[
            { label: '多集群管理', aHref: '/' },
            { label: global?.clusterInfo?.name, aHref: `/cluster/${global?.clusterInfo?.id}` },
            { label: 'Zookeeper', aHref: `` },
          ]}
        />
      </div>
      <ZookeeperCard />
      <DraggableCharts type={MetricType.Zookeeper} />
    </>
  );
};

export default ZookeeperDashboard;
