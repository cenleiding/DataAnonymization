var app = angular.module("changeRegularApp", []);
app.controller("changeRegularCtrl", function($scope,$http,dep) {

    $scope.regular = dep;

    $scope.saveChange=function () {
        if(($scope.regular.area==="")||($scope.regular.aims===""))alert("规则不能为空！")
        else {
            $http({
                url:"/MyReAndDic/changeRegular",
                method:"POST",
                data:$scope.regular
            }).then(
                function success(response) {
                    alert("修改成功！")
                    $scope.closeThisDialog();
                },
                function error(response) {
                    alert("修改失败！")
                }
            )
        }
    }


})