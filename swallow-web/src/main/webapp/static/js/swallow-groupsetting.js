/**
 * Created by mingdongli on 15/12/22.
 */

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

module.controller('GroupSettingController', ['$rootScope', '$scope', '$http', 'Paginator', 'ngDialog','$interval',
    function($rootScope, $scope, $http, Paginator, ngDialog,$interval){
        var fetchFunction = function(entity, callback){
            $http.post(window.contextPath + $scope.suburl, entity).success(callback);
        };

        $scope.suburl = "/console/server/group/list";
        $scope.numrecord = 30;

        $scope.entity = new Object();
        $scope.searchPaginator = Paginator(fetchFunction, $scope.numrecord, $scope.entity );

        $scope.groupEntry = {};
        $scope.groupEntry.groupName;

        $scope.refreshpage = function(myForm){
            $scope.groupEntry.groupName = $('#groupName').val();
            $('#myModal').modal('hide');
            var param = JSON.stringify($scope.groupEntry);

            $http.post(window.contextPath + '/console/server/group/create', $scope.groupEntry).success(function(response) {
                $scope.searchPaginator = Paginator(fetchFunction, $scope.numrecord, $scope.entity);
            });

        }

        $scope.clearModal = function(){
            $scope.groupEntry.groupName = "";
        }

        $scope.setModalInput = function(index){
            $scope.groupEntry.groupName = $scope.searchPaginator.currentPageItems[index].groupName;
        }

        $rootScope.removerecord = function(groupName){
            $http.get(window.contextPath + "/console/server/group/remove", {
                params : {
                    groupName : groupName
                }
            }).success(function(data){
                $scope.searchPaginator = Paginator(fetchFunction, $scope.numrecord, $scope.entity);
            });
            return true;
        }


        $scope.dialog = function(groupName) {
            $rootScope.groupName = groupName;
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
								<button type="button" class="btn btn-primary" ng-click="removerecord(groupName)&&closeThisDialog()">确定</button>\
							</div>\
						</div>\
					</div>',
                plain : true,
                className : 'ngdialog-theme-default'
            });
        };

        $scope.isdefault = function(compare){
            return compare != "Default";
        }
    }]);


