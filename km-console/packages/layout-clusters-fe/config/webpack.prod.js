const path = require('path');
const { CleanWebpackPlugin } = require('clean-webpack-plugin');
const CountPlugin = require('./CountComponentWebpackPlugin');
const MiniCssExtractPlugin = require('mini-css-extract-plugin');
const CopyWebpackPlugin = require('copy-webpack-plugin');
const TerserJSPlugin = require('terser-webpack-plugin');
const OptimizeCSSAssetsPlugin = require('optimize-css-assets-webpack-plugin');

const outputPath = path.resolve(process.cwd(), `../../../km-rest/src/main/resources/templates/layout`);

module.exports = {
  mode: 'production',
  plugins: [
    new CleanWebpackPlugin(),
    new CountPlugin({
      pathname: 'knowdesign',
      startCount: true,
      isExportExcel: false,
    }),
    new MiniCssExtractPlugin({
      filename: '[name]-[chunkhash].css',
    }),
    new CopyWebpackPlugin([
      {
        from: path.resolve(process.cwd(), 'static'),
        to: path.resolve(outputPath, '../static'),
      },
      {
        from: path.resolve(process.cwd(), 'favicon.ico'),
        to: path.resolve(outputPath, '../favicon.ico'),
      },
    ]),
  ],
  externals: [
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
    /^react-router$/,
    /^react-router-dom$/,
  ],
  output: {
    path: outputPath,
    publicPath: process.env.PUBLIC_PATH + '/layout/',
    filename: '[name]-[chunkhash].js',
    chunkFilename: '[name]-[chunkhash].js',
    library: 'layout',
    libraryTarget: 'amd',
  },
  optimization: {
    splitChunks: {
      cacheGroups: {
        vendor: {
          test: /[\\/]node_modules[\\/]/,
          chunks: 'all',
          name: 'vendor',
          priority: 10,
          enforce: true,
          minChunks: 1,
          maxSize: 3000000,
        },
      },
    },
    minimizer: [
      new TerserJSPlugin({
        cache: true,
        sourceMap: true,
      }),
      new OptimizeCSSAssetsPlugin({}),
    ],
  },
  devtool: 'none',
};
