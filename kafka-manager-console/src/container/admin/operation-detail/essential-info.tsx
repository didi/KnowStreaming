import * as React from 'react';
import { ILabelValue, ITasksMetaData } from 'types/base-type';
import { Descriptions } from 'antd';
import { observer } from 'mobx-react';
import { timeFormat } from 'constants/strategy';
import { urlPrefix } from 'constants/left-menu';
import { Table } from 'component/antd';
import { pagination } from 'constants/table';
import moment from 'moment';

interface IEassProps {
  tasksMetaData?: ITasksMetaData;
}

@observer
export class EassentialInfo extends React.Component<IEassProps> {
  public render() {
    const { tasksMetaData } = this.props;
    let tasks = {} as ITasksMetaData;
    tasks = tasksMetaData ? tasksMetaData : tasks;
    const gmtCreate = moment(tasks.gmtCreate).format(timeFormat);
    const options = [{
      value: tasks.taskId,
      label: '任务ID',
    }, {
      value: tasks.clusterId,
      label: '集群ID',
    }, {
      value: tasks.clusterName,
      label: '集群名称',
    }, {
      value: gmtCreate,
      label: '创建时间',
    }, {
      value: tasks.kafkaPackageName,
      label: 'kafka包',
    }, {
      value: tasks.kafkaPackageMd5,
      label: 'kafka包 MD5',
    }, {
      value: tasks.operator,
      label: '操作人',
    }];
    const optionsHost = [{
      value: tasks.hostList,
      label: '升级主机列表',
    }, {
      value: tasks.pauseHostList,
      label: '升级的主机暂停点',
    }];
    return(
      <>
      <Descriptions column={3}>
        {options.map((item: ILabelValue, index) => (
          <Descriptions.Item key={item.label || index} label={item.label}>{item.value}</Descriptions.Item>
        ))}
        <Descriptions.Item key="server" label="server配置名">
          {/* /api/v1/rd/kafka-files/66/config-files?dataCenter=cn */}
          
          <a href={`${window.origin}/api/v1/rd/kafka-files/${tasks.serverPropertiesFileId}/config-files?dataCenter=cn`} target="_blank">{tasks.serverPropertiesName}</a>
        </Descriptions.Item>
        <Descriptions.Item key="server" label="server配置 MD5">{tasks.serverPropertiesMd5}</Descriptions.Item>
      </Descriptions>
      <Descriptions column={1}>
        {optionsHost.map((item: any, index) => (
          <Descriptions.Item key={item.label || index} label="">
            <Table
              columns={[{
                title: item.label,
                dataIndex: '',
                key: '',
              }]}
              dataSource={item.value}
              pagination={pagination}
              rowKey="key"
            />
          </Descriptions.Item>
        ))}
      </Descriptions>
      </>
    );
  }

}
