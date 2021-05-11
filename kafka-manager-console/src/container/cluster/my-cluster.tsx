import * as React from 'react';
import './index.less';
import { wrapper } from 'store';
import { users } from 'store/users';
import {
  Table,
  Button,
  notification,
  Radio,
  RadioChangeEvent,
  InputNumber,
} from 'component/antd';
import { observer } from 'mobx-react';
import { pagination } from 'constants/table';
import { urlPrefix } from 'constants/left-menu';
import { getClusterColumns } from './config';
import { app } from 'store/app';
import { cluster } from 'store/cluster';
import { SearchAndFilterContainer } from 'container/search-filter';
import { IOrderParams, IClusterData, IRadioItem } from 'types/base-type';
import { region } from 'store';

@observer
export class MyCluster extends SearchAndFilterContainer {
  public state = {
    searchKey: '',
  };

  public applyCluster() {
    const xFormModal = {
      formMap: [
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
        {
          key: 'appId',
          label: '所属应用',
          rules: [{ required: true, message: '请选择所属应用' }],
          type: 'select',
          options: app.data.map((item) => {
            return {
              label: item.name,
              value: item.appId,
            };
          }),
          attrs: {
            placeholder: '请选择所属应用',
          },
        },
        {
          key: 'mode',
          label: '集群类型',
          type: 'radio_group',
          options: cluster.clusterMode,
          rules: [{ required: true, message: '请选择' }],
          attrs: {
            placeholder: '请选择集群',
          },
        },
        {
          key: 'bytesIn',
          label: '峰值流量',
          type: 'custom',
          rules: [{ required: true, message: '请选择' }],
          customFormItem: <RadioAndInput />,
        },
        {
          key: 'description',
          label: '申请原因',
          type: 'text_area',
          rules: [
            {
              required: true,
              pattern: /^.{4,}.$/,
              message: '请输入至少5个字符',
            },
          ],
          attrs: {
            placeholder: `请大致说明集群申请的原因、用途，对稳定性SLA的要求等。
例如:
原因：xxx, 用途：xxx, 稳定性：xxx`,
          },
        },
      ],
      formData: {},
      visible: true,
      title: <div><span>申请集群</span><a className='applicationDocument' href="https://github.com/didi/Logi-KafkaManager/blob/master/docs/user_guide/resource_apply.md" target='_blank'>资源申请文档</a></div>,
      okText: '确认',
      onSubmit: (value: any) => {
        value.idc = region.currentRegion;
        const params = JSON.parse(JSON.stringify(value));
        delete params.description;
        if (typeof params.bytesIn === 'number') {
          params.bytesIn = params.bytesIn * 1024 * 1024;
        }
        const clusterParams: IOrderParams = {
          type: 4,
          applicant: users.currentUser.username,
          description: value.description,
          extensions: JSON.stringify(params),
        };
        cluster.applyCluster(clusterParams).then((data) => {
          notification.success({
            message: '申请集群成功',
            // description: this.aHrefUrl(data.id),
          });
          window.location.href = `${urlPrefix}/user/order-detail/?orderId=${data.id}&region=${region.currentRegion}`;
        });
      },
    };
    wrapper.open(xFormModal);
  }

  public aHrefUrl(id: number) {
    return (
      <>
        <a href={urlPrefix + '/user/order-detail/?orderId=' + id}>
          是否跳转到集群审批页？
        </a>
      </>
    );
  }

  public componentDidMount() {
    if (!cluster.clusterData.length) {
      cluster.getClusters();
    }
    if (!cluster.clusterModes.length) {
      cluster.getClusterModes();
    }
    if (!app.data.length) {
      app.getAppList();
    }
  }

  public renderOperationPanel() {
    return (
      <ul>
        {this.renderSearch('', '请输入集群名称')}
        <li className="right-btn-1">
          <Button type="primary" onClick={() => this.applyCluster()}>
            申请集群
          </Button>
        </li>
      </ul>
    );
  }

  public getData<T extends IClusterData>(origin: T[]) {
    let data: T[] = origin;
    let { searchKey } = this.state;
    searchKey = (searchKey + '').trim().toLowerCase();

    data = searchKey ? origin.filter((item: IClusterData) =>
      (item.clusterName !== undefined && item.clusterName !== null) && item.clusterName.toLowerCase().includes(searchKey as string),
    ) : origin;
    return data;
  }

  public renderTable() {
    return (
      <Table
        loading={cluster.loading}
        rowKey="clusterId"
        dataSource={this.getData(cluster.clusterData)}
        columns={getClusterColumns(urlPrefix)}
        pagination={pagination}
      />
    );
  }

  public render() {
    return (
      <div className="container">
        <div className="table-operation-panel">
          {this.renderOperationPanel()}
        </div>
        <div className="table-wrapper">{this.renderTable()}</div>
      </div>
    );
  }
}

interface IRadioProps {
  onChange?: (result: any) => any;
  value?: number;
}

@observer
class RadioAndInput extends React.Component<IRadioProps> {
  public state = {
    value: null as number,
  };

  public onRadioChange = (e: RadioChangeEvent) => {
    const { onChange } = this.props;
    if (onChange) {
      onChange(e.target.value);
      this.setState({
        value: e.target.value,
      });
    }
  }

  public onInputChange = (e: number) => {
    const { onChange } = this.props;
    if (onChange) {
      onChange(e);
      this.setState({
        value: e,
      });
    }
  }

  public componentDidMount() {
    if (!cluster.clusterComboList.length) {
      cluster.getClusterComboList();
    }
  }

  public render() {
    const options = cluster.clusterComboList;
    const attrs = {
      min: 0,
      placeholder: '没合适？手动输入试试。',
    };
    return (
      <div className="radio-and-input">
        <Radio.Group value={this.state.value} onChange={this.onRadioChange}>
          {options.map((v: IRadioItem, index: number) => (
            <Radio.Button key={v.value || index} value={parseInt(v.label)}>
              {v.label}
            </Radio.Button>
          ))}
        </Radio.Group>
        <div className="radio-and-input-inputNuber">
          <InputNumber
            {...attrs}
            value={this.state.value}
            onChange={this.onInputChange}
          />
          <span>MB/s</span>
        </div>
      </div>
    );
  }
}
