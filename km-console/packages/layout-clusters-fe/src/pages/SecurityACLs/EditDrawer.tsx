import React, { forwardRef, useEffect, useImperativeHandle, useRef, useState } from 'react';
import { Button, Form, Input, Select, Drawer, Space, Divider, Utils, Radio, AutoComplete, Alert } from 'knowdesign';
import message from '@src/components/Message';
import api from '@src/api';
import { useParams } from 'react-router-dom';
import { UsersProps } from '../SecurityUsers';

// 字段对应后端存储值的枚举类型
export enum ACL_OPERATION {
  Unknown,
  Any,
  All,
  Read,
  Write,
  Create,
  Delete,
  Alter,
  Describe,
  ClusterAction,
  DescribeConfigs,
  AlterConfigs,
  IdempotentWrite,
}
export enum ACL_PERMISSION_TYPE {
  Unknown,
  Any,
  Deny,
  Allow,
}
export enum ACL_PATTERN_TYPE {
  Unknown,
  Any,
  Match,
  Literal,
  Prefixed,
}
export enum ACL_RESOURCE_TYPE {
  Unknown,
  Any,
  Topic,
  Group,
  Cluster,
  TransactionalId,
  DelegationToken,
}

export type RESOURCE_MAP_KEYS = Exclude<keyof typeof ACL_RESOURCE_TYPE, 'Unknown' | 'Any' | 'DelegationToken'>;

// 资源类型和操作映射表
export const RESOURCE_TO_OPERATIONS_MAP: {
  [P in RESOURCE_MAP_KEYS]: string[];
} = {
  Cluster: ['Alter', 'AlterConfigs', 'ClusterAction', 'Create', 'Describe', 'DescribeConfigs', 'IdempotentWrite'],
  Topic: ['Alter', 'AlterConfigs', 'Create', 'Delete', 'Describe', 'DescribeConfigs', 'Read', 'Write'],
  Group: ['Delete', 'Describe', 'Read'],
  TransactionalId: ['Write', 'Describe'],
};

// ACL 配置类型
const CONFIG_TYPE = [
  {
    label: '配置生产权限',
    value: 'produce',
  },
  {
    label: '配置消费权限',
    value: 'consume',
  },
  {
    label: '配置自定义权限',
    value: 'custom',
  },
];

// eslint-disable-next-line react/display-name
const AddDrawer = forwardRef((_, ref) => {
  const { clusterId } = useParams<{
    clusterId: string;
  }>();
  const [form] = Form.useForm();
  const [visible, setVisible] = useState<boolean>(false);
  const [kafkaUserOptions, setKafkaUserOptions] = useState<{ label: string; value: string }[]>([]);
  const [confirmLoading, setConfirmLoading] = useState<boolean>(false);
  const callback = useRef(() => {
    return;
  });
  const [topicMetaData, setTopicMetaData] = React.useState([]);
  const [groupMetaData, setGroupMetaData] = React.useState([]);

  // 获取 Topic 元信息
  const getTopicMetaData = (newValue: any) => {
    Utils.request(api.getTopicMetaData(+clusterId), {
      method: 'GET',
      params: { searchKeyword: newValue },
    }).then((res: UsersProps[]) => {
      const topics = (res || []).map((item: any) => {
        return {
          label: item.topicName,
          value: item.topicName,
        };
      });
      setTopicMetaData(topics);
    });
  };

  // 获取 Group 元信息
  const getGroupMetaData = () => {
    Utils.request(api.getGroupOverview(+clusterId), {
      method: 'GET',
    }).then((res: any) => {
      const groups = res?.bizData.map((item: any) => {
        return {
          label: item.name,
          value: item.name,
        };
      });
      setGroupMetaData(groups);
    });
  };

  // 获取 kafkaUser 列表
  const getKafkaUserList = () => {
    Utils.request(api.getKafkaUsers(clusterId), {
      method: 'GET',
    }).then((res: UsersProps[]) => {
      setKafkaUserOptions(res.map(({ name }) => ({ label: name, value: name })));
    });
  };

  // 提交表单
  const onSubmit = () => {
    form.validateFields().then((formData) => {
      const submitData = [];
      const { configType, principle, kafkaUser } = formData;
      if (configType === 'custom') {
        // 1. 自定义权限
        // TODO: 需要和后端联调
        const {
          resourceType,
          resourcePatternType,
          aclPermissionType,
          aclOperation,
          aclClientHost,
          cluster,
          topicName,
          topicPatternType,
          groupName,
          groupPatternType,
          transactionalId,
          transactionalIdPatternType,
        } = formData;
        submitData.push({
          clusterId,
          kafkaUser: principle === 'all' ? '*' : kafkaUser,
          resourceType,
          resourcePatternType: cluster
            ? 3
            : topicPatternType
            ? topicPatternType
            : groupPatternType
            ? groupPatternType
            : transactionalIdPatternType,
          resourceName: cluster ? cluster : topicName ? topicName : groupName ? groupName : transactionalId,
          aclPermissionType,
          aclOperation,
          aclClientHost,
        });
      } else {
        // 2. 生产或者消费权限
        // 1). 配置生产权限将赋予 User 对应 Topic 的 Create、Write 权限
        // 2). 配置消费权限将赋予 User 对应 Topic的 Read 权限和 Group 的 Read 权限
        const { topicPatternType, topicPrinciple, topicName } = formData;
        submitData.push({
          clusterId,
          kafkaUser: principle === 'all' ? '*' : kafkaUser,
          resourceType: ACL_RESOURCE_TYPE.Topic,
          resourcePatternType: topicPatternType,
          resourceName: topicPrinciple === 'all' ? '*' : topicName,
          aclPermissionType: ACL_PERMISSION_TYPE.Allow,
          aclOperation: configType === 'consume' ? ACL_OPERATION.Read : ACL_OPERATION.Create,
          aclClientHost: '*',
        });
        // 消费权限
        if (configType === 'consume') {
          const { groupPatternType, groupPrinciple, groupName } = formData;
          submitData.push({
            clusterId,
            kafkaUser: principle === 'all' ? '*' : kafkaUser,
            resourceType: ACL_RESOURCE_TYPE.Group,
            resourcePatternType: groupPatternType,
            resourceName: groupPrinciple === 'all' ? '*' : groupName,
            aclPermissionType: ACL_PERMISSION_TYPE.Allow,
            aclOperation: ACL_OPERATION.Read,
            aclClientHost: '*',
          });
        } else {
          submitData.push({
            clusterId,
            kafkaUser: principle === 'all' ? '*' : kafkaUser,
            resourceType: ACL_RESOURCE_TYPE.Topic,
            resourcePatternType: topicPatternType,
            resourceName: topicPrinciple === 'all' ? '*' : topicName,
            aclPermissionType: ACL_PERMISSION_TYPE.Allow,
            aclOperation: ACL_OPERATION.Write,
            aclClientHost: '*',
          });
        }
      }

      setConfirmLoading(true);
      Utils.request(api.addACL, {
        method: 'POST',
        data: submitData,
      }).then(
        () => {
          // 执行回调，刷新列表数据
          callback.current();

          onClose();
          message.success('成功新增 ACL');
        },
        () => setConfirmLoading(false)
      );
    });
  };

  // 展开抽屉
  const onOpen = (status: boolean, cbk: () => void) => {
    setVisible(status);
    callback.current = cbk;
  };

  // 关闭抽屉
  const onClose = () => {
    setVisible(false);
    setConfirmLoading(false);
    form.resetFields();
  };

  useImperativeHandle(ref, () => ({
    onOpen,
  }));

  useEffect(() => {
    getKafkaUserList();
    getTopicMetaData('');
    getGroupMetaData();
  }, []);

  return (
    <Drawer
      className="acls-edit-drawer"
      title="新增ACL"
      width={480}
      visible={visible}
      maskClosable={false}
      onClose={onClose}
      extra={
        <Space>
          <Button size="small" onClick={onClose}>
            取消
          </Button>
          <Button type="primary" size="small" loading={confirmLoading} onClick={onSubmit}>
            确定
          </Button>
          <Divider type="vertical" />
        </Space>
      }
    >
      <Alert
        className="drawer-alert-full-screen"
        message="新增 ACL 必须在集群已开启 ACL 功能时才会生效"
        type="info"
        showIcon
        style={{ marginBottom: 20 }}
      />
      <Form form={form} layout="vertical">
        <Form.Item
          label="ACL用途"
          name="configType"
          rules={[{ required: true, message: 'ACL用途不能为空' }]}
          initialValue={CONFIG_TYPE[0].value}
        >
          <Select options={CONFIG_TYPE} />
        </Form.Item>
        <Form.Item label="Principle" name="principle" rules={[{ required: true, message: 'Principle 不能为空' }]} initialValue="all">
          <Radio.Group>
            <Radio value="all">ALL</Radio>
            <Radio value="special">Special</Radio>
          </Radio.Group>
        </Form.Item>
        <Form.Item dependencies={['principle']} style={{ marginBottom: 0 }}>
          {({ getFieldValue }) =>
            getFieldValue('principle') === 'special' ? (
              <Form.Item name="kafkaUser" rules={[{ required: true, message: 'Kafka User 不能为空' }]}>
                <Select placeholder="请选择 Kafka User" options={kafkaUserOptions} />
              </Form.Item>
            ) : null
          }
        </Form.Item>
        <Form.Item dependencies={['configType']} style={{ marginBottom: 0 }}>
          {({ getFieldValue }) => {
            const SelectFormItems = (props: { type: string }) => {
              const { type } = props;
              return (
                <Form.Item
                  name={`${type}Name`}
                  dependencies={[`${type}PatternType`]}
                  validateTrigger="onBlur"
                  rules={[
                    ({ getFieldValue }) => ({
                      validator: (rule: any, value: string) => {
                        if (!value) {
                          return Promise.reject(`${type}Name 不能为空`);
                        }
                        if (type === 'topic' && getFieldValue(`${type}PatternType`) === ACL_PATTERN_TYPE['Literal']) {
                          return Utils.request(api.getTopicMetadata(clusterId as any, value)).then((res: any) => {
                            return res?.exist ? Promise.resolve() : Promise.reject('该 Topic 不存在');
                          });
                        }
                        return Promise.resolve();
                      },
                    }),
                  ]}
                >
                  <AutoComplete
                    filterOption={(value, option) => {
                      if (option?.value.includes(value)) {
                        return true;
                      }
                      return false;
                    }}
                    options={type === 'topic' ? topicMetaData : groupMetaData}
                    placeholder={`请输入 ${type}Name`}
                  />
                </Form.Item>
              );
            };
            const PatternTypeFormItems = (props: { type: string }) => {
              const { type } = props;
              const UpperCaseType = type[0].toUpperCase() + type.slice(1);
              return (
                <div className="form-item-group">
                  <Form.Item
                    label={`${UpperCaseType} Pattern Type`}
                    name={`${type}PatternType`}
                    rules={[{ required: true, message: `${UpperCaseType} Pattern Type 不能为空` }]}
                    initialValue={ACL_PATTERN_TYPE['Literal']}
                  >
                    <Radio.Group>
                      <Radio value={ACL_PATTERN_TYPE['Literal']}>Literal</Radio>
                      <Radio value={ACL_PATTERN_TYPE['Prefixed']}>Prefixed</Radio>
                    </Radio.Group>
                  </Form.Item>
                  <Form.Item
                    label={UpperCaseType}
                    name={`${type}Principle`}
                    rules={[{ required: true, message: `${UpperCaseType} 不能为空` }]}
                    initialValue="all"
                  >
                    <Radio.Group>
                      <Radio value="all">ALL</Radio>
                      <Radio value="special">Special</Radio>
                    </Radio.Group>
                  </Form.Item>
                  <Form.Item dependencies={[`${type}Principle`]} style={{ marginBottom: 0 }}>
                    {({ getFieldValue }) =>
                      getFieldValue(`${type}Principle`) === 'special' ? (
                        type !== 'transactionalId' ? (
                          <Form.Item
                            name={`${type}Name`}
                            dependencies={[`${type}PatternType`]}
                            validateTrigger="onBlur"
                            rules={[
                              ({ getFieldValue }) => ({
                                validator: (rule: any, value: string) => {
                                  if (!value) {
                                    return Promise.reject(`${UpperCaseType}Name 不能为空`);
                                  }
                                  if (type === 'topic' && getFieldValue(`${type}PatternType`) === ACL_PATTERN_TYPE['Literal']) {
                                    return Utils.request(api.getTopicMetadata(clusterId as any, value)).then((res: any) => {
                                      return res?.exist ? Promise.resolve() : Promise.reject('该 Topic 不存在');
                                    });
                                  }
                                  return Promise.resolve();
                                },
                              }),
                            ]}
                          >
                            <AutoComplete
                              filterOption={(value, option) => {
                                if (option?.value.includes(value)) {
                                  return true;
                                }
                                return false;
                              }}
                              options={type === 'topic' ? topicMetaData : groupMetaData}
                              placeholder={`请输入 ${type}Name`}
                            />
                          </Form.Item>
                        ) : (
                          <Form.Item name={`transactionalId`} rules={[{ required: true, message: `TransactionalId不能为空` }]}>
                            <Input placeholder={`请输入TransactionalId`}></Input>
                          </Form.Item>
                        )
                      ) : null
                    }
                  </Form.Item>
                </div>
              );
            };

            const CustomFormItems = () => {
              return (
                <>
                  <Form.Item
                    label="Permission Type"
                    name="aclPermissionType"
                    rules={[{ required: true, message: 'Permission Type 不能为空' }]}
                    initialValue={ACL_PERMISSION_TYPE['Allow']}
                  >
                    <Radio.Group>
                      <Radio value={ACL_PERMISSION_TYPE['Allow']}>Allow</Radio>
                      <Radio value={ACL_PERMISSION_TYPE['Deny']}>Deny</Radio>
                    </Radio.Group>
                  </Form.Item>
                  {/* <Form.Item
                    label="Pattern Type"
                    name="resourcePatternType"
                    rules={[{ required: true, message: 'Pattern Type 不能为空' }]}
                    initialValue={ACL_PATTERN_TYPE['Literal']}
                  >
                    <Radio.Group>
                      <Radio value={ACL_PATTERN_TYPE['Literal']}>Literal</Radio>
                      <Radio value={ACL_PATTERN_TYPE['Prefixed']}>Prefixed</Radio>
                    </Radio.Group>
                  </Form.Item> */}
                  <Form.Item
                    label="Resource Type"
                    name="resourceType"
                    rules={[{ required: true, message: 'Resource Type 不能为空' }]}
                    initialValue={ACL_RESOURCE_TYPE['Cluster']}
                  >
                    <Select
                      placeholder="请选择 Resource Type"
                      options={Object.keys(RESOURCE_TO_OPERATIONS_MAP).map((type: RESOURCE_MAP_KEYS) => ({
                        label: type,
                        value: ACL_RESOURCE_TYPE[type],
                      }))}
                    />
                  </Form.Item>
                  <Form.Item dependencies={['resourceType']}>
                    {({ getFieldValue }) => {
                      const type = getFieldValue('resourceType');
                      if (type === ACL_RESOURCE_TYPE['Cluster']) {
                        //TODO需要和后端获取集群和事务接口联调
                        return (
                          <Form.Item
                            name={`${type === 4 ? 'cluster' : 'transactionalId'}`}
                            rules={[{ required: true, message: `${type === 4 ? 'Cluster名称' : 'TransactionalId'} 不能为空` }]}
                          >
                            <Input placeholder={`请输入${type === 4 ? 'Cluster名称' : 'TransactionalId'}`}></Input>
                          </Form.Item>
                        );
                      } else if (type === ACL_RESOURCE_TYPE['TransactionalId']) {
                        return <PatternTypeFormItems type="transactionalId" />;
                      } else if (type === ACL_RESOURCE_TYPE['Topic']) {
                        return <PatternTypeFormItems type="topic" />;
                      } else if (type === ACL_RESOURCE_TYPE['Group']) {
                        return <PatternTypeFormItems type="group" />;
                      }
                      return null;
                    }}
                  </Form.Item>
                  <Form.Item dependencies={['resourceType']} style={{ marginBottom: 0 }}>
                    {({ getFieldValue }) => {
                      form.resetFields(['aclOperation']);
                      return (
                        <Form.Item label="Operation" name="aclOperation" rules={[{ required: true, message: 'Operation 不能为空' }]}>
                          <Select
                            placeholder="请选择 Resource Type"
                            options={RESOURCE_TO_OPERATIONS_MAP[ACL_RESOURCE_TYPE[getFieldValue('resourceType')] as RESOURCE_MAP_KEYS].map(
                              (type) => ({
                                label: type,
                                value: ACL_OPERATION[type as keyof typeof ACL_OPERATION],
                              })
                            )}
                          />
                        </Form.Item>
                      );
                    }}
                  </Form.Item>
                  <Form.Item label="Host" name="aclClientHost" initialValue="*">
                    <Input />
                  </Form.Item>
                </>
              );
            };

            const type = getFieldValue('configType');
            if (type === 'produce') {
              return <PatternTypeFormItems type="topic" />;
            } else if (type === 'consume') {
              return (
                <>
                  <PatternTypeFormItems type="topic" />
                  <PatternTypeFormItems type="group" />
                </>
              );
            } else if (type === 'custom') {
              return <CustomFormItems />;
            } else {
              return null;
            }
          }}
        </Form.Item>
      </Form>
    </Drawer>
  );
});

export default AddDrawer;
