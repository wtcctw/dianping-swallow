module.controller('IpInfoController', [ '$rootScope', '$scope', '$http',
		'Paginator', 'ngDialog', '$interval',
		function($rootScope, $scope, $http, Paginator, ngDialog, $interval) {

			$scope.changealarmc = function(topic, cid, ip, type, index) {
				var url = "/console/consumerid/alarm/ipinfo/";
				var id = "#" + type + index;
				var check = $(id).prop('checked');

				if ("alarm" == type) {
					url += "alarm";
					$http.get(window.contextPath + url, {
						params : {
							topic : topic,
							cid : cid,
							ip  : ip,
							alarm : check
						}
					}).success(function(response) {
					});
				} else if ("active" == type) {
					url += "active";
					$http.get(window.contextPath + url, {
						params : {
							topic : topic,
							cid : cid,
							ip  : ip,
							active : check
						}
					}).success(function(response) {
					});
				} else {
					return;
				}

			}
			
			$scope.changealarmt = function(topic, ip, type, index) {
				var url = "/console/topic/alarm/ipinfo/";
				var id = "#" + type + index;
				var check = $(id).prop('checked');

				if ("alarm" == type) {
					url += "alarm";
					$http.get(window.contextPath + url, {
						params : {
							topic : topic,
							ip  : ip,
							alarm : check
						}
					}).success(function(response) {
					});
				} else if ("active" == type) {
					url += "active";
					$http.get(window.contextPath + url, {
						params : {
							topic : topic,
							ip  : ip,
							active : check
						}
					}).success(function(response) {
					});
				} else {
					return;
				}

			}

		} ]);