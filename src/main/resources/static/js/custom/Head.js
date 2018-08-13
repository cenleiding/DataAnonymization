var app = angular.module("headApp", []);
app.controller("headCtrl", function($scope,$http,$timeout) {

    $scope.flag="0";
    $scope.user="未登入";
    (function () {
        $http({
            method:'GET',
            url:"/getUser",
        }).then(function successCallback(response) {
            $scope.user=response.data["user"];
            $scope.flag=response.data["flag"];
        }, function errorCallback(response) {
            // 请求失败执行代码
        });
    })();


    }
)