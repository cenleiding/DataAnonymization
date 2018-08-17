var app = angular.module("myFieldFormApp", ['headApp']);
app.controller("myFieldFormCtrl", function($scope,$http,$timeout,$q) {

    $scope.formNameList=[];

    (function () {
        $http(
            {
                url:"/getFieldFormName",
                method:"GET"
            }
        ).then(
            function success(response) {
                $scope.formNameList=response.data;
            },
            function error() {
                alert("获取列表失败！")
            }
        )

    })()

    $scope.editForm=function (formName) {
        alert(formName);
    }

}
)