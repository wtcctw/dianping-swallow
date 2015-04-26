var module = angular.module('SwallowModule', ['ngResource']);

module.config(function($locationProvider, $resourceProvider) {
	// configure html5 to get links working on jsfiddle
	$locationProvider.html5Mode(true);
});

module.filter('strreplace', function() {
    return function(input) {
        return input.replace(/\\/g, "")
    };
});

module.config(function($locationProvider, $resourceProvider) {
	// configure html5 to get links working on jsfiddle
	$locationProvider.html5Mode(true);
});

module.directive('onFinishRenderFilters', function ($timeout) {
    return {
        restrict: 'A',
        link: function(scope, element, attr) {
            if (scope.$last === true) {
                $timeout(function() {
                    scope.$emit('ngRepeatFinished');
                });
            }
        }
    };
});