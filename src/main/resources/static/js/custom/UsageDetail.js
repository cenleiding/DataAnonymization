var app = angular.module("usageDetailApp", []);
app.controller("usageDetailCtrl", function($scope,$http,$timeout,$q) {

    $scope.userIp;
    $scope.usage;

    $(".selectpicker").selectpicker({
        noneSelectedText : '请选择'
    });
    $('#Select').on('changed.bs.select', function (e,c) {
        getUsageByIp($scope.userIps[c-1]);
    });

    (function () {
       $http({
           method:'GET',
           url:"/getUserIp"
       }).then(
          function successCallback (response) {
              $scope.userIps=response.data;
              var select = $("#Select");
              select.append("<option value='un' disabled='disabled'>Ip选择</option>");
              for (var i = 0; i < $scope.userIps.length; i++) {
                  select.append("<option value='"+$scope.userIps[i]+"'>"
                      + $scope.userIps[i] + "</option>");
              }
              $('.selectpicker').selectpicker('val','un');
              $('.selectpicker').selectpicker('refresh');
          },
          function errorCallback (response) {
              console.log("获取使用者Ip失败！");
          }
       );
    })();

    var table = $("#list-table").DataTable({
        lengthChange: false,
        language :{
            "decimal":        "",
            "emptyTable":     "表中没有数据",
            "info":           "第 _START_ 到 _END_ 条（总共 _TOTAL_ 条记录）",
            "infoEmpty":      "无有效信息",
            "infoFiltered":   "(从 _MAX_ 条记录过滤)",
            "infoPostFix":    "",
            "thousands":      ",",
            "lengthMenu":     "每页 _MENU_ 记录",
            "loadingRecords": "载入中...",
            "processing":     "处理中...",
            "search":         "搜索:",
            "zeroRecords":    "未找到记录",
            "paginate": {
                "first":      "首页",
                "last":       "末页",
                "next":       "下一页",
                "previous":   "上一页"
            },
            "aria": {
                "sortAscending":  ": 升序排列",
                "sortDescending": ": 降序排列"
            }
        }
    });

    $('#list-table tbody').on('click', 'td.details-control', function () {
        var tr = $(this).closest('tr');
        var row = table.row( tr );

        if ( row.child.isShown() ) {
            row.child.hide();
            tr.removeClass('shown');
        }
        else {
            row.child(format(row.data()) ).show();
            tr.addClass('shown');
        }
    } );

    function format ( d ) {
        var num=0;
        var html='';
        var a=d.field.split(';');
        while ((num+4)<=a.length){
            html+='<div class="row">'
            for(var i=0;i<4;i++){
                html+='<div class="col-lg-3">'+a[num]+'</div>'
                num=num+1;
            }
            html+='</div>'
        }


        return html;
    }

    var getUsageByIp=function (ip) {
        $http({
            method:'GET',
            url:"/getUsageByIp",
            params:{Ip:ip}
        }).then(
            function (response) {
                $scope.usage=response.data;
                table.clear();
                table.destroy();
                table = $("#list-table").DataTable({
                    lengthChange: false,
                    iDisplayLength:10,
                    //deferRender: true,
                    language :{
                        "decimal":        "",
                        "emptyTable":     "表中没有数据",
                        "info":           "第 _START_ 到 _END_ 条（总共 _TOTAL_ 条记录）",
                        "infoEmpty":      "无有效信息",
                        "infoFiltered":   "(从 _MAX_ 条记录过滤)",
                        "infoPostFix":    "",
                        "thousands":      ",",
                        "lengthMenu":     "每页 _MENU_ 记录",
                        "loadingRecords": "载入中...",
                        "processing":     "处理中...",
                        "search":         "搜索:",
                        "zeroRecords":    "未找到记录",
                        "paginate": {
                            "first":      "首页",
                            "last":       "末页",
                            "next":       "下一页",
                            "previous":   "上一页"
                        },
                        "aria": {
                            "sortAscending":  ": 升序排列",
                            "sortDescending": ": 降序排列"
                        }
                    },
                    data:$scope.usage,
                    autoWidth: false,
                    columns: [
                        { data: 'id',width:'30px'},
                        { data: 'time' ,width:'130px'},
                        { data: 'num',width:'50px' },
                        { data: 'method',width:'70px' },
                        {
                            "className":      'details-control',
                            "orderable":      false,
                            "data":           null,
                            "defaultContent": '',
                            "width":'50px'
                        },
                    ]
                });
            },
            function (response) {
                console.log("获取使用记录失败");
            }
        )
        
    }





});