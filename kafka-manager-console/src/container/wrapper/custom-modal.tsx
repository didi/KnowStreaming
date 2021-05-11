import * as React from 'react';
import { observer } from 'mobx-react';
import { modal } from 'store/modal';
import { ConnectTopicList } from '../modal/connect-topic-list';
import { ConnectAppList } from '../modal/offline-app-modal';
import { ConnectAppNewList } from '../modal/offline-app-modal-new';
import { CancelTopicPermission } from 'container/modal/cancel-topic-permission';
import { OfflineClusterModal } from 'container/modal/offline-cluster-modal';
import { RenderOrderOpResult } from 'container/modal/order';

@observer
export default class AllCustomModalInOne extends React.Component {
  public render() {
    if (!modal.modalId && !modal.drawerId) return null;
    return (
      <>
        {drawerMap[modal.drawerId] || null}
        {modalMap[modal.modalId] || null}
      </>
    );
  }
}

const modalMap = {
  offlineTopicModal: <ConnectTopicList />,
  offlineAppNewModal: <ConnectAppNewList />,
  offlineAppModal: <ConnectAppList />,
  cancelTopicPermission: <CancelTopicPermission />,
  offlineClusterModal: <OfflineClusterModal />,
  orderOpResult: <RenderOrderOpResult />,
} as {
  [key: string]: JSX.Element;
};

const drawerMap = {
} as {
  [key: string]: JSX.Element;
};
