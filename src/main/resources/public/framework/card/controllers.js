(function(angular){
    'use strict';
        
    angular
    .module('OpenDashboard')
    .controller('AddCardController', function($log, $scope, $location,
                                            _ , registry, ContextMappingService,
                                            contextMapping, dashboard, cardType) {
        $scope.contextMapping = contextMapping;
        $scope.activeDashboard = dashboard;
        $scope.cards = registry.registry;
        $scope.cardType = cardType;
        $scope.newConfig = {};
        $scope.card = angular.copy($scope.cards[$scope.cardType]);
        
        $scope.cancel = function() {
            var url = '/cm/'+$scope.contextMapping.id+'/dashboard/'+$scope.activeDashboard.id;
            $location.path(url);
        };
        
        $scope.addCard = function() {
            $scope.card.config = $scope.newConfig;
            ContextMappingService
            .addCard($scope.contextMapping, $scope.activeDashboard, $scope.card)
            .then(
                function (updatedContextMapping) {
                    $scope.cancel();
                },
                function (error) {
                    $log.error(error);
                });
        };
        
    });
    
    angular
    .module('OpenDashboard')
    .controller('EditCardController', function($log, $scope, $location,
                                            _ , registry, ContextMappingService,
                                            contextMapping, dashboard, card) {
        $scope.contextMapping = contextMapping;
        $scope.activeDashboard = dashboard;
        $scope.card = card;
        $scope.cardConfig = angular.copy(registry.registry[$scope.card.cardType].config);
        
        $scope.cancel = function() {
            var url = '/cm/'+$scope.contextMapping.id+'/dashboard/'+$scope.activeDashboard.id;
            $location.path(url);
        };
        
        $scope.editCard = function() {
            ContextMappingService
            .update($scope.contextMapping)
            .then(
                function (updatedContextMapping) {
                    $scope.cancel();
                },
                function (error) {
                    $log.error(error);
                });
        };
        
    });

    
    angular
    .module('OpenDashboard')
    .controller('RemoveCardController', function($log, $scope, $location,
                                            _ , DashboardService,
                                            contextMapping, dashboard, card) {
        $scope.contextMapping = contextMapping;
        $scope.activeDashboard = dashboard;
        $scope.card = card;
        
        $scope.cancel = function() {
            var url = '/cm/'+$scope.contextMapping.id+'/dashboard/'+$scope.activeDashboard.id;
            $location.path(url);
        };
        
        $scope.removeCard = function() {
            DashboardService
            .removeCard($scope.card,$scope.activeDashboard,$scope.contextMapping)
            .then(
                function(updatedContextMapping) {
                    $scope.cancel();
                },
                function (error) {
                    $log.error(error);
                }
            );
        }
        
        
    });

})(angular);