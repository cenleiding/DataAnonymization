var app = angular.module("userConfigApp", ['navigationBarApp','userFieldFormApp','userRegularLibApp']);
app.controller("userConfigCtrl", function($scope,$http,$rootScope) {

    $rootScope.sidebarPage=4;
    $scope.page=0;

})