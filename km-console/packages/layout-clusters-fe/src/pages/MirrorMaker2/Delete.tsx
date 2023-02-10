import React, { useState } from 'react';
import { useParams } from 'react-router-dom';
import { Button, Form, Input, Modal, Utils } from 'knowdesign';
import notification from '@src/components/Notification';
import { IconFont } from '@knowdesign/icons';
import Api from '@src/api/index';

// eslint-disable-next-line react/display-name
const DeleteConnector = (props: { record: any; onConfirm?: () => void }) => {
  const { record, onConfirm } = props;
  const [form] = Form.useForm();
  const [delDialogVisible, setDelDialogVisble] = useState(false);
  const handleDelOk = () => {
    form.validateFields().then((e) => {
      const formVal = form.getFieldsValue();
      formVal.connectClusterId = Number(record.connectClusterId);
      Utils.delete(Api.mirrorMakerOperates, { data: formVal }).then((res: any) => {
        if (res === null) {
          notification.success({
            message: '删除成功',
          });
          setDelDialogVisble(false);
          onConfirm && onConfirm();
        } else {
          notification.error({
            message: '删除失败',
          });
        }
      });
    });
  };
  return (
    <>
      <Button
        type="link"
        size="small"
        onClick={(_) => {
          setDelDialogVisble(true);
        }}
      >
        删除
      </Button>
      <Modal
        className="custom-modal"
        title="确定删除此 MM2 任务吗？"
        centered={true}
        visible={delDialogVisible}
        wrapClassName="del-connect-modal"
        destroyOnClose={true}
        maskClosable={false}
        onOk={handleDelOk}
        onCancel={(_) => {
          setDelDialogVisble(false);
        }}
        okText="删除"
        okButtonProps={{
          danger: true,
          size: 'small',
          style: {
            paddingLeft: '16px',
            paddingRight: '16px',
          },
        }}
        cancelButtonProps={{
          size: 'small',
          style: {
            paddingLeft: '16px',
            paddingRight: '16px',
          },
        }}
      >
        <Form form={form} labelCol={{ span: 6 }} wrapperCol={{ span: 16 }} style={{ marginTop: 17 }}>
          <Form.Item label="MM2 Name">{record.connectorName}</Form.Item>
          <Form.Item
            name="connectorName"
            label="MM2 Name"
            rules={[
              // { required: true },
              () => ({
                validator(_, value) {
                  if (!value) {
                    return Promise.reject(new Error('请输入MM2 Name名称'));
                  } else if (value !== record.connectorName) {
                    return Promise.reject(new Error('请输入正确的MM2 Name名称'));
                  }
                  return Promise.resolve();
                },
              }),
            ]}
          >
            <Input placeholder="请输入" size="small"></Input>
          </Form.Item>
        </Form>
      </Modal>
    </>
  );
};

export default DeleteConnector;
