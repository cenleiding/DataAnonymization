var app = angular.module("privacyFieldOverViewApp", []);
app.controller("privacyFieldOverViewCtrl", function($scope,$http,$timeout,$q) {

    var fields;
    var num;

    var Accounting=[
        ["Names",0],
        ["Geographic",0],
        ["Date",0],
        ["Telephone_Numbers",0],
        ["Fax_Numbers",0],
        ["Email_Addresses",0],
        ["Social_Security_Numbers",0],
        ["Medical_Record_Numbers",0],
        ["Health_Plan_Beneficiary_Numbers",0],
        ["Account_Numbers",0],
        ["Certificate_License_Numbers",0],
        ["Vehicle_Identifiers",0],
        ["Device_Identifiers",0],
        ["Url",0],
        ["Ip",0],
        ["Biometric_Identifiers",0],
        ["Photographs",0],
        ["Other_Hard",0],
        ["Other_Middle",0],
        ["Other_Soft",0],
        ["Unstructured_Data",0]
    ];

    (function () {
        $http({
            method:'GET',
            url:"/getFieldOverView",
        }).then(function successCallback(response) {
            fields=response.data;
            num=0;
            for(var a in fields){
                num+=fields[a].length;
            }
            for(var a in Accounting){
                Accounting[a][1]=fields[Accounting[a][0]].length/num;
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
                text: '各类型字段占比<br>字段总数：'+num+''
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