import React from 'react';
import { MetricType } from '@src/api';
import TopicHealthCheck from '@src/components/CardBar/TopicHealthCheck';
import DashboardDragChart from '@src/components/DashboardDragChart';
import { AppContainer } from 'knowdesign';
import DBreadcrumb from 'knowdesign/es/extend/d-breadcrumb';

const TopicDashboard = () => {
  const [global] = AppContainer.useGlobalValue();
  return (
    <>
      <div className="breadcrumb" style={{ marginBottom: '10px' }}>
        <DBreadcrumb
          breadcrumbs={[
            { label: '多集群管理', aHref: '/' },
            { label: global?.clusterInfo?.name, aHref: `/cluster/${global?.clusterInfo?.id}` },
            { label: 'Topic', aHref: `` },
          ]}
        />
      </div>
      <TopicHealthCheck />
      <DashboardDragChart type={MetricType.Topic} />
    </>
  );
};

export default TopicDashboard;
