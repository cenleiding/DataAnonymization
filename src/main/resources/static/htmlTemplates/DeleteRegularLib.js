var app = angular.module("deleteRegularLibApp", []);
app.controller("deleteRegularLibCtrl", function($scope,$http,dep) {

    $scope.libName=dep


    $scope.delete=function(){

        $http(
            {
                url:"/MyReAndDic/deleteRegularLib",
                method:"GET",
                params:{libName:$scope.libName}
            }
        ).then(
            function success(response) {
                $scope.closeThisDialog()
            },
            function error() {
                alert("删除失败！")
                $scope.closeThisDialog()
            }
        )

    }
})