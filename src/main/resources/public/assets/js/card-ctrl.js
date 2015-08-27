(function(angular){
    'use strict';
        
    angular
    .module('OpenDashboard')
    .controller('SelectCardController', function($log, $scope, $state,
                                            _ , registry, ContextMappingService,
                                            contextMapping, dashboard) {
        $scope.$parent.contextMapping = contextMapping;
        $scope.$parent.activeDashboard = dashboard;
        $scope.cards = registry.registry;
        
        $scope.addCard = function(cardType) {
            $log.log('add card type: '+cardType);
            $state.go('index.addCard',{cmid:$scope.$parent.contextMapping.id,dbid:$scope.$parent.activeDashboard.id,cardType:cardType})
        };
    });

    angular
    .module('OpenDashboard')
    .controller('AddCardController', function($log, $scope, $state,
                                            _ , registry, ContextMappingService,
                                            contextMapping, dashboard, card) {
        $scope.$parent.contextMapping = contextMapping;
        $scope.$parent.activeDashboard = dashboard;
        $scope.newConfig = {};
        $scope.card = card;
        
        $scope.cancel = function() {
            $state.go('index.selectCard', {cmid:$scope.$parent.contextMapping.id,dbid:$scope.$parent.activeDashboard.id});
        };
        
        $scope.addCard = function() {
            $scope.card.config = $scope.newConfig;
            ContextMappingService
            .addCard($scope.$parent.contextMapping, $scope.$parent.activeDashboard, $scope.card)
            .then(
                function (updatedContextMapping) {
                    $state.go('index.dashboard',{cmid:$scope.$parent.contextMapping.id,dbid:$scope.$parent.activeDashboard.id});
                },
                function (error) {
                    $log.error(error);
                });
        };
        
    });
    
    angular
    .module('OpenDashboard')
    .controller('EditCardController', function($log, $scope, $state,
                                            _ , registry, ContextMappingService,
                                            contextMapping, dashboard, card) {
        $scope.$parent.contextMapping = contextMapping;
        $scope.$parent.activeDashboard = dashboard;
        $scope.card = card;
        $scope.cardConfig = angular.copy(registry.registry[$scope.card.cardType].config);
        
        $scope.cancel = function() {
        	$state.go('index.dashboard',{cmid:$scope.$parent.contextMapping.id,dbid:$scope.$parent.activeDashboard.id});
        };
        
        $scope.editCard = function() {
            ContextMappingService
            .update($scope.contextMapping)
            .then(
                function (updatedContextMapping) {
                	$state.go('index.dashboard',{cmid:$scope.$parent.contextMapping.id,dbid:$scope.$parent.activeDashboard.id});
                },
                function (error) {
                    $log.error(error);
                });
        };
        
    });

    
    angular
    .module('OpenDashboard')
    .controller('RemoveCardController', function($log, $scope, $state,
                                            _ , DashboardService,
                                            contextMapping, dashboard, card) {
        $scope.$parent.contextMapping = contextMapping;
        $scope.$parent.activeDashboard = dashboard;
        $scope.card = card;
        
        $scope.cancel = function() {
        	$state.go('index.dashboard',{cmid:$scope.$parent.contextMapping.id,dbid:$scope.$parent.activeDashboard.id});
        };
        
        $scope.removeCard = function() {
            DashboardService
            .removeCard($scope.card,$scope.activeDashboard,$scope.contextMapping)
            .then(
                function(updatedContextMapping) {
                	$state.go('index.dashboard',{cmid:$scope.$parent.contextMapping.id,dbid:$scope.$parent.activeDashboard.id});
                },
                function (error) {
                    $log.error(error);
                }
            );
        }
        
        
    });

})(angular);