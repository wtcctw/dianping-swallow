module.factory('Paginator', function(){
	return function(fetchFunction, pageSize, entity){
		var paginator = {
				hasNextVar: false,
					fetch: function(page){
					this.currentOffset = (page - 1) * pageSize;
					this._load();
				},
				handleResult: function(object){
					for(var i = 0; i < this.currentPageItems.length; ++i){
						if(typeof(this.currentPageItems[i].producerIpInfos) != "undefined"){
							var length = this.currentPageItems[i].producerIpInfos.length;
							var ips = "";
							for(var j = 0; j < length; ++j){
								if(j == 0){
									ips += this.currentPageItems[i].producerIpInfos[j].ip;
								}else{
									ips += "," + this.currentPageItems[i].producerIpInfos[j].ip;
								}
							}
							this.currentPageItems[i].ips = ips;
						}
					}
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

module.controller('TopicController', ['$rootScope', '$scope', '$http', 'Paginator', 'ngDialog',
        function($rootScope, $scope, $http, Paginator, ngDialog){
				var fetchFunction = function(entity, callback){
				
				$http.post(window.contextPath + $scope.suburl, entity).success(callback);
		};
			$scope.topic = "";
			$scope.searchip = "";
			
			$scope.suburl = "/console/topic/list";
			$scope.topicnum = 30;
			
			$scope.topicEntry = {};
			$scope.topicEntry.topic;
			$scope.topicEntry.administrator;
			$scope.topicEntry.producerAlarm;
			$scope.topicEntry.consumerAlarm;
			$scope.topicEntry.whiteList;
			$scope.topicEntry.producerServer;
			$scope.topicEntry.sendpeak;
			$scope.topicEntry.sendvalley;
			$scope.topicEntry.sendfluctuation;
			$scope.topicEntry.sendfluctuationBase;
			$scope.topicEntry.senddelay;
			
			$scope.setModalInput = function(index){
				if(typeof($scope.searchPaginator.currentPageItems[index].administrator) != "undefined"){
					var administrator = $scope.searchPaginator.currentPageItems[index].administrator;
					$('#administrator').tagsinput('removeAll');
					if(administrator != null && administrator.length > 0){
						var list = administrator.split(",");
						for(var i = 0; i < list.length; ++i)
							$('#administrator').tagsinput('add', list[i]);
					}
				}else{
					$('#administrator').tagsinput('removeAll');
				}
				
				$scope.topicEntry.id = $scope.searchPaginator.currentPageItems[index].id;
				$scope.topicEntry.topic = $scope.searchPaginator.currentPageItems[index].topic;
				$scope.topicEntry.producerAlarm = $scope.searchPaginator.currentPageItems[index].producerAlarm;
				$scope.topicEntry.consumerAlarm = $scope.searchPaginator.currentPageItems[index].consumerAlarm;
				$scope.topicEntry.sendpeak = $scope.searchPaginator.currentPageItems[index].sendpeak;
				$scope.topicEntry.sendvalley = $scope.searchPaginator.currentPageItems[index].sendvalley;
				$scope.topicEntry.sendfluctuation = $scope.searchPaginator.currentPageItems[index].sendfluctuation;
				$scope.topicEntry.sendfluctuationBase = $scope.searchPaginator.currentPageItems[index].sendfluctuationBase;
				$scope.topicEntry.senddelay = $scope.searchPaginator.currentPageItems[index].senddelay;
				$scope.topicEntry.producerIpInfos = $scope.searchPaginator.currentPageItems[index].producerIpInfos;
			}
			
			$scope.refreshpage = function(myForm, index){
				if ($scope.topicEntry.sendpeak < $scope.topicEntry.sendvalley){
					alert("峰值不能小于谷值");
					return;
				}
				$scope.topicEntry.administrator = $("#administrator").val();
				
				if(typeof($scope.topicEntry.producerIpInfos) != "undefined"){
					var length = $scope.topicEntry.producerIpInfos.length;
					for(var i = 0; i < length; ++i){
						var id = "#ip" + "alarm" + i;
						var check = $(id).prop('checked');
						$scope.topicEntry.producerIpInfos[i].alarm = check;
						id = "#ip" + "active" + i;
						check = $(id).prop('checked');
						$scope.topicEntry.producerIpInfos[i].active = check;
					}
				}
				var id = "#myModal" + index;
				$(id).modal('hide');
				var param = JSON.stringify($scope.topicEntry);
				
				$http.post(window.contextPath + '/console/topic/update', $scope.topicEntry).success(function(response) {
					$scope.query.topic = $scope.topicEntry.topic;
					$scope.searchPaginator = Paginator(fetchFunction, $scope.topicnum, $scope.query);
		    	});
		    }
			
			$scope.setTopic = function(topic){
				localStorage.setItem("topic", topic);
			}

			$scope.setIP = function(ip){
				localStorage.setItem("ip", ip);
			}
			
			//发送默认请求
			$scope.query = new Object();
			var tmptopic = localStorage.getItem("topic");
			if(tmptopic != null){
				
				if(tmptopic.indexOf(',') == -1){
					$scope.topic = tmptopic;
					$scope.query.topic = tmptopic;
				}else{
					$scope.query.topic = tmptopic;
					$scope.topic = "";
				}
				localStorage.clear();
			}
			$scope.query.producerServer = $scope.searchip;
			$scope.query.inactive = true;
			$scope.searchPaginator = Paginator(fetchFunction, $scope.topicnum, $scope.query);
			
			//如果topic列表返回空，则不会执行initpage
			$scope.$on('ngRepeatFinished',  function (ngRepeatFinishedEvent) {
				$scope.initpage();
			});
			
			$scope.initpage = function(){

		          //下面是在table render完成后执行的js
				 $http({
						method : 'GET',
						url : window.contextPath + '/console/topic/namelist?time=' + new Date()
					}).success(function(data, status, headers, config) {
						var topicNameList = data.first;
						var producerip = data.second;
						$("#searchtopic").typeahead({
							items: 16, 
							source : topicNameList,
							updater : function(c) {
								$scope.topic = c;
								$scope.query.topic = $scope.topic;
								$scope.query.producerServer = $("#searchip").val();
								$scope.searchPaginator = Paginator(fetchFunction, $scope.topicnum, $scope.query);		
								return c;
							}
						})
						
						$("#searchip").typeahead({
							items: 16, 
							source : producerip,
							updater : function(c) {
								$scope.searchip = c;
								$scope.query.producerServer = $scope.searchip;
								$scope.query.topic = $("#searchtopic").val();
								$scope.searchPaginator = Paginator(fetchFunction, $scope.topicnum, $scope.query);		
								return c;
							}
						})
					}).error(function(data, status, headers, config) {
					});
				 
					$http({
						method : 'GET',
						url : window.contextPath + '/console/topic/administrator'
					}).success(function(data, status, headers, config) {
						//work
						$('#administrator').tagsinput({
							  typeahead: {
								  items: 16,
								  source: data,
								  displayText: function(item){ return item;}  //necessary
							  }
						});
		        		//$('#administrator').typeahead().data('typeahead').source = data;
					}).error(function(data, status, headers, config) {
					});
					
					 $http({
							method : 'GET',
							url : window.contextPath + '/console/ip/allip'
						}).success(function(data, status, headers, config) {
							$('#producerServer').tagsinput({
								  typeahead: {
									  items: 16,
									  source: data,
									  displayText: function(item){ return item;}  //necessary
								  }
							});
						}).error(function(data, status, headers, config) {
						});

					 $http({
						 method : 'GET',
						 url : window.contextPath + '/console/topic/alarm/ipinfo/count/inactive'
					 }).success(function(data, status, headers, config) {
						 $scope.countinactive = data;
					 }).error(function(data, status, headers, config) {
					 });
					
			}
			
			$scope.setInactive = function(){
				$scope.query.inactive = !$scope.query.inactive;
				$scope.searchPaginator = Paginator(fetchFunction, $scope.topicnum, $scope.query);
			}
			
			$scope.changeipinfo = function(topic, type, index, ip){
				var id = "#ip" + type + index;
				var check = $(id).prop('checked');
				if("alarm" == type){
					$http.get(window.contextPath + "/console/topic/alarm/ipinfo/alarm",
							{
								params : {
									topic : topic,
									ip : ip,
									alarm : check
								}
							})
					.success(function(data) {
							});
				}else{
					$http.get(window.contextPath + "/console/topic/alarm/ipinfo/active",
							{
								params : {
									topic : topic,
									ip : ip,
									active : check
								}
							})
					.success(function(data) {
							});
				}
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

