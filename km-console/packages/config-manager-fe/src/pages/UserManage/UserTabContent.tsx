import React, { forwardRef, useCallback, useEffect, useImperativeHandle, useRef, useState } from 'react';
import { Form, ProTable, Select, Button, Input, Modal, Drawer, Space, Divider, AppContainer, Utils } from 'knowdesign';
import message from '@src/components/Message';
import { IconFont } from '@knowdesign/icons';
import { PlusOutlined, QuestionCircleOutlined } from '@ant-design/icons';
import moment from 'moment';
import { defaultPagination } from '@src/constants/common';
import { UserProps, UserOperate } from './config';
import CheckboxGroupContainer from './CheckboxGroupContainer';
import TagsWithHide from '../../components/TagsWithHide/index';
import api from '@src/api';
import { ConfigPermissionMap } from '../CommonConfig';

const { confirm } = Modal;
const { request } = Utils;

// 正则表达式
const PASSWORD_REGEXP = /^[a-zA-Z0-9_]{6,12}$/;
const USERNAME_REGEXP = /^[a-zA-Z0-9_]{3,128}$/;
const REALNAME_REGEXP = /^[\u4e00-\u9fa5a-zA-Z0-9_]{1,128}$/;

// 编辑用户抽屉
// eslint-disable-next-line react/display-name
const EditUserDrawer = forwardRef((props, ref) => {
  const [global] = AppContainer.useGlobalValue();
  const [form] = Form.useForm();
  const [visible, setVisible] = useState<boolean>(false);
  const [confirmLoading, setConfirmLoading] = useState<boolean>(false);
  const [type, setType] = useState<UserOperate>(UserOperate.Add);
  const [user, setUser] = useState<UserProps>();
  const [roleOptions, setRoleOptions] = useState<{ label: string; value: number }[]>([]);
  const [initSelectedOptions, setInitSelectedOptions] = useState<number[]>([]);
  const callback = useRef(() => {
    return;
  });

  // 提交表单
  const onSubmit = () => {
    form.validateFields().then((formData) => {
      setConfirmLoading(true);
      formData.roleIds = formData.roleIds.flat();
      if (!formData.pw) {
        delete formData.pw;
      }
      // 密码加密
      // formData.pw = Utils.encryptAES(formData.pw, systemCipherKey);

      const requestPromise =
        type === UserOperate.Add
          ? request(api.addUser, {
              method: 'PUT',
              data: formData,
            })
          : request(api.editUser, {
              method: 'POST',
              data: { ...formData, phone: Date.now() },
            });
      requestPromise.then(
        (res) => {
          callback.current();
          onClose();
          message.success(`${type === UserOperate.Add ? '新增' : '编辑'}用户成功`);
        },
        () => setConfirmLoading(false)
      );
    });
  };

  // 打开抽屉
  const onOpen = (status: boolean, type: UserOperate, cbk: () => { return }, userDetail: UserProps, roles) => {
    form.setFieldsValue(userDetail);
    setUser(userDetail);
    setInitSelectedOptions(userDetail?.roleList ? userDetail.roleList.map((role) => role.id) : []);
    setRoleOptions(roles);
    setType(type);
    setVisible(status);
    callback.current = cbk;
  };

  // 关闭抽屉
  const onClose = () => {
    setVisible(false);
    form.resetFields();
    setUser(undefined);
    setInitSelectedOptions([]);
    setRoleOptions([]);
    setConfirmLoading(false);
  };

  useImperativeHandle(ref, () => ({
    onOpen,
  }));

  return (
    <Drawer
      title={`${type === UserOperate.Add ? '新增' : '编辑'}用户`}
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
        <Form.Item
          label="用户账号"
          name="userName"
          rules={[
            { required: true, message: '用户账号不能为空' },
            { pattern: USERNAME_REGEXP, message: '用户账号只能由英文大小写、数字、下划线(_)组成，长度限制在3～128字符' },
          ]}
        >
          <Input disabled={type === UserOperate.Edit} placeholder="请输入用户账号" />
        </Form.Item>
        {type === UserOperate.Add || global.hasPermission(ConfigPermissionMap.USER_EDIT) ? (
          <Form.Item
            label="用户实名"
            name="realName"
            rules={[
              { required: true, message: '用户实名不能为空' },
              { pattern: REALNAME_REGEXP, message: '用户实名只能由中英文大小写、数字、下划线组成，长度限制在1～128字符' },
            ]}
          >
            <Input placeholder="请输入用户实名" />
          </Form.Item>
        ) : (
          <></>
        )}
        {type === UserOperate.Add || global.hasPermission(ConfigPermissionMap.USER_CHANGE_PASS) ? (
          <Form.Item
            label={`${type === UserOperate.Edit ? '新' : ''}密码`}
            name="pw"
            tooltip={{ title: '密码支持英文、数字、下划线(_)，长度限制在6~12字符', icon: <QuestionCircleOutlined /> }}
            rules={[
              { required: type === UserOperate.Add, message: '密码不能为空' },
              { pattern: PASSWORD_REGEXP, message: '密码只能由英文、数字、下划线(_)组成，长度限制在6~12字符' },
            ]}
          >
            <Input.Password placeholder="请输入密码" />
          </Form.Item>
        ) : (
          <></>
        )}
        {type === UserOperate.Add || global.hasPermission(ConfigPermissionMap.USER_EDIT) ? (
          <Form.Item
            label="分配角色"
            name="roleIds"
            rules={[
              () => ({
                validator(_, value) {
                  if (Array.isArray(value) && value.some((item) => !!item.length)) {
                    return Promise.resolve();
                  }
                  return Promise.reject(new Error('请为用户至少分配一名角色'));
                },
              }),
            ]}
          >
            <CheckboxGroupContainer
              formInstance={form}
              fieldName="roleIds"
              options={roleOptions}
              initSelectedOptions={initSelectedOptions}
              groupIdx={0}
            />
          </Form.Item>
        ) : (
          <></>
        )}
      </Form>
    </Drawer>
  );
});

export default (props: { curTabKey: string }) => {
  const { curTabKey } = props;
  const [global] = AppContainer.useGlobalValue();
  const [loading, setLoading] = useState<boolean>(true);
  const [users, setUsers] = useState<UserProps[]>([]);
  const [pagination, setPagination] = useState<any>(defaultPagination);
  const [simpleRoleList, setSimpleRoleList] = useState<{ value: number; label: string }[]>([]);
  const [form] = Form.useForm();
  const modalRef = useRef(null);
  const editRef = useRef(null);

  const getUserList = (query = {}) => {
    const formData = form.getFieldsValue();
    const data = {
      page: pagination.current,
      size: pagination.pageSize,
      ...formData,
      ...query,
    };
    setLoading(true);

    request(api.userList, {
      method: 'POST',
      data,
    }).then(
      (res: any) => {
        const { pageNo, pageSize, pages, total } = res.pagination;
        if (pageNo > pages && pages !== 0) {
          getUserList({ page: pages });
          return false;
        }

        setPagination({
          ...pagination,
          current: pageNo,
          pageSize,
          total,
        });
        setUsers(res.bizData);
        setLoading(false);
      },
      () => setLoading(false)
    );
  };

  const delUser = (record: UserProps) => {
    confirm({
      title: '删除提示',
      content: `确定要删除用户⌈${record.userName}⌋吗?`,
      centered: true,
      maskClosable: true,
      okType: 'primary',
      okText: '删除',
      okButtonProps: {
        size: 'small',
        danger: true,
      },
      cancelButtonProps: {
        size: 'small',
      },
      onOk() {
        return request(api.user(record.id), {
          method: 'DELETE',
        }).then((_) => {
          message.success('删除成功');
          getUserList();
        });
      },
      onCancel() {
        return;
      },
    });
  };

  const onTableChange = (curPagination) => {
    getUserList({
      page: curPagination.current,
      size: curPagination.pageSize,
    });
  };

  const columns = useCallback(() => {
    const baseColumns = [
      {
        title: '用户账号',
        dataIndex: 'userName',
      },
      {
        title: '用户实名',
        dataIndex: 'realName',
      },
      {
        title: '分配角色',
        dataIndex: 'roleList',
        width: 560,
        render(roleList) {
          const roles = roleList.map((role) => role.roleName);
          return <TagsWithHide list={roles} expandTagContent={(num) => `共有${num}个角色`} />;
        },
      },
      {
        title: '最后更新时间',
        dataIndex: 'updateTime',
        render: (date) => moment(date).format('YYYY-MM-DD HH:mm:ss'),
      },
    ];

    if (
      global.hasPermission &&
      (global.hasPermission(ConfigPermissionMap.USER_CHANGE_PASS) ||
        global.hasPermission(ConfigPermissionMap.USER_EDIT) ||
        global.hasPermission(ConfigPermissionMap.USER_DEL))
    ) {
      baseColumns.push({
        title: '操作',
        dataIndex: '',
        width: 140,
        render(record: UserProps) {
          return (
            <>
              {global.hasPermission &&
              (global.hasPermission(ConfigPermissionMap.USER_EDIT) || global.hasPermission(ConfigPermissionMap.USER_CHANGE_PASS)) ? (
                <Button
                  type="link"
                  size="small"
                  style={{ paddingLeft: 0 }}
                  onClick={() => editRef.current.onOpen(true, UserOperate.Edit, getUserList, record, simpleRoleList)}
                >
                  编辑
                </Button>
              ) : (
                <></>
              )}
              {global.hasPermission && global.hasPermission(ConfigPermissionMap.USER_DEL) ? (
                <Button type="link" size="small" onClick={() => delUser(record)}>
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
  }, [global, getUserList, simpleRoleList]);

  useEffect(() => {
    if (curTabKey === 'user') {
      getUserList();
      request(api.simpleRoleList).then((res: { id: number; roleName: string }[]) => {
        const roles = res.map(({ id, roleName }) => ({ label: roleName, value: id }));
        setSimpleRoleList(roles);
      });
    }
  }, [curTabKey]);

  return (
    <>
      <div className="operate-bar">
        <div className="left">
          <div className="refresh-icon" onClick={() => getUserList()}>
            <IconFont className="icon" type="icon-shuaxin1" />
          </div>
          <Divider type="vertical" style={{ height: 20, top: 0 }} />

          <Form form={form} layout="inline" onFinish={() => getUserList({ page: 1 })}>
            <Form.Item name="userName">
              <Input placeholder="请输入用户账号" />
            </Form.Item>
            <Form.Item name="realName">
              <Input placeholder="请输入用户实名" />
            </Form.Item>
            <Form.Item name="roleId">
              <Select style={{ width: 190 }} placeholder="选择平台已创建的角色名" options={simpleRoleList} />
            </Form.Item>
            <Form.Item>
              <Button type="primary" ghost htmlType="submit">
                查询
              </Button>
            </Form.Item>
          </Form>
        </div>
        {global.hasPermission && global.hasPermission(ConfigPermissionMap.USER_ADD) ? (
          <Button
            type="primary"
            icon={<PlusOutlined />}
            onClick={() => editRef.current.onOpen(true, UserOperate.Add, getUserList, {}, simpleRoleList)}
          >
            新增用户
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
          dataSource: users,
          paginationProps: pagination,
          columns,
          lineFillColor: true,
          attrs: {
            onChange: onTableChange,
            scroll: {
              scrollToFirstRowOnChange: true,
              x: true,
              y: 'calc(100vh - 326px)',
            },
          },
        }}
      />

      <EditUserDrawer ref={editRef} />
    </>
  );
};
