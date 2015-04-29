module.factory('Paginator', function(){
	return function(fetchFunction, pageSize,  tname, messageId, startdt, stopdt){
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
					fetchFunction(this.currentOffset, pageSize + 1, tname, messageId, startdt, stopdt,  function(data){
						items = data.topic;
						//items = angular.fromJson(items); //反序列化
						length = data.size;
						self.totalpieces = length;
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
						self.currentPageItemsTicked = items.slice(0, pageSize);
						for(var i=0;i<self.currentPageItems.length;i++){
							self.currentPageItemsTicked[i].ticked = false;
							self.currentPartialCon[i]= "点击展开";
							} 
						self.hasNextVar = items.length === pageSize + 1;
					});
				},
				formatstr: function(str){
					str=str.replace(/\"/ig,"'"); 
					return str; 
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
				totalpieces: 0,
				
				currentPageItems: [],
				currentPartialCon: [],
				currentParsedItems: [],
				currentPageItemsTicked: [],
				currentOffset: 0
		};
		
		//加载第一页
		paginator._load();
		return paginator;
	};
});

module.controller('MessageController', ['$scope', '$http', 'Paginator',
        function($scope, $http, Paginator){
				var fetchFunction = function(offset, limit, tname, messageId, startdt, stopdt, callback){
				var transFn = function(data){
					return $.param(data);
				}
				var postConfig = {
						transformRequest: transFn
				};
				var data = {'offset' : offset,
										'limit': limit,
										'tname': tname,
										'messageId': messageId,
										'startdt' : startdt,
										'stopdt' : stopdt};
				$http.get(window.contextPath + $scope.suburl, {
					params : {
						offset : offset,
						limit : limit,
						tname: tname,
						messageId: messageId,
						startdt : startdt,
						stopdt : stopdt
					}
				}).success(callback);
			};
			
			$scope.isjsonstring = function(str) {
			    try {
			        JSON.parse(str);
			    } catch (e) {
			        return false;
			    }
			    return true;
			};
			$scope.formatres = function(items){
				var str; 
				if($scope.isjsonstring(items))
					str = JSON.stringify(JSON.parse(items), null, 2);
				else
					str = items;
			    //alert(str);
				$scope.messagecontent = str;
				$('#myModal2').modal('show');
			},
			
			$scope.messagecontent = "";
			
			$scope.messageId = "";
			$scope.tname         = "";
			
			$scope.suburl = "/console/message/messagedefault";
			
			$scope.totalmessage = [];
			$scope.lastpage = 0;
			
			//for time controll
			$scope.startdt = "";
			$scope.stopdt = "";
			
			
			$scope.checked = true;
			$scope.showTimeInput = function(){
				if($scope.checked){
					$scope.checked = false;
					$("#starttime").removeAttr("disabled");
					$("#stoptime").removeAttr("disabled");
					$("#summittime").removeAttr("disabled");
					
					$("#searchmid").val("");  //clear in page
					$scope.messageId = "";
					$("#searchmid").attr("disabled","disabled");
				}
				else{		
					$scope.checked = true;
					$("#starttime").val("");
					$("#stoptime").val("");  //clear in page
					$scope.startdt = "";
					$scope.stopdt = "";  //make them empty
					
					$("#starttime").attr("disabled","disabled");
					$("#stoptime").attr("disabled","disabled");
					$("#summittime").attr("disabled","disabled");
					$("#searchmid").removeAttr("disabled");
				}
			};
			
			//for show content when click on 
			$scope.showornot = false;
			$scope.showContent = function(index){
				if(!$scope.showornot){
					$scope.searchPaginator.currentPartialCon[index] = $scope.searchPaginator.currentPageItems[index];
					$scope.showornot = true;
				}
				else{
					$scope.searchPaginator.currentPartialCon[index] = "点击展开";
					$scope.showornot = false;
				}
			};
			
			//for query by time
			$scope.queryByTime = function(){
				$scope.startdt = $("#starttime").val();
				$scope.stopdt = $("#stoptime").val();
				if($scope.startdt.length==0  || $scope.stopdt.length == 0)
					alert("时间不能为空!")
				else{
					if($scope.tname.length == 0){
						alert("Topic不能为空!")
					}
					else{
						$scope.searchPaginator = Paginator(fetchFunction, $scope.messagenum, $scope.tname , $scope.messageId, $scope.startdt,  $scope.stopdt);
					}
				}
			};
			
			//for enter is pressed and search by ip or mid
			$scope.myKeyup = function(e){
	            var keycode = window.event?e.keyCode:e.which;
	            if(keycode==13){
	            	if($scope.tname.length == 0)
	            		alert("请先输入Topic名称");  //query with mid but without topic name
	            	$scope.searchPaginator = Paginator(fetchFunction, $scope.recordofperpage, $scope.tname , $scope.messageId ,$scope.startdt,  $scope.stopdt );
	            }
	        };
	        
	        //invoke when topic name is not empty and recordofperpage is changed
	        $scope.makeChanged = function(){
	        	if($scope.tname.length != 0){
	        		$scope.searchPaginator = Paginator(fetchFunction, $scope.recordofperpage, $scope.tname , $scope.messageId ,$scope.startdt,  $scope.stopdt );
	        	}
	        };
	        
	        //for change the number of per page
	        $scope.numperpage = [
	                        { id: 1, num: 30 },
	                        { id: 2, num: 50 },
	                        { id: 3, num: 70 },
	                        { id: 4, num: 100 }
	                      ];
	        $scope.recordofperpage = $scope.numperpage[0].num;
	        
			// search topic name
			$http({
				method : 'GET',
				url : window.contextPath + '/console/topic/namelist'
			}).success(function(data, status, headers, config) {
				var topicNameList = data;
				$("#topicname").typeahead({
					source : topicNameList,
					updater : function(c) {
						$scope.tname = c;
						$scope.searchPaginator = Paginator(fetchFunction, $scope.recordofperpage, $scope.tname , $scope.messageId, $scope.startdt,  $scope.stopdt);		
						return c;
					}
				})
			}).error(function(data, status, headers, config) {
				alert("响应错误", data);
			});
			
			//judge if redirected from topic view
			var tmpname = localStorage.getItem("name");
			if(tmpname != null){
				$scope.tname = localStorage.getItem("name");
				localStorage.clear();
				$scope.searchPaginator = Paginator(fetchFunction, $scope.recordofperpage, $scope.tname , $scope.messageId,  $scope.startdt,  $scope.stopdt);
			}
			
	        //for deal with re transmit for selected messages
	        $scope.retransmit = function(){
	        	var needtotrans = [];
	        	var tmp;
	        	angular.forEach( $scope.searchPaginator.currentPageItemsTicked, function( value, key ) {
	        		
	        	    if ( value.ticked === true ) {
	        	         value.ticked = false;
	        	         tmp = value;
	        	         delete tmp.ticked;
	        	         needtotrans.push(tmp.mid);
	        	    }
	        	});
	        	$scope.starttransmit(needtotrans);
	        }
	        
	        
	        $scope.starttransmit = function(data){
	        		if(data.length == 0)
	        			$scope.showselect = false;
	        		else
	        			$http.post(window.contextPath + '/console/message/sendmessage', {"param[]":JSON.stringify(data),"topic":$scope.tname}).success(function(response) {
	        			  $scope.showselect = false;
	        			  alert("消息发送成功")
	        });
	        
	        }
	        
	        $scope.showselect = false;
	        $scope.showmultiselect = function(){
	        	if($scope.searchPaginator == null || $scope.searchPaginator.currentPageItemsTicked.length == 0)
	        		alert("没有消息可供选择发送！");
	        	else
	        		$scope.showselect = true;
	        }
	        
	        $scope.refreshpage = function(){
	        	$( document ).ready(function() {
	        		$scope.startdt = "";
					$scope.stopdt = "";
					$scope.messageId = "";
					$scope.searchPaginator = Paginator(fetchFunction, $scope.recordofperpage, $scope.tname , $scope.messageId,  $scope.startdt,  $scope.stopdt);
	        	});
	        }
	        
}]);
