var app = angular.module("loginApp", ['headApp','registeredApp','ngDialog']);
app.controller("loginCtrl", function($scope,ngDialog) {
    $scope.registered = function () {
        ngDialog.open({ template: '/htmlTemplates/registered.html', className: 'ngdialog-theme-default' });
    };
    }
)