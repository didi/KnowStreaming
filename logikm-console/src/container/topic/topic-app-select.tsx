import { Select, Tooltip } from 'component/antd';
import { urlPrefix } from 'constants/left-menu';
import { ITopic } from 'types/base-type';
import { topic } from 'store/topic';
import { updateAllTopicFormModal } from '../modal/topic';
import { searchProps } from 'constants/table';
import * as React from 'react';

const Option = Select.Option;

interface IStaffSelectProps {
  parameter?: ITopic;
  selectData?: any[];
  onChange?: (result: string) => any;
  value?: string;

}

export class TopicAppSelect extends React.Component<IStaffSelectProps> {

  public render() {
    const { value, selectData } = this.props;
    const query = `application=1`;

    let appId: string = null;
    const index = Array.isArray(selectData) ? selectData.findIndex(row => row.appId === value) : -1;
    appId = index > -1 ? value : selectData && selectData.length ? selectData[0].appId : '' ;

    return (
      <>
        <Select
          placeholder="请选择"
          defaultValue={appId}
          onChange={(e: string) => this.handleChange(e)}
          {...searchProps}
        >
          {selectData.map((d: any) => {
            const label = `${d.name}（${d.appId}）`;
            return (<Option value={d.appId} key={d.appId}>
              {label.length > 25 ? <Tooltip placement="bottomLeft" title={label}>{label}</Tooltip> : label}
            </Option>);
          })}
        </Select>
        {
          selectData.length ? null : <i>
            没有应用？
            <a href={`${urlPrefix}/topic/app-list?${query}`}>立刻创建</a>
          </i>}
      </>
    );
  }

  public handleChange(params: string) {
    const { onChange, parameter } = this.props;
    topic.getAuthorities(params, parameter.clusterId, parameter.topicName).then(() => {
      updateAllTopicFormModal();
    });
    // tslint:disable-next-line:no-unused-expression
    onChange && onChange(params);
  }
}
