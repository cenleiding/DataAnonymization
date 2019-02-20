var app = angular.module("anonymizeConfigureApp", ['rzModule']);
app.controller("anonymizeConfigureCtrl", function($scope,$http,$interval,$rootScope) {

    (function (){
        //获取配置
        $http(
            {
                url:"/FileProcessing/getAnonymizeConfigure",
                method:"GET"
            }
        ).then(
            function success(response) {
                $scope.config_old=response.data;
                $rootScope.config=JSON.parse(JSON.stringify($scope.config_old));
                $scope.refreshSlider();
                $interval(function() {
                    $scope.$broadcast('rzSliderForceRender');
                },1000);
            },
            function error() {
                alert("获取配置失败！")
            }
        )
    })()

    $scope.showFlg=true;

    $scope.encryptPassword="";

    $scope.k_small_slider = {
        value: NaN,
        options: {
            floor: 2,
            ceil: 10,
            step:1,
            translate: function(value) {
                $rootScope.config.k_small=value;
                return value
            },
        },
    },

    $scope.k_big_slider = {
        value: NaN,
        options: {
            floor: 2,
            ceil: 10,
            step:1,
            translate: function(value) {
                $rootScope.config.k_big=value;
                return value
            },
        },
    },

    $scope.suppressionLimit1_slider = {
        value: NaN,
        options: {
            floor: 0,
            ceil: 100,
            step:1,
            translate: function(value) {
                $rootScope.config.suppressionLimit_level1=value/100;
                return value+"%"
            },
        },
    },

    $scope.suppressionLimit2_slider = {
        value: NaN,
        options: {
            floor: 0,
            ceil: 100,
            step:1,
            translate: function(value) {
                $rootScope.config.suppressionLimit_level2=value/100;
                return value+"%"
            },
        },
    },

    $scope.noiseScope_small_slider = {
        value: 8,
        options: {
            floor: 0,
            ceil: 10,
            step:1,
            translate: function(value) {
                $rootScope.config.noiseScope_small=value/100;
                return value+"%"
            },
        },
    },

    $scope.noiseScope_big_slider = {
        value: NaN,
        options: {
            floor: 0,
            ceil: 10,
            step:1,
            translate: function(value) {
                $rootScope.config.noiseScope_big=value/100;
                return value+"%"
            },
        },
    },

    $scope.t_slider = {
        value: NaN,
        options: {
            floor: 0,
            ceil: 100,
            step:1,
            translate: function(value) {
                $rootScope.config.t=value/100;
                return value+"%"
            },
        },
    },

    $scope.microaggregation_slider = {
        value: NaN,
        options: {
            floor: 5,
            ceil: 20,
            step:1,
            translate: function(value) {
                $rootScope.config.microaggregation=value;
                return value+"组"
            },
        },
    },


    $scope.reset=function () {
        $rootScope.config=JSON.parse(JSON.stringify($scope.config_old));
        $scope.refreshSlider();
    }

    $scope.refreshSlider = function() {
        $scope.encryptPassword=$rootScope.config.k_small.encryptPassword;
        $scope.k_small_slider.value=parseInt($rootScope.config.k_small);
        $scope.k_big_slider.value=parseInt($rootScope.config.k_big);
        $scope.suppressionLimit1_slider.value=parseFloat($rootScope.config.suppressionLimit_level1)*100;
        $scope.suppressionLimit2_slider.value=parseFloat($rootScope.config.suppressionLimit_level2)*100;
        $scope.noiseScope_small_slider.value=parseFloat($rootScope.config.noiseScope_small)*100;
        $scope.noiseScope_big_slider.value=parseFloat($rootScope.config.noiseScope_big)*100;
        $scope.t_slider.value=parseFloat($rootScope.config.t)*100;
        $scope.microaggregation_slider.value=parseInt($rootScope.config.microaggregation);
        console.log($rootScope.config)
        $scope.$broadcast('rzSliderForceRender');
    }

})