import { DownOutlined } from '@ant-design/icons';
import { Popover } from 'knowdesign';
import { TooltipPlacement } from 'knowdesign/es/basic/tooltip';
import React, { useState, useRef, useEffect } from 'react';
import './index.less';

type PropsType = {
  list: string[];
  expandTagContent: string | ((tagNum: number) => string);
  placement?: TooltipPlacement;
};

type TagsState = {
  list: string[];
  isHideExpandNode: boolean;
  endI: number;
  calculated: boolean;
};

// 获取 DOM 元素横向 margin 值
const getNodeMargin = (node: Element) => {
  const nodeStyle = window.getComputedStyle(node);
  return [nodeStyle.marginLeft, nodeStyle.marginRight].reduce((pre, cur) => {
    return pre + Number(cur.slice(0, -2));
  }, 0);
};

// TODO: 页面宽度变化时重新计算
export default (props: PropsType) => {
  const { list = [], expandTagContent, placement = 'bottomRight' } = props;
  list.sort();
  const ref = useRef(null);
  const [curState, setCurState] = useState<TagsState>({
    list,
    isHideExpandNode: true,
    endI: -1,
    calculated: false,
  });
  const [nextTagsList, setNextTagsList] = useState(list);

  useEffect(() => {
    const f = () => {
      // 父盒子信息
      const box = ref.current;
      const boxWidth = box.offsetWidth;
      // 子元素信息
      const childrenList = Array.from(ref.current.children) as HTMLElement[] as any;
      const len = childrenList.length;
      const penultimateNode = childrenList[len - 2];
      const penultimateNodeOffsetRight = penultimateNode.offsetLeft + penultimateNode.offsetWidth - box.offsetLeft;
      // 如果内容超出展示区域，隐藏一部分
      if (penultimateNodeOffsetRight > boxWidth) {
        const lastNode = childrenList[len - 1];
        const childrenMarin = getNodeMargin(penultimateNode);
        let curWidth = lastNode.offsetWidth + getNodeMargin(lastNode);
        childrenList.some((children: any, i: number) => {
          // 计算下一个元素的宽度
          const extraWidth = children.offsetWidth + childrenMarin;
          // 如果加入下个元素后宽度未超出，则继续
          if (curWidth + extraWidth < boxWidth) {
            curWidth += extraWidth;
            return false;
          } else {
            // 否则记录当前索引值 i ，并退出
            setCurState({ list: nextTagsList, isHideExpandNode: false, endI: i, calculated: true });
            return true;
          }
        });
      } else {
        // 隐藏 展示全部 对应的 DOM 元素
        setCurState({ list: nextTagsList, isHideExpandNode: true, endI: -1, calculated: true });
      }
    };

    // 在 setTimeout 中执行，保证拿到元素此时已经渲染到页面上，能够拿到正确的数据
    setTimeout(() => f());
  }, [nextTagsList]);

  useEffect(() => {
    // 判断数据是否一致
    if (list.length !== nextTagsList.length || nextTagsList.some((item, i) => item !== list[i])) {
      setNextTagsList(list);
      setCurState({ list, isHideExpandNode: true, endI: -1, calculated: false });
    }
  }, [list]);

  return (
    <div className="list-with-hide-container" ref={ref}>
      {curState.list.map((item, i) => {
        return (
          <div
            key={i}
            className={`container-item ${
              curState.calculated ? (curState.isHideExpandNode ? 'show' : i >= curState.endI ? 'hide' : 'show') : ''
            }`}
          >
            {item}
          </div>
        );
      })}
      <Popover
        placement={placement}
        overlayClassName="tags-with-hide-popover"
        content={
          <div className="container-item-popover">
            {curState.list.map((id) => (
              <div key={id} className="container-item">
                {id}
              </div>
            ))}
          </div>
        }
      >
        <div className={`expand-item ${curState.calculated ? (curState.isHideExpandNode ? 'hide' : 'show') : ''}`}>
          {typeof expandTagContent === 'string' ? expandTagContent : expandTagContent(curState.list.length)}
          <DownOutlined />
        </div>
      </Popover>
    </div>
  );
};
