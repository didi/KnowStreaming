import * as React from 'react';
import { admin } from 'store/admin';
import { notification, Modal, Form, Input, Switch, Select, Tooltip, Radio } from 'antd';
import { IBrokersMetadata, IBrokersRegions, IExpand } from 'types/base-type';
import { searchProps } from 'constants/table';
import { expandPartition } from 'lib/api';

const layout = {
  labelCol: { span: 6 },
  wrapperCol: { span: 15 },
};

interface IXFormProps {
  form: any;
  formData?: any;
  visible?: boolean;
  handleVisible?: any;
  clusterId?: number;
  deleteTopic?: any;
}

class CustomForm extends React.Component<IXFormProps> {
  // public state = {
  //   checked: false,
  // };

  // public onSwitchChange(checked: boolean) {
  //   this.setState({ checked });
  //   this.props.form.validateFields((err: any, values: any) => {
  //     checked ? values.brokerIdList = [] : values.regionId = '';
  //   });
  // }

  public handleExpandOk() {
    this.props.form.validateFields((err: any, values: any) => {
      const deleteData = this.props.formData;
      if (!err) {
        if (values.topicName !== this.props.formData.topicName) {
          notification.error({ message: 'topic名称不正确，请重新输入' });
        } else {
          this.props.handleVisible(false);
          // 调用删除接口
          this.props.deleteTopic(deleteData);
        }
      }
      // if (!err) {
      //   this.props.handleVisible(false);
      //   const params = {
      //     topicName: values.topicName,
      //     clusterId: this.props.clusterId,
      //     partitionNum: values.partitionNum,
      //   } as IExpand;
      //   if (values.brokerIdList) {
      //     params.brokerIdList = values.brokerIdList;
      //   } else {
      //     params.regionId = values.regionId;
      //   }
      //   const valueParams = [] as IExpand[];
      //   valueParams.push(params);
      //   expandPartition(valueParams).then(data => {
      //     notification.success({ message: '扩分成功' });
      //     this.props.form.resetFields();
      //     admin.getClusterTopics(this.props.clusterId);
      //   });
    });
  }

  public handleExpandCancel() {
    this.props.handleVisible(false);
    this.props.form.resetFields();
  }

  public componentDidMount() {
    admin.getBrokersMetadata(this.props.clusterId);
    admin.getBrokersRegions(this.props.clusterId);
  }

  public render() {
    const { formData = {} as any, visible } = this.props;
    const { getFieldDecorator } = this.props.form;
    let metadata = [] as IBrokersMetadata[];
    metadata = admin.brokersMetadata ? admin.brokersMetadata : metadata;
    let regions = [] as IBrokersRegions[];
    regions = admin.brokersRegions ? admin.brokersRegions : regions;
    return (
      <Modal
        title="提示"
        visible={visible}
        onOk={() => this.handleExpandOk()}
        onCancel={() => this.handleExpandCancel()}
        maskClosable={false}
        okText="确认"
        cancelText="取消"
      >
        <Form {...layout} name="basic" onSubmit={() => ({})} >
          {/* <Form.Item label="Topic名称" >
            {getFieldDecorator('topicName', {
              initialValue: formData.topicName,
              rules: [{ required: true, message: '请输入Topic名称' }],
            })(<Input disabled={true} placeholder="请输入Topic名称" />)}
          </Form.Item> */}

          {/* 运维管控-topic信息-扩分区操作 */}
          {/* <Form.Item label="所属region" >
            {getFieldDecorator('topicName', {
              initialValue: formData.topicName,
              rules: [{ required: true, message: '请输入所属region' }],
            })(<Input disabled={true} placeholder="请输入所属region" />)}
          </Form.Item> */}
          {/* 运维管控-topic信息-扩分区操作 */}
          <div style={{ textAlign: 'center', padding: '10px 0', fontSize: '13px', fontWeight: 'bold', color: 'red' }}>
            <span>由于删除Topic是高危操作,需再次输入Topic名称进行确认。</span>
          </div>
          <Form.Item label="Topic名称" >
            {getFieldDecorator('topicName', {
              rules: [{
                required: true,
                message: 'topic名称错误',
              }],
            })(<Input />)}
          </Form.Item>

          {/* <Form.Item label="brokerIdList" style={{ display: this.state.checked ? 'none' : '' }}>
            {getFieldDecorator('brokerIdList', {
              initialValue: formData.brokerIdList,
              rules: [{ required: !this.state.checked, message: '请输入brokerIdList' }],
            })(
              <Select
                mode="multiple"
                {...searchProps}
              >
                {metadata.map((v, index) => (
                  <Select.Option
                    key={v.brokerId || v.key || index}
                    value={v.brokerId}
                  >
                    {v.host.length > 16 ?
                      <Tooltip placement="bottomLeft" title={v.host}> {v.host} </Tooltip>
                      : v.host}
                  </Select.Option>
                ))}
              </Select>,
            )}

          </Form.Item>*/}
          {/* <Form.Item label="regionId" style={{ display: this.state.checked ? '' : 'none' }} >
            {getFieldDecorator('regionId', {
              initialValue: formData.regionId,
              rules: [{ required: this.state.checked, message: '请选择regionId' }],
            })(
              <Select {...searchProps}>
                {regions.map((v, index) => (
                  <Select.Option
                    key={v.id || v.key || index}
                    value={v.id}
                  >
                    {v.name.length > 16 ?
                      <Tooltip placement="bottomLeft" title={v.name}> {v.name} </Tooltip>
                      : v.name}
                  </Select.Option>
                ))}
              </Select>,
            )}
          </Form.Item> */}
        </Form>
      </Modal>
    );
  }
}

export const ConfirmDetailTopicFormWrapper = Form.create<IXFormProps>()(CustomForm);
