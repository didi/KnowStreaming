import * as React from 'react';
import { Modal, Table, Button, notification, message, Tooltip, Icon, Popconfirm, Alert, Dropdown } from 'component/antd';
import { wrapper } from 'store';
import { observer } from 'mobx-react';
import { IXFormWrapper, IMetaData, IRegister, ILabelValue } from 'types/base-type';
import { admin } from 'store/admin';
import { users } from 'store/users';
import { registerCluster, createCluster, pauseMonitoring } from 'lib/api';
import { SearchAndFilterContainer } from 'container/search-filter';
import { cluster } from 'store/cluster';
import { customPagination } from 'constants/table';
import { urlPrefix } from 'constants/left-menu';
import { indexUrl } from 'constants/strategy';
import { region } from 'store';
import './index.less';
import { getAdminClusterColumns } from '../config';
import { FormItemType } from 'component/x-form';
import { TopicHaRelationWrapper } from 'container/modal/admin/TopicHaRelation';
import { TopicSwitchWrapper } from 'container/modal/admin/TopicHaSwitch';
import { TopicSwitchLog } from 'container/modal/admin/SwitchTaskLog';

const { confirm } = Modal;

@observer
export class ClusterList extends SearchAndFilterContainer {
  public state = {
    searchKey: '',
    haVisible: false,
    switchVisible: false,
    logVisible: false,
    currentCluster: {} as IMetaData,
  };

  private xFormModal: IXFormWrapper;

  // TODO: 公共化
  public renderClusterHref(value: number | string, item: IMetaData, key: number) {
    return ( // 0 暂停监控--不可点击  1 监控中---可正常点击
      <>
        {item.status === 1 ? <a href={`${urlPrefix}/admin/cluster-detail?clusterId=${item.clusterId}#${key}`}>{value}</a>
          : <a style={{ cursor: 'not-allowed', color: '#999' }}>{value}</a>}
      </>
    );
  }

  public updateFormModal(value: boolean, metaList: ILabelValue[]) {
    const formMap = wrapper.xFormWrapper.formMap;
    formMap[1].attrs.prompttype = !value ? '' : metaList.length ? '已设置为高可用集群，请选择所关联的主集群' : '当前暂无可用集群进行关联高可用关系，请先添加集群';
    formMap[1].attrs.prompticon = 'true';
    formMap[2].invisible = !value;
    formMap[2].attrs.disabled = !metaList.length;
    formMap[6].rules[0].required = value;

    // tslint:disable-next-line:no-unused-expression
    wrapper.ref && wrapper.ref.updateFormMap$(formMap, wrapper.xFormWrapper.formData);

  }

  public createOrRegisterCluster(item: IMetaData) {
    const self = this;
    const metaList = Array.from(admin.metaList).filter(item => item.haRelation === null).map(item => ({
      label: item.clusterName,
      value: item.clusterId,
    }));

    this.xFormModal = {
      formMap: [
        {
          key: 'clusterName',
          label: '集群名称',
          rules: [{
            required: true,
            message: '请输入集群名称',
          }],
          attrs: {
            placeholder: '请输入集群名称',
            disabled: item ? true : false,
          },
        },
        {
          key: 'ha',
          label: '高可用',
          type: FormItemType._switch,
          invisible: item ? true : false,
          rules: [{
            required: false,
          }],
          attrs: {
            className: 'switch-style',
            prompttype: '',
            prompticon: '',
            prompticomclass: '',
            promptclass: 'inline',
            onChange(value: boolean) {
              self.updateFormModal(value, metaList);
            },
          },
        },
        {
          key: 'activeClusterId',
          label: '主集群',
          type: FormItemType.select,
          options: metaList,
          invisible: true,
          rules: [{
            required: false,
          }],
          attrs: {
            placeholder: '请选择主集群',
          },
        },
        {
          key: 'zookeeper',
          label: 'zookeeper地址',
          type: 'text_area',
          rules: [{
            required: true,
            message: '请输入zookeeper地址',
          }],
          attrs: {
            placeholder: '请输入zookeeper地址，例如：192.168.0.1:2181,192.168.0.2:2181/logi-kafka',
            rows: 2,
            disabled: item ? true : false,
          },
        },
        {
          key: 'bootstrapServers',
          label: 'bootstrapServers',
          type: 'text_area',
          rules: [{
            required: true,
            message: '请输入bootstrapServers',
          }],
          attrs: {
            placeholder: '请输入bootstrapServers，例如：192.168.1.1:9092,192.168.1.2:9092',
            rows: 2,
            disabled: item ? true : false,
          },
        },
        // {
        //   key: 'idc',
        //   label: '数据中心',
        //   defaultValue: region.regionName,
        //   rules: [{ required: true, message: '请输入数据中心' }],
        //   attrs: {
        //     placeholder: '请输入数据中心',
        //     disabled: true,
        //   },
        // },
        // {
        //   key: 'mode',
        //   label: '集群类型',
        //   type: 'select',
        //   options: cluster.clusterModes.map(ele => {
        //     return {
        //       label: ele.message,
        //       value: ele.code,
        //     };
        //   }),
        //   rules: [{
        //     required: true,
        //     message: '请选择集群类型',
        //   }],
        //   attrs: {
        //     placeholder: '请选择集群类型',
        //   },
        // },
        {
          key: 'kafkaVersion',
          label: 'kafka版本',
          invisible: item ? false : true,
          rules: [{
            required: false,
            message: '请输入kafka版本',
          }],
          attrs: {
            placeholder: '请输入kafka版本',
            disabled: true,
          },
        },
        {
          key: 'securityProperties',
          label: '安全协议',
          type: 'text_area',
          rules: [{
            required: false,
            message: '请输入安全协议',
          }],
          attrs: {
            placeholder: `请输入安全协议，例如：
{
  "security.protocol": "SASL_PLAINTEXT",
  "sasl.mechanism": "PLAIN",
  "sasl.jaas.config": "org.apache.kafka.common.security.plain.PlainLoginModule required username=\\"xxxxxx\\" password=\\"xxxxxx\\";"
}`,
            rows: 8,
          },
        },
        {
          key: 'jmxProperties',
          label: 'JMX认证',
          type: 'text_area',
          rules: [{
            required: false,
            message: '请输入JMX认证',
          }],
          attrs: {
            placeholder: `请输入JMX认证，例如：
{
"maxConn": 10, #KM对单台Broker的最大jmx连接数
"username": "xxxxx", #用户名
"password": "xxxxx", #密码
"openSSL": true, #开启SSL，true表示开启SSL，false表示关闭
}`,
            rows: 8,
          },
        },
      ],
      formData: item ? item : {},
      visible: true,
      width: 590,
      title: item ? '编辑' : '接入集群',
      isWaitting: true,
      onSubmit: (value: IRegister) => {
        value.idc = region.currentRegion;
        if (item) {
          value.clusterId = item.clusterId;
          return registerCluster(value).then(data => {
            admin.getHaMetaData();
            notification.success({ message: '编辑集群成功' });
          });
        } else {
          return createCluster(value).then(data => {
            admin.getHaMetaData();
            notification.success({ message: '接入集群成功' });
          });
        }

      },
    };
    wrapper.open(this.xFormModal);
  }

  public pauseMonitor(item: IMetaData) {
    const info = item.status === 1 ? '暂停监控' : '开始监控';
    const status = item.status === 1 ? 0 : 1;
    pauseMonitoring(item.clusterId, status).then(data => {
      admin.getHaMetaData();
      notification.success({ message: `${info}成功` });
    });
  }

  public showMonitor = (record: IMetaData) => {
    admin.getBrokersRegions(record.clusterId).then((data) => {
      confirm({
        // tslint:disable-next-line:jsx-wrap-multiline
        title: <>
          <span className="offline_span">
            删除集群&nbsp;
            <a>
              <Tooltip placement="right" title={'若当前集群存在逻辑集群，则无法删除'} >
                <Icon type="question-circle" />
              </Tooltip>
            </a>
          </span>
        </>,
        icon: 'none',
        content: this.deleteMonitorModal(data),
        width: 500,
        okText: '确认',
        cancelText: '取消',
        onOk() {
          if (data.length) {
            return message.warning('存在逻辑集群，无法删除！');
          }
          admin.deleteCluster(record.clusterId).then(data => {
            notification.success({ message: '删除成功' });
            admin.getHaMetaData();
          });
        },
      });
    });
  }

  public showDelStandModal = (record: IMetaData) => {
    confirm({
      // tslint:disable-next-line:jsx-wrap-multiline
      title: '删除集群',
      // icon: 'none',
      content: <>{record.activeTopicCount ? `当前集群含有主topic，无法删除！` : record.haStatus !== 0 ? `当前集群正在进行主备切换，无法删除！` : `确认删除集群${record.clusterName}吗?`}</>,
      width: 500,
      okText: '确认',
      cancelText: '取消',
      onOk() {
        if (record.activeTopicCount || record.haStatus !== 0) {
          return;
        }
        admin.deleteCluster(record.clusterId).then(data => {
          notification.success({ message: '删除成功' });
          admin.getHaMetaData();
        });
      },
    });
  }

  public deleteMonitorModal = (source: any) => {
    const cellStyle = {
      overflow: 'hidden',
      whiteSpace: 'nowrap',
      textOverflow: 'ellipsis',
      cursor: 'pointer',
    };
    const monitorColumns = [
      {
        title: '逻辑集群列表',
        dataIndex: 'name',
        key: 'name',
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
    ];
    return (
      <>
        <div className="render_offline">
          <Table
            rowKey="key"
            dataSource={source}
            columns={monitorColumns}
            scroll={{ x: 300, y: 200 }}
            pagination={false}
            bordered={true}
          />
          <Alert message="若当前集群存在逻辑集群，则无法删除" type="error" showIcon={true} />
        </div>
      </>
    );
  }

  public getData<T extends IMetaData>(origin: T[]) {
    let data: T[] = origin;
    let { searchKey } = this.state;
    searchKey = (searchKey + '').trim().toLowerCase();

    data = searchKey ? origin.filter((item: IMetaData) =>
      (item.clusterName !== undefined && item.clusterName !== null) && item.clusterName.toLowerCase().includes(searchKey as string),
    ) : origin;
    return data;
  }

  public expandedRowRender = (record: IMetaData) => {
    const dataSource: any = record.haClusterVO ? [record.haClusterVO] : [];
    const cols = getAdminClusterColumns(false);
    const role = users.currentUser.role;

    if (!record.haClusterVO) return null;

    const haRecord = record.haClusterVO;

    const btnsMenu = (
      <>
        <ul className="dropdown-menu">
          <li>
            <a onClick={this.createOrRegisterCluster.bind(this, haRecord)} className="action-button">
              编辑
            </a>
          </li>
          <li>
            <Popconfirm
              title={`确定${haRecord.status === 1 ? '暂停' : '开始'}${haRecord.clusterName}监控？`}
              onConfirm={() => this.pauseMonitor(haRecord)}
              cancelText="取消"
              okText="确认"
            >
              <Tooltip placement="left" title="暂停监控将无法正常监控指标信息，建议开启监控">
                <a
                  className="action-button"
                >
                  {haRecord.status === 1 ? '暂停监控' : '开始监控'}
                </a>
              </Tooltip>
            </Popconfirm>
          </li>
          <li>
            <a onClick={this.showDelStandModal.bind(this, haRecord)}>
              删除
            </a>
          </li>
        </ul>
      </>);

    const noAuthMenu = (
      <ul className="dropdown-menu">
        <Tooltip placement="left" title="该功能只对运维人员开放">
          <li><a style={{ color: '#a0a0a0' }} className="action-button">编辑</a></li>
          <li><a className="action-button" style={{ color: '#a0a0a0' }}>{record.status === 1 ? '暂停监控' : '开始监控'}</a></li>
          <li><a style={{ color: '#a0a0a0' }}>删除</a></li>
        </Tooltip>
      </ul>
    );

    const col = {
      title: '操作',
      width: 270,
      render: (value: string, item: IMetaData) => (
        <>
          <a
            onClick={this.openModal.bind(this, 'haVisible', record)}
            className="action-button"
          >
            Topic高可用关联
          </a>
          {item.haStatus !== 0 ? null : <a onClick={this.openModal.bind(this, 'switchVisible', record)} className="action-button">
            Topic主备切换
          </a>}
          {item.haASSwitchJobId ? <a className="action-button" onClick={this.openModal.bind(this, 'logVisible', record)}>
            查看日志
          </a> : null}
          <Dropdown
            overlay={role === 2 ? btnsMenu : noAuthMenu}
            trigger={['click', 'hover']}
            placement="bottomLeft"
          >
            <span className="didi-theme ml-10">
              ···
            </span>
          </Dropdown>
        </>
      ),
    };
    cols.push(col as any);
    return (
      <Table
        className="expanded-table"
        rowKey="clusterId"
        style={{ width: '500px' }}
        columns={cols}
        dataSource={dataSource}
        pagination={false}
      />
    );
  }

  public getColumns = () => {
    const cols = getAdminClusterColumns();
    const role = users.currentUser.role;
    const col = {
      title: '操作',
      width: 270,
      render: (value: string, item: IMetaData) => (
        <>
          {
            role && role === 2 ? <>
              <a
                onClick={this.createOrRegisterCluster.bind(this, item)}
                className="action-button"
              >编辑
              </a>
              <Popconfirm
                title={`确定${item.status === 1 ? '暂停' : '开始'}${item.clusterName}监控？`}
                onConfirm={() => this.pauseMonitor(item)}
                cancelText="取消"
                okText="确认"
              >
                <Tooltip placement="bottom" title="暂停监控将无法正常监控指标信息，建议开启监控">
                  <a
                    className="action-button"
                  >
                    {item.status === 1 ? '暂停监控' : '开始监控'}
                  </a>
                </Tooltip>
              </Popconfirm>
              <a onClick={this.showMonitor.bind(this, item)}>
                删除
              </a>
            </> : <Tooltip placement="left" title="该功能只对运维人员开放">
              <a style={{ color: '#a0a0a0' }} className="action-button">编辑</a>
              <a className="action-button" style={{ color: '#a0a0a0' }}>{item.status === 1 ? '暂停监控' : '开始监控'}</a>
              <a style={{ color: '#a0a0a0' }}>删除</a>
            </Tooltip>
          }
        </>
      ),
    };
    cols.push(col as any);
    return cols;
  }

  public openModal(type: string, record: IMetaData) {
    this.setState({
      currentCluster: record,
    }, () => {
      this.handleVisible(type, true);
    });
  }

  public handleVisible(type: string, visible: boolean) {
    this.setState({
      [type]: visible,
    });
  }

  public renderClusterList() {
    const role = users.currentUser.role;
    return (
      <>
        <div className="container">
          <div className="table-operation-panel">
            <ul>
              {this.renderSearch('', '请输入集群名称')}
              <li className="right-btn-1">
                <a style={{ display: 'inline-block', marginRight: '20px' }} href={indexUrl.cagUrl} target="_blank">集群接入指南</a>
                {
                  role && role === 2 ?
                    <Button type="primary" onClick={this.createOrRegisterCluster.bind(this, null)}>接入集群</Button>
                    :
                    <Tooltip placement="left" title="该功能只对运维人员开放" trigger="hover">
                      <Button disabled={true} type="primary">接入集群</Button>
                    </Tooltip>
                }
              </li>
            </ul>
          </div>
          <div className="table-wrapper">
            <Table
              rowKey="key"
              expandIcon={({ expanded, onExpand, record }) => (
                record.haClusterVO ?
                  <Icon style={{ fontSize: 10 }} type={expanded ? 'down' : 'right'} onClick={e => onExpand(record, e)} />
                  : null
              )}
              loading={admin.loading}
              expandedRowRender={this.expandedRowRender}
              dataSource={this.getData(admin.haMetaList)}
              columns={this.getColumns()}
              pagination={customPagination}
            />
          </div>
        </div>
        {this.state.haVisible && <TopicHaRelationWrapper
          handleVisible={(val: boolean) => this.handleVisible('haVisible', val)}
          visible={this.state.haVisible}
          currentCluster={this.state.currentCluster}
          reload={() => admin.getHaMetaData()}
          formData={{}}
        />}
        {this.state.switchVisible &&
          <TopicSwitchWrapper
            reload={(jobId: number) => {
              admin.getHaMetaData().then((res) => {
                const currentRecord = res.find(item => item.clusterId === this.state.currentCluster.clusterId);
                currentRecord.haClusterVO.haASSwitchJobId = jobId;
                this.openModal('logVisible', currentRecord);
              });
            }}
            handleVisible={(val: boolean) => this.handleVisible('switchVisible', val)}
            visible={this.state.switchVisible}
            currentCluster={this.state.currentCluster}
            formData={{}}
          />
        }
        {this.state.logVisible &&
          <TopicSwitchLog
            reload={() => admin.getHaMetaData()}
            handleVisible={(val: boolean) => this.handleVisible('logVisible', val)}
            visible={this.state.logVisible}
            currentCluster={this.state.currentCluster}
          />
        }
      </>
    );
  }

  public componentDidMount() {
    admin.getMetaData(true);
    admin.getHaMetaData();
    cluster.getClusterModes();
    admin.getDataCenter();
  }

  public render() {
    return (
      admin.haMetaList ? <> {this.renderClusterList()} </> : null
    );
  }
}
