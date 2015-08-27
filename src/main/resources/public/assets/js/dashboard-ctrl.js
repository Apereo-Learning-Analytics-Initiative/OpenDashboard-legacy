(function(angular){
    'use strict';
    
    angular
    .module('OpenDashboard')
    .controller('AddDashboardController', function($log, $scope, $state,
                                            ContextMappingService, contextMapping) {
        $scope.$parent.contextMapping = contextMapping;
        $scope.dashboard = {};
        
        $scope.save = function () {
            $log.log($scope.contextMapping);
            ContextMappingService
            .addDashboard($scope.$parent.contextMapping, $scope.dashboard)
            .then(
                function (updatedContextMapping) {
                    $state.go('index.dashboard', {cmid:$scope.$parent.contextMapping.id, dbid:$scope.dashboard.id});                    
                },
                function (error) {
                    $log.error(error);
                });
        };
    });
    
    angular
    .module('OpenDashboard')
    .controller('DashboardController', function($log, $scope, $state,
                                            _ , registry, ContextService,
                                            contextMapping, dashboard) {

    	$scope.showNavbar = false;
        $scope.contextMapping = contextMapping;
        $scope.$parent.activeDashboard = dashboard;
        $scope.cards = registry.registry;
        
        if (!$scope.$parent.activeDashboard.cards || $scope.$parent.activeDashboard.cards.length == 0) {
          $state.go('index.selectCard', {cmid:$scope.$parent.contextMapping.id, dbid:$scope.$parent.activeDashboard.id});
        }
    });
    
    angular
    .module('OpenDashboard')
    .controller('ErrorController', function($log, $scope, $location, $translate, $translatePartialLoader, errorCode) {
        $translatePartialLoader.addPart('error');
        $translate.refresh();
        $scope.errorCode = errorCode;
    });

})(angular);