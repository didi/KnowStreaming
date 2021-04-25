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
        attrs: {
          disabled: record ? true : false
        }
      }, {
        key: 'role',
        label: '角色',
        type: 'select',
        options: Object.keys(roleMap).map((item) => ({
          label: roleMap[+item],
          value: +item,
        })),
        rules: [{ required: true, message: '请选择角色' }],
      }, 
      // {
      //   key: 'password',
      //   label: '密码',
      //   type: FormItemType.inputPassword,
      //   rules: [{ required: !record, message: '请输入密码' }],
      // },
    ],
    formData: record || {},
    visible: true,
    title: record ? '修改用户' : '新增用户',
    onSubmit: (value: IUser) => {
      if (record) {
        return users.modfiyUser(value)
      }
      return users.addUser(value).then(() => {
        message.success('操作成功');
      });
    },
  };
  if(!record){
   let formMap: any = xFormModal.formMap
    formMap.splice(2, 0,{
        key: 'password',
        label: '密码',
        type: FormItemType.inputPassword,
        rules: [{ required: !record, message: '请输入密码' }],
      },)
  }
  wrapper.open(xFormModal);
};

// const handleCfPassword = (rule:any, value:any, callback:any)=>{
//   if()
// }
export const showApplyModalModifyPassword = (record: IUser) => {
  const xFormModal:any = {
    formMap: [
      // {
      //   key: 'oldPassword',
      //   label: '旧密码',
      //   type: FormItemType.inputPassword,
      //   rules: [{ 
      //     required: true, 
      //     message: '请输入旧密码',
      //   }]
      // },
      {
        key: 'newPassword',
        label: '新密码',
        type: FormItemType.inputPassword,
        rules: [
          { 
            required: true, 
            message: '请输入新密码',
          }
        ],
        attrs:{
          onChange:(e:any)=>{
            users.setNewPassWord(e.target.value)
          }
        }
      }, 
      {
        key: 'confirmPassword',
        label: '确认密码',
        type: FormItemType.inputPassword,
        rules: [
          { 
            required: true, 
            message: '请确认密码',
            validator:(rule:any, value:any, callback:any) => {
              // 验证新密码的一致性
              if(users.newPassWord){
                if(value!==users.newPassWord){
                  rule.message = "两次密码输入不一致";
                  callback('两次密码输入不一致')
                }else{
                  callback()
                }
              }else if(!value){
                rule.message = "请确认密码";
                callback('请确认密码');
              }else{
                callback()
              }
            },
          }
        ],
      },
    ],
    formData: record || {},
    visible: true,
    title: '修改密码',
    onSubmit: (value: IUser) => {
      let params:any = {
        username:record?.username,
        password:value.confirmPassword,
        role:record?.role,
      }
      return users.modfiyUser(params).then(() => {
          message.success('操作成功');
      });
    },
  }
  wrapper.open(xFormModal);
};

