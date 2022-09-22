const path = require('path');
const MiniCssExtractPlugin = require('mini-css-extract-plugin');
const { CleanWebpackPlugin } = require('clean-webpack-plugin');
const TerserJSPlugin = require('terser-webpack-plugin');
const OptimizeCSSAssetsPlugin = require('optimize-css-assets-webpack-plugin');
const pkgJson = require('../package');

module.exports = {
  mode: 'production',
  externals: [
    /^react$/,
    /^react\/lib.*/,
    /^react-dom$/,
    /.*react-dom.*/,
    /^single-spa$/,
    /^single-spa-react$/,
    /^moment$/,
    /^lodash$/,
    /^react-router$/,
    /^react-router-dom$/,
  ],
  plugins: [
    new CleanWebpackPlugin(),
    new MiniCssExtractPlugin({
      filename: '[name]-[chunkhash].css',
    }),
  ],
  output: {
    path: path.resolve(process.cwd(), `../../../km-rest/src/main/resources/templates/${pkgJson.ident}`),
    publicPath: `${process.env.PUBLIC_PATH}/${pkgJson.ident}/`,
    library: pkgJson.ident,
    libraryTarget: 'amd',
    filename: '[name]-[chunkhash].js',
    chunkFilename: '[name]-[chunkhash].js',
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
          maxSize: 3500000,
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
