var app = angular.module("expandNodeModifyApp", ['angular-popups']);
app.controller("expandNodeModifyCtrl", function($scope,$http,$timeout,$q) {

    $scope.selectFile="";
    $scope.fileList;
    $scope.selectFrom="";
    $scope.fromList;
    $scope.data=[
        {
            "expandName":"--",
            "fromName":"--",
            "en_name":"--",
            "ch_name":"--",
            "description":"--",
            "nodeType": "--",
            "id":"--"
        }];

    $("#Select").selectpicker({
        noneSelectedText : '请选择'
    });

    $('#Select').on('changed.bs.select', function (e,c) {
        $scope.selectFile=$scope.fileList[c-1];
        $scope.selectFrom="";
        $scope.getFromList();
    });

    var table=$('#tempdata').DataTable();

    //获取数据
    (function () {
        $http({
            method:'GET',
            url:"/getExpandFileName",
        }).then(function successCallback(response) {
            $scope.fileList=response.data;
            var select = $("#Select");
            select.append("<option value='un' disabled='disabled'>文件选择</option>");
            for (var i = 0; i < $scope.fileList.length; i++) {
                select.append("<option value='"+$scope.fileList[i]+"'>"
                    + $scope.fileList[i] + "</option>");
            }
            $('#Select').selectpicker('val','un');
            $('#Select').selectpicker('refresh');
        }, function errorCallback(response) {
            // 请求失败执行代码
        });
    })();

    //获取对应文件表单信息
    $scope.getFromList=function () {
        $http({
            method:'GET',
            url:"/getExpandFromNameByFileName",
            params:{fileName:$scope.selectFile}
        }).then(function successCallback(response) {
            $scope.fromList=response.data;
        }, function errorCallback(response) {
            // 请求失败执行代码
        });
    }
    
    //处理模板双击选择事件
    $scope.selectTemp=function(C){
        $scope.selectFrom=C;
        $http({
            method:'GET',
            url:"/getExpandNodeInfoByName",
            params:{fileName:$scope.selectFile,
                    fromName:$scope.selectFrom}
        }).then(function successCallback(response) {
            $scope.data=response.data;
            datatableDraw();
        }, function errorCallback(response) {
            // 请求失败执行代码
        });
    }

    //保存原型更改
    $scope.saveChange=function () {
        if($scope.selectFrom==="") alert("请选择原型！");
        else {
            $scope.data=[];
            table.data().each( function (q) {
                $scope.data.push(q);
            });
            $http({
                method:'POST',
                url:"/updataExpandNodeInfo",
                data:$scope.data,
                params:{fileName:$scope.selectFile,
                        fromName:$scope.selectFrom}
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
                label: "节点英文名：",
                name: "en_name",
            }, {
                label: "节点中文名：",
                name: "ch_name",
            },{
                label: "节点含义：",
                name: "description",
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
                { data: "en_name" },
                { data: "ch_name" },
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