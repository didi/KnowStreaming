import { Select, Tooltip } from 'component/antd';
import { urlPrefix } from 'constants/left-menu';
import { searchProps } from 'constants/table';
import * as React from 'react';

const Option = Select.Option;

interface IStaffSelectProps {
  selectData?: any[];
  onChange?: (result: string[]) => any;
  value?: string[];
}

export class AppSelect extends React.Component<IStaffSelectProps> {

  public render() {
    const { value, selectData } = this.props;
    const query = `application=1`;
    return (
      <>
        <Select
          placeholder="请选择"
          value={value || []}
          onChange={(e: string[]) => this.handleChange(e)}
          {...searchProps}
        >
          {selectData.map((d: any) =>
            <Option value={d.appId} key={d.appId}>
              {d.name.length > 25 ? <Tooltip placement="bottomLeft" title={d.name}>{d.name}</Tooltip> : d.name}
            </Option>)}
        </Select>
        {/* {
          selectData.length ? null :  */}
        <i>
          没有应用？
            <a href={`${urlPrefix}/topic/app-list?${query}`}>立刻创建</a>
        </i>
        {/* } */}
      </>
    );
  }

  public handleChange(params: string[]) {
    const { onChange } = this.props;
    // tslint:disable-next-line:no-unused-expression
    onChange && onChange(params);
  }
}
