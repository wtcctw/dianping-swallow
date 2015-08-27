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

module.controller('IpResourceController', ['$rootScope', '$scope', '$http', 'Paginator', 'ngDialog',
        function($rootScope, $scope, $http, Paginator, ngDialog){
				var fetchFunction = function(entity, callback){
				
				$http.post(window.contextPath + $scope.suburl, entity).success(callback);
		};
			$scope.suburl = "/console/ip/list";
			$scope.numrecord = 30;
			
			$scope.searchip = "";
			$scope.searchiptype = "";
			
			$scope.ipResourceEntry = {};
			$scope.ipResourceEntry.ip;
			$scope.ipResourceEntry.ipType;
			$scope.ipResourceEntry.alarm;
			$scope.ipResourceEntry.application;
			$scope.ipResourceEntry.email;
			$scope.ipResourceEntry.opManager;
			$scope.ipResourceEntry.opMobile;
			$scope.ipResourceEntry.opEmail;
			$scope.ipResourceEntry.dpManager;
			$scope.ipResourceEntry.dpMobile;
			
			$scope.setModalInput = function(index){
				
				$scope.ipResourceEntry.id = $scope.searchPaginator.currentPageItems[index].id;
				$scope.ipResourceEntry.ip = $scope.searchPaginator.currentPageItems[index].ip;
				$scope.ipResourceEntry.ipType = $scope.searchPaginator.currentPageItems[index].ipType;
				$scope.ipResourceEntry.alarm = $scope.searchPaginator.currentPageItems[index].alarm;
				$scope.ipResourceEntry.application = $scope.searchPaginator.currentPageItems[index].application;
				$scope.ipResourceEntry.email = $scope.searchPaginator.currentPageItems[index].email;
				$scope.ipResourceEntry.opManager = $scope.searchPaginator.currentPageItems[index].opManager;
				$scope.ipResourceEntry.opMobile = $scope.searchPaginator.currentPageItems[index].opMobile;
				$scope.ipResourceEntry.opEmail = $scope.searchPaginator.currentPageItems[index].opEmail;
				$scope.ipResourceEntry.dpManager = $scope.searchPaginator.currentPageItems[index].dpManager;
				$scope.ipResourceEntry.dpMobile = $scope.searchPaginator.currentPageItems[index].dpMobile;
			}
			
			$scope.refreshpage = function(myForm){
				if ($scope.ipResourceEntry.sendpeak < $scope.ipResourceEntry.sendvalley){
					alert("峰值不能小于谷值");
					return;
				}
				$scope.ipResourceEntry.consumerIdWhiteList = $("#whitelist").val();
				$scope.ipResourceEntry.prop = $("#prop").val();
				$scope.ipResourceEntry.producerServer = $("#producerServer").val();
				$('#myModal').modal('hide');
				var param = JSON.stringify($scope.ipResourceEntry);
				
				$http.post(window.contextPath + '/console/ip/update', $scope.ipResourceEntry).success(function(response) {
					$scope.query.ip = $scope.ipResourceEntry.ip;
					$scope.query.ipType = $scope.ipResourceEntry.ipType;
					$scope.searchPaginator = Paginator(fetchFunction, $scope.numrecord, $scope.query);
		    	});
		    	
		    }
						
			$scope.setTopicName = function(topic){
				localStorage.setItem("topic", topic);
			}
			var tmpip = localStorage.getItem("ip");
			if(tmpip != null){
				$scope.searchip = tmpip;
				$scope.searchiptype = localStorage.getItem("ipType");
				localStorage.clear();
			}
			//发送默认请求
			$scope.query = new Object();
			$scope.query.ip = $scope.searchip;
			$scope.query.ipType = $scope.searchiptype;
			$scope.searchPaginator = Paginator(fetchFunction, $scope.numrecord, $scope.query);
			
			//如果topic列表返回空，则不会执行initpage
			$scope.$on('ngRepeatFinished',  function (ngRepeatFinishedEvent) {
				$scope.initpage();
			});
			
			$scope.initpage = function(){

		          //下面是在table render完成后执行的js
				 $http({
						method : 'GET',
						url : window.contextPath + '/console/ip/iplist'
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

