/* eslint-disable */
const isProd = process.env.NODE_ENV === 'production';
const pkgJson = require('./package');
const path = require('path');
// const outPath = path.resolve(__dirname, `../../pub/${pkgJson.ident}`);
const outPath = path.resolve(__dirname, `../../../km-rest/src/main/resources/templates/${pkgJson.ident}`);
const HtmlWebpackPlugin = require('html-webpack-plugin');
const merge = require('webpack-merge');
const webpack = require('webpack');

const getWebpackCommonConfig = require('./config/d1-webpack.base');
const config = getWebpackCommonConfig();
module.exports = merge(config, {
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
    publicPath: isProd ? `/${pkgJson.ident}/` : `http://localhost:${pkgJson.port}/${pkgJson.ident}/`,
    library: pkgJson.ident,
    libraryTarget: 'amd',
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
