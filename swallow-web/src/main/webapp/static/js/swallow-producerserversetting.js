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

module.controller('ProducerServerSettingController', ['$rootScope', '$scope', '$http', 'Paginator', 'ngDialog', '$interval',
    function ($rootScope, $scope, $http, Paginator, ngDialog, $interval) {
        var fetchFunction = function (entity, callback) {
            $http.post(window.contextPath + $scope.suburl, entity).success(callback);
        };

        $scope.suburl = "/console/server/producer/list";
        $scope.numrecord = 30;

        $scope.entity = new Object();
        $scope.searchPaginator = Paginator(fetchFunction, $scope.numrecord, $scope.entity);

        //for whitelist
        $http({
            method: 'GET',
            url: window.contextPath + '/console/topic/namelist'
        }).success(function (data, status, headers, config) {
            $('#whitelist').tagsinput({
                typeahead: {
                    items: 16,
                    source: data,
                    displayText: function (item) {
                        return item;
                    }  //necessary
                }
            });
        }).error(function (data, status, headers, config) {
        });

        $scope.producerserverEntry = {};
        $scope.producerserverEntry.saveAlarmSetting = {};
        $scope.producerserverEntry.ip;
        $scope.producerserverEntry.hostname;
        $scope.producerserverEntry.alarm;
        $scope.producerserverEntry.active;
        $scope.producerserverEntry.groupName;
        $scope.producerserverEntry.saveAlarmSetting.peak;
        $scope.producerserverEntry.saveAlarmSetting.valley;
        $scope.producerserverEntry.saveAlarmSetting.fluctuation;
        $scope.producerserverEntry.saveAlarmSetting.fluctuationBase;
        $scope.producerserverEntry.saveAlarmSetting.isQpsAlarm;
        $scope.refreshpage = function (myForm) {
            if ($scope.producerserverEntry.saveAlarmSetting.peak < $scope.producerserverEntry.saveAlarmSetting.valley) {
                alert("谷值不能小于峰值");
                return;
            }
            $('#myModal').modal('hide');
            var param = JSON.stringify($scope.producerserverEntry);

            $http.post(window.contextPath + '/console/server/producer/create', $scope.producerserverEntry).success(function (response) {
                $scope.searchPaginator = Paginator(fetchFunction, $scope.numrecord, $scope.entity);
            });

        }

        $scope.isReadOnly = false;
        $scope.clearModal = function () {
            $scope.isReadOnly = false;
            $scope.producerserverEntry.id = null;
            $scope.producerserverEntry.ip = "";
            $scope.producerserverEntry.hostname = "";
            $scope.producerserverEntry.alarm = true;
            $scope.producerserverEntry.active = true;
            $scope.producerserverEntry.groupName = "";
            $scope.producerserverEntry.saveAlarmSetting.peak = "";
            $scope.producerserverEntry.saveAlarmSetting.valley = "";
            $scope.producerserverEntry.saveAlarmSetting.fluctuation = "";
            $scope.producerserverEntry.saveAlarmSetting.fluctuationBase = "";
            $scope.producerserverEntry.saveAlarmSetting.isQpsAlarm = true;

            $http.get(window.contextPath + "/console/server/defaultpresource").success(function (data) {
                $scope.producerserverEntry.saveAlarmSetting.peak = data.saveAlarmSetting.peak;
                $scope.producerserverEntry.saveAlarmSetting.valley = data.saveAlarmSetting.valley;
                $scope.producerserverEntry.saveAlarmSetting.fluctuation = data.saveAlarmSetting.fluctuation;
                $scope.producerserverEntry.saveAlarmSetting.fluctuationBase = data.saveAlarmSetting.fluctuationBase;
                $scope.producerserverEntry.saveAlarmSetting.isQpsAlarm = data.saveAlarmSetting.isQpsAlarm;
            });
        }

        $scope.setModalInput = function (index) {
            $('#groupName').val($scope.searchPaginator.currentPageItems[index].groupName);
            $scope.isReadOnly = true;
            $scope.producerserverEntry.id = $scope.searchPaginator.currentPageItems[index].id;
            $scope.producerserverEntry.ip = $scope.searchPaginator.currentPageItems[index].ip;
            $scope.producerserverEntry.alarm = $scope.searchPaginator.currentPageItems[index].alarm;
            $scope.producerserverEntry.active = $scope.searchPaginator.currentPageItems[index].active;
            $scope.producerserverEntry.hostname = $scope.searchPaginator.currentPageItems[index].hostname;
            $scope.producerserverEntry.groupName = $scope.searchPaginator.currentPageItems[index].groupName;
            $scope.producerserverEntry.saveAlarmSetting.peak = $scope.searchPaginator.currentPageItems[index].saveAlarmSetting.peak;
            $scope.producerserverEntry.saveAlarmSetting.valley = $scope.searchPaginator.currentPageItems[index].saveAlarmSetting.valley;
            $scope.producerserverEntry.saveAlarmSetting.fluctuation = $scope.searchPaginator.currentPageItems[index].saveAlarmSetting.fluctuation;
            $scope.producerserverEntry.saveAlarmSetting.fluctuationBase = $scope.searchPaginator.currentPageItems[index].saveAlarmSetting.fluctuationBase;
            $scope.producerserverEntry.saveAlarmSetting.isQpsAlarm = $scope.searchPaginator.currentPageItems[index].saveAlarmSetting.isQpsAlarm;
        }

        $rootScope.removerecord = function (sid) {
            $http.get(window.contextPath + "/console/server/producer/remove", {
                params: {
                    serverId: sid
                }
            }).success(function (data) {
                $scope.searchPaginator = Paginator(fetchFunction, $scope.numrecord, $scope.entity);
            });
            return true;
        }

        $http({
            method: 'GET',
            url: window.contextPath + '/console/server/producerserverinfo'
        }).success(function (data, status, headers, config) {
            var ips = data.second;
            var hosts = data.first;
            $("#serverId").typeahead({
                items: 16,
                source: ips,
                updater: function (c) {
                    $scope.producerserverEntry.ip = c;
                    return c;
                }
            })

            $("#hostname").typeahead({
                items: 16,
                source: hosts,
                updater: function (c) {
                    $scope.producerserverEntry.hostname = c;
                    return c;
                }
            })
        }).error(function (data, status, headers, config) {
        });

        $http({
            method: 'GET',
            url: window.contextPath + '/console/server/grouptype'
        }).success(function (data) {
            $scope.types = data;
        }).error(function (data, status, headers, config) {
        });

        $scope.loadtopics = function (serverid) {
            $http({
                method: 'GET',
                params: {serverId: serverid},
                url: window.contextPath
                + '/console/setting/producerserver/topics'
            }).success(
                function (data, status, headers, config) {
                    $('#whitelist').tagsinput({
                        typeahead: {
                            items: 16,
                            source: data,
                            displayText: function (item) {
                                return item;
                            } // necessary
                        }
                    });
                }).error(
                function (data, status, headers, config) {
                });

        }

        $scope.changeproduceralarm = function (ip, index) {
            var id = "#palarm" + index;
            var check = $(id).prop('checked');

            $http.get(window.contextPath + '/console/server/producer/alarm', {
                params: {
                    ip: ip,
                    alarm: check
                }
            }).success(function (response) {
            });
        }

        $scope.changeproduceractive = function (ip, index) {
            var id = "#pactive" + index;
            var check = $(id).prop('checked');

            $http.get(window.contextPath + '/console/server/producer/active', {
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

        $scope.isdefault = function (compare) {
            return compare != "default";
        }
    }]);

