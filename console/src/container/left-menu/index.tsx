import * as React from 'react';

import './index.less';
import { Tooltip, Icon } from 'component/antd';
import { adminMenu, userMenu } from './constant';
import { BrowserRouter as Router, NavLink } from 'react-router-dom';

interface ILeftMenuProps {
  page: string;
  mode?: 'admin' | 'user';
}

export class LeftMenu extends React.Component<ILeftMenuProps> {
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

  public render() {
    const { status } = this.state;
    const { page, mode } = this.props;
    const menu = mode === 'admin' ? adminMenu : userMenu;
    return (
      <div className={`left-menu ${status}`}>
        <ul>
          {
            menu.map((m, i) => {
              const cnt = (
                <li key={m.i}>
                  <NavLink exact={true} to={m.href} activeClassName="active">
                    <i className={m.i} />
                    {status ? <span>{m.title}</span> : null}
                  </NavLink>
                </li>
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
