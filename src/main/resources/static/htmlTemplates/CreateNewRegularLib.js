var app = angular.module("createNewRegularLibApp", []);
app.controller("createNewRegularLibCtrl", function($scope,$http) {

    $scope.libName = "";
    $scope.description = "";

    $scope.create=function () {
        $http({
            url:"/MyReAndDic/createNewRegularLib",
            method:"GET",
            params:{
                libName:$scope.libName,
                description:$scope.description,
            }
        }).then(
            function success(response) {
                alert(response.data)
                if(response.data[0]==="创建成功！")
                    $scope.closeThisDialog();
            },
            function error(response) {
                alert("添加失败！")
            }
        )
    }

})