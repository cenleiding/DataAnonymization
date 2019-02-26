var app = angular.module("createNewRegularApp", []);
app.controller("createNewRegularCtrl", function($scope,$http,dep) {

    var libName = dep;
    $scope.area = "";
    $scope.aims = "";

    $scope.create=function () {
        if(($scope.area==="")||($scope.aims===""))alert("规则不能为空！")
        else {
            $http({
                url:"/MyReAndDic/createNewRegular",
                method:"GET",
                params:{
                    libName:libName,
                    area:$scope.area,
                    aims:$scope.aims
                }
            }).then(
                function success(response) {
                    alert("添加成功！")
                    $scope.closeThisDialog();
                },
                function error(response) {
                    alert("添加失败！")
                }
            )
        }
    }


})