var app = angular.module("createNewFormApp", []);
app.controller("createNewFormCtrl", function($scope,$http) {


    $scope.selectInfo=[];
    $scope.selectFormName="";
    $scope.description="";
    $scope.formNewName="";

    (function () {
        $http(
            {
                method:"GET",
                url:"/getFieldFormInfo"
            }
        ).then(
            function successCallback(response){
                var select = $("#Select");
                var formMap=response.data;
                var userNameMap={};
                var info;
                for(var i in formMap)
                    userNameMap[formMap[i].userName]="";
                for(var i in userNameMap){
                    var userName=i;
                    if (userName=="") userName="系统样本";
                    select.append("<option value='un' disabled='disabled'  data-icon='glyphicon glyphicon-user' style='background: #5cb85c; color: #fff;'>"+userName+"</option>");
                    $scope.selectInfo.push(i);
                    for(var j in formMap){
                        if(formMap[j].userName==(i)){
                            select.append("<option value='"+formMap[j].formName+"' data-icon='glyphicon glyphicon-file' >"
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

                $("#Select").selectpicker('refresh');
                $("#Select").selectpicker('val', $scope.selectInfo[1].formName);
                $scope.selectFormName=$scope.selectInfo[1].formName;
                $scope.formNewName=$scope.selectInfo[1].formName+"_"+Date.parse(new Date());
            },
            function errorCallback(response){
                alert("获取列表失败！")
            }
        )
    })()

    $( document ).ready(function() {
        $('#Select').on('changed.bs.select',function (e,c) {
            $scope.selectFormName=$scope.selectInfo[c].formName;
            $scope.formNewName=$scope.selectInfo[c].formName+"_"+Date.parse(new Date());
            $scope.$digest();
        })
    });

    $scope.create=function () {
        $http({
            url:"/MyFieldForm/createForm",
            method:"GET",
            params:{formName:$scope.formNewName,
                    father:$scope.selectFormName,
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
})