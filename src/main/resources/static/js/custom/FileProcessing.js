var app = angular.module("FileProcessingApp", ['ngFileUpload',,'ngDialog','headApp','anonymizeConfigureApp']);
app.controller("FileProcessingCtrl",function ($scope, Upload,$http,ngDialog) {

    $scope.progress = 0;
    $scope.uploadstate=false;
    $scope.files=[];
    $scope.filesList=[];
    $scope.loadingImg="img/zip.png";
    $scope.fromName="";
    $scope.selectTypelevel="s";
    $scope.selectInfo=[];
    $scope.config={};

    var select = $("#fromName");

    (function (){
        $http(
        {
            method:"GET",
            url:"/getFieldFormInfo"
        }
        ).then(
            function successCallback(response){
                var formMap=response.data;
                var userNameMap={};
                var info;
                for(var i in formMap)
                    userNameMap[formMap[i].userName]="";
                select.append("<option value='un' disabled='disabled'  data-icon='glyphicon glyphicon-user' style='background: #5cb85c; color: #fff;'>系统样本</option>");
                $scope.selectInfo.push("");
                tra("",formMap)
                for(var i in userNameMap){
                    if (i=="") continue;
                    select.append("<option value='un' disabled='disabled'  data-icon='glyphicon glyphicon-user' style='background: #5cb85c; color: #fff;'>"+i+"</option>");
                    $scope.selectInfo.push(i);
                    tra(i,formMap)
                }
                $("#fromName").selectpicker('refresh');
                $("#fromName").selectpicker('val', $scope.selectInfo[1].formName);
                $scope.config.fieldFormName=$scope.selectInfo[1].formName;
                $scope.config.level="Level1";
            },
            function errorCallback(response){
                alert("获取列表失败！")
            }
        )
        //获取配置
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
                alert("获取配置失败！")
            }
        )
        $("[data-toggle='popover']").popover();
    })();


    var tra=function (i,formMap) {
        for(var j in formMap){
            if(formMap[j].userName==(i)){
                select.append("<option value='"+formMap[j].formName+"' data-icon='glyphicon glyphicon-file' data-subtext=("+formMap[j].description+")>"
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

    $("#typeLevel").change(function(evt){
        if(evt.currentTarget.value==="Level1") alert("注意：当前选择为研究性数据！处理文件只供内部使用！！");
        $scope.config.level=evt.currentTarget.value;
        $scope.progress= 0;
    });

    $("#fromName").on('changed.bs.select', function (e,c) {
        $scope.config.fieldFormName=$scope.selectInfo[c].formName;
    });

    $(":file").change(function(){
        var files=$(this.files);
        for(var i=0;i<files.length;i++){
            var k=0;
            for(var j=0;j<$scope.filesList.length;j++)
                if($scope.filesList[j].name===files[i].name) {$scope.filesList[j]=files[i];k=1;}
            if (k===0) $scope.filesList=$scope.filesList.concat(files[i]);
        }
        $scope.progress = 0;
    });


    $scope.deleteFileal=function(i){
        $scope.filesList.splice(i,1);
        $scope.progress = 0;
    }


    $scope.uploadPic = function(file) {
        if(file.length==0){ alert("请选择需要处理的文件!!");return;}
        $scope.state=true;
        $scope.loadingImg="img/file_loading.gif";
        file.upload =
            Upload.upload({
                url: '/FileProcessing/filecontent',
                data: $scope.config,
                file: file
            });

        file.upload.then(function (response) {
            $scope.loadingImg="img/zip.png";
            $scope.path=response.data.url;
        }, function (response) {
            if (response.status > 0)
                $scope.errorMsg = response.status + ': ' + response.data;
        }, function (evt) {
            $scope.progress = Math.min(100, parseInt(100.0 *evt.loaded / evt.total));
        });
    }

    $scope.anonymizeConfigure=function () {
        ngDialog.open({
            template: '/htmlTemplates/AnonymizeConfigure.html',
            className: 'ngdialog-theme-default',
            controller: 'anonymizeConfigureCtrl',
            resolve: {//传参
                dep: function() {
                    return  $scope.config;
                }
            },
            width:450,
            height: 550,})
            .closePromise.then(function(value) {
                $scope.config=value.$dialog.scope().config;
        });
    }
})
