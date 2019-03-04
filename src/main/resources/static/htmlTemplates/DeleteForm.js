var app = angular.module("deleteFormApp", []);
app.controller("deleteFormCtrl", function($scope,$http,dep) {

    $scope.formName=dep


    $scope.delete=function(){

        $http(
            {
                url:"/MyFieldForm/deleteFieldFormByFormName",
                method:"GET",
                params:{formName:$scope.formName}
            }
        ).then(
            function success(response) {
                $scope.closeThisDialog()
            },
            function error() {
                alert("删除失败！")
            }
        )

    }
})