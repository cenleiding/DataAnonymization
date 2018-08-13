var app = angular.module("archetypeNodeModifyApp", ['angular-popups','headApp']);
app.controller("archetypeNodeModifyCtrl", function($scope,$http,$timeout,$q) {

    $scope.archetypeList;
    $scope.selectedArc="";
    $scope.data=[
        {
            "archetypeName":"--",
            "nodeName":"--",
            "nodePath": "--",
            "description": "--",
            "nodeType": "--",
            "id":"--"
        }];

    var table=$('#tempdata').DataTable();

    //获取数据
    (function () {
        $http({
            method:'GET',
            url:"/GetArchetypeName",
        }).then(function successCallback(response) {
            $scope.archetypeList=response.data;
        }, function errorCallback(response) {
            // 请求失败执行代码
        });
    })();

    //处理模板双击选择事件
    $scope.selectTemp=function(C){
        $scope.selectedArc=C;
        $http({
            method:'GET',
            url:"/GetArchetypeNodeInfoByName",
            params:{archetypeName:C}
        }).then(function successCallback(response) {
            $scope.data=response.data;
            datatableDraw();
        }, function errorCallback(response) {
            // 请求失败执行代码
        });
    }

    //保存原型更改
    $scope.saveChange=function () {
        if($scope.selectedArc==="") alert("请选择原型！");
        else {
            $scope.data=[];
            table.data().each( function (q) {
                $scope.data.push(q);
            });
            $http({
                method:'POST',
                url:"/UpdataArchetypeNodeInfo",
                data:$scope.data,
                params:{archetypeNmae:$scope.selectedArc}
            }).then(function successCallback(response) {
                alert(response.data);
            }, function errorCallback(response) {
                alert(response.data);
            });
        }
    }

    $scope.dialogShow=function () {
        $scope.sc={"open":true};
        $scope.$apply();
    }

    //重画datatables
    var datatableDraw =function(){

        var editor = new $.fn.dataTable.Editor( {
            data:  $scope.data,
            table: "#tempdata",
            idSrc:  'id',
            fields: [{
                label: "原型节点名：",
                name: "nodeName",
            }, {
                label: "原型节点路径：",
                name: "nodePath"
            }, {
                label: "原型节点含义：",
                name: "description"
            }, {
                label: "隐私等级：",
                name: "nodeType",
                type:"select",
                options : ['EI','QI_Geographic','QI_Data','QI_Number','QI_String','SI_Number','SI_String','UI']
            }
            ]
        } );

        table.clear();
        table.destroy();
        table=$('#tempdata').DataTable( {
            dom: "Bfrtip",
            data:  $scope.data,
            pageLength:15,
            order: [[ 1, 'asc' ]],
            columns: [
                {
                    data: null,
                    defaultContent: '',
                    className: 'select-checkbox',
                    orderable: false,
                },
                { data: "nodeName" },
                { data: "nodePath" },
                { data: "description" },
                { data: "nodeType" }
            ],
            select: {
                style:    'os',
                selector: 'td:first-child'
            },
            buttons: [
                { extend: "create", editor: editor,text: "添加" },
                { extend: "edit",   editor: editor,text: "编辑" },
                { extend: "remove", editor: editor,text: "删除" },
                { text: "提交",action:function(){$scope.dialogShow();}}
            ]
        } );


        $('#tempdata').on( 'click', 'tbody td:not(:first-child)', function (e) {
            editor.inline( this,{
                submitOnBlur: true
            }  );
        } );
    }

    datatableDraw();
});