// 批量Topic复制
import React, { useState, useEffect } from 'react';
import { useParams } from 'react-router-dom';
import { Button, Drawer, Form, Select, Utils, AppContainer, Space, Divider, Transfer, Checkbox, Tooltip } from 'knowdesign';
import message from '@src/components/Message';
import { IconFont } from '@knowdesign/icons';
import { QuestionCircleOutlined } from '@ant-design/icons';
import './index.less';
import Api from '@src/api/index';

const { Option } = Select;
const CheckboxGroup = Checkbox.Group;

interface DefaultConfig {
  drawerVisible: boolean;
  onClose: () => void;
  genData?: () => any;
}

export default (props: DefaultConfig) => {
  const { drawerVisible, onClose, genData } = props;
  const routeParams = useParams<{ clusterId: string }>();
  const [visible, setVisible] = useState(drawerVisible);
  const [topicList, setTopicList] = useState([]);
  const [clusterList, setClusterList] = useState([]);
  const [selectTopicList, setSelectTopicList] = useState([]);
  const [form] = Form.useForm();
  const topicsPerCluster = React.useRef({} as any);

  const mirrorScopeOptions = [
    {
      label: '数据',
      value: 'syncData',
    },
    {
      label: 'Topic配置',
      value: 'syncConfig',
    },
    {
      label: 'kafkauser+Acls',
      value: 'kafkauserAcls',
      disabled: true,
    },
    {
      label: 'Group Offset',
      value: 'groupOffset',
      disabled: true,
    },
  ];

  const getTopicList = () => {
    Utils.request(Api.getTopicMetaData(+routeParams.clusterId)).then((res: any) => {
      const dataDe = res || [];
      const dataHandle = dataDe.map((item: any) => {
        return {
          ...item,
          key: item.topicName,
          title: item.topicName,
        };
      });
      setTopicList(dataHandle);
    });
  };

  const getTopicsPerCluster = (clusterId: number) => {
    Utils.request(Api.getTopicMetaData(clusterId)).then((res: any) => {
      const dataDe = res || [];
      const dataHandle = dataDe.map((item: any) => item.topicName);
      topicsPerCluster.current = { ...topicsPerCluster.current, [clusterId]: dataHandle };
      setTimeout(() => {
        form.validateFields(['topicNames']);
      }, 1000);
    });
  };

  const getClusterList = () => {
    Utils.request(Api.getMirrorClusterList()).then((res: any) => {
      const dataDe = res || [];
      const dataHandle = dataDe.map((item: any) => {
        return {
          label: item.name,
          value: item.id,
        };
      });
      setClusterList(dataHandle);
    });
  };

  const checkTopic = (_: any, value: any[]) => {
    const clusters = form.getFieldValue('destClusterPhyIds');
    if (!value || !value.length) {
      return Promise.reject('请选择需要复制的Topic');
    } else {
      if (clusters && clusters.length) {
        // 验证Topic是否存在
        const existTopics = {} as any;
        clusters.forEach((cluster: number) => {
          if (cluster && topicsPerCluster.current[cluster]) {
            existTopics[cluster] = [];
            value.forEach((topic) => {
              if (topicsPerCluster.current[cluster].indexOf(topic) > -1) {
                existTopics[cluster].push(topic);
              }
            });
            if (!existTopics[cluster].length) delete existTopics[cluster];
          } else {
            getTopicsPerCluster(cluster);
          }
        });
        if (Object.keys(existTopics).length) {
          let errorInfo = '';
          Object.keys(existTopics).forEach((key) => {
            const clusterName = clusterList.find((item) => item.value == key)?.label;
            errorInfo = errorInfo.concat(`${existTopics[key].join('、')}在集群【${clusterName}】中已存在，`);
          });
          errorInfo = errorInfo.concat('请重新选择');
          return Promise.reject(errorInfo);
        } else {
          return Promise.resolve();
        }
      } else {
        return Promise.resolve();
      }
    }
  };

  const topicChange = (val: string[]) => {
    setSelectTopicList(val);
  };

  const clusterChange = (val: number[]) => {
    if (val && val.length) {
      val.forEach((item) => {
        if (item && !topicsPerCluster.current[item]) {
          getTopicsPerCluster(item);
        } else {
          form.validateFields(['topicNames']);
        }
      });
    } else {
      form.validateFields(['topicNames']);
    }
  };

  const onDrawerClose = () => {
    form.resetFields();
    setSelectTopicList([]);
    topicsPerCluster.current = {};
    setVisible(false);
    onClose();
  };

  useEffect(() => {
    if (!drawerVisible) return;
    setVisible(true);
    getTopicList();
    getClusterList();
  }, [drawerVisible]);

  const addTopicMirror = () => {
    form.validateFields().then((e) => {
      const formData = form.getFieldsValue();
      const handledData = [] as any;
      formData.destClusterPhyIds.forEach((cluster: number) => {
        formData.topicNames.forEach((topic: string) => {
          handledData.push({
            destClusterPhyId: cluster,
            sourceClusterPhyId: +routeParams.clusterId,
            syncData: formData.mirrorScope.indexOf('syncData') > -1,
            syncConfig: formData.mirrorScope.indexOf('syncConfig') > -1,
            topicName: topic,
          });
        });
      });
      Utils.post(Api.handleTopicMirror(), handledData).then(() => {
        message.success('成功复制Topic');
        onDrawerClose();
        genData();
      });
    });
  };

  return (
    <Drawer
      push={false}
      title="Topic复制"
      width={600}
      placement="right"
      onClose={onDrawerClose}
      visible={visible}
      className="topic-job-drawer"
      maskClosable={false}
      destroyOnClose
      extra={
        <Space>
          <Button
            size="small"
            style={{ marginRight: 8 }}
            onClick={(_) => {
              onDrawerClose();
            }}
          >
            取消
          </Button>
          <Button size="small" type="primary" onClick={addTopicMirror}>
            确定
          </Button>
          <Divider type="vertical" />
        </Space>
      }
    >
      <div className="wrap">
        <Form form={form} layout="vertical" className="task-form">
          <Form.Item name="topicNames" label="选择Topic" rules={[{ required: true, validator: checkTopic }]}>
            <Transfer
              dataSource={topicList}
              showSearch
              filterOption={(inputValue, option) => option.topicName.indexOf(inputValue) > -1}
              targetKeys={selectTopicList}
              onChange={topicChange}
              render={(item) => item.title}
              titles={['待选Topic', '已选Topic']}
              customHeader
              showSelectedCount
              locale={{ itemUnit: '', itemsUnit: '' }}
              suffix={<IconFont type="icon-fangdajing" />}
            />
          </Form.Item>
          <Form.Item
            name="destClusterPhyIds"
            label={
              <>
                <span style={{ marginRight: '8px' }}>选择目标集群</span>
                <Tooltip title="目标集群需要为使用滴滴kafka的350+版本的kafka集群">
                  <QuestionCircleOutlined />
                </Tooltip>
              </>
            }
            rules={[{ required: true, message: '请选择目标集群' }]}
          >
            <Select
              placeholder="请选择目标集群，可多选"
              mode="multiple"
              maxTagCount={'responsive'}
              allowClear
              onChange={clusterChange}
              options={clusterList.filter((item) => item.value !== +routeParams.clusterId)}
            ></Select>
          </Form.Item>
          <Form.Item
            name="mirrorScope"
            label="Topic复制范围"
            rules={[
              {
                required: true,
                validator: (_: any, value: any[]) => {
                  if (!value || !value.length) {
                    return Promise.reject('请选择Topic复制范围');
                  } else if (value.indexOf('syncData') === -1) {
                    return Promise.reject('Topic复制范围必须选择[数据]');
                  } else {
                    return Promise.resolve();
                  }
                },
              },
            ]}
            initialValue={['syncData']}
          >
            <CheckboxGroup className="checkbox-content-margin">
              {mirrorScopeOptions.map((option) => {
                return option.disabled ? (
                  <Tooltip title="当前版本暂不支持">
                    <Checkbox key={option.value} value={option.value} disabled>
                      {option.label}
                    </Checkbox>
                  </Tooltip>
                ) : (
                  <Checkbox key={option.value} value={option.value}>
                    {option.label}
                  </Checkbox>
                );
              })}
            </CheckboxGroup>
          </Form.Item>
        </Form>
      </div>
    </Drawer>
  );
};
