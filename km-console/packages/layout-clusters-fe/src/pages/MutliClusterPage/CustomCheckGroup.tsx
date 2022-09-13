import { DoubleRightOutlined } from '@ant-design/icons';
import { Checkbox } from 'knowdesign';
import { CheckboxValueType } from 'knowdesign/es/basic/checkbox/Group';
import { debounce } from 'lodash';
import React, { useEffect, useState } from 'react';

const CheckboxGroup = Checkbox.Group;

const CustomCheckGroup = (props: { kafkaVersions: string[]; onChangeCheckGroup: any }) => {
  const { kafkaVersions: newVersions, onChangeCheckGroup } = props;
  const [versions, setVersions] = React.useState<string[]>([]);
  const [versionsState, setVersionsState] = React.useState<{
    [key: string]: boolean;
  }>({});
  const [indeterminate, setIndeterminate] = React.useState(false);
  const [checkAll, setCheckAll] = React.useState(true);
  const [groupInfo, setGroupInfo] = useState({
    width: 400,
    num: 0,
  });
  const [showMore, setShowMore] = React.useState(false);

  const handleDocumentClick = (e: Event) => {
    setShowMore(false);
  };

  const updateGroupInfo = () => {
    const width = (document.getElementsByClassName('custom-check-group')[0] as any)?.offsetWidth;
    const checkgroupWidth = width - 100 - 86;
    const num = (checkgroupWidth / 108) | 0;
    setGroupInfo({
      width: num * 108 + 88 + 66,
      num,
    });
  };

  const getCheckedList = (
    versionState: {
      [key: string]: boolean;
    },
    filterFunc: (item: [string, boolean], i: number) => boolean
  ) => {
    return Object.entries(versionState)
      .filter(filterFunc)
      .map(([key]) => key);
  };

  const onVersionsChange = (isFirstLine: boolean, list: CheckboxValueType[]) => {
    const newVersionsState = { ...versionsState };
    Object.keys(newVersionsState).forEach((key, i) => {
      if (isFirstLine && i < groupInfo.num) {
        newVersionsState[key] = list.includes(key);
      } else if (!isFirstLine && i >= groupInfo.num) {
        newVersionsState[key] = list.includes(key);
      }
    });
    const checkedLen = Object.values(newVersionsState).filter((v) => v).length;

    setVersionsState(newVersionsState);
    setIndeterminate(checkedLen && checkedLen < newVersions.length);
    setCheckAll(checkedLen === newVersions.length);
    onChangeCheckGroup(getCheckedList(newVersionsState, ([, state]) => state));
  };

  const onCheckAllChange = (e: any) => {
    const checked = e.target.checked;
    const newVersionsState = { ...versionsState };
    Object.keys(newVersionsState).forEach((key) => (newVersionsState[key] = checked));

    setVersionsState(newVersionsState);
    setIndeterminate(false);
    setCheckAll(checked);
    onChangeCheckGroup(e.target.checked ? versions : []);
  };

  useEffect(() => {
    const newVersionsState = { ...versionsState };
    Object.keys(newVersionsState).forEach((key) => {
      if (!newVersions.includes(key)) {
        delete newVersionsState[key];
      }
    });
    newVersions.forEach((version) => {
      if (!Object.keys(newVersionsState).includes(version)) {
        newVersionsState[version] = true;
      }
    });
    const checkedLen = Object.values(newVersionsState).filter((v) => v).length;

    setVersions([...newVersions]);
    setVersionsState(newVersionsState);
    setIndeterminate(checkedLen && checkedLen < newVersions.length);
    setCheckAll(checkedLen === newVersions.length);
    onChangeCheckGroup(getCheckedList(newVersionsState, ([, state]) => state));
  }, [newVersions]);

  useEffect(() => {
    updateGroupInfo();
    const listen = debounce(updateGroupInfo, 500);
    window.addEventListener('resize', listen); //监听窗口大小改变
    document.addEventListener('click', handleDocumentClick);
    return () => {
      window.removeEventListener('resize', listen);
      document.removeEventListener('click', handleDocumentClick);
    };
  }, []);

  return (
    <>
      <div className="custom-check-group" onClick={(e) => e.nativeEvent.stopImmediatePropagation()}>
        <div>
          <Checkbox className="check-all" indeterminate={indeterminate} onChange={onCheckAllChange} checked={checkAll}>
            全选
          </Checkbox>
        </div>
        <CheckboxGroup
          options={Array.from(versions).splice(0, groupInfo.num)}
          value={getCheckedList(versionsState, ([, state], i) => i < groupInfo.num && state)}
          onChange={(list) => onVersionsChange(true, list)}
        />
        {showMore ? (
          <CheckboxGroup
            style={{ width: groupInfo.width }}
            className="more-check-group"
            options={Array.from(versions).splice(groupInfo.num)}
            value={getCheckedList(versionsState, ([, state], i) => i >= groupInfo.num && state)}
            onChange={(list) => onVersionsChange(false, list)}
          />
        ) : null}
        {versions.length > groupInfo.num ? (
          <div className="more-btn" onClick={() => setShowMore(!showMore)}>
            <a>
              {!showMore ? '展开更多' : '收起更多'} <DoubleRightOutlined style={{ transform: `rotate(${showMore ? '270' : '90'}deg)` }} />
            </a>
          </div>
        ) : (
          <></>
        )}
      </div>
    </>
  );
};

export default CustomCheckGroup;
