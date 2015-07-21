'use strict';

angular.module('seacloudsDashboard.about', ['ngRoute'])
    .config(['$routeProvider', function($routeProvider) {
        $routeProvider.when('/about', {
            templateUrl: 'about/about.html'
        })
    }])
    .controller('AboutCtrl', function($scope){
        $scope.Page.setTitle('SeaClouds Dashboard - About');
    });;;