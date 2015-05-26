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
							else if(self.currentPageItems[i].role == 3)
								self.currentPageItems[i].role = "User";
							else
								self.currentPageItems[i].role = "Visitor";
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

module.controller('AdministratorController', ['$rootScope','$scope', '$http','Paginator', 'ngDialog',
        function($rootScope, $scope, $http, Paginator, ngDialog){
	
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
								'role': role};
			$http.get(window.contextPath + $scope.suburl, {
				params : {
					offset : offset,
					limit : limit,
					name: name,
					role: role
				}
			}).success(callback);
		};
		$scope.name = "";
		$scope.email = "";
		$scope.role = "";
		$scope.adminnum = 30;
		
		$scope.suburl = "/console/admin/auth/admindefault";
	
		$("a[href='/console/administrator'] button").removeClass("btn-info");
		$("a[href='/console/administrator'] button").addClass("btn-purple");
		
		//edit admin and save it in database
		$scope.adminrole = "";
		$scope.adminname = "";
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
		
		//delete admin
		$rootScope.removerecord = function(name){
			$http.post(window.contextPath + '/console/admin/auth/removeadmin', {"name":name})
        		.success(function(response) {
        			$scope.searchPaginator = Paginator(fetchFunction, $scope.adminnum, $scope.name , $scope.role);
        	});
			return true;
		}
		
		$scope.setModalInput = function(name,role){
			$scope.adminname   = name;
			if($("#roleselect").val() == "VISITOR")
				$("#roleselect").val("");
			else
				$("#roleselect").val(role);
		}
		
		$scope.clearModal = function(){
			$scope.adminname   = "";
			$("#roleselect").val("Administrator");
		}
		
		//add below to control access
		$scope.adminornot = false;
		$scope.$on('ngLoadFinished',  function (ngLoadFinishedEvent, admin, user){
			$scope.adminornot = admin;
			$scope.searchPaginator = Paginator(fetchFunction, $scope.adminnum, $scope.name , $scope.role);
			
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
		
		$scope.dialog = function(name) {
			$rootScope.removeName = name;
			ngDialog.open({
							template : '\
							<div class="widget-box">\
							<div class="widget-header">\
								<h4 class="widget-title">警告</h4>\
							</div>\
							<div class="widget-body">\
								<div class="widget-main">\
									<p class="alert alert-info">\
										您确认要删除吗？\
									</p>\
								</div>\
								<div class="modal-footer">\
									<button type="button" class="btn btn-default" ng-click="closeThisDialog()">取消</button>\
									<button type="button" class="btn btn-primary" ng-click="removerecord(removeName)&&closeThisDialog()">确定</button>\
								</div>\
							</div>\
						</div>',
						plain : true,
						className : 'ngdialog-theme-default'
				});
		};
		
}]);

