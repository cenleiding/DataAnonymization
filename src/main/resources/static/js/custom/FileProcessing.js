var app = angular.module("FileProcessingApp", ['ngFileUpload','ngDialog','navigationBarApp','anonymizeConfigureApp']);
app.controller("FileProcessingCtrl",function ($scope, Upload,$http,ngDialog,$rootScope) {

    $rootScope.sidebarPage=0;
    $scope.page=0;

    $scope.uploadstate=false;
    $scope.files=[];
    $scope.filesList=[];
    $scope.loadingImg="img/zip.png";
    $scope.fromName="";
    $scope.selectTypelevel="Level1";
    $scope.selectInfo=[];

    var select = $("#fromName");

    (function (){

        $http(
        {
            method:"GET",
            url:"/FieldFormShow/getFieldFormInfo"
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
                $scope.fieldFormName=$scope.selectInfo[1].formName;
                $scope.level="Level1";
            },
            function errorCallback(response){
                alert("获取列表失败！")
            }
        )
        $("[data-toggle='popover']").popover();
    })();


    var tra=function (i,formMap) {
        for(var j in formMap){
            if(formMap[j].userName==(i)){
                select.append("<option value='"+formMap[j].formName+"' data-icon='glyphicon glyphicon-file' data-subtext=(使用次数："+formMap[j].usageCount+")>"
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
        $scope.level=evt.currentTarget.value;
    });

    $("#fromName").on('changed.bs.select', function (e,c) {
        $scope.fieldFormName=$scope.selectInfo[c].formName;
    });

    $(":file").change(function(){
        var files=$(this.files);
        for(var i=0;i<files.length;i++){
            var k=0;
            for(var j=0;j<$scope.filesList.length;j++)
                if($scope.filesList[j].name===files[i].name) {$scope.filesList[j]=files[i];k=1;}
            if (k===0) $scope.filesList=$scope.filesList.concat(files[i]);
        }
        $scope.state = false;
    });


    $scope.deleteFileal=function(i){
        $scope.filesList.splice(i,1);
        $scope.state = false;
    }


    $scope.uploadFile = function(file) {
        if(file.length==0){ alert("请选择需要处理的文件!!");return;}
        $scope.state=true;
        $scope.loadingImg="img/file_loading.gif";
        $rootScope.config.level=$scope.level;
        $rootScope.config.fieldFormName=$scope.fieldFormName;
        file.upload =
            Upload.upload({
                url: '/FileProcessing/filecontent',
                data: $rootScope.config,
                file: file
            });

        file.upload.then(function (response) {
            $scope.loadingImg="img/zip.png";
            $scope.path=response.data.url;
        }, function (response) {
            if (response.status > 0)
                $scope.errorMsg = response.status + ': ' + response.data;
        }, function (evt) {
        });
    }

})
