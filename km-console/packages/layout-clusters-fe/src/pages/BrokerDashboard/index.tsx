import React from 'react';
import { MetricType } from '@src/api';
import BrokerHealthCheck from '@src/components/CardBar/BrokerHealthCheck';
import DraggableCharts from '@src/components/DraggableCharts';
import DBreadcrumb from 'knowdesign/es/extend/d-breadcrumb';
import { AppContainer } from 'knowdesign';

const BrokerDashboard = (): JSX.Element => {
  const [global] = AppContainer.useGlobalValue();
  return (
    <>
      <div className="breadcrumb" style={{ marginBottom: '10px' }}>
        <DBreadcrumb
          breadcrumbs={[
            { label: '多集群管理', aHref: '/' },
            { label: global?.clusterInfo?.name, aHref: `/cluster/${global?.clusterInfo?.id}` },
            { label: 'Broker', aHref: `` },
          ]}
        />
      </div>
      <BrokerHealthCheck />
      <DraggableCharts type={MetricType.Broker} />
    </>
  );
};

export default BrokerDashboard;
