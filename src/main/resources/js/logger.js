var Logger = new (function() {
	function sendData(data) {
		var URL = "http://localhost:8080/users/";
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



	var uid = 0;

	return {
		log: function(tag, message) {
			var obj = {
				uid: uid,
				tag: tag
			}

			sendData([obj]);
		}
	};
})();

