import { DoubleRightOutlined } from '@ant-design/icons';
import { Checkbox } from 'knowdesign';
import { debounce } from 'lodash';
import React, { useEffect } from 'react';

const CheckboxGroup = Checkbox.Group;

interface IVersion {
  firstLine: string[];
  leftVersions: string[];
}

const CustomCheckGroup = (props: { kafkaVersions: string[]; onChangeCheckGroup: any }) => {
  const { kafkaVersions, onChangeCheckGroup } = props;
  const [checkedKafkaVersion, setCheckedKafkaVersion] = React.useState<IVersion>({
    firstLine: [],
    leftVersions: [],
  });
  const [allVersion, setAllVersion] = React.useState<IVersion>({
    firstLine: [],
    leftVersions: [],
  });

  const [indeterminate, setIndeterminate] = React.useState(false);
  const [checkAll, setCheckAll] = React.useState(true);
  const [moreGroupWidth, setMoreGroupWidth] = React.useState(400);
  const [showMore, setShowMore] = React.useState(false);

  useEffect(() => {
    document.addEventListener('click', handleDocumentClick);
    return () => {
      document.removeEventListener('click', handleDocumentClick);
    };
  }, []);

  const handleDocumentClick = (e: Event) => {
    setShowMore(false);
  };

  const setCheckAllStauts = (list: string[], otherList: string[]) => {
    onChangeCheckGroup([...list, ...otherList]);
    setIndeterminate(!!list.length && list.length + otherList.length < kafkaVersions.length);
    setCheckAll(list.length + otherList.length === kafkaVersions.length);
  };

  const getTwoPanelVersion = () => {
    const width = (document.getElementsByClassName('custom-check-group')[0] as any)?.offsetWidth;
    const checkgroupWidth = width - 100 - 86;
    const num = (checkgroupWidth / 108) | 0;
    const firstLine = Array.from(kafkaVersions).splice(0, num);
    setMoreGroupWidth(num * 108 + 88 + 66);
    const leftVersions = Array.from(kafkaVersions).splice(num);
    return { firstLine, leftVersions };
  };

  const onFirstVersionChange = (list: []) => {
    setCheckedKafkaVersion({
      ...checkedKafkaVersion,
      firstLine: list,
    });

    setCheckAllStauts(list, checkedKafkaVersion.leftVersions);
  };

  const onLeftVersionChange = (list: []) => {
    setCheckedKafkaVersion({
      ...checkedKafkaVersion,
      leftVersions: list,
    });
    setCheckAllStauts(list, checkedKafkaVersion.firstLine);
  };

  const onCheckAllChange = (e: any) => {
    const versions = getTwoPanelVersion();

    setCheckedKafkaVersion(
      e.target.checked
        ? versions
        : {
            firstLine: [],
            leftVersions: [],
          }
    );
    onChangeCheckGroup(e.target.checked ? [...versions.firstLine, ...versions.leftVersions] : []);

    setIndeterminate(false);
    setCheckAll(e.target.checked);
  };

  React.useEffect(() => {
    const handleVersionLine = () => {
      const versions = getTwoPanelVersion();
      setAllVersion(versions);
      setCheckedKafkaVersion(versions);
    };
    handleVersionLine();

    window.addEventListener('resize', handleVersionLine); //监听窗口大小改变
    return () => window.removeEventListener('resize', debounce(handleVersionLine, 500));
  }, []);

  return (
    <>
      <div className="custom-check-group" onClick={(e) => e.nativeEvent.stopImmediatePropagation()}>
        <div>
          <Checkbox className="check-all" indeterminate={indeterminate} onChange={onCheckAllChange} checked={checkAll}>
            全选
          </Checkbox>
        </div>
        <CheckboxGroup options={allVersion.firstLine} value={checkedKafkaVersion.firstLine} onChange={onFirstVersionChange} />
        {showMore ? (
          <CheckboxGroup
            style={{ width: moreGroupWidth }}
            className="more-check-group"
            options={allVersion.leftVersions}
            value={checkedKafkaVersion.leftVersions}
            onChange={onLeftVersionChange}
          />
        ) : null}
        {allVersion.leftVersions.length ? (
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
