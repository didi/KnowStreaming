
import * as React from 'react';

import { SearchAndFilterContainer } from 'container/search-filter';
import { Table } from 'component/antd';
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

  public state = {
    searchKey: '',
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
        // render: (r: string, t: IController) => {
        //   return (
        //     <a href={`${this.urlPrefix}/admin/broker-detail?clusterId=${this.clusterId}&brokerId=${t.brokerId}`}>{r}
        //     </a>
        //   );
        // },
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
  }

  public render() {
    return (
      <div className="k-row">
        <ul className="k-tab">
          <li>{this.props.tab}</li>
          {this.renderSearch('', '请输入Host')}
        </ul>
        {this.renderController()}
      </div>
    );
  }
}
