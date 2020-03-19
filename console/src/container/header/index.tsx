import * as React from 'react';

import './index.less';
import { getCookie, deleteCookie } from 'lib/utils';
import { userLogoff } from 'lib/api';
import { notification, Dropdown } from 'component/antd';
import { users } from 'store/users';
import logoUrl from '../../assets/image/kafka-logo.png';
import devIcon from '../../assets/image/devops.png';
import adminIcon from '../../assets/image/admin.png';
import userIcon from '../../assets/image/normal.png';

interface IHeader {
  active: string;
}

export const Header = (props: IHeader) => {
  const { active } = props;
  const username = getCookie('username');
  const role = Number(getCookie('role'));

  const logoff = () => {
    userLogoff(username).then(() => {
      notification.success({ message: '退出成功' });
      deleteCookie(['username', 'role']);
      location.reload();
    });
  };

  const menu = (
    <ul className="kafka-header-menu">
      {role ? <li> <a href="/admin/user_manage">管理</a></li> : ''}
      <li onClick={logoff}>退出</li>
    </ul>
  );

  return (
    <div className="kafka-header-container">
      <div className="left-content">
        <img className="kafka-header-icon" src={logoUrl} alt="" />
        <span className="kafka-header-text">Kafka Manager</span>
      </div>
      <div className="mid-content">
        <span className={active === 'user' ? 'k-active' : ''}><a href="/">集群监控</a></span>
        {role ? <span className={active === 'admin' ? 'k-active' : ''}><a href="/admin">集群管控</a></span> : ''}
      </div>
      <div className="right-content">
        <img className="kafka-avatar-icon" src={role === 2? adminIcon : role === 1 ? devIcon: userIcon } alt="" />
        <Dropdown overlay={menu}>
          <span className="kafka-header-text">
            {users.mapRole(role)} : {username}</span>
        </Dropdown>
      </div>
    </div>
  );
};
