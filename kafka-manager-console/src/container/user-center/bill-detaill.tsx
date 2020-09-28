import * as React from 'react';
import { Table, Tabs, Icon, Spin } from 'component/antd';
import { pagination } from 'constants/table';
import { observer } from 'mobx-react';
import { bill } from 'store/bill';
import { SearchAndFilterContainer } from 'container/search-filter';
import { billDetailCols } from './config';
import { timeMonth } from 'constants/strategy';
import { IBillDetail } from 'types/base-type';
import Url from 'lib/url-parser';
import * as XLSX from 'xlsx';
import moment from 'moment';

const { TabPane } = Tabs;

@observer
export class BillDetail extends SearchAndFilterContainer {
  public state = {
    searchKey: '',
  };

  private timestamp: number = null;
  constructor(props: any) {
    super(props);
    const url = Url();
    this.timestamp = Number(url.search.timestamp);
  }

  public componentDidMount() {
    bill.getDetailList(this.timestamp);
  }

  public getData<T extends IBillDetail>(origin: T[]) {
    let data: T[] = origin;
    let { searchKey } = this.state;
    searchKey = (searchKey + '').trim().toLowerCase();

    if (searchKey) {
      data = origin.filter((d) =>
        (d.topicName !== undefined && d.topicName !== null) && d.topicName.toLowerCase().includes(searchKey as string));
    }

    return data;
  }

  public handleDownLoad() {
    const tableData = bill.billDetailData.map(item => {
      return {
        // tslint:disable
        '集群ID': item.clusterId,
        '集群名称': item.clusterName,
        'quota数量': item.quota,
        'Topic名称': item.topicName,
        '金额': item.cost,
      };
    });
    const data = [].concat(tableData);
    const wb = XLSX.utils.book_new();
    // json转sheet
    const ws = XLSX.utils.json_to_sheet(data, {
      header: ['集群ID', '集群名称', 'quota数量', 'Topic名称', '金额'],
    });
    // XLSX.utils.
    XLSX.utils.book_append_sheet(wb, ws, 'bill');
    // 输出
    XLSX.writeFile(wb, 'bill-' + moment(this.timestamp).format(timeMonth) + '.xlsx');
  }

  public renderTableList() {
    return (
      <Spin spinning={bill.loading}>
        <Table
          rowKey="key"
          columns={billDetailCols}
          dataSource={this.getData(bill.billDetailData)}
          pagination={pagination}
        />
      </Spin>
    );
  }

  public render() {
    return (
      <>
        <div className="container">
          <Tabs defaultActiveKey="1" type="card">
            <TabPane tab={`账单详情-${moment(this.timestamp).format(timeMonth)}`} key="1">
              {this.renderTableList()}
            </TabPane>
          </Tabs>
          <div className="operation-panel special">
            <ul>
              {this.renderSearch('', '请输入TopicName')}
              <li className="right-btn-1">
                <Icon type="download" onClick={this.handleDownLoad.bind(this, null)} />
              </li>
            </ul>
          </div>
        </div>
      </>
    );
  }
}
