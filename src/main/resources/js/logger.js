"use strict";

var Logger = new (function() {
	var startTime = new Date().getTime();
	var uid = ("" + startTime).substring(4) + '.' + getRandomString();

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
		request.send(JSON.stringify(data));
	}


	return {
		log: function(tag, message) {
			var now = new Date().getTime();
			var ts = now - startTime;
			
			var t = ("" + now).substring(4);
			var obj = {
				uid: uid,
				ts: ts,
				time: t,
				tag: tag
			}

			sendData([obj]);
		}
	};
})();

