import React, { useState } from 'react';
import { Controlled as CodeMirror } from 'react-codemirror2';
import SwitchTab from '@src/components/SwitchTab';

const isJSON = (str: string) => {
  if (typeof str == 'string') {
    try {
      JSON.parse(str);
      return true;
    } catch (e) {
      return false;
    }
  } else {
    return false;
  }
};

const ZKData = ({ nodeData }: { nodeData: string }) => {
  const [showMode, setShowMode] = useState('default');
  return (
    <>
      <div className="zk-detail-layout-right-content-format">
        <SwitchTab defaultKey={showMode} onChange={(key) => setShowMode(key)}>
          <SwitchTab.TabItem key="default">
            <div style={{ padding: '0 10px' }}>原始格式</div>
          </SwitchTab.TabItem>
          <SwitchTab.TabItem key="JSON">
            <div style={{ padding: '0 10px' }}>JSON格式</div>
          </SwitchTab.TabItem>
        </SwitchTab>
      </div>
      {showMode === 'default' && (
        <div className={'zk-detail-layout-right-content-data'}>
          {isJSON(nodeData) ? JSON.stringify(JSON.parse(nodeData), null, 2) : nodeData}
        </div>
      )}
      {showMode === 'JSON' && (
        <div className={'zk-detail-layout-right-content-code'}>
          <CodeMirror
            className={'zk-detail-layout-right-content-code-data'}
            value={isJSON(nodeData) ? JSON.stringify(JSON.parse(nodeData), null, 2) : nodeData}
            options={{
              mode: 'application/json',
              lineNumbers: true,
              lineWrapper: true,
              autoCloseBrackets: true,
              smartIndent: true,
              tabSize: 2,
            }}
            onBeforeChange={() => {
              return;
            }}
          />
        </div>
      )}
    </>
  );
};

export default ZKData;
