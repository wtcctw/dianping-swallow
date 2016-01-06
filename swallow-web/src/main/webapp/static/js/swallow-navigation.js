/**
 * Created by mingdongli on 16/1/5.
 */

module.controller('NavigationController', function ($scope, $rootScope, $http) {

    $scope.newWindow = function(){
        var navigation = sessionStorage.getItem("navigationTopic");
        if(navigation != null && navigation.length > 0){
            window.location = $scope.getUrl(navigation);
        }else{
            window.location = $scope.getUrl("total");
        }
    }

    $scope.getUrl = function (topic) {
        return window.contextPath + "/console/monitor/consumer/" + topic + "/delay"
    }
});