module
		.factory(
				'Paginator',
				function() {
					return function(fetchFunction, pageSize, entity) {
						var paginator = {
							hasNextVar : false,
							fetch : function(page) {
								this.currentOffset = (page - 1) * pageSize;
								this._load();
							},
							next : function() {
								if (this.hasNextVar) {
									this.currentOffset += pageSize;
									this._load();
								}
							},
							_load : function() {
								var self = this; // must use self
								self.currentPage = Math
										.floor(self.currentOffset / pageSize) + 1;
								entity.offset = this.currentOffset;
								entity.limit = pageSize + 1;
								fetchFunction(
										entity,
										function(data) {
											items = data.second;
											length = data.first;
											if (length == 0) {
												return;
											}
											self.totalPage = Math.ceil(length
													/ pageSize);
											self.endPage = self.totalPage;
											// 生成链接
											if (self.currentPage > 1
													&& self.currentPage < self.totalPage) {
												self.pages = [
														self.currentPage - 1,
														self.currentPage,
														self.currentPage + 1 ];
											} else if (self.currentPage == 1
													&& self.totalPage > 1) {
												self.pages = [
														self.currentPage,
														self.currentPage + 1 ];
											} else if (self.currentPage == self.totalPage
													&& self.totalPage > 1) {
												self.pages = [
														self.currentPage - 1,
														self.currentPage ];
											}
											self.currentPageItems = items
													.slice(0, pageSize);
											self.hasNextVar = items.length === pageSize + 1;
										});
							},
							hasNext : function() {
								return this.hasNextVar;
							},
							previous : function() {
								if (this.hasPrevious()) {
									this.currentOffset -= pageSize;
									this._load();
								}
							},
							hasPrevious : function() {
								return this.currentOffset !== 0;
							},
							totalPage : 1,
							pages : [],
							lastpage : 0,
							currentPage : 1,
							endPage : 1,

							currentPageItems : [],
							currentOffset : 0
						};

						// 加载第一页
						paginator._load();
						return paginator;
					};
				});

module
		.controller(
				'ConsumerIdSettingController',
				[
						'$rootScope',
						'$scope',
						'$http',
						'Paginator',
						'ngDialog',
						'$interval',
						function($rootScope, $scope, $http, Paginator,
								ngDialog, $interval) {
							var fetchFunction = function(entity, callback) {
								$http.post(window.contextPath + $scope.suburl,
										entity).success(callback);
							};

							$scope.suburl = "/console/consumerid/list";
							$scope.numrecord = 30;
							
							$scope.topic = "";
							$scope.consumerId = "";
							$scope.consumerIp = "";
							var tmpname = localStorage.getItem("topic");
							if(tmpname != null){
								$scope.topic = localStorage.getItem("topic");
								localStorage.clear();
							}

							$scope.query = new Object();
							$scope.query.topic = $scope.topic;
							$scope.query.consumerId = $scope.consumerId;
							$scope.query.consumerIp = $scope.consumerIp;
							
							$scope.consumeridEntry = {};
							$scope.consumeridEntry.consumerId;
							$scope.consumeridEntry.topic;
							$scope.consumeridEntry.alarm;
							$scope.consumeridEntry.consumerIp;
							$scope.consumeridEntry.senddelay;
							$scope.consumeridEntry.ackdelay;
							$scope.consumeridEntry.accumulation;
							$scope.consumeridEntry.sendpeak;
							$scope.consumeridEntry.sendvalley;
							$scope.consumeridEntry.sendfluctuation;
							$scope.consumeridEntry.sendfluctuationBase;
							$scope.consumeridEntry.ackpeak;
							$scope.consumeridEntry.ackvalley;
							$scope.consumeridEntry.ackfluctuation;
							$scope.consumeridEntry.ackfluctuationBase;

							$scope.refreshpage = function(myForm, num) {
								if ($scope.consumeridEntry.sendpeak < $scope.consumeridEntry.sendvalley
										|| $scope.consumeridEntry.ackpeak < $scope.consumeridEntry.ackvalley) {
									alert("谷值不能小于峰值");
									return;
								}
								$scope.consumeridEntry.consumerIp = $("#consumerIp").val();
								var id = "#myModal" + num;
								$(id).modal('hide');
								var param = JSON
										.stringify($scope.consumeridEntry);
								
								$http.post(window.contextPath + '/console/consumerid/update', $scope.consumeridEntry).success(function(response) {
									$scope.query.topic = $scope.consumeridEntry.topic;
									$scope.query.consumerId = $scope.consumeridEntry.consumerId;
									$scope.searchPaginator = Paginator(fetchFunction, $scope.numrecord, $scope.query);
						    	});
							}

							$scope.clearModal = function() {
								$scope.consumeridEntry.consumerId = "";
								$scope.consumeridEntry.topic = "";
								$scope.consumeridEntry.alarm = true;
								$scope.consumeridEntry.consumerIp = "";
								$scope.consumeridEntry.senddelay = "";
								$scope.consumeridEntry.ackdelay = "";
								$scope.consumeridEntry.accumulation = "";
								$scope.consumeridEntry.sendpeak = "";
								$scope.consumeridEntry.sendvalley = "";
								$scope.consumeridEntry.sendfluctuation = "";
								$scope.consumeridEntry.sendfluctuationBase = "";
								$scope.consumeridEntry.ackpeak = "";
								$scope.consumeridEntry.ackvalley = "";
								$scope.consumeridEntry.ackfluctuation = "";
								$scope.consumeridEntry.ackfluctuationBase = "";
							}

							$scope.setModalInput = function(index) {
								if(typeof($scope.searchPaginator.currentPageItems[index].consumerIp) != "undefined"){
									var consumerIp = $scope.searchPaginator.currentPageItems[index].consumerIp;
									$('#consumerIp').tagsinput('removeAll');
									if(consumerIp != null && consumerIp.length > 0){
										var list = consumerIp.split(",");
										for(var i = 0; i < list.length; ++i)
											$('#consumerIp').tagsinput('add', list[i]);
									}
								}else{
									$('#consumerIp').tagsinput('removeAll');
								}
								
								$scope.consumeridEntry.id = $scope.searchPaginator.currentPageItems[index].id;
								$scope.consumeridEntry.alarm = $scope.searchPaginator.currentPageItems[index].alarm;
								$scope.consumeridEntry.consumerId = $scope.searchPaginator.currentPageItems[index].consumerId;
								$scope.consumeridEntry.topic = $scope.searchPaginator.currentPageItems[index].topic;
								$scope.consumeridEntry.senddelay = $scope.searchPaginator.currentPageItems[index].senddelay;
								$scope.consumeridEntry.ackdelay = $scope.searchPaginator.currentPageItems[index].ackdelay;
								$scope.consumeridEntry.accumulation = $scope.searchPaginator.currentPageItems[index].accumulation;
								$scope.consumeridEntry.sendpeak = $scope.searchPaginator.currentPageItems[index].sendpeak;
								$scope.consumeridEntry.sendvalley = $scope.searchPaginator.currentPageItems[index].sendvalley;
								$scope.consumeridEntry.sendfluctuation = $scope.searchPaginator.currentPageItems[index].sendfluctuation;
								$scope.consumeridEntry.sendfluctuationBase = $scope.searchPaginator.currentPageItems[index].sendfluctuationBase;
								$scope.consumeridEntry.ackpeak = $scope.searchPaginator.currentPageItems[index].ackpeak;
								$scope.consumeridEntry.ackvalley = $scope.searchPaginator.currentPageItems[index].ackvalley;
								$scope.consumeridEntry.ackfluctuation = $scope.searchPaginator.currentPageItems[index].ackfluctuation;
								$scope.consumeridEntry.ackfluctuationBase = $scope.searchPaginator.currentPageItems[index].ackfluctuationBase;
							}
							
							//如果topic列表返回空，则不会执行initpage
//							$scope.$on('ngRepeatFinished',  function (ngRepeatFinishedEvent) {
//								$scope.initpage();
//							});
							
							$scope.initpage = function(){

						          //下面是在table render完成后执行的js
								 $http({
										method : 'GET',
										url : window.contextPath + '/console/topic/namelist'
									}).success(function(data, status, headers, config) {
										var topicNameList = data.first;
										$("#searchtopic").typeahead({
											items: 16, 
											source : topicNameList,
											updater : function(c) {
												$scope.topic = c;
												$scope.query.topic = $scope.topic;
												$scope.query.consumerId = $("#searchconsumerid").val();
												$scope.query.consumerIp = $("#searchconsumerip").val();
												$scope.searchPaginator = Paginator(fetchFunction, $scope.numrecord, $scope.query);		
												return c;
											}
										})
										
										if($scope.topic.length == 0){ //默认请求
											if(topicNameList != null && topicNameList.length > 0){
												var topics = topicNameList.join(",");
												if(topicNameList.length == 1){
													$scope.topic = topics;
												}else{
													$scope.topic = "";
												}
												$scope.query.topic = topics;
												$scope.searchPaginator = Paginator(fetchFunction, $scope.numrecord, $scope.query);
											}
										}else{//点击cid跳转过来
											$scope.searchPaginator = Paginator(fetchFunction, $scope.numrecord, $scope.query);
										}
									}).error(function(data, status, headers, config) {
								});
										
								 $http({
										method : 'GET',
										url : window.contextPath + '/console/consumerid/allconsumerid'
									}).success(function(data, status, headers, config) {
										$("#searchconsumerid").typeahead({
											items: 16, 
											source : data,
											updater : function(c) {
												$scope.consumerId = c;
												$scope.query.consumerId = $scope.consumerId;
												$scope.query.topic = $("#searchtopic").val();
												$scope.query.consumerIp = $("#searchconsumerip").val();
												$scope.searchPaginator = Paginator(fetchFunction, $scope.numrecord, $scope.query);		
												return c;
											}
										})
									}).error(function(data, status, headers, config) {
								});
								 
								 $http({
										method : 'GET',
										url : window.contextPath + '/console/consumerid/ips'
									}).success(function(data, status, headers, config) {
										
										$("#searchconsumerip").typeahead({
											items: 16, 
											source : data,
											updater : function(c) {
												$scope.consumerIp = c;
												$scope.query.consumerIp = $scope.consumerIp;
												$scope.query.topic = $("#searchtopic").val();
												$scope.query.consumerId = $("#searchconsumerid").val();
												$scope.searchPaginator = Paginator(fetchFunction, $scope.numrecord, $scope.query);		
												return c;
											}
										})
										
									}).error(function(data, status, headers, config) {
								});
								 
//								 $http({
//										method : 'GET',
//										url : window.contextPath + '/console/ip/allip'
//									}).success(function(data, status, headers, config) {
//										$("#consumerIp").tagsinput({
//											  typeahead: {
//												  items: 16,
//												  source: data,
//												  displayText: function(item){ return item;}  //necessary
//											  }
//										});
//										$('#consumerIp').typeahead().data('typeahead').source = data;
//									}).error(function(data, status, headers, config) {
//								});
							}
							
							$scope.initpage();
							
							$scope.setIp = function(topic, cid){
//								localStorage.setItem("ip", ip);
								$http.get(window.contextPath + '/console/consumerid/ipinfo/' + topic + "/" + "cid").success(function(response) {
					        	});
								
							}
							
							$scope.changealarm = function( consumerid, topic, index){
								var id = "#alarm" + index;
								var check = $(id).prop('checked');
								$http.get(window.contextPath + '/console/consumerid/alarm', {
									params : {
										topic : topic,
										consumerId: consumerid,
										alarm: check } }).success(function(response) {
					        	});
							}

							$rootScope.removerecord = function(cid, topic) {
								$http
										.get(
												window.contextPath
														+ "/console/consumerid/remove",
												{
													params : {
														consumerId : cid,
														topic : topic
													}
												})
										.success(
												function(data) {
													$scope.query.topic = "";
													$scope.query.consumerId = "";
													$scope.query.consumerIp = "";
													$scope.searchPaginator = Paginator(
															fetchFunction,
															$scope.numrecord, $scope.query);
												});
								return true;
							}
							
							$scope.isdefault = function(compare){
								return compare != "default"; 
							}

							$scope.dialog = function(cid, topic) {
								$rootScope.cid = cid;
								$rootScope.topic = topic;
								ngDialog
										.open({
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
								<button type="button" class="btn btn-primary" ng-click="removerecord(cid, topic)&&closeThisDialog()">确定</button>\
							</div>\
						</div>\
					</div>',
											plain : true,
											className : 'ngdialog-theme-default'
										});
							};

						} ]);


