var app = angular.module("uploadDictionaryApp",['ngFileUpload']);
app.controller("uploadDictionaryCtrl", function($scope,$http,dep,Upload) {

    var libName = dep;
    $scope.Description = "";

    $scope.upload=function () {
        if($scope.file==null) alert("请选择文件！");
        else{
            Upload.upload({
                url: '/MyReAndDic/uploadDictionary',
                file: $scope.file,
                fields: {'libName': libName,
                         'description':$scope.Description},
            }).progress(function (evt) {
            }).success(function (data, status, headers) {
                if(data[0]==="fileName_repeat") alert("文件名重复！")
                else{
                    alert("文件上传成功");
                }
                $scope.closeThisDialog();
            }).error(function (data, status, headers, config) {
                alert('error status: ' + status);
            })
        }

    }
})