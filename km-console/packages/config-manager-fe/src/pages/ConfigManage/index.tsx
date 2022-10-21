import React, { forwardRef, useCallback, useEffect, useImperativeHandle, useRef, useState } from 'react';
import { Button, Form, Input, Select, Switch, Modal, ProTable, Drawer, Space, Divider, Tooltip, AppContainer, Utils } from 'knowdesign';
import message from '@src/components/Message';
import { IconFont } from '@knowdesign/icons';
import { PlusOutlined } from '@ant-design/icons';
import moment from 'moment';
// 引入代码编辑器
import { Controlled as CodeMirror } from 'react-codemirror2';
import 'codemirror/lib/codemirror.css';
//代码高亮
import 'codemirror/addon/edit/matchbrackets';
import 'codemirror/addon/selection/active-line';
import 'codemirror/addon/edit/closebrackets';
require('codemirror/mode/xml/xml');
require('codemirror/mode/javascript/javascript');
import api from '@src/api';
import { defaultPagination } from '@src/constants/common';
import TypicalListCard from '../../components/TypicalListCard';
import { ConfigPermissionMap } from '../CommonConfig';
import { ConfigOperate, ConfigProps } from './config';
import './index.less';

const { request } = Utils;
const { confirm } = Modal;
const { TextArea } = Input;

// 新增/编辑配置抽屉
const EditConfigDrawer = forwardRef((_, ref) => {
  const [config, setConfig] = useState<ConfigProps>({});
  const [type, setType] = useState<ConfigOperate>(ConfigOperate.Add);
  const [form] = Form.useForm();
  const [visible, setVisible] = useState<boolean>(false);
  const [groupOptions, setGroupOpions] = useState<{ label: string; value: string }[]>([]);
  const [confirmLoading, setConfirmLoading] = useState<boolean>(false);
  const [codeMirrorInput, setCodeMirrorInput] = useState<string>('');
  const callback = useRef(() => {
    return;
  });

  // 提交表单
  const onSubmit = () => {
    form.validateFields().then((formData) => {
      setConfirmLoading(true);
      formData.status = formData.status ? 1 : 2;
      const isAdd = type === ConfigOperate.Add;
      const submitApi = isAdd ? api.addConfig : api.editConfig;
      request(submitApi, {
        method: isAdd ? 'PUT' : 'POST',
        data: Object.assign(formData, isAdd ? {} : { id: config.id }),
      }).then(
        (res) => {
          // 执行回调，刷新列表数据
          callback.current();

          onClose();
          message.success(`成功${isAdd ? '新增' : '更新'}配置`);
        },
        () => setConfirmLoading(false)
      );
    });
  };

  // 展开抽屉
  const onOpen = (status: boolean, type: ConfigOperate, cbk: () => void, groupOptions, config: ConfigProps = {}) => {
    if (config.value) {
      try {
        // 如果内容可以格式化为 JSON，进行处理
        config.value = JSON.stringify(JSON.parse(config.value), null, 2);
      } catch (_) {
        //
      }
    }
    form.setFieldsValue({ ...config, status: config.status === 1 });
    setConfig(config);
    setGroupOpions(groupOptions);
    setCodeMirrorInput(config.value);
    setType(type);
    setVisible(status);
    callback.current = cbk;
  };

  // 关闭抽屉
  const onClose = () => {
    setVisible(false);
    setConfirmLoading(false);
    setConfig({});
    form.resetFields();
    setCodeMirrorInput('');
  };

  useImperativeHandle(ref, () => ({
    onOpen,
  }));

  return (
    <Drawer
      className="config-manage-edit-drawer"
      title={`${type === ConfigOperate.Add ? '新增' : '编辑'}配置`}
      width={480}
      visible={visible}
      maskClosable={false}
      onClose={onClose}
      extra={
        <Space>
          <Button size="small" onClick={onClose}>
            取消
          </Button>
          <Button type="primary" size="small" loading={confirmLoading} onClick={onSubmit}>
            确定
          </Button>
          <Divider type="vertical" />
        </Space>
      }
    >
      <Form form={form} layout="vertical">
        <Form.Item label="模块" name="valueGroup" rules={[{ required: true, message: '模块不能为空' }]}>
          <Select options={groupOptions} placeholder="请选择模块" />
        </Form.Item>
        <Form.Item label="配置键" name="valueName" rules={[{ required: true, message: '配置键不能为空' }]}>
          <Input placeholder="请输入配置键" maxLength={100} />
        </Form.Item>
        <Form.Item label="配置值" name="value" rules={[{ required: true, message: '配置值不能为空' }]}>
          <div>
            <CodeMirror
              className="codemirror-form-item"
              value={codeMirrorInput}
              options={{
                mode: 'application/json',
                lineNumbers: true,
                lineWrapper: true,
                autoCloseBrackets: true,
                smartIndent: true,
                tabSize: 2,
              }}
              onBeforeChange={(editor, data, value) => {
                form.setFieldsValue({ value });
                form.validateFields(['value']);
                setCodeMirrorInput(value);
              }}
            />
          </div>
        </Form.Item>
        <Form.Item label="描述" name="memo" rules={[{ required: true, message: '必须输入描述' }]}>
          <TextArea placeholder="请输入描述" maxLength={200} />
        </Form.Item>
        <Form.Item label="启用状态" name="status" valuePropName="checked">
          <Switch />
        </Form.Item>
      </Form>
    </Drawer>
  );
});

// 配置值详情弹窗
// eslint-disable-next-line react/display-name
const ConfigValueDetail = forwardRef((_, ref) => {
  const [visible, setVisible] = useState<boolean>(false);
  const [content, setContent] = useState<string>('');

  const onClose = () => {
    setVisible(false);
    setContent('');
  };

  useImperativeHandle(ref, () => ({
    setVisible: (status: boolean, content: string) => {
      let transformedContent = '';

      try {
        // 如果内容可以格式化为 JSON，进行处理
        transformedContent = JSON.stringify(JSON.parse(content), null, 2);
      } catch (_) {
        transformedContent = content;
      }

      setContent(transformedContent);
      setVisible(status);
    },
  }));

  return (
    <Modal
      className="config-manage-value-modal"
      title="配置值"
      visible={visible}
      centered={true}
      footer={null}
      onCancel={onClose}
      maskClosable={false}
      destroyOnClose
    >
      <CodeMirror
        value={content}
        options={{
          mode: 'application/json',
          lineNumbers: true,
          lineWrapper: true,
          autoCloseBrackets: true,
          smartIndent: true,
          tabSize: 2,
        }}
        onBeforeChange={() => {
          return;
        }}
      />
    </Modal>
  );
});

export default () => {
  const [global] = AppContainer.useGlobalValue();
  const [loading, setLoading] = useState<boolean>(true);
  const [configGroupList, setConfigGroupList] = useState<{ label: string; value: string }[]>([]);
  const [data, setData] = useState<ConfigProps[]>([]);
  const [pagination, setPagination] = useState<any>(defaultPagination);
  const [form] = Form.useForm();
  const editDrawerRef = useRef(null);
  const configValueModalRef = useRef(null);

  const getConfigList = (query = {}) => {
    const formData = form.getFieldsValue();
    const queryParams = {
      page: pagination.current,
      size: pagination.pageSize,
      ...formData,
      ...query,
    };

    setLoading(true);
    request(api.configList, {
      method: 'POST',
      data: queryParams,
    }).then(
      (res: any) => {
        const { pageNo, pageSize, pages, total } = res.pagination;
        if (pageNo > pages && pages !== 0) {
          getConfigList({ page: pages });
          return false;
        }

        setPagination({
          ...pagination,
          current: pageNo,
          pageSize,
          total,
        });
        setData(res.bizData);
        setLoading(false);
      },
      () => setLoading(false)
    );
  };

  const columns = useCallback(() => {
    const baseColumns = [
      {
        title: '模块',
        dataIndex: 'valueGroup',
        lineClampOne: true,
      },
      {
        title: '配置键',
        dataIndex: 'valueName',
        width: 150,
        lineClampOne: true,
        render(content) {
          return (
            <Tooltip title={content}>
              <div className="text-overflow-two-row">{content}</div>
            </Tooltip>
          );
        },
      },
      // TODO: 两行省略
      {
        title: '配置值',
        dataIndex: 'value',
        width: 180,
        lineClampOne: true,
        render(content) {
          return (
            <div className="text-overflow-two-row hover-light" onClick={() => configValueModalRef.current.setVisible(true, content)}>
              {content}
            </div>
          );
        },
      },
      {
        title: '描述',
        dataIndex: 'memo',
        width: 180,
        lineClampOne: true,
      },
      {
        title: '启用状态',
        dataIndex: 'status',
        render(status: number, record) {
          return (
            <div style={{ width: 60 }}>
              <Switch
                checked={status === 1}
                size="small"
                onChange={() => {
                  request(api.configSwtichStatus, {
                    method: 'POST',
                    data: {
                      id: record.id,
                      status: status === 1 ? 2 : 1,
                    },
                  }).then((_) => {
                    getConfigList();
                  });
                }}
              />
            </div>
          );
        },
      },
      {
        title: '最后更新时间',
        dataIndex: 'updateTime',
        render: (date) => moment(date).format('YYYY-MM-DD HH:mm:ss'),
      },
      {
        title: '最后更新人',
        dataIndex: 'operator',
        width: 100,
        lineClampOne: true,
      },
    ];
    if (
      global.hasPermission &&
      (global.hasPermission(ConfigPermissionMap.CONFIG_EDIT) || global.hasPermission(ConfigPermissionMap.CONFIG_DEL))
    ) {
      baseColumns.push({
        title: '操作',
        dataIndex: '',
        width: 130,
        lineClampOne: false,
        render(record: ConfigProps) {
          return (
            <>
              {global.hasPermission && global.hasPermission(ConfigPermissionMap.CONFIG_EDIT) ? (
                <Button
                  type="link"
                  size="small"
                  onClick={() => editDrawerRef.current.onOpen(true, ConfigOperate.Edit, getConfigList, configGroupList, record)}
                  style={{ paddingLeft: 0 }}
                >
                  编辑
                </Button>
              ) : (
                <></>
              )}
              {global.hasPermission && global.hasPermission(ConfigPermissionMap.CONFIG_DEL) ? (
                <Button type="link" size="small" onClick={() => onDelete(record)}>
                  删除
                </Button>
              ) : (
                <></>
              )}
            </>
          );
        },
      });
    }

    return baseColumns;
  }, [global, getConfigList, configGroupList]);

  const onDelete = (record: ConfigProps) => {
    confirm({
      title: '确定删除配置吗?',
      content: `配置 [${record.valueName}] ${record.status === 1 ? '为启用状态，无法删除' : ''}`,
      centered: true,
      okText: '删除',
      okType: 'primary',
      okButtonProps: {
        size: 'small',
        disabled: record.status === 1,
        danger: true,
      },
      cancelButtonProps: {
        size: 'small',
      },
      maskClosable: true,
      onOk() {
        return request(api.delConfig, {
          method: 'DELETE',
          params: {
            id: record.id,
          },
        }).then((_) => {
          message.success('删除成功');
          getConfigList();
        });
      },
    });
  };

  const onTableChange = (curPagination) => {
    getConfigList({ page: curPagination.current, size: curPagination.pageSize });
  };

  useEffect(() => {
    // 获取模块列表
    request(api.configGroupList).then((res: string[]) => {
      const options = res.map((item) => ({
        label: item,
        value: item,
      }));
      setConfigGroupList(options);
    });
    // 获取配置列表
    getConfigList();
  }, []);

  return (
    <>
      <TypicalListCard title="配置管理">
        <div className="config-manage-page">
          <div className="operate-bar">
            <div className="left">
              <div className="refresh-icon" onClick={() => getConfigList()}>
                <IconFont className="icon" type="icon-shuaxin1" />
              </div>
              <Divider type="vertical" style={{ height: 20, top: 0 }} />
              <Form form={form} layout="inline" onFinish={() => getConfigList({ page: 1 })}>
                <Form.Item name="valueGroup">
                  <Select style={{ width: 180 }} placeholder="请选择模块" options={configGroupList} />
                </Form.Item>
                <Form.Item name="valueName">
                  <Input style={{ width: 180 }} placeholder="请输入配置键" />
                </Form.Item>
                <Form.Item name="memo">
                  <Input style={{ width: 180 }} placeholder="请输入描述" />
                </Form.Item>
                <Form.Item>
                  <Button type="primary" ghost htmlType="submit">
                    查询
                  </Button>
                </Form.Item>
              </Form>
            </div>
            {global.hasPermission && global.hasPermission(ConfigPermissionMap.CONFIG_ADD) ? (
              <Button
                type="primary"
                icon={<PlusOutlined />}
                onClick={() => editDrawerRef.current.onOpen(true, ConfigOperate.Add, getConfigList, configGroupList)}
              >
                新增配置
              </Button>
            ) : (
              <></>
            )}
          </div>

          <ProTable
            tableProps={{
              showHeader: false,
              loading,
              rowKey: 'id',
              dataSource: data,
              paginationProps: pagination,
              columns: columns as any,
              lineFillColor: true,
              attrs: {
                onChange: onTableChange,
                scroll: {
                  scrollToFirstRowOnChange: true,
                  x: true,
                  y: 'calc(100vh - 270px)',
                },
              },
            }}
          />
        </div>
      </TypicalListCard>

      {/* 新增/编辑配置抽屉 */}
      <EditConfigDrawer ref={editDrawerRef} />
      <ConfigValueDetail ref={configValueModalRef} />
    </>
  );
};
