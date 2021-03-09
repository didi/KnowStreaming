
import * as React from 'react';

import { SearchAndFilterContainer } from 'container/search-filter';
import { Table, Button, Popconfirm, Modal, Transfer, notification } from 'component/antd';
// import { Transfer } from 'antd'
import { observer } from 'mobx-react';
import { pagination } from 'constants/table';
import Url from 'lib/url-parser';
import { IController } from 'types/base-type';
import { admin } from 'store/admin';
import './index.less';
import moment from 'moment';
import { timeFormat } from 'constants/strategy';

@observer
export class ClusterController extends SearchAndFilterContainer {
  public clusterId: number;

  public state: any = {
    searchKey: '',
    searchCandidateKey: '',
    isCandidateModel: false,
    mockData: [],
    targetKeys: [],
  };

  constructor(props: any) {
    super(props);
    const url = Url();
    this.clusterId = Number(url.search.clusterId);
  }

  public getData<T extends IController>(origin: T[]) {
    let data: T[] = origin;
    let { searchKey } = this.state;
    searchKey = (searchKey + '').trim().toLowerCase();

    data = searchKey ? origin.filter((item: IController) =>
      (item.host !== undefined && item.host !== null) && item.host.toLowerCase().includes(searchKey as string),
    ) : origin;
    return data;
  }

  public getCandidateData<T extends IController>(origin: T[]) {
    let data: T[] = origin;
    let { searchCandidateKey } = this.state;
    searchCandidateKey = (searchCandidateKey + '').trim().toLowerCase();

    data = searchCandidateKey ? origin.filter((item: IController) =>
      (item.host !== undefined && item.host !== null) && item.host.toLowerCase().includes(searchCandidateKey as string),
    ) : origin;
    return data;
  }

  // 候选controller
  public renderCandidateController() {
    const columns = [
      {
        title: 'BrokerId',
        dataIndex: 'brokerId',
        key: 'brokerId',
        width: '20%',
        sorter: (a: IController, b: IController) => b.brokerId - a.brokerId,
        render: (r: string, t: IController) => {
          return (
            <a href={`${this.urlPrefix}/admin/broker-detail?clusterId=${this.clusterId}&brokerId=${t.brokerId}`}>{r}
            </a>
          );
        },
      },
      {
        title: 'BrokerHost',
        key: 'host',
        dataIndex: 'host',
        width: '20%',
        // render: (r: string, t: IController) => {
        //   return (
        //     <a href={`${this.urlPrefix}/admin/broker-detail?clusterId=${this.clusterId}&brokerId=${t.brokerId}`}>{r}
        //     </a>
        //   );
        // },
      },
      {
        title: 'Broker状态',
        key: 'status',
        dataIndex: 'status',
        width: '20%',
        render: (r: number, t: IController) => {
          return (
            <span>{r === 1 ? '不在线' : '在线'}</span>
          );
        },
      },
      {
        title: '创建时间',
        dataIndex: 'startTime',
        key: 'startTime',
        width: '25%',
        sorter: (a: IController, b: IController) => b.timestamp - a.timestamp,
        render: (t: number) => moment(t).format(timeFormat),
      },
      {
        title: '操作',
        dataIndex: 'operation',
        key: 'operation',
        width: '15%',
        render: (r: string, t: IController) => {
          return (
            <Popconfirm
              title="确定删除？"
              onConfirm={() => this.deleteCandidateCancel(t)}
              cancelText="取消"
              okText="确认"
            >
              <a>删除</a>
            </Popconfirm>
          );
        },
      },
    ];

    return (
      <Table
        columns={columns}
        dataSource={this.getCandidateData(admin.controllerCandidate)}
        pagination={pagination}
        rowKey="key"
      />
    );
  }

  public renderController() {

    const columns = [
      {
        title: 'BrokerId',
        dataIndex: 'brokerId',
        key: 'brokerId',
        width: '30%',
        sorter: (a: IController, b: IController) => b.brokerId - a.brokerId,
        render: (r: string, t: IController) => {
          return (
            <a href={`${this.urlPrefix}/admin/broker-detail?clusterId=${this.clusterId}&brokerId=${t.brokerId}`}>{r}
            </a>
          );
        },
      },
      {
        title: 'BrokerHost',
        key: 'host',
        dataIndex: 'host',
        width: '30%',
      },
      {
        title: '变更时间',
        dataIndex: 'timestamp',
        key: 'timestamp',
        width: '40%',
        sorter: (a: IController, b: IController) => b.timestamp - a.timestamp,
        render: (t: number) => moment(t).format(timeFormat),
      },
    ];

    return (
      <Table
        columns={columns}
        dataSource={this.getData(admin.controllerHistory)}
        pagination={pagination}
        rowKey="key"
      />
    );
  }

  public componentDidMount() {
    admin.getControllerHistory(this.clusterId);
    admin.getCandidateController(this.clusterId);
    admin.getBrokersMetadata(this.clusterId);
  }

  public addController = () => {
    this.setState({ isCandidateModel: true, targetKeys: [] })
  }

  public addCandidateChange = (targetKeys: any) => {
    this.setState({ targetKeys })
  }



  public handleCandidateCancel = () => {
    this.setState({ isCandidateModel: false });
  }

  public handleCandidateOk = () => {
    let brokerIdList = this.state.targetKeys.map((item: any) => {
      return admin.brokersMetadata[item].brokerId
    })
    admin.addCandidateController(this.clusterId, brokerIdList).then(data => {
      notification.success({ message: '新增成功' });
      admin.getCandidateController(this.clusterId);
    }).catch(err => {
      notification.error({ message: '新增失败' });
    })
    this.setState({ isCandidateModel: false, targetKeys: [] });
  }

  public deleteCandidateCancel = (target: any) => {
    admin.deleteCandidateCancel(this.clusterId, [target.brokerId]).then(() => {
      notification.success({ message: '删除成功' });
    });
    this.setState({ isCandidateModel: false });
  }

  public renderAddCandidateController() {
    let filterControllerCandidate = admin.brokersMetadata.filter((item: any) => {
      return !admin.filtercontrollerCandidate.includes(item.brokerId)
    })

    return (
      <Modal
        title="新增候选Controller"
        visible={this.state.isCandidateModel}
        // okText="确认"
        // cancelText="取消"
        maskClosable={false}
        // onOk={() => this.handleCandidateOk()}
        onCancel={() => this.handleCandidateCancel()}
        footer={<>
          <Button style={{ width: '60px' }} onClick={() => this.handleCandidateCancel()}>取消</Button>
          <Button disabled={this.state.targetKeys.length > 0 ? false : true} style={{ width: '60px' }} type="primary" onClick={() => this.handleCandidateOk()}>确定</Button>
        </>
        }
      >
        <Transfer
          dataSource={filterControllerCandidate}
          targetKeys={this.state.targetKeys}
          render={item => item.host}
          onChange={(targetKeys) => this.addCandidateChange(targetKeys)}
          titles={['未选', '已选']}
          locale={{
            itemUnit: '项',
            itemsUnit: '项',
          }}
          listStyle={{
            width: "45%",
          }}
        />
      </Modal>
    );
  }

  public render() {
    return (
      <div className="k-row">
        <ul className="k-tab">
          <li>
            <span>候选Controller</span>
            <span style={{ display: 'inline-block', color: "#a7a8a9", fontSize: '12px', marginLeft: '15px' }}>Controller将会优先从以下Broker中选举</span>
          </li>
          <div style={{ display: 'flex' }}>
            <div style={{ marginRight: '15px' }}>
              <Button onClick={() => this.addController()} type='primary'>新增候选Controller</Button>
            </div>
            {this.renderSearch('', '请查找Host', 'searchCandidateKey')}
          </div>
        </ul>
        {this.renderCandidateController()}
        <ul className="k-tab" style={{ marginTop: '10px' }}>
          <li>{this.props.tab}</li>
          {this.renderSearch('', '请输入Host')}
        </ul>
        {this.renderController()}
        {this.renderAddCandidateController()}
      </div>
    );
  }
}
