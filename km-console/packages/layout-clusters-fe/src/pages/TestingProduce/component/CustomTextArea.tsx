import { PicLeftOutlined, QuestionCircleOutlined } from '@ant-design/icons';
import { Button, Input, Tooltip } from 'knowdesign';
import * as React from 'react';

interface IProps {
  value?: string;
  onChange?: any;
  tooltip?: string;
}
const CustomTextArea = (props: IProps) => {
  const { tooltip, value, onChange } = props;

  const genRandomContent = () => {
    onChange && onChange(value.trim());
  };

  const onInputChange = ({ target: { value } }: { target: { value: string } }) => {
    onChange && onChange(value.trim());
  };

  return (
    <>
      <div>
        <span>
          <Tooltip title={tooltip}>
            <QuestionCircleOutlined />
          </Tooltip>
        </span>
        <span>
          <Tooltip title={'生成随机内容'}>
            <PicLeftOutlined onClick={genRandomContent} />
          </Tooltip>
        </span>
      </div>
      <Input.TextArea value={value} onChange={onInputChange} />
    </>
  );
};

export default CustomTextArea;
