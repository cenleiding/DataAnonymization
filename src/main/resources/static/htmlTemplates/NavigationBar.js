var app = angular.module("navigationBarApp", []);
app.controller("navigationBarCtrl", function($scope,$http,$rootScope) {
    $rootScope.sidebarPage=-1;
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