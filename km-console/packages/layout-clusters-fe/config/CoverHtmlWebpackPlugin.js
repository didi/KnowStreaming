/**
 * 重置 html 内容
 * 注意: HtmlWebpackPlugin hooks 是 beta 版本，正式版本接口可能会变
 */
const HtmlWebpackPlugin = require('html-webpack-plugin');
// const PublicPath = '//img-ys011.didistatic.com/static/bp_fe_daily/bigdata_cloud_KnowStreaming_FE/gn';
const PublicPath = '';
const isProd = process.env.NODE_ENV === 'production';
const commonDepsMap = [
  {
    name: 'react',
    development: '/static/js/react.production.min.js',
    production: `${PublicPath}/static/js/react.production.min.js`,
  },
  {
    name: 'react-dom',
    development: '/static/js/react-dom.production.min.js',
    production: `${PublicPath}/static/js/react-dom.production.min.js`,
  },
  {
    name: 'single-spa',
    development: '/static/js/single-spa.min.js',
    production: `${PublicPath}/static/js/single-spa.min.js`,
  },
  {
    name: 'single-spa-react',
    development: '/static/js/single-spa-react.js',
    production: `${PublicPath}/static/js/single-spa-react.js`,
  },
  {
    name: 'moment',
    development: '/static/js/moment.min.js',
    production: `${PublicPath}/static/js/moment.min.js`,
  },
];

function generateSystemJsImportMap() {
  const importMap = {
    'react-router': 'https://unpkg.com/react-router@5.2.1/umd/react-router.min.js',
    'react-router-dom': 'https://unpkg.com/react-router-dom@5.2.1/umd/react-router-dom.min.js',
    lodash: 'https://unpkg.com/lodash@4.17.21/lodash.min.js',
    history: 'https://unpkg.com/history@5/umd/history.development.js',
    echarts: 'https://unpkg.com/echarts@5.3.1/dist/echarts.min.js',
  };
  //if (process.env.NODE_ENV === 'production') {
  commonDepsMap.forEach((o) => {
    importMap[o.name] = o[process.env.NODE_ENV];
  });
  //}
  return JSON.stringify({
    imports: importMap,
  });
}

class CoverHtmlWebpackPlugin {
  constructor(options) {
    this.isBusiness = options.BUSINESS_VERSION;
  }
  apply(compiler) {
    compiler.hooks.compilation.tap('CoverHtmlWebpackPlugin', (compilation) => {
      HtmlWebpackPlugin.getHooks(compilation).beforeEmit.tapAsync('CoverHtmlWebpackPlugin', async (data, cb) => {
        const depsMap = `
            <script type="systemjs-importmap">
              ${generateSystemJsImportMap()}
            </script>
          `;
        const portalMap = {
          '@portal/layout': '/layout.js',
        };
        const assetJson = JSON.parse(data.plugin.assetJson);
        let links = '';

        assetJson.forEach((item) => {
          if (/\.js$/.test(item)) {
            // TODO: entry 只有一个
            portalMap['@portal/layout'] = item;
          } else if (/\.css$/.test(item)) {
            links += `<link href="${item}" rel="stylesheet">`;
          }
        });
        data.html = `
            <!DOCTYPE html>
              <html>
                <head>
                  <meta charset="utf-8" />
                  <meta name="viewport" content="width=device-width" />
                  <title></title>
                  ${links}
                  <link href='${isProd ? PublicPath : ''}/favicon.ico' rel='shortcut icon'>
                  <script src='${isProd ? PublicPath : ''}/static/js/system.min.js'></script>
                  <script src='${isProd ? PublicPath : ''}/static/js/named-exports.min.js'></script>
                  <script src='${isProd ? PublicPath : ''}/static/js/use-default.min.js'></script>
                  <script src='${isProd ? PublicPath : ''}/static/js/amd.js'></script>
                  ${this.isBusiness ? `<script src=${isProd ? PublicPath : ''}/static/js/ksl.min.js></script>` : ''}
                </head>
                <body>
                  ${depsMap}
                  <script type="systemjs-importmap">
                    {
                      "imports": ${JSON.stringify(portalMap)}
                    }
                  </script>
                  <script>
                    System.import('@portal/layout');
                  </script>
                  <div id="layout"></div>
                </body>
              </html>
            `;
        cb(null, data);
      });
    });
  }
}

module.exports = CoverHtmlWebpackPlugin;
