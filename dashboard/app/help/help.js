'use strict';

angular.module('seacloudsDashboard.help', ['ngRoute'])
    .config(['$routeProvider', function($routeProvider) {
        $routeProvider.when('/help', {
            templateUrl: 'help/help.html'
        })
    }])
    .controller('HelpCtrl', function($scope){
        $scope.Page.setTitle('SeaClouds Dashboard - Help');
    });;