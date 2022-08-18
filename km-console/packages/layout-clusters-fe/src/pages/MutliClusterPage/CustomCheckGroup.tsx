import { DoubleRightOutlined } from '@ant-design/icons';
import { Checkbox } from 'knowdesign';
import { debounce } from 'lodash';
import React, { useEffect } from 'react';

const CheckboxGroup = Checkbox.Group;

interface IVersion {
  firstLine: string[];
  leftVersion: string[];
}

const CustomCheckGroup = (props: { kafkaVersion: string[]; onChangeCheckGroup: any }) => {
  const { kafkaVersion, onChangeCheckGroup } = props;
  const [checkedKafkaVersion, setCheckedKafkaVersion] = React.useState<IVersion>({
    firstLine: [],
    leftVersion: [],
  });
  const [allVersion, setAllVersion] = React.useState<IVersion>({
    firstLine: [],
    leftVersion: [],
  });

  const [indeterminate, setIndeterminate] = React.useState(false);
  const [checkAll, setCheckAll] = React.useState(true);
  const [moreGroupWidth, setMoreGroupWidth] = React.useState(400);
  const [showMore, setShowMore] = React.useState(false);

  useEffect(() => {
    document.addEventListener('click', handleDocumentClick);
    return () => {
      document.removeEventListener('click', handleDocumentClick);
    }
  }, []);

  const handleDocumentClick = (e: Event) => {
    setShowMore(false);
  }

  const setCheckAllStauts = (list: string[], otherList: string[]) => {
    onChangeCheckGroup([...list, ...otherList]);
    setIndeterminate(!!list.length && list.length + otherList.length < kafkaVersion.length);
    setCheckAll(list.length + otherList.length === kafkaVersion.length);
  };

  const getTwoPanelVersion = () => {
    const width = (document.getElementsByClassName('custom-check-group')[0] as any)?.offsetWidth;
    const checkgroupWidth = width - 100 - 86;
    const num = (checkgroupWidth / 108) | 0;
    const firstLine = Array.from(kafkaVersion).splice(0, num);
    setMoreGroupWidth(num * 108 + 88 + 66);
    const leftVersion = Array.from(kafkaVersion).splice(num);
    return { firstLine, leftVersion };
  };

  const onFirstVersionChange = (list: []) => {
    setCheckedKafkaVersion({
      ...checkedKafkaVersion,
      firstLine: list,
    });

    setCheckAllStauts(list, checkedKafkaVersion.leftVersion);
  };

  const onLeftVersionChange = (list: []) => {
    setCheckedKafkaVersion({
      ...checkedKafkaVersion,
      leftVersion: list,
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
          leftVersion: [],
        }
    );
    onChangeCheckGroup(e.target.checked ? [...versions.firstLine, ...versions.leftVersion] : []);

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
            options={allVersion.leftVersion}
            value={checkedKafkaVersion.leftVersion}
            onChange={onLeftVersionChange}
          />
        ) : null}
        <div className="more-btn" onClick={() => setShowMore(!showMore)}>
          <a>
            {!showMore ? '展开更多' : '收起更多'} <DoubleRightOutlined style={{ transform: `rotate(${showMore ? '270' : '90'}deg)` }} />
          </a>
        </div>
      </div>
    </>
  );
};

export default CustomCheckGroup;
