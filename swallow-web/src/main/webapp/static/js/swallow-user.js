module.controller('UserController', ['$scope', '$http', 
        function($scope, $http){
			$scope.username = "";
			$scope.name = "";
			$scope.namelist = "";
			$scope.login    = false;
			$scope.logout   = true;
			
			$scope.isadmin = false;
			//for judge whether it is admin or not
			if($scope.username.length == 0){
				$http({
					method : 'GET',
					url : window.contextPath + '/console/queryadminandlogin'
				}).success(function(data, status, headers, config) {
					if(data.loginname == null)
						$scope.name = "";
					else
						$scope.name = data.loginname.replace(/"/g, "");
					
					if(data.adminname == null)
						$scope.namelist = "";
					else
						$scope.namelist = data.adminname.replace(/"/g, "");
					
					$scope.username = "欢迎 " + $scope.name;
					
					if($scope.namelist == ""){  //namelist is empty, 
						if($scope.name == "李明冬")
							$scope.isadmin = true;
					}
					else{
						if($scope.name == "") //not login
							;  //nothing to do
						else if($scope.namelist.indexOf($scope.name) < 0) //login but not contains
							;
						else
							$scope.isadmin = true;
					}
					
					localStorage.setItem("isadmin", $scope.isadmin); //just for pages which need more specific control
					$scope.$broadcast("ngLoadFinished", $scope.isadmin);
				}).error(function(data, status, headers, config) {
					
				});
			}
			
			$scope.logoutservice = function(){
				$scope.username = "";
				$scope.login    = true;
				$scope.logout   = false;
				
				$http({
					method : 'GET',
					url : "https://sso.a.alpha.dp:8443/logout?service=http%3A%2F%2Flocalhost:8080"
				}).success(function(data, status, headers, config) {
					
				}).error(function(data, status, headers, config) {
				});
			};
			
}]);

