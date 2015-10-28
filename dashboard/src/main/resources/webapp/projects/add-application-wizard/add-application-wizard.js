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
            application_requirements: {
                response_time: undefined,
                availability: undefined,
                cost: undefined,
                workload: undefined
            },
            matchmakerInput: "Please load your file here...",
            matchmakerResult: "Please click the button to get results",
            optimizerInput: "Please load your file here...",
            optimizerResult: "Please click the button to get results",
            damInput: "Please load your file here...",
            monitoringModelInput: "Please load your file here...",
            monitoringRulesInput: "Please load your file here...",
            slaInput: "Please load your file here...",
            wizardLog: "",
            matchmakerInputFile: undefined,
            optimizerInputFile: undefined,
            damInputFile: undefined,
            monitoringModelInputFile: undefined,
            monitoringRulesInputFile: undefined,
            slaInputFile: undefined,
            topology: {
                "nodes": [],
                "links": []
            }
        }


        $scope.processAAM = function () {
            if (FormatValidator.validateYAML($scope.applicationWizardData.matchmakerInput)) {
                $scope.SeaCloudsApi.matchmake($scope.applicationWizardData.matchmakerInput).
                    success(function (adp) {
                        $scope.applicationWizardData.matchmakerResult = JSON.stringify(adp);
                        notificationService.success('The matchmaking process finished succesfully');
                    })
                    .error(function () {
                        notificationService.error('Something wrong happened');
                    });
            } else {
                notificationService.error('Syntax error, the input file must be a YAML file');
            }

        }

        $scope.processADP = function () {
            if (FormatValidator.validateYAML($scope.applicationWizardData.optimizerInput)) {
                $scope.SeaCloudsApi.optimize($scope.applicationWizardData.optimizerInput)
                    .success(function (dam) {
                        $scope.applicationWizardData.optimizerResult = JSON.stringify(dam);
                        notificationService.success('The optimization process finished succesfully');
                    })
                    .error(function () {
                        notificationService.error('Something wrong happened');
                    });
            } else {
                notificationService.error('Bad syntax');
            }

        }


        $scope.deployApplication = function () {

            var damSuccessCb = function () {
                $scope.applicationWizardData.wizardLog += "Starting the deployment process...";
                $scope.applicationWizardData.wizardLog += "\t Done. \n";
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


            $scope.SeaCloudsApi.addProject($scope.applicationWizardData.damInput, damSuccessCb, damFailCb,
                $scope.applicationWizardData.monitoringRulesInput, rulesSuccessCb, rulesFailCb,
                $scope.slaInput, agreementSuccessCb, agreementFailCb).
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
            if ($scope.currentStep != 1) {
                $scope.currentStep--;
            }
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
                            $scope.applicationWizardData.matchmakerInput = aam;
                            $scope.currentStep++;
                        }).
                        error(function () {
                            notificationService.error('The AMM Writer failed while processing the topology');
                        })
                    break;
                case 3:
                    $scope.currentStep++;
                    break;
                case 4:
                    $scope.deployApplication()
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
                    return true;
                case 3:
                    return FormatValidator.validateJSON($scope.applicationWizardData.matchmakerResult) && FormatValidator.validateJSON($scope.applicationWizardData.optimizerResult)
                case 4:
                    return FormatValidator.validateYAML($scope.applicationWizardData.damInput)
                        && FormatValidator.validateXML($scope.applicationWizardData.monitoringRulesInput) && FormatValidator.validateXML($scope.applicationWizardData.slaInput)
                case 5:
                    return true;
            }
        }


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
            //controller: 'AddApplicationWizardCtrl'
        };
    })
    .directive('wizardStep3', function () {
        return {
            restrict: 'E',
            templateUrl: 'projects/add-application-wizard/wizard-step-3.html',
            scope: true,
            link: function (scope, elem, attrs) {
                scope.editorOptionsInput = {
                    mode: 'application/json',
                    lineNumbers: true
                };

                scope.editorOptionsOutput = {
                    readOnly: 'nocursor',
                    mode: 'application/json',
                    lineNumbers: true
                };

                scope.$watch('matchmakerInputFile', function () {
                    if (scope.matchmakerInputFile) {
                        var r = new FileReader();
                        r.onload = function (e) {
                            scope.applicationWizardData.matchmakerInput = e.target.result;
                        }
                        r.readAsText(scope.applicationWizardData.matchmakerInputFile[0]);
                    }
                });

                scope.$watch('optimizerInputFile', function () {
                    if (scope.optimizerInputFile) {
                        var r = new FileReader();
                        r.onload = function (e) {
                            scope.applicationWizardData.optimizerInput = e.target.result;
                        }
                        r.readAsText(scope.applicationWizardData.optimizerInputFile[0]);
                    }
                });

            }
            //controller: 'AddApplicationWizardCtrl'
        };
    })
    .directive('wizardStep4', function () {
        return {
            restrict: 'E',
            templateUrl: 'projects/add-application-wizard/wizard-step-4.html',
            scope: true,
            link: function (scope, elem, attrs) {
                scope.editorOptionsDam = {
                    mode: 'yaml',
                    lineNumbers: true
                };

                scope.editorOptionsMonitoringDam = {
                    mode: 'application/json',
                    lineNumbers: true
                };

                scope.editorOptionsMonitoringRules = {
                    mode: 'xml',
                    lineNumbers: true
                };

                scope.editorOptionsSLA = {
                    mode: 'xml',
                    lineNumbers: true
                };

                scope.$watch('damInputFile', function () {
                    if (scope.damInputFile) {
                        var r = new FileReader();
                        r.onload = function (e) {
                            scope.applicationWizardData.damInput = e.target.result;
                        }
                        r.readAsText(scope.applicationWizardData.damInputFile[0]);
                    }
                });

                scope.$watch('monitoringModelInputFile', function () {
                    if (scope.monitoringModelInputFile) {
                        var r = new FileReader();
                        r.onload = function (e) {
                            scope.applicationWizardData.monitoringModelInput = e.target.result;
                        }
                        r.readAsText(scope.applicationWizardData.monitoringModelInputFile[0]);
                    }
                });

                scope.$watch('monitoringRulesInputFile', function () {
                    if (scope.monitoringRulesInputFile) {
                        var r = new FileReader();
                        r.onload = function (e) {
                            scope.applicationWizardData.monitoringRulesInput = e.target.result;
                        }
                        r.readAsText(scope.applicationWizardData.monitoringRulesInputFile[0]);
                    }
                });

                scope.$watch('slaInputFile', function () {
                    if (scope.slaInputFile) {
                        var r = new FileReader();
                        r.onload = function (e) {
                            scope.applicationWizardData.slaInput = e.target.result;
                        }
                        r.readAsText(scope.applicationWizardData.slaInputFile[0]);
                    }
                });

            }
            //controller: 'AddApplicationWizardCtrl
        };
    })
    .directive('wizardStep5', function () {
        return {
            scope: true,
            templateUrl: 'projects/add-application-wizard/wizard-step-5.html',
            link: function (scope, elem, attrs) {

            }
            //controller: 'AddApplicationWizardCtrl'
        };
    })
