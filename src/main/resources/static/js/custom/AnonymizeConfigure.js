var app = angular.module("anonymizeConfigureApp", []);
app.controller("anonymizeConfigureCtrl", function($scope,$http) {


    $scope.config={};
    $scope.showFlg=true;
    // private String                 encryptPassword;
    //
    // private Double                 noiseScope_big;
    //
    // private Double                 noiseScope_small;
    //
    // private String                 k_big;
    //
    // private String                 k_small;
    //
    // private String                 t;
    //
    // private String                 suppressionLimit_level1;
    //
    // private String                 suppressionLimit_level2;
    //
    // private String                 microaggregation;

    (function () {
        $http(
            {
                url:"/FileProcessing/getAnonymizeConfigure",
                method:"GET"
            }
        ).then(
            function success(response) {
                $scope.config=response.data;
            },
            function error() {
                alert("获取列表失败！")
            }
        )

    })()



})