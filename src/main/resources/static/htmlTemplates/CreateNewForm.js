var app = angular.module("createNewFormApp", []);
app.controller("createNewFormCtrl", function($scope,$http) {


    $scope.fieldFormInfo=[];
    $scope.description="";
    $scope.formNewName="";


    (function () {
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
                $("#Select").append("<option value='un' disabled='disabled'  data-icon='glyphicon glyphicon-user' style='background: #5cb85c; color: #fff;'>系统样本</option>");
                $scope.fieldFormInfo.push("");
                tra("",formMap)
                for(var i in userNameMap){
                    if (i=="") continue;
                    $("#Select").append("<option value='un' disabled='disabled'  data-icon='glyphicon glyphicon-user' style='background: #5cb85c; color: #fff;'>"+i+"</option>");
                    $scope.fieldFormInfo.push(i);
                    tra(i,formMap)
                }
                $("#Select").selectpicker('val', $scope.fieldFormInfo[1].formName);
                $("#Select").selectpicker('refresh');
            },
            function errorCallback(response){
                alert("获取列表失败！")
            }
        )
    })()

    var tra=function (i,formMap) {
        for(var j in formMap){
            if(formMap[j].userName==(i)){
                $("#Select").append("<option value='"+formMap[j].formName+"' data-icon='glyphicon glyphicon-file' data-subtext=(使用次数："+formMap[j].usageCount+")>"
                    +formMap[j].formName+ "</option>");
                var info={};
                info['formName']=formMap[j].formName;
                info['description']=formMap[j].description;
                info['createTime']=formMap[j].createTime;
                info['lastChangeTime']=formMap[j].lastChangeTime;
                info['father']=formMap[j].father;
                info['usageCount']=formMap[j].usageCount;
                info['userName']=formMap[j].userName;
                $scope.fieldFormInfo.push(info);
            }
        }
    }

    $scope.create=function () {
        if($scope.formNewName.trim()==="") alert("新表名不能为空！");
        else {
            $http({
                url:"/MyFieldForm/createForm",
                method:"GET",
                params:{formName:$scope.formNewName,
                    father:$scope.fieldFormInfo[$("#Select")[0].selectedIndex]['formName'],
                    description:$scope.description}
            }).then(
                function success(response) {
                    if(response.data[0]==="添加成功！")
                        $scope.closeThisDialog()
                    else alert(response.data);
                },
                function error(response) {}
            )
        }
    }
})
