module.factory('Paginator', function(){
	return function(fetchFunction, pageSize, entity){
		var paginator = {
				hasNextVar: false,
				handleResult: function(object){
					for(var i = 0; i < this.currentPageItems.length; ++i){
						if(this.currentPageItems[i].role == "ADMINISTRATOR")
							this.currentPageItems[i].role = "Administrator";
						else if(this.currentPageItems[i].role == "USER")
							this.currentPageItems[i].role = "User";
						else
							this.currentPageItems[i].role = "Visitor";
					}
				},
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
					entity.offset = this.currentOffset;
					entity.limit = pageSize + 1;
					fetchFunction(entity, function(data){
						items = data.second;
						length = data.first;
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
						self.handleResult(new Object());
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
	
	var fetchFunction = function(entity,callback){
		$http.post(window.contextPath + $scope.suburl, entity).success(callback);
	};
		$scope.name = "";
		$scope.role = "";
		$scope.adminnum = 30;
		
		$scope.suburl = "/console/admin/auth/userlist";
	
		//edit admin and save it in database
		$scope.adminrole = "";
		$scope.adminname = "";
		
		$scope.entity = new Object();
		$scope.entity.name = $scope.name;
		$scope.entity.role = $scope.role;
		
		$scope.refreshpage = function(myForm){
        	$('#myModal').modal('hide');
        	//for selected item, use jquery to get value
        	$scope.adminrole = $("#roleselect").val();
    		$scope.entity.name = $scope.adminname;
    		$scope.entity.role = $scope.adminrole;
        	$http.post(window.contextPath + '/console/admin/auth/createadmin', $scope.entity)
        		.success(function(response) {
            		$scope.entity.name = $scope.name;
            		$scope.entity.role = $scope.role;
        			$scope.searchPaginator = Paginator(fetchFunction, $scope.adminnum, $scope.entity);
        	});
        }
		
		//delete admin
		$rootScope.removerecord = function(name){
			$http.post(window.contextPath + '/console/admin/auth/removeadmin', {"name":name})
        		.success(function(response) {
            		$scope.entity.name = $scope.name;
            		$scope.entity.role = $scope.role;
        			$scope.searchPaginator = Paginator(fetchFunction, $scope.adminnum, $scope.entity);
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
		$scope.searchPaginator = Paginator(fetchFunction, $scope.adminnum, $scope.entity);
		
		$http({
			method : 'GET',
			url : window.contextPath + '/console/admin/queryvisits'
		}).success(function(data, status, headers, config) {
			var visitList = data;
			$("#modalname").typeahead({
				items: 16, 
				source : visitList,
				updater : function(c) {
					$scope.adminname = c;  //have to add this statement, or adminnam will be not completed
					return c;
				}
			})
		}).error(function(data, status, headers, config) {
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
		
		$scope.loadrs = function(){
			$http({
				method : 'GET',
				url : window.contextPath + '/console/message/randomstring'
			}).success(function(data, status, headers, config) {
				confirm(data);
			}).error(function(data, status, headers, config) {
			});
		};
		
}]);

