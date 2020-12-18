// https://github.com/gaearon/react-hot-loader/blob/master/examples/typescript/webpack.config.babel.js
const OptimizeCSSAssetsPlugin = require('optimize-css-assets-webpack-plugin');
const TsconfigPathsPlugin = require('tsconfig-paths-webpack-plugin');
const MiniCssExtractPlugin = require('mini-css-extract-plugin');
const { CleanWebpackPlugin } = require('clean-webpack-plugin');
const HtmlWebpackPlugin = require('html-webpack-plugin');
const TerserJSPlugin = require('terser-webpack-plugin');
const path = require('path')
const isProd = process.env.NODE_ENV === 'production';
const outPath = path.resolve('../kafka-manager-web/src/main/resources/templates');
const filename = isProd ? '[name].[contenthash]' : '[name]';
const MonacoWebpackPlugin = require('monaco-editor-webpack-plugin');
let publicPath = '/';

const plugins = [
  new HtmlWebpackPlugin({
    template: './src/routers/index.htm',
    favicon: './src/assets/image/logo.ico'
  }),
  new MonacoWebpackPlugin()
];

if (isProd) {
  plugins.push(new MiniCssExtractPlugin({
    filename: `${filename}.css`,
  }), new CleanWebpackPlugin());
}

module.exports = {
  mode: isProd ? 'production' : 'development',
  entry: {
    home: [
      './src/routers/index.tsx',
    ],
  },
  output: {
    filename: `${filename}.js`,
    path: outPath,
    publicPath,
  },
  devtool: isProd ? 'none' : 'cheap-module-eval-source-map',
  plugins,
  resolve: {
    alias: {
      'react-dom': '@hot-loader/react-dom',
    },
    extensions: ['.ts', '.tsx', '.js', 'jsx', '.json'],
    plugins: [
      new TsconfigPathsPlugin(),
    ],
  },
  module: {
    rules: [
      {
        test: /\.(css|less)$/,
        use: [isProd ? {
          loader: MiniCssExtractPlugin.loader,
          options: {
            publicPath,
          },
        } : 'style-loader', 'css-loader', {
          loader: 'less-loader',
          options: {
            javascriptEnabled: true,
            modifyVars: {
              'primary-color': '#f38031',
              'link-color': '#f38031',
              'font-size-base': '12px',
              // 'border-radius-base': '2px',
            },
          },
        }],
      },
      {
        test: /\.(ts|tsx)?$/,
        loader: 'ts-loader',
      },
      {
        test: /\.(png|svg|jpeg|jpg|gif)$/,
        use: [
          {
            loader: 'file-loader',
            options: {
              name: '[name].[ext]',
              outputPath: './assets/image/',
            }
          }
        ]
      },
      {
        test: /\.(ttf)$/i,
        use: [
          {
            loader: 'url-loader',
            options: {
              limit: 8192,
            },
          },
        ],
      },
    ],
  },
  optimization: {
    minimize: isProd,
    minimizer: [new TerserJSPlugin({}), new OptimizeCSSAssetsPlugin({})],
    splitChunks: {
      cacheGroups: {
        antd: {
          test: /[\\/]node_modules[\\/](antd|rc-|@ant-design)/,
          name: 'antd',
          chunks: 'all',
          priority: -1,
        },
        vendors: {
          test: /[\\/]node_modules[\\/]/,
          name: 'vendors',
          chunks: 'all',
          enforce: true,
          priority: -3,
        },
      },
    },
  },
  devServer: {
    contentBase: outPath,
    host: '127.0.0.1',
    port: 1025,
    hot: true,
    disableHostCheck: true,
    historyApiFallback: true,
    proxy: {
      '/api/v1/': {
        // target: 'http://127.0.0.1:8080',
        target: 'http://10.179.37.199:8008',
        // target: 'http://99.11.45.164:8888',
        changeOrigin: true,
      }
    },
  },
};
