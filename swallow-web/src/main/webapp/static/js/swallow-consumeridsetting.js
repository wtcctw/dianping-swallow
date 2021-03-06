module
    .factory(
    'Paginator',
    function () {
        return function (fetchFunction, pageSize, entity) {
            var paginator = {
                hasNextVar: false,
                fetch: function (page) {
                    this.currentOffset = (page - 1) * pageSize;
                    this._load();
                },
                handleResult: function (object) {
                    for (var i = 0; i < this.currentPageItems.length; ++i) {
                        if (typeof(this.currentPageItems[i].consumerIpInfos) != "undefined") {
                            var length = this.currentPageItems[i].consumerIpInfos.length;
                            var ips = "";
                            for (var j = 0; j < length; ++j) {
                                if (j == 0) {
                                    ips += this.currentPageItems[i].consumerIpInfos[j].ip;
                                } else {
                                    ips += "," + this.currentPageItems[i].consumerIpInfos[j].ip;
                                }
                            }
                            this.currentPageItems[i].ips = ips;
                        }
                    }
                },
                next: function () {
                    if (this.hasNextVar) {
                        this.currentOffset += pageSize;
                        this._load();
                    }
                },
                _load: function () {
                    var self = this; // must use self
                    self.currentPage = Math
                            .floor(self.currentOffset / pageSize) + 1;
                    entity.offset = this.currentOffset;
                    entity.limit = pageSize + 1;
                    fetchFunction(
                        entity,
                        function (data) {
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
                                    self.currentPage + 1];
                            } else if (self.currentPage == 1
                                && self.totalPage > 1) {
                                self.pages = [
                                    self.currentPage,
                                    self.currentPage + 1];
                            } else if (self.currentPage == self.totalPage
                                && self.totalPage > 1) {
                                self.pages = [
                                    self.currentPage - 1,
                                    self.currentPage];
                            }
                            self.currentPageItems = items
                                .slice(0, pageSize);
                            self.handleResult(new Object());
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
        function ($rootScope, $scope, $http, Paginator,
                  ngDialog, $interval) {
            var fetchFunction = function (entity, callback) {
                $http.post(window.contextPath + $scope.suburl,
                    entity).success(callback);
            };

            $scope.suburl = "/console/consumerid/list";
            $scope.numrecord = 30;

            $scope.topic = "";
            $scope.consumerId = "";
            $scope.consumerIp = "";
            var tmpname = localStorage.getItem("topic");
            if (tmpname != null) {
                sessionStorage.setItem("navigationTopic", tmpname);
                $scope.topic = tmpname;
                localStorage.removeItem("topic");
            }

            $scope.query = new Object();
            $scope.query.topic = $scope.topic;
            $scope.query.consumerId = $scope.consumerId;
            $scope.query.consumerIp = $scope.consumerIp;
            $scope.query.inactive = true;

            $scope.apps = [];
            $scope.resultDict = {};

            $scope.consumeridEntry = {};
            $scope.consumeridEntry.consumerAlarmSetting = {};
            $scope.consumeridEntry.consumerAlarmSetting.sendQpsAlarmSetting = {};
            $scope.consumeridEntry.consumerAlarmSetting.ackQpsAlarmSetting = {};
            $scope.consumeridEntry.consumerAlarmSetting.sendDelay;
            $scope.consumeridEntry.consumerAlarmSetting.ackDelay;
            $scope.consumeridEntry.consumerAlarmSetting.accumulation;
            $scope.consumeridEntry.consumerAlarmSetting.sendQpsAlarmSetting.peak;
            $scope.consumeridEntry.consumerAlarmSetting.sendQpsAlarmSetting.valley;
            $scope.consumeridEntry.consumerAlarmSetting.sendQpsAlarmSetting.fluctuation;
            $scope.consumeridEntry.consumerAlarmSetting.sendQpsAlarmSetting.fluctuationBase;
            $scope.consumeridEntry.consumerAlarmSetting.sendQpsAlarmSetting.peak;
            $scope.consumeridEntry.consumerAlarmSetting.ackQpsAlarmSetting.peak;
            $scope.consumeridEntry.consumerAlarmSetting.ackQpsAlarmSetting.valley;
            $scope.consumeridEntry.consumerAlarmSetting.ackQpsAlarmSetting.fluctuation;
            $scope.consumeridEntry.consumerAlarmSetting.ackQpsAlarmSetting.fluctuationBase;
            $scope.consumeridEntry.consumerId;
            $scope.consumeridEntry.topic;
            $scope.consumeridEntry.alarm;
            $scope.consumeridEntry.consumerIpInfos;
            $scope.consumeridEntry.consumerApplications = [];

            $scope.refreshpage = function (myForm, num) {
                if ($scope.consumeridEntry.consumerAlarmSetting.sendQpsAlarmSetting.peak < $scope.consumeridEntry.consumerAlarmSetting.sendQpsAlarmSetting.valley
                    || $scope.consumeridEntry.consumerAlarmSetting.ackQpsAlarmSetting.peak < $scope.consumeridEntry.consumerAlarmSetting.ackQpsAlarmSetting.valley) {
                    alert("谷值不能小于峰值");
                    return;
                }
                if (typeof($scope.consumeridEntry.consumerIpInfos) != "undefined") {
                    var length = $scope.consumeridEntry.consumerIpInfos.length;
                    for (var i = 0; i < length; ++i) {
                        var id = "#ip" + "alarm" + i;
                        var check = $(id).prop('checked');
                        $scope.consumeridEntry.consumerIpInfos[i].alarm = check;
                        id = "#ip" + "active" + i;
                        check = $(id).prop('checked');
                        $scope.consumeridEntry.consumerIpInfos[i].active = check;
                    }
                }
                $scope.consumeridEntry.consumerApplications = $("#consumerApplications").val().split(",");

                var id = "#myModal" + num;
                $(id).modal('hide');

                $http.post(window.contextPath + '/console/consumerid/update', $scope.consumeridEntry).success(function (response) {
                    $scope.query.topic = $scope.consumeridEntry.topic;
                    $scope.query.consumerId = $scope.consumeridEntry.consumerId;
                    $scope.searchPaginator = Paginator(fetchFunction, $scope.numrecord, $scope.query);
                });
            }

            $scope.clearModal = function () {
                $scope.consumeridEntry.consumerId = "";
                $scope.consumeridEntry.topic = "";
                $scope.consumeridEntry.alarm = true;
                $scope.consumeridEntry.consumerIpInfos = "";
                $scope.consumeridEntry.consumerApplications = "";
                $scope.consumeridEntry.consumerAlarmSetting.sendDelay = "";
                $scope.consumeridEntry.consumerAlarmSetting.ackDelay = "";
                $scope.consumeridEntry.consumerAlarmSetting.accumulation = "";
                $scope.consumeridEntry.consumerAlarmSetting.isDelayAlarm = true;
                $scope.consumeridEntry.consumerAlarmSetting.isIpAlarm = true;
                $scope.consumeridEntry.consumerAlarmSetting.isAccuAlarm = true;
                $scope.consumeridEntry.consumerAlarmSetting.sendQpsAlarmSetting.peak = "";
                $scope.consumeridEntry.consumerAlarmSetting.sendQpsAlarmSetting.valley = "";
                $scope.consumeridEntry.consumerAlarmSetting.sendQpsAlarmSetting.fluctuation = "";
                $scope.consumeridEntry.consumerAlarmSetting.sendQpsAlarmSetting.fluctuationBase = "";
                $scope.consumeridEntry.consumerAlarmSetting.isQpsAlarm = true;
                $scope.consumeridEntry.consumerAlarmSetting.sendQpsAlarmSetting.peak;
                $scope.consumeridEntry.consumerAlarmSetting.ackQpsAlarmSetting.peak = "";
                $scope.consumeridEntry.consumerAlarmSetting.ackQpsAlarmSetting.valley = "";
                $scope.consumeridEntry.consumerAlarmSetting.ackQpsAlarmSetting.fluctuation = "";
                $scope.consumeridEntry.consumerAlarmSetting.ackQpsAlarmSetting.fluctuationBase = "";
            }

            $scope.setModalInput = function (index, showApp) {

                if (typeof($scope.consumeridEntry.consumerApplications) != "undefined") {
                    var consumerApplications = $scope.searchPaginator.currentPageItems[index].consumerApplications;
                    $('#consumerApplications').tagsinput('removeAll');
                    if (consumerApplications != null && consumerApplications.length > 0) {
                        for (var i = 0; i < consumerApplications.length; ++i)
                            $('#consumerApplications').tagsinput('add', consumerApplications[i]);
                    }
                } else {
                    $('#consumerApplications').tagsinput('removeAll');
                }

                if (showApp) {
                    var length = $scope.searchPaginator.currentPageItems[index].consumerIpInfos.length;
                    if (length > 0) {
                        var ips = "";
                        for (var i = 0; i < length; ++i) {
                            ips += $scope.searchPaginator.currentPageItems[index].consumerIpInfos[i].ip;
                            if (i != length - 1) {
                                ips += ",";
                            }
                        }
                        var result = [];
                        $scope.ipEntry = {};
                        $scope.ipEntry.ip = ips;
                        $scope.ipEntry.alarm = true;
                        $scope.ipEntry.application = "";
                        $http.post(window.contextPath + '/console/ip/list', $scope.ipEntry).success(function (data) {
                            if (data.first > 0) {
                                $scope.apps = data.second;
                            }
                            $scope.addToDict($scope.searchPaginator.currentPageItems[index].consumerIpInfos);
                            if ($scope.apps.length > 0) {
                                $scope.addToDict($scope.apps);
                            }
                            for (var s in $scope.resultDict) {
                                result.push($scope.resultDict[s]);
                            }

                            if (result.length == 0) {
                                $scope.consumeridEntry.consumerIpInfos = $scope.searchPaginator.currentPageItems[index].consumerIpInfos;
                            } else {
                                result.sort($scope.compareArray);
                                result.reverse();
                                $scope.consumeridEntry.consumerIpInfos = result;
                            }
                            $scope.apps = [];
                            $scope.resultDict = {};
                        });
                    }

                } else {
                    $scope.consumeridEntry.consumerIpInfos = $scope.searchPaginator.currentPageItems[index].consumerIpInfos;
                }
                $scope.consumeridEntry.id = $scope.searchPaginator.currentPageItems[index].id;
                $scope.consumeridEntry.alarm = $scope.searchPaginator.currentPageItems[index].alarm;
                $scope.consumeridEntry.consumerId = $scope.searchPaginator.currentPageItems[index].consumerId;
                $scope.consumeridEntry.topic = $scope.searchPaginator.currentPageItems[index].topic;
                $scope.consumeridEntry.consumerAlarmSetting.sendDelay = $scope.searchPaginator.currentPageItems[index].consumerAlarmSetting.sendDelay;
                $scope.consumeridEntry.consumerAlarmSetting.ackDelay = $scope.searchPaginator.currentPageItems[index].consumerAlarmSetting.ackDelay;
                $scope.consumeridEntry.consumerAlarmSetting.isDelayAlarm = $scope.searchPaginator.currentPageItems[index].consumerAlarmSetting.isDelayAlarm;
                $scope.consumeridEntry.consumerAlarmSetting.accumulation = $scope.searchPaginator.currentPageItems[index].consumerAlarmSetting.accumulation;
                $scope.consumeridEntry.consumerAlarmSetting.isAccuAlarm = $scope.searchPaginator.currentPageItems[index].consumerAlarmSetting.isAccuAlarm;
                $scope.consumeridEntry.consumerAlarmSetting.sendQpsAlarmSetting.peak = $scope.searchPaginator.currentPageItems[index].consumerAlarmSetting.sendQpsAlarmSetting.peak;
                $scope.consumeridEntry.consumerAlarmSetting.sendQpsAlarmSetting.valley = $scope.searchPaginator.currentPageItems[index].consumerAlarmSetting.sendQpsAlarmSetting.valley;
                $scope.consumeridEntry.consumerAlarmSetting.sendQpsAlarmSetting.fluctuation = $scope.searchPaginator.currentPageItems[index].consumerAlarmSetting.sendQpsAlarmSetting.fluctuation;
                $scope.consumeridEntry.consumerAlarmSetting.sendQpsAlarmSetting.fluctuationBase = $scope.searchPaginator.currentPageItems[index].consumerAlarmSetting.sendQpsAlarmSetting.fluctuationBase;
                $scope.consumeridEntry.consumerAlarmSetting.isQpsAlarm = $scope.searchPaginator.currentPageItems[index].consumerAlarmSetting.isQpsAlarm;
                $scope.consumeridEntry.consumerAlarmSetting.ackQpsAlarmSetting.peak = $scope.searchPaginator.currentPageItems[index].consumerAlarmSetting.ackQpsAlarmSetting.peak;
                $scope.consumeridEntry.consumerAlarmSetting.ackQpsAlarmSetting.valley = $scope.searchPaginator.currentPageItems[index].consumerAlarmSetting.ackQpsAlarmSetting.valley;
                $scope.consumeridEntry.consumerAlarmSetting.ackQpsAlarmSetting.fluctuation = $scope.searchPaginator.currentPageItems[index].consumerAlarmSetting.ackQpsAlarmSetting.fluctuation;
                $scope.consumeridEntry.consumerAlarmSetting.ackQpsAlarmSetting.fluctuationBase = $scope.searchPaginator.currentPageItems[index].consumerAlarmSetting.ackQpsAlarmSetting.fluctuationBase;
                $scope.consumeridEntry.consumerAlarmSetting.isIpAlarm = $scope.searchPaginator.currentPageItems[index].consumerAlarmSetting.isIpAlarm;

            }

            $scope.addToDict = function (arra) {
                if (!arra instanceof Array) {
                    throw new Error("only support array!");
                }
                for (var i = 0; i < arra.length; i++) {
                    var currentElement = arra[i];
                    var ip = currentElement.ip;
                    var currentDict;

                    if (!(ip in $scope.resultDict)) {
                        $scope.resultDict[ip] = {};
                    }
                    currentDict = $scope.resultDict[ip];
                    for (var name in currentElement) {
                        if (currentElement.hasOwnProperty(name) && !currentDict.hasOwnProperty(name)) {
                            currentDict[name] = currentElement[name];
                        }
                    }
                }
            }

            $scope.compareArray = function (a, b) {
                if (a.application == null) {
                    return -1;
                }
                if (b.application == null) {
                    return 1;
                }
                if (a.application < b.application)
                    return 1;
                else if (a.application > b.application)
                    return -1;
                else
                    return 0;
            }

            $scope.setInactive = function () {
                $scope.query.inactive = !$scope.query.inactive;
                $scope.query.topic = $("#searchtopic").val();
                $scope.query.consumerIp = $("#searchconsumerip").val();
                $scope.query.consumerId = $("#searchconsumerid").val();
                $scope.searchPaginator = Paginator(fetchFunction, $scope.numrecord, $scope.query);
            }

            $scope.setApplication = function (application) {
                localStorage.setItem("application", application);
            }

            //for enter is pressed
            $scope.myKeyup = function (e) {
                var keycode = window.event ? e.keyCode : e.which;
                if (keycode == 13) {
                    if ($scope.topic == null || $scope.topic.length == 0){
                        sessionStorage.removeItem("navigationTopic");
                        $scope.query.topic = $scope.topic;
                        $scope.searchPaginator = Paginator(fetchFunction, $scope.topicnum, $scope.query);
                    }
                }
            };

            //如果topic列表返回空，则不会执行initpage
            $scope.initpage = function () {

                //下面是在table render完成后执行的js
                $http({
                    method: 'GET',
                    url: window.contextPath + '/console/topic/namelist'
                }).success(function (data, status, headers, config) {
                    var topicNameList = data.first;
                    $("#searchtopic").typeahead({
                        items: 16,
                        source: topicNameList,
                        updater: function (c) {
                            $scope.topic = c;
                            sessionStorage.setItem("navigationTopic", c);
                            $scope.query.topic = $scope.topic;
                            $scope.query.consumerId = $("#searchconsumerid").val();
                            $scope.query.consumerIp = $("#searchconsumerip").val();
                            $scope.searchPaginator = Paginator(fetchFunction, $scope.numrecord, $scope.query);
                            return c;
                        }
                    })

                    if ($scope.topic.length == 0) { //默认请求
                        var navigation = sessionStorage.getItem("navigationTopic");
                        if (navigation != null && navigation.length > 0) {
                            $scope.topic = navigation;
                            $scope.query.topic = $scope.topic;
                            $scope.searchPaginator = Paginator(fetchFunction, $scope.numrecord, $scope.query);
                        } else if (topicNameList != null && topicNameList.length > 0) {
                            var topics = topicNameList.join(",");
                            if (topicNameList.length == 1) {
                                $scope.topic = topics;
                            } else {
                                $scope.topic = "";
                            }
                            $scope.query.topic = topics;
                            $scope.searchPaginator = Paginator(fetchFunction, $scope.numrecord, $scope.query);
                        }
                    } else {//点击cid跳转过来
                        $scope.searchPaginator = Paginator(fetchFunction, $scope.numrecord, $scope.query);
                    }
                }).error(function (data, status, headers, config) {
                });

                $http({
                    method: 'GET',
                    url: window.contextPath + '/console/consumerid/allconsumerid'
                }).success(function (data, status, headers, config) {
                    $("#searchconsumerid").typeahead({
                        items: 16,
                        source: data,
                        updater: function (c) {
                            $scope.consumerId = c;
                            $scope.query.consumerId = $scope.consumerId;
                            $scope.query.topic = $("#searchtopic").val();
                            $scope.query.consumerIp = $("#searchconsumerip").val();
                            $scope.searchPaginator = Paginator(fetchFunction, $scope.numrecord, $scope.query);
                            return c;
                        }
                    })
                }).error(function (data, status, headers, config) {
                });

                $http({
                    method: 'GET',
                    url: window.contextPath + '/console/consumerid/ips'
                }).success(function (data, status, headers, config) {

                    $("#searchconsumerip").typeahead({
                        items: 16,
                        source: data,
                        updater: function (c) {
                            $scope.consumerIp = c;
                            $scope.query.consumerIp = $scope.consumerIp;
                            $scope.query.topic = $("#searchtopic").val();
                            $scope.query.consumerId = $("#searchconsumerid").val();
                            $scope.searchPaginator = Paginator(fetchFunction, $scope.numrecord, $scope.query);
                            return c;
                        }
                    })

                }).error(function (data, status, headers, config) {
                });

                $http({
                    method: 'GET',
                    url: window.contextPath + '/console/consumerid/alarm/ipinfo/count/inactive'
                }).success(function (data, status, headers, config) {
                    $scope.countinactive = data;
                }).error(function (data, status, headers, config) {
                });

                $http({
                    method: 'GET',
                    url: window.contextPath + '/console/application/applicationname'
                }).success(function (data, status, headers, config) {
                    $('#consumerApplications').tagsinput({
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

            }

            $scope.initpage();

            $scope.setIP = function (ip) {
                localStorage.setItem("ip", ip);
//								$http.get(window.contextPath + '/console/consumerid/ipinfo/' + topic + "/" + "cid").success(function(response) {
//					        	});
            }

            $scope.changealarm = function (consumerid, topic, index) {
                var id = "#alarm" + index;
                var check = $(id).prop('checked');
                $http.get(window.contextPath + '/console/consumerid/alarm', {
                    params: {
                        topic: topic,
                        consumerId: consumerid,
                        alarm: check
                    }
                }).success(function (response) {
                });
            }

            $scope.changeipinfo = function (cid, topic, type, index, ip) {
                var id = "#ip" + type + index;
                var check = $(id).prop('checked');
                if ("alarm" == type) {
                    $http.get(window.contextPath + "/console/consumerid/alarm/ipinfo/alarm",
                        {
                            params: {
                                cid: cid,
                                topic: topic,
                                ip: ip,
                                alarm: check
                            }
                        })
                        .success(function (data) {
                        });
                } else {
                    $http.get(window.contextPath + "/console/consumerid/alarm/ipinfo/active",
                        {
                            params: {
                                cid: cid,
                                topic: topic,
                                ip: ip,
                                active: check
                            }
                        })
                        .success(function (data) {
                        });
                }
            }

            $rootScope.removerecord = function (cid, topic) {
                $http.get(window.contextPath
                    + "/console/consumerid/remove",
                    {
                        params: {
                            consumerId: cid,
                            topic: topic
                        }
                    })
                    .success(function (data) {
                        $scope.query.topic = "";
                        $scope.query.consumerId = "";
                        $scope.query.consumerIp = "";
                        $scope.searchPaginator = Paginator(
                            fetchFunction,
                            $scope.numrecord, $scope.query);
                    });
                return true;
            }

            $scope.isdefault = function (compare) {
                return compare != "default";
            }

            $scope.dialog = function (cid, topic) {
                $rootScope.cid = cid;
                $rootScope.topic = topic;
                ngDialog
                    .open({
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
								<button type="button" class="btn btn-primary" ng-click="removerecord(cid, topic)&&closeThisDialog()">确定</button>\
							</div>\
						</div>\
					</div>',
                        plain: true,
                        className: 'ngdialog-theme-default'
                    });
            };

        }]);


