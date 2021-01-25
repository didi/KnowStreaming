import { IClusterData } from 'types/base-type';
import { users } from 'store/users';
import { wrapper } from 'store';
import { cluster } from 'store/cluster';
import { notification } from 'component/antd';
import { urlPrefix } from 'constants/left-menu';
import { region } from 'store';

export const showCpacityModal = (item: IClusterData) => {
  const xFormModal = {
    formMap: [
      {
        key: 'type',
        label: '扩缩容：',
        type: 'select',
        options: [{
          label: '扩容',
          value: 5,
        }, {
          label: '缩容',
          value: 15,
        }],
        rules: [{ required: true, message: '请选择' }],
        attrs: {
          placeholder: '请选择',
        },
      },
      {
        key: 'description',
        label: '申请原因',
        type: 'text_area',
        rules: [{ required: true, pattern: /^.{4,}.$/, message: '请输入至少5个字符' }],
        attrs: {
          placeholder: '请输入至少5个字符',
        },
      },
    ],
    formData: {},
    visible: true,
    title: '申请扩缩容',
    okText: '确认',
    onSubmit: (value: any) => {
      const cpacityParams = {
        type: value.type,
        applicant: users.currentUser.username,
        description: value.description,
        extensions: JSON.stringify({ clusterId: item.clusterId }),
      };
      cluster.applyCpacity(cpacityParams).then(data => {
        notification.success({
          message: `申请${value.type === 5 ? '扩容' : '缩容'}成功`,
        });
        window.location.href = `${urlPrefix}/user/order-detail/?orderId=${data.id}&region=${region.currentRegion}`;
      });
    },
  };
  wrapper.open(xFormModal);
};
