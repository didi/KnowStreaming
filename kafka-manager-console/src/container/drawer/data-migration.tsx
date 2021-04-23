import * as React from 'react';
import { Form, Table, InputNumber, Input, Tooltip, DatePicker, PaginationConfig, Button, notification, message } from 'component/antd';
import { IRenderData } from 'container/modal/expert';
import { IMigration } from 'types/base-type';
import { createMigrationTask } from 'lib/api';
import { expert } from 'store/expert';
import moment from 'moment';
import { transMBToB, transMSecondToHour, transHourToMSecond } from 'lib/utils';
import { wrapper } from 'store';
import { cellStyle } from 'constants/table';
import { urlPrefix } from 'constants/left-menu';
import { timeMinute } from 'constants/strategy';

const EditableContext = React.createContext(null);
interface IFormTableProps {
  data: IRenderData[];
  form: any;
}

interface IEditTableCellProps {
  dataIndex: string;
  title: string;
  inputType: string;
  record: IRenderData;
}

class EditableCell extends React.Component<IEditTableCellProps> {

  public renderCell = ({ getFieldDecorator }: any) => {
    const {
      dataIndex,
      title,
      inputType,
      record,
      children,
      ...restProps
    } = this.props;

    return (
      <td {...restProps}>
        {record ? (
          <Form.Item style={{ margin: 0 }}>
            {getFieldDecorator(`${record.key}-${dataIndex}`, {
              rules: [
                {
                  required: true,
                  message: `请输入 ${title}!`,
                },
              ],
              initialValue: (record as any)[dataIndex],
            })(
              <InputNumber min={0} style={{ width: 80 }} />,
            )}
          </Form.Item>
        ) : (
            children
          )}
      </td>
    );
  }

  public render() {
    return <EditableContext.Consumer>{this.renderCell}</EditableContext.Consumer>;
  }
}

class DataMigrationFormTable extends React.Component<IFormTableProps> {
  public columns = [
    {
      title: '集群名称',
      dataIndex: 'clusterName',
      key: 'clusterName',
      onCell: () => ({
        style: {
          maxWidth: 100,
          ...cellStyle,
        },
      }),
      render: (text: string) => {
        return (
          <Tooltip placement="bottomLeft" title={text}>
            {text}
          </Tooltip>
        );
      },
    }, {
      title: 'Topic名称',
      dataIndex: 'topicName',
      key: 'topicName',
      onCell: () => ({
        style: {
          maxWidth: 120,
          ...cellStyle,
        },
      }),
      sorter: (a: IRenderData, b: IRenderData) => a.topicName.charCodeAt(0) - b.topicName.charCodeAt(0),
      render: (text: string) => {
        return (
          <Tooltip placement="bottomLeft" title={text}>
            {text}
          </Tooltip>
        );
      },
    }, {
      title: '限流下限(MB/s)',
      dataIndex: 'minThrottle',
      key: 'minThrottle',
      editable: true,
    }, {
      title: '初始限流(MB/s)',
      dataIndex: 'throttle',
      key: 'throttle',
      editable: true,
    }, {
      title: '限流上限(MB/s)',
      dataIndex: 'maxThrottle',
      key: 'maxThrottle',
      editable: true,
    }, {
      title: '迁移后Topic保存时间(h)',
      dataIndex: 'reassignRetentionTime',
      key: 'reassignRetentionTime',
      editable: true,
    }, {
      title: '原Topic保存时间(h)',
      dataIndex: 'retentionTime',
      key: 'retentionTime', // originalRetentionTime
      width: '132px',
      sorter: (a: IRenderData, b: IRenderData) => b.retentionTime - a.retentionTime,
      render: (time: any) => transMSecondToHour(time),
    }, {
      title: 'BrokerID',
      dataIndex: 'brokerIdList',
      key: 'brokerIdList',
      render: (t: []) => {
        return (
          <Tooltip placement="bottom" title={t.join('、')}>
            {t.join('、')}
          </Tooltip>
        );
      },
    },
  ];

  public infoForm: any = null;

  constructor(props: any) {
    super(props);
    this.state = {
      data: [],
      editingKey: '',
    };
  }

  public cancel = () => {
    this.setState({ editingKey: '' });
  }

  public onSubmit = () => {
    let tableResult = null as any;
    this.props.form.validateFields((error: Error, result: any) => {
      if (error) {
        return;
      }
      tableResult = result;
    });

    const infoData = this.infoForm.getSubmitData();
    if (tableResult && infoData) {
      // 处理参数
      const paramsData = [] as IMigration[];
      const { data } = this.props;
      const throttleArr = [] as any[];
      infoData.beginTime = +moment(infoData.beginTime).format('x');
      Object.getOwnPropertyNames(tableResult).forEach(key => {
        const throttleIndex = Number(key.slice(0, 1));
        const throttleKey = key.slice(2);
        const throttleName = tableResult[key];
        if (!throttleArr[throttleIndex]) {
          throttleArr[throttleIndex] = {};
        }
        throttleArr[throttleIndex][throttleKey] = throttleName;
      });
      data.forEach((ele, index) => {
        throttleArr.forEach((record, i) => {
          if (index === i) {
            if (Number(record.minThrottle) >= Number(record.throttle)) {
              message.warning('限流下限小于初始限流');
              return null;
            }
            if (Number(record.throttle) >= Number(record.maxThrottle)) {
              message.warning('初始限流小于限流上限');
              return null;
            }
            const obj = {
              clusterId: ele.clusterId,
              topicName: ele.topicName,
              originalRetentionTime: ele.retentionTime,
              partitionIdList: ele.partitionIdList,
              brokerIdList: ele.brokerIdList,
              throttle: transMBToB(record.throttle),
              maxThrottle: transMBToB(record.maxThrottle),
              minThrottle: transMBToB(record.minThrottle),
              reassignRetentionTime: transHourToMSecond(record.reassignRetentionTime),
              beginTime: infoData.beginTime,
              description: infoData.description,
            } as IMigration;
            paramsData.push(obj);
          }
        });
      });
      if (paramsData.length === data.length) {
        createMigrationTask(paramsData).then(data => {
          notification.success({ message: '新建迁移任务成功' });
          expert.getHotTopics();
          window.location.href = `${urlPrefix}/expert#2`;
          wrapper.close();
        });
      }
    }
  }

  public onCancel() {
    wrapper.close();
  }

  public render() {
    const components = {
      body: {
        cell: EditableCell,
      },
    };

    const columns = this.columns.map(col => {
      if (!col.editable) {
        return col;
      }

      return {
        ...col,
        onCell: (record: IRenderData) => ({
          record,
          inputType: 'number',
          dataIndex: col.dataIndex,
          title: col.title,
        }),
      };
    });
    return (
      <>
        <EditableContext.Provider value={this.props.form}>
          <Table
            components={components}
            dataSource={this.props.data}
            columns={columns}
            pagination={false}
            scroll={{ y: 520 }}
            className="migration-table"
          />
        </EditableContext.Provider>
        <WrappedInfoForm wrappedComponentRef={(form: any) => this.infoForm = form} />
        <div className="transfer-button">
          <Button type="primary" onClick={this.onSubmit}>确定</Button>
          <Button onClick={this.onCancel}>取消</Button>
        </div>
      </>
    );
  }
}

export const WrappedDataMigrationFormTable = Form.create<IFormTableProps>()(DataMigrationFormTable);

export class InfoForm extends React.Component<IFormTableProps> {

  public getSubmitData() {
    let value = null as any;
    this.props.form.validateFields((error: Error, result: any) => {
      if (error) {
        return;
      }
      value = result;
    });
    return value;
  }

  public render() {
    const formItemLayout = {
      labelCol: {
        xs: { span: 24 },
        sm: { span: 3 },
      },
      wrapperCol: {
        xs: { span: 24 },
        sm: { span: 16 },
      },
    };
    const { getFieldDecorator } = this.props.form;
    const datePickerAttrs = {
      placeholder: '请输入计划开始时间',
      format: timeMinute,
      showTime: true,
    };
    return (
      <Form name="basic" {...formItemLayout} >
        <Form.Item
          key={1}
          className="form-item"
          label="计划开始时间"
        >
          {getFieldDecorator('beginTime', {
            initialValue: moment(),
            rules: [{ required: true, message: '请输入计划开始时间' }],
          })(
            <DatePicker {...datePickerAttrs} />)}
        </Form.Item>
        <Form.Item label="迁移说明" key={2} className="form-item">
          {getFieldDecorator('description', {
            initialValue: '',
            rules: [{ required: true, message: '请输入至少5个字符', pattern: /^.{4,}.$/ }],
          })(
            <Input.TextArea rows={5} placeholder="请输入至少5个字符" />,
          )}
        </Form.Item>
      </Form>
    );
  }
}

const WrappedInfoForm = Form.create({ name: 'migration_form' })(InfoForm);
