module
    .factory(
    'Paginator',
    function () {
        return function (fetchFunction, pageSize) {
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
                    var self = this; // must use self
                    self.currentPage = Math
                            .floor(self.currentOffset / pageSize) + 1;
                    fetchFunction(
                        this.currentOffset,
                        pageSize + 1,
                        function (data) {
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
    'AlarmMetaController',
    [
        '$rootScope',
        '$scope',
        '$http',
        'Paginator',
        'ngDialog',
        '$interval',
        function ($rootScope, $scope, $http, Paginator,
                  ngDialog, $interval) {
            var fetchFunction = function (offset, limit,
                                          callback) {
                $scope.searchEntity = {
                    offset: offset,
                    limit: limit
                };
                console.log($scope.searchEntity);
                $http.get(window.contextPath + $scope.suburl, {
                    params: $scope.searchEntity
                }).success(callback);
            };
            $scope.suburl = "/console/setting/alarmmeta/list";
            $scope.pageSize = 30;
            $scope.queryCount = 0;
            $scope.query = function () {
                $scope.searchPaginator = Paginator(
                    fetchFunction, $scope.pageSize);
            }

            $scope.refreshpage = function (myForm) {
                $('#myModal').modal('hide');
                console.log($scope.alarmMetaEntity);

                $scope.alarmMetaEntity.majorTopics = $("#majorTopics").val().split(",");
                $http
                    .post(
                    window.contextPath
                    + '/console/setting/alarmmeta/create',
                    $scope.alarmMetaEntity)
                    .success(
                    function (data) {
                        $scope.searchPaginator = Paginator(
                            fetchFunction,
                            $scope.pageSize);
                    });

            }
            $scope.clearModal = function () {
                $scope.alarmMetaEntity = {};
                $scope.alarmMetaEntity.metaId = 0;
                $scope.alarmMetaEntity.isUpdate = false;
                $scope.alarmMetaEntity.majorTopics = [];
            }

            $scope.setModalInput = function (index) {
                if (typeof($scope.alarmMetaEntity.majorTopics) != "undefined") {
                    var majorTopics = $scope.searchPaginator.currentPageItems[index].majorTopics;
                    $('#majorTopics').tagsinput('removeAll');
                    if (majorTopics != null && majorTopics.length > 0) {
                        for (var i = 0; i < majorTopics.length; ++i)
                            $('#majorTopics').tagsinput('add', majorTopics[i]);
                    }
                } else {
                    $('#majorTopics').tagsinput('removeAll');
                }
                $scope.alarmMetaEntity.type = $scope.searchPaginator.currentPageItems[index].type;
                $scope.alarmMetaEntity.levelType = $scope.searchPaginator.currentPageItems[index].levelType;
                $scope.alarmMetaEntity.isSmsMode = $scope.searchPaginator.currentPageItems[index].isSmsMode;
                $scope.alarmMetaEntity.isWeiXinMode = $scope.searchPaginator.currentPageItems[index].isWeiXinMode;
                $scope.alarmMetaEntity.isMailMode = $scope.searchPaginator.currentPageItems[index].isMailMode;
                $scope.alarmMetaEntity.isSendSwallow = $scope.searchPaginator.currentPageItems[index].isSendSwallow;
                $scope.alarmMetaEntity.isSendBusiness = $scope.searchPaginator.currentPageItems[index].isSendBusiness;
                $scope.alarmMetaEntity.alarmTitle = $scope.searchPaginator.currentPageItems[index].alarmTitle;
                $scope.alarmMetaEntity.alarmTemplate = $scope.searchPaginator.currentPageItems[index].alarmTemplate;
                $scope.alarmMetaEntity.alarmDetail = $scope.searchPaginator.currentPageItems[index].alarmDetail;
                $scope.alarmMetaEntity.maxTimeSpan = $scope.searchPaginator.currentPageItems[index].maxTimeSpan;
                $scope.alarmMetaEntity.daySpanBase = $scope.searchPaginator.currentPageItems[index].daySpanBase;
                $scope.alarmMetaEntity.nightSpanBase = $scope.searchPaginator.currentPageItems[index].nightSpanBase;
                $scope.alarmMetaEntity.createTime = $scope.searchPaginator.currentPageItems[index].createTime;
                $scope.alarmMetaEntity.isUpdate = true;
            }

            $rootScope.removerecord = function (cid) {
                console.log(cid);
                $http
                    .get(
                    window.contextPath
                    + "/console/setting/alarmmeta/remove",
                    {
                        params: {
                            metaId: cid
                        }
                    })
                    .success(
                    function (data) {
                        $scope.searchPaginator = Paginator(
                            fetchFunction,
                            $scope.pageSize);
                    });
                return true;
            }

            $scope.dialog = function (cid) {
                $rootScope.cid = cid;
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
								<button type="button" class="btn btn-primary" ng-click="removerecord(cid)&&closeThisDialog()">确定</button>\
							</div>\
						</div>\
					</div>',
                        plain: true,
                        className: 'ngdialog-theme-default'
                    });
            };

            $scope.query();
            $scope.clearModal();
            $http(
                {
                    method: 'GET',
                    url: window.contextPath
                    + '/console/alarmmeta/query/types'
                }).success(
                function (datas, status, headers, config) {
                    $scope.types = datas;
                }).error(
                function (datas, status, headers, config) {
                    console.log("types读取错误");
                });
            $http({
                method: 'GET',
                url: window.contextPath + '/console/topic/namelist'
            }).success(function (data, status, headers, config) {
                $('#majorTopics').tagsinput({
                    typeahead: {
                        items: 16,
                        source: data.first,
                        displayText: function (item) {
                            return item;
                        }
                    }
                });
            }).error(function (data, status, headers, config) {
            });
            $http(
                {
                    method: 'GET',
                    url: window.contextPath
                    + '/console/alarmmeta/query/leveltypes'
                }).success(
                function (datas, status, headers, config) {
                    $scope.leveltypes = datas;
                }).error(
                function (datas, status, headers, config) {
                    console.log("leveltypes读取错误");
                });
            $scope.booleans = [false, true];
            $("#check-all").click(
                function () {
                    $("input[name='check-list']").prop(
                        "checked", this.checked);
                });

            $('.input-tooltip').tooltip({
                showDelay: 0,
                hideDelay: 0
            });

            $scope.batchUpdate = function (updateType, isTrue) {
                var metaIds = [];
                $("input[name='check-list']:checked").each(
                    function (i) {
                        metaIds.push($(this).val());
                    });
                if (metaIds.length <= 0) {
                    alert("请选择要更新的行。");
                    return;
                }
                $("input[name='check-list']")
                var updateEntity = {
                    metaIds: metaIds,
                    updateType: updateType,
                    isOpen: isTrue
                };
                console.log(updateEntity);
                $http
                    .post(
                    window.contextPath
                    + '/console/setting/alarmmeta/batchupdate',
                    updateEntity)
                    .success(
                    function (data) {
                        $scope.searchPaginator = Paginator(
                            fetchFunction,
                            $scope.pageSize);
                    });
            }

        }]);