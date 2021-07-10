import * as React from 'react';
import { Select, Spin, Form, Tooltip } from 'component/antd';
import { message } from 'antd';
import { IFormItem } from 'component/x-form';
import { cluster } from 'store/cluster';
import { alarm } from 'store/alarm';
import { topic } from 'store/topic';
import { observer } from 'mobx-react';
import { IRequestParams, IStrategyFilter } from 'types/alarm';
import { filterKeys } from 'constants/strategy';
import { VirtualScrollSelect } from 'component/virtual-scroll-select';
import { IsNotNaN } from 'lib/utils';
import { searchProps } from 'constants/table';
import { toJS } from 'mobx';

interface IDynamicProps {
  form?: any;
  formData?: any;
}

interface IFormSelect extends IFormItem {
  options: Array<{ key?: string | number, value: string | number, label: string }>;
}

interface IVritualScrollSelect extends IFormSelect {
  getData: () => any;
  isDisabled: boolean;
  refetchData?: boolean;
}

@observer
export class DynamicSetFilter extends React.Component<IDynamicProps> {
  public isDetailPage = window.location.pathname.includes('/alarm-detail'); // 判断是否为详情
  public monitorType: string = null;
  public clusterId: number = null;
  public clusterName: string = null;
  public clusterIdentification: string | number = null;
  public topicName: string = null;
  public consumerGroup: string = null;
  public location: string = null;

  public getFormValidateData() {
    const filterList = [] as IStrategyFilter[];
    let monitorType = '' as string;
    let filterObj = {} as any;

    this.props.form.validateFields((err: Error, values: any) => {
      if (!err) {
        monitorType = values.monitorType;
        const index = cluster.clusterData.findIndex(item => item.clusterIdentification === values.cluster);
        if (index > -1) {
          values.clusterIdentification = cluster.clusterData[index].clusterIdentification;
          values.clusterName = cluster.clusterData[index].clusterName;
        }
        for (const key of Object.keys(values)) {
          if (filterKeys.indexOf(key) > -1) { // 只有这几种值可以设置
            filterList.push({
              tkey: key === 'clusterName' ? 'cluster' : key, // clusterIdentification
              topt: '=',
              tval: [values[key]],
              clusterIdentification: values.clusterIdentification
            });
          }
        }
      }
    });
    return filterObj = {
      monitorType,
      filterList,
    };
  }

  public resetForm() {
    const { resetFields } = this.props.form;
    this.clearFormData();
    resetFields();
  }

  public resetFormValue(
    monitorType: string = null,
    clusterIdentification: any = null,
    topicName: string = null,
    consumerGroup: string = null,
    location: string = null) {
    const { setFieldsValue } = this.props.form;
    setFieldsValue({
      cluster: clusterIdentification,
      topic: topicName,
      consumerGroup,
      location,
      monitorType,
    });
  }

  public getClusterId = async (clusterIdentification: any) => {
    let clusterId = null;
    const index = cluster.clusterData.findIndex(item => item.clusterIdentification === clusterIdentification);
    if (index > -1) {
      clusterId = cluster.clusterData[index].clusterId;
    }
    if (clusterId) {
      await cluster.getClusterMetaTopics(clusterId);
      this.clusterId = clusterId;
      return this.clusterId;
    };
    return this.clusterId = clusterId as any;
  }

  public async initFormValue(monitorRule: IRequestParams) {
    const strategyFilterList = monitorRule.strategyFilterList;
    const clusterFilter = strategyFilterList.filter(item => item.tkey === 'cluster')[0];
    const topicFilter = strategyFilterList.filter(item => item.tkey === 'topic')[0];
    const consumerFilter = strategyFilterList.filter(item => item.tkey === 'consumerGroup')[0];

    const clusterIdentification = clusterFilter ? clusterFilter.tval[0] : null;
    const topic = topicFilter ? topicFilter.tval[0] : null;
    const consumerGroup = consumerFilter ? consumerFilter.tval[0] : null;
    const location: string = null;
    const monitorType = monitorRule.strategyExpressionList[0].metric;
    alarm.changeMonitorStrategyType(monitorType);
    //增加clusterIdentification替代原来的clusterName
    this.clusterIdentification = clusterIdentification;
    await this.getClusterId(this.clusterIdentification);
    //
    await this.handleSelectChange(topic, 'topic');
    await this.handleSelectChange(consumerGroup, 'consumerGroup');
    this.resetFormValue(monitorType, this.clusterIdentification, topic, consumerGroup, location);
  }

  public clearFormData() {
    this.monitorType = null;
    this.topicName = null;
    this.clusterId = null;
    this.consumerGroup = null;
    this.location = null;
    this.resetFormValue();
  }

  public async handleClusterChange(e: any) {
    this.clusterIdentification = e;
    this.topicName = null;
    topic.setLoading(true);
    const clusterId = await this.getClusterId(e);
    await cluster.getClusterMetaTopics(clusterId);
    this.resetFormValue(this.monitorType, e, null, this.consumerGroup, this.location);
    topic.setLoading(false);
  }

  public handleSelectChange = (e: string, type: 'topic' | 'consumerGroup' | 'location') => {
    switch (type) {
      case 'topic':
        // if (!this.clusterId) {
        //   return message.info('请选择集群');
        // }
        this.topicName = e;
        const type = this.dealMonitorType();
        if (['kafka-consumer-maxLag', 'kafka-consumer-maxDelayTime', 'kafka-consumer-lag'].indexOf(type) > -1) {
          this.getConsumerInfo();
        }
        break;
      case 'consumerGroup':
        this.consumerGroup = e;
        break;
      case 'location':
        this.location = e;
        break;
    }
  }

  public getConsumerInfo = () => {
    if (!this.clusterId || !this.topicName) {
      return;
    }
    topic.setLoading(true);
    if (IsNotNaN(this.clusterId)) {
      topic.getConsumerGroups(this.clusterId, this.topicName);
    }
    this.consumerGroup = null;
    this.location = null;
    this.resetFormValue(this.monitorType, this.clusterIdentification, this.topicName);
    topic.setLoading(false);
  }

  public dealMonitorType() {
    const index = alarm.monitorType.indexOf('-');
    let type = alarm.monitorType;
    if (index > -1) {
      type = type.substring(index + 1);
    }
    return type;
  }

  public getRenderItem() {
    const type = this.dealMonitorType();
    const showMore = ['kafka-consumer-maxLag', 'kafka-consumer-maxDelayTime', 'kafka-consumer-lag'].indexOf(type) > -1;
    this.monitorType = alarm.monitorType;

    const monitorType = {
      key: 'monitorType',
      label: '监控指标',
      type: 'select',
      options: alarm.monitorTypeList.map(item => ({
        label: item.metricName,
        value: item.metricName,
      })),
      attrs: {
        placeholder: '请选择',
        className: 'large-size',
        disabled: this.isDetailPage,
        optionFilterProp: 'children',
        showSearch: true,
        filterOption: (input: any, option: any) => {
          if (typeof option.props.children === 'object') {
            const { props } = option.props.children as any;
            return (props.children + '').toLowerCase().indexOf(input.toLowerCase()) >= 0;
          }
          return (option.props.children + '').toLowerCase().indexOf(input.toLowerCase()) >= 0;
        },
        onChange: (e: string) => this.handleTypeChange(e),
      },
      rules: [{ required: true, message: '请选择监控指标' }],
    } as IVritualScrollSelect;
    const clusterData = toJS(cluster.clusterData);
    const options = clusterData?.length ? clusterData.map(item => {
      return {
        label: `${item.clusterName}${item.description ? '（' + item.description + '）' : ''}`,
        value: item.clusterIdentification
      }
    }) : null;

    const clusterItem = {
      label: '集群',
      options,
      defaultValue: this.clusterIdentification,
      rules: [{ required: true, message: '请选择集群' }],
      attrs: {
        placeholder: '请选择集群',
        className: 'large-size',
        disabled: this.isDetailPage,
        onChange: (e: any) => this.handleClusterChange(e),
      },
      key: 'cluster',
    } as unknown as IVritualScrollSelect;

    const topicItem = {
      label: 'Topic',
      defaultValue: this.topicName,
      rules: [{ required: true, message: '请选择Topic' }],
      isDisabled: this.isDetailPage,
      options: cluster.clusterMetaTopics.map(item => {
        return {
          label: item.topicName,
          value: item.topicName,
        };
      }),
      attrs: {
        placeholder: '请选择Topic',
        className: 'large-size',
        disabled: this.isDetailPage,
        onChange: (e: string) => this.handleSelectChange(e, 'topic'),
      },
      key: 'topic',
    } as IVritualScrollSelect;

    const consumerGroupItem = {
      label: '消费组',
      options: topic.consumerGroups.map(item => {
        return {
          label: item.consumerGroup,
          value: item.consumerGroup,
        };
      }),
      defaultValue: this.consumerGroup,
      rules: [{ required: showMore, message: '请选择消费组' }],
      attrs: {
        placeholder: '请选择消费组',
        className: 'large-size',
        disabled: this.isDetailPage,
        onChange: (e: string) => this.handleSelectChange(e, 'consumerGroup'),
      },
      key: 'consumerGroup',
    } as IVritualScrollSelect;

    const locationItem = {
      label: 'location',
      options: topic.filterGroups.map(item => {
        return {
          label: item.location,
          value: item.location,
        };
      }),
      defaultValue: this.location,
      rules: [{ required: showMore, message: '请选择location' }],
      attrs: {
        placeholder: '请选择location',
        optionFilterProp: 'children',
        showSearch: true,
        className: 'middle-size',
        disabled: this.isDetailPage,
        onChange: (e: string) => this.handleSelectChange(e, 'location'),
      },
      key: 'location',
    } as IVritualScrollSelect;

    const common = (
      <>
        {this.renderFormItem(clusterItem)}
        {this.renderFormItem(topicItem)}
      </>
    );

    const more = showMore ? (
      <>
        {this.renderFormItem(consumerGroupItem)}
        {/* {this.renderFormItem(locationItem)} */}
      </>
    ) : null;
    return (
      <>
        <div className="dynamic-set">
          {this.renderFormItem(monitorType)}
          <ul>{common}{more}</ul>
        </div>
      </>
    );
  }

  public handleTypeChange = (e: string) => {
    // tslint:disable-next-line:no-unused-expression
    this.clearFormData();
    alarm.changeMonitorStrategyType(e);
  }

  public getSelectFormItem(item: IFormItem) {
    return (
      <Select
        key={item.key}
        {...item.attrs}
        {...searchProps}
      >
        {(item as IFormSelect).options && (item as IFormSelect).options.map((v, index) => (
          <Select.Option
            key={v.value || v.key || index}
            value={v.value}
          >
            {v.label?.length > 25 ? <Tooltip placement="bottomLeft" title={v.label}>
              {v.label}
            </Tooltip> : v.label}
          </Select.Option>
        ))}
      </Select>
    );
  }

  public renderFormItem(item: IVritualScrollSelect, virtualScroll: boolean = false) {
    const { getFieldDecorator } = this.props.form;
    const { formData = {} } = this.props;
    const initialValue = formData[item.key] === 0 ? 0 : (formData[item.key] || item.defaultValue || '');
    const getFieldValue = {
      initialValue,
      rules: item.rules || [{ required: true, message: '请填写' }],
    };
    const formItemLayout = {
      labelCol: { span: 6 },
      wrapperCol: { span: 10 },
    };
    return (
      <Form.Item
        label={item.label}
        key={item.key}
        {...formItemLayout}
      >
        {getFieldDecorator(item.key, getFieldValue)(
          virtualScroll ?
            <VirtualScrollSelect
              attrs={item.attrs}
              isDisabled={item.isDisabled}
              onChange={item.attrs.onChange}
              getData={item.getData}
              refetchData={item.refetchData}
            />
            : this.getSelectFormItem(item),
        )}
      </Form.Item>
    );
  }

  public componentDidMount() {
    cluster.getClusters();
  }

  public render() {
    return (
      <Spin spinning={cluster.filterLoading}>
        <Form>
          <div className="form-list">
            {this.getRenderItem()}
          </div>
        </Form>
      </Spin>
    );
  }
}

export const WrappedDynamicSetFilter = Form.create({ name: 'dynamic_filter_form_item' })(DynamicSetFilter);
