import React, { useState } from 'react';
import { Button, ProTable, Popover } from 'knowdesign';
import { CloseOutlined } from '@ant-design/icons';

export const PopoverBroker = (props: any) => {
  const [visible, setVisible] = useState<boolean>(false);
  const columns = [
    {
      title: 'BrokerID',
      dataIndex: 'brokerId',
      key: 'brokerHost',
    },
    {
      title: 'Host',
      dataIndex: 'brokerHost',
      key: 'brokerHost',
    },
  ];
  const newPropsData = Object.keys(props.data).map((item) => {
    return {
      brokerId: item,
      brokerHost: props.data[item],
    };
  });
  return (
    <Popover
      // getPopupContainer={(triggerNode: any) => {
      //   return triggerNode;
      // }}
      placement="bottomLeft"
      overlayClassName="custom-popover-borker"
      trigger={'click'}
      title={
        <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
          <div style={{ fontSize: '16px' }}>{props?.title}</div>
          <Button
            type="text"
            icon={<CloseOutlined className="close-icon" />}
            onClick={() => {
              setVisible(false);
            }}
          />
        </div>
      }
      visible={visible}
      onVisibleChange={(visible) => setVisible(visible)}
      content={
        <div className="container">
          <div className="main">
            <ProTable
              showQueryForm={false}
              tableProps={{
                noPagination: true,
                showHeader: false,
                rowKey: 'key',
                // loading: loading,
                columns,
                dataSource: newPropsData,
                // paginationProps: { ...pagination },
                attrs: {
                  bordered: false,
                  //   className: 'frameless-table', // 纯无边框表格类名
                  //   onChange: onTableChange,
                  size: 'middle',
                },
              }}
            />
          </div>
        </div>
      }
    >
      {newPropsData && newPropsData.length > 0 && <a style={{ display: 'inline-block', marginLeft: '8px' }}>查看详情</a>}
    </Popover>
  );
};
