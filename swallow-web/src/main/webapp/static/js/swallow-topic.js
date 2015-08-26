module.factory('Paginator', function(){
	return function(fetchFunction, pageSize, entity){
		var paginator = {
				hasNextVar: false,
				handleResult: function(object){
					var whitelist = object.whitelist;
					for(var i = 0; i < this.currentPageItems.length; ++i){
						if(typeof(whitelist) != "undefined" && whitelist.indexOf(this.currentPageItems[i].name) != -1){
							this.currentPageItems[i]["alarm"] = false;
						}else{
							this.currentPageItems[i]["alarm"] = true;
						}
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
						
						var object = new Object();
						self.handleResult(object);
						
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
				var fetchFunction = function(entity, callback){
				
				$http.post(window.contextPath + $scope.suburl, entity).success(callback);
		};
			$scope.name = "";
			$scope.prop = "";
			
			$scope.suburl = "/console/topic/list";
			$scope.topicnum = 30;

			
			//for modal
			$scope.topicname = "";
			$scope.topicprop = "";
			$scope.topictime = "";
			$scope.topicalarm = "";
			$scope.blankprop = true;
			
			$scope.topicEntry = {};
			$scope.topicEntry.name;
			$scope.topicEntry.prop;
			$scope.topicEntry.producerAlarm;
			$scope.topicEntry.consumerAlarm;
			$scope.topicEntry.consumerIdWhiteList;
			$scope.topicEntry.producerServer;
			$scope.topicEntry.sendpeak;
			$scope.topicEntry.sendvalley;
			$scope.topicEntry.sendfluctuation;
			$scope.topicEntry.sendfluctuationBase;
			$scope.topicEntry.delay;
			
			$scope.setModalInput = function(index){
				if(typeof($scope.searchPaginator.currentPageItems[index].consumerIdWhiteList) != "undefined"){
					var wl = $scope.searchPaginator.currentPageItems[index].consumerIdWhiteList;
					$('#whitelist').tagsinput('removeAll');
					if(wl != null && wl.length > 0){
						var list = wl.split(",");
						for(var i = 0; i < list.length; ++i)
							$('#whitelist').tagsinput('add', list[i]);
					}
				}else{
					$('#whitelist').tagsinput('removeAll');
				}
				
				if(typeof($scope.searchPaginator.currentPageItems[index].prop) != "undefined"){
					var prop = $scope.searchPaginator.currentPageItems[index].prop;
					$('#prop').tagsinput('removeAll');
					if(prop != null && prop.length > 0){
						var list = prop.split(",");
						for(var i = 0; i < list.length; ++i)
							$('#prop').tagsinput('add', list[i]);
					}
				}else{
					$('#prop').tagsinput('removeAll');
				}
				
				if(typeof($scope.searchPaginator.currentPageItems[index].producerServer) != "undefined"){
					var producerServer = $scope.searchPaginator.currentPageItems[index].producerServer;
					$('#producerServer').tagsinput('removeAll');
					if(producerServer != null && producerServer.length > 0){
						var list = producerServer.split(",");
						for(var i = 0; i < list.length; ++i)
							$('#producerServer').tagsinput('add', list[i]);
					}
				}else{
					$('#producerServer').tagsinput('removeAll');
				}
				
				$scope.topicEntry.id = $scope.searchPaginator.currentPageItems[index].id;
				$scope.topicEntry.name = $scope.searchPaginator.currentPageItems[index].name;
				$scope.topicEntry.producerAlarm = $scope.searchPaginator.currentPageItems[index].producerAlarm;
				$scope.topicEntry.consumerAlarm = $scope.searchPaginator.currentPageItems[index].consumerAlarm;
				$scope.topicEntry.sendpeak = $scope.searchPaginator.currentPageItems[index].sendpeak;
				$scope.topicEntry.sendvalley = $scope.searchPaginator.currentPageItems[index].sendvalley;
				$scope.topicEntry.sendfluctuation = $scope.searchPaginator.currentPageItems[index].sendfluctuation;
				$scope.topicEntry.sendfluctuationBase = $scope.searchPaginator.currentPageItems[index].sendfluctuationBase;
				$scope.topicEntry.delay = $scope.searchPaginator.currentPageItems[index].delay;
			}
			
			$scope.refreshpage = function(myForm){
				if ($scope.topicEntry.sendpeak < $scope.topicEntry.sendvalley){
					alert("峰值不能小于谷值");
					return;
				}
				$scope.topicEntry.consumerIdWhiteList = $("#whitelist").val();
				$scope.topicEntry.prop = $("#prop").val();
				$scope.topicEntry.producerServer = $("#producerServer").val();
				$('#myModal').modal('hide');
				var param = JSON.stringify($scope.topicEntry);
				
				$http.post(window.contextPath + '/console/topic/update', $scope.topicEntry).success(function(response) {
					$scope.query.topic = $scope.topicEntry.name;
					$scope.searchPaginator = Paginator(fetchFunction, $scope.topicnum, $scope.query);
		    	});
		    	
		    }
			
/*			$scope.refreshpage = function(myForm){
	        	$scope.topictime = $("#datetimepicker").val();
	        	$scope.topicalarm = $("#alarmselect").val();
	        	if($scope.topicprop.length == 0 && !$scope.blankprop){
	        		$scope.dialog($scope.topicname, $scope.topicprop, $scope.topictime, $scope.topicalarm);
	        	}
	        	else{
	        		$('#myModal').modal('hide');
	        		var param = new Object();
	    			param.topic = $scope.topicname;
	    			param.prop = $scope.topicprop;
	    			param.time = $scope.topictime;
		        	
		        	$.ajax({
		        	    type: "POST",
		        	    url: window.contextPath + '/api/topic/edittopic',
		        	    dataType: "json",
		        	    data: param,
		        	    success: function(data) {
		        			$scope.query.topic = $scope.topicname;
		        			$scope.query.prop = "";
		        	    	$scope.searchPaginator = Paginator(fetchFunction, $scope.topicnum, $scope.query );
		        	    }
		        	});
	        	}
	        }*/
			
			$rootScope.doedit = function(topicname, topicprop, topictime){
				$('#myModal').modal('hide');
        		var param = new Object();
    			param.topic = $scope.topicname;
    			param.prop = $scope.topicprop;
    			param.time = $scope.topictime;
	        	$.ajax({
	        	    type: "POST",
	        	    url: window.contextPath + '/api/topic/edittopic',
	        	    dataType: "json",
	        	    data: param,
	        	    success: function(data) {
	        	    	$scope.query.topic = $scope.topicname;
	        	    	$scope.query.prop = "";
	        	    	$scope.searchPaginator = Paginator(fetchFunction, $scope.topicnum, $scope.query);
	        	    }

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
						
			$scope.setTopicName = function(topic){
				localStorage.setItem("topic", topic);
			}
			
			//发送默认请求
			$scope.query = new Object();
			$scope.query.topic = $scope.name;
			$scope.query.prop = $scope.prop;
			$scope.searchPaginator = Paginator(fetchFunction, $scope.topicnum, $scope.query);
			
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
								$scope.name = c;
								$scope.query.topic = $scope.name;
								$scope.query.prop = $scope.prop;
								$scope.searchPaginator = Paginator(fetchFunction, $scope.topicnum, $scope.query);		
								return c;
							}
						})
					}).error(function(data, status, headers, config) {
					});
					
					// search topic name with specific prop
					$http({
						method : 'GET',
						url : window.contextPath + '/console/topic/proposal'
					}).success(function(data, status, headers, config) {
//						$("#searchprop").typeahead({
//							items: 16, 
//							source : data.first,
//							updater : function(c) {
//								$scope.prop = c;
//								$scope.query.topic = $scope.name;
//								$scope.query.prop = $scope.prop;
//								$scope.searchPaginator = Paginator(fetchFunction, $scope.topicnum, $scope.query);		
//								return c;
//							}
//						})
						//work
						$('#prop').tagsinput({
							  typeahead: {
								  items: 16,
								  source: data,
								  displayText: function(item){ return item;}  //necessary
							  }
						});
		        		$('#prop').typeahead().data('typeahead').source = data.second;
		        		//$('#searchprop').typeahead().data('typeahead').source = data.first;
					}).error(function(data, status, headers, config) {
					});
					
			}
			
			$scope.changeproduceralarm = function(topic, index){
				var id = "#palarm" + index;
				var check = $(id).prop('checked');

				$http.get(window.contextPath + '/console/topic/producer/alarm', {
					params : {
						topic : topic,
						alarm: check } }).success(function(response) {
	        	});
			}
			
			$scope.changeconsumeralarm = function(topic, index){
				var id = "#calarm" + index;
				var check = $(id).prop('checked');
				$http.get(window.contextPath + '/console/topic/consumer/alarm', {
					params : {
						topic : topic,
						alarm: check } }).success(function(response) {
	        	});
			}
			
}]);

