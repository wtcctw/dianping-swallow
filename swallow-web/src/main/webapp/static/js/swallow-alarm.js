module
		.factory(
				'Paginator',
				function() {
					return function(fetchFunction, pageSize, receiver,
							startTime, endTime, relatedType, relatedInfo) {
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
								fetchFunction(
										this.currentOffset,
										pageSize + 1,
										receiver,
										startTime,
										endTime,
										relatedType,
										relatedInfo,
										function(data) {
											items = data.entitys;
											length = data.size;
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
				'AlarmController',
				[
						'$scope',
						'$http',
						'Paginator',
						function($scope, $http, Paginator) {
							var fetchFunction = function(offset, limit,
									receiver, startTime, endTime, relatedType,
									relatedInfo, callback) {
								$scope.searchEntity = {
									offset : offset,
									limit : limit,
									receiver : receiver,
									relatedType : relatedType,
									relatedInfo : relatedInfo,
									startTime : startTime,
									endTime : endTime
								};
								console.log($scope.searchEntity);
								$http.post(window.contextPath + $scope.suburl,
										$scope.searchEntity).success(callback);
							};

							$scope.receiver = "";
							$scope.relatedInfo = "";
							$scope.relatedType = "";
							$scope.startTime = "";
							$scope.endTime = "";
							$scope.suburl = "/console/alarm/search";
							$scope.pageSize = 30;
							$scope.queryCount = 0;
							console.log(window.contextPath);
							$http(
									{
										method : 'GET',
										url : window.contextPath
												+ '/console/admin/queryvisits'
									})
									.success(
											function(topicList, status,
													headers, config) {
												$("#receiver")
														.typeahead(
																{
																	source : topicList,
																	updater : function(
																			c) {
																		return c
																				+ "@dianping.com";
																	}
																})
											}).error(
											function(data, status, headers,
													config) {
												app.appError("响应错误", data);
											});

							$http(
									{
										method : 'GET',
										url : window.contextPath
												+ '/console/alarm/query/ip'
									}).success(
									function(datas, status, headers, config) {
										$scope.ips = datas;
									}).error(
									function(datas, status, headers, config) {
										console.log("ips读取错误");
									});
							$http(
									{
										method : 'GET',
										url : window.contextPath
												+ '/console/topic/namelist'
									}).success(
									function(datas, status, headers, config) {
										$scope.topicnames = datas;
									}).error(
									function(datas, status, headers, config) {
										console.log("topicname读取错误");
									});
							$http(
									{
										method : 'GET',
										url : window.contextPath
												+ '/console/alarm/query/consumerid'
									}).success(
									function(datas, status, headers, config) {
										$scope.consumerids = datas;
									}).error(
									function(datas, status, headers, config) {
										console.log("consumerid读取错误");
									});
							$("#relatedInfo").typeahead({
								updater : function(c) {
									return c;
								}
							});
							$("#relatedType")
									.change(
											function() {
												var selectValue = $(
														"#relatedType").val();
												if (selectValue == "IP") {
													$scope.relatedDatas = $scope.ips;
												} else if (selectValue == "TOPIC") {
													$scope.relatedDatas = $scope.topicnames;
												} else if (selectValue == "CONSUMERID") {
													$scope.relatedDatas = $scope.consumerids;
												} else {
													$scope.relatedDatas = [];
												}
												$("#relatedInfo").data(
														'typeahead').source = $scope.relatedDatas;
											});
							$scope.query = function() {
								if ($scope.queryCount != 0) {
									$scope.startTime = $("#starttime").val();
									$scope.endTime = $("#stoptime").val();
								}
								$scope.queryCount = $scope.queryCount + 1;
								$scope.receiver = $("#receiver").val();
								$scope.relatedType = $("#relatedType").val();
								$scope.relatedInfo = $("#relatedInfo").val();
								if ($scope.startTime != null
										&& $scope.endTime != null) {
									startDate = new Date($scope.startTime);
									endDate = new Date($scope.endTime);
									if (endDate <= startDate) {
										alert("结束时间不能小于开始时间");
										return;
									}
								}

								$scope.getRelatedUrl = function(relatedUrl) {
									if(relatedUrl=="#"){
										return "#";
									}else{
										return window.contextPath + relatedUrl;
									}
									
								}

								$scope.searchPaginator = Paginator(
										fetchFunction, $scope.pageSize,
										$scope.receiver, $scope.startTime,
										$scope.endTime, $scope.relatedType,
										$scope.relatedInfo);
							}

							$scope.query();
						} ]);