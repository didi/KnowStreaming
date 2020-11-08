import { IUser } from 'types/base-type';
import { users } from 'store/users';
import { wrapper } from 'store';
import { roleMap } from 'constants/status-map';
import { message } from 'component/antd';
import { FormItemType } from 'component/x-form';

export const showApplyModal = (record?: IUser) => {
  const xFormModal = {
    formMap: [
      {
        key: 'username',
        label: '用户名',
        rules: [{ required: true, message: '请输入用户名' }],
      }, {
        key: 'role',
        label: '角色',
        type: 'select',
        options: Object.keys(roleMap).map((item) => ({
          label: roleMap[+item],
          value: +item,
        })),
        rules: [{ required: true, message: '请选择角色' }],
      }, {
        key: 'password',
        label: '密码',
        type: FormItemType.inputPassword,
        rules: [{ required: !record, message: '请输入密码' }],
      },
    ],
    formData: record || {},
    visible: true,
    title: record ? '修改用户' : '新增用户',
    onSubmit: (value: IUser) => {
      if (record) {
        return users.modfiyUser(value).then(() => {
          message.success('操作成功');
        });
      }
      return users.addUser(value).then(() => {
        message.success('操作成功');
      });
    },
  };
  wrapper.open(xFormModal);
};
