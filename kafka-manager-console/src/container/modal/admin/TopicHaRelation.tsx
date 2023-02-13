import * as React from 'react';
import { admin } from 'store/admin';
import { Modal, Form, Radio } from 'antd';
import { IBrokersMetadata, IBrokersRegions, IMetaData } from 'types/base-type';
import { Alert, message, notification, Table, Tooltip, Spin } from 'component/antd';
import { getClusterHaTopicsStatus, getAppRelatedTopics, setHaTopics, unbindHaTopics } from 'lib/api';
import { cellStyle } from 'constants/table';
import { renderAttributes, TransferTable, IKafkaUser } from './TopicHaSwitch'
import './index.less'

const layout = {
  labelCol: { span: 3 },
  wrapperCol: { span: 21 },
};

interface IXFormProps {
  form: any;
  reload: any;
  formData?: any;
  visible?: boolean;
  handleVisible?: any;
  currentCluster?: IMetaData;
}

interface IHaTopic {
  clusterId: number;
  clusterName: string;
  haRelation: number;
  isHaRelation: boolean;
  topicName: string;
  key: string;
  disabled?: boolean;
}

const resColumns = [
  {
    title: 'TopicName',
    dataIndex: 'topicName',
    key: 'topicName',
    width: 120,
  },
  {
    title: '状态',
    dataIndex: 'code',
    key: 'code',
    width: 60,
    render: (t: number) => {
      return (
        <span className={t === 0 ? 'success' : 'fail'}>
          {t === 0 ? '成功' : '失败'}
        </span>
      );
    },
  },
  {
    title: '原因',
    dataIndex: 'message',
    key: 'message',
    width: 125,
    onCell: () => ({
      style: {
        maxWidth: 120,
        ...cellStyle,
      },
    }),
    render: (text: string) => {
      return (
        <Tooltip placement="bottomLeft" title={text} >
          {text}
        </Tooltip>);
    },
  },
];

const columns = [
  {
    dataIndex: 'topicName',
    title: '名称',
    width: 260,
    ellipsis: true,
  },
];

const kafkaUserColumn = [
  {
    dataIndex: 'kafkaUser',
    title: 'kafkaUser',
    width: 100,
    ellipsis: true,
  },
  {
    dataIndex: 'manualSelectedTopics',
    title: '已选中Topic',
    width: 120,
    render: (text: string[]) => {
      return text?.length ? renderAttributes({ data: text, limit: 3 }) : '-';
    },
  },
  {
    dataIndex: 'autoSelectedTopics',
    title: '选中关联Topic',
    width: 120,
    render: (text: string[]) => {
      return text?.length ? renderAttributes({ data: text, limit: 3 }) : '-';
    },
  },
];

class TopicHaRelation extends React.Component<IXFormProps> {
  public state = {
    radioCheck: 'spec',
    topics: [] as IHaTopic[],
    kafkaUsers: [] as IKafkaUser[],
    targetKeys: [] as string[],
    selectedKeys: [] as string[],
    confirmLoading: false,
    firstMove: true,
    primaryActiveKeys: [] as string[],
    primaryStandbyKeys: [] as string[],
    manualSelectedKeys: [] as string[],
    spinLoading: false,
  };

  public selectSingle = null as boolean;
  public manualSelectedNames = [] as string[];

  public setSelectSingle = (val: boolean) => {
    this.selectSingle = val;
  }
  public setManualSelectedNames = (keys: string[]) => {
    // this.manualSelectedNames = this.getTopicsByKeys(keys);
    this.manualSelectedNames = keys;
  }

  public filterManualSelectedKeys = (key: string, selected: boolean) => {
    const newManualSelectedKeys = [...this.state.manualSelectedKeys];
    const index = newManualSelectedKeys.findIndex(item => item === key);
    if (selected) {
      if (index === -1) newManualSelectedKeys.push(key);
    } else {
      if (index !== -1) newManualSelectedKeys.splice(index, 1);
    }
    this.setManualSelectedNames(newManualSelectedKeys);
    this.setState({
      manualSelectedKeys: newManualSelectedKeys,
    });
  }

  public getManualSelected = (single: boolean, key?: any, selected?: boolean) => {
    this.setSelectSingle(single);
    if (single) {
      this.filterManualSelectedKeys(key, selected);
    } else {
      this.setManualSelectedNames(key);
      this.setState({
        manualSelectedKeys: key,
      });
    }
  }

  public handleOk = () => {
    this.props.form.validateFields((err: any, values: any) => {
      const { primaryStandbyKeys, targetKeys} = this.state;
      const unbindKeys = [];
      const bindKeys = [];

      if (values.rule === 'all') {
        setHaTopics({
          all: true,
          activeClusterId: this.props.currentCluster.clusterId,
          standbyClusterId: this.props.currentCluster.haClusterVO?.clusterId,
          topicNames: [],
        }).then(res => {
          handleMsg(res, '关联成功');
          this.setState({
            confirmLoading: false,
          });
          this.handleCancel();
        });
        return;
      }

      for (const item of primaryStandbyKeys) {
        if (!targetKeys.includes(item)) {
          unbindKeys.push(item);
        }
      }
      for (const item of targetKeys) {
        if (!primaryStandbyKeys.includes(item)) {
          bindKeys.push(item);
        }
      }

      if (!unbindKeys.length && !bindKeys.length) {
        return message.info('请选择您要操作的Topic');
      }

      const handleMsg = (res: any[], successTip: string) => {
        const errorRes = res.filter(item => item.code !== 0);

        if (errorRes.length) {
          Modal.confirm({
            title: '执行结果',
            width: 520,
            icon: null,
            content: (
              <Table
                columns={resColumns}
                rowKey="id"
                dataSource={res}
                scroll={{ y: 260 }}
                pagination={false}
              />
            ),
          });
        } else {
          notification.success({ message: successTip });
        }

        this.props.reload();
      };

      if (bindKeys.length) {
        this.setState({
          confirmLoading: true,
        });
        setHaTopics({
          all: false,
          activeClusterId: this.props.currentCluster.clusterId,
          standbyClusterId: this.props.currentCluster.haClusterVO?.clusterId,
          // topicNames: this.getTopicsByKeys(bindKeys),
          topicNames: bindKeys,
        }).then(res => {
          this.setState({
            confirmLoading: false,
          });
          this.handleCancel();
          handleMsg(res, '关联成功');
        });
      }

      if (unbindKeys.length) {
        this.setState({
          confirmLoading: true,
        });
        unbindHaTopics({
          all: false,
          activeClusterId: this.props.currentCluster.clusterId,
          standbyClusterId: this.props.currentCluster.haClusterVO?.clusterId,
          // topicNames: this.getTopicsByKeys(unbindKeys),
          topicNames: unbindKeys,
          retainStandbyResource: values.retainStandbyResource,
        }).then(res => {
          this.setState({
            confirmLoading: false,
          });
          this.handleCancel();
          handleMsg(res, '解绑成功');
        });
      }
    });
  }

  public handleCancel = () => {
    this.props.handleVisible(false);
    this.props.form.resetFields();
  }

  public handleRadioChange = (e: any) => {
    this.setState({
      radioCheck: e.target.value,
    });
  }

  public isPrimaryStatus = (targetKeys: string[]) => {
    const { primaryStandbyKeys } = this.state;
    let isReset = false;
    // 判断当前移动是否还原为最初的状态
    if (primaryStandbyKeys.length === targetKeys.length) {
      const diff = targetKeys.find(item => primaryStandbyKeys.indexOf(item) < 0);
      isReset = diff ? false : true;
    }
    return isReset;
  }

  public getNewSelectKeys = (removeKeys: string[], selectedKeys: string[]) => {
    const { topics, kafkaUsers } = this.state;
    // 根据移除的key找与该key关联的其他key，一起移除
    let relatedTopics: string[] = [];
    const relatedKeys: string[] = [];
    const newSelectKeys = [];
    for (const key of removeKeys) {
      const topicName = topics.find(row => row.key === key)?.topicName;
      for (const item of kafkaUsers) {
        if (item.selectedTopicNameList.includes(topicName)) {
          relatedTopics = relatedTopics.concat(item.selectedTopicNameList, item.notSelectTopicNameList);
        }
      }
      for (const item of relatedTopics) {
        const key = topics.find(row => row.topicName === item)?.key;
        if (key) {
          relatedKeys.push(key);
        }
      }
      for (const key of selectedKeys) {
        if (!relatedKeys.includes(key)) {
          newSelectKeys.push(key);
        }
      }
    }
    return newSelectKeys;
  }

  public setTopicsStatus = (targetKeys: string[], disabled: boolean, isAll = false) => {
    const { topics } = this.state;
    const newTopics = Array.from(topics);
    if (isAll) {
      for (let i = 0; i < topics.length; i++) {
        newTopics[i].disabled = disabled;
      }
    } else {
      for (const key of targetKeys) {
        const index = topics.findIndex(item => item.key === key);
        if (index > -1) {
          newTopics[index].disabled = disabled;
        }
      }
    }
    this.setState(({
      topics: newTopics,
    }));
  }

  public getTopicsByKeys = (keys: string[]) => {
    // 依据key值找topicName
    const topicNames: string[] = [];
    for (const key of keys) {
      const topicName = this.state.topics.find(item => item.key === key)?.topicName;
      if (topicName) {
        topicNames.push(topicName);
      }
    }
    return topicNames;
  }

  public getNewKafkaUser = (targetKeys: string[]) => {
    const { primaryStandbyKeys, kafkaUsers, topics } = this.state;
    const removeKeys = [];
    const addKeys = [];
    for (const key of primaryStandbyKeys) {
      if (targetKeys.indexOf(key) < 0) {
        // 移除的
        removeKeys.push(key);
      }
    }
    for (const key of targetKeys) {
      if (primaryStandbyKeys.indexOf(key) < 0) {
        // 新增的
        addKeys.push(key);
      }
    }
    const keepKeys = [...removeKeys, ...addKeys];
    const newKafkaUsers = kafkaUsers;

    const moveTopics = this.getTopicsByKeys(keepKeys);

    for (const topic of moveTopics) {
      for (const item of newKafkaUsers) {
        if (item.selectedTopicNameList.includes(topic)) {
          item.show = true;
        }
      }
    }

    const showKafaUsers = newKafkaUsers.filter(item => item.show === true);

    for (const item of showKafaUsers) {
      let i = 0;
      while (i < moveTopics.length) {
        if (!item.selectedTopicNameList.includes(moveTopics[i])) {
          i++;
        } else {
          break;
        }
      }

      // 表示该kafkaUser不该展示
      if (i === moveTopics.length) {
        item.show = false;
      }
    }

    return showKafaUsers;
  }

  public getAppRelatedTopicList = (selectedKeys: string[]) => {
    const { topics, targetKeys, primaryStandbyKeys, kafkaUsers } = this.state;
    const filterTopicNameList = this.getTopicsByKeys(selectedKeys);
    const isReset = this.isPrimaryStatus(targetKeys);

    if (!filterTopicNameList.length && isReset) {
      // targetKeys
      this.setState({
        kafkaUsers: kafkaUsers.map(item => ({
          ...item,
          show: false,
        })),
      });
      return;
    } else {
      // 保留选中项与移动的的项
      this.setState({
        kafkaUsers: this.getNewKafkaUser(targetKeys),
      });
    }

    // 单向选择，所以取当前值的clusterId
    const clusterInfo = topics.find(item => item.topicName === filterTopicNameList[0]);
    const clusterPhyId = clusterInfo?.clusterId;
    if (!clusterPhyId) return;
    this.setState({spinLoading: true});
    getAppRelatedTopics({
      clusterPhyId,
      filterTopicNameList,
      ha: clusterInfo.isHaRelation,
      useKafkaUserAndClientId: false,
    }).then((res: IKafkaUser[]) => {
      let notSelectTopicNames: string[] = [];
      const notSelectTopicKeys: string[] = [];
      for (const item of (res || [])) {
        notSelectTopicNames = notSelectTopicNames.concat(item.notSelectTopicNameList || []);
      }

      for (const item of notSelectTopicNames) {
        const key = topics.find(row => row.topicName === item)?.key;

        if (key && notSelectTopicKeys.indexOf(key) < 0) {
          notSelectTopicKeys.push(key);
        }
      }

      const newSelectedKeys = selectedKeys.concat(notSelectTopicKeys);
      const newKafkaUsers = (res || []).map(item => ({
        ...item,
        show: true,
        manualSelectedTopics: item.selectedTopicNameList.filter(topic => this.manualSelectedNames.indexOf(topic) > -1),
        autoSelectedTopics: [...item.selectedTopicNameList, ...item.notSelectTopicNameList].filter(topic => this.manualSelectedNames.indexOf(topic) === -1),
      }));
      const { kafkaUsers } = this.state;

      for (const item of kafkaUsers) {
        const resItem = res.find(row => row.kafkaUser === item.kafkaUser);
        if (!resItem) {
          newKafkaUsers.push(item);
        }
      }
      this.setState({
        kafkaUsers: newKafkaUsers,
        selectedKeys: newSelectedKeys,
      });

      if (notSelectTopicKeys.length) {
        this.getAppRelatedTopicList(newSelectedKeys);
      }
    }).finally(() => {
      this.setState({spinLoading: false});
    });
  }

  public getRelatedKeys = (currentKeys: string[]) => {
    // 未被选中的项
    const removeKeys = [];
    // 对比上一次记录的选中的值找出本次取消的项
    const { selectedKeys } = this.state;
    for (const preKey of selectedKeys) {
      if (!currentKeys.includes(preKey)) {
        removeKeys.push(preKey);
      }
    }

    return removeKeys?.length ? this.getNewSelectKeys(removeKeys, currentKeys) : currentKeys;
  }

  public handleTopicChange = (sourceSelectedKeys: string[], targetSelectedKeys: string[]) => {
    if (this.selectSingle) {
      this.setSelectSingle(false);
    } else {
      this.getManualSelected(false, [...sourceSelectedKeys, ...targetSelectedKeys])
    }
    const { topics, targetKeys } = this.state;
    // 条件限制只允许选中一边，单向操作
    const keys = [...sourceSelectedKeys, ...targetSelectedKeys];

    // 判断当前选中项属于哪一类
    if (keys.length) {
      const isHaRelation = topics.find(item => item.key === keys[0])?.isHaRelation;
      const needDisabledKeys = topics.filter(item => item.isHaRelation !== isHaRelation).map(row => row.key);
      this.setTopicsStatus(needDisabledKeys, true);
    }
    const selectedKeys = this.state.selectedKeys.length ? this.getRelatedKeys(keys) : keys;

    const isReset = this.isPrimaryStatus(targetKeys);
    if (!selectedKeys.length && isReset) {
      this.setTopicsStatus([], false, true);
    }
    this.setState({
      selectedKeys,
    });
    this.getAppRelatedTopicList(selectedKeys);
  }

  public onDirectChange = (targetKeys: string[], direction: string, moveKeys: string[]) => {
    const { primaryStandbyKeys, firstMove, primaryActiveKeys, kafkaUsers } = this.state;

    const getKafkaUser = () => {
      const newKafkaUsers = kafkaUsers;
      const moveTopics = this.getTopicsByKeys(moveKeys);
      for (const topic of moveTopics) {
        for (const item of newKafkaUsers) {
          if (item.selectedTopicNameList.includes(topic)) {
            item.show = true;
          }
        }
      }
      return newKafkaUsers;
    };
    // 判断当前移动是否还原为最初的状态
    const isReset = this.isPrimaryStatus(targetKeys);
    if (firstMove) {
      const primaryKeys = direction === 'right' ? primaryStandbyKeys : primaryActiveKeys;
      this.setTopicsStatus(primaryKeys, true, false);
      this.setState(({
        firstMove: false,
        kafkaUsers: getKafkaUser(),
        targetKeys,
      }));
      return;
    }
    // 如果是还原为初始状态则还原禁用状态
    if (isReset) {
      this.setTopicsStatus([], false, true);
      this.setState(({
        firstMove: true,
        targetKeys,
        kafkaUsers: [],
      }));
      return;
    }

    this.setState(({
      targetKeys,
      kafkaUsers: this.getNewKafkaUser(targetKeys),
    }));
  }

  public componentDidMount() {
    Promise.all([
      getClusterHaTopicsStatus(this.props.currentCluster.clusterId, true),
      getClusterHaTopicsStatus(this.props.currentCluster.clusterId, false),
    ]).then(([activeRes, standbyRes]: IHaTopic[][]) => {
      activeRes = (activeRes || []).map(row => ({
        ...row,
        isHaRelation: row.haRelation === 1 || row.haRelation === 0,
        key: row.topicName,
      })).filter(item => !item.isHaRelation);
      standbyRes = (standbyRes || []).map(row => ({
        ...row,
        isHaRelation: row.haRelation === 1 || row.haRelation === 0,
        key: row.topicName,
      })).filter(item => item.isHaRelation);
      this.setState({
        topics: [].concat([...activeRes, ...standbyRes]).sort((a, b) => a.topicName.localeCompare(b.topicName)),
        primaryActiveKeys: activeRes.map(row => row.key),
        primaryStandbyKeys: standbyRes.map(row => row.key),
        targetKeys: standbyRes.map(row => row.key),
      });
    });
  }

  public render() {
    const { formData = {} as any, visible, currentCluster } = this.props;
    const { getFieldDecorator } = this.props.form;
    let metadata = [] as IBrokersMetadata[];
    metadata = admin.brokersMetadata ? admin.brokersMetadata : metadata;
    let regions = [] as IBrokersRegions[];
    regions = admin.brokersRegions ? admin.brokersRegions : regions;
    const { kafkaUsers, confirmLoading, radioCheck, targetKeys, selectedKeys, topics, primaryStandbyKeys, spinLoading} = this.state;
    const tableData = kafkaUsers.filter(row => row.show);

    return (
      <>
        <Modal
          title="Topic高可用关联"
          wrapClassName="no-padding"
          visible={visible}
          onOk={this.handleOk}
          onCancel={this.handleCancel}
          maskClosable={false}
          confirmLoading={confirmLoading}
          width={800}
          okText="确认"
          cancelText="取消"
        >
          <Alert
            message={`将【集群${currentCluster.clusterName}】和【集群${currentCluster.haClusterVO?.clusterName}】的Topic关联高可用关系，此操作会把同一个kafkaUser下的所有Topic都关联高可用关系`}
            type="info"
            showIcon={true}
          />
          <Spin spinning={spinLoading}>
            <Form {...layout} name="basic" className="x-form">
              {/* <Form.Item label="规则">
                {getFieldDecorator('rule', {
                  initialValue: 'spec',
                  rules: [{
                    required: true,
                    message: '请选择规则',
                  }],
                })(<Radio.Group onChange={this.handleRadioChange} >
                  <Radio value="all">应用于所有Topic</Radio>
                  <Radio value="spec">应用于特定Topic</Radio>
                </Radio.Group>)}
              </Form.Item> */}
              {radioCheck === 'spec' ? <Form.Item className="no-label" label=""  >
                {getFieldDecorator('topicNames', {
                  initialValue: targetKeys,
                  rules: [{
                    required: false,
                    message: '请选择Topic',
                  }],
                })(
                  <TransferTable
                    selectedKeys={selectedKeys}
                    topicChange={this.handleTopicChange}
                    onDirectChange={this.onDirectChange}
                    columns={columns}
                    dataSource={topics}
                    currentCluster={currentCluster}
                    getManualSelected={this.getManualSelected}
                    transferAttrs={{
                      titles: ['未关联', '已关联'],
                    }}
                    tableAttrs={{
                      className: 'no-table-header',
                    }}
                  />,
                )}
              </Form.Item> : null}
              {radioCheck === 'spec' ? <Table
                className="modal-table-content no-lr-padding"
                columns={kafkaUserColumn}
                dataSource={tableData}
                size="small"
                rowKey="kafkaUser"
                pagination={false}
                scroll={{ y: 300 }}
              /> : null}
              {targetKeys.length < primaryStandbyKeys.length ? <Form.Item label="数据清理策略" labelCol={{span: 4}} wrapperCol={{span: 20}}>
                {getFieldDecorator('retainStandbyResource', {
                  initialValue: false,
                  rules: [{
                    required: true,
                    message: '请选择数据清理策略',
                  }],
                })(<Radio.Group>
                  <Radio value={false}>删除备集群所有数据</Radio>
                  <Radio value={true}>保留备集群所有数据</Radio>
                </Radio.Group>)}
              </Form.Item> : null}
            </Form>
          </Spin>
        </Modal>
      </>
    );
  }
}
export const TopicHaRelationWrapper = Form.create<IXFormProps>()(TopicHaRelation);
