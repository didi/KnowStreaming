// https://github.com/gaearon/react-hot-loader/blob/master/examples/typescript/webpack.config.babel.js
const OptimizeCSSAssetsPlugin = require('optimize-css-assets-webpack-plugin');
const TsconfigPathsPlugin = require('tsconfig-paths-webpack-plugin');
const MiniCssExtractPlugin = require('mini-css-extract-plugin');
const HtmlWebpackPlugin = require('html-webpack-plugin');
const TerserJSPlugin = require('terser-webpack-plugin');

const isProd = process.env.NODE_ENV === 'production';
const outPath = `${__dirname}/dist`;
const filename = isProd ? '[name].[contenthash]' : '[name]';
const publicPath = '/';

const plugins = [
  new HtmlWebpackPlugin({
    template: './src/routers/index.htm',
    favicon: './src/assets/image/logo.ico'
  }),
];

if (isProd) {
  plugins.push(new MiniCssExtractPlugin({
    filename: `${filename}.css`,
  }));
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
    publicPath,
    path: outPath,
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
        use: [ isProd ? {
          loader: MiniCssExtractPlugin.loader,
          options: {
            publicPath: '/',
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
        test:/\.(png|svg|jpeg|jpg|gif)$/,
        use:[       
          {
              loader:'file-loader',
              options:{
                  name:'[name].[ext]',  
                  outputPath:'./assets/image/',                             
              }
          }
        ]
      }
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
    host: '0.0.0.0',
    port: 1025,
    hot: true,
    disableHostCheck: true,
    historyApiFallback: true,
    proxy: {
      '/api/v1/': {
        target: 'http://10.179.99.250:8080',
        changeOrigin: true,
      },
    },
  },
};
