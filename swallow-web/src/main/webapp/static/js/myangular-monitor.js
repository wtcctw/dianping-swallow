
function renderGraph(url, divName,  http){
		http({
			method : 'POST',
			url : window.contextpath + url
		}).success(function(data, status, headers, config) {
			
				var parent = $('#' + divName);
				var count = 0;
				data.forEach(function(item){
					count++;
					var childdiv=$('<div></div>'); 
					childdiv.appendTo(parent);
					$(function () {
						childdiv.highcharts({
				            title: {
				                text: item.title,
				                x: 0 //center
				            },
				            subtitle: {
				                text: item.subTitle,
				                x: 0
				            },
				            xAxis: {
				                type: 'datetime'
				            },
				            yAxis: {
				                title: {
				                    text: item.yAxisTitle
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
			
			alert("响应错误" + data);
//			app.appError("响应错误", data);
		});	
}

module.controller('ProducerServerQpsController', function($scope, $http) {

	$scope.getProducerServerQps = function(){
		renderGraph("/console/monitor/producerserver/qps/get", "container", $http);
	};

});

module.controller('ConsumerServerQpsController', function($scope, $http) {
	
	$scope.getConsumerServerQps = function(){
		renderGraph("/console/monitor/consumerserver/qps/get", "container", $http);
	};
});

module.controller('ConsumerQpsController', function($scope, $http) {
	$http({
		method : 'POST',
		url : window.contextpath + '/console/monitor/topiclist/get'
	}).success(function(topicList, status, headers, config) {
		
		$("#consumer-div").typeahead({
			source : topicList,
			updater : function(c) {
				window.location = window.contextpath + "/console/monitor/consumer/"+c+"/qps";
				return c;
			}
		})
	}).error(function(data, status, headers, config) {
		 app.appError("响应错误", data);
	});

	
	$scope.getConsumerQps = function(topicName){
		renderGraph("/console/monitor/consumer/"+topicName+"/qps/get", "container", $http);
	};
});

module.controller('ConsumerDelayController', function($scope, $http) {

	// search
	$http({
		method : 'POST',
		url : window.contextpath + '/console/monitor/topiclist/get'
	}).success(function(topicList, status, headers, config) {
		
		$("#consumer-div").typeahead({
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
		renderGraph('/console/monitor/consumer/' + topicName + '/delay/get', "container", $http);
	};
});
