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

angular.module('seacloudsDashboard.applications.application.monitor', ['datatables', 'chart.js', 'ui.codemirror'])
    .directive('monitor', function () {
        return {
            scope: true,
            restrict: 'E',
            templateUrl: 'applications/application/monitor/monitor.html',
            controller: 'MonitorCtrl'
        };
    })
    .controller('MonitorCtrl', function ($scope, $interval, $filter, DTOptionsBuilder, notificationService) {

        $scope.dtOptions = DTOptionsBuilder.newOptions().withDisplayLength(5);

        $scope.chartOptions ={
            maintainAspectRatio: false,
            responsive: true
        }
        $scope.rawMonitoringRulesViewerOptions = {
            readOnly: true,
            mode: 'xml',
            lineNumbers: true
        };

        $scope.entities = [];
        $scope.codemirror.monitoringRules = "Monitoring Rules visualizer not ready yet.";

        var selectedTab = 0;

        $scope.getSelectedMonitorTab = function () {
            return selectedTab;
        }

        $scope.setSelectedMonitorTab = function (index) {
            selectedTab = index;
        }

        $scope.SeaCloudsApi.getEntityMetricMapList($scope.seaCloudsApplicationId).
            success(function (entities) {
                $scope.entities = entities;
                $scope.metricsLoaded = true;
            }).
            error(function () {
                notificationService.error("Unable to retrieve the available metrics");
            });

        var retrievalErrors = 0;
        var dataRetrievalFunction = function(entity, metric){
            $scope.SeaCloudsApi.getMetricValue($scope.seaCloudsApplicationId, entity.id, metric.name).
                success(function (value) {
                    metric.data.values.push(value);
                    metric.data.labels.push($filter('date')(new Date(), "mediumTime"))
                    if (metric.data.values.length > 20) {
                        metric.data.values.shift();
                        metric.data.labels.shift();
                    }
                }).
                error(function (value) {
                    retrievalErrors++;
                    if(retrievalErrors > 3){
                        notificationService.error("Metric data" + metric.name + " of the Entity " + entity.name +
                            " failed to be retrieved after several attempts. This metric will be disabled.");
                        $scope.toggleMetric(entity, metric);
                    }
                })
        }

        $scope.toggleMetric = function (entity, metric){
            metric.enabled = !metric.enabled;

            if(metric.enabled){
                metric.data = {
                    labels: [],
                    values: []
                };

                metric.updateFunction = $interval(function () {
                    dataRetrievalFunction(entity, metric);
                }, 5000);

            }else{
                metric.data = {
                    labels: [],
                    values: []
                };
                $interval.cancel(metric.updateFunction);
                metric.updateFunction = undefined;
            }
        }

        $scope.hasMetricsEnabled = function () {
            var hasMetricsEnabled = false;

            $scope.entities.forEach(function (entity) {
                entity.metrics.forEach(function (metric) {
                    hasMetricsEnabled = hasMetricsEnabled || metric.enabled;

                })
            });

            return hasMetricsEnabled;
        }

        $scope.$on('$destroy', function () {
            $scope.entities.forEach(function (entity) {
                entity.metrics.forEach(function (metric) {
                    if (metric.updateFunction) {
                        $interval.cancel(metric.updateFunction);
                    }
                })
            });
        });

    })
;