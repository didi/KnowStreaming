import React from 'react';
import 'styles/custom-component.less';
import { IBtn } from 'types/base-type';
import { Dropdown } from 'component/antd';

interface IMoreBtnsProps {
  btns: IBtn[];
  data: object;
}

export const MoreBtns = (props: IMoreBtnsProps) => {
  const { btns, data } = props;
  const btnsMenu = (
    <ul className="dropdown-menu">
      {btns.map((v, index) => (
        v.clickFunc ? <li key={index} onClick={() => v.clickFunc(data)} className="didi-theme">{v.label}</li>
        : <li key={index} className="didi-theme">{v.label}</li>
      ))}
    </ul>
  );
  return (
    <Dropdown
      key="2"
      overlay={btnsMenu}
      trigger={['click', 'hover']}
      placement="bottomLeft"
    >
      <span className="didi-theme ml-10">
        更多
      </span>
    </Dropdown>
  );
};
