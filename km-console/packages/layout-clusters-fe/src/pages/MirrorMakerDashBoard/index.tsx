import React from 'react';
import { MetricType } from '@src/api';
import MirrorMakerCard from '@src/components/CardBar/MirrorMakerCard';
import DraggableCharts from '@src/components/DraggableCharts';
import DBreadcrumb from 'knowdesign/es/extend/d-breadcrumb';
import { AppContainer } from 'knowdesign';
import HasConnector from './HasConnector';

const MirrorMakerDashboard = (): JSX.Element => {
  const [global] = AppContainer.useGlobalValue();
  return (
    <>
      <div className="breadcrumb" style={{ marginBottom: '10px' }}>
        <DBreadcrumb
          breadcrumbs={[
            { label: '多集群管理', aHref: '/' },
            { label: global?.clusterInfo?.name, aHref: `/cluster/${global?.clusterInfo?.id}` },
            { label: 'Replication', aHref: `` },
          ]}
        />
      </div>
      <HasConnector>
        <>
          <MirrorMakerCard />
          <DraggableCharts type={MetricType.MM2} />
        </>
      </HasConnector>

      {/* <DraggableCharts type={MetricType.Broker} /> */}
    </>
  );
};

export default MirrorMakerDashboard;
