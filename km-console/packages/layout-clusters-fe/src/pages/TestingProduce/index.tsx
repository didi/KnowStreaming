import { AppContainer } from 'knowdesign';
import * as React from 'react';
import ProduceClientTest from './Produce';
import './index.less';
import TaskTabs from '../TestingConsumer/component/TaskTabs';
import DBreadcrumb from 'knowdesign/es/extend/d-breadcrumb';
import { useParams } from 'react-router-dom';

const Produce = () => {
  const initial = {
    label: '生产',
    key: 'tab-1',
    closable: false,
    tabpane: <ProduceClientTest />,
  };
  const { clusterId } = useParams<{ clusterId: string }>();
  const [global] = AppContainer.useGlobalValue();
  const ref: any = React.useRef();

  React.useEffect(() => {
    AppContainer.eventBus.on('ProduceTopicChange', (args: string) => {
      ref.current && ref.current.setTabsTitle && ref.current.setTabsTitle(`生产 ${args}`);
    });
  }, []);

  return (
    <>
      <div className="breadcrumb">
        <DBreadcrumb
          breadcrumbs={[
            { label: '多集群管理', aHref: '/' },
            { label: global?.clusterInfo?.name, aHref: `/cluster/${global?.clusterInfo?.id}` },
            { label: 'Produce', aHref: '' },
          ]}
        />
      </div>
      <TaskTabs initial={initial} ref={ref} />
    </>
  );
};

export default Produce;
