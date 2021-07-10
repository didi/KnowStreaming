import * as React from 'react';
import { wrapper, region } from 'store';
import './index.less';
import { topic, IConsumerGroups, IConsumeDetails } from 'store/topic';
import { observer } from 'mobx-react';
import { SearchAndFilterContainer } from 'container/search-filter';
import Url from 'lib/url-parser';
import { pagination } from 'constants/table';
import { IXFormWrapper, IOffset } from 'types/base-type';
import { Table, Button, Tooltip } from 'component/antd';
import ResetOffset from './reset-offset';
import { urlPrefix } from 'constants/left-menu';
import { cellStyle } from 'constants/table';
import './index.less';

@observer
export class GroupID extends SearchAndFilterContainer {

  public static getDerivedStateFromProps(nextProps: any, prevState: any) {
    const url = Url();
    return {
      ...prevState,
      isDetailPage: url.search.consumerGroup && url.search.location,
    };
  }
  public clusterId: number;
  public topicName: string;
  public consumerGroup: string;
  public location: string;
  public isPhysicalTrue: string;

  public state = {
    searchKey: '',
    updateRender: false,
    isDetailPage: false,
  };

  private xFormWrapper: IXFormWrapper;

  constructor(props: any) {
    super(props);
    this.handleUrlSearch();
  }

  public componentDidMount() {
    if (!topic.showConsumeDetail) {
      return topic.getConsumerGroups(this.clusterId, this.topicName);
    }
    return topic.getConsumeDetails(this.clusterId, this.topicName, this.consumerGroup, this.location);
  }

  public componentDidUpdate(prevProps: any, prevState: any) {
    if (prevState.isDetailPage !== this.state.isDetailPage) {
      this.handleUrlSearch();
      if (!topic.showConsumeDetail && !topic.consumerGroups.length) {
        topic.getConsumerGroups(this.clusterId, this.topicName);
      }
    }
  }

  public handleUrlSearch = () => {
    const url = Url();
    this.clusterId = Number(url.search.clusterId);
    this.topicName = url.search.topic;
    const isPhysical = Url().search.hasOwnProperty('isPhysicalClusterId');
    this.isPhysicalTrue = isPhysical ? '&isPhysicalClusterId=true' : '';
    topic.setConsumeDetail(false);
    if (url.search.consumerGroup && url.search.location) {
      topic.setConsumeDetail(true);
      this.consumerGroup = url.search.consumerGroup;
      this.location = url.search.location;
    }
  }

  public showConsumeDetail = (record: IConsumerGroups) => {
    // tslint:disable-next-line:max-line-length
    const url = `${urlPrefix}/topic/topic-detail?clusterId=${this.clusterId}&topic=${this.topicName}&consumerGroup=${record.consumerGroup}&location=${record.location}${this.isPhysicalTrue}&region=${region.currentRegion}#4`;
    history.pushState({ url }, '', url);
    this.handleUrlSearch();
    topic.setConsumeDetail(true);
    topic.getConsumeDetails(this.clusterId, this.topicName, record.consumerGroup, record.location);
    this.setState({
      updateRender: !this.state.updateRender,
    });
  }

  public updateDetailsStatus = () => {
    topic.getConsumeDetails(this.clusterId,
      this.topicName, this.consumerGroup, this.location);
  }

  public backToPage = () => {
    // tslint:disable-next-line:max-line-length
    const url = `${urlPrefix}/topic/topic-detail?clusterId=${this.clusterId}&topic=${this.topicName}${this.isPhysicalTrue}&region=${region.currentRegion}#4`;
    history.pushState({ url }, '', url);
    topic.setConsumeDetail(false);
    topic.getConsumerGroups(this.clusterId, this.topicName);
    this.setState({
      updateRender: !this.state.updateRender,
    });
  }

  public showResetOffset() {
    this.xFormWrapper = {
      type: 'drawer',
      formMap: [
      ],
      formData: {
      },
      visible: true,
      width: 600,
      title: '重置消费偏移',
      customRenderElement: this.renderDrawerInfo(),
      noform: true,
      nofooter: true,
      onSubmit: (value: any) => {
        //
      },
    };
    wrapper.open(this.xFormWrapper);
  }

  public renderDrawerInfo() {
    const OffsetReset = {
      clusterId: this.clusterId,
      topicName: this.topicName,
      consumerGroup: this.consumerGroup,
      location: this.location,
      offsetList: [],
      timestamp: 0,
    } as IOffset;
    return (
      <div>
        <ResetOffset OffsetReset={OffsetReset} />
      </div>
    );
  }

  public renderConsumerDetails() {
    const consumerGroup = this.consumerGroup;
    const columns: any = [{
      title: 'Partition ID',
      dataIndex: 'partitionId',
      key: 'partitionId',
      width: '10%',
      sorter: (a: IConsumeDetails, b: IConsumeDetails) => +b.partitionId - +a.partitionId,
    }, {
      title: 'Consumer ID',
      dataIndex: 'clientId',
      key: 'clientId',
      width: '40%',
      onCell: () => ({
        style: {
          maxWidth: 300,
          ...cellStyle,
        },
      }),
      render: (t: IConsumeDetails) => <Tooltip placement="bottomLeft" title={t}> {t} </Tooltip>,
    }, {
      title: 'Consume Offset',
      dataIndex: 'consumeOffset',
      key: 'consumeOffset',
      width: '20%',
      sorter: (a: IConsumeDetails, b: IConsumeDetails) => +b.consumeOffset - +a.consumeOffset,
    }, {
      title: 'Partition Offset',
      dataIndex: 'partitionOffset',
      key: 'partitionOffset',
      width: '20%',
      sorter: (a: IConsumeDetails, b: IConsumeDetails) => +b.partitionOffset - +a.partitionOffset,
    }, {
      title: 'Lag',
      dataIndex: 'lag',
      key: 'lag',
      width: '10%',
      sorter: (a: IConsumeDetails, b: IConsumeDetails) => +b.lag - +a.lag,
    }];
    return (
      <>
        <div className="details-box">
          <b>{consumerGroup}</b>
          <div style={{ display: 'flex' }}>
            {this.renderSearch('', '请输入Consumer ID')}
            <Button onClick={this.backToPage}>返回</Button>
            <Button onClick={this.updateDetailsStatus}>刷新</Button>
            <Button onClick={() => this.showResetOffset()}>重置Offset</Button>
          </div>
        </div>
        <Table
          columns={columns}
          dataSource={this.getDetailData(topic.consumeDetails)}
          rowKey="key"
          pagination={pagination}
        />
      </>
    );
  }

  public renderConsumerTable(consumerData: IConsumerGroups[]) {
    const columns = [{
      title: '消费组名称',
      dataIndex: 'consumerGroup',
      key: 'consumerGroup',
      width: '36%',
      render: (t: string, r: IConsumerGroups) => (
        <> <a onClick={() => this.showConsumeDetail(r)}>{t}</a> </>
      ),
    }, {
      title: 'AppIds',
      dataIndex: 'appIds',
      key: 'appIds',
      width: '36%',
    }, {
      title: 'Location',
      dataIndex: 'location',
      key: 'location',
      width: '34%',
    }, {
      title: '状态',
      dataIndex: 'state',
      key: 'state',
      width: '34%',
    }
    ];
    return (
      <>
        <Table
          columns={columns}
          dataSource={consumerData}
          pagination={pagination}
          rowKey="key"
          scroll={{ y: 400 }}
        />
      </>
    );
  }

  public getData<T extends IConsumerGroups>(origin: T[]) {
    let data: T[] = origin;
    let { searchKey } = this.state;
    searchKey = (searchKey + '').trim().toLowerCase();

    data = searchKey ? origin.filter((item: IConsumerGroups) =>
      (item.consumerGroup !== undefined && item.consumerGroup !== null) && item.consumerGroup.toLowerCase().includes(searchKey as string),
    ) : origin;
    return data;
  }

  public getDetailData<T extends IConsumeDetails>(origin: T[]) {
    let data: T[] = origin;
    let { searchKey } = this.state;
    searchKey = (searchKey + '').trim().toLowerCase();
    data = searchKey ? origin.filter((item: IConsumeDetails) =>
      (item.clientId !== undefined && item.clientId !== null) && item.clientId.toLowerCase().includes(searchKey as string),
    ) : origin;
    return data;
  }

  public renderConsumerGroups() {
    return (
      <>
        <div className="k-row" >
          <ul className="k-tab">
            <li>消费组信息</li>
            {this.renderSearch('', '请输入消费组名称')}
          </ul>
          {this.renderConsumerTable(this.getData(topic.consumerGroups))}
        </div>
      </>
    );
  }

  public render() {
    return (
      <>
        {topic.showConsumeDetail ? this.renderConsumerDetails() : this.renderConsumerGroups()}
      </>
    );
  }
}
