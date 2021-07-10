import * as React from 'react';
import { Form, Row, Button, Input, DatePicker, Col, Select, Radio, Alert, notification, Tooltip } from 'component/antd';
import { topic } from 'store/topic';
import { consume } from 'store/consume';
import { observer } from 'mobx-react';
import { resetOffset } from 'lib/api';
import { timeMinute } from 'constants/strategy';
import { searchProps } from 'constants/table';
import { disabledDate, disabledDateTime } from 'lib/utils';
import moment, { Moment } from 'moment';
import './index.less';

@observer
class ResetOffset extends React.Component<any> {

  public state = {
    typeValue: 'time',
    offsetValue: 'offset',
  };

  constructor(props: any) {
    super(props);
  }

  public handleSubmit = (e: React.MouseEvent<any, MouseEvent>) => {
    e.preventDefault();
    const { typeValue, offsetValue } = this.state;
    if (typeValue === 'time') {
      this.props.form.validateFields((err: Error, values: any) => {
        if (err) return;
        const { timestamp } = values;
        this.props.OffsetReset.offsetList = [];
        if (offsetValue === 'offset') {
          this.props.OffsetReset.offsetPos = 2;
          this.props.OffsetReset.timestamp = 0;
        } else {
          this.props.OffsetReset.offsetPos = 0;
          this.props.OffsetReset.timestamp = +moment(timestamp).format('x');
        }
        resetOffset(this.props.OffsetReset).then(data => {
          notification.success({ message: '重置时间成功' });
        });
      });
    } else {
      const offsetData = consume.offsetList.filter(ele => ele.offset || ele.partitionId);
      if (offsetData && offsetData.length) {
        this.props.OffsetReset.offsetPos = 0;
        this.props.OffsetReset.timestamp = 0;
        this.props.OffsetReset.offsetList = offsetData;
        return resetOffset(this.props.OffsetReset).then(data => {
          notification.success({ message: '重置分区及偏移成功' });
        });
      }
      return notification.warning({ message: '请先填写分区及偏移！！！' });
    }
  }

  public onChangeType = (e: any) => {
    this.setState({
      typeValue: e.target.value,
    });
  }

  public onChangeOffset = (e: any) => {
    this.setState({
      offsetValue: e.target.value,
    });
  }

  public render() {
    const { getFieldDecorator } = this.props.form;
    const { typeValue, offsetValue } = this.state;
    return (
      <>
        <Alert message="重置消费偏移前,请先关闭客户端,否则会重置失败 ！！！" type="warning" showIcon={true} />
        <Alert message="关闭客户端后,请等待一分钟之后再重置消费偏移 ！！！" type="warning" showIcon={true} />
        {/* <Alert message="重置之前一定要关闭消费客户端！！！" type="warning" showIcon={true} className="mb-30" /> */}
        <div className="o-container">
          <Form labelAlign="left" onSubmit={this.handleSubmit} >
            <Radio.Group onChange={this.onChangeType} value={typeValue}>
              <Radio value="time"><span className="title-con">重置到指定时间</span></Radio>
              <Row>
                <Col span={26}>
                  <Form.Item label="" >
                    <Radio.Group
                      onChange={this.onChangeOffset}
                      value={offsetValue}
                      disabled={typeValue === 'partition'}
                      defaultValue="offset"
                      className="mr-10"
                    >
                      <Radio.Button value="offset">最新offset</Radio.Button>
                      <Radio.Button value="custom">自定义</Radio.Button>
                    </Radio.Group>
                    {typeValue === 'time' && offsetValue === 'custom' &&
                      getFieldDecorator('timestamp', {
                        rules: [{ required: false, message: '' }],
                        initialValue: moment(),
                      })(
                        <DatePicker
                          showTime={true}
                          format={timeMinute}
                          disabledDate={disabledDate}
                          disabledTime={disabledDateTime}
                          style={{ width: '50%' }}
                        />,
                      )}
                  </Form.Item>
                </Col>
              </Row>
              <Radio value="partition"><span className="title-con">重置指定分区及偏移</span></Radio>
            </Radio.Group>
            <Row>
              <Form.Item>
                <Row>
                  <Col span={8}>分区ID</Col>
                  <Col span={16}>偏移</Col>
                </Row>
                {consume.offsetList.map((ele, index) => {
                  return (
                    <Row key={index} gutter={16}>
                      <Col span={8}>
                        <Select
                          placeholder="请选择"
                          onChange={consume.selectChange.bind(consume, index)}
                          disabled={typeValue === 'time'}
                          {...searchProps}
                        >
                          {topic.consumeDetails.map((r, k) => {
                            return <Select.Option key={k} value={r.partitionId}>
                              {(r.partitionId + '').length > 16 ?
                                <Tooltip placement="bottomLeft" title={r.partitionId}>{r.partitionId}</Tooltip>
                                : r.partitionId}
                            </Select.Option>;
                          })}
                        </Select></Col>
                      <Col span={10}>
                        <Input
                          placeholder="请输入partition offset"
                          disabled={typeValue === 'time'}
                          onChange={consume.inputChange.bind(consume, index)}
                        />
                      </Col>
                      <Col span={6} className="b-list">
                        <Button
                          type="dashed"
                          icon="plus"
                          disabled={typeValue === 'time'}
                          onClick={consume.handleList.bind(null, null)}
                        />
                        <Button
                          type="dashed"
                          icon="minus"
                          disabled={index === 0 || typeValue === 'time'}
                          onClick={consume.handleList.bind(null, index)}
                        />
                      </Col>
                    </Row>
                  );
                })}
                <Row className="partionButton">
                  <Button type="primary" htmlType="submit" onClick={this.handleSubmit}>重置</Button>
                </Row>
              </Form.Item>
            </Row>
          </Form>
        </div>
      </>
    );
  }
}

export default Form.create<any>({ name: 'topicSample' })(ResetOffset);
