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
						if(this.currentPageItems[i].finished){
							this.currentPageItems[i].finished = "已导出";
						}else{
							this.currentPageItems[i].finished = "导出中";
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
					fetchFunction(entity, function(data){
						items = data.second;
						length = data.first;
						if(length == 0){
							return;
						}
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

module.controller('DownloadController', ['$rootScope', '$scope', '$http', 'Paginator', 'ngDialog','$interval',
                function($rootScope, $scope, $http, Paginator, ngDialog,$interval){
	var fetchFunction = function(entity, callback){
		$http.post(window.contextPath + $scope.suburl, entity).success(callback);
	};
	
	$scope.topic = "";
	
	$scope.suburl = "/console/download/filename";
	$scope.topicnum = 30;
	$scope.entity = new Object();
	$scope.entity.topic = "";
	
	$http({
		method : 'GET',
		url : window.contextPath + '/console/topic/namelist'
	}).success(function(data, status, headers, config) {
		var topicNameList = data.first;
		$("#searchname").typeahead({
			items: 16, 
			source : topicNameList,
			updater : function(c) {
				$scope.topic = c;
				$scope.entity.topic = $scope.topic;
				$scope.searchPaginator = Paginator(fetchFunction, $scope.topicnum, $scope.entity);
				return c;
			}
		})
	}).error(function(data, status, headers, config) {
	});
	
	$scope.updatefilestatus = function() {
		
		$http.post(window.contextPath + $scope.suburl, $scope.entity).success(function(data){
			var message = data.second;
			if(typeof(message) == "undefined"){
				$interval.cancel($scope.Timer);
				return;
			}
			for(var i = 0; i < message.length; ++i){
				if(message[i].finished){
					message[i].finished = "已导出";
				}else{
					message[i].finished = "导出中";
				}
			}
			$scope.searchPaginator.currentPageItems = message;
			var concel = true;
			for(var i=0;i<message.length;i++){
				if(message[i].finished == "导出中"){
					concel = false;
					break;
				}
			}
			if(concel){
	            if (angular.isDefined($scope.Timer)) {
	                $interval.cancel($scope.Timer);
	            }
			}
		});
    }
	
	$scope.dialog = function(name) {
		$rootScope.removeName = name;
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
								<button type="button" class="btn btn-primary" ng-click="removefile(removeName)&&closeThisDialog()">确定</button>\
							</div>\
						</div>\
					</div>',
					plain : true,
					className : 'ngdialog-theme-default'
			});
	};
	
	$rootScope.removefile = function(filename){
		$http.get(window.contextPath + "/console/download/auth/removefile", {
			params : {
				topic : $scope.topic,
				filename: filename
			}
		}).success(function(data){
			$scope.entity.topic = $scope.topic;
			$scope.searchPaginator = Paginator(fetchFunction, $scope.topicnum, $scope.entity);
		});
		return true;
	}
	
	var tmptopic = localStorage.getItem("topic");
	if(tmptopic != undefined){
		$scope.topic = tmptopic;
		localStorage.clear("topic");
		$scope.entity.topic = $scope.topic;
		$scope.searchPaginator = Paginator(fetchFunction, $scope.topicnum, $scope.entity);
		$scope.Timer = $interval( function(){ $scope.updatefilestatus(); }, 3000);
	}else{
		$scope.entity.topic = $scope.topic;
		$scope.searchPaginator = Paginator(fetchFunction, $scope.topicnum, $scope.entity);
		$scope.Timer = $interval( function(){ $scope.updatefilestatus(); }, 3000);
	}

			
}]);

