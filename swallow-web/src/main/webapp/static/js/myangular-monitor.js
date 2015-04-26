module.controller('ConsumerDelayController', function($scope, $http) {

	// search
	$http({
		method : 'POST',
		url : window.contextpath + '/console/monitor/topiclist/get'
	}).success(function(topicList, status, headers, config) {
		
		$("#consumer-delay-div").typeahead({
			source : topicList,
			updater : function(c) {
				window.location = window.contextpath + "/console/monitor/consumer/"+c+"/delay";
				return c;
			}
		})
	}).error(function(data, status, headers, config) {
		 app.appError("响应错误", data);
	});

	$scope.getDelay = function(topicName) {
		$http({
			method : 'POST',
			url : window.contextpath + '/console/monitor/consumer/' + topicName + '/delay/get'
		}).success(function(data, status, headers, config) {
			
				var parent = $('#container');
				var count = 0;
				data.forEach(function(item){
					count++;
					var childdiv=$('<div></div>'); 
					childdiv.appendTo(parent);
					$(function () {
//				        $('#container').highcharts({
						childdiv.highcharts({
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
				});
		}).error(function(data, status, headers, config) {
			
			alert("响应错误", data);
//			app.appError("响应错误", data);
		});
	}
});
