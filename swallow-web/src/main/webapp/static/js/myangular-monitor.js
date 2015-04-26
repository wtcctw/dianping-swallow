module.controller('ConsumerDelayController', function($scope, $http) {

	$scope.getDelay = function(topicName) {
		$http({
			method : 'POST',
//			url : window.contextpath + '/console/monitor/producer/' + topicName + '/savedelay/get'
			url : window.contextpath + '/console/monitor/consumer/' + topicName + '/delay/get'
		}).success(function(data, status, headers, config) {
			
				item = data[0];
				$(function () {
			        $('#container').highcharts({
			            title: {
			                text: item.title,
			                x: 0 //center
			            },
			            subtitle: {
			                text: item.subTitle,
			                x: -20
			            },
			            xAxis: {
			                type: 'datetime'
			            },
			            yAxis: {
			                title: {
			                    text: 'QPS'
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
			                    pointStart: item.plotOption.series.pointStart + 8*3600*1000,
			                    pointInterval: item.plotOption.series.pointInterval // one day
			                }
			            },
			            series: item.series
			        });
			    });
		}).error(function(data, status, headers, config) {
			
			alert("响应错误", data);
//			app.appError("响应错误", data);
		});
	}
});
