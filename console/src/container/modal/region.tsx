import { Modal, Form, Row, Select, Input, message } from 'component/antd';
import { IRegionData, statusMap, region } from 'store/region';
import { cluster } from 'store/cluster';
import urlQuery from 'store/url-query';
import { observer } from 'mobx-react';
import { modal } from 'store';
import React from 'react';
import { addRegion, modifyRegion } from 'lib/api';
import { broker } from 'store/broker';
import { IValueLabel } from 'types/base-type';

const regionFormItemLayout = {
  labelCol: {
    span: 7,
  },
  wrapperCol: {
    span: 11,
  },
};

@observer
export class Region extends React.Component<any> {
  public componentDidMount() {
    cluster.getClusters();
    broker.initBrokerOptions(urlQuery.clusterId);
  }

  public handleSubmit = (e: React.MouseEvent<any, MouseEvent>) => {
    e.preventDefault();
    this.props.form.validateFieldsAndScroll((err: any, values: any) => {
      if (err) return;
      const commonFn = (fn: any) => {
        fn.then(() => {
          message.success(this.getTips());
          region.getRegions(urlQuery.clusterId);
          modal.close();
        });
      };
      modal.regionData ?
        commonFn(modifyRegion(Object.assign(values, { regionId: modal.regionData.regionId }))) : commonFn(addRegion(values));
    });
  }

  public getTips() {
    if (modal.regionData) return '修改成功';
    return '添加成功';
  }

  public getTitle() {
    if (modal.regionData) return '修改Region';
    return '添加Region';
  }

  public render() {
    const { getFieldDecorator } = this.props.form;
    let title = '新增Region';
    if (modal.regionData) title = '更新Region';
    const regionData = modal.regionData || {} as IRegionData;
    return (
      <Modal
        title={title}
        style={{ top: 70 }}
        visible={true}
        onCancel={modal.close}
        maskClosable={false}
        width={700}
        destroyOnClose={true}
        okText="确定"
        cancelText="取消"
        onOk={this.handleSubmit}
      // confirmLoading={loading}
      >
        <Form {...regionFormItemLayout} >
          <Row>
            <Form.Item
              label="Region名称"
            >
              {getFieldDecorator('regionName', {
                rules: [{ required: true, message: '请输入Region名称' }],
                initialValue: regionData.regionName,
              })(
                <Input placeholder="请输入Region名称" />,
              )}
            </Form.Item>
          </Row>
          <Row>
            <Form.Item
              label="集群"
            >
              {getFieldDecorator('clusterId', {
                rules: [{ required: true, message: '请选择集群' }],
                initialValue: urlQuery.clusterId,
              })(
                <Select disabled={true}>
                  {
                    cluster.data.slice(1).map(c => {
                      return <Select.Option value={c.clusterId} key={c.clusterId}>{c.clusterName}</Select.Option>;
                    })
                  }
                </Select>,
              )}
            </Form.Item>
          </Row>
          <Row>
            <Form.Item
              label="BrokerList"
            >
              {getFieldDecorator('brokerIdList', {
                rules: [{ required: true, message: '请输入brokerList' }],
                initialValue: regionData.brokerIdList,
              })(
                <Select mode="multiple" optionLabelProp="title">
                  {
                    broker.BrokerOptions.map((t: IValueLabel) => {
                      return <Select.Option value={t.value} title={t.value + ''} key={t.value} >
                        <span aria-label={t.value}> {t.label} </span>
                      </Select.Option>;
                    })
                  }
                </Select>,
              )}
            </Form.Item>
          </Row>
          <Row>
            <Form.Item
              label="状态"
            >
              {getFieldDecorator('status', {
                initialValue: regionData.status || 0,
              })(
                <Select>
                  {statusMap.map((ele, index) => {
                    return <Select.Option key={index} value={index - 1}>{ele}</Select.Option>;
                  })}
                </Select>,
              )}
            </Form.Item>
          </Row>
          <Row>
            <Form.Item
              label="等级"
            >
              {getFieldDecorator('level', {
                initialValue: regionData.level || 0,
              })(
                <Select>
                  <Select.Option value={0}>普通</Select.Option>
                  <Select.Option value={1}>重要</Select.Option>
                </Select>,
              )}
            </Form.Item>
          </Row>
          <Row>
            <Form.Item
              label="备注"
            >
              {getFieldDecorator('description', {
                rules: [{ required: false }],
                initialValue: regionData.description,
              })(
                <Input.TextArea />,
              )}
            </Form.Item>
          </Row>
        </Form>
      </Modal>
    );
  }
}

export default Form.create({ name: 'Region' })(Region);
