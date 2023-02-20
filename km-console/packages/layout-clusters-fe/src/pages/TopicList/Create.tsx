import React, { useState, useEffect } from 'react';
import { useParams } from 'react-router-dom';
import { Alert, Button, Checkbox, Divider, Drawer, Form, Input, InputNumber, Modal, Select, Utils, Radio, AppContainer } from 'knowdesign';
import notification from '@src/components/Notification';
import { PlusOutlined, DownOutlined, UpOutlined } from '@ant-design/icons';
import Api from '@src/api/index';
import { ControlStatusMap } from '../CommonRoute';

const CheckboxGroup = Checkbox.Group;
const RadioGroup = Radio.Group;

interface DefaultConfig {
  name: string;
  defaultValue: string;
  readOnly: boolean;
}

const { Option } = Select;
const { TextArea } = Input;
const timeUnitMap = {
  ms: 1,
  min: 60 * 1000,
  h: 60 * 60 * 1000,
  day: 24 * 60 * 60 * 1000,
};
const customDefaultFields = ['retention.ms', 'cleanup.policy', 'max.message.bytes'];
function sizeof(str: string, charset?: string) {
  let total = 0,
    charCode,
    i,
    len;
  charset = charset ? charset.toLowerCase() : '';
  if (charset === 'utf-16' || charset === 'utf16') {
    for (i = 0, len = str.length; i < len; i++) {
      charCode = str.charCodeAt(i);
      if (charCode <= 0xffff) {
        total += 2;
      } else {
        total += 4;
      }
    }
  } else {
    for (i = 0, len = str.length; i < len; i++) {
      charCode = str.charCodeAt(i);
      if (charCode <= 0x007f) {
        total += 1;
      } else if (charCode <= 0x07ff) {
        total += 2;
      } else if (charCode <= 0xffff) {
        total += 3;
      } else {
        total += 4;
      }
    }
  }
  return total;
}
export default (props: any) => {
  const { onConfirm } = props;
  const [retentionTime, setRetentionTime] = useState(0);
  const [retentionTimeUnit, setRetentionTimeUnit] = useState<'ms' | 'min' | 'h' | 'day'>('ms');
  const [addTopicVisible, setAddTopicVisible] = useState(false);
  const [testMessageSizeModalVisible, setTestMessageSizeModalVisible] = useState(false);
  const [testMessage, setTestMessage] = useState('');
  const [moreConfigOpenStatus, setMoreConfigOpenStatus] = useState('close');
  const [defaultConfigs, setDefaultConfigs] = useState<Array<DefaultConfig>>([]);
  const [form] = Form.useForm();
  const [originDefaultConfigs, setOriginDefaultConfigs] = useState<any>({});
  const routeParams = useParams<{
    clusterId: string;
  }>();
  const [global] = AppContainer.useGlobalValue();
  const multiCleanupPolicy = global.isShowControl && global.isShowControl(ControlStatusMap.CREATE_TOPIC_CLEANUP_POLICY);
  const confirm = () => {
    form.validateFields().then((e) => {
      const formVal = JSON.parse(JSON.stringify(form.getFieldsValue()));
      formVal.clusterId = Number(routeParams.clusterId);
      formVal.properties['retention.ms'] = retentionTime * timeUnitMap[retentionTimeUnit];
      Object.keys(originDefaultConfigs).forEach((k) => {
        if (formVal.properties[k] === originDefaultConfigs[k]) {
          delete formVal.properties[k];
        }
      });
      Object.keys(formVal.properties).forEach((k) => {
        if (Array.isArray(formVal.properties[k])) {
          formVal.properties[k] = formVal.properties[k].join(',');
        }
      });
      Utils.post(Api.addTopic(), formVal).then((res: any) => {
        if (res === null) {
          notification.success({
            message: '创建成功',
          });
          onConfirm && onConfirm();
          form.resetFields();
          setAddTopicVisible(false);
        } else {
          notification.error({
            message: '创建失败',
          });
        }
      });
    });
  };
  useEffect(() => {
    addTopicVisible &&
      Utils.request(Api.getDefaultTopicConfig(Number(routeParams.clusterId))).then((res: Array<DefaultConfig>) => {
        setDefaultConfigs(res);
        const defaultConfigValue = res.map((item) => {
          let res: any;
          try {
            res =
              item.name === 'cleanup.policy' && multiCleanupPolicy
                ? item.defaultValue
                    .replace(/\[|\]|\s+/g, '')
                    .split(',')
                    .filter((_) => _)
                : item.defaultValue;
          } catch (e) {
            res = [];
          }
          return {
            name: ['properties', item.name],
            value: res,
          };
        });
        setOriginDefaultConfigs(
          res.reduce((acc, cur) => {
            acc[cur.name] = cur.defaultValue;
            return acc;
          }, originDefaultConfigs)
        );
        const newRetentionTime: any =
          Utils.transUnitTimePro(Number(res.find((item) => item.name === 'retention.ms').defaultValue), 2) || '';
        setRetentionTime(newRetentionTime?.value);
        setRetentionTimeUnit(newRetentionTime?.unit);
        form.setFieldsValue({ saveTime: newRetentionTime?.value, saveTimeUnit: newRetentionTime?.unit });
        form.setFields([...defaultConfigValue]);
      });
  }, [addTopicVisible]);
  return (
    <>
      <Button
        type="primary"
        className="add-btn"
        icon={<PlusOutlined />}
        onClick={() => {
          setAddTopicVisible(true);
        }}
      >
        新增Topic
      </Button>
      <Drawer
        title="创建Topic"
        width={800}
        className="cluster-topic-add"
        visible={addTopicVisible}
        maskClosable={false}
        destroyOnClose={true}
        extra={
          <div className="operate-wrap">
            <Button
              size="small"
              style={{ marginRight: 8 }}
              onClick={(_) => {
                form.resetFields();
                setAddTopicVisible(false);
              }}
            >
              取消
            </Button>
            <Button size="small" type="primary" onClick={confirm}>
              确定
            </Button>
            <Divider type="vertical" />
          </div>
        }
        onClose={(_) => {
          setAddTopicVisible(false);
        }}
      >
        <Form form={form} layout="vertical" validateTrigger="onBlur">
          <Form.Item
            name="topicName"
            label="Topic名称"
            rules={[
              { required: true, message: '' },
              // { pattern: /[\u4e00-\u9fa5\w\-]+/, message: 'Topic名称只能由中英文、下划线、短划线（-）组成' },
              () => ({
                validator(_, value) {
                  if (!value) {
                    return Promise.reject(`Topic名称不能为空`);
                  } else if (!/[\u4e00-\u9fa5\w\-]+/.test(value)) {
                    return Promise.reject(`Topic名称只能由中英文、下划线、短划线（-）组成`);
                  } else if (!/^[a-zA-Z]/.test(value)) {
                    return Promise.reject('Topic名称须以字母开头');
                  } else if (value.length < 3 || value.length > 128) {
                    return Promise.reject('Topic名称长度限制为3～128字符');
                  } else {
                    const validPromise = new Promise((resolve, reject) => {
                      Utils.request(Api.getTopicMetadata(Number(routeParams.clusterId), value))
                        .then((res: any) => {
                          if (res.exist) {
                            reject(`Topic名称重复`);
                          } else {
                            resolve('');
                          }
                        })
                        .catch((e) => {
                          resolve('');
                        });
                    });
                    return validPromise;
                  }
                },
              }),
            ]}
          >
            <Input placeholder="请输入Topic名称"></Input>
          </Form.Item>
          <Form.Item name="description" label="Topic描述" rules={[{ required: true, message: '请输入Topic描述' }]}>
            <Input.TextArea style={{ height: 80 }} placeholder="请输入Topic描述。例如，数据来源、使用方式、预计流量等"></Input.TextArea>
          </Form.Item>
          <div className="create-topic-flex-layout">
            <Form.Item name="partitionNum" label="分区数" rules={[{ required: true, message: '请输入分区数' }]}>
              <InputNumber min={1} style={{ width: '100%' }} addonAfter="个" />
            </Form.Item>
            <Form.Item name="replicaNum" label="副本数" rules={[{ required: true, message: '请输入副本数' }]}>
              <InputNumber min={1} style={{ width: '100%' }} addonAfter="个" />
            </Form.Item>
          </div>
          <Form.Item className="data-save-time-label" name="dataSaveTime">
            <div className="data-save-time-wrap">
              <Form.Item name="saveTime" label={'数据保存时间'} rules={[{ required: true, message: '请输入数据保存时间' }]}>
                <InputNumber
                  min={0}
                  className="num"
                  value={retentionTime}
                  onChange={(time) => {
                    setRetentionTime(time);
                  }}
                />
              </Form.Item>
              <Form.Item
                name="saveTimeUnit"
                label={' '}
                rules={[
                  {
                    validator: (r: any, v: any) => {
                      if (!v) {
                        return Promise.reject(`请选择时间格式`);
                      }
                      return Promise.resolve('');
                    },
                  },
                ]}
              >
                <Select
                  className="unit"
                  value={retentionTimeUnit}
                  onChange={(unit) => {
                    setRetentionTimeUnit(unit);
                  }}
                >
                  <Option value="ms">ms</Option>
                  <Option value="min">min</Option>
                  <Option value="h">h</Option>
                  <Option value="day">day</Option>
                </Select>
              </Form.Item>
            </div>
          </Form.Item>
          <Form.Item name={['properties', 'cleanup.policy']} label="清理策略" rules={[{ required: true, message: '请输入清理策略' }]}>
            {multiCleanupPolicy ? (
              <CheckboxGroup>
                <Checkbox style={{ marginRight: '65px' }} value="delete">
                  delete
                </Checkbox>
                <Checkbox value="compact">compact</Checkbox>
              </CheckboxGroup>
            ) : (
              <RadioGroup>
                <Radio style={{ marginRight: '65px' }} value="delete">
                  delete
                </Radio>
                <Radio value="compact">compact</Radio>
              </RadioGroup>
            )}
          </Form.Item>
          <div className="more-config">
            <div className="txt">更多配置</div>
            {/* <div className="horizontal-line"></div> */}
            <div
              className="switch-expand"
              onClick={(_) => {
                setMoreConfigOpenStatus(moreConfigOpenStatus === 'open' ? 'close' : 'open');
              }}
            >
              <span className="txt">{moreConfigOpenStatus === 'open' ? '收起' : '展开'}</span>
              {moreConfigOpenStatus === 'open' ? <UpOutlined /> : <DownOutlined />}
            </div>
          </div>
          <div style={{ display: moreConfigOpenStatus === 'open' ? 'block' : 'none' }}>
            <Alert
              className="tip-info"
              message="不知道单条消息大小?"
              type="warning"
              showIcon
              action={
                <div
                  className="test-right-away"
                  onClick={(_) => {
                    setTestMessageSizeModalVisible(true);
                  }}
                >
                  立即测试
                </div>
              }
            />
            <div className="create-topic-flex-layout">
              <Form.Item name={['properties', 'max.message.bytes']} label="Max message size">
                <InputNumber
                  min={0}
                  style={{ width: '100%' }}
                  formatter={(value) => `${value}KB`}
                  // @ts-ignore
                  parser={(value) => value.replace('KB', '')}
                />
              </Form.Item>
              {defaultConfigs
                .filter((dc) => !customDefaultFields.includes(dc.name))
                .map((configItem, i) => (
                  <Form.Item
                    key={i}
                    name={['properties', configItem.name]}
                    label={configItem.name.slice(0, 1).toUpperCase() + configItem.name.slice(1)}
                  >
                    <Input />
                  </Form.Item>
                ))}
            </div>
          </div>
        </Form>
      </Drawer>
      <Modal
        wrapClassName="msg-size-info-modal custom-modal"
        title="消息大小测试"
        visible={testMessageSizeModalVisible}
        centered={true}
        footer={null}
        onCancel={(_) => {
          setTestMessageSizeModalVisible(false);
        }}
      >
        <div className="msg-size-info">在下方输入框内输入消息样例即可计算出消息大小</div>
        <TextArea
          rows={16}
          value={testMessage}
          onChange={(e) => {
            setTestMessage(e.target.value);
          }}
        />
        <div className="statistics-info">
          <span>字符数：{testMessage.length}</span>
          <span>大小：{sizeof(testMessage)}Bytes</span>
        </div>
      </Modal>
    </>
  );
};
