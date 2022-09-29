import { CheckCircleFilled } from '@ant-design/icons';
import { Tooltip } from 'knowdesign';
import React, { useState } from 'react';
import CopyToClipboard from 'react-copy-to-clipboard';
import { IconFont } from '@knowdesign/icons';
import './index.less';

const ContentWithCopy = (props: { content: string }) => {
  const { content } = props;
  const [visible, setVisible] = useState(false);
  return (
    <CopyToClipboard text={content}>
      <div className="content-with-copy">
        <Tooltip title={content}>
          <span className="content">{content}</span>
        </Tooltip>
        {content && (
          <Tooltip
            title={
              <span>
                <CheckCircleFilled style={{ color: '#00b365' }} /> 复制成功
              </span>
            }
            visible={visible}
            onVisibleChange={() => setVisible(false)}
          >
            <IconFont className="copy-icon" type="icon-fuzhi" onClick={() => setVisible(true)} />
          </Tooltip>
        )}
      </div>
    </CopyToClipboard>
  );
};

export default ContentWithCopy;
