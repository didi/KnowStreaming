/* eslint-disable */
const path = require('path');
require('dotenv').config({ path: path.resolve(process.cwd(), '../../.env') });
const isProd = process.env.NODE_ENV === 'production';
const HtmlWebpackPlugin = require('html-webpack-plugin');
const webpack = require('webpack');
const merge = require('webpack-merge');
const pkgJson = require('./package');
const getWebpackCommonConfig = require('./config/d1-webpack.base');
const outPath = path.resolve(__dirname, `../../../km-rest/src/main/resources/templates/${pkgJson.ident}`);
const jsFileName = isProd ? '[name]-[chunkhash].js' : '[name].js';

module.exports = merge(getWebpackCommonConfig(), {
  mode: isProd ? 'production' : 'development',
  entry: {
    [pkgJson.ident]: ['./src/index.tsx'],
  },
  plugins: [
    new webpack.DefinePlugin({
      'process.env': {
        NODE_ENV: JSON.stringify(process.env.NODE_ENV),
        RUN_ENV: JSON.stringify(process.env.RUN_ENV),
      },
    }),
    new HtmlWebpackPlugin({
      meta: {
        manifest: 'manifest.json',
      },
      template: './src/index.html',
      inject: 'body',
    }),
  ],
  output: {
    path: outPath,
    publicPath: isProd ? `${process.env.PUBLIC_PATH}/${pkgJson.ident}/` : `http://localhost:${pkgJson.port}/${pkgJson.ident}/`,
    library: pkgJson.ident,
    libraryTarget: 'amd',
    filename: jsFileName,
    chunkFilename: jsFileName,
  },
  devtool: isProd ? 'none' : 'cheap-module-eval-source-map',
  devServer: {
    host: '127.0.0.1',
    port: pkgJson.port,
    hot: true,
    open: false,
    publicPath: `http://localhost:${pkgJson.port}/${pkgJson.ident}/`,
    inline: true,
    disableHostCheck: true,
    historyApiFallback: true,
    headers: {
      'Access-Control-Allow-Origin': '*',
    },
    proxy: {},
  },
});
