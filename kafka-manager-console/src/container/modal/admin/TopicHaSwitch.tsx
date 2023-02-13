import * as React from 'react';
import { admin } from 'store/admin';
import { Modal, Form, Radio, Tag, Popover, Button, Tooltip, Spin } from 'antd';
import { IBrokersMetadata, IBrokersRegions, IMetaData } from 'types/base-type';
import { Alert, Icon, message, Table, Transfer } from 'component/antd';
import { getClusterHaTopics, getAppRelatedTopics, createSwitchTask } from 'lib/api';
import { TooltipPlacement } from 'antd/es/tooltip';
import * as XLSX from 'xlsx';
import moment from 'moment';
import { cloneDeep } from "lodash";
import { timeMinute } from 'constants/strategy';

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
  topicName: string;
  key: string;
  activeClusterId: number;
  consumeAclNum: number;
  produceAclNum: number;
  standbyClusterId: number;
  status: number;
  disabled?: boolean;
}

export interface IKafkaUser {
  clusterPhyId: number;
  kafkaUser: string;
  notHaTopicNameList: string[];
  notSelectTopicNameList: string[];
  selectedTopicNameList: string[];
  show: boolean;
  manualSelectedTopics: string[];
  autoSelectedTopics: string[];
  clientId?: string;
  haClientIdList?: string[]
}

const columns = [
  {
    dataIndex: 'topicName',
    title: '名称',
    width: 100,
    ellipsis: true,
  },
  {
    dataIndex: 'produceAclNum',
    title: '生产者数量',
    width: 80,
  },
  {
    dataIndex: 'consumeAclNum',
    title: '消费者数量',
    width: 80,
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
    dataIndex: 'clientId',
    title: 'clientID',
    width: 100,
    ellipsis: true,
  },
  {
    dataIndex: 'manualSelectedTopics',
    title: '已选中Topic',
    // width: 120,
    render: (text: string[]) => {
      return text?.length ? renderAttributes({ data: text, limit: 3 }) : '-';
    },
  },
  {
    dataIndex: 'autoSelectedTopics',
    title: '选中关联Topic',
    // width: 120,
    render: (text: string[]) => {
      return text?.length ? renderAttributes({ data: text, limit: 3 }) : '-';
    },
  },
  {
    dataIndex: 'notHaTopicNameList',
    title: '未建立HA Topic',
    // width: 120,
    render: (text: string[]) => {
      return text?.length ? renderAttributes({ data: text, limit: 3 }) : '-';
    },
  },
];

export const renderAttributes = (params: {
  data: any;
  type?: string;
  limit?: number;
  splitType?: string;
  placement?: TooltipPlacement;
}) => {
  const { data, type = ',', limit = 2, splitType = '；', placement } = params;
  let attrArray = data;
  if (!Array.isArray(data) && data) {
    attrArray = data.split(type);
  }
  const showItems = attrArray.slice(0, limit) || [];
  const hideItems = attrArray.slice(limit, attrArray.length) || [];
  const content = hideItems.map((item: string, index: number) => (
    <Tag key={index} className="tag-gray">
      {item}
    </Tag>
  ));
  const showItemsContent = showItems.map((item: string, index: number) => (
    <Tag key={index} className="tag-gray">
      {item}
    </Tag>
  ));

  return (
    <div className="attribute-content">
      {showItems.length > 0 ? showItemsContent : '-'}
      {hideItems.length > 0 && (
        <Popover placement={placement || 'bottomRight'} content={content} overlayClassName="attribute-tag">
          共{attrArray.length}个<Icon className="icon" type="down" />
        </Popover>
      )}
    </div>
  );
};
class TopicHaSwitch extends React.Component<IXFormProps> {
  public state = {
    radioCheck: 'spec',
    switchMode: 'kafkaUser',
    targetKeys: [] as string[],
    selectedKeys: [] as string[],
    topics: [] as IHaTopic[],
    kafkaUsers: [] as IKafkaUser[],
    primaryTopics: [] as string[],
    primaryActiveKeys: [] as string[],
    primaryStandbyKeys: [] as string[],
    firstMove: true,
    manualSelectedKeys: [] as string[],
    selectTableColumn: kafkaUserColumn.filter(item => item.title !== 'clientID') as [],
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

  public getTargetTopics = (currentKeys: string[], primaryKeys: string[]) => {
    const targetTopics = [];
    for (const key of currentKeys) {
      if (!primaryKeys.includes(key)) {
        // const topic = this.state.topics.find(item => item.key === key)?.topicName;
        // targetTopics.push(topic);
        targetTopics.push(key);
      }
    }
    return targetTopics;
  }

  public handleOk = () => {
    const { primaryStandbyKeys, primaryActiveKeys, topics, kafkaUsers, switchMode } = this.state;
    const standbyClusterId = this.props.currentCluster.haClusterVO?.clusterId;
    const activeClusterId = this.props.currentCluster.clusterId;

    this.props.form.validateFields((err: any, values: any) => {

      if (values.rule === 'all') {
        createSwitchTask({
          activeClusterPhyId: activeClusterId,
          all: true,
          mustContainAllKafkaUserTopics: true,
          standbyClusterPhyId: standbyClusterId,
          kafkaUserAndClientIdList: [],
          topicNameList: [],
        }).then(res => {
          message.success('任务创建成功');
          this.handleCancel();
          this.props.reload(res);
        });
        return;
      }
      // 判断当前移动是否还原为最初的状态
      const isPrimary = this.isPrimaryStatus(values.targetKeys || []);
      if (isPrimary) {
        return message.info('请选择您要切换的Topic');
      }

      // 右侧框值
      const currentStandbyKeys = values.targetKeys || [];
      // 左侧框值
      const currentActiveKeys = [];
      for (const item of topics) {
        if (!currentStandbyKeys.includes(item.key)) {
          currentActiveKeys.push(item.key);
        }
      }

      const currentKeys = currentStandbyKeys.length > primaryStandbyKeys.length ? currentStandbyKeys : currentActiveKeys;
      const primaryKeys = currentStandbyKeys.length > primaryStandbyKeys.length ? primaryStandbyKeys : primaryActiveKeys;
      const activeClusterPhyId = currentStandbyKeys.length > primaryStandbyKeys.length ? standbyClusterId : activeClusterId;
      const standbyClusterPhyId = currentStandbyKeys.length > primaryStandbyKeys.length ? activeClusterId : standbyClusterId;
      const targetTopics = this.getTargetTopics(currentKeys, primaryKeys);
      const clientIdParams = kafkaUsers.map(item => ({ clientId: item.clientId, kafkaUser: item.kafkaUser }));
      const kafkaUserParams = [] as any;
      kafkaUsers.forEach(item => {
        kafkaUserParams.push({
          clientId: null,
          kafkaUser: item.kafkaUser,
        });
        if (item.haClientIdList?.length) {
          item.haClientIdList.forEach(clientId => {
            kafkaUserParams.push({
              clientId,
              kafkaUser: item.kafkaUser,
            });
          });
        }
      });
      createSwitchTask({
        activeClusterPhyId,
        all: false,
        mustContainAllKafkaUserTopics: switchMode === 'kafkaUser',
        standbyClusterPhyId,
        kafkaUserAndClientIdList: switchMode === 'clientID' ? clientIdParams : kafkaUserParams,
        topicNameList: targetTopics,
      }).then(res => {
        message.success('任务创建成功');
        this.handleCancel();
        this.props.reload(res);
      });
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
    const { topics, targetKeys, primaryStandbyKeys, kafkaUsers, switchMode } = this.state;
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

    // 单向选择，所以取当前值的aactiveClusterId
    const clusterPhyId = topics.find(item => item.topicName === filterTopicNameList[0])?.activeClusterId;
    if (!clusterPhyId) return;
    this.setState({spinLoading: true});
    getAppRelatedTopics({
      clusterPhyId,
      filterTopicNameList,
      ha: true,
      useKafkaUserAndClientId: switchMode === 'clientID',
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
        const resItem = res.find(row => switchMode === 'clientID' ? row.kafkaUser === item.kafkaUser && row.clientId === item.clientId : row.kafkaUser === item.kafkaUser);
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
    const { topics, targetKeys } = this.state;
    // 条件限制只允许选中一边，单向操作
    const keys = [...sourceSelectedKeys, ...targetSelectedKeys];

    // 判断当前选中项属于哪一类
    if (keys.length) {
      const activeClusterId = topics.find(item => item.key === keys[0])?.activeClusterId;
      const needDisabledKeys = topics.filter(item => item.activeClusterId !== activeClusterId).map(row => row.key);
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
    const { primaryStandbyKeys, firstMove, primaryActiveKeys, kafkaUsers, topics } = this.state;

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

    // 切换后重新判定展示项
    this.setState(({
      targetKeys,
      kafkaUsers: this.getNewKafkaUser(targetKeys),
    }));

  }

  public downloadData = () => {
    const { kafkaUsers, switchMode } = this.state;
    const tableData = kafkaUsers.map(item => {
      const column = {
        // tslint:disable
        'kafkaUser': item.kafkaUser,
        'clientID': item.clientId,
        '已选中Topic': item.manualSelectedTopics?.join('、'),
        '选中关联Topic': item.autoSelectedTopics?.join('、'),
        '未建立HA Topic': item.notHaTopicNameList?.join(`、`),
      };
      if (switchMode === 'kafkaUser') {
        delete column.clientID
      }
      return column;
    });
    const data = [].concat(tableData);
    const wb = XLSX.utils.book_new();
    // json转sheet
    const header = ['kafkaUser', 'clientID', '已选中Topic', '选中关联Topic', '未建立HA Topic'];
    const ws = XLSX.utils.json_to_sheet(data, {
      header: switchMode === 'kafkaUser' ? header.filter(item => item !== 'clientID') : header,
    });
    // XLSX.utils.
    XLSX.utils.book_append_sheet(wb, ws, 'kafkaUser');
    // 输出
    XLSX.writeFile(wb, 'kafkaUser-' + moment((new Date()).getTime()).format(timeMinute) + '.xlsx');
  }

  public judgeSubmitStatus = () => {
    const { kafkaUsers } = this.state;

    const newKafkaUsers = kafkaUsers.filter(item => item.show)
    for (const item of newKafkaUsers) {
      if (item.notHaTopicNameList.length) {
        return true;
      }
    }
    return false;
  }

  public onModeChange = (e: any) => {
    const mode = e.target.value;
    // 切换方式变更时，初始化数据
    const { primaryTopics, primaryStandbyKeys } = this.state;
    this.setState({
      switchMode: mode,
      topics: cloneDeep(primaryTopics),
      targetKeys: primaryStandbyKeys,
      selectedKeys: [],
      kafkaUsers: [],
      firstMove: true,
      manualSelectedKeys: [],
      selectTableColumn: mode === 'kafkaUser' ? kafkaUserColumn.filter(item => item.title !== 'clientID') : kafkaUserColumn,
    });
    this.props.form.setFieldsValue({targetKeys: primaryStandbyKeys});
    this.setSelectSingle(null);
    this.setManualSelectedNames([]);
  }

  public componentDidMount() {
    const standbyClusterId = this.props.currentCluster.haClusterVO?.clusterId;
    const activeClusterId = this.props.currentCluster.clusterId;
    getClusterHaTopics(activeClusterId, standbyClusterId).then((res: IHaTopic[]) => {
      res = res.map((item) => ({
        key: item.topicName,
        ...item,
      }));
      const targetKeys = (res || []).filter((item) => item.activeClusterId === standbyClusterId).map(row => row.key);
      const primaryActiveKeys = (res || []).filter((item) => item.activeClusterId === activeClusterId).map(row => row.key);
      this.setState({
        topics: res || [],
        primaryTopics: cloneDeep(res) || [],
        primaryStandbyKeys: targetKeys,
        primaryActiveKeys,
        targetKeys,
      });
    });
  }

  public render() {
    const { visible, currentCluster } = this.props;
    const { getFieldDecorator } = this.props.form;
    let metadata = [] as IBrokersMetadata[];
    metadata = admin.brokersMetadata ? admin.brokersMetadata : metadata;
    let regions = [] as IBrokersRegions[];
    regions = admin.brokersRegions ? admin.brokersRegions : regions;
    const { switchMode, kafkaUsers, radioCheck, targetKeys, selectedKeys, topics, selectTableColumn, spinLoading } = this.state;
    const tableData = kafkaUsers.filter(row => row.show);
    const rulesNode = (
      <div>
        1、符合规范的ClientID格式为P#或C#前缀，分别代表生产者客户端和消费者客户端。
        <br />
        2、此处只展示符合规范的ClientID格式的高可用Topic。若未找到所需Topic，请检查ClientID的格式是否正确。
      </div>
    );

    return (
      <Modal
        title="Topic主备切换"
        wrapClassName="no-padding"
        visible={visible}
        onCancel={this.handleCancel}
        maskClosable={false}
        width={800}
        footer={<>
          <Button onClick={this.handleCancel}>取消</Button>
          <Button disabled={this.judgeSubmitStatus()} style={{ marginLeft: 8 }} type="primary" onClick={() => this.handleOk()}>确定</Button>
        </>
        }
      >
        <Alert
          message={`注意：必须把同一个${switchMode}关联的所有Topic都建立高可用关系，并且都选中，才能执行任务`}
          type="info"
          showIcon={true}
        />
        <Spin spinning={spinLoading}>
          <Form {...layout} name="basic" className="x-form">
            <Form.Item label="切换维度">
              {getFieldDecorator('switchMode', {
                initialValue: 'kafkaUser',
                rules: [{
                  required: true,
                  message: '请选择切换维度',
                }],
              })(<Radio.Group onChange={this.onModeChange}>
                <Radio value="kafkaUser">kafkaUser</Radio>
                <Radio value="clientID">kafkaUser + clientID</Radio>
              </Radio.Group>)}
            </Form.Item>
            {/* <Form.Item label="规则"  >
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
            {switchMode === 'clientID' && <div style={{ margin: '-10px 0 10px 0' }} >
              <Tooltip placement="bottomLeft" title={rulesNode}>
                <a>规则说明</a>
              </Tooltip>
            </div>}
            {radioCheck === 'spec' ? <Form.Item className="no-label" label=""  >
              {getFieldDecorator('targetKeys', {
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
                />,
              )}
            </Form.Item> : null}
          </Form>
          {radioCheck === 'spec' ?
            <>
              <Table
                className="modal-table-content"
                columns={selectTableColumn}
                dataSource={tableData}
                size="small"
                rowKey="kafkaUser"
                pagination={false}
                scroll={{ y: 300 }}
              />
              {tableData.length ? <div onClick={this.downloadData} className="modal-table-download"><a>下载表单内容</a></div> : null}
            </>
            : null}
        </Spin>
      </Modal>
    );
  }
}
export const TopicSwitchWrapper = Form.create<IXFormProps>()(TopicHaSwitch);

export const TableTransfer = ({ leftColumns, getManualSelected, tableAttrs, ...restProps }: any) => (
  <Transfer {...restProps} showSelectAll={true}>
    {({
      filteredItems,
      direction,
      onItemSelect,
      selectedKeys: listSelectedKeys,
    }) => {
      const columns = leftColumns;

      const rowSelection = {
        columnWidth: 40,
        getCheckboxProps: (item: any) => ({
          disabled: item.disabled,
        }),
        onSelect({ key }: any, selected: any) {
          getManualSelected(true, key, selected);
          onItemSelect(key, selected);
        },
        selectedRowKeys: listSelectedKeys,
      };
      return (
        <Table
          rowSelection={rowSelection}
          columns={columns}
          dataSource={filteredItems}
          size="small"
          pagination={false}
          scroll={{ y: 320 }}
          style={{ marginBottom: 14 }}
          bordered={false}
          onRow={({ key, disabled }) => ({
            onClick: () => {
              if (disabled) return;
              getManualSelected(true, key, listSelectedKeys.includes(key));
              onItemSelect(key, !listSelectedKeys.includes(key));
            },
          })}
          {...tableAttrs}
        />
      );
    }}
  </Transfer>
);

interface IProps {
  value?: any;
  onChange?: any;
  onDirectChange?: any;
  currentCluster: any;
  topicChange: any;
  columns: any[];
  dataSource: any[];
  selectedKeys: string[];
  getManualSelected: any;
  transferAttrs?: any;
  tableAttrs?: any;
}

export class TransferTable extends React.Component<IProps> {
  public onChange = (nextTargetKeys: any, direction: string, moveKeys: string[]) => {
    this.props.onDirectChange(nextTargetKeys, direction, moveKeys);
    // tslint:disable-next-line:no-unused-expression
    this.props.onChange && this.props.onChange(nextTargetKeys);
  }

  public render() {
    const { currentCluster, columns, dataSource, value, topicChange, selectedKeys, getManualSelected, transferAttrs, tableAttrs } = this.props;
    return (
      <div>
        <TableTransfer
          dataSource={dataSource}
          targetKeys={value || []}
          selectedKeys={selectedKeys}
          showSearch={true}
          onChange={this.onChange}
          onSelectChange={topicChange}
          filterOption={(inputValue: string, item: any) => item.topicName?.indexOf(inputValue) > -1}
          leftColumns={columns}
          titles={[`集群${currentCluster.clusterName}`, `集群${currentCluster.haClusterVO?.clusterName}`]}
          locale={{
            itemUnit: '',
            itemsUnit: '',
          }}
          getManualSelected={getManualSelected}
          tableAttrs={tableAttrs}
          {...transferAttrs}
        />
      </div>
    );
  }
}
