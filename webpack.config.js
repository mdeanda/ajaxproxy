const path = require("path");
const HtmlWebPackPlugin = require("html-webpack-plugin");
//const getFilesFromDir = require("./config/files");
const fs = require("fs");
const PAGE_DIR = path.join("src", "main", "js", "pages", path.sep);

function getFilesFromDir(dir, fileTypes) {
    const filesToReturn = [];
    function walkDir(currentPath) {
      const files = fs.readdirSync(currentPath);
      for (let i in files) {
        const curFile = path.join(currentPath, files[i]);
        if (fs.statSync(curFile).isFile() && fileTypes.indexOf(path.extname(curFile)) != -1) {
          filesToReturn.push(curFile);
        } else if (fs.statSync(curFile).isDirectory()) {
          walkDir(curFile);
        }
      }
    };
    walkDir(dir);
    return filesToReturn;
}

const htmlPlugins = getFilesFromDir(PAGE_DIR, [".html"]).map( filePath => {
  const fileName = filePath.replace(PAGE_DIR, "");
  // { chunks:["contact", "vendor"], template: "src/pages/contact.html",  filename: "contact.html"}
  return new HtmlWebPackPlugin({
    chunks:[fileName.replace(path.extname(fileName), ""), "vendor"],
    template: filePath,
    filename: fileName
  })
});

// { contact: "./src/pages/contact.js" }
const entry = getFilesFromDir(PAGE_DIR, [".js"]).reduce( (obj, filePath) => {
  const entryChunkName = filePath.replace(path.extname(filePath), "").replace(PAGE_DIR, "");
  obj[entryChunkName] = `./${filePath}`;
  return obj;
}, {});

module.exports = (env, argv) => ({
    entry: entry,
    output: {
        path: path.join(__dirname, "src", "main", "resources", "assets", "built"),
        filename: "[name].js"
    },
    devtool: "sourcemaps", // argv.mode === 'production' ? false : 'eval-source-maps',
    resolve:{
        alias:{
            src: path.resolve(__dirname, "src"),
            components: path.resolve(__dirname, "src", "main", "js", "components"),
            store: path.resolve(__dirname, "src", "main", "js", "store"),
            reducers: path.resolve(__dirname, "src", "main", "js", "reducers")
        }
    },
    module: {
		rules: [
			{
				test: /\.js$/,
				exclude: /node_modules/,
				use: {
					loader:"babel-loader",
					options:{
						presets: [
							"@babel/preset-env",
							"@babel/preset-react"
						],
						plugins: [
                            "@babel/plugin-proposal-class-properties"
                        ]
					}
				},
            },
            {
				test: /\.css$/,
				use: ["style-loader", {loader: "css-loader", options: {modules: true}}],
				exclude: /node_modules/,
            },
            {
                test: /\.s[ac]ss$/i,
                use: [
                    // Creates `style` nodes from JS strings
                    'style-loader',
                    // Translates CSS into CommonJS
                    'css-loader',
                    // Compiles Sass to CSS
                    'sass-loader',
                ]
            },
            {
                test: /\.(svg|jpg|gif|png)$/,
                use: [
                    {
                        loader: 'file-loader',
                        options: {
                            name: '[name].[ext]',
                            outputPath: (url, resourcePath, context) => {
                                if(argv.mode === 'development') {
                                    const relativePath = path.relative(context, resourcePath);
                                    return `/${relativePath}`;
                                }
                                return `/assets/images/${path.basename(resourcePath)}`;
                            }
                        }
                    }
                ]
            },
            {
                test: /\.(woff|woff2|eot|ttf|otf)$/,
                use: [
                    {
                        loader: 'file-loader',
                        options: {
                            outputPath: (url, resourcePath, context) => {
                                if(argv.mode === 'development') {
                                    const relativePath = path.relative(context, resourcePath);
                                    return `/${relativePath}`;
                                }
                                return `/assets/fonts/${path.basename(resourcePath)}`;
                            }
                        }
                    }
                ]
            }
        ]
    },
    optimization: {
        minimize: false, // argv.mode === 'production' ? true : false,
        splitChunks: {
            cacheGroups: {
                vendor: {
                    test: /node_modules/,
                    chunks: "initial",
                    name: "vendor",
                    enforce: true
                }
            }
        }
    }
});
