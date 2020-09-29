import * as React from 'react';
import debounce from 'lodash.debounce';
import { Select, Tooltip } from 'component/antd';
import { ILabelValue } from 'types/base-type';
import { searchProps } from 'constants/table';

interface IAttars {
  mode?: 'multiple' | 'tags' | 'default' | 'combobox';
  placeholder?: string;
}

interface ISelectProps {
  onChange: (result: string[] | string) => any;
  value?: string[] | string;
  isDisabled?: boolean;
  attrs?: IAttars;
  getData: () => any;
  refetchData?: boolean; // 有些页面通过store拿数据需要二次更新
}
export class VirtualScrollSelect extends React.Component<ISelectProps> {
  public static getDerivedStateFromProps(nextProps: any, prevState: any) {
    if (nextProps.refetchData) {
      return {
        ...prevState,
        refetchData: true,
      };
    }
    return null;
  }
  public state = {
    optionsData: [] as ILabelValue[],
    scrollPage: 0,
    keyword: '',
    total: 0,
    refetchData: false,
  };

  public componentDidMount() {
    this.getData();
  }

  public getData = async () => {
    const { getData } = this.props;
    if (!getData) return;
    const pageSize = this.state.scrollPage;
    let originData = await getData();

    if (originData) {
      originData = this.state.keyword ?
        originData.filter((item: any) => item.label.includes(this.state.keyword)) : originData;
      let data = [].concat(originData);
      // tslint:disable-next-line:no-bitwise
      const total = data.length ? data.length / 30 | 1 : 0;
      data = data.splice(pageSize * 30, 30); // 每页展示30条数据

      return this.setState({
        optionsData: data,
        total,
        refetchData: false,
      });
    }
  }

  public componentDidUpdate(prevProps: any) {
    if (this.state.refetchData && !this.state.optionsData.length) {
      // this.getData();
    }
  }

  public handleSearch = (e: string) => {
    debounce(() => {
      this.setState({
        keyword: e.trim(),
        scrollPage: 0,
      }, () => {
        this.getData();
      });
    }, 300)();
  }

  public handleSelectScroll = (e: any) => {
    e.persist();
    const { target } = e;
    const { scrollPage } = this.state;
    debounce(() => {
      if (target.scrollTop + target.offsetHeight === target.scrollHeight) {
        const nextScrollPage = scrollPage + 1;
        if (this.state.total <= nextScrollPage) { // 已全部拉取
          return;
        }
        this.setState({
          scrollPage: nextScrollPage,
        }, () => {
          this.getData();
        });
      }
      if (target.scrollTop === 0 && scrollPage !== 0) { // 往上滚且不是第一页
        const nextScrollPage = scrollPage - 1;
        this.setState({
          scrollPage: nextScrollPage,
        }, () => {
          this.getData();
        });
      }
    }, 200)();
  }

  public render() {
    // tslint:disable-next-line:prefer-const
    let { value, isDisabled, attrs } = this.props;
    if (attrs && (attrs.mode === 'multiple' || attrs.mode === 'tags')) {
      value = value || [];
    }
    return (
      <>
        <Select
          {...attrs}
          defaultValue={value}
          value={value}
          onChange={this.props.onChange}
          onSearch={this.handleSearch}
          disabled={isDisabled}
          onPopupScroll={this.handleSelectScroll}
          {...searchProps}
        >
          {this.state.optionsData.map((d: ILabelValue) =>
            <Select.Option value={d.value} key={d.value}>
              {d.label.length > 25 ? <Tooltip placement="bottomLeft" title={d.label}>
                {d.label.substring(0, 25) + '...'}
              </Tooltip> : d.label}
            </Select.Option>)}
        </Select>
      </>
    );
  }
}
