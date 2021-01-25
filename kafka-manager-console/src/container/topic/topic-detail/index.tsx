import * as React from 'react';
import './index.less';
import { wrapper, region } from 'store';
import { Tabs, PageHeader, Button, notification, Drawer, message, Icon, Spin } from 'antd';
import { observer } from 'mobx-react';
import { BaseInformation } from './base-information';
import { StatusChart } from './status-chart';
import { ConnectInformation } from './connect-information';
import { GroupID } from './group-id';
import { PartitionInformation } from './partition-information';
import { BrokersInformation } from './brokers-information';
import { AppIdInformation } from './appid-information';
import { BillInformation } from './bill-information';
import { showAllPermissionModal } from 'container/modal';
import { IXFormWrapper, ITopic } from 'types/base-type';
import { getTopicCompile, getTopicSampling } from 'lib/api';
import { copyString } from 'lib/utils';
import { topic, ITopicBaseInfo } from 'store/topic';
import { XFormComponent, IFormItem } from 'component/x-form';
import { applyExpandModal } from 'container/modal';
import { applyTopicQuotaQuery } from '../config';
import { users } from 'store/users';
import { urlPrefix } from 'constants/left-menu';
import { handlePageBack } from 'lib/utils';
import Url from 'lib/url-parser';
import router from 'routers/router';
const { TabPane } = Tabs;
import { app } from 'store/app';

interface IInfoData {
  value: string;
  history: any;
}

@observer
export class TopicDetail extends React.Component<any> {
  public clusterId: number;
  public topicName: string;
  public isPhysicalTrue: string;
  public needAuth: string;
  public clusterName: string;

  public state = {
    drawerVisible: false,
    infoVisible: false,
    infoTopicList: [] as IInfoData[],
    isExecutionBtn: false
  };
  private $formRef: any;

  private xFormWrapper: IXFormWrapper;

  constructor(props: any) {
    super(props);
    const url = Url();
    this.clusterId = Number(url.search.clusterId);
    this.needAuth = url.search.needAuth;
    this.clusterName = decodeURI(decodeURI(url.search.clusterName));
    this.topicName = url.search.topic;
    const isPhysical = Url().search.hasOwnProperty('isPhysicalClusterId');
    this.isPhysicalTrue = isPhysical ? '&isPhysicalClusterId=true' : '';
  }

  public compileDetails() {
    this.xFormWrapper = {
      formMap: [
        {
          key: 'topicName',
          label: 'Topic名称',
          attrs: {
            disabled: true,
          },
        },
        {
          key: 'appName',
          label: '应用名称',
          attrs: {
            disabled: true,
          },
        },
        {
          key: 'description',
          label: '备注',
          attrs: {
            placeholder: '请输入备注',
          },
          rules: [{
            required: true,
            message: '请输入备注',
          }],
        },
      ],
      formData: {
        topicName: this.topicName,
        appName: topic.baseInfo.appName,
        description: topic.baseInfo.description,
      },
      okText: '确认',
      visible: true,
      width: 600,
      title: '编辑',
      onSubmit: (value: any) => {
        const compile = {
          appId: topic.baseInfo.appId,
          clusterId: this.clusterId,
          description: value.description,
          topicName: this.topicName,
        } as ITopic;
        getTopicCompile(compile).then(data => {
          notification.success({ message: '编辑成功' });
          topic.getTopicBasicInfo(this.clusterId, this.topicName);
        });
      },
    };
    wrapper.open(this.xFormWrapper);
  }

  public drawerRender() {
    const formMap = [
      {
        key: 'maxMsgNum',
        label: '最大采样条数',
        type: 'input_number',
        rules: [{
          required: true,
          message: '请输入最大采样条数',
        }],
        attrs: {
          max: 100,
        },
      },
      {
        key: 'timeout',
        label: '采样超时时间(ms)',
        type: 'input_number',
        rules: [{
          required: true,
          message: '请输入采样超时时间(ms)',
        }],
        attrs: {
          max: 500000,
        },
      },
      {
        key: 'partitionId',
        label: '采样分区号',
        type: 'input_number',
        rules: [{
          required: false,
          message: '请输入采样分区号',
        }],
      },
      {
        key: 'offset',
        label: '采样offset位置',
        type: 'input_number',
        rules: [{
          required: false,
          message: '请输入采样offset位置',
        }],
      },
      {
        key: 'truncate',
        label: '是否截断',
        type: 'radio_group',
        defaultValue: 'true',
        options: [{
          label: '是',
          value: 'true',
        }, {
          label: '否',
          value: 'false',
        }],
        rules: [{
          required: true,
          message: '请选择是否截断',
        }],
      },
    ] as IFormItem[];
    const formData = {
      maxMsgNum: 1,
      timeout: 5000,
    };
    const { infoVisible } = this.state;
    return (
      <>
        <Drawer
          title="Topic 采样"
          placement="right"
          closable={false}
          onClose={this.drawerClose}
          visible={this.state.drawerVisible}
          width={600}
          key="1"
        >
          <XFormComponent
            ref={form => this.$formRef = form}
            formData={formData}
            formMap={formMap}
          />
          <Button type="primary" onClick={this.drawerSubmit} className="sample-button" disabled={this.state.isExecutionBtn}>
            {this.state.isExecutionBtn ? (<span>采样中<Spin indicator={this.antIcon} size="small" /></span>) : '采 样'}
          </Button>
          {infoVisible ? this.renderInfo() : null}
        </Drawer>
      </>
    );
  }

  public getAllValue = () => {
    const { infoTopicList } = this.state;
    const text = infoTopicList.map(ele => ele.value);
    return text.join('\n\n');
  }

  public renderInfo() {
    const { infoTopicList } = this.state;
    return (
      <>
        <div>
          <div className="topic-detail-sample">
            <h2>采样</h2>
            <div className="detail-sample-box">
              <h3>
                {this.topicName} <Icon
                  onClick={() => copyString(this.getAllValue())}
                  type="copy"
                  className="didi-theme"
                />
              </h3>
              <div className="detail-sample-span">
                {infoTopicList.map((v, index) => (
                  <li key={index}>
                    <Icon
                      onClick={() => copyString(v.value)}
                      type="copy"
                      className="didi-theme"
                    /> {v.value}
                  </li>
                ))}
              </div>
            </div>
          </div>
        </div>
      </>
    );
  }

  // 执行加载图标
  public antIcon = <Icon type="loading" style={{ fontSize: 12, color: '#cccccc', marginLeft: '5px' }} spin />

  public drawerSubmit = (value: any) => {
    this.setState({ isExecutionBtn: true })
    this.$formRef.validateFields((error: Error, result: any) => {
      if (error) {
        return;
      }
      result.truncate = result.truncate === 'true';
      getTopicSampling(result, this.clusterId, this.topicName).then(data => {
        this.setState({
          infoTopicList: data,
          infoVisible: true,
          isExecutionBtn: false
        });
        message.success('采样成功');
      }).catch(err => {
        this.setState({ isExecutionBtn: false })
      })
    });
  }

  public showDrawer = () => {
    this.setState({
      drawerVisible: true,
    });
  }

  public drawerClose = () => {
    this.setState({
      drawerVisible: false,
      infoVisible: false,
    });
    this.resetForm();
  }

  public resetForm = (resetFields?: string[]) => {
    // tslint:disable-next-line:no-unused-expression
    this.$formRef && this.$formRef.resetFields(resetFields || '');
  }

  public cusstr(str: string, findStr: string, num: number) {
    let idx = str.indexOf(findStr);
    let count = 1;
    while (idx >= 0 && count < num) {
      idx = str.indexOf(findStr, idx + 1);
      count++;
    }
    if (idx < 0) {
      return '';
    }
    return str.substring(0, idx);
  }

  public setConsumeUrl = (key: string) => {
    // tslint:disable-next-line:max-line-length
    const url = `${urlPrefix}/topic/topic-detail?clusterId=${this.clusterId}&topic=${this.topicName}${this.isPhysicalTrue}&region=${region.currentRegion}#${key}`;
    history.pushState({ url }, '', url);
  }

  public handleTabKey = (key: string) => {
    location.hash = key;
    if (location.search.match(RegExp(/&consumerGroup=/))) {
      this.setConsumeUrl(key);
    }
  }

  public onTabClick = (key: string) => {
    location.hash = key;
    if (key === '4') {
      topic.setConsumeDetail(false);
      this.setConsumeUrl(key);
    }
  }

  public componentDidMount() {
    topic.getTopicBasicInfo(this.clusterId, this.topicName);
    topic.getTopicBusiness(this.clusterId, this.topicName);
    app.getAppList();
  }

  public render() {
    const role = users.currentUser.role;
    const baseInfo = topic.baseInfo as ITopicBaseInfo;
    const showEditBtn = (role == 1 || role == 2) || (topic.topicBusiness && topic.topicBusiness.principals.includes(users.currentUser.username));
    const topicRecord = {
      clusterId: this.clusterId,
      topicName: this.topicName,
      clusterName: this.clusterName
    } as ITopic;

    return (
      <>
        {
          baseInfo ?
            <>
              <PageHeader
                className="detail topic-detail-header"
                onBack={() => handlePageBack(`/topic`)}
                title={this.topicName || ''}
                extra={
                  <>
                    {this.needAuth == "true" && <Button key="0" type="primary" onClick={() => showAllPermissionModal(topicRecord)} >申请权限</Button>}
                    <Button key="1" type="primary" onClick={() => applyTopicQuotaQuery(topicRecord)} >申请配额</Button>
                    <Button key="2" type="primary" onClick={() => applyExpandModal(topicRecord)} >申请分区</Button>
                    <Button key="3" type="primary" onClick={() => this.props.history.push(`/alarm/add`)} >新建告警</Button>
                    <Button key="4" type="primary" onClick={this.showDrawer.bind(this)} >数据采样</Button>
                    {/* {showEditBtn && <Button key="5" onClick={() => this.compileDetails()} type="primary">编辑</Button>} */}
                  </>
                }
              />
              <Tabs
                activeKey={location.hash.substr(1) || '1'}
                type="card"
                onChange={this.handleTabKey}
                onTabClick={this.onTabClick}
              >
                <TabPane tab="基本信息" key="1">
                  <BaseInformation baseInfo={topic.baseInfo} />
                </TabPane>
                <TabPane tab="状态图" key="2">
                  <StatusChart />
                </TabPane>
                <TabPane tab="连接信息" key="3">
                  <ConnectInformation baseInfo={topic.baseInfo} />
                </TabPane>
                <TabPane tab="消费组信息" key="4">
                  <GroupID />
                </TabPane>
                <TabPane tab="分区信息" key="5">
                  <PartitionInformation />
                </TabPane>
                {
                  role === 0 ? null :
                    <TabPane tab="Broker信息" key="6">
                      <BrokersInformation />
                    </TabPane>
                }
                <TabPane tab="应用信息" key="7">
                  <AppIdInformation />
                </TabPane>
                <TabPane tab="账单信息" key="8">
                  <BillInformation />
                </TabPane>
              </Tabs>
            </>
            : null
        }
        {this.drawerRender()}
      </>
    );
  }
}
