//var module = angular.module('SwallowModule', ['ngResource','isteven-multi-select']);
//Your app's root module...
var module = angular
		.module(
				'SwallowModule',
				[ 'ngResource', 'ngDialog', 'mgcrea.ngStrap' ],
				function($httpProvider) {
					// Use x-www-form-urlencoded Content-Type
					// $httpProvider.defaults.headers.post['Content-Type'] =
					// 'application/json;charset=utf-8';
					// $httpProvider.defaults.headers.put['Content-Type'] =
					// 'application/json;charset=utf-8';

					/**
					 * The workhorse; converts an object to
					 * x-www-form-urlencoded serialization.
					 * 
					 * @param {Object}
					 *            obj
					 * @return {String}
					 */
					var param = function(obj) {
						var query = '', name, value, fullSubName, subName, subValue, innerObj, i;

						for (name in obj) {
							value = obj[name];

							if (value instanceof Array) {
								for (i = 0; i < value.length; ++i) {
									subValue = value[i];
									fullSubName = name + '[' + i + ']';
									innerObj = {};
									innerObj[fullSubName] = subValue;
									query += param(innerObj) + '&';
								}
							} else if (value instanceof Object) {
								for (subName in value) {
									subValue = value[subName];
									fullSubName = name + '[' + subName + ']';
									innerObj = {};
									innerObj[fullSubName] = subValue;
									query += param(innerObj) + '&';
								}
							} else if (value !== undefined && value !== null)
								query += encodeURIComponent(name) + '='
										+ encodeURIComponent(value) + '&';
						}

						return query.length ? query.substr(0, query.length - 1)
								: query;
					};

					// Override $http service's default transformRequest
					// $httpProvider.defaults.transformRequest = [function(data)
					// {
					// return angular.isObject(data) && String(data) !==
					// '[object File]' ? param(data) : data;
					// }];
				});

module.config(function($locationProvider, $resourceProvider) {
	// configure html5 to get links working on jsfiddle
	// $locationProvider.html5Mode(true);
});

module.filter('strreplace', function() {
	return function(input) {
		return input.replace(/\\/g, "")
	};
});

module.directive('onFinishRenderFilters', function($timeout) {
	return {
		restrict : 'A',
		link : function(scope, element, attr) {
			if (scope.$last === true) {
				$timeout(function() {
					scope.$emit('ngRepeatFinished');
				});
			}
		}
	};
});

// module.directive('onFinishLoadFilters', function ($timeout) {
// return {
// restrict: 'A',
// link: function(scope, element, attr) {
// if (scope.$last === true) {
// $timeout(function() {
// scope.$broadcast('ngLoadFinished');
// });
// }
// }
// };
// });

module.directive('ngConfirmClick', [ function() {
	return {
		link : function(scope, element, attr) {
			var msg = attr.ngConfirmClick || "确定需要重新发送选中的消息?";
			var clickAction = attr.confirmedClick;
			element.bind('click', function(event) {
				if (window.confirm(msg)) {
					scope.$eval(clickAction)
				}
			});
		}
	};
} ]);

module.filter('reverse', function() {
	return function(items) {
		return items.slice().reverse();
	};
});

module.filter('notblank', function() {
	return function(items) {
		if (items != null) {
			for (var i = 0; i < items.length; ++i) {
				if (typeof (items[i].topic) == "undefined") {
					items.splice(i, 1);
				}
			}
		}
		return items;
	};
});

module.filter('nodefault', function() {
	return function(items) {
		if (items != null) {
			for (var i = 0; i < items.length; ++i) {
				if ( items[i].topic == "default") {
					items.splice(i, 1);
				}
			}
		}
		return items;
	};
});

module.filter('cut', function() {
	return function(value, wordwise, max, tail) {
		if (!value)
			return '';

		max = parseInt(max, 10);
		if (!max)
			return value;
		if (value.length <= max)
			return value;

		value = value.substr(0, max);
		if (wordwise) {
			var lastspace = value.lastIndexOf(' ');
			if (lastspace != -1) {
				value = value.substr(0, lastspace);
			}
		}

		return value + (tail || ' …');
	};
});

/**
 * 时间对象的格式化
 */
Date.prototype.format = function(format) {
	/*
	 * format="yyyy-MM-dd hh:mm:ss";
	 */
	var o = {
		"M+" : this.getMonth() + 1,
		"d+" : this.getDate(),
		"H+" : this.getHours(),
		"m+" : this.getMinutes(),
		"s+" : this.getSeconds(),
		"q+" : Math.floor((this.getMonth() + 3) / 3),
		"S" : this.getMilliseconds()
	}

	if (/(y+)/.test(format)) {
		format = format.replace(RegExp.$1, (this.getFullYear() + "")
				.substr(4 - RegExp.$1.length));
	}

	for ( var k in o) {
		if (new RegExp("(" + k + ")").test(format)) {
			format = format.replace(RegExp.$1, RegExp.$1.length == 1 ? o[k]
					: ("00" + o[k]).substr(("" + o[k]).length));
		}
	}
	return format;
}
