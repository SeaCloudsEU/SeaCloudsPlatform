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

angular.module('seacloudsDashboard.applications', ['ngRoute'])
    .config(['$routeProvider', function ($routeProvider) {
        $routeProvider.when('/applications', {
            templateUrl: 'applications/applications.html',
            controller: 'ApplicationsCtrl'
        })
    }])
    .controller('ApplicationsCtrl', function ($scope, $interval, notificationService) {

        $scope.isViewActive = function (route) {
            return $location.path().startsWith(route);
        };
        $scope.Page.setTitle('SeaClouds Dashboard - Applications overview');


        $scope.applications = [];
        $scope.updateFunction = undefined;

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
                    if(applications != $scope.applications){
                        $scope.applications = applications;
                    }
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
            if($scope.updateFunction){
                $interval.cancel($scope.updateFunction);
            }
        });



        var slaFULFILLED = "The application is satisfying the SLA's";
        var slaVIOLATED = "There are some violations in SLA's";
        var slaUNDETERMINED = "The SLA's status is undetermined";

        $scope.getSLATooltip = function (status) {
            switch(status) {
                case 'FULFILLED':
                    return slaFULFILLED;
                case 'VIOLATED':
                    return slaVIOLATED;
                default:
                    return slaUNDETERMINED;
            }
        }

        var statusRUNNING = "The application is running";
        var statusSTARTING = "The application is starting";
        var statusSTOPPING = "The application is stopping";
        var statusERROR = "The application failed during the execution";

        $scope.getStatusTooltip = function (status) {
            switch(status){
                case 'RUNNING':
                    return statusRUNNING;
                case 'STARTING':
                    return statusSTARTING;
                case 'STOPPING':
                    return statusSTOPPING;
                default:
                    return statusERROR;
            }
        }
    });