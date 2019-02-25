var app = angular.module("userRegularLibApp", ['ngDialog','uploadDictionaryApp']);
app.controller("userRegularLibCtrl", function($scope,$http,$rootScope,ngDialog) {

    $scope.ifedit = false;
    $scope.regularLibList=[];
    $scope.regularLib = {};
    $scope.dictionaryList = [];


    //获得规则库信息
    (function () {
        $http(
            {
                url:"/MyReAndDic/getRegularLibName",
                method:"GET"
            }
        ).then(
            function success(response) {
                console.log(response.data);
                $scope.regularLibList=response.data;
            },
            function error() {
                alert("获取列表失败！")
            }
        )

    })()


    $scope.editForm=function (libName) {
        $scope.ifedit=true;
        // 基础信息展示
        for (var i in $scope.regularLibList){
            var re = $scope.regularLibList[i]
            if (re.libName===libName) {
                $scope.regularLib.libName =re.libName;
                $scope.regularLib.changeTime = re.changeTime;
                $scope.regularLib.createTime = re.createTime;
                $scope.regularLib.user = re.user;
                $scope.regularLib.description = re.description;
                $scope.regularLib.new_libName = $scope.regularLib.libName;
                $scope.regularLib.new_description = $scope.regularLib.description;
            }
        }

        // 字典列表展示
        getDictionaryByLibName();

    }
    
    $scope.saveChange = function () {
        $http({
            method:'GET',
            url:"/UserConfig/SaveChange",
            params:{old_libName: $scope.regularLib.libName,
                    new_libName: $scope.regularLib.new_libName,
                    new_description:$scope.regularLib.new_description
            }
        }).then(function successCallback(response) {
            alert(response.data);
            location.reload();
        }, function errorCallback(response) {
            alert("保存失败！")
        });
        
    }

    var getDictionaryByLibName = function(){
        $http({
            method:'GET',
            url:"/MyReAndDic/getDictionaryByLibName",
            params:{libName: $scope.regularLib.libName
            }
        }).then(function successCallback(response) {
            $scope.dictionaryList = response.data;
            console.log(response.data);
        }, function errorCallback(response) {
            alert("字典列表获取失败！")
        });
    }

    $scope.uploadDictionary=function () {
        if($scope.regularLib.libName == null) alert("请选择规则库！")
        else {
            ngDialog.open({
                template: '/htmlTemplates/UploadDictionary.html',
                className: 'ngdialog-theme-default',
                controller: 'uploadDictionaryCtrl',
                resolve: {//传参
                    dep: function() {
                        return $scope.regularLib.libName;
                    }
                },
                width:240,
                height: 350,})
                .closePromise.then(function(value) {
                getDictionaryByLibName();
            });
        }
    }

    $scope.deleteDictionary=function (fileName) {
        $http({
            method:'GET',
            url:"/MyReAndDic/deleteDictionary",
            params:{
                libName: $scope.regularLib.libName,
                fileName:fileName
            }
        }).then(function successCallback(response) {
            getDictionaryByLibName();
        }, function errorCallback(response) {
            alert("删除失败！")
        });
    }

    $scope.downloadDictionary=function (fileName) {
        $http({
            method:'GET',
            url:"/MyReAndDic/downloadDictionary",
            params:{
                libName: $scope.regularLib.libName,
                fileName:fileName
            }
        }).then(function successCallback(response) {
            var a = document.createElement('a');
            a.href = response.data[0];
            a.click();
            console.log(response.data[0]);
        }, function errorCallback(response) {
            alert("删除失败！")
        });
    }


})