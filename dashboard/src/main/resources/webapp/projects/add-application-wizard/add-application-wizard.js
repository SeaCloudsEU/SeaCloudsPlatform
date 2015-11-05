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

angular.module('seacloudsDashboard.projects.addApplicationWizard', ['ngRoute', 'angularTopologyEditor', 'ui.codemirror', 'ngFileUpload'])
    .config(['$routeProvider', function ($routeProvider) {
        $routeProvider.when('/add-application-wizard', {
            templateUrl: 'projects/add-application-wizard/add-application-wizard.html'
        })
    }])
    .controller('AddApplicationWizardCtrl', function ($scope, notificationService) {
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


        $scope.deployApplication = function () {

            var damSuccessCb = function (futureEntity) {
                $scope.applicationWizardData.wizardLog += "Starting the deployment process...";
                $scope.applicationWizardData.wizardLog += "\t Done. \n";
                $scope.applicationWizardData.id = futureEntity.entityId;
            }

            var damFailCb = function () {
                $scope.applicationWizardData.wizardLog += "Starting the deployment process...";
                $scope.applicationWizardData.wizardLog += "\t ERROR. \n";
            }


            var rulesSuccessCb = function () {
                $scope.applicationWizardData.wizardLog += "Installing Monitoring Rules...";
                $scope.applicationWizardData.wizardLog += "\t Done. \n";
            }

            var rulesFailCb = function () {
                $scope.applicationWizardData.wizardLog += "Installing Monitoring Rules...";
                $scope.applicationWizardData.wizardLog += "\t ERROR. \n";
            }

            var agreementSuccessCb = function () {
                $scope.applicationWizardData.wizardLog += "Installing Service Level Agreements...";
                $scope.applicationWizardData.wizardLog += "\t Done. \n";
            }

            var agreementFailCb = function () {
                $scope.applicationWizardData.wizardLog += "Installing Service Level Agreements...";
                $scope.applicationWizardData.wizardLog += "\t ERROR. \n";
            }


            $scope.SeaCloudsApi.addProject($scope.applicationWizardData.finalDam, damSuccessCb, damFailCb, $scope.applicationWizardData.finalMonitoringRules, rulesSuccessCb, rulesFailCb,
                $scope.applicationWizardData.finalSlaRules, agreementSuccessCb, agreementFailCb).
                success(function (data) {
                    $scope.applicationWizardData.wizardLog += "\n\n";
                    $scope.applicationWizardData.wizardLog += "The application deployment process was triggered succesfully*. \n";
                    $scope.applicationWizardData.wizardLog += "* Please notice that although the wizard finished the application runtime" +
                        "failures could happen please go to the status view in order to verify " +
                        "that everything is running properly"
                }).
                error(function (data) {
                    $scope.applicationWizardData.wizardLog += "\n\n";
                    $scope.applicationWizardData.wizardLog += "Something wrong happened!\n";
                    $scope.applicationWizardData.wizardLog += "Please restart the process and try again\n";
                    $scope.applicationWizardData.wizardLog += "All the changes were reverted.\n";
                })
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
                    break;
                case 3:
                    $scope.applicationWizardData.feasibleAdps = undefined;
                    $scope.applicationWizardData.finalAdp = undefined;
                    break;
                case 4:
                    $scope.applicationWizardData.finalDam = undefined;
                    $scope.applicationWizardData.finalMonitoringRules = undefined;
                    $scope.applicationWizardData.finalSlaRules = undefined;
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
                    $scope.applicationWizardData.topology.name = $scope.applicationWizardData.name;
                    $scope.applicationWizardData.topology.application_requirements = $scope.applicationWizardData.application_requirements;

                    $scope.SeaCloudsApi.getAamFromDesigner($scope.applicationWizardData.topology).
                        success(function (aam) {
                            $scope.applicationWizardData.aam = aam;
                            $scope.currentStep++;
                            $scope.SeaCloudsApi.getAdpList(aam).
                                success(function (feasibleAdps) {
                                    $scope.applicationWizardData.feasibleAdps = feasibleAdps;
                                })
                                .error(function () {
                                    notificationService.error('The Planner failed to generate the feasible ADPs');
                                });
                        }).
                        error(function () {
                            notificationService.error('The AMM Writer failed while processing the topology');
                        });
                    break;
                case 3:
                    $scope.currentStep++;
                    notificationService.info('The DAM Generator is not ready yet. Please fill the required inputs if you want to continue.');
                    break;
                case 4:
                    $scope.deployApplication();
                    $scope.currentStep++;
                    break;
                case 5:
                default:
                    break;
            }
        };

        $scope.wizardCanRollback = function () {
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


        $scope.codemirrorSlaRulesOptions = {
            mode: 'xml',
            lineNumbers: true,
        };

        $scope.codeMirrorMonitoringRulesOptions = {
            mode: 'xml',
            lineNumbers: true,
        };
    })
    .directive('addApplicationWizard', function () {
        return {
            restrict: 'E',
            templateUrl: 'projects/add-application-wizard/add-application-wizard.html',
            controller: 'AddApplicationWizardCtrl'
        };
    })
    .directive('wizardStep1', function () {
        return {
            restrict: 'E',
            templateUrl: 'projects/add-application-wizard/wizard-step-1.html',
            scope: true
            //controller: 'AddApplicationWizardCtrl'
        };
    })
    .directive('wizardStep2', function () {
        return {
            restrict: 'E',
            templateUrl: 'projects/add-application-wizard/wizard-step-2.html',
            scope: true,
        };
    })
    .directive('wizardStep3', function () {
        return {
            restrict: 'E',
            templateUrl: 'projects/add-application-wizard/wizard-step-3.html',
            scope: true,
            controller: function ($scope, $element) {
                var MAX_ITEM_PER_PAGE = 3
                var currentPage = 0;
                $scope.getCurrentlyVisibleFeasibleAdps = function () {
                    $scope.MAX_PAGES = Math.floor($scope.applicationWizardData.feasibleAdps.length / MAX_ITEM_PER_PAGE);
                    return $scope.applicationWizardData.feasibleAdps.slice(currentPage * MAX_ITEM_PER_PAGE,
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

                $scope.setFinalAdp = function (finalAdp) {
                    $scope.applicationWizardData.finalAdp = finalAdp;
                }
            }
        };
    })
    .directive('wizardStep4', function () {
        return {
            restrict: 'E',
            templateUrl: 'projects/add-application-wizard/wizard-step-4.html',
            scope: true
        };
    })
    .directive('wizardStep5', function () {
        return {
            restrict: 'E',
            templateUrl: 'projects/add-application-wizard/wizard-step-5.html',
            scope: true,
            controller: function ($scope, $interval, notificationService) {
                $scope.applicationWizardData.brooklynAppTopology = {
                    "nodes": [],
                    "links": []
                },

                    $scope.$watch('applicationWizardData.id', function (newValue) {
                        if (newValue) {
                            $scope.updateFunction = $interval(function () {
                                $scope.SeaCloudsApi.getProject($scope.applicationWizardData.id).
                                    success(function (project) {
                                        $scope.applicationWizardData.brooklynAppTopology = TopologyEditorUtils.getTopologyFromEntities(project);
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
