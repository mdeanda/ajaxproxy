"use strict";

var Logger = new (function() {
	var startTime = new Date().getTime();
	var uid = ("" + startTime).substring(4) + '.' + getRandomString();
	var index = 0;

	function getRandomString() {
		var s = "0000" + ("" + Math.random()).replace('.', '');
		return s.substring(s.length - 5);
	}
	
	function sendData(data) {
		var URL = "%PATH%";
		var request;
		if (window.XMLHttpRequest) {
			request = new XMLHttpRequest();
		} else if (window.ActiveXObject) {
			try {
				request = new ActiveXObject('Msxml2.XMLHTTP');
			}
			catch (e) {
				try {
					request = new ActiveXObject('Microsoft.XMLHTTP');
				}
				catch (e) {}
			}
		}

		request.open('POST', URL, true);
		request.setRequestHeader('Content-Type', 'application/json; charset=utf-8');
		request.send(toJson(data));
	}
	
	function toJson(o) {
		return JSON.stringify(o);
	}

	function getMessage(tag, message) {
		var output = [];
		var index = 0;
		
		for (var i=1; i<arguments.length; i++) {
			output.push(arguments[i]);
		}
		
		return output;
	}

	return {
		log: function(tag, message) {
			var now = new Date().getTime();
			var ts = now - startTime;
			
			var t = ("" + now).substring(4);
			var obj = {
				uid: uid,
				index: index++,
				ts: ts,
				time: t,
				tag: tag,
				message: getMessage.apply(this, arguments)
			}

			sendData([obj]);
		}
	};
})();

