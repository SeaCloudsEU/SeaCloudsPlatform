'use strict';

angular.module('seacloudsDashboard.footer', [])
    .directive('footer', function(){
        return {
            restrict: 'E',
            templateUrl: 'footer/footer.html'
        };
    });