const path = require('path');
require('dotenv').config({ path: path.resolve(process.cwd(), '../../.env') });
const webpack = require('webpack');
const merge = require('webpack-merge');
const CopyWebpackPlugin = require('copy-webpack-plugin');
const HtmlWebpackPlugin = require('html-webpack-plugin');
const getWebpackCommonConfig = require('./config/d1-webpack.base');
const CountPlugin = require('./config/CountComponentWebpackPlugin');
const isProd = process.env.NODE_ENV === 'production';
const jsFileName = isProd ? '[name]-[chunkhash].js' : '[name].js';
const outPath = path.resolve(__dirname, `../../../km-rest/src/main/resources/templates/layout`);
module.exports = merge(getWebpackCommonConfig(), {
  mode: isProd ? 'production' : 'development',
  entry: {
    layout: ['./src/index.tsx'],
  },
  plugins: [
    new CountPlugin({
      pathname: 'knowdesign',
      startCount: true,
      isExportExcel: false,
    }),
    new webpack.DefinePlugin({
      'process.env': {
        NODE_ENV: JSON.stringify(process.env.NODE_ENV),
        RUN_ENV: JSON.stringify(process.env.RUN_ENV),
        BUSINESS_VERSION: process.env.BUSINESS_VERSION === 'true',
        PUBLIC_PATH: JSON.stringify(process.env.PUBLIC_PATH),
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
    new CopyWebpackPlugin(
      [
        {
          from: path.resolve(__dirname, 'static'),
          to: path.resolve(outPath, '../static'),
        },
      ].concat(
        isProd
          ? [
              {
                from: path.resolve(__dirname, 'favicon.ico'),
                to: path.resolve(outPath, '../favicon.ico'),
              },
            ]
          : []
      )
    ),
  ],
  output: {
    path: outPath,
    publicPath: isProd ? process.env.PUBLIC_PATH + '/layout/' : '/',
    filename: jsFileName,
    chunkFilename: jsFileName,
    library: 'layout',
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
