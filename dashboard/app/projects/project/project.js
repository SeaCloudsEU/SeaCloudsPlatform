'use strict';

angular.module('seacloudsDashboard.projects.project', ['ngRoute', 'seacloudsDashboard.projects', 'seacloudsDashboard.projects.project.status',
    'seacloudsDashboard.projects.project.monitor', 'seacloudsDashboard.projects.project.sla'])
    .config(['$routeProvider', function ($routeProvider) {
        $routeProvider.when('/projects/:project', {
            templateUrl: 'projects/project/project.html'
        })
    }])
    .controller('ProjectCtrl', function ($scope, $routeParams, $location, $interval, notificationService) {
        $scope.Page.setTitle('SeaClouds Dashboard - Project details');

        $scope.project = undefined;
        
        $scope.isLoaded = false;

        $scope.SeaCloudsApi.getProject($routeParams.project).
            success(function(project){
                $scope.project = project;
                $scope.isLoaded = true;
            }).
            error(function(){
                notificationService.error("Cannot retrieve the project, please try it again");
            })


        $scope.updateFunction = undefined;

        $scope.updateFunction = $interval(function () {
            $scope.SeaCloudsApi.getProject($routeParams.project).
                success(function (project) {
                    $scope.project = project;
                }).
                error(function () {
                    //TODO: Handle the error better than showing a notification
                    notificationService.error("Unable to retrieve the project");

                })
        }, 5000);

        $scope.$on('$destroy', function () {
            if($scope.updateFunction){
                $interval.cancel($scope.updateFunction);
            }
        });


        $scope.$watch('projects', function (data) {
            $scope.SeaCloudsApi.getProject($routeParams.project).
                success(function(project){
                    $scope.project = project;
                }).
                error(function(){
                    notificationService.error("Cannot retrieve the project, please try it again");
                })
        })

        var tabSelected = 1;

        $scope.getSelectedTab = function () {
            return tabSelected;
        }

        $scope.setSelectedTab = function (tab) {
            tabSelected = tab;
        }

        $scope.removeProject = function () {
            $scope.SeaCloudsApi.removeProject($scope.project.id).
                success(function () {
                    $location.path("/projects")
                    notificationService.success("The application will be removed soon");
                }).
                error(function () {
                    notificationService.error("An error happened during the removal process, please try it again");
                })
        }

    });