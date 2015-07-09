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

angular.module('seacloudsDashboard.projects.project.sla', [])
    .directive('sla', function () {
        return {
            restrict: 'E',
            templateUrl: 'projects/project/sla/sla.html',
            controller: 'SlaCtrl'
        };
    })
    .controller('SlaCtrl', function ($scope, $interval, notificationService, DTOptionsBuilder) {
        $scope.slaInput = "Please load your file here...";
        $scope.editorOptionsSLA = {
            mode: 'xml',
            lineNumbers: true
        };
        $scope.dtOptions = DTOptionsBuilder.newOptions().withDisplayLength(3);


        $scope.agreement = undefined;
        $scope.terms = undefined;

        $scope.SeaCloudsApi.getAgreementStatus($scope.project.id).
            success(function (value) {
                $scope.terms = value;
            }).
            error(function () {
                // Handle error
            })

        $scope.SeaCloudsApi.getAgreements($scope.project.id)
            .success(function (agreement) {
                $scope.agreement = agreement;
            }).error(function () {
                notificationService.error("An error occurred while retrieving the SLAs")
            })


        $scope.updateFunction = undefined;
        $scope.updateFunction = $interval(function () {
            $scope.SeaCloudsApi.getAgreementStatus($scope.project.id).
                success(function (value) {
                    $scope.terms = value;
                }).
                error(function (value) {
                    // Handle error
                })
        }, 5000);

        $scope.$on('$destroy', function () {
            if ($scope.updateFunction) {
                $interval.cancel($scope.updateFunction);
            }
        });

        $scope.processSLA = function () {
            if (isValidXML($scope.slaInput)) {
                notificationService.success('Success!!!');
                $scope.slaInput = "Please load your file here...";
                $scope.showSLASettings(false);

            } else {
                notificationService.error('Bad syntax');
            }

        }

        $scope.$watch('slaInputFile', function () {
            if ($scope.slaInputFile) {
                var r = new FileReader();
                r.onload = function (e) {
                    $scope.slaInput = e.target.result;
                    $scope.$apply();
                }
                r.readAsText($scope.slaInputFile[0]);
            }
        });

        var selectedSLA = 0;
        var slaSetupActive = false;

        $scope.isSLASettingVisible = function () {
            return slaSetupActive;
        }

        $scope.showSLASettings = function (status) {
            slaSetupActive = status;
        }

        $scope.viewSLATerm = function (index) {
            selectedSLA = index;
        }

        $scope.getActiveTermIndex = function () {
            return selectedSLA;
        }


        $scope.getSlaTermQoS = function (index) {
            if($scope.agreement) {
                var obj = JSON.parse($scope.agreement.terms.allTerms.guaranteeTerms[index].serviceLevelObjetive.kpitarget.customServiceLevel);
                return obj.qos;
            }else{
                return "Not available yet"
            }
        }
        
        $scope.getSlaTermConstrain = function (index) {
            
            if($scope.agreement){
                var obj = JSON.parse($scope.agreement.terms.allTerms.guaranteeTerms[index].serviceLevelObjetive.kpitarget.customServiceLevel);
                return obj.constraint;
            }else{
                return "Not available yet"
            }

        }

    });