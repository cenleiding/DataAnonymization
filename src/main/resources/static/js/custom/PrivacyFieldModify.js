var app = angular.module("privacyFieldModifyApp", ['headApp']);
app.controller("privacyFieldModifyCtrl", function($scope,$http,$timeout,$q) {

    $scope.fields;
    $scope.selectedArc;
    $scope.selectedArc_fiedlds;
    $scope.data=[
        {
            "DT_RowId":"--",
            "nodeName": "--",
            "nodePath": "--",
            "Db_field": "--",
            "En_field": "--",
            "Ch_field": "--",
            "type": "--"
        }];

    var table=$('#tempdata').DataTable();


    //获取数据
    (function () {
        $http({
            method:'GET',
            url:"/getPrivacyField",
        }).then(function successCallback(response) {
            $scope.fields=response.data;
        }, function errorCallback(response) {
            // 请求失败执行代码
        });
    })();


    //处理模板双击选择事件
     $scope.selectTemp=function(C){
         $scope.selectedArc=C;
         for(var F in $scope.fields){
             if($scope.fields[F].archetypeName===C){
                 var f=$scope.fields[F].fields;
                 for(var i in f){
                     f[i].DT_RowId=i;
                 }
                 $scope.data=f;
                 datatableDraw();
             }
         }
     }

     //保存单原型更改
    $scope.savechange=function () {
        for(var d in $scope.fields){
           if($scope.fields[d].archetypeName===$scope.selectedArc){
               $scope.fields[d].fields=[];
               table.data().each( function (q) {
                   if(q.Db_field instanceof Array==false){
                       q.Db_field=q.Db_field.split(/,|，/);
                   }
                   if(q.En_field instanceof Array==false){
                       q.En_field=q.En_field.split(/,|，/);
                   }
                   if(q.Ch_field instanceof Array==false){
                       q.Ch_field=q.Ch_field.split(/,|，/);
                   }
                   $scope.fields[d].fields.push(q);
               } );
               break;
           }
        }
        alert("保存成功！");
    }

    //提交更改
    $scope.updateFields=function(){
        $http({
            method:'POST',
            url:"/updataFields",
            data:$scope.fields,
        }).then(function successCallback(response) {
            alert(response.data);
        }, function errorCallback(response) {
            alert("更新错误");
        });

    }

     //重画datatables
     var datatableDraw =function(){

         var editor = new $.fn.dataTable.Editor( {
             data:  $scope.data,
             table: "#tempdata",
             fields: [{
                 label: "原型节点名：",
                 name: "nodeName",
             }, {
             //     label: "节点路径：",
             //     name: "nodePath"
             // },{
                 label: "数据库字段：",
                 name: "Db_field"
             }, {
                 label: "英文字段：",
                 name: "En_field"
             }, {
                 label: "中文字段：",
                 name: "Ch_field"
             }, {
                 label: "数据类型：",
                 name: "type",
                 type:"select",
                 options : [ 'Names', 'Geographic','Date','Telephone_Numbers','Fax_Numbers','Email_Addresses',
                     'Social_Security_Numbers','Medical_Record_Numbers','Health_Plan_Beneficiary_Numbers',
                     'Account_Numbers','Certificate_License_Numbers','Vehicle_Identifiers','Device_Identifiers',
                     'Url','Ip','Biometric_Identifiers','Photographs','Other_Hard','Other_Middle','Other_Soft','Unstructured_Data']
             }
             ]
         } );

         table.clear();
         table.destroy();
         table=$('#tempdata').DataTable( {
             dom: "Bfrtip",
             data:  $scope.data,
             pageLength:8,
             order: [[ 1, 'asc' ]],
             columns: [
                 {
                     data: null,
                     defaultContent: '',
                     className: 'select-checkbox',
                     orderable: false,
                 },
                 { data: "nodeName" },
                 // { data: "nodePath" },
                 { data: "Db_field" },
                 { data: "En_field" },
                 { data: "Ch_field" },
                 { data: "type" }
             ],
             select: {
                 style:    'os',
                 selector: 'td:first-child'
             },
             buttons: [
                 { extend: "create", editor: editor,text: "添加" },
                 { extend: "edit",   editor: editor,text: "编辑" },
                 { extend: "remove", editor: editor,text: "删除" },
                 { text: "保存",action:function(){$scope.savechange();}}
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