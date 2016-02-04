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

angular.module('seacloudsDashboard.apiclient', ['ngRoute'])
    .config(['$routeProvider', function ($routeProvider) {
        $routeProvider.when('/apiclient', {
            templateUrl: 'apiclient/apiclient.html'
        })
    }])
    .controller('ApiCtrl', function ($scope) {
        $scope.Page.setTitle('SeaClouds Dashboard - API Client');
    })
    .directive('hideSwaggerHeader', function () {
        return {
            restrict: 'A',
            link: function (scope, element, attrs) {

                // Hack to hide Swagger iframe header by modifying the CSS
                var hideCss = {"visibility": "hidden", "height": "0px"};
                var $header = $(element).contents().find('#header');

                $(element).load(function () {
                    // cached body element overrides when src loads
                    // hence have to cache it again
                    $header = $(element).contents().find('#header');
                    $header.css(hideCss);
                });

                attrs.$observe('hideSwaggerHeader', function (value) {
                    $header.css(hideCss);
                }, true);
            }
        }
    });