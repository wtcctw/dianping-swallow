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

module.controller('ConsumerServerSettingController', ['$rootScope', '$scope', '$http', 'Paginator', 'ngDialog', '$interval',
    function ($rootScope, $scope, $http, Paginator, ngDialog, $interval) {
        var fetchFunction = function (entity, callback) {
            $http.post(window.contextPath + $scope.suburl, entity).success(callback);
        };

        $scope.suburl = "/console/server/consumer/list";
        $scope.numrecord = 30;

        $scope.entity = new Object();
        $scope.searchPaginator = Paginator(fetchFunction, $scope.numrecord, $scope.entity);

        $scope.consumerserverEntry = {};
        $scope.consumerserverEntry.sendAlarmSetting = {};
        $scope.consumerserverEntry.ackAlarmSetting = {};
        $scope.consumerserverEntry.id = null;
        $scope.consumerserverEntry.ip = "";
        $scope.consumerserverEntry.hostname = "";
        $scope.consumerserverEntry.alarm = false;
        $scope.consumerserverEntry.active = true;
        $scope.consumerserverEntry.sendAlarmSetting.peak = "";
        $scope.consumerserverEntry.sendAlarmSetting.valley = "";
        $scope.consumerserverEntry.sendAlarmSetting.fluctuation = "";
        $scope.consumerserverEntry.sendAlarmSetting.fluctuationBase = "";
        $scope.consumerserverEntry.ackAlarmSetting.peak = "";
        $scope.consumerserverEntry.ackAlarmSetting.valley = "";
        $scope.consumerserverEntry.ackAlarmSetting.fluctuation = "";
        $scope.consumerserverEntry.ackAlarmSetting.fluctuationBase = "";
        $scope.consumerserverEntry.port = "";
        $scope.consumerserverEntry.type = "";
        $scope.consumerserverEntry.groupId = "";

        $scope.refreshpage = function (myForm, index) {
            if ($scope.consumerserverEntry.sendAlarmSetting.peak < $scope.consumerserverEntry.sendAlarmSetting.valley
                || $scope.consumerserverEntry.ackAlarmSetting.peak < $scope.consumerserverEntry.ackAlarmSetting.valley) {
                alert("谷值不能小于峰值");
                return;
            }
            var id = "#myModal" + index;
            $(id).modal('hide');
            var param = JSON.stringify($scope.consumerserverEntry);

            $http.post(window.contextPath + '/console/server/consumer/create', $scope.consumerserverEntry).success(function (response) {
                $scope.searchPaginator = Paginator(fetchFunction, $scope.numrecord, $scope.entity);
            });
        }

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

        $http({
            method: 'GET',
            url: window.contextPath + '/console/server/consumerserverinfo'
        }).success(function (data, status, headers, config) {
            var ips = data.second;
            var hosts = data.first;
            $("#serverId").typeahead({
                items: 16,
                source: ips,
                updater: function (c) {
                    $scope.consumerserverEntry.ip = c;
                    return c;
                }
            })

            $("#hostname").typeahead({
                items: 16,
                source: hosts,
                updater: function (c) {
                    $scope.consumerserverEntry.hostname = c;
                    return c;
                }
            })
        }).error(function (data, status, headers, config) {
        });


        $scope.isReadOnly = false;
        $scope.clearModal = function () {
            $scope.isReadOnly = false;
            $scope.consumerserverEntry.id = null;
            $scope.consumerserverEntry.ip = "";
            $scope.consumerserverEntry.hostname = "";
            $scope.consumerserverEntry.alarm = false;
            $scope.consumerserverEntry.active = true;
            $scope.consumerserverEntry.groupName ="";
            $scope.consumerserverEntry.sendAlarmSetting.peak = "";
            $scope.consumerserverEntry.sendAlarmSetting.valley = "";
            $scope.consumerserverEntry.sendAlarmSetting.fluctuation = "";
            $scope.consumerserverEntry.sendAlarmSetting.fluctuationBase = "";
            $scope.consumerserverEntry.ackAlarmSetting.peak = "";
            $scope.consumerserverEntry.ackAlarmSetting.valley = "";
            $scope.consumerserverEntry.ackAlarmSetting.fluctuation = "";
            $scope.consumerserverEntry.ackAlarmSetting.fluctuationBase = "";
            $scope.consumerserverEntry.isQpsAlarm = true;
            $scope.consumerserverEntry.port = "";
            $scope.consumerserverEntry.type = "";
            $scope.consumerserverEntry.groupId = "";
            $http.get(window.contextPath + "/console/server/defaultcresource").success(function (data) {
                $scope.consumerserverEntry.sendAlarmSetting.peak = data.sendAlarmSetting.peak;
                $scope.consumerserverEntry.sendAlarmSetting.valley = data.sendAlarmSetting.valley;
                $scope.consumerserverEntry.sendAlarmSetting.fluctuation = data.sendAlarmSetting.fluctuation;
                $scope.consumerserverEntry.sendAlarmSetting.fluctuationBase = data.sendAlarmSetting.fluctuationBase;
                $scope.consumerserverEntry.isQpsAlarm = data.isQpsAlarm;
                $scope.consumerserverEntry.ackAlarmSetting.peak = data.ackAlarmSetting.peak;
                $scope.consumerserverEntry.ackAlarmSetting.valley = data.ackAlarmSetting.valley;
                $scope.consumerserverEntry.ackAlarmSetting.fluctuation = data.ackAlarmSetting.fluctuation;
                $scope.consumerserverEntry.ackAlarmSetting.fluctuationBase = data.ackAlarmSetting.fluctuationBase;
            });
        }

        $scope.setModalInput = function (index) {
            $scope.isReadOnly = true;
            $scope.consumerserverEntry.id = $scope.searchPaginator.currentPageItems[index].id;
            $scope.consumerserverEntry.ip = $scope.searchPaginator.currentPageItems[index].ip;
            $scope.consumerserverEntry.hostname = $scope.searchPaginator.currentPageItems[index].hostname;
            $scope.consumerserverEntry.type = $scope.searchPaginator.currentPageItems[index].type;
            $scope.consumerserverEntry.groupName = $scope.searchPaginator.currentPageItems[index].groupName;
            $scope.consumerserverEntry.sendAlarmSetting.peak = $scope.searchPaginator.currentPageItems[index].sendAlarmSetting.peak;
            $scope.consumerserverEntry.sendAlarmSetting.valley = $scope.searchPaginator.currentPageItems[index].sendAlarmSetting.valley;
            $scope.consumerserverEntry.sendAlarmSetting.fluctuation = $scope.searchPaginator.currentPageItems[index].sendAlarmSetting.fluctuation;
            $scope.consumerserverEntry.sendAlarmSetting.fluctuationBase = $scope.searchPaginator.currentPageItems[index].sendAlarmSetting.fluctuationBase;
            $scope.consumerserverEntry.isQpsAlarm = $scope.searchPaginator.currentPageItems[index].isQpsAlarm;
            $scope.consumerserverEntry.ackAlarmSetting.peak = $scope.searchPaginator.currentPageItems[index].ackAlarmSetting.peak;
            $scope.consumerserverEntry.ackAlarmSetting.valley = $scope.searchPaginator.currentPageItems[index].ackAlarmSetting.valley;
            $scope.consumerserverEntry.ackAlarmSetting.fluctuation = $scope.searchPaginator.currentPageItems[index].ackAlarmSetting.fluctuation;
            $scope.consumerserverEntry.ackAlarmSetting.fluctuationBase = $scope.searchPaginator.currentPageItems[index].ackAlarmSetting.fluctuationBase;
            $scope.consumerserverEntry.port = $scope.searchPaginator.currentPageItems[index].port;
            $scope.consumerserverEntry.groupId = $scope.searchPaginator.currentPageItems[index].groupId;
            $scope.consumerserverEntry.alarm = $scope.searchPaginator.currentPageItems[index].alarm;
            $scope.consumerserverEntry.active = $scope.searchPaginator.currentPageItems[index].active;

        }

        $scope.setConsumerIps = function (ip) {
            $http.get(window.contextPath + "/console/server/consumer/get/topics", {
                params: {
                    ip: ip
                }
            }).success(function (data) {
                var size = data.length;
                if (size > 0) {
                    var topic = data.join(",");
                    localStorage.setItem("topic", topic);
                    window.location = window.contextPath + "/console/topic";
                }
            });
        }

        $rootScope.removerecord = function (sid) {
            $http.get(window.contextPath + "/console/server/consumer/remove", {
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

        $scope.types = ["MASTER", "SLAVE"];

        $http({
            method: 'GET',
            url: window.contextPath + '/console/server/grouptype'
        }).success(function (data) {
            $scope.groups = data;
        }).error(function (data, status, headers, config) {
        });

        $scope.changeconsumeralarm = function (ip, index) {
            var id = "#calarm" + index;
            var check = $(id).prop('checked');
            $http.get(window.contextPath + '/console/server/consumer/alarm', {
                params: {
                    ip: ip,
                    alarm: check
                }
            }).success(function (response) {
            });
        }

        $scope.changeconsumeractive = function (ip, index) {
            var id = "#cactive" + index;
            var check = $(id).prop('checked');
            $http.get(window.contextPath + '/console/server/consumer/active', {
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

