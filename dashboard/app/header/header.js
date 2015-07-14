'use strict';

angular.module('seacloudsDashboard.header', ["ngLoadingSpinner"])
    .controller('HeaderCtrl', function ($scope, $location, $interval, notificationService) {

        $scope.login = $scope.UserCredentials.login;
        $scope.logout = $scope.UserCredentials.logout;
        $scope.isUserAuthenticated = $scope.UserCredentials.isUserAuthenticated;

        $scope.projects = [];

        $scope.SeaCloudsApi.getProjects().
            success(function (projects) {
                $scope.projects = projects;
            }).
            error(function () {
                //TODO: Handle the error better than showing a notification
                notificationService.error("Unable to retrieve the projects");

            })

        $scope.updateFunction = undefined;

        $scope.updateFunction = $interval(function () {
            $scope.SeaCloudsApi.getProjects().
                success(function (projects) {
                    $scope.projects = projects;
                }).
                error(function () {
                    //TODO: Handle the error better than showing a notification
                    notificationService.error("Unable to retrieve the projects");

                })
        }, 5000);

        $scope.$on('$destroy', function () {
            if ($scope.updateFunction) {
                $interval.cancel($scope.updateFunction);
            }
        });

        $scope.isViewActive = function (route) {
            return $location.path().startsWith(route);
        }
    })
    .directive('header', function () {
        return {
            restrict: 'E',
            templateUrl: 'header/header.html',
            controller: 'HeaderCtrl'
        };
    });