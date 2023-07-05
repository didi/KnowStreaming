/* eslint-disable react/display-name */
import { Button, Divider, Drawer, Form, ProTable, Table, Utils } from 'knowdesign';
import message from '@src/components/Message';
import React, { useState } from 'react';
import { useIntl } from 'react-intl';
import { getHealthySettingColumn } from './config';
import API from '../../api';
import { useParams } from 'react-router-dom';
import notification from '@src/components/Notification';

interface HealthConfig {
  dimensionCode: number;
  dimensionName: string;
  configGroup: string;
  configItem: string;
  configName: string;
  configDesc: string;
  value: string;
}

const HealthySetting = React.forwardRef((props: any, ref): JSX.Element => {
  const intl = useIntl();
  const [form] = Form.useForm();
  const { clusterId } = useParams<{ clusterId: string }>();

  const [visible, setVisible] = useState(false);
  const [initialValues, setInitialValues] = useState({} as any);

  const [data, setData] = React.useState<HealthConfig[]>([]);

  React.useImperativeHandle(ref, () => ({
    setVisible,
    getHealthconfig,
  }));

  const getHealthconfig = () => {
    return Utils.request(API.getClusterHealthyConfigs(+clusterId)).then((res: HealthConfig[]) => {
      const values = {} as any;
      res.sort((a, b) => a.dimensionCode - b.dimensionCode);

      try {
        res.forEach((item) => {
          const itemValue = JSON.parse(item.value);
          const { value, latestMinutes, detectedTimes, amount, ratio } = itemValue;

          value && (values[`value_${item.configItem}`] = value);
          latestMinutes && (values[`latestMinutes_${item.configItem}`] = latestMinutes);
          detectedTimes && (values[`detectedTimes_${item.configItem}`] = detectedTimes);
          amount && (values[`amount_${item.configItem}`] = amount);
          ratio && (values[`ratio_${item.configItem}`] = ratio);
        });
      } catch (err) {
        notification.error({
          message: '健康项检查规则解析失败',
        });
      }
      const formItemsValue = {
        ...initialValues,
        ...values,
      };
      setInitialValues(formItemsValue);
      form.setFieldsValue(formItemsValue);
      setData(res);
    });
  };

  const onCancel = () => {
    form.resetFields();
    setVisible(false);
  };

  const onSubmit = () => {
    form.validateFields().then((res) => {
      const params = [] as any;
      data.map((item) => {
        params.push({
          clusterId: +clusterId,
          value: JSON.stringify({
            clusterPhyId: +clusterId,
            detectedTimes: res[`detectedTimes_${item.configItem}`],
            latestMinutes: res[`latestMinutes_${item.configItem}`],
            amount: res[`amount_${item.configItem}`],
            ratio: res[`ratio_${item.configItem}`],
            value: item.configItem === 'Controller' ? 1 : res[`value_${item.configItem}`],
          }),
          valueGroup: item.configGroup,
          valueName: item.configName,
        });
      });
      Utils.put(API.putPlatformConfigs, params)
        .then((res) => {
          message.success('修改成功');
          form.resetFields();
          setVisible(false);
        })
        .catch((err) => {
          message.error('操作失败' + err.message);
        });
    });
  };

  const onHandleValuesChange = (value: any, allValues: any) => {
    //
  };

  return (
    <>
      <Drawer
        className="drawer-content healthy-drawer-content"
        onClose={onCancel}
        maskClosable={false}
        extra={
          <div className="operate-wrap">
            <Button size="small" style={{ marginRight: 8 }} onClick={onCancel}>
              取消
            </Button>
            <Button size="small" type="primary" onClick={onSubmit}>
              确定
            </Button>
            <Divider type="vertical" />
          </div>
        }
        title={intl.formatMessage({ id: 'healthy.setting' })}
        visible={visible}
        placement="right"
        width={1080}
      >
        <Form form={form} layout="vertical" onValuesChange={onHandleValuesChange}>
          <ProTable
            tableProps={{
              rowKey: 'configItem',
              showHeader: false,
              dataSource: data,
              columns: getHealthySettingColumn(form, data, clusterId),
              noPagination: true,
            }}
          />
        </Form>
      </Drawer>
    </>
  );
});

export default HealthySetting;
