import React, { forwardRef, useEffect, useImperativeHandle, useRef, useState } from 'react';
import {
  Form,
  ProTable,
  Button,
  Input,
  Modal,
  Space,
  Divider,
  Drawer,
  Transfer,
  Row,
  Col,
  Tooltip,
  Spin,
  AppContainer,
  Utils,
  Popover,
  IconFont,
} from 'knowdesign';
import message from '@src/components/Message';
import moment from 'moment';
import { LoadingOutlined, PlusOutlined } from '@ant-design/icons';
import { defaultPagination } from '@src/constants/common';
import { RoleProps, PermissionNode, AssignUser, RoleOperate, FormItemPermission } from './config';
import api from '@src/api';
import CheckboxGroupContainer from './CheckboxGroupContainer';
import { ConfigPermissionMap } from '../CommonConfig';

const { request } = Utils;
const { confirm } = Modal;
const { TextArea } = Input;

// 新增/编辑角色、查看角色详情抽屉
// eslint-disable-next-line react/display-name
const RoleDetailAndUpdate = forwardRef((props, ref): JSX.Element => {
  const [global] = AppContainer.useGlobalValue();
  const [form] = Form.useForm();
  const [visible, setVisible] = useState<boolean>(false);
  const [type, setType] = useState<RoleOperate>(RoleOperate.Add);
  const [roleDetail, setRoleDetail] = useState<RoleProps>();
  const [permissions, setPermissions] = useState<FormItemPermission[]>([]);
  const [permissionFormLoading, setPermissionFormLoading] = useState<boolean>(false);
  const [initSelectedPermissions, setInitSelectedPermission] = useState<{ [index: string]: [] }>({});
  const [confirmLoading, setConfirmLoading] = useState(false);
  const callback = useRef(() => {
    return;
  });

  useEffect(() => {
    const globalPermissions = global.permissions;
    if (globalPermissions && globalPermissions.length) {
      const sysPermissions = globalPermissions.map((sys: PermissionNode) => {
        const result = {
          id: sys.id,
          name: sys.permissionName,
          essentialPermission: undefined,
          options: [],
        };
        result.options = sys.childList.map((node) => {
          if (node.permissionName === '多集群管理查看' || node.permissionName === '系统管理查看') {
            result.essentialPermission = { label: node.permissionName, value: node.id };
          }
          return { label: node.permissionName, value: node.id };
        });
        return result;
      });
      setPermissions(sysPermissions);
    }
  }, [global]);

  useEffect(() => {
    if (roleDetail) {
      setPermissionFormLoading(true);
      request(api.role(roleDetail.id)).then((res: any) => {
        const initSelected = {};
        const permissions = res.permissionTreeVO.childList;
        permissions.forEach((sys) => {
          initSelected[sys.id] = [];
          sys.childList.forEach((node) => node.has && initSelected[sys.id].push(node.id));
        });
        setInitSelectedPermission(initSelected);
        setPermissionFormLoading(false);
      });
    }
  }, [roleDetail]);

  const onSubmit = () => {
    form.validateFields().then((formData) => {
      formData.permissionIdList.forEach((arr, i) => {
        // 如果分配的系统下的子权限，自动赋予该系统的权限
        if (!Array.isArray(arr)) {
          arr = [];
        }
        if (arr?.length) {
          arr.push(permissions[i].id);
        }
      });
      formData.permissionIdList = formData.permissionIdList.flat().filter((item) => item !== undefined);
      setConfirmLoading(true);
      request(api.editRole, {
        method: type === RoleOperate.Add ? 'POST' : 'PUT',
        data: Object.assign(formData, type === RoleOperate.Edit ? { id: roleDetail.id } : {}),
      }).then(
        (res) => {
          callback.current();
          onClose();
          setInitSelectedPermission({});
          message.success(`${type === RoleOperate.Add ? '新增' : '编辑'}角色成功`);
        },
        () => setConfirmLoading(false)
      );
    });
  };

  // 打开抽屉
  const onOpen = (status: boolean, type: RoleOperate, cbk: () => { return }, record: RoleProps) => {
    setInitSelectedPermission({});
    form.setFieldsValue(record);
    callback.current = cbk;
    setRoleDetail(record);
    setType(type);
    setVisible(status);
  };

  // 关闭抽屉
  const onClose = () => {
    setVisible(false);
    setRoleDetail(undefined);
    form.resetFields();
    setConfirmLoading(false);
  };

  useImperativeHandle(ref, () => ({
    onOpen,
  }));

  return (
    <Drawer
      title={`${type === RoleOperate.Add ? '新增角色' : type === RoleOperate.Edit ? '编辑角色' : '查看角色详情'}`}
      className="role-tab-detail"
      width={600}
      visible={visible}
      maskClosable={false}
      onClose={onClose}
      extra={
        <Space>
          {type !== RoleOperate.View && (
            <>
              <Button size="small" onClick={onClose}>
                取消
              </Button>
              <Button type="primary" size="small" loading={confirmLoading} onClick={onSubmit}>
                确定
              </Button>
              <Divider type="vertical" />
            </>
          )}
        </Space>
      }
    >
      {type === RoleOperate.View ? (
        // 查看角色详情
        <>
          <Row gutter={[12, 12]} className="desc-row">
            <Col span={3} className="label-col">
              角色名称:
            </Col>
            <Col span={21} className="value-col">
              {(roleDetail && roleDetail.roleName) || '-'}
            </Col>
            <Col span={3} className="label-col">
              描述:
            </Col>
            <Col span={21} className="value-col">
              {(roleDetail && roleDetail.description) || '-'}
            </Col>
          </Row>
          <div className="role-permissions-container">
            <div className="title">角色绑定权限项</div>
            <>
              {permissions.length ? (
                <Spin spinning={permissionFormLoading}>
                  {permissions.map((permission, i) => (
                    <CheckboxGroupContainer
                      key={i}
                      formInstance={form}
                      fieldName="permissionIdList"
                      options={permission.options}
                      initSelectedOptions={initSelectedPermissions[permission.id] || []}
                      groupIdx={i}
                      allCheckText={permission.name}
                      disabled
                    />
                  ))}
                </Spin>
              ) : (
                <></>
              )}
            </>
          </div>
        </>
      ) : (
        // 新增/编辑角色
        <Form form={form} layout="vertical">
          <Form.Item
            label="角色名称"
            name="roleName"
            rules={[
              { required: true, message: '角色名称不能为空' },
              {
                pattern: /^[\u4e00-\u9fa5a-zA-Z0-9_]{3,128}$/,
                message: '角色名称只能由中英文大小写、数字、下划线(_)组成，长度限制在3～128字符',
              },
            ]}
          >
            <Input placeholder="请输入角色名称" />
          </Form.Item>
          <Form.Item label="描述" name="description" rules={[{ required: true, message: '描述不能为空' }]}>
            <TextArea placeholder="请输入描述" maxLength={200} />
          </Form.Item>
          <Form.Item
            label="分配权限"
            name="permissionIdList"
            required
            rules={[
              () => ({
                validator(_, value) {
                  if (Array.isArray(value) && value.some((item) => !!item?.length)) {
                    const errs = [];
                    value.forEach((arr, i) => {
                      if (arr?.length && !arr.includes(permissions[i].essentialPermission.value)) {
                        errs.push(`[${permissions[i].essentialPermission.label}]`);
                      }
                    });
                    if (errs.length) {
                      return Promise.reject(`您必须分配 ${errs.join(' 和 ')} 权限`);
                    }
                    return Promise.resolve();
                  }
                  return Promise.reject(new Error('请为角色至少分配一项权限'));
                },
              }),
            ]}
          >
            <>
              {permissions.length ? (
                <Spin spinning={permissionFormLoading}>
                  {permissions.map((permission, i) => (
                    <CheckboxGroupContainer
                      key={i}
                      formInstance={form}
                      fieldName={`permissionIdList`}
                      options={permission.options}
                      initSelectedOptions={initSelectedPermissions[permission.id] || []}
                      groupIdx={i}
                      allCheckText={permission.name}
                    />
                  ))}
                </Spin>
              ) : (
                <></>
              )}
            </>
          </Form.Item>
        </Form>
      )}
    </Drawer>
  );
});

// 用户角色分配抽屉
// eslint-disable-next-line react/display-name
const AssignRoles = forwardRef((props, ref) => {
  // TODO: check 状态
  const [visible, setVisible] = useState<boolean>(false);
  const [roleInfo, setRoleInfo] = useState<RoleProps>(undefined);
  const [users, setUsers] = useState<AssignUser[]>([]);
  const [selectedUsers, setSelectedUsers] = useState<string[]>([]);
  const [loading, setLoading] = useState<boolean>(false);
  const [confirmLoading, setConfirmLoading] = useState<boolean>(false);
  const callback = useRef(() => {
    return;
  });

  const onClose = () => {
    setVisible(false);
    setRoleInfo(undefined);
    setUsers([]);
    setSelectedUsers([]);
    setLoading(false);
    setConfirmLoading(false);
  };

  const filterOption = (inputValue, option) => option.name.includes(inputValue);

  const onChange = (newTargetKeys) => {
    setSelectedUsers(newTargetKeys);
  };

  const onSubmit = () => {
    if (!roleInfo) return;

    setConfirmLoading(true);
    request(api.assignRoles, {
      method: 'POST',
      data: {
        flag: false,
        id: roleInfo.id,
        idList: selectedUsers,
      },
    }).then(
      (res) => {
        message.success('成功为角色分配用户');
        callback.current();
        onClose();
      },
      () => setConfirmLoading(false)
    );
  };

  useEffect(() => {
    if (roleInfo && visible) {
      setLoading(true);
      request(api.getAssignedUsersByRoleId(roleInfo.id)).then((res: AssignUser[]) => {
        const selectedUsers = [];
        res.forEach((user) => user.has && selectedUsers.push(user.id));
        setUsers(res);
        setSelectedUsers(selectedUsers);
        setLoading(false);
      });
    }
  }, [visible, roleInfo]);

  useImperativeHandle(ref, () => ({
    setVisible: (status: boolean, record: RoleProps, cbk: () => { return }) => {
      callback.current = cbk;
      setRoleInfo(record);
      setVisible(status);
    },
  }));

  return (
    <Drawer
      title="分配角色"
      className="role-tab-assign-user"
      width={600}
      visible={visible}
      maskClosable={false}
      onClose={onClose}
      extra={
        <Space>
          <Button size="small" onClick={onClose}>
            取消
          </Button>
          <Button type="primary" size="small" disabled={loading} loading={confirmLoading} onClick={onSubmit}>
            确定
          </Button>
          <Divider type="vertical" />
        </Space>
      }
    >
      <Row gutter={[12, 12]} className="desc-row">
        <Col span={3} className="label-col">
          角色名称:
        </Col>
        <Col span={21} className="value-col">
          {roleInfo && roleInfo.roleName}
        </Col>
        <Col span={3} className="label-col">
          描述:
        </Col>
        <Col span={21} className="value-col">
          {roleInfo && roleInfo.description}
        </Col>
      </Row>
      <Spin spinning={loading}>
        <Transfer
          titles={['未分配用户', '已分配用户']}
          dataSource={users}
          rowKey={(record) => record.id}
          render={(item: AssignUser) => item.name}
          showSearch
          filterOption={filterOption}
          targetKeys={selectedUsers}
          onChange={onChange}
          pagination
          suffix={<IconFont type="icon-fangdajing" />}
        />
      </Spin>
    </Drawer>
  );
});

export default (props: { curTabKey: string }): JSX.Element => {
  const [global] = AppContainer.useGlobalValue();
  const { curTabKey } = props;
  const [roles, setRoles] = useState<RoleProps[]>([]);
  const [pagination, setPagination] = useState<any>(defaultPagination);
  const [loading, setLoading] = useState<boolean>(true);
  const [deleteBtnLoading, setDeleteBtnLoading] = useState<number>(-1);
  const [searchKeywords, setSearchKeywords] = useState('');
  const [searchKeywordsInput, setSearchKeywordsInput] = useState('');
  const detailRef = useRef(null);
  const assignRolesRef = useRef(null);

  const columns = [
    {
      title: '角色ID',
      dataIndex: 'roleCode',
    },
    {
      title: '名称',
      dataIndex: 'roleName',
    },
    {
      title: '描述',
      dataIndex: 'description',
      lineClampOne: true,
    },
    {
      title: '分配用户数',
      dataIndex: 'authedUserCnt',
      width: 100,
      render(cnt: Pick<RoleProps, 'authedUserCnt'>, record: RoleProps) {
        return cnt ? (
          <Popover
            placement="right"
            overlayClassName="tags-with-hide-popover"
            content={
              <div className="container-item-popover">
                {record.authedUsers.map((username) => (
                  <div key={username} className="container-item">
                    {username}
                  </div>
                ))}
              </div>
            }
          >
            <Button size="small" type="link">
              {cnt}
            </Button>
          </Popover>
        ) : (
          <Button size="small" type="link">
            {cnt}
          </Button>
        );
      },
    },
    {
      title: '最后修改人',
      dataIndex: 'lastReviser',
    },
    {
      title: '最后更新时间',
      dataIndex: 'updateTime',
      width: 180,
      render: (date) => moment(date).format('YYYY-MM-DD HH:mm:ss'),
    },
    {
      title: '操作',
      width: 260,
      render(record) {
        return (
          <>
            <Button
              type="link"
              size="small"
              style={{ paddingLeft: 0 }}
              onClick={() => detailRef.current.onOpen(true, RoleOperate.View, getRoleList, record)}
            >
              查看详情
            </Button>
            {global.hasPermission && global.hasPermission(ConfigPermissionMap.ROLE_ASSIGN) ? (
              <Button type="link" size="small" onClick={() => assignRolesRef.current.setVisible(true, record, getRoleList)}>
                分配用户
              </Button>
            ) : (
              <></>
            )}
            {global.hasPermission && global.hasPermission(ConfigPermissionMap.ROLE_EDIT) ? (
              <Button type="link" size="small" onClick={() => detailRef.current.onOpen(true, RoleOperate.Edit, getRoleList, record)}>
                编辑
              </Button>
            ) : (
              <></>
            )}
            {global.hasPermission && global.hasPermission(ConfigPermissionMap.ROLE_DEL) ? (
              <Button type="link" size="small" onClick={() => onDelete(record)}>
                {record.id === deleteBtnLoading ? <LoadingOutlined /> : '删除'}
              </Button>
            ) : (
              <></>
            )}
          </>
        );
      },
    },
  ];

  const getRoleList = (query = {}) => {
    const data = {
      page: pagination.current,
      size: pagination.pageSize,
      roleName: searchKeywords,
      ...query,
    };

    setLoading(true);
    request(api.roleList, {
      method: 'POST',
      data,
    }).then(
      (res: any) => {
        const { pageNo, pageSize, pages, total } = res.pagination;
        if (pageNo > pages && pages !== 0) {
          getRoleList({ page: pages });
          return false;
        }

        setPagination({
          ...pagination,
          current: pageNo,
          pageSize,
          total,
        });
        setRoles(res.bizData);
        setLoading(false);
      },
      () => setLoading(false)
    );
  };

  const onDelete = (record) => {
    if (deleteBtnLoading !== -1) return;
    setDeleteBtnLoading(record.id);
    request(api.checkRole(record.id), {
      method: 'DELETE',
    }).then(
      (res: any) => {
        setDeleteBtnLoading(-1);
        const userList = res && res.userNameList;
        const couldDelete = !(userList && userList.length);
        const isShowTooltip = couldDelete ? false : userList.length > 2;

        confirm({
          // 删除角色弹窗
          title: couldDelete ? '确定删除以下角色吗?' : '请先解除角色引用关系',
          content: (
            <div>
              <div>{record.roleName}</div>
              {!couldDelete && (
                <span>
                  已被用户 “
                  {isShowTooltip ? (
                    <Tooltip title={userList.join('，')}>
                      {userList.slice(0, 2).join('，')}...等 {userList.length} 人
                    </Tooltip>
                  ) : (
                    userList.join('，')
                  )}
                  ” 引用，请先解除引用关系
                </span>
              )}
            </div>
          ),
          okText: couldDelete ? '删除' : '确定',
          okType: 'primary',
          centered: true,
          okButtonProps: {
            size: 'small',
            danger: couldDelete,
          },
          cancelButtonProps: {
            size: 'small',
          },
          maskClosable: true,
          onOk() {
            return (
              couldDelete &&
              request(api.role(record.id), {
                method: 'DELETE',
              }).then((_) => {
                message.success('删除成功');
                getRoleList();
              })
            );
          },
          onCancel() {
            return;
          },
        });
      },
      () => setDeleteBtnLoading(-1)
    );
  };

  const onTableChange = (curPagination) => {
    getRoleList({
      page: curPagination.current,
      size: curPagination.pageSize,
    });
  };

  useEffect(() => {
    if (curTabKey === 'role') {
      getRoleList();
    }
  }, [curTabKey]);

  useEffect(() => {
    (searchKeywords || searchKeywords === '') && getRoleList({ pageNo: 1 });
  }, [searchKeywords]);

  return (
    <>
      <div className="operate-bar">
        <div className="left">
          <div className="refresh-icon" onClick={() => getRoleList()}>
            <IconFont className="icon" type="icon-shuaxin1" />
          </div>
        </div>
        <div className="right">
          <Input
            className="search-input"
            suffix={
              <IconFont
                type="icon-fangdajing"
                onClick={(_) => {
                  setSearchKeywords(searchKeywordsInput);
                }}
                style={{ fontSize: '16px' }}
              />
            }
            placeholder="请输入角色名称"
            value={searchKeywordsInput}
            onPressEnter={(_) => {
              setSearchKeywords(searchKeywordsInput);
            }}
            onChange={(e) => {
              setSearchKeywordsInput(e.target.value);
            }}
          />
          {global.hasPermission && global.hasPermission(ConfigPermissionMap.ROLE_ADD) ? (
            <Button
              type="primary"
              icon={<PlusOutlined />}
              onClick={() => detailRef.current.onOpen(true, RoleOperate.Add, getRoleList, undefined)}
            >
              新增角色
            </Button>
          ) : (
            <></>
          )}
        </div>
      </div>

      <ProTable
        tableProps={{
          showHeader: false,
          loading,
          rowKey: 'id',
          dataSource: roles,
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

      <RoleDetailAndUpdate ref={detailRef} />
      <AssignRoles ref={assignRolesRef} />
    </>
  );
};
