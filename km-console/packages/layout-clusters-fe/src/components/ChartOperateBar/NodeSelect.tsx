import React, { useState, useEffect, useRef, PropsWithChildren } from 'react';
import { Radio, Input, Popover, Space, Checkbox, Row, Col, Button } from 'knowdesign';
import { IconFont } from '@knowdesign/icons';
import './style/node-scope.less';

interface NodeSelectProps {
  name?: string;
  onChange: (data: any, isTop: boolean) => void;
}

const TOP_SELECT_OPTIONS = [
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

const NodeSelect = ({ name, onChange, children }: PropsWithChildren<NodeSelectProps>) => {
  const [topNum, setTopNum] = useState<number>(5);
  const [isTop, setIsTop] = useState(true);
  const [audioOptions] = useState(TOP_SELECT_OPTIONS);
  const [inputValue, setInputValue] = useState<string>(null);
  const [popVisible, setPopVisible] = useState(false);

  useEffect(() => {
    if (topNum) {
      const timeOption = audioOptions.find((item) => item.value === topNum);

      setInputValue(timeOption?.label);

      setPopVisible(false);
    }
  }, [topNum]);

  const visibleChange = (visible: any) => {
    setPopVisible(visible);
  };

  const periodtimeChange = (e: any) => {
    const topNum = e.target.value;
    setTopNum(topNum);
    onChange(topNum, true);
    setIsTop(true);
  };

  const clickContent = (
    <div className="dd-node-scope-module">
      <div className="flx_con">
        <div className="flx_l">
          <h6 className="time_title">选择 top 范围</h6>
          <Radio.Group optionType="button" buttonStyle="solid" className="topNum-radio-group" onChange={periodtimeChange} value={topNum}>
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
          {children ? (
            React.Children.map(children, (child) => {
              return React.cloneElement(child as React.ReactElement<any>, {
                isTop,
                visibleChange: visibleChange,
                onChange: (data: any, inputValue: string) => {
                  onChange(data, false);
                  setIsTop(false);
                  setTopNum(null);
                  setInputValue(inputValue);
                  setPopVisible(false);
                },
              });
            })
          ) : (
            <></>
          )}
        </div>
      </div>
    </div>
  );
  return (
    <div id="d-node-scope">
      <div className="scope-title">{name}筛选：</div>
      <Popover
        trigger={['click']}
        visible={popVisible}
        content={clickContent}
        placement="bottomRight"
        overlayClassName="d-node-scope-popover large-size"
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
  );
};

export default NodeSelect;
