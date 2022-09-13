import React, { useLayoutEffect, useState } from 'react';
import { Checkbox, Col, FormInstance, Row } from 'knowdesign';

type Option = { label: string; value: string | number };
type CheckValue = string | number;
interface CheckboxGroupType {
  options: Option[];
  initSelectedOptions?: CheckValue[];
  formInstance: FormInstance;
  fieldName: string;
  groupIdx: number;
  disabled?: boolean;
  allCheckText?: string;
}

const CheckboxGroupContainer = (props: CheckboxGroupType) => {
  const { options, initSelectedOptions = [], formInstance, fieldName, groupIdx, disabled = false, allCheckText = '全选' } = props;
  const [checkedList, setCheckedList] = useState<CheckValue[]>([]);
  const [indeterminate, setIndeterminate] = useState<boolean>(false);
  const [checkAll, setCheckAll] = useState<boolean>(false);

  // 更新表单项内容
  const updateFieldValue = (curList: CheckValue[]) => {
    const curFieldValue = formInstance.getFieldValue(fieldName);
    const newFieldValue = [].concat(curFieldValue);
    newFieldValue[groupIdx] = curList;
    formInstance.setFieldsValue({
      [fieldName]: newFieldValue,
    });
  };

  // 选择
  const onCheckedChange = (list: CheckValue[]) => {
    list.sort();
    updateFieldValue(list);
    setCheckedList(list);
    setIndeterminate(!!list.length && list.length < options.length);
    setCheckAll(list.length === options.length);
  };

  // 全选
  const onCheckAllChange = (e) => {
    const newOptions = e.target.checked ? options : [];
    updateFieldValue(newOptions.map((option) => option.value));
    setCheckedList(newOptions.map((option) => option.value));
    setIndeterminate(false);
    setCheckAll(e.target.checked);
  };

  useLayoutEffect(() => {
    const newInitOptions = [...initSelectedOptions].sort();
    if (checkedList.length !== newInitOptions.length) {
      setCheckedList(newInitOptions);
      updateFieldValue(newInitOptions);
    } else {
      if (checkedList.some((option, i) => option !== newInitOptions[i])) {
        setCheckedList(newInitOptions);
        updateFieldValue(newInitOptions);
      }
    }
  }, [initSelectedOptions]);

  useLayoutEffect(() => {
    setIndeterminate(!!initSelectedOptions.length && initSelectedOptions.length < options.length);
    setCheckAll(!!initSelectedOptions.length && options.length === initSelectedOptions.length);
  }, [options, initSelectedOptions]);

  return (
    <div style={{ marginBottom: 30 }}>
      <div style={{ marginBottom: 12 }}>
        <Checkbox disabled={disabled} indeterminate={indeterminate} onChange={onCheckAllChange} checked={checkAll}>
          {allCheckText}
        </Checkbox>
      </div>
      <Checkbox.Group disabled={disabled} style={{ width: '100%' }} value={checkedList} onChange={onCheckedChange}>
        <Row gutter={[10, 10]}>
          {options.map((option) => {
            return (
              <Col span={8} key={option.value}>
                <Checkbox value={option.value} className="checkbox-content-ellipsis">
                  {option.label.replace('Cluster-Load', '')}
                </Checkbox>
              </Col>
            );
          })}
        </Row>
      </Checkbox.Group>
    </div>
  );
};

export default CheckboxGroupContainer;
