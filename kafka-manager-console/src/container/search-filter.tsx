import * as React from 'react';
import { Input, Checkbox, Icon, Select, Tooltip } from 'component/antd';
import { IFilter } from 'types/base-type';
import { cluster } from 'store/cluster';
import { expert } from 'store/expert';
import { app } from 'store/app';
import 'styles/search-filter.less';
import { urlPrefix } from 'constants/left-menu';
import { searchProps } from 'constants/table';

const Search = Input.Search;
const Option = Select.Option;

interface IFilterParams {
  filters: IFilter[];
  setSelectedKeys: (selectedKeys: string[]) => void;
  confirm?: () => void;
}

interface ISearchAndFilterState {
  [filter: string]: boolean | string | number | any[];
}

export class SearchAndFilterContainer extends React.Component<any, ISearchAndFilterState> {
  public timer: number;

  public urlPrefix: string = urlPrefix;

  public renderApp(text?: string) {
    return (
      <li className="render-box">
        <span>{text}</span>
        <Select
          value={app.active}
          onChange={app.changeActiveApp}
          className="render-select"
          style={{ width: 210 }}
          {...searchProps}
        >
          {app.selectData.map((d, index) =>
            <Select.Option
              value={d.appId}
              key={index}
            >
              {d.name.length > 16 ?
                <Tooltip placement="bottomLeft" title={d.name}>{d.name}</Tooltip>
                : d.name}
            </Select.Option>)}
        </Select>
      </li>
    );
  }

  public renderCluster(text?: string) {
    return (
      <li className="render-box">
        <span>{text}</span>
        <Select
          value={cluster.active}
          onChange={cluster.changeCluster}
          className="render-select"
          style={{ width: 210 }}
          {...searchProps}
        >
          {cluster.selectData.map((d, index) =>
            <Select.Option
              value={d.value}
              key={index}
            >
              {d.label.length > 16 ?
                <Tooltip placement="bottomLeft" title={d.label}>{d.label}</Tooltip>
                : d.label}
            </Select.Option>)}
        </Select>
      </li>
    );
  }

  public renderAllCluster(text?: string) {
    return (
      <li className="render-box">
        <span>{text}</span>
        <Select
          value={cluster.allActive}
          onChange={cluster.changeAllCluster}
          className="render-select"
          style={{ width: 210 }}
          {...searchProps}
        >
          {cluster.selectAllData.map((d, index) =>
            <Select.Option
              value={d.value}
              key={index}
            >
              {d.label.length > 16 ?
                <Tooltip placement="bottomLeft" title={d.label}>{d.label}</Tooltip>
                : d.label}
            </Select.Option>)}
        </Select>
      </li>
    );
  }

  public renderPhysical(text?: string) {
    return (
      <li className="render-box">
        <span>{text}</span>
        <Select
          value={expert.active}
          onChange={expert.changePhysical}
          className="render-select"
          style={{ width: 210 }}
          {...searchProps}
        >
          {expert.metaData.map((d, index) =>
            <Select.Option
              value={d.clusterId}
              key={index}
            >
              {d.clusterName.length > 16 ?
                <Tooltip placement="bottomLeft" title={d.clusterName}>{d.clusterName}</Tooltip>
                : d.clusterName}
            </Select.Option>)}
        </Select>
      </li>
    );
  }

  public renderSearch(text?: string, placeholder?: string, keyName: string = 'searchKey') {
    const value = this.state[keyName] as string;
    return (
      <li className="render-box">
        <span>{text}</span>
        <Search
          placeholder={placeholder || '请输入Topic名称'}
          onChange={this.onSearchChange.bind(null, keyName)}
          style={{ width: 210 }}
          value={value}
        />
      </li>
    );
  }

  public onSearchChange = (keyName: string, e: React.ChangeEvent<HTMLInputElement>) => {
    const searchKey = e.target.value.trim();
    this.setState({
      [keyName]: searchKey,
    });
  }

  public handleChange(params: IFilterParams, e: []) {
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

  public renderFilter = (type: string, params: IFilterParams) => {
    const { filters } = params;
    return filters !== undefined ? (
      <ul
        onMouseOver={this.handleVisble.bind(null, type)}
        onMouseLeave={this.handleUnVisble.bind(null, type)}
        className="ant-dropdown-menu ant-dropdown-menu-vertical"
      >
        <Checkbox.Group onChange={this.handleChange.bind(null, params)}>
          {filters.map((ele, i) => <li key={i} className="ant-dropdown-menu-item">
            <Checkbox value={ele.value} >{ele.text}</Checkbox>
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

  public renderColumnsFilter = (type: string, params?: any) => {
    return {
      filterIcon: this.renderFilterIcon.bind(null, type),
      filterDropdownVisible: this.state[type] as boolean,
      filterDropdown: this.renderFilter.bind(null, type),
    };
  }
}
