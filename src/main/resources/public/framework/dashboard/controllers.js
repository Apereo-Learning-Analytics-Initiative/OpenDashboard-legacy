(function(angular){
    'use strict';
    
    angular
    .module('OpenDashboard')
    .controller('AddDashboardController', function($log, $scope, $location,
                                            ContextMappingService,
                                            contextMapping) {
        $scope.contextMapping = contextMapping;
        $scope.dashboard = {};
        
        $scope.save = function () {
            $log.log($scope.contextMapping);
            ContextMappingService
            .addDashboard($scope.contextMapping, $scope.dashboard)
            .then(
                function (updatedContextMapping) {
                    var url = '/cm/' + $scope.contextMapping.id + '/dashboard/' + $scope.dashboard.id;
                    $location.path(url);
                },
                function (error) {
                    $log.error(error);
                });
        };
    });
    
    angular
    .module('OpenDashboard')
    .controller('DashboardController', function($log, $scope, $location, $route,
                                            _ , registry, ContextService,
                                            contextMapping, dashboard) {
    	$scope.isStudent = ContextService.getCurrentUser().isStudent();
    	$scope.showNavbar = false;
        $scope.showAddCard = ($route.current.params.addCard == 'true');
        $scope.contextMapping = contextMapping;
        $scope.activeDashboard = dashboard;
        $scope.cards = registry.registry;
        
        $scope.toggleNavbar = function () {
        	$scope.showNavbar = !$scope.showNavbar;
        };
        
        $scope.addCard = function(cardType) {
            $log.log('add card type: '+cardType);
            var url = '/cm/'+$scope.contextMapping.id+'/dashboard/'+$scope.activeDashboard.id+'/add/'+cardType;
            $location.path(url).search({});
        };
        
        $scope.editCard = function(card) {
            $log.log('edit card: '+card);
            var url = '/cm/'+$scope.contextMapping.id+'/dashboard/'+$scope.activeDashboard.id+'/edit/'+card.id;
            $location.path(url).search({});
        };
        
        $scope.removeCard = function(card) {
            $log.log('remove card: '+card);
            var url = '/cm/'+$scope.contextMapping.id+'/dashboard/'+$scope.activeDashboard.id+'/remove/'+card.id;
            $location.path(url).search({});
        };
        
    });

})(angular);