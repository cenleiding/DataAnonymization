var app = angular.module("registeredApp", []);
app.controller("registeredCtrl", function($scope,$http) {

        $scope.user="";
        $scope.password="";
        $scope.flag1=0;
        $scope.flag2=0;
        $scope.flag3=0;
        $scope.register=function () {
            $scope.flag1=0;
            $scope.flag2=0;
            $scope.flag3=0;
            if($scope.user.replace(" ")=="") flag1=1;
            if($scope.password.replace(" ")=="") flag2=1;
            if($scope.flag1+$scope.flag2==0) {
                $http({
                    method:'GET',
                    url:'/register',
                    params:{"username":$scope.user,"password": $scope.password}
                }).then(
                    function successCallback(response) {
                        if(response.data["flag"]==1) $scope.login();
                        else  $scope.flag3=1;
                    },
                    function errorCallback(response) {
                        $scope.flag3=2;
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
                    window.location.href='/FieldModify';
                }
            )
        }
    }
)