"use strict";

var Logger = new (function() {
	var startTime = new Date().getTime();
	var uid = getUid(startTime);
	var index = 0;
	var delay = 500;
	var timer = null;
	var queue = [];
	var proxyTabIndex = 0;

	function getUid(startTime) {
		var s = '0000' + (Math.floor(Math.random() * 1679616)).toString(36);
		s = '-' + s.substring(s.length - 4);
		
		var dt = startTime.toString(36);
		dt = dt.substring(0, dt.length-4) + '-' + dt.substring(dt.length-4);
		s = dt + s;
		return s;
	}
	
	function sendData(data) {
		queue.push(data);
		if (timer) {
			clearTimeout(timer);
		}
		timer = setTimeout(function() {
			actuallySendData();
		}, delay);
	}
	
	function actuallySendData() {
		var data = queue;
		queue = [];
		timer = null;
		
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
	
	function proxyTabs(n) {
		var ret = '';
		for (var i=0; i<n; i++) {
			ret += '--';
		}
		return ret;
	}

	function toStringShort(v) {
		var t = typeof v;
		if (v == null || t == 'undefined'
				|| t == 'number' || t == 'boolean') {
			return v;
		}
		if (typeof v == 'string') {
			var l = v.length;
			if (l > 30) {
				return '"' + v.substring(0,30) + '..."';
			} else {
				return '"' + v + '"';
			}
		}
		if (v instanceof Array) {
			return '[Array]';
		} else {
			return '[Object]';
		}
	}
	
	function proxyFunction(object, key, objectName) {
		var fn = object[key];
		var field = objectName + "." + key;
		Logger.log("Proxy Config", field);
		object[key] = function() {
			var ctx = this;
			var params = [];
			try {
				for (var a in arguments) {
					params.push(toStringShort(arguments[a]));
				}
			} catch(e) {};
			var msg = proxyTabs(proxyTabIndex) + field + '(' + params.join(',') + ')';
			proxyTabIndex++;
			try {
				var ret = fn.apply(ctx, arguments);
				proxyTabIndex--;
				Logger.log("Proxy Method Called", msg, params, ret);
				return ret;
			} catch(e) {
				proxyTabIndex--;
				Logger.log("Proxy Error", msg, params, e);
				throw e;
			}
		}
	}
	function isInIgnoreList(ignoreList, name) {
		var ret = false;
		if (ignoreList) {
			for (var i=0; i<ignoreList.length; i++) {
				//TODO: support regex as well
				var item = ignoreList[i];
				if (item instanceof RegExp) {
					if (item.test(name)) {
						ret = true;
						break;
					}
				} else if (item == name) {
					ret = true;
					break;
				}
			}
		}
		return ret;
	}
	function proxyObject(object, objectName, ignoreList) {
		if (typeof object != 'object' || !object) return;

		for (var key in object) {
			if (!isInIgnoreList(ignoreList, key)) {
				var f = object[key];
				if (typeof f == 'function') {
					proxyFunction(object, key, objectName);
				}
			} else {
				Logger.log("Proxy Filter", key);	
			}
		}
	}

	return {
		applyLogger: function(obj, tag) {
			var me = this;
			obj.log = function() {
				var args = [];
				args.push(tag);
				for (var i in arguments) {
					args.push(arguments[i]);
				}
				me.log.apply(me, args);
			}
		},
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

			sendData(obj);
		},
		proxy: function(object, objectName, ignoreList) {
			proxyObject(object, objectName, ignoreList);
		}
	};
})();

