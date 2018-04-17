var app = angular.module("usageOverViewApp", []);
app.controller("usageOverViewCtrl", function($scope,$http,$timeout,$q) {

    $scope.usenum=0;
    $scope.usernum=0;
    $scope.overViewData;

    (function () {
        $http({
            method:'GET',
            url:"/getUsageOverView"
        }).then(
            function (response) {
                $scope.overViewData=response.data;
                $scope.usernum=$scope.overViewData.length;
                for(var i in $scope.overViewData)
                    for(var j in $scope.overViewData[i].data)
                        $scope.usenum=$scope.usenum+$scope.overViewData[i].data[j][1];
                $scope.createcharts();
            },
            function (response) {
                // 请求失败执行代码
            }
        );
    })();

    $scope.createcharts=function () {
        Highcharts.chart('container', {
            credits:{enabled:false},
            chart: {
                type: 'spline'
            },
            title: {
                text: 'API使用情况总览'
            },
            subtitle: {
                text: ''
            },
            xAxis: {
                type: 'datetime',
            },
            yAxis: {
                title: {
                    text: '日使用次数'
                },
                min: 0
            },
            tooltip: {
                headerFormat: '<b>{series.name}</b><br>',
                pointFormat: '{point.x:%Y/%m/%e} : {point.y} 次'
            },
            plotOptions: {
                spline: {
                    marker: {
                        enabled: true
                    }
                }
            },
            series: $scope.overViewData,
        });
    }


});