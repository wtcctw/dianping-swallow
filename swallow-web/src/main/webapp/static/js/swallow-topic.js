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
        function($scope, $http, Paginator){
				var fetchFunction = function(offset, limit, name, prop, dept, callback){
				var transFn = function(data){
					return $.param(data);
				}
				var postConfig = {
						transformRequest: transFn
				};
				var data = {'offset' : offset,
										'limit': limit,
										'topic': name,
										'prop': prop,
										'dept': dept};
				$http.get(window.contextPath + $scope.suburl, {
					params : {
						offset : offset,
						limit : limit,
						topic: name,
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
				//clear all first
				$('#topicprops').tagsinput('removeAll');
				if(prop != null && prop.length > 0){
					var props = prop.split(",");
					for(var i = 0; i < props.length; ++i)
						$('#topicprops').tagsinput('add', props[i]);
				}
				$scope.topicprop  = prop;
				$scope.topicdept = dept;
				$scope.topictime = time;
			}
			
			$scope.refreshpage = function(myForm){
	        	$('#myModal').modal('hide');
	        	$scope.topictime = $("#datetimepicker").val();
	        	$scope.topicprop = $("#topicprops").val();
	        	$http.post(window.contextPath + '/console/topic/auth/edittopic', {"topic":$scope.topicname,"prop":$scope.topicprop,
	        		"dept":$scope.topicdept,"time":$scope.topictime}).success(function(response) {
					$scope.searchPaginator = Paginator(fetchFunction, $scope.topicnum, $scope.topicname , "" , "");
	        	});
	        }
						
			$scope.setTopicName = function(name){
				localStorage.setItem("name", name);
			}
			
			//display different view for different login user
			$scope.firstaccess = false;
			$scope.$on('ngLoadFinished',  function (ngLoadFinishedEvent, admin, user){
				if(!$scope.firstaccess){
					if(!admin)
						$scope.prop = user;  //if not admin, show all topic, so that it can edit
					$scope.searchPaginator = Paginator(fetchFunction, $scope.topicnum, $scope.name , $scope.prop , $scope.dept);
					$scope.prop = "";
					$scope.firstaccess = true;
				}
				else
					$scope.searchPaginator = Paginator(fetchFunction, $scope.topicnum, $scope.name , $scope.prop , $scope.dept);

			});
			
			
			$scope.$on('ngRepeatFinished',  function (ngRepeatFinishedEvent) {
				$scope.initpage();
			});
			
			$scope.initpage = function(){
				$("a[href='/console/topic'] button").removeClass("btn-info");
				$("a[href='/console/topic'] button").addClass("btn-purple");
				$scope.adminornot = localStorage.getItem("isadmin");
				
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
					});
					
					// search topic name with specific prop
					$http({
						method : 'GET',
						url : window.contextPath + '/console/topic/propdept'
					}).success(function(data, status, headers, config) {
						var props = data.prop;
						var depts = data.dept;
						$("#searchprop").typeahead({
							source : props,
							updater : function(c) {
								$scope.prop = c
								$scope.searchPaginator = Paginator(fetchFunction, $scope.topicnum, $scope.name , $scope.prop , $scope.dept);		
								return c;
							}
						})
						$("#searchdept").typeahead({
							source : depts,
							updater : function(c) {
								$scope.dept = c
								$scope.searchPaginator = Paginator(fetchFunction, $scope.topicnum, $scope.name , $scope.prop , $scope.dept);		
								return c;
							}
						})
						//work
						$('#topicprops').tagsinput({
							  typeahead: {      
								  source: props,
								  displayText: function(item){ return item;}  //necessary
							  }
						});
					}).error(function(data, status, headers, config) {
					});
			}
			
}]);

