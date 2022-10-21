import React, { useState } from 'react';
import { useParams } from 'react-router-dom';
import { Button, Form, Input, Modal, Utils } from 'knowdesign';
import notification from '@src/components/Notification';
import { IconFont } from '@knowdesign/icons';
import Api from '@src/api/index';

// eslint-disable-next-line react/display-name
export default (props: { record: any; onConfirm?: () => void }) => {
  const { record, onConfirm } = props;
  const routeParams = useParams<{
    clusterId: string;
  }>();
  const [form] = Form.useForm();
  const [delDialogVisible, setDelDialogVisble] = useState(false);
  const handleDelOk = () => {
    form.validateFields().then((e) => {
      const formVal = form.getFieldsValue();
      formVal.clusterId = Number(routeParams.clusterId);
      Utils.delete(Api.deleteTopic(), { data: formVal }).then((res: any) => {
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
        onClick={(_) => {
          setDelDialogVisble(true);
        }}
      >
        删除
      </Button>
      <Modal
        className="custom-modal"
        title="确定要删除此Topic吗？"
        centered={true}
        visible={delDialogVisible}
        wrapClassName="del-topic-modal"
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
        <div className="tip-info">
          <IconFont type="icon-warning-circle"></IconFont>
          <span>会删除Topic的全部消息数据和ACL权限！请再次输入Topic名称进行确认！</span>
        </div>
        <Form form={form} labelCol={{ span: 5 }} style={{ marginTop: 18 }}>
          <Form.Item label="TopicName">{record.topicName}</Form.Item>
          <Form.Item
            name="topicName"
            label="TopicName"
            rules={[
              // { required: true },
              () => ({
                validator(_, value) {
                  if (!value) {
                    return Promise.reject(new Error('请输入Topic名称'));
                  } else if (value !== record.topicName) {
                    return Promise.reject(new Error('请输入正确的Topic名称'));
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
