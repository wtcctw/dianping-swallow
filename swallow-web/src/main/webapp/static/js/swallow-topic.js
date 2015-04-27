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
						items = data.topic;
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

module.controller('TopicController', ['$scope', '$http', 'Paginator',
        function($scope, $http, Paginator, instance){
				var fetchFunction = function(offset, limit, name, prop, dept, callback){
				var transFn = function(data){
					return $.param(data);
				}
				var postConfig = {
						transformRequest: transFn
				};
				var data = {'offset' : offset,
										'limit': limit,
										'name': name,
										'prop': prop,
										'dept': dept};
				$http.get(window.contextPath + $scope.suburl, {
					params : {
						offset : offset,
						limit : limit,
						name: name,
						prop: prop,
						dept: dept
					}
				}).success(callback);
			};
			$scope.name = "";
			$scope.prop = "";
			$scope.dept = "";
			
			$scope.suburl = "/console/topic/topicdefault";
			$scope.topicnum = 30;

			
			//for modal
			$scope.topicname = "";
			$scope.topicprop = "";
			$scope.topicdept = "";
			$scope.topictime = "";
			$scope.setModalInput = function(name,prop,dept,time){
				$scope.topicname = name;
				$scope.topicprop  = prop;
				$scope.topicdept = dept;
				$scope.topictime = time;
			}
						
			$scope.setTopicName = function(name){
				localStorage.setItem("name", name);
			}
			
			$scope.searchPaginator = Paginator(fetchFunction, $scope.topicnum, $scope.name , $scope.prop , $scope.dept);
			
			$scope.$on('ngRepeatFinished',  function (ngRepeatFinishedEvent) {
		          //下面是在table render完成后执行的js
				 $http({
						method : 'GET',
						url : window.contextPath + '/console/topic/namelist'
					}).success(function(data, status, headers, config) {
						var topicNameList = jQuery.unique(data);
						$("#searchname").typeahead({
							source : topicNameList,
							updater : function(c) {
								$scope.name = c
								$scope.searchPaginator = Paginator(fetchFunction, $scope.topicnum, $scope.name , $scope.prop , $scope.dept);		
								return c;
							}
						})
					}).error(function(data, status, headers, config) {
						alert("响应错误", data);
					});
					
					// search topic name with specific prop
					$http({
						method : 'GET',
						url : window.contextPath + '/console/topic/proplist'
					}).success(function(data, status, headers, config) {
						var topicPropList = data;
						$("#searchprop").typeahead({
							source : topicPropList,
							updater : function(c) {
								$scope.prop = c
								$scope.searchPaginator = Paginator(fetchFunction, $scope.topicnum, $scope.name , $scope.prop , $scope.dept);		
								return c;
							}
						})
					}).error(function(data, status, headers, config) {
						alert("响应错误", data);
					});
					
					// search topic name with specific dept
					$http({
						method : 'GET',
						url : window.contextPath + '/console/topic/deptlist'
					}).success(function(data, status, headers, config) {
						var topicDeptList = data;
						$("#searchdept").typeahead({
							source : topicDeptList,
							updater : function(c) {
								$scope.dept = c
								$scope.searchPaginator = Paginator(fetchFunction, $scope.topicnum, $scope.name , $scope.prop , $scope.dept);		
								return c;
							}
						})
					}).error(function(data, status, headers, config) {
						alert("响应错误", data);
					});
			 });
}]);

