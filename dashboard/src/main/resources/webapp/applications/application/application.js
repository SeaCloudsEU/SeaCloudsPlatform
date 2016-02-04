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

angular.module('seacloudsDashboard.applications.application', ['ngRoute', 'ui.bootstrap', 'seacloudsDashboard.applications.application.status',
    'seacloudsDashboard.applications.application.monitor', 'seacloudsDashboard.applications.application.sla'])
    .config(['$routeProvider', function ($routeProvider) {
        $routeProvider.when('/applications/:seaCloudsApplicationId', {
            templateUrl: 'applications/application/application.html',
            controller: 'ProjectCtrl'
        })
    }])
    .controller('ProjectCtrl', function ($scope, $routeParams, $location, $interval, $uibModal, notificationService) {
        $scope.Page.setTitle('SeaClouds Dashboard - Application details');


        $scope.seaCloudsApplicationId = $routeParams.seaCloudsApplicationId;
        $scope.brooklynApplication = undefined;
        $scope.isLoaded = false;

        $scope.SeaCloudsApi.getApplication($scope.seaCloudsApplicationId).
            success(function (brooklynApplication) {
                $scope.brooklynApplication = brooklynApplication;
                $scope.isLoaded = true;
            }).
            error(function () {
                $scope.isLoaded = true;
                notificationService.error("Cannot retrieve the application, please try it again");
            })


        $scope.updateFunction = $interval(function () {
            $scope.SeaCloudsApi.getApplication($scope.seaCloudsApplicationId).
                success(function (brooklynApplication) {
                    $scope.brooklynApplication = brooklynApplication;
                }).
                error(function () {
                    $interval.cancel($scope.updateFunction);
                    $scope.updateFunction = undefined;
                    notificationService.error("It was not possible to retrieve the application after several attempts. Disabling automatic refresh.");
                })
        }, 5000);

        $scope.$on('$destroy', function () {
            if($scope.updateFunction){
                $interval.cancel($scope.updateFunction);
            }
        });

        var tabSelected = 0;

        $scope.getSelectedApplicationTab = function () {
            return tabSelected;
        }

        $scope.setSelectedApplicationTab = function (tab) {
            tabSelected = tab;
        }


        $scope.openRemoveApplicationModal = function () {
            var modalInstance = $uibModal.open({
                resolve: {
                    project: function () {
                        return $scope.project;
                    }
                },
                templateUrl: 'removeApplicationModal.html',
                controller: function ($scope, $uibModalInstance, application) {
                    $scope.application = application;

                    $scope.ok = function () {
                        $uibModalInstance.close($scope.seaCloudsApplicationId);
                    };

                    $scope.cancel = function () {
                        $uibModalInstance.dismiss('cancel');
                    };

                }
            });

            modalInstance.result.then(function (seaCloudsApplicationId) {
                $scope.SeaCloudsApi.removeApplication(seaCloudsApplicationId).
                    success(function () {
                        $location.path("/applications")
                        notificationService.success("The application " + seaCloudsApplicationId + " will be removed soon");
                    }).
                    error(function () {
                        notificationService.error("An error happened during the removal process, please try it again");
                        $scope.closeRemoveApplicationModal();
                    });
            });
        };


    });