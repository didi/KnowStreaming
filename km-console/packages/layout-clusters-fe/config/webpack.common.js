const path = require('path');
const theme = require('./theme');
const webpack = require('webpack');
const HtmlWebpackPlugin = require('html-webpack-plugin');
const MiniCssExtractPlugin = require('mini-css-extract-plugin');
const ProgressBarPlugin = require('progress-bar-webpack-plugin');
const CoverHtmlWebpackPlugin = require('./CoverHtmlWebpackPlugin.js');
const CaseSensitivePathsPlugin = require('case-sensitive-paths-webpack-plugin');

const devMode = process.env.NODE_ENV === 'development';
const babelOptions = {
  cacheDirectory: true,
  babelrc: false,
  presets: [require.resolve('@babel/preset-env'), require.resolve('@babel/preset-typescript'), require.resolve('@babel/preset-react')],
  plugins: [
    [require.resolve('@babel/plugin-proposal-decorators'), { legacy: true }],
    [require.resolve('@babel/plugin-proposal-class-properties'), { loose: true }],
    [require.resolve('@babel/plugin-proposal-private-property-in-object'), { loose: true }],
    [require.resolve('@babel/plugin-proposal-private-methods'), { loose: true }],
    require.resolve('@babel/plugin-proposal-export-default-from'),
    require.resolve('@babel/plugin-proposal-export-namespace-from'),
    require.resolve('@babel/plugin-proposal-object-rest-spread'),
    require.resolve('@babel/plugin-transform-runtime'),
    devMode && require.resolve('react-refresh/babel'),
    devMode && [
      'babel-plugin-import',
      {
        libraryName: 'antd',
        style: true,
      },
    ],
  ].filter(Boolean),
};

module.exports = {
  entry: {
    layout: ['./src/index.tsx'],
  },
  resolve: {
    symlinks: false,
    extensions: ['.web.jsx', '.web.js', '.ts', '.tsx', '.js', '.jsx', '.json'],
    alias: {
      '@src': path.resolve('src'),
    },
  },
  plugins: [
    new CoverHtmlWebpackPlugin(),
    new ProgressBarPlugin(),
    new CaseSensitivePathsPlugin(),
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
  ],
  module: {
    rules: [
      {
        parser: { system: false },
      },
      {
        test: /\.(js|jsx|ts|tsx)$/,
        exclude: /node_modules/,
        use: [
          {
            loader: 'babel-loader',
            options: babelOptions,
          },
          {
            loader: 'ts-loader',
            options: {
              allowTsInNodeModules: true,
            },
          },
        ],
      },
      {
        test: /\.(png|svg|jpeg|jpg|gif|ttf|woff|woff2|eot|pdf|otf)$/,
        use: [
          {
            loader: 'file-loader',
            options: {
              name: '[name].[ext]',
              outputPath: './assets/image/',
              esModule: false,
            },
          },
        ],
      },
      {
        test: /\.(css|less)$/,
        use: [
          MiniCssExtractPlugin.loader,
          'css-loader',
          {
            loader: 'less-loader',
            options: {
              javascriptEnabled: true,
              modifyVars: theme,
            },
          },
        ],
      },
    ],
  },
  node: {
    fs: 'empty',
    net: 'empty',
    tls: 'empty',
  },
  stats: 'errors-warnings',
};
