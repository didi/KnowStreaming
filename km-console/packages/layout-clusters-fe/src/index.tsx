import React from 'react';
import ReactDOM from 'react-dom';
import App from './app';
import './style-addition.less';


// function invalidModal(downloadBrowserUrl?: string) {
//   Modal.warning({
//     title: '浏览器版本过低',
//     content: (
//       <div>
//         正在使用的浏览器版本过低，将不能正常浏览本平台。为了保证更好的使用体验，请升级至Chrome 70以上版本。
//         {
//           downloadBrowserUrl
//             ? <div>
//               <a href={downloadBrowserUrl}>下载最新版本</a>
//             </div> : null
//         }
//       </div>
//     ),
//   });
// }

// if (!isValidBrowser) {
//   fetch(api.downloadBrowser).then((res) => {
//     return res.json();
//   }).then((res) => {
//     invalidModal(res.dat);
//   }).catch((e) => {
//     console.log(e);
//     invalidModal();
//   });
// }

ReactDOM.render(<App />, document.getElementById('layout'));
