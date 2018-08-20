var app = angular.module("myFieldFormApp", ['headApp','ngDialog','createNewFormApp']);
app.controller("myFieldFormCtrl", function($scope,$http,ngDialog) {

    $scope.formNameList=[];
    $scope.fieldData=[];
    $scope.dataTableSet_ALL={'ALL':[]};
    $scope.selectType=["ALL","EI","QI_Link","QI_Geography","QI_DateRecord","QI_DateAge","QI_Number","QI_String","SI_Number","SI_String","UI"];

    $scope.description="";
    $scope.createTime="";
    $scope.lastChangeTime="";
    $scope.father="";
    $scope.usageCount="";
    $scope.userName="";

    var percentage={"SUM":0}
    var Accounting=[
        ["EI",0],
        ["QI_Link",0],
        ["QI_Geography",0],
        ["QI_DateRecord",0],
        ["QI_DateAge",0],
        ["QI_Number",0],
        ["QI_String",0],
        ["SI_Number",0],
        ["SI_String",0],
        ["UI",0]
    ];

    $scope.fieldChangeLog=[];

    var table=$('#table').DataTable();



    (function () {
        $http(
            {
                url:"/MyFieldForm/getFieldFormName",
                method:"GET"
            }
        ).then(
            function success(response) {
                $scope.formNameList=response.data;
            },
            function error() {
                alert("获取列表失败！")
            }
        )

    })()

    $("#typeSelect").on('changed.bs.select',function (e,c){
        $scope.fieldData=[]
    })

    var getFieldFormInfoByFormName=function (formName) {
        $http(
            {
                url:"/MyFieldForm/getFieldFormInfoByFormName",
                method:"GET",
                params:{formName:formName}
            }
        ).then(
            function success(response) {
                formNameInfo=response.data;
                $scope.description=formNameInfo.description
                $scope.createTime=formNameInfo.createTime
                $scope.lastChangeTime=formNameInfo.lastChangeTime
                $scope.father=formNameInfo.father
                $scope.usageCount=formNameInfo.usageCount
                $scope.userName=formNameInfo.userName
            },
            function error() {
                alert("获取列表信息失败！")
            }
        )
    }

    var getFieldDetailByFormName=function (formName) {
        $http(
            {
                url:"/MyFieldForm/getFieldDetailByFormName",
                method:"GET",
                params:{formName:formName}
            }
        ).then(
            function success(response) {
                var data=response.data;
                var all=[];
                var id=0;
                for(var i in data){
                    var t=[];
                    for(var j in data[i]){
                        id=id+1;
                        var d={};
                        d.fieldName=data[i][j];
                        d.fieldType=i;
                        d.id=id;
                        t.push(d);
                    }
                    all=all.concat(t);
                    $scope.dataTableSet_ALL[i]=t;
                }
                $scope.dataTableSet_ALL['ALL']=all;
                datatableDraw('ALL')
            },
            function error() {
                alert("获取列表信息失败！")
            }
        )
    }
    
    var getFieldOverViewByFormName=function (formName) {
        $http({
            method:'GET',
            url:"/MyFieldForm/getFieldOverViewByFormName",
            params:{formName: formName}
        }).then(function successCallback(response) {
            percentage=response.data;
            for(var a in Accounting){
                Accounting[a][1]=percentage[Accounting[a][0]];
            }
            createcharts();
        }, function errorCallback(response) {
            // 请求失败执行代码
        });
        
    }

    var getFieldChangeLogByFormName=function (formName) {
        $http({
            method:'GET',
            url:"/MyFieldForm/getFieldChangeLogByFormName",
            params:{formName: formName}
        }).then(function successCallback(response) {
            $scope.fieldChangeLog=response.data;
        }, function errorCallback(response) {
            // 请求失败执行代码
        });
    }



    $scope.createNewForm=function () {
        ngDialog.open({
            template: '/htmlTemplates/createNewForm.html',
            className: 'ngdialog-theme-default',
            width:540,
            height: 400,});
    }

    $scope.lookChangeLog=function(changeLog){
        alert("修改:\n"+changeLog);
    }

    $scope.editForm=function (formName) {
        getFieldFormInfoByFormName(formName);
        getFieldDetailByFormName(formName);
        getFieldOverViewByFormName(formName);
        getFieldChangeLogByFormName(formName);
    }

    $scope.deleteForm=function(formName){
        alert(formName);
    }


    //重画datatables
    var datatableDraw =function(type) {

        var editor = new $.fn.dataTable.Editor({
            data: $scope.dataTableSet_ALL[type],
            table: "#table",
            idSrc: 'id',
            fields: [{
                label: "字段名：",
                name: "fieldName",
            }, {
                label: "字段类型：",
                name: "fieldType",
                type: "select",
                options: ['EI', 'QI_Link', 'QI_Geography', 'QI_DateRecord','QI_DateAge','QI_Number','QI_String','SI_Number','SI_String','UI']
            }]
        });

        table.clear();
        table.destroy();


        table = $('#table').DataTable({
            dom: "Bfrtip",
            data: $scope.dataTableSet_ALL[type],
            pageLength: 10,
            order: [[1, 'asc']],
            columns: [
                {
                    data: null,
                    defaultContent: '',
                    className: 'select-checkbox',
                    orderable: false,
                },
                {data: "fieldName"},
                {data: "fieldType"}
            ],
            select: {
                style: 'os',
                selector: 'td:first-child'
            },
            buttons: [
                { extend: "create", editor: editor,text: "添加" },
                { extend: "edit",   editor: editor,text: "编辑" },
                { extend: "remove", editor: editor,text: "删除" },
                { text: "保存",action:function(){$scope.dialogShow();}}
            ]
        });

        $('#table').on('click', 'tbody td:not(:first-child)', function (e) {
            editor.inline(this, {
                submitOnBlur: true
            });
        });
    }

    //highcharts 图表
    var createcharts=function () {
        $('#container').highcharts({
            credits:{enabled:false},
            chart: {
                plotBackgroundColor: null,
                plotBorderWidth: null,
                plotShadow: false,
            },
            title: {
                text: '各类型节点占比<br>节点总数：'+percentage.SUM+''
            },
            tooltip: {
                headerFormat: '{series.name}<br>',
                pointFormat: '{point.name}: <b>{point.percentage:.1f}%</b>'
            },
            plotOptions: {
                pie: {
                    allowPointSelect: true,
                    cursor: 'pointer',
                    dataLabels: {
                        enabled: true,
                        format: '<b>{point.name} </b>: {point.percentage:.1f} %',
                        style: {
                            color: (Highcharts.theme && Highcharts.theme.contrastTextColor) || 'black'
                        }
                    }
                }
            },
            series: [{
                type: 'pie',
                name: '字段占比',
                data: Accounting,
            }]
        });
    };


    datatableDraw();
    createcharts();


})