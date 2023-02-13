import * as React from 'react';
import { admin } from 'store/admin';
import { Modal, Form, Radio, Tag, Popover, Button } from 'antd';
import { IBrokersMetadata, IBrokersRegions, IMetaData } from 'types/base-type';
import { Alert, Icon, message, Table, Transfer } from 'component/antd';
import { getClusterHaTopics, getAppRelatedTopics, createSwitchTask } from 'lib/api';
import { TooltipPlacement } from 'antd/es/tooltip';
import * as XLSX from 'xlsx';
import moment from 'moment';
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

interface IKafkaUser {
  clusterPhyId: number;
  kafkaUser: string;
  notHaTopicNameList: string[];
  notSelectTopicNameList: string[];
  selectedTopicNameList: string[];
  show: boolean;
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
    dataIndex: 'selectedTopicNameList',
    title: '已选中Topic',
    width: 120,
    render: (text: string[]) => {
      return text?.length ? renderAttributes({ data: text, limit: 3 }) : '-';
    },
  },
  {
    dataIndex: 'notSelectTopicNameList',
    title: '选中关联Topic',
    width: 120,
    render: (text: string[]) => {
      return text?.length ? renderAttributes({ data: text, limit: 3 }) : '-';
    },
  },
  {
    dataIndex: 'notHaTopicNameList',
    title: '未建立HA Topic',
    width: 120,
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
    targetKeys: [] as string[],
    selectedKeys: [] as string[],
    topics: [] as IHaTopic[],
    kafkaUsers: [] as IKafkaUser[],
    primaryActiveKeys: [] as string[],
    primaryStandbyKeys: [] as string[],
    firstMove: true,
  };

  public isPrimaryStatus = (targetKeys: string[]) => {
    const { primaryStandbyKeys } = this.state;
    let isReset = false;
    // 判断当前移动是否还原为最初的状态
    if (primaryStandbyKeys.length === targetKeys.length) {
      targetKeys.sort((a, b) => +a - (+b));
      primaryStandbyKeys.sort((a, b) => +a - (+b));
      let i = 0;
      while (i < targetKeys.length) {
        if (targetKeys[i] === primaryStandbyKeys[i]) {
          i++;
        } else {
          break;
        }
      }
      isReset = i === targetKeys.length;
    }
    return isReset;
  }

  public getTargetTopics = (currentKeys: string[], primaryKeys: string[]) => {
    const targetTopics = [];
    for (const key of currentKeys) {
      if (!primaryKeys.includes(key)) {
        const topic = this.state.topics.find(item => item.key === key)?.topicName;
        targetTopics.push(topic);
      }
    }
    return targetTopics;
  }

  public handleOk = () => {
    const { primaryStandbyKeys, primaryActiveKeys, topics } = this.state;
    const standbyClusterId = this.props.currentCluster.haClusterVO.clusterId;
    const activeClusterId = this.props.currentCluster.clusterId;

    this.props.form.validateFields((err: any, values: any) => {

      if (values.rule === 'all') {
        createSwitchTask({
          activeClusterPhyId: activeClusterId,
          all: true,
          mustContainAllKafkaUserTopics: true,
          standbyClusterPhyId: standbyClusterId,
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
      createSwitchTask({
        activeClusterPhyId,
        all: false,
        mustContainAllKafkaUserTopics: true,
        standbyClusterPhyId,
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
          relatedTopics = relatedTopics.concat(item.selectedTopicNameList);
          relatedTopics = relatedTopics.concat(item.notSelectTopicNameList);
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

  public getFilterTopics = (selectKeys: string[]) => {
    // 依据key值找topicName
    const filterTopics: string[] = [];
    const targetKeys = selectKeys;
    for (const key of targetKeys) {
      const topicName = this.state.topics.find(item => item.key === key)?.topicName;
      if (topicName) {
        filterTopics.push(topicName);
      }
    }
    return filterTopics;
  }

  public getNewKafkaUser = (targetKeys: string[]) => {
    const { primaryStandbyKeys, topics } = this.state;
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
    const newKafkaUsers = this.state.kafkaUsers;

    const moveTopics = this.getFilterTopics(keepKeys);

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
    const filterTopicNameList = this.getFilterTopics(selectedKeys);
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
    const clusterPhyId = topics.find(item => item.topicName === filterTopicNameList[0]).activeClusterId;
    getAppRelatedTopics({
      clusterPhyId,
      filterTopicNameList,
    }).then((res: IKafkaUser[]) => {
      let notSelectTopicNames: string[] = [];
      const notSelectTopicKeys: string[] = [];
      for (const item of (res || [])) {
        notSelectTopicNames = notSelectTopicNames.concat(item.notSelectTopicNameList || []);
      }

      for (const item of notSelectTopicNames) {
        const key = topics.find(row => row.topicName === item)?.key;

        if (key) {
          notSelectTopicKeys.push(key);
        }
      }

      const newSelectedKeys = selectedKeys.concat(notSelectTopicKeys);
      const newKafkaUsers = (res || []).map(item => ({
        ...item,
        show: true,
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
      const activeClusterId = topics.find(item => item.key === keys[0]).activeClusterId;
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
    const { primaryStandbyKeys, firstMove, primaryActiveKeys, topics } = this.state;

    const getKafkaUser = () => {
      const newKafkaUsers = this.state.kafkaUsers;
      const moveTopics = this.getFilterTopics(moveKeys);
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
    const { kafkaUsers } = this.state;
    const tableData = kafkaUsers.map(item => {
      return {
        // tslint:disable
        'kafkaUser': item.kafkaUser,
        '已选中Topic': item.selectedTopicNameList?.join('、'),
        '选中关联Topic': item.notSelectTopicNameList?.join('、'),
        '未建立HA Topic': item.notHaTopicNameList?.join(`、`),
      };
    });
    const data = [].concat(tableData);
    const wb = XLSX.utils.book_new();
    // json转sheet
    const ws = XLSX.utils.json_to_sheet(data, {
      header: ['kafkaUser', '已选中Topic', '选中关联Topic', '未建立HA Topic'],
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

  public componentDidMount() {
    const standbyClusterId = this.props.currentCluster.haClusterVO.clusterId;
    const activeClusterId = this.props.currentCluster.clusterId;
    getClusterHaTopics(this.props.currentCluster.clusterId, standbyClusterId).then((res: IHaTopic[]) => {
      res = res.map((item, index) => ({
        key: index.toString(),
        ...item,
      }));
      const targetKeys = (res || []).filter((item) => item.activeClusterId === standbyClusterId).map(row => row.key);
      const primaryActiveKeys = (res || []).filter((item) => item.activeClusterId === activeClusterId).map(row => row.key);
      this.setState({
        topics: res || [],
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
    const tableData = this.state.kafkaUsers.filter(row => row.show);

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
          message={`注意：必须把同一个kafkauser关联的所有Topic都建立高可用关系，并且都选中，才能执行任务`}
          type="info"
          showIcon={true}
        />
        <Form {...layout} name="basic" className="x-form">
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
          {this.state.radioCheck === 'spec' ? <Form.Item className="no-label" label=""  >
            {getFieldDecorator('targetKeys', {
              initialValue: this.state.targetKeys,
              rules: [{
                required: false,
                message: '请选择Topic',
              }],
            })(
              <TransferTable
                selectedKeys={this.state.selectedKeys}
                topicChange={this.handleTopicChange}
                onDirectChange={this.onDirectChange}
                dataSource={this.state.topics}
                currentCluster={currentCluster}
              />,
            )}
          </Form.Item> : ''}
        </Form>
        {this.state.radioCheck === 'spec' ?
          <>
            <Table
              className="modal-table-content"
              columns={kafkaUserColumn}
              dataSource={tableData}
              size="small"
              rowKey="kafkaUser"
              pagination={false}
              scroll={{ y: 300 }}
            />
            {this.state.kafkaUsers.length ? <div onClick={this.downloadData} className="modal-table-download"><a>下载表单内容</a></div> : null}
          </>
          : null}
      </Modal>
    );
  }
}
export const TopicSwitchWrapper = Form.create<IXFormProps>()(TopicHaSwitch);

const TableTransfer = ({ leftColumns, ...restProps }: any) => (
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
              onItemSelect(key, !listSelectedKeys.includes(key));
            },
          })}
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
  dataSource: any[];
  selectedKeys: string[];
}

export class TransferTable extends React.Component<IProps> {
  public onChange = (nextTargetKeys: any, direction: string, moveKeys: string[]) => {
    this.props.onDirectChange(nextTargetKeys, direction, moveKeys);
    // tslint:disable-next-line:no-unused-expression
    this.props.onChange && this.props.onChange(nextTargetKeys);
  }

  public render() {
    const { currentCluster, dataSource, value, topicChange, selectedKeys } = this.props;
    return (
      <div>
        <TableTransfer
          dataSource={dataSource}
          targetKeys={value || []}
          selectedKeys={selectedKeys}
          showSearch={true}
          onChange={this.onChange}
          onSelectChange={topicChange}
          leftColumns={columns}
          titles={[`集群${currentCluster.clusterName}`, `集群${currentCluster.haClusterVO.clusterName}`]}
          locale={{
            itemUnit: '',
            itemsUnit: '',
          }}
        />
      </div>
    );
  }
}
