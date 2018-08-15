var app = angular.module("fieldFormShowApp", ['angular-popups','headApp']);
app.controller("fieldFormShowCtrl", function($scope,$http,$timeout,$q) {

    $scope.formMap;
        (function () {
            $http(
                {
                    method:"GET",
                    url:"/getFromNameMap"
                }
            ).then(
                function successCallback(response){
                    $scope.formMap=response.data;
                    var select = $("#Select");
                    for(var i=0;i<$scope.formMap.length;i++){
                        var userName=$scope.formMap[i].username;
                        if (userName==null) userName="系统样本"
                        select.append("<option value='un' disabled='disabled'  data-icon='glyphicon glyphicon-user' style='background: #5cb85c; color: #fff;'>"+userName+"</option>");
                        for(var j in $scope.formMap[i].formNameAndDes){
                            select.append("<option data-icon='glyphicon glyphicon-file' data-subtext=("+$scope.formMap[i].formNameAndDes[j]+")>"
                                +j+ "</option>");
                        }
                    }
                    $('.selectpicker').selectpicker('val','un');
                    $('.selectpicker').selectpicker('refresh');
                },
                function errorCallback(response){
                    alert("获取列表失败！")
                }
            )
        })()
}
)