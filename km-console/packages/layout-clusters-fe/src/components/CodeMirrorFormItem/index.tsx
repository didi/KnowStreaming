import React, { useEffect, useState } from 'react';
// 引入代码编辑器
import { Controlled as CodeMirror } from 'react-codemirror2';
import 'codemirror/lib/codemirror.css';
//代码高亮
import 'codemirror/addon/edit/matchbrackets';
import 'codemirror/addon/selection/active-line';
import 'codemirror/addon/edit/closebrackets';
import 'codemirror/addon/display/placeholder';
require('codemirror/mode/xml/xml');
require('codemirror/mode/javascript/javascript');
import './index.less';

interface PropsType {
  defaultInput: string;
  placeholder?: string;
  resize?: boolean;
  onBeforeChange: (value: string) => void;
  onBlur?: (value: string) => void;
}

const CodeMirrorFormItem = (props: PropsType): JSX.Element => {
  const { defaultInput, placeholder = '请输入内容', resize = false, onBeforeChange: changeCallback, onBlur } = props;
  const [input, setInput] = useState('');

  useEffect(() => {
    let formattedInput = '';
    try {
      formattedInput = JSON.stringify(JSON.parse(defaultInput), null, 2);
    } catch (e) {
      formattedInput = defaultInput;
    }
    setInput(formattedInput);
  }, [defaultInput]);

  const blur = (value: any) => {
    onBlur && onBlur(value);
  };

  return (
    <CodeMirror
      className={`codemirror-form-item ${resize ? 'codemirror-form-item-resize' : ''}`}
      value={input}
      options={{
        mode: 'application/json',
        lineNumbers: true,
        lineWrapper: true,
        autoCloseBrackets: true,
        smartIndent: true,
        tabSize: 2,
        placeholder,
      }}
      onBlur={blur}
      onBeforeChange={(editor, data, value) => {
        changeCallback(value);
        setInput(value);
      }}
    />
  );
};

export default CodeMirrorFormItem;
