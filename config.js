{
	port: 8093,
	resourceBase: "web",
	variables: {
		host: "ajaxproxy.thedeanda.com"
	},
	proxy: [
		{
			port: 80,
			path: '/fooey/*',
			domain: '${host}'
		}
	],
	merge: [
		{
			path: "/test.js",
			minify: false,
			filePath: "merge.txt"
		}
	]
}
