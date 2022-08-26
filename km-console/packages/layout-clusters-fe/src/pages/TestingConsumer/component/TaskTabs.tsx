/* eslint-disable react/display-name */
import { LeftOutlined, PlusOutlined, RightOutlined } from '@ant-design/icons';
import { Button, Tabs } from 'knowdesign';
import _ from 'lodash';
import * as React from 'react';
import { useIntl } from 'react-intl';
import './style/tabs.less';

interface ITab {
  label: string;
  tabpane: any;
  key: string;
  closable?: boolean;
}

interface IPagation {
  pageSize: number;
  total: number;
  pageNo: number;
}

interface IProps {
  title?: string;
  initial: ITab;
}

const { TabPane } = Tabs;
const TaskTabs = React.forwardRef((props: IProps, ref): JSX.Element => {
  const intl = useIntl();
  const { initial } = props;

  const [tabs, setTabs] = React.useState([{ ...initial }]);
  const [activeKey, setActiveKey] = React.useState('tab-1');
  const [pagination, setPagination] = React.useState<IPagation>({
    pageSize: 10,
    total: 1,
    pageNo: 1,
  });

  const onClickArrow = _.throttle((type = '') => {
    const navWrap = document.querySelector('#tabs-list .dcloud-tabs-nav-wrap') as HTMLElement;
    const navList = document.querySelector('#tabs-list .dcloud-tabs-nav-list') as HTMLElement;
    const navTab = document.querySelector('#tabs-list .dcloud-tabs-tab') as HTMLElement;

    const navWrapWidth = Number(navWrap.offsetWidth);
    const navListWidth = Number(navList.offsetWidth);
    const tagWidth = navTab ? Number(navTab.offsetWidth) + 12 : 139 + 12;
    const translateLen = Number((navList as any).style.transform.split('(')[1].split(',')[0].replace('px', ''));
    const navListScrollLeft = Number(translateLen * -1) + (pagination.pageNo === pagination.total ? 180 : 0);
    const navListScrollRight = Number(navListWidth - navWrapWidth - navListScrollLeft);
    const tabVW = tagWidth * pagination.pageSize;

    if (type === 'left') {
      if (navListScrollLeft <= 0) {
        return;
      }
      if (navListScrollLeft > tabVW) {
        navList.style.transform = `translate(${translateLen + tabVW}px, 0)`;
      } else {
        navList.style.transform = `translate(0, 0)`;
      }
    }

    if (type === 'right') {
      if (navListScrollRight <= 0) {
        return;
      }
      if (navListScrollRight >= tabVW) {
        navList.style.transform = `translate(${translateLen - tabVW}px, 0)`;
      } else {
        navList.style.transform = `translate(${navWrapWidth - navListWidth - 80}px, 0)`;
      }
    }

    setPaginationAfterClick(type);
  }, 300);

  const setPaginationAfterClick = (type: string) => {
    setPagination({
      ...pagination,
      pageNo: type === 'left' ? pagination.pageNo - 1 : pagination.pageNo + 1,
    });
  };

  const countPagination = () => {
    const navWrap = document.querySelector('#tabs-list .dcloud-tabs-nav-wrap') as HTMLElement;
    const navList = document.querySelector('#tabs-list .dcloud-tabs-nav-list') as HTMLElement;
    const navWrapWidth = Number(navWrap?.offsetWidth);
    const navListWidth = Number(navList?.offsetWidth);
    const tabNum = (navWrapWidth / (139 + 12)) | 0; // 每页可展示的数量
    const index = tabs.findIndex((tab) => tab.key === activeKey); // 当前tab所在位置

    if (navListWidth >= navWrapWidth) {
      setPagination({
        pageNo: Math.ceil((index + 1) / tabNum),
        pageSize: tabNum,
        total: Math.ceil(tabs.length / tabNum),
      });
    } else {
      setPagination({
        pageNo: 1,
        pageSize: 10,
        total: 1,
      });
      navList.style.transform = `translate(0, 0)`;
    }
  };

  React.useEffect(() => {
    countPagination();
    const resize = _.throttle(() => {
      countPagination();
    }, 300);

    const navWrap = document.querySelector('#tabs-list .dcloud-tabs-nav-wrap') as HTMLElement;
    const onMouseOverOrOut = _.throttle((event, type) => {
      if (
        !event.target?.className ||
        typeof event.target?.className !== 'string' ||
        !event.target.className.includes('dcloud-tabs-tab') ||
        event.target.className.includes('dcloud-tabs-tab-active')
      ) {
        return;
      }

      if (event.target?.firstChild?.className === 'dcloud-tabs-tab-btn') {
        const keys = event.target.firstChild.id?.split('-') || [];
        const key = `tab-${keys[keys.length - 1]}`;
        let _tabs = [].concat(tabs);

        _tabs = _tabs.map((tab) => {
          return {
            ...tab,
            closable: tab.key === key ? type === 'over' : tab.key === activeKey,
          };
        });

        setTabs([..._tabs]);
      }
    }, 300);

    const onMouseOver = (event: any) => onMouseOverOrOut(event, 'over');
    const onMouseOut = (event: any) => onMouseOverOrOut(event, 'out');

    window.addEventListener('resize', resize);

    navWrap.addEventListener('mouseover', onMouseOver);
    navWrap.addEventListener('mouseout', onMouseOut);
    return () => {
      navWrap.removeEventListener('mouseover', onMouseOver);
      navWrap.removeEventListener('mouseout', onMouseOut);
      window.removeEventListener('resize', resize);
    };
  }, [tabs, activeKey]);

  const setTabsTitle = (title: string) => {
    const _tabs = [].concat(tabs);
    const index = _tabs.findIndex((item) => item.key === activeKey);

    if (index > -1) {
      _tabs[index].label = `${title}`;
      setTabs(_tabs);
    }
  };

  React.useImperativeHandle(ref, () => ({
    setTabsTitle,
  }));

  const addTask = async () => {
    const _tabs = Array.from(tabs).map((tab) => ({
      ...tab,
      closable: false,
    }));
    const lastKey = _tabs[_tabs.length - 1].key?.split('-')[1];
    await setTabs([..._tabs, { ...initial, key: `tab-${+lastKey + 1}`, closable: true }]);
    setActiveKey(`tab-${+lastKey + 1}`);
  };

  const removeTask = async (targetKey: string) => {
    let _tabs = Array.from(tabs).filter((tab) => tab.key !== targetKey);

    _tabs = _tabs.map((row, index) => ({
      ...row,
      closable: _tabs.length !== 1 && index === _tabs.length - 1,
    }));

    await setTabs([..._tabs]);
    // 设置最后一个tab为当前tab
    setActiveKey(_tabs[_tabs.length - 1].key);
  };

  const onTabChange = async (key: string) => {
    const _tabs = [].concat(tabs).map((tab) => ({
      ...tab,
      closable: tab.key === key,
    }));

    await setTabs([..._tabs]);
    setActiveKey(key);
  };

  const onTabScroll = ({ direction }: { direction: string }) => {
  };

  const onEdit = (targetKey: string, action: string) => {
    if (action === 'remove') {
      removeTask(targetKey);
    }
  };

  const renderTabOpsSlot = () => {
    return {
      left: (
        <Button className="add-task" onClick={addTask} type="dashed" icon={<PlusOutlined color="#74788D" />}>
          {intl.formatMessage({ id: 'add.task' })}
        </Button>
      ),
      right: null as any,
    };
  };

  return (
    <>
      <div className="tabs-panel">
        <Tabs
          tabBarExtraContent={renderTabOpsSlot()}
          animated={false}
          onEdit={onEdit}
          hideAdd={true}
          type="editable-card"
          id="tabs-list"
          defaultActiveKey="tab-1"
          activeKey={activeKey}
          onChange={onTabChange}
          onTabScroll={onTabScroll}
        >
          {tabs.map((row) => (
            <TabPane tab={row.label} key={row.key} closable={row.closable}>
              {row.tabpane}
            </TabPane>
          ))}
        </Tabs>
        {pagination.total > 1 && (
          <div className="tab-nav-right">
            <LeftOutlined
              className={`tab-nav-right-icon ${pagination.pageNo <= 1 ? 'disabled' : ''}`}
              onClick={() => onClickArrow('left')}
            />
            <span className="tab-nav-right-text">{pagination.pageNo}</span>
            <span className="tab-nav-right-text-divider">/</span>
            <span className="tab-nav-right-text">{pagination.total}</span>
            <RightOutlined
              className={`tab-nav-right-icon ${pagination.pageNo >= pagination.total ? 'disabled' : ''}`}
              onClick={() => onClickArrow('right')}
            />
          </div>
        )}
      </div>
    </>
  );
});

export default TaskTabs;
