/*******************************************************************************
 * Copyright 2015 Unicon (R) Licensed under the
 * Educational Community License, Version 2.0 (the "License"); you may
 * not use this file except in compliance with the License. You may
 * obtain a copy of the License at
 *
 * http://www.osedu.org/licenses/ECL-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an "AS IS"
 * BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing
 * permissions and limitations under the License.
 *******************************************************************************/
(function(angular){
    'use strict';
    
    angular
    .module('OpenDashboard')
    .controller('AddDashboardController', function($scope, $state, Notification, ContextMappingService, contextMapping, dataService) {
        $scope.$parent.contextMapping = contextMapping;
        $scope.dashboard = {};
        
        $scope.save = function () {
            ContextMappingService
            .addDashboard($scope.$parent.contextMapping, $scope.dashboard)
            .then(
                function (updatedContextMapping) {
                    $state.go('index.dashboard', {cmid:$scope.$parent.contextMapping.id, dbid:$scope.dashboard.id});                    
                },
                function (error) {
                    Notification.error('Unable to add dashboard.');
                });
        };
        
        $scope.onDashboardTitleAdd = function(form, field, model){
        	var dashboardForm = $scope[form];
        	var unique = dataService.checkUniqueValue($scope.contextMapping.dashboards, field, model);
        	dashboardForm[field].$invalid = !unique;
        	dashboardForm[field].$valid = unique;
        	dashboardForm.$invalid = !unique;
        	dashboardForm.$valid = unique;
        	
        };
    })
    .controller('RemoveDashboardController', function($scope, $state, Notification, ContextMappingService, contextMapping, dashboardId) {
        $scope.$parent.contextMapping = contextMapping;
        $scope.dashboardId = dashboardId;
        
        $scope.save = function () {
            ContextMappingService
            .removeDashboard($scope.$parent.contextMapping, $scope.dashboardId)
            .then(
                function (updatedContextMapping) {
                	if (updatedContextMapping.dashboards != null && updatedContextMapping.dashboards.length > 0) {
                	  $scope.$parent.activeDashboard = $scope.contextMapping.dashboards[0];
                	  $state.go('index.dashboard', {cmid:$scope.$parent.contextMapping.id, dbid:$scope.contextMapping.dashboards[0].id});
                	}
                	else {
                	  $state.go('index.addDashboard', {cmid:$scope.$parent.contextMapping.id});
                	}
                },
                function (error) {
                    Notification.error('Unable to remove dashboard.');
                });
        };
    });
    
    angular
    .module('OpenDashboard')
    .controller('DashboardController', function($scope, $state, _, registry, contextMapping, dashboardId) {
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
    .controller('ErrorController', function($scope, $location, errorCode) {
        $scope.errorCode = errorCode;
    });

})(angular);