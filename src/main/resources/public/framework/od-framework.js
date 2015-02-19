(function(angular){
    'use strict';
    
    angular
    .module('OpenDashboardRegistry',[])
    .provider('registry', function() {
        var registry = {};
        var dashboards = {};
        this.register = function(key,card) {
            var c = angular.copy(card);
            registry[key] = c;
            return this;
        };
        this.registerDashboard = function(key,dashboard) {
            var d = angular.copy(dashboard);
            dashboards[key] = d;
            return this;
        };
        this.$get = function() {
            return {
                registry: registry,
                dashboards: dashboards
            };
        };
    });
    
    angular
    .module('underscore', [])
    .factory('_', function ($window) {
        return $window._;
    });

    angular
    .module('OpenDashboardAPI', ['underscore'])
    .factory('OpenDashboard_API', function ($log, $window, _) {
        return $window.OpenDashboardApi;
    });

    angular
    .module('OpenDashboardFramework', ['OpenDashboardRegistry', 'underscore', 'OpenDashboardAPI']);

})(angular);