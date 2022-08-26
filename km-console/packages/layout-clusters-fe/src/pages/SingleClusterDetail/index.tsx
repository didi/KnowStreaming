import DBreadcrumb from 'knowdesign/lib/extend/d-breadcrumb';
import React from 'react';
import TourGuide, { ClusterDetailSteps } from '@src/components/TourGuide';
import './index.less';
import LeftSider from './LeftSider';
import ChartPanel from './DetailChart';
import ChangeLog from './ChangeLog';

const SingleClusterDetail = (): JSX.Element => {
  return (
    <>
      <TourGuide guide={ClusterDetailSteps} run={true} />
      <div className="single-cluster-detail">
        <div className="breadcrumb">
          <DBreadcrumb
            breadcrumbs={[
              { label: '多集群管理', aHref: '/' },
              { label: '集群详情', aHref: '' },
            ]}
          />
        </div>
        <div className="cluster-detail">
          <LeftSider />
          <div className="chart-panel">
            <ChartPanel>
              <ChangeLog />
            </ChartPanel>
          </div>
        </div>
      </div>
    </>
  );
};

export default SingleClusterDetail;
