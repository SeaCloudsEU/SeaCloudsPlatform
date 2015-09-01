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
(function (window, document, angular) {
    'use strict';

    angular.module('angularTopologyEditor', [])

        .directive('topologyEditor', function ($window) {
            return {
                restrict: 'E',
                scope: {topology: '='},
                link: function (scope, elem, attrs) {

                    var drawCanvas = function(){
                        var randLetter = String.fromCharCode(65 + Math.floor(Math.random() * 26));
                        var uniqid = randLetter + Date.now();
                        elem.html('<div id="' + uniqid + '" class="topology-editor"></div>');

                        var canvasOptions = {
                            addlinkcallback: Editor.addlinkcallback,
                            changehandler: function(model) {
                                console.log("New model is " + model);
                            }
                        };

                        canvasOptions.height = (!attrs.height)? 450 : attrs.height;
                        canvasOptions.width = (!attrs.width)? $window.innerWidth : attrs.width;

                        var canvas = Canvas();
                        canvas.init(uniqid, canvasOptions);

                        Editor.init(canvas);
                        Editor.fromjson(scope.topology);
                        canvas.restart();
                    };

                    // Resize watcher
                    angular.element($window).bind('resize', function () {
                        scope.windowHeight = $window.innerHeight;
                        scope.windowWidth = $window.innerWidth;
                        //TODO: Fix resize

                        scope.$apply();
                    });

                    // Visibility watcher
                    scope.visible = (typeof scope.visible == 'undefined')? true : scope.visible;

                    drawCanvas();
                }
            };
        })
        .directive('topologyStatus', function ($window) {
            return {
                restrict: 'E',
                scope: {topology: '='},
                link: function (scope, elem, attrs) {

                    var drawCanvas = function(){
                        var randLetter = String.fromCharCode(65 + Math.floor(Math.random() * 26));
                        var uniqid = randLetter + Date.now();
                        elem.html('<div id="' + uniqid + '" class="topology-config"></div>');

                        var canvasOptions = {
                            addlinkcallback: Editor.addlinkcallback,
                            changehandler: function(model) {
                                console.log("New model is " + model);
                            }
                        };

                        canvasOptions.height = (!attrs.height)? 450 : attrs.height;
                        canvasOptions.width = (!attrs.width)? $window.innerWidth : attrs.width;
                        

                        var canvas = Canvas();
                        canvas.init(uniqid, canvasOptions);

                        Status.init(canvas);
                        Status.fromjson(scope.topology);
                        canvas.restart();
                    };

                    // Resize watcher
                    angular.element($window).bind('resize', function () {
                        scope.windowHeight = $window.innerHeight;
                        scope.windowWidth = $window.innerWidth;
                        //TODO: Fix resize

                        scope.$apply();
                    });

                    // Visibility watcher
                    scope.visible = (typeof scope.visible == 'undefined')? true : scope.visible;

                    drawCanvas();
                }
            };
        });
})(window, document, angular);

