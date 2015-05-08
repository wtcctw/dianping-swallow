module.controller('AdministratorController', ['$scope', '$http',
        function($scope, $http){
		$("a[href='/console/administrator'] button").removeClass("btn-info");
		$("a[href='/console/administrator'] button").addClass("btn-purple");
		
		$scope.refreshpage = function(myForm){
        	$('#myModal').modal('hide');
        	$http.post(window.contextPath + '/console/editadmin', {"name":JSON.stringify($scope.namelist)})
        		.success(function(response) {
        	});
        }
		
		$scope.namelist = "";
		$http({
			method : 'GET',
			url : window.contextPath + '/console/queryadminandlogin'
		}).success(function(data, status, headers, config) {
			if(data.adminname == null)
				$scope.namelist = "";
			else
				$scope.namelist = data.adminname.replace(/"/g, "")
		}),
		
		//add below to control access
		$scope.adminornot = false;
		$scope.$on('ngLoadFinished',  function (ngLoadFinishedEvent, msg){
			$scope.adminornot = msg;
		});
//		while(localStorage.getItem("setadmindone") == null);
//		localStorage.removeItem("setadmindone"); 
		
		
}]);

