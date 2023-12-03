import React, { forwardRef, useEffect, useImperativeHandle, useRef, useState } from 'react';
import {
  Button,
  Form,
  Input,
  Modal,
  ProTable,
  Drawer,
  Space,
  Divider,
  AppContainer,
  DKSBreadcrumb,
  Utils,
  Checkbox,
  Tooltip,
  Alert,
} from 'knowdesign';
import message from '@src/components/Message';
import { IconFont } from '@knowdesign/icons';
import { CloseOutlined, EyeInvisibleOutlined, EyeOutlined, LoadingOutlined } from '@ant-design/icons';
import './index.less';
import api from '@src/api';
import { useParams } from 'react-router-dom';
import { regKafkaPassword } from '@src/constants/reg';
import { tableHeaderPrefix } from '@src/constants/common';
import { ClustersPermissionMap } from '../CommonConfig';
export const randomString = (len = 32, chars = 'abcdefghijklmnopqrstuvwxyz1234567890'): string => {
  const maxPos = chars.length;
  let str = '';
  for (let i = 0; i < len; i++) {
    str += chars.charAt(Math.floor(Math.random() * maxPos));
  }
  return str;
};

const { confirm } = Modal;

enum UsersOperate {
  Add,
  ChangePassword,
}
export type UsersProps = {
  clusterId: number;
  name: string;
  authType: any;
  authName: string;
  createTime: string;
  updateTime: string;
};

export const defaultPagination = {
  current: 1,
  pageSize: 10,
  position: 'bottomRight',
  showSizeChanger: true,
  pageSizeOptions: ['10', '20', '50', '100'],
};

const AES_KEY = 'KnowStreamingKM$';
const initialShowPassword = '********';

const AUTH_TYPES = [
  {
    label: 'SCRAM',
    value: 1300,
  },
  {
    label: 'GSSAPI',
    value: 'gssapi',
    disabled: true,
  },
  {
    label: 'PLAIN',
    value: 'plain',
    disabled: true,
  },
  {
    label: 'OAUTHBEARER',
    value: 'oauthbearer',
    disabled: true,
  },
];

const PasswordContent = (props: { clusterId: string; name: string }) => {
  const { clusterId, name } = props;
  const [loading, setLoading] = useState(false);
  const [pw, setPw] = useState(initialShowPassword);
  const [global] = AppContainer.useGlobalValue();
  const switchPwStatus = () => {
    if (!loading) {
      setLoading(true);
      if (pw === initialShowPassword) {
        Utils.request(api.getKafkaUserToken(clusterId, name)).then(
          (res: any) => {
            let token = res.token || '';
            if (res?.decrypt) {
              token = Utils.decryptAES(token, AES_KEY);
            }
            setPw(token);
            setLoading(false);
          },
          () => setLoading(false)
        );
      } else {
        setPw(initialShowPassword);
        setLoading(false);
      }
    }
  };

  return (
    <div style={{ display: 'flex', alignItems: 'center' }}>
      <Tooltip title={pw} placement="bottom">
        <div style={{ maxWidth: '80%', overflow: 'hidden', textOverflow: 'ellipsis', whiteSpace: 'nowrap' }}>{pw}</div>
      </Tooltip>
      {global.hasPermission(ClustersPermissionMap.SECURITY_USER_VIEW_PASSWORD) ? (
        <span style={{ marginLeft: 6 }} onClick={switchPwStatus}>
          {loading ? <LoadingOutlined /> : pw === initialShowPassword ? <EyeInvisibleOutlined /> : <EyeOutlined />}
        </span>
      ) : (
        <></>
      )}
    </div>
  );
};

// 新增 KafkaUser / 修改 KafkaUser 密码
// eslint-disable-next-line react/display-name
const EditKafkaUser = forwardRef((_, ref) => {
  const { clusterId } = useParams<{
    clusterId: string;
  }>();
  const [type, setType] = useState<UsersOperate>(UsersOperate.Add);
  const [form] = Form.useForm();
  const [visible, setVisible] = useState<boolean>(false);
  const [confirmLoading, setConfirmLoading] = useState<boolean>(false);
  const callback = useRef(() => {
    return;
  });

  // 提交表单
  const onSubmit = () => {
    form.validateFields().then((formData) => {
      // 对密码进行加密
      formData.token = Utils.encryptAES(formData.token, AES_KEY);
      // 注意：目前 authType 字段固定传 SCRAM 认证方式的值，之后如果开通了多认证方式，这里需要做更改
      formData.authType = 1300;
      setConfirmLoading(true);
      Utils.request(type === UsersOperate.Add ? api.kafkaUser : api.updateKafkaUserToken, {
        method: type === UsersOperate.Add ? 'POST' : 'PUT',
        data: Object.assign(formData, { clusterId: Number(clusterId) }),
      }).then(
        () => {
          // 执行回调，刷新列表数据
          callback.current();

          onClose();
          message.success(type === UsersOperate.Add ? '成功新增 KafkaUser' : '成功修改密码');
        },
        () => setConfirmLoading(false)
      );
    });
  };

  // 展开抽屉
  const onOpen = (status: boolean, type: UsersOperate, cbk: () => void, detail: UsersProps) => {
    detail && form.setFieldsValue({ kafkaUser: detail.name });
    setType(type);
    setVisible(status);
    callback.current = cbk;
  };

  // 关闭抽屉
  const onClose = () => {
    setVisible(false);
    setConfirmLoading(false);
    form.resetFields();
  };

  useImperativeHandle(ref, () => ({
    onOpen,
  }));

  return (
    <Drawer
      className="users-edit-drawer"
      title={type === UsersOperate.Add ? '新增 KafkaUser' : '修改密码'}
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
      {type === UsersOperate.Add && (
        <Alert
          className="drawer-alert-full-screen"
          message="新增 KafkaUser 必须在集群已开启 ACL 功能时才会生效"
          type="info"
          showIcon
          style={{ marginBottom: 20 }}
        />
      )}
      <Form form={form} layout="vertical">
        <Form.Item
          label="KafkaUser"
          name="kafkaUser"
          rules={[
            { required: true, message: 'KafkaUser 不能为空' },
            { pattern: /^[a-zA-Z0-9]{3,128}$/, message: 'KafkaUser 支持大小写英文、数字，长度为 3~128 字符' },
          ]}
        >
          <Input placeholder="请输入 KakfaUser" disabled={type === UsersOperate.ChangePassword} />
        </Form.Item>
        {type === UsersOperate.Add && (
          <Form.Item
            label="认证方式"
            name="authType"
            rules={[{ required: true, message: '认证方式不能为空' }]}
            initialValue={[AUTH_TYPES[0].value]}
          >
            <Checkbox.Group>
              {AUTH_TYPES.map((type) => {
                return type.disabled ? (
                  <Tooltip title="当前版本暂不支持">
                    <Checkbox value={type.value} disabled>
                      {type.label}
                    </Checkbox>
                  </Tooltip>
                ) : (
                  <Checkbox value={type.value}>{type.label}</Checkbox>
                );
              })}
            </Checkbox.Group>
          </Form.Item>
        )}
        <Form.Item
          label={
            <div className="complex-label">
              <span>{type === UsersOperate.ChangePassword && 'New'} Password</span>
              <span>
                <Tooltip title={'生成随机内容'} placement="left">
                  <IconFont
                    type="icon-shengchengdaima"
                    className="random-icon"
                    onClick={() => {
                      const randomStr = randomString(
                        12,
                        'ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz1234567890_-!"#$%&\'()*+,./:;<=>?@[]^`{|}~'
                      );
                      form && form.setFieldsValue({ token: randomStr });
                    }}
                  />
                </Tooltip>
              </span>
            </div>
          }
          name="token"
          rules={[
            { required: true, message: 'Password 不能为空' },
            {
              pattern: regKafkaPassword,
              message: '密码支持中英文、数字、特殊字符 ! " # $ % & \' ( ) * + , - . / : ; < = > ? @ [  ] ^ _ ` { | } ~',
            },
          ]}
        >
          <Input.Password placeholder="请输入密码" maxLength={128} />
        </Form.Item>
      </Form>
    </Drawer>
  );
});

const SecurityUsers = (): JSX.Element => {
  const [global] = AppContainer.useGlobalValue();
  const { clusterId } = useParams<{
    clusterId: string;
  }>();
  const [loading, setLoading] = useState<boolean>(true);
  const [data, setData] = useState<UsersProps[]>([]);
  const [pagination, setPagination] = useState<any>(defaultPagination);
  const [searchKeywords, setSearchKeywords] = useState('');
  const [searchKeywordsInput, setSearchKeywordsInput] = useState('');
  const editDrawerRef = useRef(null);

  const getKafkaUserList = (query = {}) => {
    const queryParams = {
      pageNo: pagination.current,
      pageSize: pagination.pageSize,
      searchKeywords,
      ...query,
    };
    setLoading(true);
    Utils.request(api.getKafkaUsers(clusterId), {
      method: 'POST',
      data: queryParams,
    }).then(
      (res: any) => {
        const { pageNo, pageSize, total } = res.pagination;
        const pages = Math.ceil(total / pageSize);
        if (pageNo > pages && pages !== 0) {
          getKafkaUserList({ pageNo: pages });
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
        return true;
      },
      () => setLoading(false)
    );
  };

  const columns = () => {
    const baseColumns: any = [
      {
        title: 'KafkaUser',
        dataIndex: 'name',
        width: 300,
        render(name: string) {
          return (
            <Tooltip title={name} placement="topLeft">
              <div style={{ overflow: 'hidden', textOverflow: 'ellipsis', whiteSpace: 'nowrap' }}>{name}</div>
            </Tooltip>
          );
        },
      },
      {
        title: '认证方式',
        dataIndex: 'authName',
      },
      {
        title: 'Password',
        dataIndex: 'token',
        width: 300,
        render(_: string, record: UsersProps) {
          return <PasswordContent clusterId={clusterId} name={record.name} />;
        },
      },
    ];
    if (global.hasPermission) {
      baseColumns.push({
        title: '操作',
        dataIndex: '',
        width: 240,
        render(record: UsersProps) {
          return (
            <>
              {global.hasPermission(ClustersPermissionMap.SECURITY_USER_EDIT_PASSWORD) ? (
                <Button
                  type="link"
                  size="small"
                  style={{ paddingLeft: 0 }}
                  onClick={() => editDrawerRef.current.onOpen(true, UsersOperate.ChangePassword, getKafkaUserList, record)}
                >
                  修改密码
                </Button>
              ) : (
                <></>
              )}
              {global.hasPermission(ClustersPermissionMap.SECURITY_USER_DELETE) ? (
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
  };

  const onDelete = (record: UsersProps) => {
    confirm({
      title: '确定删除该 KafkaUser 吗?',
      okText: '删除',
      okType: 'primary',
      centered: true,
      okButtonProps: {
        size: 'small',
        danger: true,
      },
      cancelButtonProps: {
        size: 'small',
      },
      maskClosable: false,
      onOk() {
        return Utils.request(api.kafkaUser, {
          method: 'DELETE',
          data: {
            clusterId,
            kafkaUser: record.name,
          },
        }).then((_) => {
          message.success('删除成功');
          getKafkaUserList();
        });
      },
    });
  };

  const onTableChange = (curPagination: any) => {
    getKafkaUserList({ pageNo: curPagination.current, pageSize: curPagination.pageSize });
  };

  useEffect(() => {
    // 获取配置列表
    getKafkaUserList();
  }, []);

  useEffect(() => {
    (searchKeywords || searchKeywords === '') && getKafkaUserList({ pageNo: 1 });
  }, [searchKeywords]);

  return (
    <div className="security-users-page">
      <DKSBreadcrumb
        breadcrumbs={[
          { label: '多集群管理', aHref: '/' },
          { label: global?.clusterInfo?.name, aHref: `/cluster/${global?.clusterInfo?.id}` },
          { label: 'Users', aHref: `` },
        ]}
      />
      <div className="security-users-page-list">
        <div className={tableHeaderPrefix}>
          <div className={`${tableHeaderPrefix}-left`}>
            <div className={`${tableHeaderPrefix}-left-refresh`} onClick={() => getKafkaUserList()}>
              <IconFont className={`${tableHeaderPrefix}-left-refresh-icon`} type="icon-shuaxin1" />
            </div>
          </div>
          <div className={`${tableHeaderPrefix}-right`}>
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
              placeholder="请输入 Kafka User"
              value={searchKeywordsInput}
              onPressEnter={(_) => {
                setSearchKeywords(searchKeywordsInput);
              }}
              onChange={(e) => {
                setSearchKeywordsInput(e.target.value);
              }}
            />
            {global.hasPermission && global.hasPermission(ClustersPermissionMap.SECURITY_USER_ADD) ? (
              <Button
                type="primary"
                // icon={<PlusOutlined />}
                onClick={() => editDrawerRef.current.onOpen(true, UsersOperate.Add, getKafkaUserList)}
              >
                新增KafkaUser
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
            rowKey: 'name',
            dataSource: data,
            paginationProps: pagination,
            columns: columns() as any,
            lineFillColor: true,
            attrs: {
              onChange: onTableChange,
              scroll: { y: 'calc(100vh - 270px)' },
            },
          }}
        />
      </div>

      <EditKafkaUser ref={editDrawerRef} />
    </div>
  );
};

export default SecurityUsers;
