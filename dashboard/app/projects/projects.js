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

angular.module('seacloudsDashboard.projects', ['ngRoute', 'ngFitText',
    'seacloudsDashboard.projects.project'])
    .config(['$routeProvider', function ($routeProvider) {
        $routeProvider.when('/projects', {
            templateUrl: 'projects/projects.html'
        })
    }])
    .controller('ProjectsCtrl', function ($scope, $interval) {

        $scope.isViewActive = function (route) {
            return $location.path().startsWith(route);
        };
        $scope.Page.setTitle('SeaClouds Dashboard - SeaCloudsApi overview');


        $scope.projects = [];
        $scope.updateFunction = undefined;

        $scope.SeaCloudsApi.getProjects().
            success(function (projects) {
                $scope.projects = projects;
            }).
            error(function () {
                //TODO: Handle the error better than showing a notification
                notificationService.error("Unable to retrieve the projects");

            })

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
            if($scope.updateFunction){
                $interval.cancel($scope.updateFunction);
            }
        });


        var appUp = "The application is up";
        var appDown = "The application is down";
        $scope.getUpTooltip = function (project){
            if(project.serviceUp){
                return appUp;
            }else{
                return appDown;
            }
        }

        var slaOK = "The application is satisfying the SLA's";
        var slaFAILED = "There are some violations in SLA's";

        $scope.getSLATooltip = function (project) {
            if (project.slaStatus == "OK") {
                return slaOK;
            } else {
                return slaFAILED;
            }
        }

        var statusOK = "The application is running";
        var statusSTARTING = "The application is starting";
        var statusRECONFIGURING = "The application needs to be reconfigured";
        var statusFAILURE = "The application failed during the execution";

        $scope.getStatusTooltip = function (project) {
            if (project.serviceState == "running") {
                return statusOK;
            } else if (project.serviceState == "starting") {
                return statusSTARTING;
            } else if (project.serviceState == "reconfiguring") {
                return statusRECONFIGURING;
            } else {
                return statusFAILURE;
            }

        }
    });