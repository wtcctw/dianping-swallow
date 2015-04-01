module.controller("RuleController", function($scope, DataService, $resource,
		$http) {
	$scope.getRule = function() {
		var url = window.contextpath + '/monitor/status/rule/get';

		$http({
			method : 'GET',
			url : url
		}).success(function(data, status, headers, config) {
			if (data.errorCode == 0) {
				if (data.rule != null) {
					$scope.rule = data.rule;
				}
				$('#RuleController > div.main-content').show();
			} else {
				app.appError("响应错误", data);
			}
		}).error(function(data, status, headers, config) {
			app.appError("响应错误", data);
		});
	};
	$scope.edit = function() {
		window.location = window.contextpath + '/monitor/status/rule/edit';
	}
	$scope.cancleEdit = function() {
		window.location = window.contextpath + '/monitor/status/rule';
	}
	$scope.save = function() {
		$http({
			method : 'POST',
			data : $scope.rule,
			url : window.contextpath + '/monitor/status/rule/save'
		}).success(function(data, status, headers, config) {
			if (data.errorCode == 0) {
				window.location = window.contextpath + '/monitor/status/rule';
			} else {
				app.appError("保存失败: " + data.errorMessage);
			}
		}).error(function(data, status, headers, config) {
			app.appError("响应错误", data);
		});
	};
	$scope.getRule();
});