import { Select, Tooltip } from 'component/antd';
import { getStaff } from 'lib/api';
import debounce from 'lodash.debounce';
import { users } from 'store/users';
import { IStaff } from 'types/base-type';
import { getCookie } from 'lib/utils';
import { searchProps } from 'constants/table';
import * as React from 'react';

const Option = Select.Option;

interface IStaffSelectProps {
  onChange?: (result: string[]) => any;
  value?: string[];
  isDisabled?: boolean;
}

export class StaffSelect extends React.Component<IStaffSelectProps> {

  public state = {
    staffList: users.staff as IStaff[],
  };

  public componentDidMount() {
    this.getStaffList();
  }

  public getStaffList = () => {
    const { value } = this.props;
    const current = users.currentUser.username || getCookie('username');
    const principals = [''];
    // const principals = value || (current ? [current] : []);
    const promises: any[] = [];

    for (const item of principals) {
      promises.push(getStaff(item));
    }

    Promise.all(promises).then((dataList) => {
      let list = [] as IStaff[];
      if (dataList && dataList.length) {
        dataList.forEach((data: IStaff[], index) => {
          if (!data || !data.length) {
            data = [{
              chineseName: '',
              username: principals[index],
              department: '',
            }] as IStaff[];
          }
          list.push(...data);
        });
      }
      list = list.map(item => ({
        ...item,
        label: item.chineseName ? `${item.chineseName}（${item.username}）${item.department}` : item.username,
        value: item.username,
      }));
      this.setState({
        staffList: list,
      });
    });
  }

  public render() {
    const { value, isDisabled } = this.props;
    const current = users.currentUser.username || getCookie('username');
    const principals = value || (current ? [current] : []);
    return (
      <Select
        mode="multiple"
        placeholder="请选择，(最少选两人)"
        defaultValue={principals}
        onChange={(e: string[]) => this.handleChange(e)}
        onSearch={(e: string) => this.handleSearch(e)}
        onFocus={() => this.getFocus()}
        disabled={isDisabled}
        {...searchProps}
      >
        {this.state.staffList.map((d: IStaff) =>
          <Option value={d.value} key={d.value}>
            {d.label.length > 25 ? <Tooltip placement="bottomLeft" title={d.label}>{d.label}</Tooltip> : d.label}
          </Option>)}
      </Select>
    );
  }

  public getFocus() {
    this.getStaffList();
  }

  public handleSearch(params: string) {
    debounce(() => {
      getStaff(params).then((data: IStaff[]) => {
        data = data.map(item => ({
          ...item,
          label: `${item.chineseName}（${item.username}）${item.department}`,
          value: item.username,
        })) || [];

        this.setState({
          staffList: data,
        });
      });
    }, 300)();
  }

  public handleChange(params: string[]) {
    const { onChange } = this.props;
    // tslint:disable-next-line:no-unused-expression
    onChange && onChange(params);
  }

}
