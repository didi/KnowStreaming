import React, { useState, useEffect, useRef } from 'react';
import { Utils, Drawer, Button, Form, Space, Divider, AppContainer, Input, Transfer, InputNumber } from 'knowdesign';
import message from '@src/components/Message';
import { IconFont } from '@knowdesign/icons';
import { CloseOutlined } from '@ant-design/icons';
import api from '../../api';
import './style/BalanceDrawer.less';
interface PropsType extends React.HTMLAttributes<HTMLDivElement> {
  onClose: () => void;
  visible: boolean;
  isCycle?: boolean;
  formData?: any;
  genData?: any;
}
const ClusterNorms: React.FC<PropsType> = ({ onClose, visible, genData }) => {
  const [global] = AppContainer.useGlobalValue();
  const [form] = Form.useForm();
  const [nodeData, setNodeData] = useState([]);
  const [nodeTargetKeys, setNodeTargetKeys] = useState([]);

  useEffect(() => {
    visible && getNodeList();
    visible && getTopicList();
  }, [visible]);

  const submit = () => {
    // 周期均衡
    form.validateFields().then((values) => {
      const params = values?.brokers?.map((item: any) => {
        const brokerId = nodeData?.filter((key) => key.brokerId === item)[0]?.brokerId;
        const newValue = brokerId !== undefined && { brokerId, cpu: values?.cpu, disk: values?.disk, flow: values?.flow };
        return {
          clusterId: global?.clusterInfo?.id + '',
          value: JSON.stringify(newValue),
          valueGroup: 'BROKER_SPEC',
          valueName: brokerId + '',
          description: '',
        };
      });

      Utils.put(api.putPlatformConfig(), params).then((res: any) => {
        const dataDe = res || [];
        onClose();
        message.success('设置集群规格成功');
        genData();
      });
    });
  };

  const nodeChange = (val: any) => {
    setNodeTargetKeys(val);
  };

  const getNodeList = () => {
    Utils.request(api.getBrokersMetaList(global?.clusterInfo?.id), {
      method: 'GET',
    }).then((res: any) => {
      const dataDe = res || [];
      const dataHandle = dataDe.map((item: any) => {
        return {
          ...item,
          key: item.brokerId,
          title: `${item.brokerId} (${item.host})`,
        };
      });
      setNodeData(dataHandle);
    });
  };

  const getTopicList = () => {
    Utils.request(api.getPlatformConfig(global?.clusterInfo?.id, 'BROKER_SPEC')).then((res: any) => {
      const targetKeys = res?.map((item: any) => {
        return JSON.parse(item.value).brokerId;
      });
      setNodeTargetKeys(targetKeys || []);
      const newValues = JSON.parse(res?.[0].value);
      const fieldValue = {
        cpu: newValues?.cpu,
        disk: newValues?.disk,
        flow: newValues?.flow,
        brokers: targetKeys || [],
      };
      form.setFieldsValue(fieldValue);
    });
  };

  return (
    <>
      <Drawer
        title={'设置集群规格'}
        width="600px"
        destroyOnClose={true}
        className="balance-drawer"
        onClose={onClose}
        visible={visible}
        maskClosable={false}
        extra={
          <Space>
            <Button size="small" onClick={onClose}>
              取消
            </Button>
            <Button type="primary" size="small" disabled={false} onClick={submit}>
              确定
            </Button>

            <Divider type="vertical" />
          </Space>
        }
      >
        <Form
          form={form}
          layout="vertical"
          preserve={false}
          initialValues={{
            status: 1,
          }}
        >
          <Form.Item
            name="brokers"
            rules={[
              {
                required: true,
                message: `请选择!`,
              },
            ]}
          >
            <Transfer
              dataSource={nodeData}
              showSearch
              filterOption={(inputValue, option) => option.host.indexOf(inputValue) > -1}
              targetKeys={nodeTargetKeys}
              onChange={nodeChange}
              render={(item) => item.title}
              titles={['待选节点', '已选节点']}
              customHeader
              showSelectedCount
              locale={{ itemUnit: '', itemsUnit: '' }}
              suffix={<IconFont type="icon-fangdajing" />}
            />
          </Form.Item>
          <Form.Item
            name="cpu"
            label="单机核数"
            rules={[
              {
                required: true,
                message: `请输入!`,
              },
            ]}
          >
            <InputNumber decimalSeparator={'0'} min={0} max={99999} placeholder="请输入单机核数" addonAfter="C" style={{ width: '100%' }} />
          </Form.Item>
          <Form.Item
            name="disk"
            label="单机磁盘"
            rules={[
              {
                required: true,
                message: `请输入!`,
              },
            ]}
          >
            <InputNumber min={0} max={99999} placeholder="请输入磁盘大小" addonAfter="GB" style={{ width: '100%' }} />
          </Form.Item>
          <Form.Item
            name="flow"
            label="单机网络"
            rules={[
              {
                required: true,
                message: `请输入!`,
              },
            ]}
          >
            <InputNumber min={0} max={99999} placeholder="请输入单机网络" addonAfter="MB/s" style={{ width: '100%' }} />
          </Form.Item>
        </Form>
      </Drawer>
    </>
  );
};

export default ClusterNorms;
