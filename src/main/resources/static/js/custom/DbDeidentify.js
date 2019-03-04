var app = angular.module("DbDeidentifyApp", ['navigationBarApp','ngDialog','anonymizeConfigureApp']);
app.controller("DbDeidentifyCtrl", function($scope,$http,$timeout,$sce,ngDialog,$rootScope) {

    $rootScope.sidebarPage=1;
    $scope.page=0;

    $scope.selectDbType="MySql";
    $scope.host="";
    $scope.port="";
    $scope.databaseName="";
    $scope.user="";
    $scope.password="";
    $scope.portCase="3306";
    $scope.databaseNameCase="数据库名:";
    $scope.userCase="root";
    $scope.pollflag=true;
    $scope.DbInfo=["匿名化未启动..."];
    $scope.wuliaoLog=". . . /";

    $scope.level="Level1";
    $scope.fieldFormInfo=[];
    $scope.regularLibInfo=[];

    var select = $("#fromName");
    var regularLibSelect =$("#regularLibName");


    (function (){
        $http(
            {
                method:"GET",
                url:"/FieldFormShow/getFieldFormInfo"
            }
        ).then(
            function successCallback(response){
                var formMap=response.data;
                var userNameMap={};
                var info;
                for(var i in formMap)
                    userNameMap[formMap[i].userName]="";
                select.append("<option value='un' disabled='disabled'  data-icon='glyphicon glyphicon-user' style='background: #5cb85c; color: #fff;'>系统样本</option>");
                $scope.fieldFormInfo.push("");
                tra_fieldForm("",formMap)
                for(var i in userNameMap){
                    if (i=="") continue;
                    select.append("<option value='un' disabled='disabled'  data-icon='glyphicon glyphicon-user' style='background: #5cb85c; color: #fff;'>"+i+"</option>");
                    $scope.fieldFormInfo.push(i);
                    tra_fieldForm(i,formMap)
                }
                $("#fromName").selectpicker('refresh');
                $("#fromName").selectpicker('val', $scope.fieldFormInfo[1].formName);
                $scope.fieldFormName=$scope.fieldFormInfo[1].formName;
            },
            function errorCallback(response){
                alert("获取列表失败！")
            }
        )

        $http(
            {
                method:"GET",
                url:"/MyReAndDic/getAll"
            }
        ).then(
            function successCallback(response) {
                var libMap=response.data;
                var userNameMap={}
                for(var i in libMap)
                    userNameMap[libMap[i].user]="";
                regularLibSelect.append("<option value='un' disabled='disabled'  data-icon='glyphicon glyphicon-user' style='background: #5cb85c; color: #fff;'>系统规则库</option>");
                $scope.regularLibInfo.push("original");
                tra_regularLib("original",libMap);
                for(var i in userNameMap){
                    if(i=="original") continue;
                    regularLibSelect.append("<option value='un' disabled='disabled'  data-icon='glyphicon glyphicon-user' style='background: #5cb85c; color: #fff;'>"+i+"</option>");
                    $scope.regularLibInfo.push(i);
                    tra_regularLib(i,libMap)
                }
                $("#regularLibName").selectpicker('refresh');
                $("#regularLibName").selectpicker('val', $scope.regularLibInfo[1].libName);
                $scope.regularLibName=$scope.regularLibInfo[1].libName;
            },
            function errorCallback(response) {
                alert("获取规则库失败！")
            }

        )
    })();

    var tra_fieldForm=function (i,formMap) {
        for(var j in formMap){
            if(formMap[j].userName==(i)){
                select.append("<option value='"+formMap[j].formName+"' data-icon='glyphicon glyphicon-file' data-subtext=(使用次数："+formMap[j].usageCount+")>"
                    +formMap[j].formName+ "</option>");
                var info={};
                info['formName']=formMap[j].formName;
                info['description']=formMap[j].description;
                info['createTime']=formMap[j].createTime;
                info['lastChangeTime']=formMap[j].lastChangeTime;
                info['father']=formMap[j].father;
                info['usageCount']=formMap[j].usageCount;
                info['userName']=formMap[j].userName;
                $scope.fieldFormInfo.push(info);
            }
        }
    }

    var tra_regularLib=function (i,libMap) {
        for(var j in libMap){
            if(libMap[j].user == i){
                regularLibSelect.append("<option value='"+libMap[j].libName+"' data-icon='glyphicon glyphicon-book'>"
                    +libMap[j].libName+ "</option>");
            }
            var info={};
            info['user']=libMap[j].userName;
            info['libName']=libMap[j].libName;
            info['description']=libMap[j].description;
            $scope.regularLibInfo.push(info);
        }
    }

    $("#typeLevel").change(function(evt){
        if(evt.currentTarget.value==="Level1") alert("注意：当前选择为研究性数据！处理文件只供内部使用！！");
        $scope.level=evt.currentTarget.value;
        $scope.progress= 0;
    });

    $("#fromName").on('changed.bs.select', function (e,c) {
        $scope.fieldFormName=$scope.fieldFormInfo[c].formName;
    });

    $("#regularLibName").on('changed.bs.select', function (e,c) {
        $scope.regularLibName=$scope.regularLibInfo[c].libName;
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
                url:"/DbDeidentify/DbTestConnection",
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
            $rootScope.config.level=$scope.level;
            $rootScope.config.fieldFormName=$scope.fieldFormName;
            $rootScope.config.regularLib=$scope.regularLibName;
            $http({
                method:"POST",
                url:"/DbDeidentify/runDeidentify",
                params:{
                    dbType:$scope.selectDbType,
                    host:$scope.host,
                    port:$scope.port,
                    databaseName:$scope.databaseName,
                    user:$scope.user,
                    password:$scope.password,
                    method:$scope.selectMethod,
                    fieldFromName:$scope.selectFromName,
                },
                data:$rootScope.config
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
            ,500)
    };

    var timeout;
    $scope.pollDbInfo=function () {
        if(timeout) {
            $timeout.cancel(timeout);
        }
        timeout=$timeout(
            function () {
                $http({
                    method:"GET",
                    url:"/DbDeidentify/getInfo",
                    params:{
                        dbType:$scope.selectDbType,
                        host:$scope.host,
                        port:$scope.port,
                        databaseName:$scope.databaseName
                    }
                }).then(
                    function success(response) {
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
            },2000)
    };

    $scope.getContentHtml=function(content)
    {
        return $sce.trustAsHtml(content);
    }


});