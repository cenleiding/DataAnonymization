var app = angular.module("loginApp", ['navigationBarApp','registeredApp','ngDialog']);
app.controller("loginCtrl", function($scope,ngDialog) {
    $scope.registered = function () {
        ngDialog.open({
            template: '/htmlTemplates/Registered.html',
            className: 'ngdialog-theme-default' ,
            width:380,
            height: 280});
    };
    }
)