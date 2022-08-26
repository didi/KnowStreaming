// @ts-nocheck 
import React from 'react';
import { Dropdown, Input } from 'knowdesign';
import Cron from 'react-cron-antd';
// import QnnReactCron, { CronProps, CronFns } from "qnn-react-cron";
import './style/CronInput.less';
interface PropsType extends React.HTMLAttributes<HTMLDivElement> {
  onChange?: Function
  value?: boolean;
}

const CronInput: React.FC<PropsType> = (props) => {
  const { value, onChange } = props;

  return (
    <Dropdown
      trigger={['click']}
      placement="bottomLeft"
      overlayClassName="cron-input-dropDown"
      overlay={<Cron className="cron-input" style={{ width: '553px' }} value={value} onOk={onChange} />}
    >
      <Input value={value} placeholder="请输入任务周期" />
    </Dropdown>
  );
}

export default CronInput;