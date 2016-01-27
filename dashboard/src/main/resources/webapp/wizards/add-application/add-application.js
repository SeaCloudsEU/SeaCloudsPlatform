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

angular.module('seacloudsDashboard.wizards.addApplication', ['ngRoute', 'angularTopologyEditor', 'ui.codemirror'])
    .config(['$routeProvider', function ($routeProvider) {
        $routeProvider.when('/wizards/add-application', {
            templateUrl: 'wizards/add-application/add-application.html',
            controller: 'AddApplicationCtrl'
        })
    }])
    .controller('AddApplicationCtrl', function ($scope, notificationService) {
        $scope.applicationWizardData = {
            name: "",
            id: undefined,
            application_requirements: {
                response_time: undefined,
                availability: undefined,
                cost: undefined,
                workload: undefined
            },
            aam: undefined,
            feasibleAdps: undefined,
            finalAdp: undefined,
            finalDam: undefined,
            finalMonitoringRules: undefined,
            finalSlaRules: undefined,
            wizardLog: "",
            topology: {
                "nodes": [],
                "links": []
            },
            brooklynApplication: undefined
        }

        $scope.steps = ['Application properties', 'Design topology',
            'Optimize & Plan', 'Configuration summary', 'Process Summary & Deploy'];

        $scope.currentStep = 1;
        $scope.isSelected = function (step) {
            return $scope.currentStep == step;
        };
        $scope.getStepCount = function () {

            return $scope.steps.length;
        };
        $scope.range = function (n) {
            return new Array(n);
        };
        $scope.previousStep = function () {
            switch ($scope.currentStep) {
                case 1:
                    break;
                case 2:
                    $scope.applicationWizardData.aam = undefined;
                    break;
                case 3:
                    $scope.applicationWizardData.adpDescriptions = undefined;
                    $scope.applicationWizardData.feasibleAdps = undefined;
                    $scope.applicationWizardData.finalAdp = undefined;
                    $scope.adpsGenerated = false;
                    break;
                case 4:
                    $scope.applicationWizardData.finalDam = undefined;
                    $scope.applicationWizardData.finalMonitoringRules = undefined;
                    $scope.applicationWizardData.finalSlaRules = undefined;
                    $scope.damGenerated = false;
                    break;
                case 5:
                default:
                    break;
            }
            $scope.currentStep--;
        };

        $scope.nextStep = function () {
            switch ($scope.currentStep) {
                case 1:
                    $scope.currentStep++;
                    break;
                case 2:
                    var designPhaseOutput = $scope.applicationWizardData.topology;
                    designPhaseOutput.name = $scope.applicationWizardData.name;
                    designPhaseOutput.application_requirements = $scope.applicationWizardData.application_requirements;
                    designPhaseOutput.application_requirements.workload *= 60; // Fix Issue #182
                    designPhaseOutput.application_requirements.availability /= 100; // Fix Issue #223
                    notificationService.info("Generating AAM for the current topology.");
                    console.log("Topology: " + JSON.stringify(designPhaseOutput));
                    $scope.lockButtons = true;
                    $scope.SeaCloudsApi.getAamFromDesigner(designPhaseOutput).
                        success(function (aam) {
                            $scope.applicationWizardData.aam = aam;
                            notificationService.info("Generating ADPs for the current AAM.");
                            console.log("AAM: " + JSON.stringify(aam));
                            $scope.SeaCloudsApi.getAdpList(aam).
                                success(function (feasibleAdps) {
                                    $scope.applicationWizardData.feasibleAdps = feasibleAdps.adps;
                                    $scope.applicationWizardData.adpDescriptions = feasibleAdps.adps.map(AdpPretifier.adpToObject);
                                    $scope.adpsGenerated = true;
                                    $scope.currentStep++;
                                    console.log("ADP's: " + JSON.stringify(feasibleAdps));
                                    $scope.lockButtons = false;
                                })
                                .error(function () {
                                    $scope.adpsGenerated = true;
                                    $scope.lockButtons = false;
                                    notificationService.error('The Planner failed to generate the feasible ADPs');
                                    $scope.currentStep++;
                                });
                        }).
                        error(function () {
                            $scope.lockButtons = false;
                            notificationService.error('The AMM Writer failed while processing the topology');
                        });
                    break;
                case 3:
                    notificationService.info("Generating DAM for the current ADP.");
                    console.log("ADP: " + JSON.stringify($scope.applicationWizardData.finalAdp));
                    $scope.lockButtons = true;
                    $scope.SeaCloudsApi.getDam($scope.applicationWizardData.finalAdp).
                        success(function(plannerResponse){
                            $scope.applicationWizardData.finalDam = plannerResponse.dam;
                            $scope.damGenerated = true;
                            $scope.currentStep++;
                            $scope.lockButtons = false;
                            console.log("DAM: " + JSON.stringify(plannerResponse.dam));
                        }).
                        error(function(){
                            $scope.damGenerated = true;
                            $scope.currentStep++;
                            $scope.lockButtons = false;
                            notificationService.error('The DAM failed to generate the DAM, please use the API to deploy an usermade DAM');
                        })
                    notificationService.notify('The DAM Generator is still under development. The autogenerated DAM could be not deployable.');
                    break;
                case 4:
                    notificationService.info("Starting the deployment process... Please wait.");
                    $scope.applicationWizardData.wizardLog += "Starting the deployment process... \t Done. \n";
                    $scope.lockButtons = false;
                    $scope.SeaCloudsApi.addApplication($scope.applicationWizardData.finalDam).
                        success(function (application) {
                            $scope.applicationWizardData.seaCloudsApplicationId = application.seaCloudsApplicationId;
                            $scope.applicationWizardData.wizardLog += "\n\n";
                            $scope.applicationWizardData.wizardLog += "The application deployment process was triggered succesfully*. \n";
                            $scope.applicationWizardData.wizardLog += "* Please notice that although the wizard finished the application runtime" +
                                "failures could happen please go to the status view in order to verify " +
                                "that everything is running properly"
                            $scope.currentStep++;
                        }).
                        error(function (data) {
                            $scope.applicationWizardData.wizardLog += "\n\n";
                            $scope.applicationWizardData.wizardLog += "Something wrong happened!\n";
                            $scope.applicationWizardData.wizardLog += "Please restart the process and try again\n";
                            $scope.applicationWizardData.wizardLog += "All the changes were reverted.\n";
                            $scope.currentStep++;
                        })
                    break;
                case 5:
                default:
                    break;
            }
        };

        $scope.lockButtons = false;
        $scope.wizardCanRollback = function () {

            if($scope.lockButtons){
                return false
            };

            switch ($scope.currentStep) {
                case 1:
                    return false;
                case 2:
                case 3:
                case 4:
                    return true;
                case 5:
                    return false;
            }
        }

        $scope.wizardCanContinue = function () {
            if($scope.lockButtons){
                return false
            };

            switch ($scope.currentStep) {
                case 1:
                    return $scope.applicationWizardData.name && $scope.applicationWizardData.application_requirements.availability &&
                        $scope.applicationWizardData.application_requirements.cost && $scope.applicationWizardData.application_requirements.response_time &&
                        $scope.applicationWizardData.application_requirements.workload;
                case 2:
                    return $scope.applicationWizardData.topology.nodes.length;
                case 3:
                    return $scope.applicationWizardData.finalAdp;
                case 4:
                    return $scope.applicationWizardData.finalDam;
                case 5:
                    return true;
            }
        }

        $scope.codemirrorFeasibleDamOptions = {
            mode: 'yaml',
            readOnly: true,
            cursorBlinkRate: -1
        };


        $scope.codemirrorDamOptions = {
            mode: 'yaml',
            lineNumbers: true,
        };

    })
    .directive('wizardStep1', function () {
        return {
            restrict: 'E',
            templateUrl: 'wizards/add-application/wizard-step-1.html',
            scope: true
            //controller: 'AddApplicationWizardCtrl'
        };
    })
    .directive('wizardStep2', function () {
        return {
            restrict: 'E',
            templateUrl: 'wizards/add-application/wizard-step-2.html',
            scope: true,
        };
    })
    .directive('wizardStep3', function () {
        return {
            restrict: 'E',
            templateUrl: 'wizards/add-application/wizard-step-3.html',
            scope: true,
            controller: function ($scope, $element) {
                var MAX_ITEM_PER_PAGE = 3
                var currentPage = 0;

                $scope.getCurrentAdpDescriptions = function() {
                    $scope.MAX_PAGES = Math.floor($scope.applicationWizardData.adpDescriptions.length / MAX_ITEM_PER_PAGE);

                    return $scope.applicationWizardData.adpDescriptions.slice(currentPage * MAX_ITEM_PER_PAGE,
                        currentPage * MAX_ITEM_PER_PAGE + MAX_ITEM_PER_PAGE);
                }

                $scope.getCurrentPage = function () {
                    return currentPage;
                }

                $scope.nextPage = function () {
                    currentPage++;
                }

                $scope.previousPage = function () {
                    currentPage--;
                }

                $scope.isAdpSelected = function(index){
                    var computedIndex = index + currentPage * MAX_ITEM_PER_PAGE;
                    return  $scope.applicationWizardData.finalAdp == $scope.applicationWizardData.feasibleAdps[computedIndex];
                }

                $scope.setFinalAdp = function (index) {
                    var computedIndex = index + currentPage * MAX_ITEM_PER_PAGE;
                    $scope.applicationWizardData.finalAdp = $scope.applicationWizardData.feasibleAdps[computedIndex];
                }
            }
        };
    })
    .directive('wizardStep4', function () {
        return {
            restrict: 'E',
            templateUrl: 'wizards/add-application/wizard-step-4.html',
            scope: true
        };
    })
    .directive('wizardStep5', function () {
        return {
            restrict: 'E',
            templateUrl: 'wizards/add-application/wizard-step-5.html',
            scope: true,
            controller: function ($scope, $interval, notificationService) {
                $scope.applicationWizardData.brooklynAppTopology = {
                    "nodes": [],
                    "links": []
                },

                    $scope.$watch('applicationWizardData.seaCloudsApplicationId', function (newValue) {
                        if (newValue) {
                            $scope.updateFunction = $interval(function () {
                                $scope.SeaCloudsApi.getApplication($scope.applicationWizardData.seaCloudsApplicationId).
                                    success(function (application) {
                                        $scope.applicationWizardData.brooklynAppTopology = TopologyEditorUtils.getTopologyFromEntities(application);
                                    }).error(function () {
                                        //TODO: Handle the error better than showing a notification
                                        notificationService.error("Unable to retrieve the projects");
                                    })
                            }, 5000);
                        }
                    });

                $scope.$on('$destroy', function () {
                    if ($scope.updateFunction) {
                        $interval.cancel($scope.updateFunction);
                    }
                });
            }
        }
    })
