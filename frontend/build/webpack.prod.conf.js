'use strict'
const path = require('path')
const utils = require('./utils')
const webpack = require('webpack')
const config = require('../config')
const merge = require('webpack-merge')
const baseWebpackConfig = require('./webpack.base.conf')
const CopyWebpackPlugin = require('copy-webpack-plugin')
const HtmlWebpackPlugin = require('html-webpack-plugin')
const ExtractTextPlugin = require('extract-text-webpack-plugin')
const OptimizeCSSPlugin = require('optimize-css-assets-webpack-plugin')
const UglifyJsPlugin = require('uglifyjs-webpack-plugin')
const SWPrecacheWebpackPlugin = require('sw-precache-webpack-plugin')

const env = process.env.NODE_ENV === 'testing'
  ? require('../config/test.env')
  : require('../config/prod.env')

const THE_SERVER = process.argv.slice(2)[0];

const webpackConfig = merge(baseWebpackConfig, {
  module: {
    rules: utils.styleLoaders({
      sourceMap: config.build.productionSourceMap,
      extract: true,
      usePostCSS: true
    })
  },
  devtool: config.build.productionSourceMap ? config.build.devtool : false,
  output: {
    path: config.build.assetsRoot,
    filename: utils.assetsPath('js/[name].[chunkhash].js'),
    chunkFilename: utils.assetsPath('js/[id].[chunkhash].js')
  },
  plugins: [
    // http://vuejs.github.io/vue-loader/en/workflow/production.html
    new webpack.DefinePlugin({
      'process.env': env
    }),
    new UglifyJsPlugin({
      // We do use webpack v3.12.0, the newest version of uglifyjs-webpack-plugin compatible with it is 1.3.0.
      // This plugin utilizes uglify-es v3.3.9 for minification/compression.
      uglifyOptions: {
        // In terms of minification/uglification the most time-consuming part is expression specific optimizations.
        // It takes more than 90% of the build time and gives 0.4 MB of the distributive size optimization (which is only 1.74%).
        // We still want to do other "light" optimizations, e.g. removing comments, whitespaces and dead/unused code, mangling.
        // This takes takes seconds and gives 2.2 MB of the size optimization.
        // Based on this metrics it was decided to exclude expression specific optimizations from the build process.
        // Details of `compress` options can be found here (v3.3.9):
        // https://github.com/mishoo/UglifyJS/tree/4eb4cb656cc4f3850c403689cf29e529f4c67944#compress-options
        compress: {
          warnings: false,
          // We need below options (true by default) to remove dead/unused code:
          dead_code: true,
          unused: true,
          // We explicitly switch off expression specific optimizations (0.4 MB), which are switched on by default
          booleans: false,
          collapse_vars: false,
          comparisons: false,
          conditionals: false,
          drop_debugger: false,
          evaluate: false,
          expression: false,
          hoist_props: false,
          if_return: false,
          inline: false,
          join_vars: false,
          keep_fargs: false,
          loops: false,
          negate_iife: false,
          properties: false,
          reduce_funcs: false,
          reduce_vars: false,
          sequences: false,
          side_effects: false,
          switches: false,
          typeofs: false
        },
        // To introduce default optimization/compression back please use below `compress` settings instead.
        // compress: { warnings: false }
        mangle: true
      },
      sourceMap: config.build.productionSourceMap,
      parallel: true
    }),
    // extract css into its own file
    new ExtractTextPlugin({
      filename: utils.assetsPath('css/[name].[contenthash].css'),
      // Setting the following option to `false` will not extract CSS from codesplit chunks.
      // Their CSS will instead be inserted dynamically with style-loader when the codesplit chunk has been loaded by webpack.
      // It's currently set to `true` because we are seeing that sourcemaps are included in the codesplit bundle as well when it's `false`,
      // increasing file size: https://github.com/vuejs-templates/webpack/issues/1110
      allChunks: true
    }),
    // Compress extracted CSS. We are using this plugin so that possible
    // duplicated CSS from different components can be deduped.
    new OptimizeCSSPlugin({
      cssProcessorOptions: config.build.productionSourceMap
        ? { safe: true, map: { inline: false } }
        : { safe: true }
    }),
    // generate dist index.html with correct asset hash for caching.
    // you can customize output by editing /index.html
    // see https://github.com/ampedandwired/html-webpack-plugin
    new HtmlWebpackPlugin({
      filename: process.env.NODE_ENV === 'testing'
        ? 'index.html'
        : config.build.index,
      template: THE_SERVER === 'dev' ? 'index.html' : 'index.prod.html',
      inject: true,
      minify: {
        removeComments: true,
        collapseWhitespace: true,
        removeAttributeQuotes: true
        // more options:
        // https://github.com/kangax/html-minifier#options-quick-reference
      },
      // necessary to consistently work with multiple chunks via CommonsChunkPlugin
      chunksSortMode: 'dependency'
    }),
    // keep module.id stable when vendor modules does not change
    new webpack.HashedModuleIdsPlugin(),
    // enable scope hoisting
    new webpack.optimize.ModuleConcatenationPlugin(),
    // split vendor js into its own file
    new webpack.optimize.CommonsChunkPlugin({
      name: 'vendor',
      minChunks (module) {
        // any required modules inside node_modules are extracted to vendor
        return (
          module.resource &&
          /\.js$/.test(module.resource) &&
          module.resource.indexOf(
            path.join(__dirname, '../node_modules')
          ) === 0
        )
      }
    }),
    // extract webpack runtime and module manifest to its own file in order to
    // prevent vendor hash from being updated whenever app bundle is updated
    new webpack.optimize.CommonsChunkPlugin({
      name: 'manifest',
      minChunks: Infinity
    }),
    // This instance extracts shared chunks from code splitted chunks and bundles them
    // in a separate chunk, similar to the vendor chunk
    // see: https://webpack.js.org/plugins/commons-chunk-plugin/#extra-async-commons-chunk
    new webpack.optimize.CommonsChunkPlugin({
      name: 'app',
      async: 'vendor-async',
      children: true,
      minChunks: 3
    }),

    // copy custom static assets
    new CopyWebpackPlugin([
      {
        from: path.resolve(__dirname, '../static'),
        to: config.build.assetsSubDirectory,
        ignore: ['.*']
      }
    ]),

    // service worker caching
    new SWPrecacheWebpackPlugin({
      cacheId: '97tax-app',
      filename: 'service-worker.js',
      staticFileGlobs: ['target/dist/**/*.{js,html,css,jpg,png,svg}'],
      minify: true,
      stripPrefix: 'target/dist/',
      runtimeCaching: [
        {
          urlPattern: /^https:\/\/fonts\.googleapis\.com\//,
          handler: 'cacheFirst'
        },
        {
          urlPattern: /^https:\/\/fonts\.gstatic\.com\//,
          handler: 'cacheFirst'
        },
        {
          urlPattern: /^https:\/\/verify\.authorize\.net\/anetseal\/images\/secure90x72\.gif/,
          handler: 'cacheFirst'
        },
        {
          urlPattern: /^https:\/\/maps\.gstatic\.com\/mapfiles\/api-3\/images\/powered-by-google-on-white3\.png/,
          handler: 'cacheFirst'
        },
        {
          urlPattern: /^https:\/\/maps\.gstatic\.com\/mapfiles\/api-3\/images\/autocomplete-icons\.png/,
          handler: 'cacheFirst'
        },
        {
          urlPattern: /^https:\/\/www\.gstatic\.com\/images\/branding\/product\/1x\/translate_24dp\.png/,
          handler: 'cacheFirst'
        },
        {
          urlPattern: /^https:\/\/www\.gstatic\.com\/images\/branding\/product\/2x\/translate_24dp\.png/,
          handler: 'cacheFirst'
        },
        {
          urlPattern: /^https:\/\/www\.google\.com\/images\/cleardot\.gif/,
          handler: 'cacheFirst'
        },
        {
          urlPattern: /^https:\/\/translate\.googleapis\.com\/translate_static\/img\/te_ctrl3\.gif/,
          handler: 'cacheFirst'
        }
      ]
    })
  ]
})

if (config.build.productionGzip) {
  const CompressionWebpackPlugin = require('compression-webpack-plugin')

  webpackConfig.plugins.push(
    new CompressionWebpackPlugin({
      asset: '[path].gz[query]',
      algorithm: 'gzip',
      test: new RegExp(
        '\\.(' +
        config.build.productionGzipExtensions.join('|') +
        ')$'
      ),
      threshold: 10240,
      minRatio: 0.8
    })
  )
}

if (config.build.bundleAnalyzerReport) {
  const BundleAnalyzerPlugin = require('webpack-bundle-analyzer').BundleAnalyzerPlugin
  webpackConfig.plugins.push(new BundleAnalyzerPlugin())
}

module.exports = webpackConfig
