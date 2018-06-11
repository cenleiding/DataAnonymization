var app = angular.module("DbDeidentifyApp", []);
app.controller("DbDeidentifyCtrl", function($scope,$http,$timeout,$sce) {

    $scope.selectDbType="MySql";
    $scope.host="";
    $scope.port="";
    $scope.databaseName="";
    $scope.user="";
    $scope.password="";
    $scope.portCase="3306";
    $scope.databaseNameCase="数据库名:";
    $scope.userCase="root";
    $scope.selectMethod="SafeHarbor";
    $scope.selectFromName="Original";
    $scope.pollflag=true;
    $scope.DbInfo=["匿名化未启动..."];
    $scope.wuliaoLog=". . . /";


    (function (){
        $http({
            method:'GET',
            url:"/getFromNameList"
        }).then(
            function successCallback (response) {
                var fromName=response.data;
                var select = $("#fromName");
                for (var i = 0; i < fromName.length; i++) {
                    select.append("<option value='"+fromName[i]+"'>"
                        + fromName[i] + "</option>");
                }
                $('#fromName').selectpicker('val','Original');
                $('.selectpicker').selectpicker('refresh');
            },
            function errorCallback (response) {
                console.log("获取字段表单失败！");
            }
        );
    })();

    $("#fromName").on('changed.bs.select', function (e,c) {
        $scope.selectFromName=e.target.value;
    });

    $("#DbType").change(function(evt){
        if(evt.currentTarget.value==="MySql")     {
            $scope.portCase="3306";
            $scope.databaseNameCase="数据库名:";
            $scope.userCase="root";
            $scope.host="";
            $scope.port="";
            $scope.databaseName="";
            $scope.user="";
            $scope.password="";
        };
        if(evt.currentTarget.value==="SqlServer") {
            $scope.portCase="1433";
            $scope.databaseNameCase="数据库名:";
            $scope.userCase="sa";
            $scope.host="";
            $scope.port="";
            $scope.databaseName="";
            $scope.user="";
            $scope.password="";
        }
        if(evt.currentTarget.value==="Oracle") {
            $scope.portCase="1521";
            $scope.databaseNameCase="服务名:";
            $scope.userCase="";
            $scope.host="";
            $scope.port="";
            $scope.databaseName="";
            $scope.user="";
            $scope.password="";
        }
    });

    $scope.testConnection=function(){
        if(($scope.host==="")||($scope.port==="")||($scope.databaseName==="")||($scope.user==="")||($scope.password===""))
            alert("请填写完整数据库连接信息！");
        else{
            $http({
                method:"GET",
                url:"/DbTestConnection",
                params:{
                    dbType:$scope.selectDbType,
                    host:$scope.host,
                    port:$scope.port,
                    databaseName:$scope.databaseName,
                    user:$scope.user,
                    password:$scope.password,
                }
            }).then(
                function success(response) {
                    alert(response.data);
                },
                function errorCallBack(response){
                    alert(response.data);
                }
            );
        }
    }

    $scope.DbDeidentify=function () {
        if(($scope.host==="")||($scope.port==="")||($scope.databaseName==="")||($scope.user==="")||($scope.password===""))
            alert("请填写完整数据库连接信息！");
        else{
            $scope.DbInfo="";
            $http({
                method:"GET",
                url:"/runDeidentify",
                params:{
                    dbType:$scope.selectDbType,
                    host:$scope.host,
                    port:$scope.port,
                    databaseName:$scope.databaseName,
                    user:$scope.user,
                    password:$scope.password,
                    method:$scope.selectMethod,
                    fieldFromName:$scope.selectFromName
                }
            }).then(
                function success(response) {
                    $scope.pollflag=false;
                    alert(response.data);
                },
                function errorCallBack(response){
                    $scope.pollflag=false;
                    alert(response.data);
                }
            );
            $scope.pollDbInfo();
            $scope.wuliao();
        }
    };

    $scope.wuliao=function () {
        $timeout(
            function () {
                if($scope.wuliaoLog===". . . /")
                    $scope.wuliaoLog=". . . \\";
                else
                    $scope.wuliaoLog=". . . /";
                if($scope.pollflag==true){
                    $scope.wuliao();
                }
            }
            ,200)
    };
    
    $scope.pollDbInfo=function () {
            $timeout(
                $http({
                    method:"GET",
                    url:"/getInfo",
                    params:{
                        dbType:$scope.selectDbType,
                        host:$scope.host,
                        port:$scope.port,
                        databaseName:$scope.databaseName
                    }
                }).then(
                    function success(response) {
                        console.log(response.data)
                        if(response.data.length!=0)
                        $scope.DbInfo=response.data;
                        document.getElementById('msg_end').scrollIntoView(false);
                        document.getElementById('msg_end').scrollTop;
                        if($scope.pollflag==true){
                            $scope.pollDbInfo();
                        }

                    },
                    function errorCallBack(response){
                        console.log(response.data);
                    }
                )
                ,1000);
    };

    $scope.getContentHtml=function(content)
    {
        return $sce.trustAsHtml(content);
    }


});