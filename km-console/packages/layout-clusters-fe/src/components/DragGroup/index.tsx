import { Col, Row } from 'knowdesign';
import { IconFont } from '@knowdesign/icons';
import React from 'react';
import { SortableContainer, SortableContainerProps, SortableHandle, SortableElement, SortableElementProps } from 'react-sortable-hoc';
import './index.less';

interface PropsType extends React.HTMLAttributes<HTMLDivElement> {
  children: React.ReactNode;
  sortableContainerProps?: SortableContainerProps;
  gridProps?: {
    span: number;
    gutter: [number, number];
  };
  dragItemProps?: Omit<SortableElementProps, 'index'>;
}

interface SortableItemProps {
  useDragHandle?: boolean;
  span: number;
}

// 拖拽容器
const DragContainer = SortableContainer(({ children, gutter }: any) => (
  <Row className="drag-container" gutter={gutter}>
    {children}
  </Row>
));

// 拖拽按钮
const DragHandle = SortableHandle(() => <IconFont className="drag-handle-icon" type="icon-tuozhuai1" />);

// 拖拽项
const SortableItem = SortableElement(
  ({ children, sortableItemProps }: { children: React.ReactNode; sortableItemProps: SortableItemProps }) => (
    <Col className="drag-sort-item" span={sortableItemProps.span}>
      {sortableItemProps?.useDragHandle && <DragHandle />}
      {children}
    </Col>
  )
);

const DragGroup: React.FC<PropsType> = ({ children, gridProps = { span: 8, gutter: [10, 10] }, sortableContainerProps }) => {
  return (
    <DragContainer pressDelay={0} {...sortableContainerProps} gutter={gridProps.gutter}>
      {React.Children.map(children, (child: any, index: number) => {
        // 如果传入 child 有 key 值就复用，没有的话使用 index 作为 key
        const key = typeof child === 'object' && child !== null ? child.key || index : index;

        return (
          <SortableItem
            key={key}
            index={index}
            sortableItemProps={{
              useDragHandle: sortableContainerProps.useDragHandle || false,
              span: gridProps.span,
            }}
          >
            {child}
          </SortableItem>
        );
      })}
    </DragContainer>
  );
};

export default DragGroup;
