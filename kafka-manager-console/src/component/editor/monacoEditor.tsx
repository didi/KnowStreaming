import * as React from 'react';
import * as monaco from 'monaco-editor';
import format2json from 'format-to-json';
import { Input } from 'component/antd';
import './index.less';

export interface IEditorProps {
  style?: React.CSSProperties;
  options: monaco.editor.IStandaloneEditorConstructionOptions;
  uri?: monaco.Uri;
  autoUnmount?: boolean;
  customMount?: (editor: monaco.editor.IStandaloneCodeEditor, monaco: any) => any;
  placeholder?: string;
  value: '';
  onChange?: any;
}

class Monacoeditor extends React.Component<IEditorProps> {
  public ref: HTMLElement = null;
  public editor: monaco.editor.IStandaloneCodeEditor;
  public state = {
    placeholder: '',
  };
  // public arr = '{"clusterId":95,"startId":37397856,"step":100,"topicName":"kmo_topic_metrics_tempory_zq"}';
  // public Ars(a: string) {
  //   const obj = JSON.parse(a);
  //   const newobj: any = {};
  //   for (const item in obj) {
  //     if (typeof obj[item] === 'object') {
  //       this.Ars(obj[item]);
  //     } else {
  //       newobj[item] = obj[item];
  //     }
  //   }
  //   return JSON.stringify(newobj);
  // }
  public async componentDidMount() {
    const { value, onChange } = this.props;
    const format: any = await format2json(value);
    this.editor = monaco.editor.create(this.ref, {
      value: format.result,
      language: 'json',
      lineNumbers: 'off',
      scrollBeyondLastLine: false,
      // selectOnLineNumbers: true,
      // roundedSelection: false,
      // readOnly: true,
      minimap: {
        enabled: false,
      },
      // automaticLayout: true, // 自动布局
      glyphMargin: true, // 字形边缘 {},[]
      // useTabStops: false,
      // formatOnPaste: true,
      // mode: 'application/json',
      // indentWithTabs: true,
      // smartIndent: true,
      // matchBrackets: 'always',
      // autoCloseBrackets: true,
      // styleSelectedText: true,
      // foldGutter: true,
    });
    this.editor.onDidChangeModelContent((e) => {
      const newValue = this.editor.getValue();
      onChange(newValue);
    });
  }
  public render() {
    return (
      <div className="monacoEditor ant-input" >
        <Input style={{ display: 'none' }} {...this.props} />
        <div className="editor" {...this.props} ref={(id) => { this.ref = id; }} />
      </div>
    );
  }
}
export default Monacoeditor;
