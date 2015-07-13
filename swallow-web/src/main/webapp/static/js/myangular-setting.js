module.controller('ProducerServerSettingController', function($scope, $http) {

	// search
	$http({
		method : 'POST',
		url : window.contextpath + '/console/monitor/topiclist/get'
	}).success(function(topicList, status, headers, config) {
		
		$("#consumer-div").typeahead({
			source : topicList,
			updater : function(c) {
				window.location = window.contextpath + "/console/monitor/consumer/"+c+"/delay";
				return c;
			}
		})
	}).error(function(data, status, headers, config) {
		 app.appError("响应错误", data);
	});

	$scope.getDelay = function(topicName) {
		renderGraph('/console/monitor/consumer/' + topicName + '/delay/get', "container", $http);
	};
});

module.controller('ConsumerServerSettingController', function($scope, $http) {

	// search
	$http({
		method : 'POST',
		url : window.contextpath + '/console/monitor/topiclist/get'
	}).success(function(topicList, status, headers, config) {
		
		$("#consumer-div").typeahead({
			source : topicList,
			updater : function(c) {
				window.location = window.contextpath + "/console/monitor/consumer/"+c+"/delay";
				return c;
			}
		})
	}).error(function(data, status, headers, config) {
		 app.appError("响应错误", data);
	});

	$scope.getDelay = function(topicName) {
		renderGraph('/console/monitor/consumer/' + topicName + '/delay/get', "container", $http);
	};
});

module.controller('ConsumerIdSettingController', function($scope, $http) {

	// search
	$http({
		method : 'POST',
		url : window.contextpath + '/console/monitor/topiclist/get'
	}).success(function(topicList, status, headers, config) {
		
		$("#consumer-div").typeahead({
			source : topicList,
			updater : function(c) {
				window.location = window.contextpath + "/console/monitor/consumer/"+c+"/delay";
				return c;
			}
		})
	}).error(function(data, status, headers, config) {
		 app.appError("响应错误", data);
	});

	$scope.getDelay = function(topicName) {
		renderGraph('/console/monitor/consumer/' + topicName + '/delay/get', "container", $http);
	};
	
	$scope.refreshpage = function(myForm){
    	$('#myModal').modal('hide');
    	//for selected item, use jquery to get value
    	$scope.adminrole = $("#roleselect").val();
    	$http.post(window.contextPath + '/console/admin/auth/createadmin', {"name":$scope.adminname, 
    		"role":$scope.adminrole})
    		.success(function(response) {
    			$scope.searchPaginator = Paginator(fetchFunction, $scope.adminnum, $scope.name , $scope.role);
    	});
    }
});

module.controller('TopicSettingController', function($scope, $http) {

	// search
	$http({
		method : 'POST',
		url : window.contextpath + '/console/monitor/topiclist/get'
	}).success(function(topicList, status, headers, config) {
		
		$("#consumer-div").typeahead({
			source : topicList,
			updater : function(c) {
				window.location = window.contextpath + "/console/monitor/consumer/"+c+"/delay";
				return c;
			}
		})
	}).error(function(data, status, headers, config) {
		 app.appError("响应错误", data);
	});

	$scope.getDelay = function(topicName) {
		renderGraph('/console/monitor/consumer/' + topicName + '/delay/get', "container", $http);
	};
});