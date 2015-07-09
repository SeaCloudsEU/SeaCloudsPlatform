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

        // Wizard data
        $scope.applicationName ="";
        $scope.matchmakerInput = "Please load your file here...";
        $scope.matchmakerResult = "Please click the button to get results";
        $scope.optimizerInput = "Please load your file here...";
        $scope.optimizerResult = "Please click the button to get results";
        $scope.damInput = "Please load your file here...";
        $scope.monitoringModelInput = "Please load your file here...";
        $scope.monitoringRulesInput = "Please load your file here...";
        $scope.slaInput = "Please load your file here...";
        $scope.wizardLog = "";


        // File uploader
        $scope.matchmakerInputFile = undefined;
        $scope.optimizerInputFile = undefined;
        $scope.damInputFile = undefined;
        $scope.monitoringModelInputFile = undefined;
        $scope.monitoringRulesInputFile = undefined;
        $scope.slaInputFile = undefined;

        //TODO: Link topology with the editor
        $scope.topology = {
            "nodes": [],
            "links": []
        };

        $scope.processAAM = function () {
            if (isValidYAML($scope.matchmakerInput)) {
                $scope.SeaCloudsApi.matchmake($scope.matchmakerInput).
                    success(function (adp) {
                        $scope.matchmakerResult = JSON.stringify(adp);
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
            if (isValidYAML($scope.optimizerInput)) {
                $scope.SeaCloudsApi.optimize($scope.optimizerInput)
                    .success(function (dam) {
                        $scope.optimizerResult = JSON.stringify(dam);
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
                $scope.wizardLog += "Starting the deployment process...";
                $scope.wizardLog += "\t Done. \n";
            }

            var damFailCb = function () {
                $scope.wizardLog += "Starting the deployment process...";
                $scope.wizardLog += "\t ERROR. \n";
            }
            

            var rulesSuccessCb = function () {
                $scope.wizardLog += "Installing Monitoring Rules...";
                $scope.wizardLog += "\t Done. \n";
            }

            var rulesFailCb = function () {
                $scope.wizardLog += "Installing Monitoring Rules...";
                $scope.wizardLog += "\t ERROR. \n";
            }

            var agreementSuccessCb = function () {
                $scope.wizardLog += "Installing Service Level Agreements...";
                $scope.wizardLog += "\t Done. \n";
            }

            var agreementFailCb = function () {
                $scope.wizardLog += "Installing Service Level Agreements...";
                $scope.wizardLog += "\t ERROR. \n";
            }


            $scope.SeaCloudsApi.addProject($scope.damInput, damSuccessCb, damFailCb, $scope.monitoringModelInput, $scope.monitoringRulesInput, rulesSuccessCb, rulesFailCb,
                $scope.slaInput, agreementSuccessCb, agreementFailCb).
                success(function (data) {
                    $scope.wizardLog += "\n\n";
                    $scope.wizardLog += "The application deployment process was triggered succesfully*. \n";
                    $scope.wizardLog += "* Please notice that although the wizard finished the application runtime" +
                    "failures could happen please go to the status view in order to verify " +
                    "that everything is running properly"
                    $scope.$apply()
                }).
                error(function (data) {
                    $scope.wizardLog += "\n\n";
                    $scope.wizardLog += "Something wrong happened!\n";
                    $scope.wizardLog += "Please restart the process and try again\n";
                    $scope.wizardLog += "All the changes were reverted.\n";
                    $scope.$apply()
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
                case 2:
                case 3:
                    break;
                case 4:
                    $scope.deployApplication();
                case 5:
                default:
                    break;
            }

            if ($scope.currentStep != $scope.getStepCount()) {
                $scope.currentStep++;
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
                    return true;
                case 2:
                    return true;
                case 3:
                    return isValidJSON($scope.matchmakerResult) && isValidJSON($scope.optimizerResult)
                case 4:
                    return isValidYAML($scope.damInput)
                        && isValidXML($scope.monitoringRulesInput) && isValidXML($scope.slaInput)
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
            templateUrl: 'projects/add-application-wizard/wizard-step-1.html'
            //controller: 'AddApplicationWizardCtrl'
        };
    })
    .directive('wizardStep2', function () {
        return {
            restrict: 'E',
            templateUrl: 'projects/add-application-wizard/wizard-step-2.html'
            //controller: 'AddApplicationWizardCtrl'
        };
    })
    .directive('wizardStep3', function () {
        return {
            restrict: 'E',
            templateUrl: 'projects/add-application-wizard/wizard-step-3.html',
            link: function (scope, elem, attrs) {
                scope.editorOptionsInput = {
                    mode: 'application/json',
                    lineNumbers: true,
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
                            scope.$parent.matchmakerInput = e.target.result;
                        }
                        r.readAsText(scope.matchmakerInputFile[0]);
                    }
                });

                scope.$watch('optimizerInputFile', function () {
                    if (scope.optimizerInputFile) {
                        var r = new FileReader();
                        r.onload = function (e) {
                            scope.$parent.optimizerInput = e.target.result;
                        }
                        r.readAsText(scope.optimizerInputFile[0]);
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
                            scope.$parent.damInput = e.target.result;
                        }
                        r.readAsText(scope.damInputFile[0]);
                    }
                });

                scope.$watch('monitoringModelInputFile', function () {
                    if (scope.monitoringModelInputFile) {
                        var r = new FileReader();
                        r.onload = function (e) {
                            scope.$parent.monitoringModelInput = e.target.result;
                        }
                        r.readAsText(scope.monitoringModelInputFile[0]);
                    }
                });

                scope.$watch('monitoringRulesInputFile', function () {
                    if (scope.monitoringRulesInputFile) {
                        var r = new FileReader();
                        r.onload = function (e) {
                            scope.$parent.monitoringRulesInput = e.target.result;
                        }
                        r.readAsText(scope.monitoringRulesInputFile[0]);
                    }
                });

                scope.$watch('slaInputFile', function () {
                    if (scope.slaInputFile) {
                        var r = new FileReader();
                        r.onload = function (e) {
                            scope.$parent.slaInput = e.target.result;
                        }
                        r.readAsText(scope.slaInputFile[0]);
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
