import * as React from 'react';
import * as monaco from 'monaco-editor';

import './index.less';

export interface IEditorProps {
  style?: React.CSSProperties;
  options: monaco.editor.IStandaloneEditorConstructionOptions;
  uri?: monaco.Uri;
  autoUnmount?: boolean;
  customMount?: (editor: monaco.editor.IStandaloneCodeEditor, monaco: any) => any;
  placeholder?: string;
}

export class EditorCom extends React.Component<IEditorProps> {
  public ref: HTMLElement = null;
  public editor: monaco.editor.IStandaloneCodeEditor;
  public state = {
    placeholder: this.props.placeholder ?? '',
  };

  public componentWillUnmount() {
    if (this.props.autoUnmount === false) return;
    const model = this.editor.getModel();
    model.dispose();
    this.editor.dispose();
  }

  public componentDidMount() {
    const { customMount, options, uri } = this.props;
    const { value, language } = options;
    if (uri) {
      options.model = monaco.editor.createModel(value, language, uri);
    }

    this.editor = monaco.editor.create(this.ref,
      options,
    );
    if (customMount) customMount(this.editor, monaco);
  }

  public render() {
    const { style } = this.props;
    return (
      <>
        <div style={style} className="editor" ref={(id) => { this.ref = id; }} />
      </>
    );
  }
}
