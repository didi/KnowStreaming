import React from 'react';
import { Select, Form, Input, Switch, Radio, DatePicker, Row, Col, Collapse } from 'knowdesign';
export interface CardBarProps {
  cardClumns: any[];
}
const CardBar = (props: CardBarProps) => {
  const { cardClumns } = props;
  const CardClumnsItem: any = (cardItem: any) => {
    const { cardClumnsItemData } = cardItem;
    return (
      <Row>
        <Row>
          <col>{cardClumnsItemData.icon}</col>
          <col>{cardClumnsItemData.title}</col>
        </Row>
        <Row>
          <col>{cardClumnsItemData.lable}</col>
        </Row>
      </Row>
    );
  };
  return (
    <>
      <div className="card-bar-container">
        <div className="card-bar-container">
          <div>
            <div>左侧</div>
            <div>
              <div>title</div>
              <div>
                <div>分数</div>
                <div>详情</div>
              </div>
            </div>
          </div>
          {cardClumns &&
            cardClumns.length != 0 &&
            cardClumns.map((index, item) => {
              return <CardClumnsItem key={index} cardClumnsItemData={item}></CardClumnsItem>;
            })}
        </div>
      </div>
    </>
  );
};
export default CardBar;
