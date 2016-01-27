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

angular.module('seacloudsDashboard.header', [])
    .directive('header', function () {
        return {
            restrict: 'E',
            templateUrl: 'header/header.html',
            controller: 'HeaderCtrl'
        };
    })
    .controller('HeaderCtrl', function ($scope, $location, $interval, notificationService) {

        $scope.applications = [];

        $scope.SeaCloudsApi.getAboutSeaClouds().success(function (endpoints){
            $scope.endpoints = endpoints;
        }).error(function () {
            notificationService.error("SeaClouds server seems to be down. Please refresh the browser");
        })

        $scope.SeaCloudsApi.getApplications().
            success(function (applications) {
                $scope.applications = applications;
            }).
            error(function () {
                notificationService.error("Unable to retrieve the applications");
            })

        var retrievalErrors = 0;
        $scope.updateFunction = $interval(function () {
            $scope.SeaCloudsApi.getApplications().
                success(function (applications) {
                    $scope.applications = applications;
                }).
                error(function () {
                    retrievalErrors++;
                    if(retrievalErrors > 3) {
                        $interval.cancel($scope.updateFunction);
                        $scope.updateFunction = undefined;
                        notificationService.error("It was not possible to retrieve the Applications after several attempts. Disabling automatic refresh.");
                    }
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
    });