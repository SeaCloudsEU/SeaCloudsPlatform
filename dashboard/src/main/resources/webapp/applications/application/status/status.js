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


angular.module('seacloudsDashboard.applications.application.status', ['datatables', 'angularTopologyEditor'])
    .directive('status', function () {
        return {
            scope: true,
            restrict: 'E',
            templateUrl: 'applications/application/status/status.html',
            controller: 'StatusCtrl'
        };
    })
    .controller('StatusCtrl', function ($scope, $interval, DTOptionsBuilder, notificationService) {

        $scope.dtOptions = DTOptionsBuilder.newOptions().withDisplayLength(5);

        var sensorViewVisible = false;

        $scope.isSensorViewVisible = function () {
            return sensorViewVisible;
        }

        $scope.showSensorView = function (status) {
            sensorViewVisible = status;
        }

        $scope.$watch('brooklynApplication', function(newValue, oldValue) {
            if(newValue != undefined && newValue != oldValue){
                $scope.topology = TopologyEditorUtils.getTopologyFromEntities(newValue);
            }
        });

        $scope.SeaCloudsApi.getEntitySensorMapList($scope.seaCloudsApplicationId).
            success(function (entities) {
                $scope.entities = entities;
            }).
            error(function () {
                //TODO: Handle the error better thwan showing a notification
                notificationService.error("Unable to retrieve the application sensors");
            });

    });