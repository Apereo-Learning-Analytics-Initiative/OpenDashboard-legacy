(function(angular){
    'use strict';
    
    angular
    .module('OpenDashboard')
    .controller('AddDashboardController', function($log, $scope, $state, $translate, $translatePartialLoader, Notification,
                                            ContextMappingService, contextMapping, dataService) {
    	$translatePartialLoader.addPart('dashboard');
        $translate.refresh();
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
                    Notification.error('Unable to add dashboard.');
                });
        };
        
        $scope.cancel = function() {
            $state.go('index.selectCard', {cmid:$scope.$parent.contextMapping.id,dbid:$scope.$parent.activeDashboard.id});
        };
        
        $scope.onDashboardTitleAdd = function(form, field, model){
        	var dashboardForm = $scope[form];
        	var unique = dataService.checkUniqueValue($scope.contextMapping.dashboards, field, model);
        	dashboardForm[field].$invalid = !unique;
        	dashboardForm[field].$valid = unique;
        	dashboardForm.$invalid = !unique;
        	dashboardForm.$valid = unique;
        	
        };
    });
    
    angular
    .module('OpenDashboard')
    .controller('DashboardController', function($log, $scope, $state, $translate, $translatePartialLoader,
                                            _ , registry, contextMapping, dashboardId) {
    	$translatePartialLoader.addPart('dashboard');
        $translate.refresh();
    	$scope.showNavbar = false;
    	$scope.$parent.contextMapping = contextMapping;
        $scope.$parent.activeDashboard = _.find($scope.$parent.contextMapping.dashboards,{'id':dashboardId});
        $scope.cards = registry.registry;
        
        $scope.showEditLink = function (card) {
          var show = false;
          
          if (card && card.config) {
            var config = card.config;
            for(var prop in config) {
                if(config.hasOwnProperty(prop)) {
                    show = true;
                    break;
                }
            }
          }
          
          return show;
        }
        
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