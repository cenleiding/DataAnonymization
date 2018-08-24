var app = angular.module("anonymizeConfigureApp", ['rzModule']);
app.controller("anonymizeConfigureCtrl", function($scope,$http,$timeout,dep) {

    $scope.config=JSON.parse(JSON.stringify(dep));
    $scope.showFlg=true;

    $scope.encryptPassword="";

    $scope.k_small_slider = {
        value: NaN,
        options: {
            floor: 2,
            ceil: 10,
            step:1,
            translate: function(value) {
                $scope.config.k_small=value;
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
                $scope.config.k_big=value;
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
                $scope.config.suppressionLimit_level1=value/100;
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
                $scope.config.suppressionLimit_level2=value/100;
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
                $scope.config.noiseScope_small=value/100;
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
                $scope.config.noiseScope_big=value/100;
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
                $scope.config.t=value/100;
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
                $scope.config.microaggregation=value;
                return value+"组"
            },
        },
    },


    $scope.reset=function () {
        $scope.config=JSON.parse(JSON.stringify(dep));
        $scope.refreshSlider();
    }

    $scope.refreshSlider = function() {
        $scope.encryptPassword=$scope.config.k_small.encryptPassword;
        $scope.k_small_slider.value=parseInt($scope.config.k_small);
        $scope.k_big_slider.value=parseInt($scope.config.k_big);
        $scope.suppressionLimit1_slider.value=parseFloat($scope.config.suppressionLimit_level1)*100;
        $scope.suppressionLimit2_slider.value=parseFloat($scope.config.suppressionLimit_level2)*100;
        $scope.noiseScope_small_slider.value=parseFloat($scope.config.noiseScope_small)*100;
        $scope.noiseScope_big_slider.value=parseFloat($scope.config.noiseScope_big)*100;
        $scope.t_slider.value=parseFloat($scope.config.t)*100;
        $scope.microaggregation_slider.value=parseInt($scope.config.microaggregation);
        $timeout(function() {
            $scope.$broadcast('rzSliderForceRender')
        },0,false)
    }
    $scope.refreshSlider();


})