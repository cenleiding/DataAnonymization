var app = angular.module("saveChangeApp", []);
app.controller("saveChangeCtrl", function($scope,$http,dep) {

$scope.newFormDetail=dep;
$scope.logDescription=""

    $scope.delete=function(){

        $http(
            {
                url:"/MyFieldForm/updateFieldForm",
                method:"POST",
                params:{
                    newFormName:$scope.newFormDetail.newFormName,
                    oldFormName:$scope.newFormDetail.oldFormName,
                    newDescription:$scope.newFormDetail.newDescription,
                    logDescription:$scope.logDescription
                },
                data:$scope.newFormDetail.field
            }
        ).then(
            function success(response) {
                if(response.data==="更新成功！")
                    $scope.closeThisDialog();
                else
                    alert(response.data)
            },
            function error() {
                alert("删除失败！")
            }
        )

    }
})