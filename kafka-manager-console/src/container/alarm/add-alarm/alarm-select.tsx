import * as React from 'react';
import { alarm } from 'store/alarm';
import { IMonitorGroups } from 'types/base-type';
import { getValueFromLocalStorage, setValueToLocalStorage, deleteValueFromLocalStorage } from 'lib/local-storage';
import { VirtualScrollSelect } from '../../../component/virtual-scroll-select';

interface IAlarmSelectProps {
  onChange?: (result: string[]) => any;
  value?: string[];
  isDisabled?: boolean;
}

export class AlarmSelect extends React.Component<IAlarmSelectProps> {
  public getData = async () => {
    const originMonitorList = getValueFromLocalStorage('monitorGroups');
    if (originMonitorList) return originMonitorList;
    return await this.fetchMonitor();
  }

  public fetchMonitor = async () => {
    let data = await alarm.getMonitorGroups();
    data = (data || []).map((item: IMonitorGroups) => {
      return {
        ...item,
        label: item.name,
        value: item.name,
      };
    });
    setValueToLocalStorage('monitorGroups', data);
    return data;
  }
  public handleChange = (params: string[]) => {
    const { onChange } = this.props;

    // tslint:disable-next-line:no-unused-expression
    onChange && onChange(params);
  }

  public componentWillUnmount() {
    deleteValueFromLocalStorage('monitorGroups');
  }

  public render() {
    const { value, isDisabled } = this.props;
    return (
      <>
        <VirtualScrollSelect
          attrs={{ mode: 'multiple', placeholder: '请选择报警接收组' }}
          value={value}
          isDisabled={isDisabled}
          getData={this.getData}
          onChange={this.handleChange}
        />
        <a
          className="icon-color"
          target="_blank"
          href="https://github.com/didi/kafka-manager"
        >
          新增规则组？
        </a>
      </>
    );
  }
}
