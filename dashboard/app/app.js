/*
 Copyright 2014 SeaClouds
 Contact: SeaClouds

 Licensed under the Apache License, Version 2.0 (the "License");
 you may not use this file except in compliance with the License.
 You may obtain a copy of the License at

 http://www.apache.org/licenses/LICENSE-2.0

 Unless required by applicable law or agreed to in writing, software
 distributed under the License is distributed on an "AS IS" BASIS,
 WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 See the License for the specific language governing permissions and
 limitations under the License.
 */
'use strict';

var seacloudsDashboard = angular.module('seacloudsDashboard', [
    'ui.bootstrap',
    'jlareau.pnotify',
    'ngAnimate',
    'seacloudsDashboard.header',
    'seacloudsDashboard.footer',
    'seacloudsDashboard.signin',
    'seacloudsDashboard.about',
    'seacloudsDashboard.help',
    'seacloudsDashboard.projects',
    'seacloudsDashboard.projects.addApplicationWizard'

]);

seacloudsDashboard.config(['$routeProvider', function ($routeProvider) {
    $routeProvider.when("/", {redirectTo: '/projects'})
    //TODO: Create a not available view
    $routeProvider.otherwise({redirectTo: '/not-available.html'});
}]);


seacloudsDashboard.factory('Page', function () {
    var title = 'SeaClouds Dashboard';
    return {
        getTitle: function () {
            return title;
        },
        setTitle: function (newTitle) {
            title = newTitle;
        }
    };
});

seacloudsDashboard.factory('UserCredentials', function ($location) {
    var authenticatedUser = {
        id: 1337,
        username: "Manager",
        email: "admin@example.com"
    };

    return {
        getUser: function () {
            return authenticatedUser;
        },
        isUserAuthenticated: function () {
            return !(!authenticatedUser)
        },
        login: function (userCredentials) {
            authenticatedUser = {
                id: 1337,
                username: "Manager",
                email: "admin@example.com"
            };
            $location.path('/projects');
        },
        logout: function () {
            authenticatedUser = undefined;
            $location.path('/signin');
        }
    };

});

seacloudsDashboard.factory('SeaCloudsApi', function ($http) {
    return {
        getProjects: function () {
            return $http.get("/api/deployer/applications");
        },
        getProject: function (id) {
            var promise = new Promise(function (resolve, reject) {
                $http.get("/api/deployer/applications").
                    success(function (data) {
                        var project = data.filter(function (project) {
                            return project.id == id;
                        })[0];
                        resolve(project);
                    }).
                    error(function (err) {
                        reject(Error(err));
                    });
            });
            promise.success = function (fn) {
                promise.then(fn);
                return promise;
            }

            promise.error = function (fn) {
                promise.then(null, fn);
                return promise;
            }

            return promise;
        },
        addProject: function (dam, damSuccessCallback, damErrorCallback,
                              monitoringRules, monitoringRulesSuccessCallback, monitoringRulesErrorCallback,
                              agreements, agreementsSuccessCallback, agreementsErrorCallback) {

            var generateMonitoringRulesId = function(monitoringRules, entityId){
                var xmlParser = new DOMParser();
                var xmlSerialzier = new XMLSerializer()
                var rulesXml = xmlParser.parseFromString(monitoringRules, "text/xml");
                var rules = rulesXml.getElementsByTagName("monitoringRule");
                
                for (var i = 0; i< rules.length; i++){
                    var rule = rules[i];
                    var oldId = rule.getAttribute("id");
                    var oldLabel = rule.getAttribute("label");
                    rule.setAttribute("id", oldId + "-" + entityId);
                    rule.setAttribute("label", oldLabel + "-" + entityId);

                    var actions = rule.getElementsByTagName("action");
                    for (var i = 0; i < actions.length; i++){
                        var action = actions[i];
                        // TODO: Check if this substitution is really neccesary for each type of metric (eg. sth different to OutputMetric)
                        var parameters = action.getElementsByTagName("parameter");
                        if(action.getAttribute("name") == "OutputMetric"){
                            for(var j = 0; j < parameters.length; j++){
                                var parameter = parameters[j];
                                if(parameter.getAttribute("name") == "metric"){
                                    var oldId = parameter.firstChild.nodeValue;
                                    parameter.firstChild.nodeValue = oldId + "-" + entityId;
                                }
                            }
                        }

                    }


                }

                return xmlSerialzier.serializeToString(rulesXml);
            }
            
            var generateAgreementsId = function(agreements, entityId){
                var xmlParser = new DOMParser();
                var xmlSerialzier = new XMLSerializer()

                var agreementsXml = xmlParser.parseFromString(agreements, "text/xml");
                agreementsXml.documentElement.setAttribute("wsag:AgreementId", entityId)

                return xmlSerialzier.serializeToString(agreementsXml);

            }
            
            var promise = new Promise(function (resolveParent, rejectParent) {
                var deployerResponse;

                // Start application deployment
                $http.post("/api/deployer/applications", dam).
                    success(function (response) {
                        deployerResponse = response;
                        damSuccessCallback(response)


                        var futureEntityId = response.entityId;
                
                        // Deploy monitor rules
                        $http.post("/api/monitor/rules", generateMonitoringRulesId(monitoringRules, futureEntityId)).
                            success(function () {
                                monitoringRulesSuccessCallback();

         
                                $http.post("/api/sla/agreements", {
                                    rules: monitoringRules,
                                    agreements: generateAgreementsId(agreements, futureEntityId)
                                }).
                                    success(function (err) {
                                        agreementsSuccessCallback();
                                        resolveParent(deployerResponse);

                                    }).
                                    error(function (err) {
                                        //TODO: Rollback monitoring rules + monitor model + deployed app
                                        agreementsErrorCallback();
                                        rejectParent(err);
                                    })
                            }).
                            error(function (err) {
                                //TODO: Rollback monitor model + deployed app
                                monitoringRulesErrorCallback();
                                rejectParent(err);
                            })
                    }).
                    error(function (err) {
                        damErrorCallback();
                        rejectParent(err);
                    })

            });
            promise.success = function (fn) {
                promise.then(fn);
                return promise;
            }

            promise.error = function (fn) {
                promise.then(null, fn);
                return promise;
            }

            return promise;


        },
        removeProject: function (id) {
            return $http.delete("/api/deployer/applications/" + id);
        },
        getSensors: function (id) {
            var promise = new Promise(function (resolve, reject) {
                $http.get("/api/deployer/applications/" + id + "/sensors").
                    success(function (sensors) {
                        resolve(sensors);
                    }).
                    error(function (err) {
                        reject(Error(err));
                    });
            });
            promise.success = function (fn) {
                promise.then(fn);
                return promise;
            }

            promise.error = function (fn) {
                promise.then(null, fn);
                return promise;
            }

            return promise;
        },
        getAvailableMetrics: function (applicationId) {
            var promise = new Promise(function (resolve, reject) {
                $http.get("/api/deployer/applications/" + applicationId + "/metrics").
                    success(function (sensors) {
                        resolve(sensors);
                    }).
                    error(function (err) {
                        reject(Error(err));
                    });
            });
            promise.success = function (fn) {
                promise.then(fn);
                return promise;
            }

            promise.error = function (fn) {
                promise.then(null, fn);
                return promise;
            }

            return promise;
        },
        getMetricValue: function (applicationId, entityId, metricId) {
            var promise = new Promise(function (resolve, reject) {
                $http.get("/api/deployer/applications/" + applicationId + "/metrics/value?entityId=" + entityId + "&metricId=" + metricId).
                    success(function (value) {
                        resolve(value);
                    }).
                    error(function (err) {
                        reject(Error(err));
                    });
            });
            promise.success = function (fn) {
                promise.then(fn);
                return promise;
            }

            promise.error = function (fn) {
                promise.then(null, fn);
                return promise;
            }

            return promise;
        },
        matchmake: function (aam) {
            var promise = new Promise(function (resolve, reject) {
                $http.post("/api/planner/matchmake", aam)
                    .success(function (value) {
                        resolve(value);
                    })
                    .error(function (err) {
                        reject(Error(err));
                    });
            });
            promise.success = function (fn) {
                promise.then(fn);
                return promise;
            }

            promise.error = function (fn) {
                promise.then(null, fn);
                return promise;
            }

            return promise;
        },
        optimize: function (adp) {
            var promise = new Promise(function (resolve, reject) {
                $http.post("/api/planner/optimize", adp)
                    .success(function (value) {
                        resolve(value);
                    })
                    .error(function (err) {
                        reject(Error(err));
                    })
            });
            promise.success = function (fn) {
                promise.then(fn);
                return promise;
            }

            promise.error = function (fn) {
                promise.then(null, fn);
                return promise;
            }

            return promise;
        },
        getAgreementStatus: function (applicationId) {
            var promise = new Promise(function (resolve, reject) {
                $http.get("/api/sla/agreements/" + applicationId + "/status").
                 success(function (value) {
                    resolve(value);
                 }).
                 error(function (err) {
                    reject(Error(err));
                 });


            });
            promise.success = function (fn) {
                promise.then(fn);
                return promise;
            }

            promise.error = function (fn) {
                promise.then(null, fn);
                return promise;
            }

            return promise;
        },
        getAgreements: function (applicationId) {
            var promise = new Promise(function (resolve, reject) {
                $http.get("/api/sla/agreements/" + applicationId).
                success(function (value) {
                    resolve(value);
                 }).
                 error(function (err) {
                    reject(Error(err));
                 })
            });
            promise.success = function (fn) {
                promise.then(fn);
                return promise;
            }

            promise.error = function (fn) {
                promise.then(null, fn);
                return promise;
            }

            return promise;
        }

    };
});


seacloudsDashboard.controller('GlobalCtrl', function ($scope, Page, UserCredentials, SeaCloudsApi) {
    $scope.Page = Page;
    $scope.UserCredentials = UserCredentials;
    $scope.SeaCloudsApi = SeaCloudsApi;

    if (!UserCredentials.isUserAuthenticated()) {
        $location.path('/access-restricted.html');
    }
});

