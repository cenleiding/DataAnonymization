var app = angular.module("sysManageApp", ['angular-popups']);
app.controller("sysManageCtrl", function($scope,$http,$timeout,$q) {

    $scope.backUp=function () {
        $http({
            method:'GET',
            url:"/nodeFileBackUp"
        }).then(
            function successCallback (response) {
                alert("文件备份成功！")
            },
            function errorCallback (response) {
                console.log("文件备份失败！");
            }
        );
    }

    $scope.reset=function () {
        $http({
            method:'GET',
            url:"/nodeReset"
        }).then(
            function successCallback (response) {
                alert(response.data);
            },
            function errorCallback (response) {
                console.log("重置失败！");
            }
        );
    }

});