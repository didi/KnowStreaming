import React, { useState, useEffect } from 'react';
import { Button, DatePicker, Drawer, Form, notification, Radio, Utils, Space, Divider } from 'knowdesign';
import { useParams } from 'react-router-dom';
import EditTable from '../TestingProduce/component/EditTable';
import Api from '@src/api/index';
import moment from 'moment';

const CustomSelectResetTime = (props: { value?: string; onChange?: (val: Number | String) => void }) => {
  const { value, onChange } = props;
  const [timeSetMode, setTimeSetMode] = useState('newest');
  useEffect(() => {
    onChange('newest');
  }, []);
  return (
    <>
      <Radio.Group
        style={{
          marginBottom: 20,
        }}
        onChange={(e) => {
          setTimeSetMode(e.target.value);
          if (e.target.value === 'newest') {
            onChange('newest');
          }
        }}
        value={timeSetMode}
      >
        <Radio value={'newest'}>最新Offset</Radio>
        <Radio value={'custom'}>自定义</Radio>
      </Radio.Group>
      {timeSetMode === 'custom' && (
        <DatePicker
          value={moment(value === 'newest' ? Date.now() : value)}
          style={{ width: '100%' }}
          showTime={true}
          onChange={(v) => {
            onChange(v.valueOf());
          }}
        ></DatePicker>
      )}
    </>
  );
};

export default (props: any) => {
  const { record } = props;
  const routeParams = useParams<{
    clusterId: string;
  }>();
  const [form] = Form.useForm();
  const defaultResetType = 'assignedTime';
  const [resetType, setResetType] = useState(defaultResetType);
  const [resetOffsetVisible, setResetOffsetVisible] = useState(false);
  const customFormRef: any = React.createRef();
  const clusterPhyId = Number(routeParams.clusterId);
  useEffect(() => {
    form.setFieldsValue({
      resetType: defaultResetType,
    });
  }, []);
  const confirm = () => {
    let tableData;
    if (customFormRef.current) {
      tableData = customFormRef.current.getTableData();
    }
    const formData = form.getFieldsValue();
    let resetParams: any = {
      clusterId: clusterPhyId,
      createIfNotExist: false,
      groupName: record.groupName,
      topicName: record.topicName,
    };
    if (formData.resetType === 'assignedTime') {
      resetParams.resetType = formData.timestamp === 'newest' ? 0 : 2;
      if (resetParams.resetType === 2) {
        resetParams.timestamp = formData.timestamp;
      }
    }
    if (formData.resetType === 'partition') {
      resetParams.resetType = 3;
      resetParams.offsetList = tableData
        ? tableData.map((item: { key: string; value: string }) => ({ partitionId: item.key, offset: item.value }))
        : [];
    }
    Utils.put(Api.resetGroupOffset(), resetParams).then((data) => {
      if (data === null) {
        notification.success({
          message: '重置offset成功',
        });
        setResetOffsetVisible(false);
      } else {
        notification.error({
          message: '重置offset失败',
        });
        setResetOffsetVisible(false);
      }
    });
  };
  return (
    <>
      <Button
        size="small"
        type="primary"
        onClick={(_) => {
          setResetOffsetVisible(true);
        }}
      >
        重置Offset
      </Button>

      <Drawer
        title="重置Offset"
        width={480}
        visible={resetOffsetVisible}
        maskClosable={false}
        extra={
          <Space>
            <Button
              size="small"
              style={{ marginRight: 8 }}
              onClick={(_) => {
                setResetOffsetVisible(false);
              }}
            >
              取消
            </Button>
            <Button size="small" type="primary" onClick={confirm}>
              确定
            </Button>
            <Divider type="vertical" />
          </Space>
        }
        className="cluster-detail-consumer-resetoffset"
        onClose={(_) => {
          setResetOffsetVisible(false);
        }}
      >
        <Form form={form} labelCol={{ span: 5 }} layout="vertical" className="reset-offset-form">
          <Form.Item name="resetType" label="重置类型" required>
            <Radio.Group
              defaultValue="assignedTime"
              value={resetType}
              onChange={(e) => {
                setResetType(e.target.value);
              }}
            >
              <Radio value={'assignedTime'}>重置到指定时间</Radio>
              <Radio value={'partition'}>重置分区</Radio>
            </Radio.Group>
          </Form.Item>
          {resetType === 'assignedTime' && (
            <Form.Item name="timestamp" label="时间" required>
              <CustomSelectResetTime />
            </Form.Item>
          )}
          {resetType === 'partition' && (
            <Form.Item name="partition" label="分区及偏移" required>
              <EditTable
                ref={customFormRef}
                colCustomConfigs={[
                  {
                    title: 'PartitionID',
                    inputType: 'number',
                    placeholder: '请输入Partition',
                  },
                  {
                    title: 'Offset',
                    inputType: 'number',
                    placeholder: '请输入Offset',
                  },
                ]}
              ></EditTable>
            </Form.Item>
          )}
        </Form>
      </Drawer>
    </>
  );
};
