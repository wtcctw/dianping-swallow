module.factory('Paginator', function(){
	return function(fetchFunction, pageSize,  name, prop, dept){
		var paginator = {
				hasNextVar: false,
				fetch: function(page){
					this.currentOffset = (page - 1) * pageSize;
					this._load();
				},
				next: function(){
					if(this.hasNextVar){
						this.currentOffset += pageSize;
						this._load();
					}
				},
				_load: function(){
					var self = this;  //must use  self
					self.currentPage = Math.floor(self.currentOffset/pageSize) + 1;
					fetchFunction(this.currentOffset, pageSize + 1, name, prop, dept, function(data){
						items = data.admin;
						length = data.size;
						self.totalPage = Math.ceil(length/pageSize);
						self.endPage = self.totalPage;
						//生成链接
						if (self.currentPage > 1 && self.currentPage < self.totalPage) {
							self.pages = [
			                    self.currentPage - 1,
			                    self.currentPage,
			                    self.currentPage + 1
			                ];
			            } else if (self.currentPage == 1 && self.totalPage > 1) {
			            	self.pages = [
			                    self.currentPage,
			                    self.currentPage + 1
			                ];
			            } else if (self.currentPage == self.totalPage && self.totalPage > 1) {
			            	self.pages = [
			                    self.currentPage - 1,
			                    self.currentPage
			                ];
			            }
						self.currentPageItems = items.slice(0, pageSize);
						for(var i = 0; i < self.currentPageItems.length; ++i){
							if(self.currentPageItems[i].role == 0)
								self.currentPageItems[i].role = "Administrator";
							else
								self.currentPageItems[i].role = "User";
						}
						self.hasNextVar = items.length === pageSize + 1;
					});
				},
				hasNext: function(){
					return this.hasNextVar;
				},
				previous: function(){
					if(this.hasPrevious()){
						this.currentOffset -= pageSize;
						this._load();
					}
				},
				hasPrevious: function(){
					return this.currentOffset !== 0;
				},
				totalPage: 1,
				pages : [],
				lastpage : 0,
				currentPage: 1,
				endPage: 1,
				
				currentPageItems: [],
				currentOffset: 0
		};
		
		//加载第一页
		paginator._load();
		return paginator;
	};
});

module.controller('AdministratorController', ['$scope', '$http','Paginator',
        function($scope, $http, Paginator){
	
	var fetchFunction = function(offset, limit, name, email, role, callback){
		var transFn = function(data){
			return $.param(data);
		}
		var postConfig = {
				transformRequest: transFn
		};
		var data = {'offset' : offset,
								'limit': limit,
								'name': name,
								'email': email,
								'role': role};
			$http.get(window.contextPath + $scope.suburl, {
				params : {
					offset : offset,
					limit : limit,
					name: name,
					email: email,
					role: role
				}
			}).success(callback);
		};
		$scope.name = "";
		$scope.email = "";
		$scope.role = "";
		$scope.adminnum = 30;
		
		$scope.suburl = "/console/admin/admindefault";
	
		$("a[href='/console/administrator'] button").removeClass("btn-info");
		$("a[href='/console/administrator'] button").addClass("btn-purple");
		
		//edit admin and save it in database
		$scope.adminrole = "";
		$scope.adminemail = "";
		$scope.adminname = "";
		$scope.refreshpage = function(myForm){
        	$('#myModal').modal('hide');
        	//for selected item, use jquery to get value
        	$scope.adminrole = $("#roleselect").val();
        	$http.post(window.contextPath + '/console/admin/createadmin', {"name":$scope.adminname, 
        		"email": $scope.adminemail,"role":$scope.adminrole})
        		.success(function(response) {
        			$scope.searchPaginator = Paginator(fetchFunction, $scope.adminnum, $scope.name , $scope.email , $scope.role);
        	});
        }
		
		//delete admin
		$scope.removerecord = function(name, email){
			$http.post(window.contextPath + '/console/admin/removeadmin', {"name":name, "email": email})
        		.success(function(response) {
        			$scope.searchPaginator = Paginator(fetchFunction, $scope.adminnum, $scope.name , $scope.email , $scope.role);
        	});
		}
		
		$scope.setModalInput = function(name,email,date,role){
			$scope.adminname   = name;
			$scope.adminemail  = email;
			$("#roleselect").val(role);
		}
		
		$scope.clearModal = function(){
			$scope.adminname   = "";
			$scope.adminemail  = "";
			$("#roleselect").val("Administrator");
		}
		
		//add below to control access
		$scope.adminornot = false;
		$scope.$on('ngLoadFinished',  function (ngLoadFinishedEvent, admin, user){
			$scope.adminornot = admin;
			$scope.searchPaginator = Paginator(fetchFunction, $scope.adminnum, $scope.name , $scope.email , $scope.role);
			
			$http({
				method : 'GET',
				url : window.contextPath + '/console/admin/queryvisits'
			}).success(function(data, status, headers, config) {
				var visitList = data;
				$("#modalname").typeahead({
					source : visitList,
					updater : function(c) {
						$scope.adminname = c;  //have to add this statement, or adminnam will be not completed
						return c;
					}
				})
			}).error(function(data, status, headers, config) {
			});
		});
		
}]);

