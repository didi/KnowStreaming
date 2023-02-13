import * as React from 'react';
import { admin } from 'store/admin';
import { Modal, Form, Radio } from 'antd';
import { IBrokersMetadata, IBrokersRegions, IMetaData } from 'types/base-type';
import { Alert, message, notification, Table, Tooltip, Transfer } from 'component/antd';
import { getClusterHaTopicsStatus, setHaTopics, unbindHaTopics } from 'lib/api';
import { cellStyle } from 'constants/table';

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
class TopicHaRelation extends React.Component<IXFormProps> {
  public state = {
    radioCheck: 'spec',
    haTopics: [] as IHaTopic[],
    targetKeys: [] as string[],
    confirmLoading: false,
    firstMove: true,
    primaryActiveKeys: [] as string[],
    primaryStandbyKeys: [] as string[],
  };

  public handleOk = () => {
    this.props.form.validateFields((err: any, values: any) => {
      const unbindTopics = [];
      const bindTopics = [];

      if (values.rule === 'all') {
        setHaTopics({
          all: true,
          activeClusterId: this.props.currentCluster.clusterId,
          standbyClusterId: this.props.currentCluster.haClusterVO.clusterId,
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

      for (const item of this.state.primaryStandbyKeys) {
        if (!this.state.targetKeys.includes(item)) {
          unbindTopics.push(item);
        }
      }
      for (const item of this.state.targetKeys) {
        if (!this.state.primaryStandbyKeys.includes(item)) {
          bindTopics.push(item);
        }
      }

      if (!unbindTopics.length && !bindTopics.length) {
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

      if (bindTopics.length) {
        this.setState({
          confirmLoading: true,
        });
        setHaTopics({
          all: false,
          activeClusterId: this.props.currentCluster.clusterId,
          standbyClusterId: this.props.currentCluster.haClusterVO.clusterId,
          topicNames: bindTopics,
        }).then(res => {
          this.setState({
            confirmLoading: false,
          });
          this.handleCancel();
          handleMsg(res, '关联成功');
        });
      }

      if (unbindTopics.length) {
        this.setState({
          confirmLoading: true,
        });
        unbindHaTopics({
          all: false,
          activeClusterId: this.props.currentCluster.clusterId,
          standbyClusterId: this.props.currentCluster.haClusterVO.clusterId,
          topicNames: unbindTopics,
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

  public setTopicsStatus = (targetKeys: string[], disabled: boolean, isAll = false) => {
    const { haTopics } = this.state;
    const newTopics = Array.from(haTopics);
    if (isAll) {
      for (let i = 0; i < haTopics.length; i++) {
        newTopics[i].disabled = disabled;
      }
    } else {
      for (const key of targetKeys) {
        const index = haTopics.findIndex(item => item.key === key);
        if (index > -1) {
          newTopics[index].disabled = disabled;
        }
      }
    }
    this.setState(({
      haTopics: newTopics,
    }));
  }

  public onTransferChange = (targetKeys: string[], direction: string, moveKeys: string[]) => {
    const { primaryStandbyKeys, firstMove, primaryActiveKeys } = this.state;
    // 判断当前移动是否还原为最初的状态
    const isReset = this.isPrimaryStatus(targetKeys);
    if (firstMove) {
      const primaryKeys = direction === 'right' ? primaryStandbyKeys : primaryActiveKeys;
      this.setTopicsStatus(primaryKeys, true, false);
      this.setState(({
        firstMove: false,
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
      }));
      return;
    }

    this.setState({
      targetKeys,
    });
  }

  public componentDidMount() {
    Promise.all([
      getClusterHaTopicsStatus(this.props.currentCluster.clusterId, true),
      getClusterHaTopicsStatus(this.props.currentCluster.clusterId, false),
    ]).then(([activeRes, standbyRes]: IHaTopic[][]) => {
      activeRes = (activeRes || []).map(row => ({
        ...row,
        key: row.topicName,
      })).filter(item => item.haRelation === null);
      standbyRes = (standbyRes || []).map(row => ({
        ...row,
        key: row.topicName,
      })).filter(item => item.haRelation === 1 || item.haRelation === 0);
      this.setState({
        haTopics: [].concat([...activeRes, ...standbyRes]).sort((a, b) => a.topicName.localeCompare(b.topicName)),
        primaryActiveKeys: activeRes.map(row => row.topicName),
        primaryStandbyKeys: standbyRes.map(row => row.topicName),
        targetKeys: standbyRes.map(row => row.topicName),
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
    return (
      <>
        <Modal
          title="Topic高可用关联"
          wrapClassName="no-padding"
          visible={visible}
          onOk={this.handleOk}
          onCancel={this.handleCancel}
          maskClosable={false}
          confirmLoading={this.state.confirmLoading}
          width={590}
          okText="确认"
          cancelText="取消"
        >
          <Alert
            message={`将【集群${currentCluster.clusterName}】和【集群${currentCluster.haClusterVO?.clusterName}】的Topic关联高可用关系`}
            type="info"
            showIcon={true}
          />
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
            {this.state.radioCheck === 'spec' ? <Form.Item className="no-label" label=""  >
              {getFieldDecorator('topicNames', {
                initialValue: this.state.targetKeys,
                rules: [{
                  required: false,
                  message: '请选择Topic',
                }],
              })(
                <Transfer
                  className="transfe-list"
                  dataSource={this.state.haTopics}
                  targetKeys={this.state.targetKeys}
                  showSearch={true}
                  onChange={this.onTransferChange}
                  render={item => item.topicName}
                  titles={['未关联', '已关联']}
                  locale={{
                    itemUnit: '',
                    itemsUnit: '',
                  }}
                />,
              )}
            </Form.Item> : ''}
          </Form>
        </Modal>
      </>
    );
  }
}
export const TopicHaRelationWrapper = Form.create<IXFormProps>()(TopicHaRelation);
