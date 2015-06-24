module.factory('Paginator', function(){
	return function(fetchFunction, pageSize, tname, messageId, startdt, stopdt, sort){
		var paginator = {
				hasNextVar: false,
				limit : pageSize,
				
				fetch: function(page){
					this.currentOffset = (page - 1) * this.limit;
					if(page == this.endPage){
						pageSize = this.totalpieces % this.limit - 1; //最后一页的大小
						if(startdt.length > 0){
							this.basemid = startdt;
						}else{
							this.basemid = !this.reverse ? "1" : "-1"; //取个小值
						} 
					}else{
						this.basemid = "";
					}
					this._load();
				},
				next: function(){
					if(this.hasNextVar){
						this.currentOffset += this.limit;
						if(this.totalpieces - this.currentOffset < this.limit){
							this.fetch(Math.ceil(this.totalpieces/this.limit));
						}
						var len = this.currentPageItems.length;
						if(!this.reverse){
							this.basemid = "-" + this.currentPageItems[len-1].mid;
						}else{
							this.basemid = this.currentPageItems[len-1].mid;
						}
						this._load();
					}
				},
				_load: function(){
					var self = this;  //must use  self
					self.currentPage = Math.floor(self.currentOffset/self.limit) + 1;
					fetchFunction(this.currentOffset, pageSize + 1, tname, messageId, startdt, stopdt, this.basemid, sort, function(data){
						pageSize = self.limit;
						self.basemid = "";
						
						items = data.message;
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
						if(!self.byprevious){
							self.currentPageItems = items.slice(0, pageSize);
							self.hasNextVar = items.length === self.limit + 1;
						}
						else{
							self.currentPageItems = items.slice(0, pageSize).reverse();
							self.byprevious = false;
						}
						if (self.currentPageItems.length > 0) {
							$("#message-retransmit").css(
									'display', 'block');
						} else {
							$("#message-retransmit").css(
									'display', 'none');
						}
						for(var i=0;i<self.currentPageItems.length;i++){
							self.currentPartialCon[i]= "点击展开";
						} 
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
						if(!this.reverse){
							this.basemid = this.currentPageItems[0].mid;
						}else{
							this.basemid = "-" + this.currentPageItems[0].mid;
						}
						this.byprevious = true;
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
				currentOffset: 0,
				
				reverse: false,
				basemid: "",
				byprevious: false
		};
		
		//加载第一页
		paginator._load();
		return paginator;
	};
});

module.controller('MessageController', ['$rootScope', '$scope', '$http', 'Paginator', 'ngDialog', '$interval',
        function($rootScope, $scope, $http, Paginator, ngDialog, $interval){
				var fetchFunction = function(offset, limit, tname, messageId, startdt, stopdt, basemid, sort, callback){
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
										'stopdt' : stopdt,
										'basemid' : basemid,
										'sort' : sort};
				$http.get(window.contextPath + $scope.suburl, {
					params : {
						offset : offset,
						limit : limit,
						tname: tname,
						messageId: messageId,
						startdt : startdt,
						stopdt : stopdt,
						basemid : basemid,
						sort : sort
					}
				}).success(callback);
			};
			
			//－－－－－－－－－－for show content which click on 点击展开－－－－－－－－－－－		
			$scope.messagecontent = "";
			$scope.isjsonstring = function(str) {
			    try {
			        JSON.parse(str);
			    } catch (e) {
			        return false;
			    }
			    return true;
			};
			$scope.formatres2 = function(items){
				var str; 
				if($scope.isjsonstring(items))
					str = JSON.stringify(JSON.parse(items), null, 2);
				else
					str = items;
				$scope.messagecontent = str;
				$('#myModal2').modal('show');
			},
			
			$scope.formatres = function(mid){
				var topic = $scope.tname;
				$http.get(window.contextPath + "/console/message/auth/content", {
					params : {
						topic:topic,
						mid:mid
					}
				}).success(function(data){
					$scope.formatres2(data.c);
				});
			},
			//－－－－－－－－－－for show more options－－－－－－－－－－－
			$scope.fullmessage    = "";
	        $scope.showfullmessage = function(mid){
				var topic = $scope.tname;
				$http.get(window.contextPath + "/console/message/auth/content", {
					params : {
						topic:topic,
						mid:mid
					}
				}).success(function(data){
					delete data.id;
					$scope.formatres3(data);
				});
	        },
	        
			$scope.formatres3 = function(items){
				var str; 
					str = JSON.stringify(items,null, 3);
				$scope.fullmessage = str;
				$('#myModal3').modal('show');
			},
			
			$scope.messageId      = "";
			$scope.tname          = "";
			
			$scope.suburl         = "/console/message/messagedefault";
			
			$scope.totalmessage   = [];
			$scope.lastpage       = 0;
			
			//for time controll
			$scope.startdt        = "";
			$scope.stopdt         = "";
			
			
			//for show content when click on 
			$scope.showornot = false;
			$scope.showContent = function(index){
				var mid = $scope.searchPaginator.currentPageItems[index].mid;
				var topic = $scope.tname;
				if(!$scope.showornot){
					$http.get(window.contextPath + "/console/message/auth/content", {
						params : {
							topic:topic,
							mid:mid
						}
					}).success(function(data){
						$scope.searchPaginator.currentPartialCon[index] = data.c;
						$scope.showornot = true;
					});

				}
				else{
					$scope.searchPaginator.currentPartialCon[index] = "点击展开";
					$scope.showornot = false;
				}
			};
			
			$scope.dumprecord = function(){
				$scope.startdt = $("#starttime").val();
				$scope.stopdt = $("#stoptime").val();
				var d1 = new Date($scope.startdt);
				var d2 = new Date($scope.stopdt);
				if(d1 > d2){
					alert("开始时间不能小于结束时间!");
					return;
				}
					
				if($scope.startdt.length==0  || $scope.stopdt.length == 0)
					alert("时间不能为空!")
				else{
					if($scope.tname.length == 0){
					}
					else{
						$("#dumprecord").css(
		                           'display', 'block');
						//$interval( function(){ $scope.changeprogress(); }, 100);
						setInterval(updateProgressBar,400);
						$http.get(window.contextPath + "/console/message/auth/dump", {
							params : {
								topic: $scope.tname,
								startdt: $scope.startdt,
								stopdt: $scope.stopdt
							}
						}).success(function(data){
							if(data.status != 0){
								alert("error with status " + data.status);
								return;
							}
							localStorage.setItem("file", JSON.stringify(data.file)); //json for array
							window.location.href = window.contextPath + "/console/download";
						});
					}
				}
			};
			
			$scope.changeprogress = function(){
				var p = $('.progress-bar').attr('aria-valuenow');
				var pint = parseInt(p);
				var value;
				if(pint + 2 >= 95){
					value = 95;
				}else{
					value = pint + 2;
				}
				var vstring = value.toString();
				$('.progress-bar').css('width', vstring+'%').attr('aria-valuenow', vstring);
			}
			
			//for query by time
			$scope.queryByTime = function(){
				$scope.startdt = $("#starttime").val();
				$scope.stopdt = $("#stoptime").val();
				var d1 = new Date($scope.startdt);
				var d2 = new Date($scope.stopdt);
				if(d1 > d2){
					alert("开始时间不能小于结束时间!");
					return;
				}
					
				if($scope.startdt.length==0  || $scope.stopdt.length == 0)
					alert("时间不能为空!")
				else{
					if($scope.tname.length == 0){
						//alert("Topic不能为空!")
					}
					else{
						var sort = false;
						if(typeof($scope.searchPaginator) != "undefined"){
							sort = $scope.searchPaginator.reverse;
						}
						$scope.searchPaginator = Paginator(fetchFunction, $scope.recordofperpage, $scope.tname , $scope.messageId, $scope.startdt,  $scope.stopdt, sort);
	            		if(typeof($scope.searchPaginator) != "undefined"){
	            			$scope.searchPaginator.reverse = sort;	            		
	            		}
					}
				}
			};
			
			//for enter is pressed and search by ip or mid
			$scope.myKeyup = function(e){
	            var keycode = window.event?e.keyCode:e.which;
	            if(keycode==13){
	            	if($scope.tname == null || $scope.tname.length == 0)
	            		alert("请先输入Topic名称");  //query with mid but without topic name
	            	else{
						var sort = false;
						if(typeof($scope.searchPaginator) != "undefined"){
							sort = $scope.searchPaginator.reverse;
						}
	            		$scope.searchPaginator = Paginator(fetchFunction, $scope.recordofperpage, $scope.tname , $scope.messageId ,$scope.startdt,  $scope.stopdt, sort);
	            		if(typeof($scope.searchPaginator) != "undefined"){
	            			$scope.searchPaginator.reverse = sort;	            		
	            		}
	            	}
	            }
	        };
	        
	        //invoke when topic name is not empty and recordofperpage is changed
	        $scope.makeChanged = function(){
	        	if($scope.tname.length != 0){
					var sort;
					if(typeof($scope.searchPaginator) != "undefined"){
						sort = $scope.searchPaginator.reverse;
					}
	        		$scope.searchPaginator = Paginator(fetchFunction, $scope.recordofperpage, $scope.tname , $scope.messageId ,$scope.startdt,  $scope.stopdt, "", sort);
            		if(typeof($scope.searchPaginator) != "undefined"){
            			$scope.searchPaginator.reverse = sort;	            		
            		}
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
	        $scope.mintime = "";
	        $scope.maxtime = "";
	        $scope.fulltopicname = "";
			$http({
				method : 'GET',
				url : window.contextPath + '/console/topic/namelist'
			}).success(function(data, status, headers, config) {
				var topicNameList = data;
				$("#topicname").typeahead({
					items: 16, 
					source : topicNameList,
					updater : function(c) {
						if($scope.fulltopicname != c){
							$http.get(window.contextPath + "/console/message/timespan", {
								params : {
									topic: c
								}
							}).success(function(data){
								$scope.mintime = data.min;
								$scope.maxtime = data.max;
								$scope.tname = c;
								var sort = false;
								if(typeof($scope.searchPaginator) != "undefined"){
									sort = $scope.searchPaginator.reverse;
								}
								$scope.searchPaginator = Paginator(fetchFunction, $scope.recordofperpage, $scope.tname , $scope.messageId, $scope.startdt, $scope.stopdt, sort);
			            		if(typeof($scope.searchPaginator) != "undefined"){
			            			$scope.searchPaginator.reverse = sort;	            		
			            		}
							});
						}
						else{
							var sort;
							if(typeof($scope.searchPaginator) != "undefined"){
								sort = $scope.searchPaginator.reverse;
							}
							$scope.tname = c;
							$scope.searchPaginator = Paginator(fetchFunction, $scope.recordofperpage, $scope.tname , $scope.messageId, $scope.startdt, $scope.stopdt, sort);
		            		if(typeof($scope.searchPaginator) != "undefined"){
		            			$scope.searchPaginator.reverse = sort;	            		
		            		}
						}
						$scope.fulltopicname = c;
						return c;
					}
				})
			}).error(function(data, status, headers, config) {
			});
			
	        //for deal with re transmit for selected messages
			$scope.dialog = function() {
				if($(".swallowcheckbox:checked").length <1){
					alert("请先勾选需要发送的消息！");
					return;
				}
				else{
					ngDialog.open({
								template : '\
								<div class="widget-box">\
								<div class="widget-header">\
									<h4 class="widget-title">警告</h4>\
								</div>\
								<div class="widget-body">\
									<div class="widget-main">\
										<p class="alert alert-info">\
											您确认要重新发送所选的消息吗？\
										</p>\
									</div>\
									<div class="modal-footer">\
										<button type="button" class="btn btn-default" ng-click="closeThisDialog()">取消</button>\
										<button type="button" class="btn btn-primary" ng-click="retransmit()&&closeThisDialog()">确定</button>\
									</div>\
								</div>\
							</div>',
							plain : true,
							className : 'ngdialog-theme-default'
					});
				}
			};
			
			$scope.selectall = function(){
				$(".swallowcheckbox").prop('checked', $("#selectall").prop('checked'));
				$("#selectnone").prop('checked', false);
			};
			
			$scope.selectnone = function(){
				$(".swallowcheckbox").prop('checked', false);
				$("#selectall").prop('checked', false);
			};
			
			$rootScope.retransmit = function(){
	        	var needtotrans = "";
	        	var scb = $(".swallowcheckbox");
	        	for(var i=0;i < scb.length;i++){
	        		if(scb[i].checked){
	        			if(needtotrans.length == 0)
	        				needtotrans += $scope.searchPaginator.currentPageItems[i].mid;
	        			else{
		        			needtotrans = needtotrans + "," + $scope.searchPaginator.currentPageItems[i].mid;
	        			}
	        		}
	        	}
	        	
	        	$scope.starttransmit(needtotrans);
	        	return true;
	        }
	        
	        $scope.starttransmit = function(data){
        			$http.post(window.contextPath + '/api/message/sendmessageid', {"mid": data,"topic":$scope.tname}).success(function(response) {
        			  $("#selectnone").prop('checked', false);
        			  $("#selectall").prop('checked', false);
        			  $(".swallowcheckbox").prop('checked', false);
        			  $scope.messageId = "";
        			  $scope.startdt = "";
        			  $scope.stopdt = "";
        			  var sort;
					  if(typeof($scope.searchPaginator) != "undefined"){
						  sort = $scope.searchPaginator.reverse;
					  }
        			  $scope.searchPaginator = Paginator(fetchFunction, $scope.recordofperpage, $scope.tname , $scope.messageId, $scope.startdt, $scope.stopdt, sort);
	            	  if(typeof($scope.searchPaginator) != "undefined"){
	            		  $scope.searchPaginator.reverse = sort;	            		
	            	  }
	        });
	        
	        }
	        // for retransmit self defined messages
			$scope.delimitor = ':';
			$scope.dearray = [',','_','#',';',':'];
			$('#delimitor').tooltip({
				showDelay: 0,
				hideDelay: 0
			});
			$('#dump').tooltip({
				showDelay: 0,
				hideDelay: 0
			});

			
			$scope.setdelimitor = function(index){
				$('#delimitor').val($scope.dearray[index]);
				$scope.delimitor = $scope.dearray[index];
			}
			
	        $scope.textarea = "";
	        $scope.ttype = "";
	        $scope.tproperty = "";
	        $scope.refreshpage = function(myForm){
	        	if($scope.dearray.indexOf($('#delimitor').val()) < 0){
	        		alert("不合法的分隔符!");
	        		return;
	        	}
	        	var property = "";
	        	var hasproperty = false;
	        	var propertyinput = $(".property-input");
	        	for(var i = 0 ; i < propertyinput.length; i++){
	        		var p = propertyinput.eq(i).val(); //or propertyinput[i].value
	        		p = p.replace(/ /g, '');
	        		if(p.length > 2){ //至少3个字符
	        			var re = new RegExp($scope.delimitor, 'g');
	        			var count = (p.match(re) || []).length
	        			if(count != 1){
	        				if(count == 0){
	        					alert("请使用选择的分隔符"+$scope.delimitor+"分隔键值对!");
	        					return;
	        				}
	        				else{
	        					alert("键值对中不要出现" + $scope.delimitor + ",您可以切换为其他分隔符");
	        					return;
	        				}
	        			}
	        			else{
	        				hasproperty = true;
	        				property = property + p + $scope.delimitor + $scope.delimitor;
	        			}
	        		}else if(p.length != 0){
	        			alert("请准确输入键值对");
	        			return;
	        		}
	        	}
	        	$('#myModal').modal('hide');
	        	if(hasproperty){
	        		$scope.tproperty = property.substring(0,property.length-2);
	        	}
	        	$http.post(window.contextPath + '/console/message/auth/sendmessage', {"content":$scope.textarea,"topic":$scope.tname,"type":$scope.ttype,"delimitor":$scope.delimitor,"property":$scope.tproperty}).success(function(response) {
					$scope.textarea = "";
					$scope.tproperty = "";
					
	        		$scope.startdt = "";
					$scope.stopdt = "";
					$scope.messageId = "";
      			    var sort;
				    if(typeof($scope.searchPaginator) != "undefined"){
					    sort = $scope.searchPaginator.reverse;
				    }
					$scope.searchPaginator = Paginator(fetchFunction, $scope.recordofperpage, $scope.tname , $scope.messageId, $scope.startdt, $scope.stopdt, sort);
	            	if(typeof($scope.searchPaginator) != "undefined"){
	            		$scope.searchPaginator.reverse = sort;	            		
	            	}
	        	});
	        }
	        
	        $scope.max_fields = 100;
	        $scope.fields     = 1;
	        $scope.addfield = function(){
	        	if($scope.fields < $scope.max_fields){ //max input box allowed
	        		$scope.fields++; //text box increment
                    $(".input_fields_wrap").append('<div><input type="text" name="property" class="input-xlarge property-input"/><a href="#" class="remove_field" ng-click="removefield();$event.preventDefault()"><i class="icon-minus"></i> Remove</a></div>'); //add input box
                }
	        }
	        $(".input_fields_wrap").on("click",".remove_field", function(e){ //user click on remove text
                e.preventDefault(); $(this).parent('div').remove(); $scope.fields--;
            })

	        
			//judge if redirected from topic view
			var tmpname = localStorage.getItem("name");
			if(tmpname != null){
				$scope.tname = localStorage.getItem("name");
				localStorage.clear();
				$http.get(window.contextPath + "/console/message/timespan", {
					params : {
						topic: $scope.tname
					}
				}).success(function(data){
					$scope.mintime = data.min;
					$scope.maxtime = data.max;
					if(data.min.length > 0){  //没有纪录就不用查询了
						$scope.searchPaginator = Paginator(fetchFunction, $scope.recordofperpage, $scope.tname , $scope.messageId, $scope.startdt, $scope.stopdt, false);
					}
				});
			}
			
			//reverse record
			$scope.reverse = function(){
  			    var sort = false;
			    if(typeof($scope.searchPaginator) != "undefined"){
				    sort = !$scope.searchPaginator.reverse;
			    }
				$scope.searchPaginator = Paginator(fetchFunction, $scope.recordofperpage, $scope.tname , $scope.messageId, $scope.startdt, $scope.stopdt, sort);
				if(typeof($scope.searchPaginator) != "undefined"){
					$scope.searchPaginator.reverse = sort;
				}
			}
	        
}]);
		
		function updateProgressBar (){
			  var progressBar = $(".progress-bar-success");
			  var currentPer = parseInt(progressBar.attr("aria-valuenow"));
			  var updatedPer;
			  if(currentPer > 95){
			    updatedPer = 95;
			  } else {
			    updatedPer = currentPer + 2;
			  }
			  progressBar.attr("aria-valuenow", updatedPer.toString());
			  progressBar.css("width",updatedPer.toString()+"%");
		}

