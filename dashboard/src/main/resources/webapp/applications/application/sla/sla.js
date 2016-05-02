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
            .then(function (result) {
                $scope.agreement = result.data;
                return $scope.SeaCloudsApi.getAgreementStatus($scope.seaCloudsApplicationId);
            })
            .then(function (result) {
                $scope.agreementStatus = result.data;

                // View first term is it exists
                if ($scope.agreementStatus && $scope.agreementStatus.guaranteeterms.length) {
                    $scope.viewSLATerm($scope.agreementStatus.guaranteeterms[0].name);
                }
            })
            .catch(function () {
                notificationService.error("SLA Agreements are not available of this application")
            })
            .finally(function () {
                $scope.agrementLoaded = true;
            })


        var retrievalErrors = 0;
        $scope.updateFunction = $interval(function () {
            $scope.SeaCloudsApi.getAgreementStatus($scope.seaCloudsApplicationId)
                .then(function (result) {
                    $scope.agreementStatus = result.data;
                })
                .catch(function () {
                    retrievalErrors++;

                    if (retrievalErrors > 3) {
                        $interval.cancel($scope.updateFunction);
                        $scope.updateFunction = undefined;
                        notificationService.error("It was not possible to retrieve the SLA's status after several attempts. Disabling automatic refresh.");
                    }
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
        var selectedTermPenalties;
        var rawAgreementVisible;

        $scope.codemirror = { agreement:  "RAW Agreement visualizer is not ready yet."};

        $scope.isRawAgreementVisible = function () {
            return rawAgreementVisible;
        }

        $scope.showRawAgreement = function (status) {
            rawAgreementVisible = status;
        }

        $scope.viewSLATerm = function (name) {
            selectedTermName = name;

            $scope.agreement.terms.allTerms.guaranteeTerms.forEach(function (term, index) {
                if (term.name == selectedTermName) {
                    selectedTermIndex = index;
                }
            })

            $scope.SeaCloudsApi.getAgreementTermViolations($scope.seaCloudsApplicationId, name).
                success(function (violations) {
                    selectedTermViolations = violations;
                }).
                error(function () {
                    // Handle error
                })

            $scope.SeaCloudsApi.getAgreementTermPenalties($scope.seaCloudsApplicationId, name).
                success(function (penalties) {
                    selectedTermPenalties = penalties;
                }).
                error(function () {
                    // Handle error
                });
        }

        $scope.getActiveTermName = function () {
            return selectedTermName;
        }

        $scope.getActiveTermViolations = function () {
            return selectedTermViolations;
        }

        $scope.getActiveTermPenalties = function () {
            return selectedTermPenalties;
        }

        $scope.getSlaTermQoS = function () {
            if ($scope.agreement) {
                var obj = JSON.parse($scope.agreement.terms.allTerms.guaranteeTerms[selectedTermIndex].serviceLevelObjetive.kpitarget.customServiceLevel);
                return obj.qos;
            } else {
                return "Not available yet"
            }
        }

        $scope.getSlaTermConstrain = function () {
            if ($scope.agreement) {
                var obj = JSON.parse($scope.agreement.terms.allTerms.guaranteeTerms[selectedTermIndex].serviceLevelObjetive.kpitarget.customServiceLevel);
                return obj.constraint;
            } else {
                return "Not available yet"
            }

        }

    });

var SlaUtil = (function() {
    /*
     * TODO: atob and btoa unsafe in unicode strings.
     */

    function insert_agreement(rawdam, agreement) {
        var dam = jsyaml.safeLoad(rawdam);
        var base64agreement = window.btoa(agreement);
        try {
            dam
                .topology_template
                .groups
                .seaclouds_configuration_policy
                .policies[0]
                .configuration
                .slaAgreement = base64agreement;

        } catch(e) {
            console.warn("Could not insert agreement into dam");
        }
        return jsyaml.safeDump(dam);
    }

    function extract_agreement(rawdam) {
        var dam = jsyaml.safeLoad(rawdam);
        try {
            var base64agreement = dam
                .topology_template
                .groups
                .seaclouds_configuration_policy
                .policies[0]
                .configuration
                .slaAgreement;

        } catch(e) {
            console.warn("Could not extract agreement from dam");
            base64agreement = "";
        }

        var rawxml = window.atob(base64agreement);
        var xml = format_xml(rawxml);
        return xml;
    }

    /*
     * From https://gist.github.com/sente/1083506
     */
    function format_xml(xml) {
        var formatted = '';
        var reg = /(>)(<)(\/*)/g;
        xml = xml.replace(reg, '$1\r\n$2$3');
        var pad = 0;
        jQuery.each(xml.split('\r\n'), function(index, node) {
            var indent = 0;
            if (node.match( /.+<\/\w[^>]*>$/ )) {
                indent = 0;
            } else if (node.match( /^<\/\w/ )) {
                if (pad != 0) {
                    pad -= 1;
                }
            } else if (node.match( /^<\w[^>]*[^\/]>.*$/ )) {
                indent = 1;
            } else {
                indent = 0;
            }

            var padding = '';
            for (var i = 0; i < pad; i++) {
                padding += '  ';
            }

            formatted += padding + node + '\r\n';
            pad += indent;
        });

        return formatted;
    }

    return {
        insert_agreement: insert_agreement,
        extract_agreement: extract_agreement,
        format_xml: format_xml
    };
})();
