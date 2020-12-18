// import * as React from 'react';
// import CodeMirror from 'codemirror/lib/codemirror';
// import 'codemirror/lib/codemirror.css';
// import 'codemirror/mode/sql/sql';
// import 'codemirror/mode/javascript/javascript';
// import 'codemirror/addon/hint/show-hint.js';
// import 'codemirror/addon/hint/sql-hint.js';
// import 'codemirror/addon/hint/show-hint.css';
// import './index.less';
// import { indexStore } from 'store/my-index';

// interface IProps {
//   value?: string;
//   placeholder?: string;
//   readOnly?: boolean;
// }
// export class CodeMirrorEditor extends React.Component<IProps> {

//   public editor = null as any;

//   public handleCodeFocus = () => {
//     // tslint:disable-next-line:no-unused-expression
//     this.editor && this.editor.focus();
//   }

//   public componentDidMount() {
//     const { value, placeholder, readOnly } = this.props;
//     const code = document.querySelector('.codemirror');
//     code.innerHTML = '';
//     const editor = CodeMirror(document.querySelector('.codemirror'), {
//       mode: 'application/json',
//       indentWithTabs: true,
//       smartIndent: true,
//       lineNumbers: true,
//       matchBrackets: true,
//       autoCloseBrackets: true,
//       styleSelectedText: true,
//       foldGutter: true,
//       readOnly,
//       extraKeys: readOnly ? {} : {
//         'Ctrl-Enter': 'autocomplete',
//         'Tab': (cm) => {
//           const spaces = Array(cm.getOption('indentUnit') + 1).join(' ');
//           cm.replaceSelection(spaces);
//         },
//       },
//       placeholder,
//     });
//     editor.setValue(value || '');
//     indexStore.setCodeEditorValue(value || '');
//     editor.on('changes', (a: any) => {
//       const data = a.getValue();
//       indexStore.setCodeEditorValue(data);
//     });
//     this.editor = editor;
//   }

//   public render() {
//     return (
//       <div
//         className="editor-wrap"
//         onClick={this.handleCodeFocus}
//       >
//         <div className="codemirror" />
//       </div >
//     );
//   }
// }
