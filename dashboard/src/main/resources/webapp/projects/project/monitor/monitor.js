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

angular.module('seacloudsDashboard.projects.project.monitor', ['datatables', 'chart.js'])
    .directive('monitor', function () {
        return {
            restrict: 'E',
            templateUrl: 'projects/project/monitor/monitor.html',
            controller: 'MonitorCtrl'
        };
    })
    .controller('MonitorCtrl', function ($scope, $interval, $filter, DTOptionsBuilder, notificationService) {
        $scope.dtOptions = DTOptionsBuilder.newOptions().withDisplayLength(5);
        $scope.chartOptions ={
            maintainAspectRatio: false,
            responsive: true,
        }
        $scope.availableMetrics = [];

        $scope.$watch('availableMetrics', function (entities) {
            entities.forEach(function (entity) {

                entity.metrics.forEach(function (metric) {
                    if (metric.enabled) {
                        if (!metric.updateFunction) {
                            metric.data = {
                                labels: [],
                                values: []
                            };

                            metric.updateFunction = $interval(function () {

                                $scope.SeaCloudsApi.getMetricValue(entity.applicationId, entity.id, metric.name).
                                    success(function (value) {
                                        metric.data.values.push(value);
                                        metric.data.labels.push($filter('date')(new Date(), "mediumTime"))
                                        if (metric.data.values.length > 20) {
                                            metric.data.values.shift();
                                            metric.data.labels.shift();
                                        }
                                    }).
                                    error(function (value) {
                                        // Handle error
                                    })
                            }, 5000);
                        }

                    } else {
                        if (metric.updateFunction) {
                            $interval.cancel(metric.updateFunction);
                            metric.updateFunction = undefined;
                            metric.data = {
                                labels: [],
                                values: []
                            };

                        }
                    }
                })

            })
        }, true);

        $scope.$on('$destroy', function () {
            $scope.availableMetrics.forEach(function (entity) {
                entity.metrics.forEach(function (metric) {
                    if (metric.updateFunction) {
                        $interval.cancel(metric.updateFunction);
                    }
                })
            });
        });

        $scope.SeaCloudsApi.getAvailableMetrics($scope.project.id).
            success(function (data) {
                $scope.availableMetrics = data;
            }).
            error(function () {
                //TODO: Handle the error better than showing a notification
                notificationService.error("Unable to retrieve the available metrics");
            });


        var metricSetupActive = true;

        $scope.hasMetricsEnabled = function () {
            var hasMetricsEnabled = false;

            $scope.availableMetrics.forEach(function (entity) {
                entity.metrics.forEach(function (metric) {
                    hasMetricsEnabled = hasMetricsEnabled || metric.enabled;

                })
            });

            return hasMetricsEnabled;
        }


        $scope.isMetricSettingVisible = function () {
            return metricSetupActive;
        }

        $scope.showMetricSettings = function (status) {
            metricSetupActive = status;
        }
    })
;