/**
 * Created by mingdongli on 16/2/18.
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

module.controller('JmxSettingController', ['$rootScope', '$scope', '$http', 'Paginator', 'ngDialog','$interval',
    function($rootScope, $scope, $http, Paginator, ngDialog,$interval){
        var fetchFunction = function(entity, callback){
            $http.post(window.contextPath + $scope.suburl, entity).success(callback);
        };

        $scope.suburl = "/console/jmx/list";
        $scope.numrecord = 30;

        $scope.entity = new Object();
        $scope.searchPaginator = Paginator(fetchFunction, $scope.numrecord, $scope.entity );

        $scope.jmxEntry = {};
        $scope.jmxEntry.group;
        $scope.jmxEntry.name;
        $scope.jmxEntry.type;
        $scope.jmxEntry.tag;
        $scope.jmxEntry.clazz;

        $scope.refreshpage = function(myForm){
            $('#myModal').modal('hide');
            $http.post(window.contextPath + '/console/jmx/create', $scope.jmxEntry).success(function(response) {
                $scope.searchPaginator = Paginator(fetchFunction, $scope.numrecord, $scope.entity);
            });

        }

        $scope.clearModal = function(){
            $scope.jmxEntry.group = "";
            $scope.jmxEntry.name = "";
            $scope.jmxEntry.type = "";
            $scope.jmxEntry.tag = "";
            $scope.jmxEntry.clazz = "";
        }

        $scope.setModalInput = function(index){
            $scope.jmxEntry.group = $scope.searchPaginator.currentPageItems[index].group;
            $scope.jmxEntry.name = $scope.searchPaginator.currentPageItems[index].name;
            $scope.jmxEntry.type = $scope.searchPaginator.currentPageItems[index].type;
            $scope.jmxEntry.tag = $scope.searchPaginator.currentPageItems[index].tag;
            $scope.jmxEntry.clazz = $scope.searchPaginator.currentPageItems[index].clazz;
            $('#clazz').val($scope.searchPaginator.currentPageItems[index].clazz);
        }

        $rootScope.removerecord = function(index){
            var entity = new Object();
            entity.group = $scope.searchPaginator.currentPageItems[index].group;
            entity.name = $scope.searchPaginator.currentPageItems[index].name;
            entity.type = $scope.searchPaginator.currentPageItems[index].type;
            entity.tag = $scope.searchPaginator.currentPageItems[index].tag;
            entity.clazz = $scope.searchPaginator.currentPageItems[index].clazz;
            $http.post(window.contextPath + '/console/jmx/remove', entity).success(function(response) {
                $scope.searchPaginator = Paginator(fetchFunction, $scope.numrecord, $scope.entity);
            });
            return true;
        }


        $scope.dialog = function(index) {
            $rootScope.jmxindex = index;
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
								<button type="button" class="btn btn-primary" ng-click="removerecord(jmxindex)&&closeThisDialog()">确定</button>\
							</div>\
						</div>\
					</div>',
                plain : true,
                className : 'ngdialog-theme-default'
            });
        };

        $scope.types = ["Gauge", "Meter", "Counter"];

    }]);