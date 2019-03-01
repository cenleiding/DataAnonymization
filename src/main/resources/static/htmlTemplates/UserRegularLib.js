var app = angular.module("userRegularLibApp", ['ngDialog','uploadDictionaryApp','createNewRegularApp','createNewRegularLibApp','changeRegularApp','deleteRegularLibApp']);
app.controller("userRegularLibCtrl", function($scope,$http,ngDialog) {

    $scope.ifedit = false;
    $scope.regularLibList=[];
    $scope.regularLib = {};
    $scope.dictionaryList = [];
    $scope.regularList = [];
    $scope.testResult=[];

    $scope.checkbox_type = {
        "xtzd" : true,
        "xtgz" : true,
        "wdzd" : true,
        "wdgz" : true,
        "jqxx" : true
    }


    $scope.testText =
        "测 试 样 例\n" +
        "入 院 记 录\n" +
        "    姓    名：张润梅\n" +
        "    出 生 地：山西省太原市\n" +
        "    性    别：女\n" +
        "    职    业：个体\n" +
        "    年    龄：61\n" +
        "    入院日期：2013年08月01日\n" +
        "    民    族：汉\n" +
        "    记录日期：2013年08月01日\n" +
        "    婚    否：已婚\n" +
        "    病历陈述者：患者本人\n" +
        "主  诉：发现血糖升高10年。\n" +
        "    现病史：2003年患者因脸部浮肿，无明显多尿、口干、多饮症状，体重减轻约10斤，就诊于山西省中医研究所，测空腹血糖升高（具体值不详），行糖耐量试验、胰岛素释放试验等相关试验，诊断为“2型糖尿病”，口服中药降糖治疗，监测血糖示：空腹血糖波动于6-7mmol/L,餐后2小时血糖波动于8-9mmol/L。后因血糖控制不佳，多次调整降糖方案。2013年春节，因血糖控制不佳，调整治疗方案为早、晚口服格列美脲片（1片，2次/日）降糖治疗，测空腹血糖波动于6-8mmol/L,餐后2小时血糖波动于8-9mmol/L。病程中患者间断出现双手麻木，视物模糊，无针刺样疼痛，无双下肢水肿，无蚁行感，无黑曚、斜视，现为进一步诊断及治疗入住我科，自发病以来患者神志清，精神好，睡眠食欲可，大小便正常，体重无明显改变。\n" +
        "   发现血压升高10年，血压最高达160/90mmHg,目前口服左旋氨氯地平片（早：1片，晚：半片）降压治疗，血压控制尚可。\n" +
        "    既往史：既往体健，20年前行腮腺瘤手术，腰椎间盘膨出病史20年。否认肝炎结核等传染病史，否认手术、外伤史，否认输血史，否认食物、药物过敏史，对抗菌优过敏否认预防接种史。";


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


    $scope.editRegularLib=function (libName) {
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
        // 规则列表展示
        getRegularByLibName();
    }

    $scope.deleteRegularLib = function (libName) {
        ngDialog.open({
            template: '/htmlTemplates/DeleteRegularLib.html',
            className: 'ngdialog-theme-default',
            controller: 'deleteRegularLibCtrl',
            resolve: {//传参
                dep: function() {
                    return libName;
                }
            },
            width:320,
            height: 150,})
            .closePromise.then(function(value) {
                location.reload();
        });
    }
    
    $scope.saveChange = function () {
        $http({
            method:'GET',
            url:"/MyReAndDic/saveChange",
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
        }, function errorCallback(response) {
            alert("字典列表获取失败！")
        });
    }

    var getRegularByLibName=function () {
        $http({
            method:'GET',
            url:"/MyReAndDic/getRegularByLibName",
            params:{libName: $scope.regularLib.libName
            }
        }).then(function successCallback(response) {
            $scope.regularList = response.data;
        }, function errorCallback(response) {
            alert("规则列表获取失败！")
        });
    }


    $scope.uploadDictionary=function () {

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

    $scope.createNewRegular = function(){
        ngDialog.open({
            template: '/htmlTemplates/CreateNewRegular.html',
            className: 'ngdialog-theme-default',
            controller: 'createNewRegularCtrl',
            resolve: {//传参
                dep: function() {
                    return $scope.regularLib.libName;
                }
            },
            width:550,
            height: 300,})
            .closePromise.then(function(value) {
            getRegularByLibName();
        });
    }

    $scope.createNewRegularLib = function () {
        ngDialog.open({
            template: '/htmlTemplates/CreateNewRegularLib.html',
            className: 'ngdialog-theme-default',
            controller: 'createNewRegularLibCtrl',
            width:250,
            height: 300,})
            .closePromise.then(function(value) {
            location.reload();
            });
    }
    
    $scope.changeRegular = function (regular) {
        ngDialog.open({
            template: '/htmlTemplates/ChangeRegular.html',
            className: 'ngdialog-theme-default',
            controller: 'changeRegularCtrl',
            resolve: {//传参
                dep: function() {
                    return regular;
                }
            },
            width:550,
            height: 300,})
            .closePromise.then(function(value) {
            getRegularByLibName();
        });
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

    $scope.deleteRegular=function (id) {
        $http({
            method:'GET',
            url:"/MyReAndDic/deleteRegular",
            params:{
                id: id,
                libName:$scope.regularLib.libName
            }
        }).then(function successCallback(response) {
            getRegularByLibName();
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
        }, function errorCallback(response) {
            alert("删除失败！")
        });
    }

    // 测试单字典
    $scope.test_simple_dictionary = function (fileName) {
        $http({
            method:'GET',
            url:"/MyReAndDic/testSimpleDictionary",
            params:{
                libName: $scope.regularLib.libName,
                fileName:fileName,
                content: $scope.testText
            }
        }).then(function successCallback(response) {
            showTestResult(response)
        }, function errorCallback(response) {
            alert("发生未知错误！")
        });
    }

    $scope.test_simple_regular = function (regular) {
        $http({
            method:'GET',
            url:"/MyReAndDic/testSimpleRegular",
            params:{
                area:regular.area,
                aims:regular.aims,
                content: $scope.testText
            }
        }).then(function successCallback(response) {
            showTestResult(response)
        }, function errorCallback(response) {
            alert("发生未知错误！")
        });
    }

    $scope.test_all = function () {
        $http({
            method:'GET',
            url:"/MyReAndDic/testAll",
            params:{
                xtzd : $scope.checkbox_type.xtzd,
                xtgz : $scope.checkbox_type.xtgz,
                wdzd : $scope.checkbox_type.wdzd,
                wdgz : $scope.checkbox_type.wdgz,
                jqxx : $scope.checkbox_type.jqxx,
                libName : $scope.regularLib.libName,
                content: $scope.testText
            }
        }).then(function successCallback(response) {
            showTestResult(response)
        }, function errorCallback(response) {
            alert("发生未知错误！")
        });
    }

    var showTestResult = function (response) {
        $scope.testResult=[]
        for(key in response.data){
            var data = {};
            data.tar = key;
            data.alt = response.data[key];
            $scope.testResult.push(data);
        }
    }

})