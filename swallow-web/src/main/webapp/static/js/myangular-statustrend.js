module.controller('StatusTrendController', function($scope, DataService, $http) {
	$scope.poolList = [];
	$scope.currentPool = 'all';
	
	$scope.getPoolList = function() {
		$http({
			method : 'GET',
			url : window.contextpath + '/monitor/status/data/poollist/get'
		}).success(function(data, status, headers, config) {
			if(data.errorCode == 0 ){
				$scope.poolList = data.poolList;
			} else {
				alert("获取集群列表失败: " + data.errorMessage);
			}
		}).error(function(data, status, headers, config) {
			alert("响应集群列表错误", data);
		});
	}
	
	$scope.changePool = function(){
		window.location = window.contextpath + '/monitor/status/data/' + $scope.currentPool;
	}
	
	$scope.generateChart = function(poolName) {
		$http({
			method : 'GET',
			url : window.contextpath + '/monitor/' + poolName + '/data/get'
		}).success(function(data, status, headers, config) {
			chart = data.chart;
			if(data.errorCode == 0 ){
				$(function () {
			        $('#container').highcharts({
			            title: {
			                text: chart.title,
			                x: 0 //center
			            },
			            subtitle: {
			                text: chart.subTitle,
			                x: -20
			            },
			            xAxis: {
			                type: 'datetime'
			            },
			            yAxis: {
			                title: {
			                    text: 'status count'
			                },
			                plotLines: [{
			                    value: 0,
			                    width: 10,
			                    color: '#808080'
			                }]
			            },
			            tooltip: {
			                valueSuffix: ''
			            },
			            legend: {
			                layout: 'vertical',
			                align: 'right',
			                verticalAlign: 'middle',
			                borderWidth: 0
			            },
			            plotOptions: {
			                series: {
			                    pointStart: chart.plotOption.series.pointStart + 8*3600*1000,
			                    pointInterval: chart.plotOption.series.pointInterval // one day
			                }
			            },
			            series: chart.series
			        });
			    });
			} else {
				alert("获取失败: " + data.errorMessage);
			}
		}).error(function(data, status, headers, config) {
			alert("响应错误", data);
		});
	}
	// search
	$scope.pools = DataService.getPools(function() {
		var poolNameList = [];
		$.each($scope.pools, function(i, pool) {
			poolNameList.push(pool.name);
		});
		$("#pool-search-nav").typeahead(
				{
					source : poolNameList,
					updater : function(c) {
						window.location = window.contextpath
								+ '/monitor/status/data/' + c;
						return c;
					}
				})
	});
	
	$scope.initMethod =  function() {
	    angular.element(document).ready(function () {
	    	$scope.getPoolList();
	    	$scope.generateChart($scope.currentPool);
	    });
	};
	$scope.initMethod();
});