var app = angular.module("FileProcessingApp", ['ngFileUpload']);
app.controller("FileProcessingCtrl", ['$scope', 'Upload', '$timeout', function ($scope, Upload, $timeout) {

    $scope.progress = 0;
    $scope.typelevel="s";
    $scope.uploadstate=false;
    $scope.files=[];
    $scope.filesList=[];
    $scope.loadingImg="img/zip.png";

    $(function (){
        $("[data-toggle='popover']").popover();
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

    $(".selectpicker").change(function(evt){
        if(evt.currentTarget.value==="l") alert("注意：当前选择方式为有限数据集！处理文件只供内部使用！！")
        $scope.progress= 0;
    })

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
                url: 'filecontent',
                data: {'level':$scope.typelevel},
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



}]);