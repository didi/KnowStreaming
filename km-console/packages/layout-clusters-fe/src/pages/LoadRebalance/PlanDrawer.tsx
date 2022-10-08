import React, { useState } from 'react';
import { message, Drawer, Button, Space, Divider, AppContainer } from 'knowdesign';
import { IconFont } from '@knowdesign/icons';

import RebalancePlan from '../Jobs/RebalancePlan';
interface PropsType extends React.HTMLAttributes<HTMLDivElement> {
  onClose: () => void;
  balanceImmediate?: Function;
  visible: boolean;
  isPrevew?: boolean;
  detailData?: any;
  isEdit?: boolean;
}

const PlanDrawer: React.FC<PropsType> = ({ onClose, visible, detailData, isPrevew, balanceImmediate, isEdit }) => {
  const [global] = AppContainer.useGlobalValue();

  const submit = () => {
    if (detailData?.replicas === 0) {
      message['warning']('replicas=0,该集群已达到均衡要求，不需要再执行均衡任务。');
    } else {
      balanceImmediate && balanceImmediate();
    }
  };

  return (
    <>
      <Drawer
        title={
          <Space size={0}>
            <Button type="text" className="drawer-title-left-button" icon={<IconFont type="icon-fanhui1" />} onClick={onClose} />
            <Divider type="vertical" />
            <span>执行计划</span>
          </Space>
        }
        width="1080px"
        destroyOnClose={true}
        className="plan-drawer"
        onClose={onClose}
        visible={visible}
        maskClosable={false}
        extra={
          <Space>
            {!!isPrevew && (
              <>
                <Button size="small" onClick={onClose}>
                  取消
                </Button>
                <Button className={isEdit ? '' : 'btn-width84'} type="primary" size="small" disabled={false} onClick={submit}>
                  {isEdit ? '确定' : '立即均衡'}
                </Button>

                <Divider type="vertical" />
              </>
            )}
          </Space>
        }
      >
        <RebalancePlan balanceData={detailData} />
      </Drawer>
    </>
  );
};

export default PlanDrawer;
