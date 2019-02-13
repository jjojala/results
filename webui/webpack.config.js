module.exports = {
    entry: [
        __dirname + "/src/index.html",
        __dirname + "/src/index.js"
    ],
    module: {
        rules: [
            {
                test: /\.(js|jsx)$/,
                exclude: /node_modules/,
                use: {
                    loader: 'babel-loader'
                }
            },
            {  
                test: /\.html/,
                use: {
                    loader: 'file-loader?name=[name].[ext]'
                }
            }
        ]
    },
    watch: true,
    watchOptions: {
        poll: 1000,
        ignored: /node_modules/
    }
};