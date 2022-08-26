import React, { useState, useEffect } from 'react';
import { Radio, Input, Popover, Space, Checkbox, Row, Col, Button, IconFont } from 'knowdesign';
import { InodeScopeModule } from './index';
import './style/node-scope.less';

interface propsType {
  change: Function;
  nodeScopeModule: InodeScopeModule;
}

const OptionsDefault = [
  {
    label: 'Top 5',
    value: 5,
  },
  {
    label: 'Top 10',
    value: 10,
  },
  {
    label: 'Top 15',
    value: 15,
  },
];

const NodeScope = ({ nodeScopeModule, change }: propsType) => {
  const {
    customScopeList: customList,
    scopeName = '自定义节点范围',
    showSearch = false,
    searchPlaceholder = '输入内容进行搜索',
  } = nodeScopeModule;
  const [topNum, setTopNum] = useState<number>(5);
  const [isTop, setIsTop] = useState(true);
  const [audioOptions, setAudioOptions] = useState(OptionsDefault);
  const [scopeSearchValue, setScopeSearchValue] = useState('');
  const [inputValue, setInputValue] = useState<string>(null);
  const [indeterminate, setIndeterminate] = useState(false);
  const [popVisible, setPopVisible] = useState(false);
  const [checkAll, setCheckAll] = useState(false);
  const [checkedListTemp, setCheckedListTemp] = useState([]);
  const [checkedList, setCheckedList] = useState([]);
  const [allCheckedList, setAllCheckedList] = useState([]);

  useEffect(() => {
    const all = customList?.map((item) => item.value) || [];
    setAllCheckedList(all);
  }, [customList]);

  useEffect(() => {
    if (topNum) {
      const timeOption = audioOptions.find((item) => item.value === topNum);

      setInputValue(timeOption?.label);
      setCheckedListTemp([]);
      setCheckedList([]);

      setPopVisible(false);
    }
  }, [topNum]);

  useEffect(() => {
    setIndeterminate(!!checkedListTemp.length && checkedListTemp.length < allCheckedList.length);
    setCheckAll(checkedListTemp?.length === allCheckedList.length);
  }, [checkedListTemp]);

  const customSure = () => {
    if (checkedListTemp?.length > 0) {
      setCheckedList(checkedListTemp);
      change(checkedListTemp, false);
      setIsTop(false);
      setTopNum(null);
      setInputValue(`已选${checkedListTemp?.length}项`);
      setPopVisible(false);
    }
  };

  const customCancel = () => {
    setCheckedListTemp(checkedList);
    setPopVisible(false);
  };

  const visibleChange = (visible: any) => {
    setCheckedListTemp(checkedList);
    setPopVisible(visible);
  };

  const periodtimeChange = (e: any) => {
    const topNum = e.target.value;
    setTopNum(topNum);
    change(topNum, true);
    setIsTop(true);
  };

  const onCheckAllChange = (e: any) => {
    setCheckedListTemp(e.target.checked ? allCheckedList : []);
    setIndeterminate(false);
    setCheckAll(e.target.checked);
  };

  const checkChange = (val: any) => {
    setCheckedListTemp(val);
    // setIndeterminate(!!val.length && val.length < allCheckedList.length);
    // setCheckAll(val?.length === allCheckedList.length);
  };

  const clickContent = (
    <div className="dd-node-scope-module">
      {/* <span>时间：</span> */}
      <div className="flx_con">
        <div className="flx_l">
          <h6 className="time_title">选择top范围</h6>
          <Radio.Group
            optionType="button"
            buttonStyle="solid"
            className="topNum-radio-group"
            // options={audioOptions}
            onChange={periodtimeChange}
            value={topNum}
          >
            <Space direction="vertical" size={16}>
              {audioOptions.map((item, index) => (
                <Radio value={item.value} key={index}>
                  {item.label}
                </Radio>
              ))}
            </Space>
          </Radio.Group>
        </div>
        <div className="flx_r">
          <h6 className="time_title">{scopeName}</h6>
          <div className="custom-scope">
            <div className="check-row">
              <Checkbox className="check-all" indeterminate={indeterminate} onChange={onCheckAllChange} checked={checkAll}>
                全选
              </Checkbox>
              <Input
                className="search-input"
                suffix={
                  <IconFont type="icon-fangdajing" style={{ fontSize: '16px' }} />
                }
                size="small"
                placeholder={searchPlaceholder}
                onChange={(e) => setScopeSearchValue(e.target.value)}
              />
            </div>
            <div className="fixed-height">
              <Checkbox.Group style={{ width: '100%' }} onChange={checkChange} value={checkedListTemp}>
                <Row gutter={[10, 12]}>
                  {customList
                    .filter((item) => !showSearch || item.label.includes(scopeSearchValue))
                    .map((item) => (
                      <Col span={12} key={item.value}>
                        <Checkbox value={item.value}>{item.label}</Checkbox>
                      </Col>
                    ))}
                </Row>
              </Checkbox.Group>
            </div>

            <div className="btn-con">
              <Button
                type="primary"
                size="small"
                className="btn-sure"
                onClick={customSure}
                disabled={checkedListTemp?.length > 0 ? false : true}
              >
                确定
              </Button>
              <Button size="small" onClick={customCancel}>
                取消
              </Button>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
  return (
    <>
      <div id="d-node-scope">
        <Popover
          trigger={['click']}
          visible={popVisible}
          content={clickContent}
          placement="bottomRight"
          overlayClassName="d-node-scope-popover"
          onVisibleChange={visibleChange}
        >
          <span className="input-span">
            <Input
              className={isTop ? 'relativeTime d-node-scope-input' : 'absoluteTime d-node-scope-input'}
              value={inputValue}
              readOnly={true}
              suffix={<IconFont type="icon-jiantou1" rotate={90} style={{ color: '#74788D' }}></IconFont>}
            />
          </span>
        </Popover>
      </div>
    </>
  );
};

export default NodeScope;
