var app = angular.module("fieldFormShowApp", ['angular-popups','headApp']);
app.controller("fieldFormShowCtrl", function($scope,$http,$timeout,$q) {


    $scope.selectInfo=[];
    $scope.formNameList=[];
    $scope.dataTableSet_ALL=[];

    $scope.description=""
    $scope.createTime=""
    $scope.lastChangeTime=""
    $scope.father=""
    $scope.usageCount=""
    $scope.userName=""

    $scope.spanFlag=false;
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

    (function () {
        $http(
            {
                method:"GET",
                url:"/getFieldFormInfo"
            }
        ).then(
            function successCallback(response){
                var select = $("#Select");
                var formMap=response.data;
                var userNameMap={};
                var info;
                for(var i in formMap)
                    userNameMap[formMap[i].userName]="";
                for(var i in userNameMap){
                    var userName=i;
                    if (userName=="") userName="系统样本";
                    select.append("<option value='un' disabled='disabled'  data-icon='glyphicon glyphicon-user' style='background: #5cb85c; color: #fff;'>"+userName+"</option>");
                    $scope.selectInfo.push(i);
                    for(var j in formMap){
                        if(formMap[j].userName==(i)){
                            select.append("<option data-icon='glyphicon glyphicon-file' data-subtext=("+formMap[j].description+")>"
                                +formMap[j].formName+ "</option>");
                            var info={};
                            info['formName']=formMap[j].formName;
                            info['description']=formMap[j].description;
                            info['createTime']=formMap[j].createTime;
                            info['lastChangeTime']=formMap[j].lastChangeTime;
                            info['father']=formMap[j].father;
                            info['usageCount']=formMap[j].usageCount;
                            info['userName']=formMap[j].userName;
                            $scope.selectInfo.push(info);
                        }
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

    $("#Select").on('changed.bs.select',function (e,c) {
        $scope.description=$scope.selectInfo[c].description
        $scope.createTime=$scope.selectInfo[c].createTime
        $scope.lastChangeTime=$scope.selectInfo[c].lastChangeTime
        $scope.father=$scope.selectInfo[c].father
        $scope.usageCount=$scope.selectInfo[c].usageCount
        $scope.userName=$scope.selectInfo[c].userName
        if($scope.father=="") $scope.father="原生模板"
        if($scope.userName=="") $scope.userName="系统样本"
        $scope.spanFlag=1
        $http({
            method:'GET',
            url:"/getFieldOverViewByFormName",
            params:{formName: $scope.selectInfo[c].formName}
        }).then(function successCallback(response) {
            percentage=response.data;
            for(var a in Accounting){
                Accounting[a][1]=percentage[Accounting[a][0]];
            }
            $scope.createcharts();
        }, function errorCallback(response) {
            // 请求失败执行代码
        });

        $http({
            method:'GET',
            url:"/getFieldDetailByFormName",
            params:{formName:$scope.selectInfo[c].formName}
        }).then(function successCallback(response) {
            data=response.data;
            for(var i in data){
                for(var j in data[i]){
                    var d=[];
                    d.push(data[i][j]);
                    d.push(i);
                    $scope.dataTableSet_ALL.push(d);
                }
            }
            datatableDraw();
        }, function errorCallback(response) {
            // 请求失败执行代码
        });
    })

    //dataTable
    var datatableDraw=function(){
        dataTable=$('#dataTable').dataTable( {
            "data": $scope.dataTableSet_ALL,
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
    $scope.createcharts=function () {
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
}
)