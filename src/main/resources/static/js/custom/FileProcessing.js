var app = angular.module("FileProcessingApp", ['ngFileUpload','headApp']);
app.controller("FileProcessingCtrl",function ($scope, Upload,$http,$timeout) {

    $scope.progress = 0;
    $scope.uploadstate=false;
    $scope.files=[];
    $scope.filesList=[];
    $scope.loadingImg="img/zip.png";
    $scope.fromName="";
    $scope.selectTypelevel="s";
    $scope.selectFromName="Original";

    (function (){
        $http({
            method:'GET',
            url:"/getFromNameList"
        }).then(
            function successCallback (response) {
                $scope.fromName=response.data;
                var select = $("#fromName");
                for (var i = 0; i < $scope.fromName.length; i++) {
                    select.append("<option value='"+$scope.fromName[i]+"'>"
                        + $scope.fromName[i] + "</option>");
                }
                $('#fromName').selectpicker('val','Original');
                $('.selectpicker').selectpicker('refresh');
            },
            function errorCallback (response) {
                console.log("获取字段表单失败！");
            }
        );
        $("[data-toggle='popover']").popover();
    })();

    $("#typeLevel").change(function(evt){
        if(evt.currentTarget.value==="l") alert("注意：当前选择方式为有限数据集！处理文件只供内部使用！！")
        $scope.progress= 0;
    });

    $("#fromName").on('changed.bs.select', function (e,c) {
        $scope.selectFromName=$scope.fromName[c];
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
                url: '/filecontent',
                data: {level:$scope.selectTypelevel,
                       fieldFromName:$scope.selectFromName},
                file: file
            });

        file.upload.then(function (response) {
            $scope.loadingImg="img/zip.png";
            $scope.path=response.data[0];
        }, function (response) {
            if (response.status > 0)
                $scope.errorMsg = response.status + ': ' + response.data;
        }, function (evt) {
            $scope.progress = Math.min(100, parseInt(100.0 *evt.loaded / evt.total));
        });
    }

})
