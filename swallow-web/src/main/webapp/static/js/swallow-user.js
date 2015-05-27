module.controller('UserController', ['$scope', '$http', 
        function($scope, $http){
			$scope.username = "";
			$scope.name = "";
			$scope.namelist = "";
			$scope.login    = false;
			$scope.logout   = true;
			
			$scope.logouturl = "";
			$scope.isadmin = false;
			//------------for judge whether it is admin or not-----------------------
			if($scope.username.length == 0){
				$http({
					method : 'GET',
					url : window.contextPath + '/console/admin/queryadminandlogin'
				}).success(function(data, status, headers, config) {
					if(data.env == "alpha"){
						$scope.logouturl = "logout?service=http%3A%2F%2Falpha.swallow.dp%3A8080";
					}
					else if(data.env == "qa"){
						$scope.logouturl = "logout?service=http%3A%2F%2Fbeta.swallow.dp%3A8080";
					}
					else if(data.env == "ppe"){
						$scope.logouturl = "logout?service=http%3A%2F%2Fppe.swallow.dp%3A8080";
					}else {
						$scope.logouturl = "logout?service=http%3A%2F%2Fswallow.dp%3A8080";
					}
					if(data.loginname == null)
						$scope.name = "";
					else
						$scope.name = data.loginname.replace(/"/g, "");
					$scope.username = "欢迎 " + $scope.name;
					//set lmd to default admin
					if(data.admin == true || $scope.name == "mingdong.li"){
						$scope.isadmin = true;
						$("#navbar-admin").css('display','block');
					}
					else
						$scope.isadmin = false;
					
					localStorage.setItem("isadmin", $scope.isadmin); //just for pages which need more specific control
					$scope.$broadcast("ngLoadFinished", $scope.isadmin, $scope.name);
				}).error(function(data, status, headers, config) {
					
				});
			}
			
}]);

