var app = angular.module("userRegularLibApp", []);
app.controller("userRegularLibCtrl", function($scope,$http,$rootScope) {

    $scope.regularLibList=[];
    $scope.regularLib = {};


    //获得规则库信息
    (function () {
        $http(
            {
                url:"/MyReAndDic/getRegularLibName",
                method:"GET"
            }
        ).then(
            function success(response) {
                console.log(response.data);
                $scope.regularLibList=response.data;
            },
            function error() {
                alert("获取列表失败！")
            }
        )

    })()


    $scope.editForm=function (libName) {
        for (var i in $scope.regularLibList){
            var re = $scope.regularLibList[i]
            if (re.libName===libName) {
                $scope.regularLib.libName =re.libName;
                $scope.regularLib.changeTime = re.changeTime;
                $scope.regularLib.createTime = re.createTime;
                $scope.regularLib.user = re.user;
                $scope.regularLib.description = re.description;
                $scope.regularLib.new_libName = $scope.regularLib.libName;
                $scope.regularLib.new_description = $scope.regularLib.description;
            }
        }

    }


})