/**
 * Created by mingdongli on 15/11/20.
 */
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

module.controller('TopicApplyController', ['$rootScope', '$scope', '$http', 'Paginator', 'ngDialog','$interval',
    function($rootScope, $scope, $http, Paginator, ngDialog,$interval){
        var fetchFunction = function(entity, callback){
            $http.post(window.contextPath + $scope.suburl, entity).success(callback);
        };

        $scope.topic = "";

        $scope.suburl = "/console/topicapply/list";
        $scope.topicnum = 30;
        $scope.query = new Object();
        $scope.query.topic = "";

        $scope.searchPaginator = Paginator(fetchFunction, $scope.topicnum, $scope.query);

        $scope.topicApplyEntry = {};
        $scope.topicApplyEntry.topic;
        $scope.topicApplyEntry.topicApplyDto = {};
        $scope.topicApplyEntry.lionConfigureResult = {};
        $scope.topicApplyEntry.responseStatus = {};
        $scope.topicApplyEntry.topicApplyDto.size;
        $scope.topicApplyEntry.topicApplyDto.amount;
        $scope.topicApplyEntry.topicApplyDto.approver;
        $scope.topicApplyEntry.topicApplyDto.applicant;
        $scope.topicApplyEntry.topicApplyDto.type;
        $scope.topicApplyEntry.lionConfigureResult.mongoServer;
        $scope.topicApplyEntry.lionConfigureResult.consumerServer;
        $scope.topicApplyEntry.lionConfigureResult.size4SevenDay;
        $scope.topicApplyEntry.responseStatus.message;
        $scope.topicApplyEntry.responseStatus.status;

        $scope.setModalInput = function(index){
            $scope.topicApplyEntry.id = $scope.searchPaginator.currentPageItems[index].id;
            $scope.topicApplyEntry.topicApplyDto.size = $scope.searchPaginator.currentPageItems[index].topicApplyDto.size;
            $scope.topicApplyEntry.topicApplyDto.amount = $scope.searchPaginator.currentPageItems[index].topicApplyDto.amount;
            $scope.topicApplyEntry.topicApplyDto.approver = $scope.searchPaginator.currentPageItems[index].topicApplyDto.approver;
            $scope.topicApplyEntry.topicApplyDto.applicant = $scope.searchPaginator.currentPageItems[index].topicApplyDto.applicant;
            $scope.topicApplyEntry.topicApplyDto.type = $scope.searchPaginator.currentPageItems[index].topicApplyDto.type;
            $scope.topicApplyEntry.lionConfigureResult.mongoServer = $scope.searchPaginator.currentPageItems[index].mongoServer;
            $scope.topicApplyEntry.lionConfigureResult.consumerServer = $scope.searchPaginator.currentPageItems[index].consumerServer;
            $scope.topicApplyEntry.lionConfigureResult.size4SevenDay = $scope.searchPaginator.currentPageItems[index].size4SevenDay;
            $scope.topicApplyEntry.responseStatus.message = $scope.searchPaginator.currentPageItems[index].message;
            $scope.topicApplyEntry.responseStatus.status = $scope.searchPaginator.currentPageItems[index].status;
        }

        $http({
            method : 'GET',
            url : window.contextPath + '/console/topic/namelist'
        }).success(function(data, status, headers, config) {
            var topicNameList = data.first;
            $("#searchtopic").typeahead({
                items: 16,
                source : topicNameList,
                updater : function(c) {
                    $scope.topic = c;
                    $scope.query.topic = $scope.topic;
                    $scope.searchPaginator = Paginator(fetchFunction, $scope.topicnum, $scope.query);
                    return c;
                }
            })
        }).error(function(data, status, headers, config) {
        });

    }]);

