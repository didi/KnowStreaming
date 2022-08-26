import React, { useLayoutEffect, useRef, useState } from 'react';
import './index.less';

interface SwitchTabProps {
  defaultKey: string;
  onChange: (key: string) => void;
  children: any;
}

interface TabItemProps {
  key: string;
  children: JSX.Element;
}

const TabItem = (props: TabItemProps) => {
  const { key, children } = props;
  return <div key={key}>{children}</div>;
};

const SwitchTab = (props: SwitchTabProps) => {
  const { defaultKey, onChange, children } = props;
  const tabRef = useRef();
  const [activeKey, setActiveKey] = useState<string>(defaultKey);
  const [pos, setPos] = useState({
    left: 0,
    width: 0,
  });

  useLayoutEffect(() => {
    if (tabRef.current) {
      [...(tabRef?.current as HTMLDivElement)?.children].some((node: HTMLElement) => {
        if (node.className.includes('active')) {
          setPos({
            left: node?.offsetLeft || 0,
            width: node?.offsetWidth || 0,
          });
          return true;
        }
        return false;
      });
    }
  }, [activeKey]);

  return (
    <div ref={tabRef} className="d-switch-tab">
      {children.map((content: any) => {
        const key = content.key;
        return (
          <div
            key={key}
            className={`d-switch-tab-content d-switch-tab-content-${activeKey === key ? 'active' : ''}`}
            onClick={() => {
              setActiveKey(key);
              onChange(key);
            }}
          >
            {content}
          </div>
        );
      })}
      <div className="d-switch-tab-bar" style={{ ...pos }}></div>
    </div>
  );
};

SwitchTab.TabItem = TabItem;

export default SwitchTab;
