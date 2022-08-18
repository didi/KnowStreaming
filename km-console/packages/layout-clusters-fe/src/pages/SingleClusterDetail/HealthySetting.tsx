/* eslint-disable react/display-name */
import { Button, Divider, Drawer, Form, message, ProTable, Table, Utils } from 'knowdesign';
import React, { useState } from 'react';
import { useIntl } from 'react-intl';
import { getHealthySettingColumn } from './config';
import API from '../../api';
import { useParams } from 'react-router-dom';

const HealthySetting = React.forwardRef((props: any, ref): JSX.Element => {
  const intl = useIntl();
  const [form] = Form.useForm();

  const [visible, setVisible] = useState(false);
  const [initialValues, setInitialValues] = useState({} as any);

  const [data, setData] = React.useState([]);
  const { clusterId } = useParams<{ clusterId: string }>();

  React.useImperativeHandle(ref, () => ({
    setVisible,
    getHealthconfig,
  }));

  const getHealthconfig = () => {
    return Utils.request(API.getClusterHealthyConfigs(+clusterId)).then((res: any) => {
      const values = {} as any;

      try {
        res = res.map((item: any) => {
          const itemValue = JSON.parse(item.value);
          item.weight = itemValue?.weight;

          item.configItemName =
            item.configItem.indexOf('Group Re-Balance') > -1
              ? 'ReBalance'
              : item.configItem.includes('副本未同步')
                ? 'UNDER_REPLICA'
                : item.configItem;

          values[`weight_${item.configItemName}`] = itemValue?.weight;
          values[`value_${item.configItemName}`] = itemValue?.value;
          values[`latestMinutes_${item.configItemName}`] = itemValue?.latestMinutes;
          values[`detectedTimes_${item.configItemName}`] = itemValue?.detectedTimes;
          return item;
        });
      } catch (err) {
        //
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
            detectedTimes: res[`detectedTimes_${item.configItemName}`],
            latestMinutes: res[`latestMinutes_${item.configItemName}`],
            weight: res[`weight_${item.configItemName}`],
            value: item.configItemName === 'Controller' ? 1 : res[`value_${item.configItemName}`],
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
              rowKey: 'dimensionCode',
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
