import { Input } from 'component/antd';
import { region } from 'store';
import * as React from 'react';
import { indexUrl } from 'constants/strategy';

interface IPeakFlowProps {
  value?: any;
  onChange?: (result: any) => any;
}

export class PeakFlowInput extends React.Component<IPeakFlowProps> {

  public render() {
    const { value } = this.props;
    return (
      <>
        <Input
          placeholder="请输入峰值流量"
          suffix="MB/s"
          value={value}
          onChange={(e: React.ChangeEvent<HTMLInputElement>) => this.handleChange(e)}
        />
        <span>
          预估费用：{region.currentRegion === 'cn' ? value * 40 : value * 45}元/月，
          <a
            // tslint:disable-next-line:max-line-length
            href={indexUrl.indexUrl}
            target="_blank"
          >kafka计价方式
          </a>
        </span>
      </>
    );
  }
  public handleChange(e: React.ChangeEvent<HTMLInputElement>) {
    const { onChange } = this.props;
    // tslint:disable-next-line:no-unused-expression
    onChange && onChange(e.target.value);
  }
}
