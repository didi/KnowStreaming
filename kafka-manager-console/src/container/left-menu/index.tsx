import * as React from 'react';

import './index.less';
import { Tooltip, Icon, Badge } from 'component/antd';
import { adminMenu, topicMenu, clusterMenu, expertMenu, userMenu, alarmMenu } from '../../constants/left-menu';
import { NavLink } from 'react-router-dom';
import { observer } from 'mobx-react';
import { ILeftMenu } from 'types/base-type';
import { users } from 'store/users';
import { order } from 'store/order';

interface ILeftMenuProps {
  mode?: 'admin' | 'user' | 'topic' | 'cluster' | 'expert' | 'alarm';
}

@observer
export class LeftMenu extends React.Component<ILeftMenuProps> {
  public isUser = window.location.pathname.includes('/user'); // 判断是否为详情
  public state = {
    status: 'k-open',
  };

  public open = () => {
    const { status } = this.state;
    const newStatus = !status ? 'k-open' : '';
    this.setState({
      status: newStatus,
    });
  }

  public componentDidMount() {
    if (this.isUser) {
      order.getApplyOrderList(0);
      order.getApprovalList(0);
    }
  }

  public render() {
    const { status } = this.state;
    const { mode } = this.props;
    let menu = topicMenu;
    switch (mode) {
      case 'admin' :
        menu = adminMenu;
        break;
      case 'cluster' :
        menu = clusterMenu;
        break;
      case 'expert' :
        menu = expertMenu;
        break;
      case 'user' :
        menu = userMenu;
        break;
      case 'topic' :
        menu = topicMenu;
        break;
        case 'alarm':
        menu = alarmMenu;
    }

    return (
      <div className={`left-menu ${status}`}>
        <ul>
          {
            menu.map((m: ILeftMenu, i) => {
              if (m.hide) {
                return null;
              }
              const cnt = (
                <Badge
                  count={m.class === 'approval' ? order.approval : m.class === 'apply' ? order.apply : 0}
                  overflowCount={999}
                  key={i}
                >
                  <li key={i} className={m.class ? 'approval' : ''}>
                    <NavLink exact={true} to={m.href} activeClassName="active">
                      <i className={m.i} />
                      {status ? <span>{m.title}</span> : null}
                    </NavLink>
                  </li>
                </Badge>
              );

              if (!status) {
                return <Tooltip placement="right" title={m.title} key={m.i} >{cnt}</Tooltip>;
              }
              return cnt;
            })
          }
        </ul>
        <div className="k-float-op" onClick={this.open}>
          <Icon type={status ? 'double-left' : 'double-right'} />
        </div>
      </div>
    );
  }
}
