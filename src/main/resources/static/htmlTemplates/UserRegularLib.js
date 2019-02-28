var app = angular.module("userRegularLibApp", ['ngDialog','uploadDictionaryApp','createNewRegularApp','changeRegularApp']);
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


    $scope.testText = "患者姓名史玉腾年龄12岁住院号00148804复印病案用途申请人史玉腾证件及证件号130581200306132219代理人与患者关系证件及证件号出院科室耳鼻咽喉头颈外科出院日期医师签名办理方式邮寄:出院当日办理自取:病案归档后办理办理时间工作日(8:00-11:30,14:30-17:30)办理地点医学影像楼4层病案室重要提示:请携带本申请表患者和代理人有效身份证件原件科室盖章联系电话:14797169824复印时间年月日\n" +
        "入院记录姓名:赵青爱出生地:山西晋城性别:男职业:退(离)休人员年龄:71岁入院日期:2015年09月民族:汉族记录日期:2015年09月婚否:已婚病历陈述者:患者本人主诉:间断关节肿痛15年,加重2月。\n" +
        "现病史:2000年06月患者在进食肉类及饮酒后出现左足第1跖趾关节肿痛,就诊于太原市煤炭中心医院,查血尿酸460μmol/L,诊断为\"痛风\",予静滴\"青霉素\"800万Ux5天,口服\"苯溴马隆\"50mg,1次/日治疗,1周后关节肿痛减轻,后因出现腹泻症状更换为\"别嘌醇\"0.1-0.2/日,病情控制良好。\n" +
        "2003年患者因皮肤红斑就诊于山西医科大学第二医院,诊断为\"变应性血管炎\",考虑为别嘌醇所致,予停用别嘌醇,更换为\"雷公藤多甙\"20mg,3次/日治疗,半年后出现\"心律失常,室性早搏\",遂停用雷公藤多甙。\n" +
        "2005年冬患者出现右手第2指近端指间关节痛,自行口服中药及\"复方伸筋胶囊\"2粒,2次/日治疗,关节痛减轻。\n" +
        "2014年09月患者复查血尿酸550μmol/L,自行口服中药及\"碳酸氢钠\"2粒,3次/日治疗,后复查血尿酸降至正常范围,尿PH值升高,停服上述药物。\n" +
        "2015年07月患者在进食鸡肉后出现右足第1跖趾关节肿痛,自行口服\"吲哚美辛\"1片,2次/日,\"非布司他\"40mg,1次/日治疗,关节肿痛减轻,于2015年08月再次出现右踝关节及右足第1跖趾关节肿痛,程度较上次为轻,自行口服\"塞来昔布\"及\"秋水仙碱\"治疗,后因出现心慌、腹部不适症状停用,关节肿痛缓解不明显,为求进一步诊治收住我科。\n" +
        "病程中有右足麻木,无面部蝶形红斑、反复口腔溃疡、脱发、光过敏,无腰痛、臀区痛、足跟痛,无四肢近端肌痛、肌无力,无发热、皮疹、咳嗽、气短、腹痛等症状。\n";



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
        // 规则列表展示
        getRegularByLibName();
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
            console.log(response.data);
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