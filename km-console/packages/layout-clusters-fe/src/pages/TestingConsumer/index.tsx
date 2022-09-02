import { AppContainer } from 'knowdesign';
import DBreadcrumb from 'knowdesign/es/extend/d-breadcrumb';
import * as React from 'react';
import { useParams } from 'react-router-dom';
import TaskTabs from './component/TaskTabs';
import ConsumeClientTest from './Consume';
import './index.less';

const Consume = () => {
  const initial = {
    label: '消费',
    key: 'tab-1',
    closable: false,
    tabpane: <ConsumeClientTest />,
  };

  const ref: any = React.useRef();
  const { clusterId } = useParams<{ clusterId: string }>();
  const [global] = AppContainer.useGlobalValue();
  React.useEffect(() => {
    AppContainer.eventBus.on('ConsumeTopicChange', (args: string) => {
      ref.current && ref.current.setTabsTitle && ref.current.setTabsTitle(`消费 ${args}`);
    });
  }, []);

  return (
    <>
      <div className="breadcrumb">
        <DBreadcrumb
          breadcrumbs={[
            { label: '多集群管理', aHref: '/' },
            { label: global?.clusterInfo?.name, aHref: `/cluster/${global?.clusterInfo?.id}` },
            { label: 'Consume', aHref: '' },
          ]}
        />
      </div>
      <TaskTabs initial={initial} ref={ref} />
    </>
  );
};

export default Consume;
