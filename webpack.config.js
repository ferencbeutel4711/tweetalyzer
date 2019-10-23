const path = require('path');
const CopyPlugin = require('copy-webpack-plugin');
const {VueLoaderPlugin} = require('vue-loader');

const inLocalDevMode = process.env.LOCAL_DEV;

const pluginsConfig = [new VueLoaderPlugin()];

if (inLocalDevMode) {
    pluginsConfig.push(new CopyPlugin([
        {
            from: path.resolve(__dirname, './src/main/resources/static/js/main.js'),
            to: path.resolve(__dirname, './build/resources/main/static/js/main.js')
        }
    ]))
}

module.exports = {
    entry: './src/main/client/js/main.js',
    module: {
        rules: [
            {
                test: /\.vue$/,
                loader: 'vue-loader'
            },
            {
                test: /\.js$/,
                loader: 'babel-loader',
                options: {
                    presets: ['@babel/preset-env']
                }
            },
            {
                test: /\.css$/,
                use: [
                    'vue-style-loader',
                    'css-loader'
                ]
            },
            {
                test: /\.scss$/,
                use: [
                    'vue-style-loader',
                    'css-loader',
                    'sass-loader'
                ]
            }
        ]
    },
    plugins: pluginsConfig,
    devtool: 'cheap-module-eval-source-map',
    output: {
        path: path.resolve(__dirname, './src/main/resources/static/js'),
        filename: 'main.js'
    },
    performance: {
        hints: false
    },
    resolve: {
        alias: {
            'vue$': 'vue/dist/vue.esm.js'
        },
        extensions: ['*', '.js', '.vue', '.json']
    }
};
