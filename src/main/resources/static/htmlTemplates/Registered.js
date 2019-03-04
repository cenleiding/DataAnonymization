var app = angular.module("registeredApp", []);
app.controller("registeredCtrl", function($scope,$http) {

        $scope.user="";
        $scope.password="";
        $scope.flag1=0;
        $scope.flag2=0;
        $scope.flag3=0;

        $scope.register=function () {
            if($scope.user.replace(" ")==="") alert("用户名不能为空！");
            if($scope.password.replace(" ")==="") alert("密码不能为空！");
            if((!$scope.user.replace(" ")=="")&&(!$scope.password.replace(" ")==="")) {
                $http({
                    method:'GET',
                    url:'/register',
                    params:{"username":$scope.user,"password": $scope.password}
                }).then(
                    function successCallback(response) {
                        if(response.data["flag"]==1) {
                            alert("注册成功！")
                            $scope.login();
                        }else {
                            alert("用户名重复！")
                        }
                    },
                    function errorCallback(response) {
                        alert("注册失败！")
                    }
                );
            }
        }

        $scope.login=function(){
            $http(
                {
                    method:'POST',
                    url:'/login',
                    data:$.param({"username":$scope.user,"password": $scope.password}),
                    headers : { 'Content-Type': 'application/x-www-form-urlencoded' }
                }
            ).then(
                function success(response) {
                    window.location.href='/UserConfig';
                }
            )
        }
    }
)