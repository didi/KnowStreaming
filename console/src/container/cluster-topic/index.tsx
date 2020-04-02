import * as React from 'react';
import { Select, Input, Checkbox, Icon } from 'component/antd';
import { cluster } from 'store/cluster';
import { IFiler } from 'types/base-type';

const Option = Select.Option;
const Search = Input.Search;

interface IParams {
  filters: IFiler[];
  setSelectedKeys: (selectedKeys: string[]) => void;
  confirm?: () => void;
}

interface IState {
  [filter: string]: boolean | string;
}

export class SearchAndFilter extends React.Component<any, IState> {
  public timer: number;

  public renderCluster() {
    return (
      <li>
        <Select value={cluster.active} onChange={cluster.changeCluster}>
          {cluster.data.map((d) => <Option value={d.clusterId} key={d.clusterId}>{d.clusterName}</Option>)}
        </Select>
      </li>
    );
  }

  public renderSearch(placeholder?: string, keyName: string = 'searchKey') {
    return (
      <li><Search placeholder={placeholder || '请输入Topic名称'} onChange={this.onSearchChange.bind(null, keyName)} /></li>
    );
  }

  public onSearchChange = (keyName: string, e: React.ChangeEvent<HTMLInputElement>) => {
    const searchKey = e.target.value.trim();
    this.setState({
      [keyName]: searchKey,
    });
  }

  public handleChange(params: IParams, e: []) {
    const { setSelectedKeys, confirm } = params;
    setSelectedKeys(e);
    confirm();
  }

  public handleVisble = (type: string) => {
    if (this.timer) window.clearTimeout(this.timer);
    window.setTimeout(() => {
      this.setState({ [type]: true });
    });
  }

  public handleUnVisble = (type: string) => {
    this.timer = window.setTimeout(() => {
      this.setState({ [type]: false });
    }, 100);
  }

  public renderFilter = (type: string, params: IParams) => {
    const { filters } = params;
    return filters !== undefined ? (
      <ul
        onMouseOver={this.handleVisble.bind(null, type)}
        onMouseLeave={this.handleUnVisble.bind(null, type)}
        className="ant-dropdown-menu ant-dropdown-menu-vertical"
      >
        <Checkbox.Group onChange={this.handleChange.bind(null, params)}>
          {filters.map(i => <li key={i.value} className="ant-dropdown-menu-item">
            <Checkbox value={i.value} >{i.text}</Checkbox>
          </li>)}
        </Checkbox.Group>
      </ul>
    ) : <div />;
  }

  public renderFilterIcon = (type: string) => {
    return (
      <span
        onMouseOver={this.handleVisble.bind(null, type)}
        onMouseLeave={this.handleUnVisble.bind(null, type)}
      ><Icon type="filter" theme="filled" />
      </span>
    );
  }

  public renderColumnsFilter = (type: string) => {
    return {
      filterIcon: this.renderFilterIcon.bind(null, type),
      filterDropdownVisible: this.state[type],
      filterDropdown: this.renderFilter.bind(null, type),
    };
  }
}
