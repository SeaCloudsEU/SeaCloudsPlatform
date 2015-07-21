'use strict';

angular.module('seacloudsDashboard.signin', ['ngRoute'])
    .config(['$routeProvider', function($routeProvider) {
        $routeProvider.when('/signin', {
            templateUrl: 'signin/signin.html'
        })
    }])
    .controller('SigninCtrl', function($scope, $location){
        $scope.userCredentials = {email : undefined, password : undefined};
        $scope.login = $scope.UserCredentials.login;
        $scope.Page.setTitle('SeaClouds Dashboard - Sign In');
    });;