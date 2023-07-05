import React, { useEffect } from 'react';
import { Drawer, Form, Input, Space, Button, Checkbox, Utils, Row, Col, Divider } from 'knowdesign';
import message from '@src/components/Message';
import { IconFont } from '@knowdesign/icons';
import { useParams } from 'react-router-dom';
import Api from '@src/api';
export const ConfigurationEdit = (props: any) => {
  const urlParams = useParams<any>();
  const [form] = Form.useForm();
  const onClose = () => {
    props.setVisible(false);
  };

  const onOk = () => {
    form.validateFields().then((res: any) => {
      const data = {
        applyAll: res.applyAll,
        brokerId: Number(props.hashData?.brokerId),
        changedProps: {
          [props.record?.name]: res.newValue,
        },
        clusterId: Number(urlParams.clusterId),
      };
      Utils.put(Api.getBrokerEditConfig(), data)
        .then((res: any) => {
          message.success('编辑配置成功');
          props.setVisible(false);
          props.genData({ pageNo: props.pagination.current, pageSize: props.pagination.pageSize });
        })
        .catch((err: any) => {
          console.log(err, 'err');
        });
    });
  };

  React.useEffect(() => {
    form.setFieldsValue(props.record);
  }, [props.record]);

  return (
    <Drawer
      title={
        <Space size={0}>
          <Button className="drawer-title-left-button" type="text" size="small" icon={<IconFont type="icon-fanhui1" />} onClick={onClose} />
          <Divider type="vertical" />
          <span style={{ paddingLeft: '5px' }}>编辑配置</span>
        </Space>
      }
      width={580}
      visible={props.visible}
      onClose={() => props.setVisible(false)}
      maskClosable={false}
      destroyOnClose
      extra={
        <Space>
          <Button size="small" onClick={onClose}>
            取消
          </Button>
          <Button size="small" type="primary" onClick={onOk}>
            确认
          </Button>
          <Divider type="vertical" />
        </Space>
      }
    >
      <Row gutter={[12, 12]} className="desc-row">
        <Col span={3} className="label-col">
          配置名:
        </Col>
        <Col span={21} className="value-col">
          {props.record?.name || '-'}
        </Col>
        <Col span={3} className="label-col">
          描述:
        </Col>
        <Col span={21} className="value-col">
          {props.record?.documentation || '-'}
        </Col>
      </Row>
      <Form form={form} layout="vertical">
        <Form.Item name="defaultValue" label="Kafka默认配置">
          <Input disabled />
        </Form.Item>
        <Form.Item name="value" label="当前配置">
          <Input disabled />
        </Form.Item>
        <Form.Item name="newValue" label="新配置" rules={[{ required: true, message: '请输入新的配置值！！！' }]}>
          <Input />
        </Form.Item>
        <Form.Item name="applyAll" valuePropName="checked">
          <Checkbox defaultChecked={false}>应用到全部Broker</Checkbox>
        </Form.Item>
      </Form>
    </Drawer>
  );
};
