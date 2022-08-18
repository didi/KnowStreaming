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

const isProd = process.env.NODE_ENV === 'production';

const babelOptions = {
  cacheDirectory: true,
  babelrc: false,
  presets: [require.resolve('@babel/preset-env'), require.resolve('@babel/preset-typescript'), require.resolve('@babel/preset-react')],
  plugins: [
    [require.resolve('@babel/plugin-proposal-decorators'), { legacy: true }],
    [require.resolve('@babel/plugin-proposal-class-properties'), { loose: true }],
    require.resolve('@babel/plugin-proposal-export-default-from'),
    require.resolve('@babel/plugin-proposal-export-namespace-from'),
    require.resolve('@babel/plugin-proposal-object-rest-spread'),
    require.resolve('@babel/plugin-transform-runtime'),
    !isProd && require.resolve('react-refresh/babel'),
  ]
    .filter(Boolean)
    .concat([
      '@babel/plugin-transform-object-assign',
      '@babel/plugin-transform-modules-commonjs',
    ]),
};

module.exports = () => {
  const jsFileName = isProd ? '[name]-[chunkhash].js' : '[name].js';
  const cssFileName = isProd ? '[name]-[chunkhash].css' : '[name].css';

  const plugins = [
    new CoverHtmlWebpackPlugin(),
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
    resolve.mainFields = ['browser', 'main', 'module'];
  }

  return {
    output: {
      filename: jsFileName,
      chunkFilename: jsFileName,
      library: 'layout',
      libraryTarget: 'amd',
    },
    externals: [
      /^react$/,
      /^react\/lib.*/,
      /^react-dom$/,
      /.*react-dom.*/,
      /^single-spa$/,
      /^single-spa-react$/,
      /^moment$/,
      /^react-router$/,
      /^react-router-dom$/,
    ],
    resolve,
    plugins,
    module: {
      rules: [
        {
          parser: { system: false },
        },
        {
          test: /\.(js|jsx)$/,
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
          test: /\.(png|svg|jpeg|jpg|gif|ttf|woff|woff2|eot|pdf)$/,
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
    devtool: isProd ? 'cheap-module-source-map' : 'source-map',
    node: {
      fs: 'empty',
      net: 'empty',
      tls: 'empty',
    },
  };
};
