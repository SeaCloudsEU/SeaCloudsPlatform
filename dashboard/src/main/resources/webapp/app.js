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
    'jlareau.pnotify',
    'ngAnimate',
    'angular-loading-bar',
    'seacloudsDashboard.header',
    'seacloudsDashboard.footer',
    'seacloudsDashboard.about',
    'seacloudsDashboard.apiclient',
    'seacloudsDashboard.applications',
    'seacloudsDashboard.applications.application',
    'seacloudsDashboard.wizards.addApplication'
]);

seacloudsDashboard.config(['$routeProvider', '$httpProvider', 'cfpLoadingBarProvider',
    function ($routeProvider, $httpProvider, cfpLoadingBarProvider) {
        // Disable Angular loading spinner
        cfpLoadingBarProvider.includeSpinner = false;

        // Allows $http.get/post... to use legacy success and error methods
        $httpProvider.useLegacyPromiseExtensions(true);
        $routeProvider.when("/", {redirectTo: '/applications'})
        //TODO: Create a not available view
        $routeProvider.otherwise({redirectTo: function(){
            window.location = "/not-available.html"
        }});
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

seacloudsDashboard.factory('SeaCloudsApi', function ($http) {
    return {
        getAboutSeaClouds: function () {
            return $http.get("/api/about");
        },
        getApplications: function () {
            return $http.get("/api/deployer/applications");
        },
        getApplication: function (seaCloudsId) {
            return $http.get("/api/deployer/applications/" + seaCloudsId);
        },
        addApplication: function (toscaDam) {
            return $http.post("/api/deployer/applications", toscaDam);
        },
        removeApplication: function (seaCloudsId) {
            return $http.delete("/api/deployer/applications/" + seaCloudsId);
        },
        getEntitySensorMapList: function (seaCloudsId) {
            return $http.get("/api/monitor/applications/" + seaCloudsId + "/sensors");
        },
        getEntityMetricMapList: function (seaCloudsId) {
            return $http.get("/api/monitor/applications/" + seaCloudsId + "/metrics");
        },
        getMetricValue: function (seaCloudsId, entityId, metricId) {
            return $http.get("/api/monitor/applications/" + seaCloudsId + "/entities/" + entityId + "/metrics/" +metricId);
        },
        getAgreement: function (seaCloudsId) {
           return $http.get("/api/sla/agreements/" + seaCloudsId);
        },
        getAgreementStatus: function (seaCloudsId) {
            return $http.get("/api/sla/agreements/" + seaCloudsId + "/status");
        },
        getAgreementTermViolations: function (seaCloudsId, termName) {
            return $http.get("/api/sla/agreements/" + seaCloudsId + "/terms/" + termName + "/violations");
        },
        getAamFromDesigner: function (designerTopology) {
            return $http.post("/api/aamwriter/translate", designerTopology);
        },
        getAdpList: function (aam) {
            return $http.post("/api/planner/adps", aam);
        },
        getDam: function (adp) {
            return $http.post("/api/planner/dam", adp);
        }
    };
});


seacloudsDashboard.controller('GlobalCtrl', function ($scope, Page, SeaCloudsApi) {
    $scope.Page = Page;
    $scope.SeaCloudsApi = SeaCloudsApi;
});

