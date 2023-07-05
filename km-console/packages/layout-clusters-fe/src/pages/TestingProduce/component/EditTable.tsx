/* eslint-disable react/display-name */
import React, { useState } from 'react';
import { Table, Input, InputNumber, Popconfirm, Form, Typography, Button, Select } from 'knowdesign';
import message from '@src/components/Message';
import { IconFont } from '@knowdesign/icons';
import './style/edit-table.less';
import { CheckOutlined, CloseOutlined, PlusSquareOutlined } from '@ant-design/icons';

const EditableCell = ({ editing, dataIndex, title, inputType, placeholder, record, index, children, options, ...restProps }: any) => {
  const inputNode =
    inputType === 'number' ? (
      <InputNumber min={0} precision={0} style={{ width: '130px' }} autoComplete="off" placeholder={placeholder} />
    ) : inputType === 'select' ? (
      <Select style={{ width: '140px' }} options={options || []} placeholder={placeholder} />
    ) : (
      <Input autoComplete="off" placeholder={placeholder} />
    );
  return (
    <td {...restProps}>
      {editing ? (
        <Form.Item
          name={dataIndex}
          style={{
            margin: 0,
          }}
          rules={[
            {
              required: true,
              message: `请输入!`,
            },
          ]}
        >
          {inputNode}
        </Form.Item>
      ) : (
        children
      )}
    </td>
  );
};

const EditTable = React.forwardRef((props: any, ref: any) => {
  const { colCustomConfigs } = props;
  const [form] = Form.useForm();
  const [data, setData] = useState([]);
  const [editingKey, setEditingKey] = useState(0);

  const isEditing = (record: any) => record.editKey === editingKey;

  const getTableData = () => {
    return data;
  };

  const resetTable = () => {
    setData([]);
  };

  React.useImperativeHandle(ref, () => ({
    getTableData,
    resetTable,
  }));

  const edit = (record: any) => {
    form.setFieldsValue({
      key: '',
      value: '',
      ...record,
    });
    setEditingKey(record.editKey);
  };
  const deleteRow = (record: any) => {
    const _data = data.filter((item) => item.editKey !== record.editKey);
    setData(_data);
  };

  const cancel = (record: any) => {
    if (record.key === '') {
      const _data = data.filter((item) => item.editKey !== record.editKey);
      setData(_data);
    }
    setEditingKey(0);
  };

  const add = () => {
    if (editingKey !== 0) {
      message.error('请先保存当前编辑项');
      return;
    }
    form.resetFields();
    const editKey = data.length ? data[data.length - 1].editKey + 1 : 1;
    setData([...data, { key: '', value: '', editKey }]);
    setEditingKey(editKey);
  };
  const save = async (editKey: number) => {
    try {
      const row = await form.validateFields();
      const newData = [...data];
      const index = newData.findIndex((item) => editKey === item.editKey);

      if (index > -1) {
        const item = newData[index];
        newData.splice(index, 1, { ...item, ...row });
        setData(newData);
        setEditingKey(0);
      } else {
        newData.push(row);
        setData(newData);
        setEditingKey(0);
      }
    } catch (errInfo) {
      console.log('Validate Failed:', errInfo);
    }
  };

  const columns = [
    {
      title: 'key',
      dataIndex: 'key',
      width: 40,
      editable: true,
    },
    {
      title: 'value',
      dataIndex: 'value',
      width: 40,
      editable: true,
    },
    {
      title: '',
      dataIndex: 'operation',
      width: 20,
      className: 'no-padding',
      render: (text: any, record: any) => {
        const editable = isEditing(record);
        return editable ? (
          <span>
            <Typography.Link
              onClick={() => save(record.editKey)}
              style={{
                marginRight: 8,
              }}
            >
              <CheckOutlined />
            </Typography.Link>
            {/* <Popconfirm title="确认取消?" onConfirm={() => cancel(record)}> */}
            <a>
              <CloseOutlined onClick={() => cancel(record)} />
            </a>
            {/* </Popconfirm> */}
          </span>
        ) : (
          <>
            <Typography.Link
              className="custom-typography"
              disabled={editingKey !== 0}
              style={{
                marginRight: 8,
              }}
              onClick={() => edit(record)}
            >
              <IconFont type={'icon-bianji1'} size={11} />
            </Typography.Link>
            <Typography.Link className="custom-typography" disabled={editingKey !== 0} onClick={() => deleteRow(record)}>
              <IconFont type={'icon-shanchu1'} size={11} />
            </Typography.Link>
          </>
        );
      },
    },
  ];

  const mergedColumns = columns.map((col, index) => {
    if (!col.editable) {
      return col;
    }

    return {
      ...col,
      onCell: (record: any) =>
        Object.assign(
          {
            record,
            inputType: 'text',
            dataIndex: col.dataIndex,
            title: col.title,
            editing: isEditing(record),
          },
          colCustomConfigs?.[index]
        ),
      title: colCustomConfigs?.[index]?.title || col.title,
    };
  });

  return (
    <div className="edit-table-form">
      <Form form={form} component={false}>
        <Table
          components={{
            body: {
              cell: EditableCell,
            },
          }}
          // scroll={{
          //   x: true,
          // }}
          dataSource={data}
          columns={mergedColumns}
          rowClassName="editable-row"
          pagination={false}
        />
      </Form>
      <div className="add-btn">
        <Button type="link" onClick={add}>
          <PlusSquareOutlined /> 新增条件
        </Button>
      </div>
    </div>
  );
});

export default EditTable;
