const isProd = process.env.NODE_ENV === 'production';
const HtmlWebpackPlugin = require('html-webpack-plugin');
const merge = require('webpack-merge');
const webpack = require('webpack');
const path = require('path');
// const outPath = path.resolve(__dirname, `../../pub/layout`);
const outPath = path.resolve(__dirname, `../../../km-rest/src/main/resources/templates/layout`);
const CopyWebpackPlugin = require('copy-webpack-plugin');
const getWebpackCommonConfig = require('./config/d1-webpack.base');
const config = getWebpackCommonConfig();
const CountPlugin = require('./config/CountComponentWebpackPlugin');

module.exports = merge(config, {
  mode: isProd ? 'production' : 'development',
  entry: {
    layout: ['./src/index.tsx'],
  },
  // node: {
  //   console: true,
  //   fs: true,
  //   module: 'empty',
  // },
  plugins: [
    new CountPlugin({
      pathname: '@didi/dcloud-design',
      startCount: true,
      isExportExcel: false,
    }),
    new webpack.DefinePlugin({
      'process.env': {
        NODE_ENV: JSON.stringify(process.env.NODE_ENV),
        RUN_ENV: JSON.stringify(process.env.RUN_ENV),
        BUSINESS_VERSION: getWebpackCommonConfig.BUSINESS_VERSION,
      },
    }),
    new HtmlWebpackPlugin({
      meta: {
        manifest: 'manifest.json',
      },
      template: './src/index.html',
      favicon: path.resolve('favicon.ico'),
      inject: 'body',
    }),
    isProd
      ? new CopyWebpackPlugin([
        {
          from: path.resolve(__dirname, 'static'),
          to: path.resolve(outPath, '../static'),
        },
        {
          from: path.resolve(__dirname, 'favicon.ico'),
          to: path.resolve(outPath, '../favicon.ico'),
        },
      ])
      : new CopyWebpackPlugin([
        {
          from: path.resolve(__dirname, 'static'),
          to: path.resolve(outPath, '../static'),
        },
      ]),
  ],
  output: {
    path: outPath,
    // publicPath: isProd ? '//img-ys011.didistatic.com/static/bp_fe_daily/bigdata_cloud_KnowStreaming_FE/gn/layout/' : '/',
    publicPath: isProd ? '/layout/' : '/',
    libraryTarget: 'amd',
  },
  devServer: {
    host: 'localhost',
    port: 8000,
    hot: true,
    open: true,
    openPage: 'http://localhost:8000/',
    inline: true,
    historyApiFallback: true,
    publicPath: `http://localhost:8000/`,
    headers: {
      'cache-control': 'no-cache',
      pragma: 'no-cache',
      'Access-Control-Allow-Origin': '*',
    },
    // historyApiFallback: {
    //   rewrites: [{ from: reg, to: '/agent.html' }],
    // },
    proxy: {
      '/ks-km/api/v3': {
        changeOrigin: true,
        target: 'https://api-kylin-xg02.intra.xiaojukeji.com/ks-km/',
      },
      '/logi-security/api/v1': {
        changeOrigin: true,
        target: 'https://api-kylin-xg02.intra.xiaojukeji.com/ks-km/',
      },
    },
  },

  resolve: {
    alias: {
      '@src': path.resolve(__dirname, 'src'),
    },
  },
});
