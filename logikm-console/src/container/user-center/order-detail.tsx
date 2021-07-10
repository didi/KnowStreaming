import * as React from 'react';
import { PageHeader, Button, Descriptions, Steps, Divider, Popconfirm, message, Table, Tooltip, Spin, Alert } from 'component/antd';
import { IOrderInfo, IUser, IConnectionInfo } from 'types/base-type';
import { showApprovalModal } from 'container/modal/order';
import { getInfoRenderItem } from './config';
import { observer } from 'mobx-react';
import { order } from 'store/order';
import { admin } from 'store/admin';
import { users } from 'store/users';
import { cellStyle } from 'constants/table';
import { timeFormat } from 'constants/strategy';
import { handlePageBack } from 'lib/utils';
import Url from 'lib/url-parser';
import moment from 'moment';
import './index.less';

const { Step } = Steps;

@observer
export class OrderDetail extends React.Component {
  public result: boolean;

  constructor(private orderId: number) {
    super(orderId);
    const url = Url();
    this.orderId = Number(url.search.orderId);
  }

  public dealInfo(info: IOrderInfo) {
    info = JSON.parse(JSON.stringify(info));
    if (!info.detail) {
      info.detail = {};
    }

    const { approverList = [] } = info;
    info.approvers = '';

    approverList.forEach(item => {
      if (item.chineseName) {
        info.approvers += `${item.chineseName} `;
      }
    });
    info.applicant = info.applicant || {} as IUser;
    info.currentStep = info.status === 0 ? 1 : (info.status === 1 || info.status === 2) ? 2 : 0; // 0待审批 1通过 2拒绝 3撤销
    info.gmtCreate = moment(info.gmtCreate).format(timeFormat);
    return info;
  }

  public getOrderDetail() {
    const { orderId } = this;
    order.getOrderDetail(orderId);
  }

  public cancelOrder() {
    order.cancelOrder(order.orderInfo.id).then(() => {
      message.success('撤销成功');
      this.getOrderDetail();
      order.getApplyOrderList(0);
      order.getApprovalList(0);
    });
  }

  public renderDetail() {
    const infoList = getInfoRenderItem(order.orderInfo, this.result);
    const type = order.orderInfo.type;
    const info = this.dealInfo(order.orderInfo);
    return (
      <>
        <Descriptions title={`申请内容-${order.orderTypeMap[type] || ''}`} column={type === 1 ? 2 : 3}>
          {infoList && infoList.map((item, key) => (
            <Descriptions.Item key={key} label={item.label}>
              <Tooltip placement="bottomLeft" title={item.value}>
                <span className="overview-bootstrap">
                  <i className="overview-boot">{item.value}</i>
                </span>
              </Tooltip>
            </Descriptions.Item>
          ))}
        </Descriptions>
        <Descriptions column={1}>
          <Descriptions.Item label="申请原因">
            <Tooltip placement="bottomLeft" title={info.description}>
              <span className="overview-bootstrap reason-application">
                <i className="overview-boot">{info.description}</i>
              </span>
            </Tooltip>
          </Descriptions.Item>
        </Descriptions>
        {(type === 10 || type === 11 || type === 13) ? this.getConnectOffline() : null}
        {type === 14 ? this.getClusterOffline() : null}
      </>
    );
  }

  public getConnectOffline() {
    let connectSource = [] as IConnectionInfo[];
    connectSource = order.orderInfo.detail.connectionList;
    const connectColumns = [
      {
        title: 'AppID',
        dataIndex: 'appId',
        key: 'appId',
        width: '20%',
        sorter: (a: IConnectionInfo, b: IConnectionInfo) => a.appId.charCodeAt(0) - b.appId.charCodeAt(0),
      },
      {
        title: '主机名',
        dataIndex: 'hostname',
        key: 'hostname',
        width: '40%',
        onCell: () => ({
          style: {
            maxWidth: 250,
            ...cellStyle,
          },
        }),
        render: (t: string) => {
          return (
            <Tooltip placement="bottomLeft" title={t} >{t}</Tooltip>
          );
        },
      },
      {
        title: '客户端版本',
        dataIndex: 'clientVersion',
        key: 'clientVersion',
        width: '20%',
      },
      {
        title: '客户端类型',
        dataIndex: 'clientType',
        key: 'clientType',
        width: '20%',
        render: (t: string) => <span>{t === 'consumer' ? '消费' : '生产'}</span>,
      },
      // {
      //   title: '客户端语言',
      //   dataIndex: 'language',
      //   key: 'language',
      //   width: '20%',
      // },
    ];
    return (
      <>
        <h3 className="consumer-head">连接信息：</h3>
        <Table
          rowKey="key"
          dataSource={connectSource}
          columns={connectColumns}
          pagination={false}
          scroll={{ y: 240 }}
        />
      </>
    );
  }

  public getClusterOffline() {
    let clusterSource = [] as any[];
    clusterSource = order.orderInfo.detail.topicNameList.map((item, index) => {
      return {
        key: index,
        topicName: item,
      };
    });
    const clusterColumns = [
      {
        title: '集群Topic列表',
        dataIndex: 'topicName',
        key: 'topicName',
      },
    ];
    return (
      <>
        <Table
          rowKey="key"
          dataSource={clusterSource}
          columns={clusterColumns}
          pagination={false}
          scroll={{ y: 240 }}
        />
      </>
    );
  }

  public getOpinion(info: IOrderInfo) {
    return (
      <>
        <Tooltip placement="bottom" title={info.opinion} >
          <span className="detail-opinion">{info.opinion}</span>
        </Tooltip>
        {moment(info.gmtHandle).format(timeFormat)}
      </>
    );
  }

  public async adopt(info: IOrderInfo) {
    // tslint:disable-next-line:max-line-length
    if ((info.type === 0 || info.type === 2) && info.detail) {
      admin.setRegionIdList(info.detail.regionIdList || []);
      if (info.detail.physicalClusterId) {
        await admin.getBrokersRegions(info.detail.physicalClusterId);
      }
    }
    await showApprovalModal(info, 1, 'detail');
  }
  public componentDidMount() {
    this.getOrderDetail();
    order.getOrderTypeList();
  }

  public render() {
    const info = this.dealInfo(order.orderInfo);
    this.result = info.approverList.some((ele) => {
      if (ele.username === users.currentUser.username) {
        return true;
      }
    });
    return (
      <>
        <Spin spinning={order.loading}>
          <PageHeader
            className="btn-group"
            onBack={() => handlePageBack('/user/my-order')}
            title="工单详情"
            extra={info.currentStep === 1 ?
              <span key="6">
                {
                  this.result ? <span key="5">
                    <Button key="3" type="primary" className="detail_but" onClick={() => this.adopt(info)}>通过</Button>
                    <Button key="2" className="detail_but" onClick={() => showApprovalModal(info, 2, 'detail')}>驳回</Button>
                  </span> : null
                }
                <Button key="1">
                  <Popconfirm title="确定撤回？" okText="撤回" cancelText="取消" onConfirm={() => this.cancelOrder()}>
                    <a>撤回</a>
                  </Popconfirm>
                </Button>
              </span>
              : null}
          />
          <div className="work_detail_box">
            <Steps
              className="step"
              current={info.currentStep}
              status={(info.currentStep === 2 && info.status === 2) ? 'error' : 'process'}
              progressDot={true}
            >
              <Step
                title={`${order.orderTypeMap[info.type] || ''}${info.status === 3 ? '（已撤回）' : ''}`}
                subTitle={info.applicant.chineseName}
                description={moment(info.gmtCreate).format(timeFormat)}
              />
              <Step title="待审批" subTitle={info.status === 0 ? info.approvers : null} />
              <Step
                title={info.status === 1 ? '已通过' : (info.status === 2 ? '已拒绝' : '完成')}
                subTitle={info.currentStep === 2 ? info.approvers : null}
                description={info.currentStep === 2 ? this.getOpinion(info) : null}
              />
            </Steps>
            <Divider />
            <Descriptions title="申请人信息" column={3}>
              <Descriptions.Item label="申请人">{info.applicant.chineseName}</Descriptions.Item>
              <Descriptions.Item label="申请时间">{moment(info.gmtCreate).format(timeFormat)}</Descriptions.Item>
              <Descriptions.Item label="部门-岗位">{info.applicant.department}</Descriptions.Item>
            </Descriptions>
            <Divider />
            {order.orderInfo && order.orderInfo.detail ? this.renderDetail() : null}
          </div>
        </Spin>
      </>);
  }
}
