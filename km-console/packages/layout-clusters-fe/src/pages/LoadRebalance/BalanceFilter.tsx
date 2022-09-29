import React, { useEffect, useState } from 'react';
import { Button, Popover, Row, Col, Select } from 'knowdesign';
import { IconFont } from '@knowdesign/icons';
import { CloseOutlined } from '@ant-design/icons';

const balancePrefix = 'custom-popover-balance';
interface FilterListType {
  id: number;
  firstLevel: string;
  secondLevel: number;
}

const filterNorm = [
  {
    label: 'Disk',
    value: 'disk',
    disabled: false,
  },
  {
    label: 'Byte In',
    value: 'bytesIn',
    disabled: false,
  },
  {
    label: 'Byte Out',
    value: 'bytesOut',
    disabled: false,
  },
];

const isBalance = [
  {
    label: '已均衡',
    value: 0,
  },
  {
    label: '未均衡',
    value: 2,
  },
];

export const BalanceFilter = (props: any) => {
  const [visible, setVisible] = useState<boolean>(false);
  const [filterList, setFilterList] = useState<FilterListType[]>(null);
  const [filterNormList, setFilterNormList] = useState(filterNorm);

  // 添加一个筛选条件
  const addClick = (key: number) => {
    if (filterList.length >= filterNorm.length) return;
    const newFilterList = JSON.parse(JSON.stringify(filterList));
    const getDate = new Date().getTime();
    newFilterList.push({ id: getDate, firstLevel: '', secondLevel: null });
    setFilterList(newFilterList);
  };

  // 减少一个筛选条件
  const reduceClick = (key: number) => {
    const newFilterList = filterList.filter((item, index) => {
      return item.id !== key;
    });
    // 取消已清除的指标的禁用
    const filterNewFilterList = newFilterList.map((item: any) => item.firstLevel);
    const newfilterNormList = JSON.parse(JSON.stringify(filterNorm)).map((item: any) => {
      if (filterNewFilterList.includes(item.value)) {
        return { ...item, disabled: true };
      }
      return item;
    });
    newfilterNormList.sort((a: any, b: any) => a.disabled - b.disabled);

    setFilterList(newFilterList);
    setFilterNormList(newfilterNormList);
  };

  // 第一列下拉操作
  const firstLevelChange = (value: any, rowId: number) => {
    const newFilterList = JSON.parse(JSON.stringify(filterList));
    const newFilterListIndex = newFilterList.findIndex((item: any) => item.id === rowId);
    newFilterList[newFilterListIndex].firstLevel = value;

    // 控制已选中的指标无法再次被选中
    const filterNewFilterList = newFilterList.map((item: any) => item.firstLevel);
    const newfilterNormList = JSON.parse(JSON.stringify(filterNorm)).map((item: any) => {
      if (filterNewFilterList.includes(item.value)) {
        return { ...item, disabled: true };
      }
      return item;
    });
    newfilterNormList.sort((a: any, b: any) => a.disabled - b.disabled);

    setFilterList(newFilterList);
    setFilterNormList(newfilterNormList);
  };

  // 第二列下拉操作
  const secondLevelChange = (value: any, rowId: number) => {
    const newFilterList = JSON.parse(JSON.stringify(filterList));
    const newFilterListIndex = newFilterList.findIndex((item: any) => item.id === rowId);
    newFilterList[newFilterListIndex].secondLevel = value;
    setFilterList(newFilterList);
  };

  const submitClick = () => {
    const newFilterList = filterList.filter((item: any) => {
      return item.firstLevel && (item.secondLevel === 0 || item.secondLevel);
    });
    props?.getNorms(newFilterList);
    onClose();
  };

  const onClose = () => {
    // 控制已选中的指标无法再次被选中
    if (props?.filterList && props?.filterList.length > 0) {
      const filterNewFilterList = props?.filterList.map((item: any) => item.firstLevel);
      const newfilterNormList = JSON.parse(JSON.stringify(filterNorm)).map((item: any) => {
        if (filterNewFilterList.includes(item.value)) {
          return { ...item, disabled: true };
        }
        return item;
      });
      newfilterNormList.sort((a: any, b: any) => a.disabled - b.disabled);
      setFilterList(props?.filterList);
      setFilterNormList(newfilterNormList);
    } else {
      setFilterList([
        {
          id: 0,
          firstLevel: null,
          secondLevel: null,
        },
      ]);
      setFilterNormList(filterNorm);
    }
    setVisible(false);
  };

  useEffect(() => {
    // 控制已选中的指标无法再次被选中
    if (props?.filterList && props?.filterList.length > 0) {
      const filterNewFilterList = props?.filterList.map((item: any) => item.firstLevel);
      const newfilterNormList = JSON.parse(JSON.stringify(filterNorm)).map((item: any) => {
        if (filterNewFilterList.includes(item.value)) {
          return { ...item, disabled: true };
        }
        return item;
      });
      newfilterNormList.sort((a: any, b: any) => a.disabled - b.disabled);
      setFilterList(props?.filterList);
      setFilterNormList(newfilterNormList);
    } else {
      setFilterList([
        {
          id: 0,
          firstLevel: null,
          secondLevel: null,
        },
      ]);
      setFilterNormList(filterNorm);
    }
  }, [props?.filterList]);

  return (
    <Popover
      placement="bottomLeft"
      overlayClassName={balancePrefix}
      trigger={'click'}
      title={
        <div className={`${balancePrefix}-title`}>
          <div style={{ fontSize: '16px' }}>{props?.title}</div>
          <Button type="text" icon={<CloseOutlined className="close-icon" />} onClick={onClose} />
        </div>
      }
      visible={visible}
      onVisibleChange={(visible) => setVisible(visible)}
      destroyTooltipOnHide
      content={
        <div className={`${balancePrefix}-container`}>
          <div className={`${balancePrefix}-container-main`}>
            <div>
              {filterList &&
                filterList.length > 0 &&
                filterList.map((item, index) => {
                  return (
                    <Row key={item.id} style={{ padding: '9px 0' }}>
                      {index !== 0 && (
                        <Col span={2}>
                          <span className={`${balancePrefix}-container-main-andlink`}>
                            <span>and</span>
                          </span>
                        </Col>
                      )}
                      <Col span={index !== 0 ? 10 : 12}>
                        <Select
                          size="small"
                          value={item.firstLevel || null}
                          placeholder="请选择"
                          style={{ width: index !== 0 ? 148 : 180 }}
                          options={filterNormList}
                          onChange={(e) => firstLevelChange(e, item.id)}
                        ></Select>
                      </Col>
                      <Col span={10}>
                        <Select
                          size="small"
                          value={item.secondLevel}
                          placeholder="请选择"
                          options={isBalance}
                          style={{ width: 150 }}
                          onChange={(e) => secondLevelChange(e, item.id)}
                        ></Select>
                      </Col>
                      <Col className={`${balancePrefix}-container-main-option`} span={2}>
                        <span onClick={() => addClick(item.id)}>
                          <IconFont type="icon-jiahao" />
                        </span>
                        {index !== 0 && (
                          <span onClick={() => reduceClick(item.id)}>
                            <IconFont type="icon-jian" />
                          </span>
                        )}
                      </Col>
                    </Row>
                  );
                })}
            </div>
            <div className={`${balancePrefix}-container-main-footer`}>
              <Button onClick={onClose} size="small">
                取消
              </Button>
              <Button onClick={submitClick} style={{ marginLeft: '10px' }} size="small" type="primary">
                确定
              </Button>
            </div>
          </div>
        </div>
      }
    >
      <Button icon={<IconFont type="icon-shaixuan" />}>筛选列表</Button>
    </Popover>
  );
};
