module.factory('Paginator', function(){
	return function(fetchFunction, pageSize,  name, prop){
		var paginator = {
				hasNextVar: false,
				loadalarm : function(){
					
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
					fetchFunction(this.currentOffset, pageSize + 1, name, prop, function(data){
						items = data.first.second;
						length = data.first.first;
						whitelist = data.second;
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
							if(whitelist.indexOf(self.currentPageItems[i].name) != -1){
								self.currentPageItems[i]["alarm"] = false;
							}else{
								self.currentPageItems[i]["alarm"] = true;
							}
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

module.controller('TopicController', ['$rootScope', '$scope', '$http', 'Paginator', 'ngDialog',
        function($rootScope, $scope, $http, Paginator, ngDialog){
				var fetchFunction = function(offset, limit, name, prop, callback){
				var transFn = function(data){
					return $.param(data);
				}
				var postConfig = {
						transformRequest: transFn
				};
				var data = {'offset' : offset,
										'limit': limit,
										'topic': name,
										'prop': prop};
				$http.get(window.contextPath + $scope.suburl, {
					params : {
						offset : offset,
						limit : limit,
						topic: name,
						prop: prop
					}
				}).success(callback);
			};
			$scope.name = "";
			$scope.prop = "";
			
			$scope.suburl = "/console/topic/topicdefault";
			$scope.topicnum = 30;

			
			//for modal
			$scope.topicname = "";
			$scope.topicprop = "";
			$scope.topictime = "";
			$scope.topicalarm = "";
			$scope.blankprop = true;
			$scope.setModalInput = function(name,prop,time){
				$scope.topicname = name;
				$('#topicprops').tagsinput('removeAll');
				if(prop != null && prop.length > 0){
					var props = prop.split(",");
					for(var i = 0; i < props.length; ++i)
						$('#topicprops').tagsinput('add', props[i]);
					$scope.blankprop = false;
				}
				$scope.topicprop  = prop;
				$scope.topictime = time;
			}
			
			$scope.refreshpage = function(myForm){
	        	$scope.topictime = $("#datetimepicker").val();
	        	$scope.topicprop = $("#topicprops").val();
	        	$scope.topicalarm = $("#alarmselect").val();
	        	if($scope.topicprop.length == 0 && !$scope.blankprop){
	        		$scope.dialog($scope.topicname, $scope.topicprop, $scope.topictime, $scope.topicalarm);
	        	}
	        	else{
	        		$('#myModal').modal('hide');
		        	$http.post(window.contextPath + '/api/topic/edittopic', {"topic":$scope.topicname,"prop":$scope.topicprop,
		        		"time":$scope.topictime}).success(function(response) {
						$scope.searchPaginator = Paginator(fetchFunction, $scope.topicnum, $scope.topicname , "" );
		        	});
	        	}
	        }
			
			$rootScope.doedit = function(topicname, topicprop, topictime){
				$('#myModal').modal('hide');
				$http.post(window.contextPath + '/api/topic/edittopic', {"topic":topicname,"prop":topicprop,
	        		"time":topictime}).success(function(response) {
					$scope.searchPaginator = Paginator(fetchFunction, 30, topicname , "");
	        	});
				return true;
			}
			
			//for deal with re transmit for selected messages
			$scope.dialog = function(topicname, topicprop, topictime) {
				$rootScope.topicname = topicname;
				$rootScope.topicprop = topicprop;
				$rootScope.topictime = topictime;
				ngDialog.open({
							template : '\
							<div class="widget-box">\
							<div class="widget-header">\
								<h4 class="widget-title">警告</h4>\
							</div>\
							<div class="widget-body">\
								<div class="widget-main">\
									<p class="alert alert-info">\
										您确认要清空消息申请人么？\
									</p>\
								</div>\
								<div class="modal-footer">\
									<button type="button" class="btn btn-default" ng-click="closeThisDialog()">取消</button>\
									<button type="button" class="btn btn-primary" ng-click="doedit(topicname,topicprop,topictime)&&closeThisDialog()">确定</button>\
								</div>\
							</div>\
						</div>',
						plain : true,
						className : 'ngdialog-theme-default'
				});
			};
						
			$scope.setTopicName = function(name){
				localStorage.setItem("name", name);
			}
			
			//发送默认请求
			$scope.searchPaginator = Paginator(fetchFunction, $scope.topicnum, $scope.name , $scope.prop);
			
			//如果topic列表返回空，则不会执行initpage
			$scope.$on('ngRepeatFinished',  function (ngRepeatFinishedEvent) {
				$scope.initpage();
			});
			
			$scope.initpage = function(){

		          //下面是在table render完成后执行的js
				 $http({
						method : 'GET',
						url : window.contextPath + '/console/topic/namelist'
					}).success(function(data, status, headers, config) {
						var topicNameList = data;
						$("#searchname").typeahead({
							items: 16, 
							source : topicNameList,
							updater : function(c) {
								$scope.name = c
								$scope.searchPaginator = Paginator(fetchFunction, $scope.topicnum, $scope.name , $scope.prop);		
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
						var props = data;
						$("#searchprop").typeahead({
							items: 16, 
							source : props,
							updater : function(c) {
								$scope.prop = c;
								$scope.searchPaginator = Paginator(fetchFunction, $scope.topicnum, $scope.name , $scope.prop);		
								return c;
							}
						})
						//work
						$('#topicprops').tagsinput({
							  typeahead: {
								  items: 16,
								  source: props,
								  displayText: function(item){ return item;}  //necessary
							  }
						});
		        		$('#topicprops').typeahead().data('typeahead').source = props;
		        		$('#searchprop').typeahead().data('typeahead').source = props;
					}).error(function(data, status, headers, config) {
					});
					
			}
			
			$scope.changealarm = function(topic, index){
				var id = "#alarm" + index;
				var check = $(id).prop('checked');
				$http.post(window.contextPath + '/api/topic/alarm', {"topic":topic,
	        		"alarm":!check}).success(function(response) {
	        			if(check){
	        				alert(topic + " : 添加告警成功");
	        			}else{
	        				alert(topic + " : 解除告警成功")
	        			}
	        	});
			}
			
}]);

