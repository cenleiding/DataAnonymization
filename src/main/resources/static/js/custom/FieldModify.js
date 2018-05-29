var app = angular.module("fieldModifyApp", ['angular-popups']);
app.controller("fieldModifyCtrl", function($scope,$http,$timeout,$q) {

    $scope.fromName;
    $scope.selectFrom="";
    $scope.newFromName="";
    $scope.EIfield=[{"fieldName":"--","fieldType":"--"}];
    $scope.QIfield=[{"fieldName":"--","fieldType":"--"}];
    $scope.SIfield=[{"fieldName":"--","fieldType":"--"}];
    $scope.UIfield=[{"fieldName":"--","fieldType":"--"}];

    var EItable=$('#EItable').DataTable();
    var QItable=$('#QItable').DataTable();
    var SItable=$('#SItable').DataTable();
    var UItable=$('#UItable').DataTable();

    $(".selectpicker").selectpicker({
        noneSelectedText : '请选择'
    });
    $('#Select').on('changed.bs.select', function (e,c) {
        $scope.selectFrom=$scope.fromName[c-1];
        $scope.newFromName=$scope.selectFrom;
        $scope.getField($scope.fromName[c-1]);
    });

    (function () {
        $http({
            method:'GET',
            url:"/getFromNameList"
        }).then(
            function successCallback (response) {
                $scope.fromName=response.data;
                var select = $("#Select");
                select.append("<option value='un' disabled='disabled'>表单选择</option>");
                for (var i = 0; i < $scope.fromName.length; i++) {
                    select.append("<option value='"+$scope.fromName[i]+"'>"
                        + $scope.fromName[i] + "</option>");
                }
                $('.selectpicker').selectpicker('val','un');
                $('.selectpicker').selectpicker('refresh');
            },
            function errorCallback (response) {
                console.log("获取字段表单失败！");
            }
        );
    })();



    $scope.getField=function(fromName){
        $http({
            method:"GET",
            url:"/getFieldByFromName",
            params:{fromName:fromName}
            }
        ).then(
            function successCallback (response) {
                var allField=response.data;
                $scope.EIfield=[];
                $scope.QIfield=[];
                $scope.SIfield=[];
                $scope.UIfield=[];
                for(var i=0;i<allField.length;i++){
                    if(allField[i].fieldType.indexOf("EI")!=-1)$scope.EIfield.push(allField[i]);
                    if(allField[i].fieldType.indexOf("QI")!=-1)$scope.QIfield.push(allField[i]);
                    if(allField[i].fieldType.indexOf("SI")!=-1)$scope.SIfield.push(allField[i]);
                    if(allField[i].fieldType.indexOf("UI")!=-1)$scope.UIfield.push(allField[i]);
                }
                datatableDraw();
            },
            function errorCallback(response) {
                console.log("获取表单字段失败");
            }
        );

    }

    $scope.saveChange=function () {
        var allField=[];
        EItable.data().each( function (q) {
            var p=q;
            p.fieldType="EI";
            p.fromName=$scope.selectFrom;
            allField.push(p);
        });
        QItable.data().each(function (q) {
            var p=q;
            p.fromName=$scope.selectFrom;
            allField.push(p);
        });
        SItable.data().each(function (q) {
            var p=q;
            p.fromName=$scope.selectFrom;
            allField.push(p)
        });
        UItable.data().each(function (q) {
            var p=q;
            p.fieldType="UI";
            p.fromName=$scope.selectFrom;
            allField.push(p)
        });
        $http({
            method:"POST",
            url:"/updataField",
            data:allField,
            params:{newFromName:$scope.newFromName}
        }).then(
            function success(response) {

                alert(response.data);
            },
            function errorCallBack(response){
                alert(response.data);
            }
        );
    }

    $scope.deleteFrom=function () {
        if($scope.selectFrom===""){
            alert("请选择需要删除的字段表！");
        }
        else if($scope.selectFrom==="Original"){
            alert("Original字段表为基础字段表，无法删除！");
        } else{
            $http({
                method:"GET",
                url:"/deleteFromByName",
                params:{fromName:$scope.selectFrom}
            }).then(
                function success(response) {
                    alert("删除成功");
                },
                function errorCallBack(response){
                    alert("删除失败");
                }
            );
        }
    }

    $scope.creatFrom=function () {
        if($scope.selectFrom==="") alert("请选择一张表作为模板表！");
        else{
            $http({
                    method:"GET",
                    url:"/getFieldByFromName",
                    params:{fromName:$scope.selectFrom}
                }
            ).then(
                function success(response){
                    var fname=$scope.newFromName+Date.parse(new Date());
                    var f=response.data;
                    for(var i in f){
                        f[i].fromName=fname;
                    }
                    $http({
                        method:"POST",
                        url:"/updataField",
                        data:response.data,
                        params:{newFromName:fname}
                    }).then(
                        function success(response) {
                            alert(response.data);
                        },
                        function errorCallBack(response){
                            alert("新表创建失败！");
                        }
                    );
                },
                function errorCallBack(response) {
                    alert("新表创建失败！")
                }
            )
        }
    }

    //重画datatables
    var datatableDraw =function(){

        var EIeditor = new $.fn.dataTable.Editor( {
            data:  $scope.EIfield,
            table: "#EItable",
            idSrc:  'id',
            fields: [{
                label: "字段名：",
                name: "fieldName",
            }]
        } );

        var UIeditor = new $.fn.dataTable.Editor( {
            data:  $scope.UIfield,
            table: "#UItable",
            idSrc:  'id',
            fields: [{
                label: "字段名：",
                name: "fieldName",
            }]
        } );

        var QIeditor = new $.fn.dataTable.Editor( {
            data:  $scope.QIfield,
            table: "#QItable",
            idSrc:  'id',
            fields: [{
                label: "字段名：",
                name: "fieldName",
            },{
                label: "字段类型：",
                name: "fieldType",
                type:"select",
                options:['QI_Geographic','QI_Date','QI_Number','QI_String']
            }]
        } );

        var SIeditor = new $.fn.dataTable.Editor( {
            data:  $scope.SIfield,
            table: "#SItable",
            idSrc:  'id',
            fields: [{
                label: "字段名：",
                name: "fieldName",
            },{
                label: "字段类型：",
                name: "fieldType",
                type:"select",
                options:['SI_Number','SI_String']
            }]
        } );


        EItable.clear();
        EItable.destroy();
        UItable.clear();
        UItable.destroy();
        QItable.clear();
        QItable.destroy();
        SItable.clear();
        SItable.destroy();

        EItable=$('#EItable').DataTable( {
            dom: "Bfrtip",
            data:  $scope.EIfield,
            pageLength:8,
            order: [[ 1, 'asc' ]],
            columns: [
                {
                    data: null,
                    defaultContent: '',
                    className: 'select-checkbox',
                    orderable: false,
                },
                { data: "fieldName" }
            ],
            select: {
                style:    'os',
                selector: 'td:first-child'
            },
            buttons: [
                { extend: "create", editor: EIeditor,text: "添加" },
                { extend: "remove", editor: EIeditor,text: "删除" }
            ]
        } );

        UItable=$('#UItable').DataTable( {
            dom: "Bfrtip",
            data:  $scope.UIfield,
            pageLength:8,
            order: [[ 1, 'asc' ]],
            columns: [
                {
                    data: null,
                    defaultContent: '',
                    className: 'select-checkbox',
                    orderable: false,
                },
                { data: "fieldName" }
            ],
            select: {
                style:    'os',
                selector: 'td:first-child'
            },
            buttons: [
                { extend: "create", editor: UIeditor,text: "添加" },
                { extend: "remove", editor: UIeditor,text: "删除" }
            ]
        } );

        QItable=$('#QItable').DataTable( {
            dom: "Bfrtip",
            data:  $scope.QIfield,
            pageLength:8,
            order: [[ 1, 'asc' ]],
            columns: [
                {
                    data: null,
                    defaultContent: '',
                    className: 'select-checkbox',
                    orderable: false,
                },
                { data: "fieldName" },
                { data: "fieldType" }
            ],
            select: {
                style:    'os',
                selector: 'td:first-child'
            },
            buttons: [
                { extend: "create", editor: QIeditor,text: "添加" },
                { extend: "remove", editor: QIeditor,text: "删除" }
            ]
        } );

        SItable=$('#SItable').DataTable( {
            dom: "Bfrtip",
            data:  $scope.SIfield,
            pageLength:8,
            order: [[ 1, 'asc' ]],
            columns: [
                {
                    data: null,
                    defaultContent: '',
                    className: 'select-checkbox',
                    orderable: false,
                },
                { data: "fieldName" },
                { data: "fieldType" }
            ],
            select: {
                style:    'os',
                selector: 'td:first-child'
            },
            buttons: [
                { extend: "create", editor: SIeditor,text: "添加" },
                { extend: "remove", editor: SIeditor,text: "删除" }
            ]
        } );


        $('#EItable').on( 'click', 'tbody td:not(:first-child)', function (e) {
            EIeditor.inline( this,{
                submitOnBlur: true
            }  );
        } );

        $('#UItable').on( 'click', 'tbody td:not(:first-child)', function (e) {
            UIeditor.inline( this,{
                submitOnBlur: true
            }  );
        } );

        $('#QItable').on( 'click', 'tbody td:not(:first-child)', function (e) {
            QIeditor.inline( this,{
                submitOnBlur: true
            }  );
        } );

        $('#SItable').on( 'click', 'tbody td:not(:first-child)', function (e) {
            SIeditor.inline( this,{
                submitOnBlur: true
            }  );
        } );
    }

    datatableDraw();
});