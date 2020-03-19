import * as React from 'react';
import { Modal } from 'component/antd';
import { modal } from 'store/modal';
import { NetWorkFlow } from 'container/topic-detail/com';

export class ClusterDetail extends React.Component<any> {
  public render() {
    return (
      <Modal
        title="集群流量"
        style={{ top: 70 }}
        visible={true}
        onCancel={modal.close}
        maskClosable={false}
        width={1000}
        destroyOnClose={true}
      >
        <div className="k-row">
          <NetWorkFlow clusterId={modal.currentCluster.clusterId} />
        </div>
      </Modal>
    );
  }
}
