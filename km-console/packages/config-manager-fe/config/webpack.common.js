const path = require('path');
const webpack = require('webpack');
const HtmlWebpackPlugin = require('html-webpack-plugin');
const MiniCssExtractPlugin = require('mini-css-extract-plugin');
const ProgressBarPlugin = require('progress-bar-webpack-plugin');
const CaseSensitivePathsPlugin = require('case-sensitive-paths-webpack-plugin');
const StatsPlugin = require('stats-webpack-plugin');
const HappyPack = require('happypack');
const os = require('os');
const happyThreadPool = HappyPack.ThreadPool({ size: os.cpus().length });
const theme = require('./theme');
const pkgJson = require('../package');

const devMode = process.env.NODE_ENV === 'development';
const babelOptions = {
  cacheDirectory: true,
  babelrc: false,
  presets: [require.resolve('@babel/preset-env'), require.resolve('@babel/preset-typescript'), require.resolve('@babel/preset-react')],
  overrides: [
    // TODO：编译时需要做的事情更多，应该只针对目标第三方库
    {
      include: './node_modules',
      sourceType: 'unambiguous'
    }
  ],
  plugins: [
    [require.resolve('@babel/plugin-proposal-decorators'), { legacy: true }],
    [require.resolve('@babel/plugin-proposal-class-properties'), { loose: true }],
    [require.resolve('@babel/plugin-proposal-private-methods'), { loose: true }],
    [require.resolve('@babel/plugin-proposal-private-property-in-object'), { loose: true }],
    require.resolve('@babel/plugin-proposal-export-default-from'),
    require.resolve('@babel/plugin-proposal-export-namespace-from'),
    require.resolve('@babel/plugin-proposal-object-rest-spread'),
    require.resolve('@babel/plugin-transform-runtime'),
    require.resolve('@babel/plugin-proposal-optional-chaining'), //
    require.resolve('@babel/plugin-proposal-nullish-coalescing-operator'), // 解决 ?? 无法转义问题
    require.resolve('@babel/plugin-proposal-numeric-separator'), // 转义 1_000_000
    devMode && require.resolve('react-refresh/babel'),
  ].filter(Boolean),
};

module.exports = {
  entry: {
    [pkgJson.ident]: ['./src/index.tsx'],
  },
  resolve: {
    symlinks: false,
    extensions: ['.web.jsx', '.web.js', '.ts', '.tsx', '.js', '.jsx', '.json'],
    alias: {
      '@src': path.resolve(process.cwd(), 'src'),
    },
  },
  plugins: [
    new ProgressBarPlugin(),
    new CaseSensitivePathsPlugin(),
    new StatsPlugin('manifest.json', {
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
  ].filter(Boolean),
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
