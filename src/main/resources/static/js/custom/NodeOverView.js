var app = angular.module("nodeOverViewApp", ['headApp']);
app.controller("nodeOverViewCtrl", function($scope,$http,$timeout,$q) {

    var percentage;
    var num;

    var Accounting=[
        ["EI",0],
        ["QI-Geographic",0],
        ["QI-Datate",0],
        ["QI-Number",0],
        ["QI-String",0],
        ["SI-Number",0],
        ["SI-String",0],
        ["UI",0]
    ];

    (function () {
        $http({
            method:'GET',
            url:"/getNodeOverView",
        }).then(function successCallback(response) {
            percentage=response.data;
            for(var a in Accounting){
                Accounting[a][1]=percentage[a];
            }
            $scope.createcharts();
        }, function errorCallback(response) {
            // 请求失败执行代码
        });
    })();

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
                text: '各类型节点占比<br>节点总数：'+percentage[percentage.length-1]+''
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


});