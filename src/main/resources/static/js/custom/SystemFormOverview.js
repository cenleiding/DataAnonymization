var app = angular.module("SystemFormOverviewApp", ['angular-popups','navigationBarApp']);
app.controller("SystemFormOverviewCtrl", function($scope,$http,$rootScope) {

    $rootScope.sidebarPage=3;
    $scope.page=0;

    // 字段变量
    $scope.fieldFormInfo=[];
    $scope.formNameList=[];
    $scope.dataTableSet_ALL={'ALL':[]};
    $scope.selectType=["ALL","EI","QI_Link","QI_Geography","QI_DateRecord","QI_DateAge","QI_Number","QI_String","SI_Number","SI_String","UI"];

    $scope.description=""
    $scope.createTime=""
    $scope.lastChangeTime=""
    $scope.father=""
    $scope.usageCount=""
    $scope.userName=""

    var percentage={"SUM":0}
    var Accounting=[
        ["EI",0],
        ["QI_Link",0],
        ["QI_Geography",0],
        ["QI_DateRecord",0],
        ["QI_DateAge",0],
        ["QI_Number",0],
        ["QI_String",0],
        ["SI_Number",0],
        ["SI_String",0],
        ["UI",0]
    ];

    // 节点变量
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

    (function () {
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
                $("#FieldFormSelect").append("<option value='un' disabled='disabled'  data-icon='glyphicon glyphicon-user' style='background: #5cb85c; color: #fff;'>系统样本</option>");
                $scope.fieldFormInfo.push("");
                tra("",formMap)
                for(var i in userNameMap){
                    if (i=="") continue;
                    $("#FieldFormSelect").append("<option value='un' disabled='disabled'  data-icon='glyphicon glyphicon-user' style='background: #5cb85c; color: #fff;'>"+i+"</option>");
                    $scope.fieldFormInfo.push(i);
                    tra(i,formMap)
                }
                $("#FieldFormSelect").selectpicker('refresh');
                $("#FieldFormSelect").selectpicker('val', $scope.fieldFormInfo[1].formName);
                refresh(1);
            },
            function errorCallback(response){
                alert("获取列表失败！")
            }
        )

        $http({
            method:'GET',
            url:"/NodeFormShow/getExpandFileName",
        }).then(function successCallback(response) {
            var List=response.data;
            $("#NodeFormSelect").append("<option value='un' disabled='disabled'>文件选择</option>");
            $("#NodeFormSelect").append("<option disabled='disabled'  data-icon='glyphicon glyphicon-user' style='background: #5cb85c; color: #fff;'>OpenEhr</option>");
            $("#NodeFormSelect").append("<option value='openEhr' data-icon='glyphicon glyphicon-file' >OpenEhr原型节点表</option>");
            $("#NodeFormSelect").append("<option disabled='disabled'  data-icon='glyphicon glyphicon-user' style='background: #428bca; color: #fff;'>拓展表单</option>");
            for (var i = 0; i < List.length; i++) {
                $("#NodeFormSelect").append("<option value='"+List[i]+"' data-icon='glyphicon glyphicon-file'>"
                    + List[i] + "</option>");
            }
            $scope.fileList=[].concat(["un","un","openEhr","un"]);
            $scope.fileList=$scope.fileList.concat(List);
            $("#NodeFormSelect").selectpicker('val','un');
            $("#NodeFormSelect").selectpicker('refresh');
        }, function errorCallback(response) {
            // 请求失败执行代码
        });
    })()

    ///////////////// 字段函数

    var tra=function (i,formMap) {
        for(var j in formMap){
            if(formMap[j].userName==(i)){
                $("#FieldFormSelect").append("<option value='"+formMap[j].formName+"' data-icon='glyphicon glyphicon-file' data-subtext=(使用次数："+formMap[j].usageCount+")>"
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

    //用于刷新表单和图
    var refresh=function (c) {
        $scope.description=$scope.fieldFormInfo[c].description
        $scope.createTime=$scope.fieldFormInfo[c].createTime
        $scope.lastChangeTime=$scope.fieldFormInfo[c].lastChangeTime
        $scope.father=$scope.fieldFormInfo[c].father
        $scope.usageCount=$scope.fieldFormInfo[c].usageCount
        $scope.userName=$scope.fieldFormInfo[c].userName
        if($scope.father=="") $scope.father="原生模板"
        if($scope.userName=="") $scope.userName="系统样本"
        $http({
            method:'GET',
            url:"/FieldFormShow/getFieldOverViewByFormName",
            params:{formName: $scope.fieldFormInfo[c].formName}
        }).then(function successCallback(response) {
            percentage=response.data;
            for(var a in Accounting){
                Accounting[a][1]=percentage[Accounting[a][0]];
            }
            createcharts();
        }, function errorCallback(response) {
            // 请求失败执行代码
        });

        $http({
            method:'GET',
            url:"/FieldFormShow/getFieldDetailByFormName",
            params:{formName:$scope.fieldFormInfo[c].formName}
        }).then(function successCallback(response) {
            var data=response.data;
            var all=[];
            for(var i in data){
                var t=[];
                for(var j in data[i]){
                    var d=[];
                    d.push(data[i][j]);
                    d.push(i);
                    t.push(d);
                }
                all=all.concat(t);
                $scope.dataTableSet_ALL[i]=t;
            }
            $scope.dataTableSet_ALL['ALL']=all;
            fieldDatatableDraw(all);
        }, function errorCallback(response) {
            // 请求失败执行代码
        });
    }

    $("#FieldFormSelect").on('changed.bs.select',function (e,c) {
        refresh(c);
        $('#typeSelect').selectpicker('val', 'ALL');
        $('#typeSelect').selectpicker('refresh');
    })

    $("#typeSelect").on('changed.bs.select',function (e,c){
        fieldDatatableDraw($scope.dataTableSet_ALL[$scope.selectType[c]]);
    })

    //dataTable
    var fieldDatatableDraw=function(data){
        dataTable=$('#FieldDataTable').dataTable( {
            "data": data,
            "autoWidth": true,
            "destroy": true,
            "bLengthChange":false,
            "iDisplayLength":10,
            "columns": [
                { "title": "字段名" },
                { "title": "字段类型" },
            ]
        } );
    }

    //highcharts 图表
    var createcharts=function () {
        $('#container').highcharts({
            credits:{enabled:false},
            chart: {
                plotBackgroundColor: null,
                plotBorderWidth: null,
                plotShadow: false,
            },
            title: {
                text: '各类型节点占比<br>节点总数：'+percentage.SUM+''
            },
            tooltip: {
                headerFormat: '{series.name}<br>',
                pointFormat: '{point.name}: <b>{point.percentage:.1f}%</b>'
            },
            plotOptions: {
                pie: {
                    allowPointSelect: true,
                    cursor: 'pointer',
                    dataLabels: {
                        enabled: true,
                        format: '<b>{point.name} </b>: {point.percentage:.1f} %',
                        style: {
                            color: (Highcharts.theme && Highcharts.theme.contrastTextColor) || 'black'
                        }
                    }
                }
            },
            series: [{
                type: 'pie',
                name: '字段占比',
                data: Accounting,
            }]
        });
    };

    ///////////////// 节点函数

    //按钮点击事件
    $('#NodeFormSelect').on('changed.bs.select', function (e,c) {
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
                nodeDatatableDraw();
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
                nodeDatatableDraw();
            }, function errorCallback(response) {
                // 请求失败执行代码
            });
        }
    }

    //dataTable
    var nodeDatatableDraw=function(){
        if($scope.selectFile==="openEhr"){
            dataTable=$('#NodeDataTable').dataTable( {
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
            dataTable=$('#NodeDataTable').dataTable( {
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



    fieldDatatableDraw([]);
    nodeDatatableDraw();
    createcharts();
})