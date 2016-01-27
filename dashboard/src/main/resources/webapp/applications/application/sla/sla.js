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

angular.module('seacloudsDashboard.applications.application.sla', ['datatables', 'ui.codemirror'])
    .directive('sla', function () {
        return {
            scope: true,
            restrict: 'E',
            templateUrl: 'applications/application/sla/sla.html',
            controller: 'SlaCtrl'
        };
    })
    .controller('SlaCtrl', function ($scope, $interval, notificationService, DTOptionsBuilder) {
        $scope.rawAgreementViewerOptions = {
            readOnly: true,
            mode: 'json',
            lineNumbers: true
        };

        $scope.dtOptions = DTOptionsBuilder.newOptions().withDisplayLength(3);

        $scope.SeaCloudsApi.getAgreement($scope.seaCloudsApplicationId)
            .success(function (agreement) {
                $scope.agreement = agreement;
            }).error(function () {
                notificationService.error("SLA Agreements are not available of this application")
            })

        $scope.SeaCloudsApi.getAgreementStatus($scope.seaCloudsApplicationId).
            success(function (value) {
                $scope.agreementStatus = value;

                // View first term is it exists
                if($scope.agreementStatus && $scope.agreementStatus.guaranteeterms.length){
                    $scope.viewSLATerm($scope.agreementStatus.guaranteeterms[0].name);
                }
            }).
            error(function () {
            })

        $scope.updateFunction = $interval(function () {
            $scope.SeaCloudsApi.getAgreementStatus($scope.seaCloudsApplicationId).
                success(function (value) {
                    $scope.agreementStatus = value;
                }).
                error(function () {
                    $interval.cancel($scope.updateFunction);
                    $scope.updateFunction = undefined;
                    notificationService.error("It was not possible to retrieve the SLA's status after several attempts. Disabling automatic refresh.");
                })
        }, 5000);

        $scope.$on('$destroy', function () {
            if ($scope.updateFunction) {
                $interval.cancel($scope.updateFunction);
            }
        });

        var selectedTermIndex;
        var selectedTermName;
        var selectedTermViolations;
        var rawAgreementVisible;

        $scope.isRawAgreementVisible = function () {
            return rawAgreementVisible;
        }

        $scope.showRawAgreement = function (status) {
            rawAgreementVisible = status;
        }

        $scope.viewSLATerm = function (name) {
            selectedTermName = name;

            $scope.agreement.terms.allTerms.guaranteeTerms[index].forEach(function(term, index){
                if(term.name == selectedTermName){
                    selectedTermIndex = index;
                }
            })

            scope.SeaCloudsApi.getAgreementTermViolations($scope.seaCloudsApplicationId, name).
                success(function(violations){
                    selectedTermViolations = violations;
                }).
                error(function(){
                    // Handle error
                })

        }

        $scope.getActiveTermName = function () {
            return selectedTermName;
        }

        $scope.getActiveTermViolations = function(){
            return selectedTermViolations;
        }
        $scope.getSlaTermQoS = function () {
            if($scope.agreement) {
                var obj = JSON.parse($scope.agreement.terms.allTerms.guaranteeTerms[selectedTermIndex].serviceLevelObjetive.kpitarget.customServiceLevel);
                return obj.qos;
            }else{
                return "Not available yet"
            }
        }
        
        $scope.getSlaTermConstrain = function () {
            if($scope.agreement){
                var obj = JSON.parse($scope.agreement.terms.allTerms.guaranteeTerms[selectedTermIndex].serviceLevelObjetive.kpitarget.customServiceLevel);
                return obj.constraint;
            }else{
                return "Not available yet"
            }

        }

    });