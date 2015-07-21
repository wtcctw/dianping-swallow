module
		.factory(
				'Paginator',
				function() {
					return function(fetchFunction, pageSize, receiver,
							startTime, endTime) {
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

module.controller('AlarmController', [
		'$scope',
		'$http',
		'Paginator',
		function($scope, $http, Paginator) {
			var fetchFunction = function(offset, limit, receiver, startTime,
					endTime, callback) {

				$http.get(window.contextPath + $scope.suburl, {
					params : {
						offset : offset,
						limit : limit,
						receiver : receiver,
						startTime : startTime,
						endTime : endTime
					}
				}).success(callback);
			};

			$scope.receiver = "";
			$scope.startTime = "";
			$scope.endTime = "";
			$scope.suburl = "/console/alarm/search";
			$scope.pageSize = 30;

			$scope.query = function() {
				$scope.startTime = $("#starttime").val();
				$scope.endTime = $("#stoptime").val();
				$scope.searchPaginator = Paginator(fetchFunction,
						$scope.pageSize, $scope.receiver, $scope.startTime,
						$scope.endTime);
			}

		} ]);