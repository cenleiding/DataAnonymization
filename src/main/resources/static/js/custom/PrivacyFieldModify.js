var app = angular.module("privacyFieldModifyApp", []);
app.controller("privacyFieldModifyCtrl", function($scope,$http,$timeout,$q) {

    $scope.fields;
    $scope.selectedTemp_EN;
    $scope.selectedTemp_CH;
    $scope.selectedTemp_fiedlds;
    $scope.data=[
        {
            "DT_RowId":"--",
            "Database_field": "--",
            "English_field": "--",
            "Chinese_field": "--",
            "type": "--",
        }];
    $scope.selectedTemp_fiedlds=["字段1","字段2","字段3","字段4"];

    var editor=new $.fn.dataTable.Editor();
    var table=$('#tempdata').DataTable();


    //获取数据
    (function () {
        $http({
            method:'GET',
            url:"/PrivacyField/getPrivacyField",
        }).then(function successCallback(response) {
            $scope.fields=response.data;
        }, function errorCallback(response) {
            // 请求失败执行代码
        });
    })();


    //处理模板双击选择事件
     $scope.selectTemp=function(C,E){
         $scope.selectedTemp_EN=E;
         $scope.selectedTemp_CH=C;
         for(var F in $scope.fields){
             if($scope.fields[F].form_name===E){
                 var f=$scope.fields[F].fields;
                 for(var i in f){
                     f[i].DT_RowId=i;
                 }
                 $scope.data=f;
                 datatableDraw();
             }
         }
     }

     //保存单模板更改
    $scope.savechange=function () {
        for(var d in $scope.fields){
           if($scope.fields[d].form_name===$scope.selectedTemp_EN){
               $scope.fields[d].fields=[];
               table.data().each( function (q) {
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
            url:"/PrivacyField/updataFields",
            data:$scope.fields,
        }).then(function successCallback(response) {
            alert(response.data);
        }, function errorCallback(response) {
            alert("更新错误");
        });

    }

     //重画datatables
     var datatableDraw =function(){
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
                     orderable: false
                 },
                 { data: "Database_field" },
                 { data: "English_field" },
                 { data: "Chinese_field" },
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

         editor = new $.fn.dataTable.Editor( {
             data:  $scope.data,
             table: "#tempdata",
             fields: [ {
                 label: "数据库字段：",
                 name: "Database_field"
             }, {
                 label: "英文字段：",
                 name: "English_field"
             }, {
                 label: "中文字段：",
                 name: "Chinese_field"
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

         $('#tempdata').on( 'click', 'tbody td:not(:first-child)', function (e) {
             editor.inline( this,{
                 submitOnBlur: true
             }  );
         } );
     }

     datatableDraw();



    // $(document).ready(function() {
    //     editor = new $.fn.dataTable.Editor( {
    //         data:  $scope.data,
    //         table: "#tempdata",
    //         fields: [ {
    //             label: "数据库字段：",
    //             name: "Database_field"
    //         }, {
    //             label: "英文字段：",
    //             name: "English_field"
    //         }, {
    //             label: "中文字段：",
    //             name: "Chinese_field"
    //         }, {
    //             label: "数据类型：",
    //             name: "type"
    //         }
    //         ]
    //     } );
    //
    //     // Activate an inline edit on click of a table cell
    //     $('#tempdata').on( 'click', 'tbody td:not(:first-child)', function (e) {
    //         editor.inline( this );
    //         console.log(table.data());
    //     } );
    //
    //     table=$('#tempdata').DataTable( {
    //         dom: "Bfrtip",
    //         data:  $scope.data,
    //         order: [[ 1, 'asc' ]],
    //         columns: [
    //             {
    //                 data: null,
    //                 defaultContent: '',
    //                 className: 'select-checkbox',
    //                 orderable: false
    //             },
    //             { data: "Database_field" },
    //             { data: "English_field" },
    //             { data: "Chinese_field" },
    //             { data: "type" }
    //         ],
    //         select: {
    //             style:    'os',
    //             selector: 'td:first-child'
    //         },
    //         buttons: [
    //             { extend: "create", editor: editor, text: "添加" },
    //             { extend: "edit",   editor: editor, text: "编辑"  },
    //             { extend: "remove", editor: editor, text: "移除"  }
    //         ]
    //     } );
    // } );
});