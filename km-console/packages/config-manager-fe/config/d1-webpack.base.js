/* eslint-disable */
const MiniCssExtractPlugin = require('mini-css-extract-plugin');
const ProgressBarPlugin = require('progress-bar-webpack-plugin');
const CaseSensitivePathsPlugin = require('case-sensitive-paths-webpack-plugin');
const StatsPlugin = require('stats-webpack-plugin');
const { CleanWebpackPlugin } = require('clean-webpack-plugin');
const TerserJSPlugin = require('terser-webpack-plugin');
const OptimizeCSSAssetsPlugin = require('optimize-css-assets-webpack-plugin');
const HappyPack = require('happypack');
const os = require('os');
const happyThreadPool = HappyPack.ThreadPool({ size: os.cpus().length });
const ReactRefreshWebpackPlugin = require('@pmmmwh/react-refresh-webpack-plugin');
const HardSourceWebpackPlugin = require('hard-source-webpack-plugin');
const theme = require('./theme');
var cwd = process.cwd();

const path = require('path');
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
    [require.resolve('@babel/plugin-proposal-private-methods'), { loose: true }],
    require.resolve('@babel/plugin-proposal-export-default-from'),
    require.resolve('@babel/plugin-proposal-export-namespace-from'),
    require.resolve('@babel/plugin-proposal-object-rest-spread'),
    require.resolve('@babel/plugin-transform-runtime'),
    require.resolve('@babel/plugin-proposal-optional-chaining'), //
    require.resolve('@babel/plugin-proposal-nullish-coalescing-operator'), // 解决 ?? 无法转义问题
    require.resolve('@babel/plugin-proposal-numeric-separator'), // 转义 1_000_000
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
  const manifestName = `manifest.json`;
  const jsFileName = isProd ? '[name]-[chunkhash].js' : '[name].js';
  const cssFileName = isProd ? '[name]-[chunkhash].css' : '[name].css';

  const plugins = [
    // !isProd && new HardSourceWebpackPlugin(),
    new ProgressBarPlugin(),
    new CaseSensitivePathsPlugin(),
    new MiniCssExtractPlugin({
      filename: cssFileName,
    }),
    new StatsPlugin(manifestName, {
      chunkModules: false,
      source: true,
      chunks: false,
      modules: false,
      assets: true,
      children: false,
      exclude: [/node_modules/],
    }),
    new HappyPack({
      id: 'babel',
      loaders: [
        'cache-loader',
        {
          loader: 'babel-loader',
          options: babelOptions,
        },
      ],
      threadPool: happyThreadPool,
    }),
    !isProd &&
      new ReactRefreshWebpackPlugin({
        overlay: false,
      }),
  ].filter(Boolean);
  if (isProd) {
    plugins.push(new CleanWebpackPlugin());
  }
  return {
    output: {
      filename: jsFileName,
      chunkFilename: jsFileName,
      publicPath,
    },
    externals: isProd
      ? [/^react$/, /^react\/lib.*/, /^react-dom$/, /.*react-dom.*/, /^single-spa$/, /^single-spa-react$/, /^moment$/, /^antd$/, /^lodash$/]
      : [],
    resolve: {
      symlinks: false,
      extensions: ['.web.jsx', '.web.js', '.ts', '.tsx', '.js', '.jsx', '.json'],
      alias: {
        // '@pkgs': path.resolve(cwd, 'src/packages'),
        '@pkgs': path.resolve(cwd, './node_modules/@didi/d1-packages'),
        '@cpts': path.resolve(cwd, 'src/components'),
        '@interface': path.resolve(cwd, 'src/interface'),
        '@apis': path.resolve(cwd, 'src/api'),
        react: path.resolve('./node_modules/react'),
        actions: path.resolve(cwd, 'src/actions'),
        lib: path.resolve(cwd, 'src/lib'),
        constants: path.resolve(cwd, 'src/constants'),
        components: path.resolve(cwd, 'src/components'),
        container: path.resolve(cwd, 'src/container'),
        api: path.resolve(cwd, 'src/api'),
        assets: path.resolve(cwd, 'src/assets'),
        mobxStore: path.resolve(cwd, 'src/mobxStore'),
      },
    },
    plugins,
    module: {
      rules: [
        {
          parser: { system: false },
        },
        {
          test: /\.(js|jsx|ts|tsx)$/,
          exclude: /node_modules\/(?!react-intl|@didi\/dcloud-design)/,
          use: [
            {
              loader: 'happypack/loader?id=babel',
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
    optimization: Object.assign(
      {
        splitChunks: {
          cacheGroups: {
            vendor: {
              test: /[\\/]node_modules[\\/]/,
              chunks: 'all',
              name: 'vendor',
              priority: 10,
              enforce: true,
              minChunks: 1,
              maxSize: 3500000,
            },
          },
        },
      },
      isProd
        ? {
            minimizer: [
              new TerserJSPlugin({
                cache: true,
                sourceMap: true,
              }),
              new OptimizeCSSAssetsPlugin({}),
            ],
          }
        : {}
    ),
    devtool: isProd ? 'cheap-module-source-map' : 'source-map',
    node: {
      fs: 'empty',
      net: 'empty',
      tls: 'empty',
    },
  };
};
