import * as React from 'react';
import { cellStyle } from 'constants/table';

import { Tooltip } from 'antd';
import { admin } from 'store/admin';
import moment = require('moment');

const moduleList = [
  { moduleId: 0, moduleName: 'Topic' },
  { moduleId: 1, moduleName: '应用' },
  { moduleId: 2, moduleName: '配额' },
  { moduleId: 3, moduleName: '权限' },
  { moduleId: 4, moduleName: '集群' },
  { moduleId: 5, moduleName: '分区' },
  { moduleId: 6, moduleName: 'Gateway配置' },
]

export const operateList = {
  0: '新增',
  1: '删除',
  2: '修改'
}


// [
//   { operate: '新增', operateId: 0 },
//   { operate: '删除', operateId: 1 },
//   { operate: '修改', operateId: 2 },
// ]

export const getJarFuncForm: any = (props: any) => {
  const formMap = [
    {
      key: 'moduleId',
      label: '模块',
      type: 'select',
      attrs: {
        style: {
          width: '130px'
        },
        placeholder: '请选择模块',
      },
      options: moduleList.map(item => {
        return {
          label: item.moduleName,
          value: item.moduleId
        }
      }),
      formAttrs: {
        initialvalue: 0,
      },
    },
    {
      key: 'operator',
      label: '操作人',
      type: 'input',
      attrs: {
        style: {
          width: '170px'
        },
        placeholder: '请输入操作人'
      },
      getvaluefromevent: (event: any) => {
        return event.target.value.replace(/\s+/g, '')
      },
    },
    // {
    //   key: 'resource',
    //   label: '资源名称',
    //   type: 'input',
    //   attrs: {
    //     style: {
    //       width: '170px'
    //     },
    //     placeholder: '请输入资源名称'
    //   },
    // },
    // {
    //   key: 'content',
    //   label: '操作内容',
    //   type: 'input',
    //   attrs: {
    //     style: {
    //       width: '170px'
    //     },
    //     placeholder: '请输入操作内容'
    //   },
    // },
  ]
  return formMap;
}
export const getOperateColumns = () => {

  const columns: any = [
    {
      title: '模块',
      dataIndex: 'module',
      key: 'module',
      align: 'center',
      width: '12%'
    },
    {
      title: '资源名称',
      dataIndex: 'resource',
      key: 'resource',
      align: 'center',
      width: '12%'
    },
    {
      title: '操作内容',
      dataIndex: 'content',
      key: 'content',
      align: 'center',
      width: '25%',
      onCell: () => ({
        style: {
          maxWidth: 350,
          ...cellStyle,
        },
      }),
      render: (text: string, record: any) => {
        return (
          <Tooltip placement="topLeft" title={text} >{text}</Tooltip>);
      },
    },
    {
      title: '操作人',
      dataIndex: 'operator',
      align: 'center',
      width: '12%'
    },
  ];
  return columns
}