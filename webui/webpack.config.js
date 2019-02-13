module.exports = {
    module: {
        rules: [
            {
                test: /\.(js|jsx)$/,
                exclude: /node_modules/,
                use: {
                    loader: "babel-loader"
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