module.factory('Paginator', function(){
	return function(fetchFunction, pageSize, entity){
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
					entity.offset = this.currentOffset;
					entity.limit = pageSize + 1;
					fetchFunction( entity, function(data){
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

module.controller('ApplicationResourceController', ['$rootScope', '$scope', '$http', 'Paginator', 'ngDialog',
        function($rootScope, $scope, $http, Paginator, ngDialog){
				var fetchFunction = function(entity, callback){
				
				$http.post(window.contextPath + $scope.suburl, entity).success(callback);
		};
			$scope.suburl = "/console/application/list";
			$scope.numrecord = 30;
			
			$scope.searchapplication = "";
			
			$scope.applicationEntry = {};
			$scope.applicationEntry.application;
			$scope.applicationEntry.email;
			$scope.applicationEntry.opManager;
			$scope.applicationEntry.opMobile;
			$scope.applicationEntry.opEmail;
			$scope.applicationEntry.dpManager;
			$scope.applicationEntry.dpMobile;
			
			$scope.setModalInput = function(index){
				
				$scope.applicationEntry.id = $scope.searchPaginator.currentPageItems[index].id;
				$scope.applicationEntry.application = $scope.searchPaginator.currentPageItems[index].application;
				$scope.applicationEntry.email = $scope.searchPaginator.currentPageItems[index].email;
				$scope.applicationEntry.opManager = $scope.searchPaginator.currentPageItems[index].opManager;
				$scope.applicationEntry.opMobile = $scope.searchPaginator.currentPageItems[index].opMobile;
				$scope.applicationEntry.opEmail = $scope.searchPaginator.currentPageItems[index].opEmail;
				$scope.applicationEntry.dpManager = $scope.searchPaginator.currentPageItems[index].dpManager;
				$scope.applicationEntry.dpMobile = $scope.searchPaginator.currentPageItems[index].dpMobile;
			}
			
			$scope.refreshpage = function(myForm){
				$('#myModal').modal('hide');
				var param = JSON.stringify($scope.applicationEntry);
				
				$http.post(window.contextPath + '/console/application/create', $scope.applicationEntry).success(function(response) {
					$scope.query.application = $scope.applicationEntry.application;
					$scope.searchPaginator = Paginator(fetchFunction, $scope.numrecord, $scope.query);
		    	});
		    	
		    }
						
			//发送默认请求
			$scope.query = new Object();
			var tmpapplication = localStorage.getItem("application");
			if(tmpapplication != null){
				
				if(tmpapplication.indexOf(',') == -1){
					$scope.searchapplication = tmpapplication;
					$scope.query.application = tmpapplication;
				}else{
					$scope.query.application = tmpapplication;
					$scope.searchapplication = "";
				}
				localStorage.clear();
			}

			var tmplocation = location.search;
			if(tmplocation != "" && tmplocation.length > 12 && tmplocation.substr(0,13)=="?application="){//application get
				var subtmpapplication = tmplocation.substring(13);
				$scope.searchapplication = subtmpapplication;
				$scope.query.application = subtmpapplication;
				$scope.searchPaginator = Paginator(fetchFunction, $scope.numrecord, $scope.query);//topic
			}else{ 
				tmplocation = null;
				$scope.searchPaginator = Paginator(fetchFunction, $scope.numrecord, $scope.query);
			}
			
			$scope.initpage = function(){

				 $http({
						method : 'GET',
						url : window.contextPath + '/console/application/applicationname'
					}).success(function(data, status, headers, config) {
						var apps = data;
						$("#searchapplication").typeahead({
							items: 16, 
							source : apps,
							updater : function(c) {
								$scope.searchapplication = c;
								$scope.query.application = $scope.searchapplication;
								$scope.searchPaginator = Paginator(fetchFunction, $scope.numrecord, $scope.query);		
								return c;
							}
						})
					}).error(function(data, status, headers, config) {
					});
					
			}
			
			$scope.initpage();
			
			$scope.dialog = function(app) {
				$rootScope.app = app;
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
										<button type="button" class="btn btn-primary" ng-click="removerecord(app)&&closeThisDialog()">确定</button>\
									</div>\
								</div>\
							</div>',
							plain : true,
							className : 'ngdialog-theme-default'
					});
			};
			
			$rootScope.removerecord = function(app){
				$http.get(window.contextPath + "/console/application/remove", {
					params : {
						application : app
					}
				}).success(function(data){
					$scope.searchPaginator = Paginator(fetchFunction, $scope.numrecord, $scope.query);
				});
				return true;
			}
			
}]);

