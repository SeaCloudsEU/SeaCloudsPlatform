'use strict';


angular.module('seacloudsDashboard.projects.project.status', ['datatables'])
    .directive('status', function () {
        return {
            restrict: 'E',
            templateUrl: 'projects/project/status/status.html',
            controller: 'StatusCtrl'
        };
    })
    .controller('StatusCtrl', function ($scope, $interval, DTOptionsBuilder, notificationService) {

        $scope.entities = undefined

        $scope.SeaCloudsApi.getSensors($scope.project.id).
            success(function(data){
                $scope.entities = data;
            }).
            error(function(){
                //TODO: Handle the error better thwan showing a notification
                notificationService.error("Unable to retrieve the sensors");
            });


        $scope.dtOptions = DTOptionsBuilder.newOptions().withDisplayLength(5);

        var statusSetupActive = false;

        $scope.isStatusSettingVisible = function(){
            return statusSetupActive;
        }

        $scope.showStatusSettings = function(status){
            statusSetupActive = status;
        }

        var generateTopology = function (parentEntity) {
            var parentTopology = {
                nodes: [],
                links: []
            }

            if (parentEntity) {
                var type = undefined;
                if(parentEntity.type.search("database") >= 0){
                    type = "Database"
                }else if(parentEntity.type.search("webapp") >= 0){
                    type = "WebApplication"
                }else if(parentEntity.type.search("SameServerEntity") >= 0){
                    type = "Host"
                }else {
                    type = "BasicApplication"
                }

                parentTopology.nodes.push(
                    {
                        name: parentEntity.name,
                        label: parentEntity.name,
                        properties: {
                            status: parentEntity.serviceState
                        },
                        type : type
                    })

                if (parentEntity.children) {
                    parentEntity.children.forEach(function (childEntity) {
                        parentTopology.links.push({
                            source: parentEntity.name,
                            target: childEntity.name,
                            properties: {}
                        })

                        var childTopology = generateTopology(childEntity);
                        parentTopology.nodes = parentTopology.nodes.concat(childTopology.nodes);
                        parentTopology.links = parentTopology.links.concat(childTopology.links);
                    })
                }
            }

            return parentTopology;


        }
        $scope.topology = generateTopology($scope.project);
    });