/**
 * Created by mingdongli on 16/1/28.
 */

module.factory('Paginator', function () {
    return function (fetchFunction, pageSize, entity) {
        var paginator = {
            hasNextVar: false,
            fetch: function (page) {
                this.currentOffset = (page - 1) * pageSize;
                this._load();
            },
            next: function () {
                if (this.hasNextVar) {
                    this.currentOffset += pageSize;
                    this._load();
                }
            },
            _load: function () {
                var self = this;  //must use  self
                self.currentPage = Math.floor(self.currentOffset / pageSize) + 1;
                entity.offset = this.currentOffset;
                entity.limit = pageSize + 1;
                fetchFunction(entity, function (data) {
                    items = data.second;
                    length = data.first;
                    if (length == 0) {
                        return;
                    }
                    self.totalPage = Math.ceil(length / pageSize);
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
            hasNext: function () {
                return this.hasNextVar;
            },
            previous: function () {
                if (this.hasPrevious()) {
                    this.currentOffset -= pageSize;
                    this._load();
                }
            },
            hasPrevious: function () {
                return this.currentOffset !== 0;
            },
            totalPage: 1,
            pages: [],
            lastpage: 0,
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

module.controller('KafkaServerSettingController', ['$rootScope', '$scope', '$http', 'Paginator', 'ngDialog', '$interval',
    function ($rootScope, $scope, $http, Paginator, ngDialog, $interval) {
        var fetchFunction = function (entity, callback) {
            $http.post(window.contextPath + $scope.suburl, entity).success(callback);
        };

        $scope.suburl = "/console/server/kafka/list";
        $scope.numrecord = 30;

        $scope.entity = new Object();
        $scope.searchPaginator = Paginator(fetchFunction, $scope.numrecord, $scope.entity);

        $scope.kafkaserverEntry = {};
        $scope.kafkaserverEntry.id = null;
        $scope.kafkaserverEntry.ip = "";
        $scope.kafkaserverEntry.hostname = "";
        $scope.kafkaserverEntry.zkServers = "";
        $scope.kafkaserverEntry.alarm = false;
        $scope.kafkaserverEntry.active = true;
        $scope.kafkaserverEntry.port = "";
        $scope.kafkaserverEntry.groupName = "";
        $scope.kafkaserverEntry.groupId = "";

        $scope.refreshpage = function (myForm, index) {
            var id = "#myModal" + index;
            $(id).modal('hide');
            var param = JSON.stringify($scope.kafkaserverEntry);

            $http.post(window.contextPath + '/console/server/kafka/create', $scope.kafkaserverEntry).success(function (response) {
                $scope.searchPaginator = Paginator(fetchFunction, $scope.numrecord, $scope.entity);
            });
        }

        $http({
            method: 'GET',
            url: window.contextPath + '/console/server/kafkaserverinfo'
        }).success(function (data, status, headers, config) {
            var ips = data.second;
            var hosts = data.first;
            $("#serverId").typeahead({
                items: 16,
                source: ips,
                updater: function (c) {
                    $scope.kafkaserverEntry.ip = c;
                    return c;
                }
            })

            $("#hostname").typeahead({
                items: 16,
                source: hosts,
                updater: function (c) {
                    $scope.kafkaserverEntry.hostname = c;
                    return c;
                }
            })
        }).error(function (data, status, headers, config) {
        });


        $scope.isReadOnly = false;
        $scope.clearModal = function () {
            $scope.isReadOnly = false;
            $scope.kafkaserverEntry.id = null;
            $scope.kafkaserverEntry.ip = "";
            $scope.kafkaserverEntry.hostname = "";
            $scope.kafkaserverEntry.zkServers = "";
            $scope.kafkaserverEntry.alarm = false;
            $scope.kafkaserverEntry.active = true;
            $scope.kafkaserverEntry.groupName ="";
            $scope.kafkaserverEntry.port = "";
            $scope.kafkaserverEntry.groupId = "";
            $scope.kafkaserverEntry.brokerId = "";
        }

        $scope.setModalInput = function (index) {
            $scope.isReadOnly = true;
            $scope.kafkaserverEntry.id = $scope.searchPaginator.currentPageItems[index].id;
            $scope.kafkaserverEntry.ip = $scope.searchPaginator.currentPageItems[index].ip;
            $scope.kafkaserverEntry.hostname = $scope.searchPaginator.currentPageItems[index].hostname;
            $scope.kafkaserverEntry.groupName = $scope.searchPaginator.currentPageItems[index].groupName;
            $scope.kafkaserverEntry.zkServers = $scope.searchPaginator.currentPageItems[index].zkServers;
            $scope.kafkaserverEntry.port = $scope.searchPaginator.currentPageItems[index].port;
            $scope.kafkaserverEntry.groupId = $scope.searchPaginator.currentPageItems[index].groupId;
            $scope.kafkaserverEntry.brokerId = $scope.searchPaginator.currentPageItems[index].brokerId;
            $scope.kafkaserverEntry.alarm = $scope.searchPaginator.currentPageItems[index].alarm;
            $scope.kafkaserverEntry.active = $scope.searchPaginator.currentPageItems[index].active;

        }

        $rootScope.removerecord = function (sid) {
            $http.get(window.contextPath + "/console/server/kafka/remove", {
                params: {
                    serverId: sid
                }
            }).success(function (data) {
                $scope.searchPaginator = Paginator(fetchFunction, $scope.numrecord, $scope.entity);
            });
            return true;
        }

        $scope.isdefault = function (compare) {
            return compare != "default";
        }

        $http({
            method: 'GET',
            url: window.contextPath + '/console/server/grouptype'
        }).success(function (data) {
            $scope.groups = data;
        }).error(function (data, status, headers, config) {
        });

        $scope.changekafkaalarm = function (ip, index) {
            var id = "#calarm" + index;
            var check = $(id).prop('checked');
            $http.get(window.contextPath + '/console/server/kafka/alarm', {
                params: {
                    ip: ip,
                    alarm: check
                }
            }).success(function (response) {
            });
        }

        $scope.changekafkaactive = function (ip, index) {
            var id = "#cactive" + index;
            var check = $(id).prop('checked');
            $http.get(window.contextPath + '/console/server/kafka/active', {
                params: {
                    ip: ip,
                    active: check
                }
            }).success(function (response) {
            });
        }

        $scope.dialog = function (cid) {
            $rootScope.cid = cid;
            ngDialog.open({
                template: '\
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
								<button type="button" class="btn btn-primary" ng-click="removerecord(cid)&&closeThisDialog()">确定</button>\
							</div>\
						</div>\
					</div>',
                plain: true,
                className: 'ngdialog-theme-default'
            });
        };

    }]);


