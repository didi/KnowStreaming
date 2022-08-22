/* eslint-disable */
const MiniCssExtractPlugin = require('mini-css-extract-plugin');
const ProgressBarPlugin = require('progress-bar-webpack-plugin');
const CaseSensitivePathsPlugin = require('case-sensitive-paths-webpack-plugin');
const { CleanWebpackPlugin } = require('clean-webpack-plugin');
const CoverHtmlWebpackPlugin = require('./CoverHtmlWebpackPlugin.js');
var webpackConfigResolveAlias = require('./webpackConfigResolveAlias');
const TerserJSPlugin = require('terser-webpack-plugin');
const OptimizeCSSAssetsPlugin = require('optimize-css-assets-webpack-plugin');
const theme = require('./theme');
const ReactRefreshWebpackPlugin = require('@pmmmwh/react-refresh-webpack-plugin');
const HardSourceWebpackPlugin = require('hard-source-webpack-plugin');
const BUSINESS_VERSION = false;

const isProd = process.env.NODE_ENV === 'production';
// const publicPath = isProd ? '//img-ys011.didistatic.com/static/bp_fe_daily/bigdata_cloud_KnowStreaming_FE/gn/' : '/';
const publicPath = '/';
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
    !isProd && require.resolve('react-refresh/babel'),
  ]
    .filter(Boolean)
    .concat([
      [
        'babel-plugin-import',
        {
          libraryName: 'antd',
          style: true,
        },
      ],
      '@babel/plugin-transform-object-assign',
    ]),
};

module.exports = () => {
  const jsFileName = isProd ? '[name]-[chunkhash].js' : '[name].js';
  const cssFileName = isProd ? '[name]-[chunkhash].css' : '[name].css';

  const plugins = [
    // !isProd && new HardSourceWebpackPlugin(),
    new CoverHtmlWebpackPlugin({
      BUSINESS_VERSION,
    }),
    new ProgressBarPlugin(),
    new CaseSensitivePathsPlugin(),
    new MiniCssExtractPlugin({
      filename: cssFileName,
    }),
    !isProd &&
    new ReactRefreshWebpackPlugin({
      overlay: false,
    }),
  ].filter(Boolean);
  const resolve = {
    symlinks: false,
    extensions: ['.web.jsx', '.web.js', '.ts', '.tsx', '.js', '.jsx', '.json'],
    alias: webpackConfigResolveAlias,
  };

  if (isProd) {
    plugins.push(new CleanWebpackPlugin());
  }

  if (!isProd) {
    resolve.mainFields = ['module', 'browser', 'main'];
  }

  return {
    output: {
      filename: jsFileName,
      chunkFilename: jsFileName,
      library: 'layout',
      libraryTarget: 'amd',
      publicPath,
    },
    externals: isProd
      ? [
        /^react$/,
        /^react\/lib.*/,
        /^react-dom$/,
        /.*react-dom.*/,
        /^single-spa$/,
        /^single-spa-react$/,
        /^moment$/,
        /^antd$/,
        /^lodash$/,
        /^echarts$/,
      ]
      : [],
    resolve,
    plugins,
    module: {
      rules: [
        {
          parser: { system: false },
        },
        {
          test: /\.(js|jsx)$/,
          exclude: /node_modules/,
          use: [
            {
              loader: 'babel-loader',
              options: babelOptions,
            },
          ],
        },
        {
          test: /\.(ts|tsx)$/,
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
            {
              loader: MiniCssExtractPlugin.loader,
            },
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
    optimization: isProd
      ? {
        minimizer: [
          new TerserJSPlugin({
            cache: true,
            sourceMap: true,
          }),
          new OptimizeCSSAssetsPlugin({}),
        ],
      }
      : {},
    devtool: isProd ? 'cheap-module-source-map' : '',
    node: {
      fs: 'empty',
      net: 'empty',
      tls: 'empty',
    },
  };
};
module.exports.BUSINESS_VERSION = BUSINESS_VERSION;
