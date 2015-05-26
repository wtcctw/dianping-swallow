module.controller('UserController', ['$scope', '$http', 
        function($scope, $http){
			$scope.username = "";
			$scope.name = "";
			$scope.namelist = "";
			$scope.login    = false;
			$scope.logout   = true;
			
			$scope.isadmin = false;
			//------------for judge whether it is admin or not-----------------------
			if($scope.username.length == 0){
				$http({
					method : 'GET',
					url : window.contextPath + '/console/admin/queryadminandlogin'
				}).success(function(data, status, headers, config) {
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

