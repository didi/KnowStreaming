import * as React from 'react';
import './index.less';
import { Tabs, Table, Button, Tooltip } from 'component/antd';
import { SearchAndFilterContainer } from 'container/search-filter';
import { expert } from 'store/expert';
import { handleTabKey } from 'lib/utils';
import { observer } from 'mobx-react';
import { IDetailData, IHotTopics } from 'types/base-type';
import { pagination } from 'constants/table';
import { IRenderData } from 'container/modal/expert';
import { migrationModal } from 'container/modal/expert';

import './index.less';
import { region } from 'store/region';
import { MigrationTask } from 'container/admin/operation-management/migration-task';

const { TabPane } = Tabs;

@observer
export class HotSpotTopic extends SearchAndFilterContainer {

  public selectedData: IHotTopics[];

  public state = {
    loading: false,
    hasSelected: false,
    migrationVisible: false,
    searchKey: '',
  };

  public onSelectChange = {
    onChange: (selectedRowKeys: string[], selectedRows: IHotTopics[]) => {
      this.selectedData = selectedRows ? selectedRows : [];
      this.setState({
        hasSelected: !!selectedRowKeys.length,
      });
    },
  };

  public getData(origin: IHotTopics[]) {
    let data: IHotTopics[] = [];
    const { searchKey } = this.state;
    if (expert.active !== -1 || searchKey !== '') {
      data = origin.filter(d =>
        ((d.topicName !== undefined && d.topicName !== null) && d.topicName.toLowerCase().includes(searchKey.toLowerCase()))
        && (expert.active === -1 || +d.clusterId === expert.active),
      );
    } else {
      data = origin;
    }
    return data;
  }

  public zoningHotspots(hotData: IHotTopics[]) {
    const { loading, hasSelected } = this.state;
    const columns = [
      {
        title: 'Topic名称',
        dataIndex: 'topicName',
        width: '30%',
        sorter: (a: IHotTopics, b: IHotTopics) => a.topicName.charCodeAt(0) - b.topicName.charCodeAt(0),
        render: (text: string, item: IHotTopics) => (
          <Tooltip placement="bottomLeft" title={text}>
            <a
              // tslint:disable-next-line:max-line-length
              href={`${this.urlPrefix}/topic/topic-detail?clusterId=${item.clusterId}&topic=${item.topicName}&isPhysicalClusterId=true&region=${region.currentRegion}`}
            >{text}
            </a>
          </Tooltip>),
      },
      {
        title: '所在集群',
        dataIndex: 'clusterName',
        width: '30%',
      },
      {
        title: '分区热点状态',
        dataIndex: 'detailList',
        width: '30%',
        render: (detailList: IDetailData[], item: any, index: number) => (
          <>
            <Tooltip
              placement="rightTop"
              title={() => this.ReactNode(detailList)}
            >
              <span>查看状态</span>
            </Tooltip>
          </>
        ),
      },
      {
        title: '操作',
        dataIndex: 'action',
        width: '10%',
        render: (value: any, item: IHotTopics) => (
          <span onClick={this.dataMigration.bind(this, item)}><a>数据迁移</a></span>
        ),
      },
    ];
    return (
      <>
        <div className="table-operation-panel">
          <ul>
            {this.renderPhysical('物理集群：')}
            {this.renderSearch('名称：', '请输入Topic名称')}
          </ul>
        </div>
        <Table
          rowKey="key"
          rowSelection={this.onSelectChange}
          columns={columns}
          dataSource={hotData}
          pagination={pagination}
        />
        <div className="zoning-button">
          <Button onClick={() => this.dataMigration()} disabled={!hasSelected} loading={loading}>
            批量数据迁移
          </Button>
        </div>
      </>
    );
  }

  public ReactNode(detailList: IDetailData[]) {
    return (
      <ul>
        {detailList.map((record: IDetailData) => (
          <li>broker{record.brokeId}:{record.partitionNum}个分区</li>
        ))}
      </ul>
    );
  }

  public dataMigration(item?: IHotTopics) {
    let migrateData = [] as IHotTopics[];
    const renderData = [] as IRenderData[];
    if (item) {
      migrateData.push(item);
    } else {
      migrateData = this.selectedData;
    }
    migrateData.forEach((ele, index) => {
      const brokerId = [] as number[];
      ele.detailList.forEach(t => {
        brokerId.push(t.brokeId);
      });
      const item = {
        brokerIdList: brokerId,
        partitionIdList: [],
        topicName: ele.topicName,
        clusterId: ele.clusterId,
        clusterName: ele.clusterName,
        retentionTime: ele.retentionTime,
        key: index,
      } as IRenderData;
      renderData.push(item);
    });
    this.migrationInterface(renderData);
  }

  public migrationInterface(renderData: IRenderData[]) {
    migrationModal(renderData);
  }

  public componentDidMount() {
    expert.getHotTopics();
    if (!expert.metaData.length) {
      expert.getMetaData(false);
    }
  }

  public render() {
    return (
      <>
        <Tabs activeKey={location.hash.substr(1) || '1'} type="card" onChange={handleTabKey}>
          <TabPane tab="分区热点Topic" key="1">
            {this.zoningHotspots(this.getData(expert.hotTopics))}
          </TabPane>
          <TabPane tab="迁移任务" key="2">
            <MigrationTask />
          </TabPane>
        </Tabs>
      </>
    );
  }
}
