var app = angular.module("NodeFormShowApp", ['angular-popups','headApp']);
app.controller("NodeFormShowCtrl", function($scope,$http,$timeout,$q) {

    $scope.fileList=[];
    $scope.fromList=[];

    $scope.data=[
        {
            "expandName":"--",
            "fromName":"--",
            "en_name":"--",
            "ch_name":"--",
            "description":"--",
            "nodeType": "--",
            "id":"--"
        }];

    //获取数据
    (function () {
        $http({
            method:'GET',
            url:"/NodeFormShow/getExpandFileName",
        }).then(function successCallback(response) {
            var List=response.data;
            var select = $("#Select");
            select.append("<option value='un' disabled='disabled'>文件选择</option>");
            select.append("<option disabled='disabled'  data-icon='glyphicon glyphicon-user' style='background: #5cb85c; color: #fff;'>OpenEhr</option>");
            select.append("<option value='openEhr' data-icon='glyphicon glyphicon-file' >OpenEhr原型节点表</option>");
            select.append("<option disabled='disabled'  data-icon='glyphicon glyphicon-user' style='background: #428bca; color: #fff;'>拓展表单</option>");
            for (var i = 0; i < List.length; i++) {
                select.append("<option value='"+List[i]+"' data-icon='glyphicon glyphicon-file'>"
                    + List[i] + "</option>");
            }
            $scope.fileList=[].concat(["un","un","openEhr","un"]);
            $scope.fileList=$scope.fileList.concat(List);
            $('#Select').selectpicker('val','un');
            $('#Select').selectpicker('refresh');
        }, function errorCallback(response) {
            // 请求失败执行代码
        });
    })();

    //按钮点击事件
    $('#Select').on('changed.bs.select', function (e,c) {
        $scope.selectFile=$scope.fileList[c];
        $scope.selectFrom="";
        $scope.getFromList();
    });

    //获取对应文件表单信息
    $scope.getFromList=function () {
        if($scope.selectFile==="openEhr"){
            $http({
                method:'GET',
                url:"/NodeFormShow/GetArchetypeName",
            }).then(function successCallback(response) {
                $scope.fromList=response.data;
            }, function errorCallback(response) {});
        }else {
            $http({
                method:'GET',
                url:"/NodeFormShow/getExpandFromNameByFileName",
                params:{fileName:$scope.selectFile}
            }).then(function successCallback(response) {
                $scope.fromList=response.data;
            }, function errorCallback(response) {});
        }

    }

    //处理模板双击选择事件
    $scope.selectTemp=function(C){
        $scope.selectFrom=C;
        if($scope.selectFile==="openEhr"){
            $http({
                method:'GET',
                url:"/NodeFormShow/GetArchetypeNodeInfoByName",
                params:{archetypeName:C}
            }).then(function successCallback(response) {
                $scope.data=response.data;
                datatableDraw();
            }, function errorCallback(response) {
                // 请求失败执行代码
            });
        }else {
            $http({
                method:'GET',
                url:"/NodeFormShow/getExpandNodeInfoByName",
                params:{fileName:$scope.selectFile,
                    fromName:$scope.selectFrom}
            }).then(function successCallback(response) {
                $scope.data=response.data;
                datatableDraw();
            }, function errorCallback(response) {
                // 请求失败执行代码
            });
        }
    }

    //arcdataTable
    var datatableDraw=function(){
        if($scope.selectFile==="openEhr"){
            dataTable=$('#dataTable').dataTable( {
                "data": $scope.data,
                "autoWidth": true,
                "destroy": true,
                "bLengthChange":false,
                "iDisplayLength":10,
                "pageLength":15,
                "columns": [
                    { data: "nodeName" ,"title":"原型节点名："},
                    { data: "nodePath" ,"title":"原型节点路径："},
                    { data: "description" ,"title":"原型节点含义："},
                    { data: "nodeType" ,"title":"隐私等级："}
                ]
            });
        }else{
            dataTable=$('#dataTable').dataTable( {
                "data": $scope.data,
                "autoWidth": true,
                "destroy": true,
                "bLengthChange":false,
                "iDisplayLength":10,
                "pageLength":15,
                "columns": [
                    { data: "en_name" ,"title":"节点英文名："},
                    { data: "ch_name" ,"title":"节点中文名："},
                    { data: "description" ,"title":"节点含义："},
                    { data: "nodeType" ,"title":"隐私等级："}
                ]
            });
        }
    }
    datatableDraw();
})