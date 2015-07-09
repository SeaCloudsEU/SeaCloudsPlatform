/*
 *  Copyright 2014 SeaClouds
 *  Contact: SeaClouds
 *
 *      Licensed under the Apache License, Version 2.0 (the "License");
 *      you may not use this file except in compliance with the License.
 *      You may obtain a copy of the License at
 *
 *          http://www.apache.org/licenses/LICENSE-2.0
 *
 *      Unless required by applicable law or agreed to in writing, software
 *      distributed under the License is distributed on an "AS IS" BASIS,
 *      WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *      See the License for the specific language governing permissions and
 *      limitations under the License.
 */

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