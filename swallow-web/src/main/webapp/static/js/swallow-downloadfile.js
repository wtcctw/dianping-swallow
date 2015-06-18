module.controller('DownloadController', ['$rootScope', '$scope', '$http', 'Paginator', 'ngDialog',
                function($rootScope, $scope, $http, Paginator, ngDialog){
	
	$scope.name = "";
	$http({
		method : 'GET',
		url : window.contextPath + '/console/topic/namelist'
	}).success(function(data, status, headers, config) {
		var topicNameList = data;
		$("#searchname").typeahead({
			items: 16, 
			source : topicNameList,
			updater : function(c) {
				$scope.name = c
				$http.get(window.contextPath + "/console/message/auth/dump", {
					params : {
						topic: $scope.name,
						startdt: "",
						stopdt: ""
					}
				}).success(function(data){
					$scope.files = data.file;
				});		
				return c;
			}
		})
	}).error(function(data, status, headers, config) {
	});
	
	$scope.showornot = true;
	$scope.files = [];
	var tmpfile = JSON.parse(localStorage.getItem("file"));
	if(typeof(tmpfile) != "undefined"){
		$scope.files = tmpfile;
		localStorage.clear("file");
	}
	$scope.update = function(){
		$('.progress-bar').css({'width':'80%'}).find('span').html('80%');
	}
			
}]);

