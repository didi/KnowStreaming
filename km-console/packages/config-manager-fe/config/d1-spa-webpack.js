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

var cwd = process.cwd();
const path = require('path');
const isProd = process.env.NODE_ENV === 'production';

const babelOptions = {
  cacheDirectory: true,
  babelrc: false,
  presets: [require.resolve('@babel/preset-env'), require.resolve('@babel/preset-typescript'), require.resolve('@babel/preset-react')],
  plugins: [
    new HtmlWebpackPlugin({
      meta: {
        manifest: 'manifest.json',
      },
      template: './src/index.html',
      inject: 'body',
    }),
  ],
  output: {
    library: pkgJson.ident,
    libraryTarget: 'amd',
  },
  entry: {
    [pkgJson.ident]: ['./src/index.tsx'],
  },
};
