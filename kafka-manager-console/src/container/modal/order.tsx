import { wrapper } from 'store';
import { order } from 'store/order';
import { message, Icon, notification, Modal, Table, Tooltip } from 'component/antd';
import { IApprovalOrder, IBaseOrder, IOrderInfo } from 'types/base-type';
import { admin } from 'store/admin';
import { modal } from 'store/modal';
import { cluster } from 'store/cluster';
import { cellStyle } from 'constants/table';
import * as React from 'react';

const updateInputModal = (status: string, type: number) => {
  const formMap = wrapper.xFormWrapper.formMap;
  const region = type === 0 ? 6 : 3;
  const broker = type === 0 ? 7 : 4;
  formMap[region].invisible = status === 'region';
  formMap[broker].invisible = status !== 'region';

  formMap[region].rules = type === 0 ? [{ required: status !== 'region' }] : [{ required: false }];
  formMap[broker].rules = type === 0 ? [{ required: status === 'region' }] : [{ required: false }];
  // tslint:disable-next-line:no-unused-expression
  wrapper.ref && wrapper.ref.updateFormMap$(formMap, wrapper.xFormWrapper.formData);
};

const renderModalTilte = (type: number, status: number) => {
  return (
    <>
      <span key={'title-1'}>审批</span>
      {
        type === 3 && status === 1 ? <span key={'subtitle-1'}>
          <a target="_blank" href="https://github.com/didi/kafka-manager">
            <span className="safe-tip" key={1}>敏感操作：请确认您当前操作是否包含敏感信息</span>&nbsp;
            <Icon key={2} type="question-circle" />
          </a>
        </span> : null}
    </>);
};

export const showApprovalModal = (info: IOrderInfo, status: number, from?: string) => {
  const { id, type } = info;
  const formMap = [{
    key: 'clusterId',
    label: '所属集群',
    type: 'input_number',
    defaultValue: info.detail.logicalClusterName,
    attrs: { disabled: true },
  }, {
    key: 'partitionNum',
    label: '分区数',
    type: 'input_number',
    rules: [{
      required: true,
      message: '请输入分区数（正数）',
      pattern: /^[1-9]\d*$/,
    }],
  }, {
    key: 'replicaNum',
    label: '副本数',
    type: 'input_number',
    defaultValue: 3,
    rules: [{
      required: true,
      message: '请输入副本数（正数）',
      pattern: /^[1-9]\d*$/,
    }],
  }, {
    key: 'retentionTime',
    label: '保存时间（/小时）',
    defaultValue: '12',
    type: 'input_number',
    // options: [{
    //   label: '12小时',
    //   value: '12',
    // }, {
    //   label: '24小时',
    //   value: '24',
    // }, {
    //   label: '48小时',
    //   value: '48',
    // }, {
    //   label: '72小时',
    //   value: '72',
    // }],
    rules: [{
      required: true,
      message: '请输入大于0小于10000的整数',
      pattern: /^\+?[1-9]\d{0,3}(\.\d*)?$/,
    }],
  }, {
    key: 'species',
    label: '类型',
    type: 'radio_group',
    defaultValue: 'region',
    options: [{
      label: 'Region',
      value: 'region',
    }, {
      label: 'Broker',
      value: 'broker',
    }],
    rules: [{ required: false, message: '请选择类型' }],
    attrs: {
      onChange(item: any) {
        updateInputModal(item.target.value, type);
      },
    },
  }, {
    key: 'brokerIdList',
    label: 'Broker',
    invisible: true,
    rules: [{ required: false, message: '请输入Broker' }],
    attrs: {
      placeholder: '请输入Broker',
    },
  }, {
    key: 'regionId',
    label: 'Region',
    type: 'select',
    invisible: false,
    options: admin.brokersRegions,
    rules: [{ required: true, message: '请选择Region' }],
    attrs: {
      placeholder: '请选择Region',
    },
  }] as any;

  const quotaFormMap = [{
    key: 'nowPartitionNum',
    label: '现有分区数',
    type: 'input_number',
    defaultValue: info.detail.partitionNum || info.detail.presentPartitionNum,
    rules: [{ required: false }],
    attrs: { disabled: true },
  }, {
    key: 'regionName',
    label: '所属Region',
    defaultValue: info.detail.regionNameList,
    rules: [{ required: false }],
    attrs: { disabled: true },
  }, {
    key: 'regionBrokerIdList',
    label: 'RegionBroker',
    defaultValue: info.detail.regionBrokerIdList,
    rules: [{ required: false }],
    attrs: { disabled: true },
  }, {
    key: 'topicBrokerIdList',
    label: 'TopicBroker',
    defaultValue: info.detail.topicBrokerIdList,
    rules: [{ required: false }],
    attrs: { disabled: true },
  }, {
    key: 'partitionNum',
    label: '新增分区数',
    type: 'input_number',
    rules: [{
      required: type !== 2,
      message: '请输入分区数（正数）',
      pattern: /^[1-9]\d*$/,
    }],
  }, {
    key: 'brokerIdList',
    label: '扩至Broker',
    defaultValue: info.detail.regionBrokerIdList,
    rules: [{ required: true }],
  }] as any;

  const xFormWrapper = {
    formMap: [
      {
        key: 'id',
        label: '工单ID',
        type: 'input_number',
        attrs: { disabled: true },
      },
      {
        key: 'opinion',
        label: '审批意见',
        type: 'text_area',
        rules: [{
          required: status === 2,
          validator: (rule: any, value: string, callback: any) => {
            if (status === 2) {
              value = value.trim();
              const regexp = /^[ ]+$/;
              if (value.length <= 0 || regexp.test(value)) {
                callback('审批意见不能为空');
                return false;
              }
            }
            return true;
          },
        }],
      }],
    formData: { id },
    okText: status === 1 ? '通过' : '驳回',
    visible: true,
    width: type === 0 ? 760 : 520,
    title: renderModalTilte(type, status) as any,
    onSubmit: (value: any) => {
      value.id = id;
      value.status = status;
      if (value.brokerIdList) {
        value.brokerIdList = value.brokerIdList && Array.isArray(value.brokerIdList) ?
          value.brokerIdList : value.brokerIdList.split(',');
      }
      let params = {} as any;
      if (type === 0) {
        params = {
          replicaNum: value.replicaNum ? Number(value.replicaNum) : '',
          partitionNum: value.partitionNum ? Number(value.partitionNum) : null,
          retentionTime: value.retentionTime,
          brokerIdList: value.brokerIdList,
          regionId: value.regionId,
        };
        params.regionId ? delete params.brokerIdList : delete params.regionId;
      } else if (type === 2 || type === 12) {
        params = {
          partitionNum: value.partitionNum ? Number(value.partitionNum) : null,
          brokerIdList: value.brokerIdList,
        };
      } else {
        params = {};
      }
      const orderParams = {
        id,
        opinion: value.opinion.trim(),
        status,
        detail: JSON.stringify(params),
      } as IApprovalOrder;
      order.approvalOrder(orderParams).then(() => {
        message.success('操作成功');
        if (from) {
          order.getOrderDetail(id);
        }
        order.getApplyOrderList(0);
        order.getApprovalList(0);
      }).catch(err => {
        if (!err || !err.code) {
          notification.error({
            message: '错误',
            description: '网络或服务器错误，请重试！',
          });
        }
      });
    },
  };
  if (type === 0 && status === 1) { // 通过 topic
    xFormWrapper.formMap.splice(1, 0, ...formMap);
  }
  if ((type === 2 || type === 12) && status === 1) { // 通过配额  12分区
    xFormWrapper.formMap.splice(1, 0, ...quotaFormMap);
  }
  wrapper.open(xFormWrapper);
};

export const renderOrderOpModal = (selectedRowKeys: IBaseOrder[], status: number, rowsCallBack?: any) => {
  if (modal.actionAfterClose === 'close') {
    // tslint:disable-next-line: no-unused-expression
    rowsCallBack && rowsCallBack.onChange([], []);
    order.setSelectedRows([]);
  }
  const orderIdList = selectedRowKeys.map((ele: IBaseOrder) => {
    return ele.id;
  });
  const xFormWrapper = {
    formMap: [
      {
        key: 'opinion',
        label: '审批意见',
        type: 'text_area',
        rules: [{
          required: true,
          validator: (rule: any, value: string, callback: any) => {
            value = value.trim();
            const regexp = /^[ ]+$/;
            if (value.length <= 0 || regexp.test(value)) {
              callback('审批意见不能为空');
              return false;
            }
            return true;
          },
        }],
      },
    ],
    formData: {},
    visible: true,
    okText: status === 1 ? '通过' : '驳回',
    title: status === 1 ? '通过' : '驳回',
    onSubmit: async (value: any) => {
      const params = {
        opinion: value.opinion,
        orderIdList,
        status,
      };
      order.batchApprovalOrders(params).then(data => {
        modal.setAction('close');
        modal.showOrderOpResult();
        order.setSelectedRows();
      });
    },
  };
  wrapper.open(xFormWrapper);
};

export const RenderOrderOpResult = () => {
  const handleOk = () => {
    order.getApplyOrderList(0);
    order.getApprovalList(0);
    order.setSelectedRows();
    modal.close();
  };

  const flowColumns = [
    {
      title: '工单Id',
      dataIndex: 'id',
      key: 'id',
    },
    {
      title: '状态',
      dataIndex: 'code',
      key: 'code',
      render: (t: number) => {
        return (
          <span className={t === 0 ? 'success' : 'fail'}>
            {t === 0 ? '成功' : '失败'}
          </span>
        );
      },
    },
    {
      title: '原因',
      dataIndex: 'message',
      key: 'message',
      onCell: () => ({
        style: {
          maxWidth: 120,
          ...cellStyle,
        },
      }),
      render: (text: string) => {
        return (
          <Tooltip placement="bottomLeft" title={text} >
            {text}
          </Tooltip>);
      },
    },
  ];
  return (
    <>
      <Modal
        title="审批结果"
        visible={true}
        onOk={handleOk}
        onCancel={handleOk}
      >
        <Table
          columns={flowColumns}
          rowKey="id"
          dataSource={order.batchApprovalList}
          scroll={{ x: 450, y: 260 }}
          pagination={false}
        />
      </Modal>
    </>
  );
};
