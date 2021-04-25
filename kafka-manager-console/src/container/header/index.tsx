import * as React from 'react';

import './index.less';
import { userLogOut } from 'lib/api';
import { notification, Dropdown, Icon, Tooltip, Popover } from 'component/antd';
import { urlPrefix } from 'constants/left-menu';
import { region, IRegionIdcs } from 'store/region';
import logoUrl from '../../assets/image/kafka-logo.png';
import userIcon from '../../assets/image/normal.png';
import weChat from '../../assets/image/wechat.jpeg';
import { users } from 'store/users';
import { observer } from 'mobx-react';
import { Link } from 'react-router-dom';
import './index.less';

interface IHeader {
  active: string;
}

interface IMenuItem {
  active: boolean;
  label: string;
  href: string;
  show: boolean;
}

export const Header = observer((props: IHeader) => {
  const { active } = props;
  const role = users.currentUser.role;
  const headerMenu = [{
    active: active === 'topic',
    label: 'Topic管理',
    href: `/topic`,
    show: true,
  }, {
    active: active === 'cluster',
    label: '集群管理',
    href: `/cluster`,
    show: true,
  }, {
    active: active === 'alarm',
    label: '监控告警',
    href: `/alarm`,
    show: true,
  }, {
    active: active === 'admin',
    label: '运维管控',
    href: `/admin`,
    show: role !== 0,
  }, {
    active: active === 'expert',
    label: '专家服务',
    href: `/expert`,
    show: role === 2,
  }];

  const logOut = () => {
    userLogOut().then(() => {
      notification.success({ message: '退出成功' });
    });
  };
  const content = (
    <div style={{ height: '250px', padding: '5px' }} className="kafka-avatar-img">
      <img style={{ width: '190px', height: '246px' }} src={weChat} alt="" />
    </div>
  );
  const helpCenter = (
    <ul className="kafka-header-menu">
      <li>
        <a
          href="https://github.com/didi/kafka-manager"
          target="_blank"
        >产品介绍
        </a></li>
      <li>
        <a
          href="https://github.com/didi/kafka-manager/blob/master/docs/user_guide/user_guide_cn.md"
          target="_blank"
        >使用手册
        </a></li>
      <li>
        <a
          href="https://github.com/didi/kafka-manager/blob/master/docs/install_guide/install_guide_cn.md"
          target="_blank"
        >QuickStart
        </a></li>
      <li>
        <a
          href="https://github.com/didi/kafka-manager/blob/master/docs/user_guide/faq.md"
          target="_blank"
        >常见问题
        </a></li>
      <li>
        <Popover placement="left" content={content} trigger="hover">
          <a
            // tslint:disable-next-line:max-line-length
            // href="https://github.com/didi/kafka-manager"
            href="javascript:void(0)"
            onClick={() => { return false }}
          // target="_blank"
          >联系我们
          </a>
        </Popover>
      </li>
    </ul>
  );

  const menu = (
    <ul className="kafka-header-menu">
      <li> <Link to={`/user/my-order`} key="1"> 我的申请 </Link> </li>
      <li> <Link to={`/user/my-approval`} key="2"> 我的审批 </Link> </li>
      <li> <Link to={`/user/bill`} key="3"> 账单管理 </Link> </li>
      <li><a onClick={() => logOut()}>退出</a></li>
    </ul>
  );

  const handleChangeRegion = (value: IRegionIdcs) => {
    region.changeRegion(value);

    location.assign(handleJumpLocation());
  };

  const handleJumpLocation = (): string => {
    const isDetailPage = window.location.pathname.includes('detail');
    const pathNames = window.location.pathname.split('/');
    const loc = window.location.pathname.includes('error') ? `${urlPrefix}/topic` : window.location.pathname;

    return isDetailPage ? pathNames.splice(0, 3).join('/') : loc;
  };

  const regionMenu = (
    <ul className="kafka-header-menu">
      {region.regionIdcList.map((v, index) => (
        <li key={index} value={v.idc}>
          <a onClick={() => handleChangeRegion({ name: v.name, idc: v.idc })}>{v.name}</a>
        </li>
      ))}
    </ul>
  );

  const isLongName = (users.currentUser.chineseName ? users.currentUser.chineseName.length > 8
    : users.currentUser.username ? users.currentUser.username.length > 11 : false);
  return (
    <div className="kafka-header-container">
      <div className="left-content">
        <img className="kafka-header-icon" src={logoUrl} alt="" />
        <span className="kafka-header-text">Kafka Manager</span>
        <a className='kafka-header-version' href="https://github.com/didi/Logi-KafkaManager/releases" target='_blank'>v2.4.0</a>
        {/* 添加版本超链接 */}
      </div>
      <div className="mid-content">
        {headerMenu.map((item: IMenuItem, index: number) =>
          item.show ?
            <Link to={item.href} key={index}>
              <span key={index} className={item.active ? 'k-active' : ''}>
                {item.label}
              </span>
            </Link> : null,
        )}
      </div>
      <div className="right-content">
        <Dropdown key="1" overlay={helpCenter} trigger={['click', 'hover']} placement="bottomCenter">
          <span className="region-user-text">
            <span className="region-text">
              帮助中心 <Icon className="region-text-icon" type="down" />
            </span>
          </span>
        </Dropdown>
        <Dropdown
          key="2"
          overlay={regionMenu}
          trigger={['click', 'hover']}
          placement="bottomCenter"
        >
          <span className="region-user-text">
            <a className="region-text">
              {region.regionName} <Icon className="region-text-icon" type="down" />
            </a>
          </span>
        </Dropdown>
        <Dropdown key="3" overlay={menu} trigger={['click', 'hover']} placement="bottomCenter">
          <span className="kafka-avatar-box">
            <img className="kafka-avatar-icon" src={userIcon} alt="" />
            {isLongName ?
              <Tooltip
                placement="left"
                title={users.currentUser.chineseName || users.currentUser.username}
              >
                <span className="kafka-user-span">
                  {users.currentUser.chineseName || users.currentUser.username}
                </span>
              </Tooltip> :
              <span className="kafka-user-span">
                {users.currentUser.chineseName || users.currentUser.username}
              </span>}
          </span>
        </Dropdown>
      </div>
    </div>
  );
});
