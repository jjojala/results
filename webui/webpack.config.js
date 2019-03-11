module.exports = {
    entry: [
        __dirname + "/src/index.html",
        __dirname + "/src/app/App.js"
    ],
    resolve: {
    	modules: [
		__dirname + '/src/',
		'node_modules'
		],
	},
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
                test: /\.html$/,
                use: {
                    loader: 'file-loader?name=[name].[ext]'
                }
            },
	    {
	    	test: /\.css$/,
		use: ['style-loader', 'css-loader']
	    }
        ]
    },
    watch: true,
    watchOptions: {
        poll: 1000,
        ignored: /node_modules/
    }
};
