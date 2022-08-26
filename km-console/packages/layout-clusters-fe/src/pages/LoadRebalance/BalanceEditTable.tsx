/* eslint-disable react/display-name */
import React, { useState, useEffect, useRef } from 'react';
import { Table, Input, InputNumber, Form, Tooltip, Button, message, IconFont } from 'knowdesign';
import { QuestionCircleOutlined } from '@ant-design/icons';

const EditableCell = ({ dataIndex, editable, title, inputType, handleSave, placeholder, record, index, children, ...restProps }: any) => {
  const inputRef = useRef(null);

  const save = async () => {
    try {
      handleSave({ ...record });
    } catch (errInfo) {
      console.log('Save failed:', errInfo);
    }
  };
  const inputNode =
    inputType === 'number' ? (
      <InputNumber
        style={{ width: '130px' }}
        min={1}
        max={100}
        autoComplete="off"
        placeholder={placeholder}
        ref={inputRef}
        formatter={(value: number | string) => `${value}%`}
        parser={(value: number | string) => value!.toString().replace('%', '')}
        onPressEnter={save}
        onBlur={save}
        prefix="avg+-"
      />
    ) : (
      <Input autoComplete="off" placeholder={placeholder} />
    );
  return (
    <td {...restProps}>
      {editable ? (
        <div style={{ display: 'flex', alignItems: 'center' }}>
          <span style={{ marginRight: '5px' }}>avg ±</span>
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
        </div>
      ) : (
        children
      )}
    </td>
  );
};

const BalanceEditTable = React.forwardRef((props: any, ref: any) => {
  const { colCustomConfigs, tableData, tableDataChange } = props;
  const [form] = Form.useForm();
  const [dataSource, setDataSource] = useState(tableData);

  useEffect(() => {
    setDataSource(tableData);
    const formData: any = {};
    tableData?.forEach((item: any) => {
      formData[`${item.type}-intervalPercent`] = item.intervalPercent;
    });
    form.setFieldsValue({ ...formData });
  }, [tableData]);

  const editTableValidate = async () => {
    const values = await form.validateFields();
    return values;
  };

  const handleSave = async (row: any) => {
    let values: any = {};
    form
      .validateFields()
      .then((data) => {
        values = data;
        tableChange(values);
      })
      .catch((errs) => {
        values = errs?.values;
        tableChange(values);
      });
  };

  const tableChange = (formData: any) => {
    const newData = [...dataSource];
    Object.keys(formData).forEach((key) => {
      const type = key.split('-')[0];
      const index = newData.findIndex((item) => type === item.type);
      const item = newData[index];
      newData.splice(index, 1, {
        ...item,
        intervalPercent: formData[key],
      });
    });
    tableDataChange(newData);
  };

  React.useImperativeHandle(ref, () => ({
    editTableValidate,
  }));

  const columns = [
    {
      title: '均衡维度',
      dataIndex: 'name',
      width: '40%',
    },
    {
      title: () => (
        <span>
          均衡区间{' '}
          <Tooltip title="单位：%，大于0，小于100">
            {' '}
            <QuestionCircleOutlined />
          </Tooltip>
        </span>
      ),
      dataIndex: 'intervalPercent',
      width: '40%',
      editable: true,
    },
    {
      title: '优先级',
      dataIndex: 'priority',
      width: '20%',
    },
  ];

  const mergedColumns = columns.map((col, index) => {
    if (!col.editable) {
      return col;
    }

    return {
      ...col,
      onCell: (record: any, index: number) =>
        Object.assign(
          {
            record,
            inputType: 'number',
            dataIndex: `${record.type}-${col.dataIndex}`,
            title: col.title,
            editable: col.editable,
            index: index,
            handleSave,
            form,
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
          dataSource={dataSource}
          columns={mergedColumns}
          rowClassName={() => 'editable-row'}
          pagination={false}
        />
      </Form>
    </div>
  );
});

export default BalanceEditTable;
