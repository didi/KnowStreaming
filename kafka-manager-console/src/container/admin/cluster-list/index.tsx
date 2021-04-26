import * as React from 'react';
import { Modal, Table, Button, notification, message, Tooltip, Icon, Popconfirm, Alert, Popover } from 'component/antd';
import { wrapper } from 'store';
import { observer } from 'mobx-react';
import { IXFormWrapper, IMetaData, IRegister } from 'types/base-type';
import { admin } from 'store/admin';
import { users } from 'store/users';
import { registerCluster, createCluster, pauseMonitoring } from 'lib/api';
import { SearchAndFilterContainer } from 'container/search-filter';
import { cluster } from 'store/cluster';
import { customPagination } from 'constants/table';
import { urlPrefix } from 'constants/left-menu';
import { indexUrl } from 'constants/strategy'
import { region } from 'store';
import './index.less';
import Monacoeditor from 'component/editor/monacoEditor';
import { getAdminClusterColumns } from '../config';

const { confirm } = Modal;

@observer
export class ClusterList extends SearchAndFilterContainer {
  public state = {
    searchKey: '',
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

  public createOrRegisterCluster(item: IMetaData) {
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
      onSubmit: (value: IRegister) => {
        value.idc = region.currentRegion;
        if (item) {
          value.clusterId = item.clusterId;
          registerCluster(value).then(data => {
            admin.getMetaData(true);
            notification.success({ message: '编辑集群成功' });
          });
        } else {
          createCluster(value).then(data => {
            admin.getMetaData(true);
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
      admin.getMetaData(true);
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
          });
        },
      });
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

  public getColumns = () => {
    const cols = getAdminClusterColumns();
    const role = users.currentUser.role;
    const col = {
      title: '操作',
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
                <Tooltip placement="left" title="暂停监控将无法正常监控指标信息，建议开启监控">
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
                    <Tooltip placement="left" title="该功能只对运维人员开放" trigger='hover'>
                      <Button disabled type="primary">接入集群</Button>
                    </Tooltip>
                }
              </li>
            </ul>
          </div>
          <div className="table-wrapper">
            <Table
              rowKey="key"
              loading={admin.loading}
              dataSource={this.getData(admin.metaList)}
              columns={this.getColumns()}
              pagination={customPagination}
            />
          </div>
        </div>
      </>
    );
  }

  public componentDidMount() {
    admin.getMetaData(true);
    cluster.getClusterModes();
    admin.getDataCenter();
  }

  public render() {
    return (
      admin.metaList ? <> {this.renderClusterList()} </> : null
    );
  }
}
