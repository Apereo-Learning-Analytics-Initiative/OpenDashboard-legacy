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
    .controller('SelectCardController', function($scope, $state, Notification, _, registry, ContextMappingService, contextMapping, dashboardId) {
        $scope.$parent.contextMapping = contextMapping;
        $scope.$parent.activeDashboard = _.find($scope.$parent.contextMapping.dashboards,{'id':dashboardId});
        $scope.cards = registry.registry;

        var activeDashboard = $scope.$parent.activeDashboard;
        
        $scope.addCard = function(cardType) {
          
          var card = angular.copy($scope.cards[cardType]);
          
          var cardConfig = card.config;
          if (cardConfig && cardConfig.length > 0) {
            $state.go('index.addCard',{cmid:$scope.$parent.contextMapping.id,dbid:$scope.$parent.activeDashboard.id,cardType:cardType});
          }
          else {
        	card.config = {};
            ContextMappingService
              .addCard($scope.$parent.contextMapping, $scope.$parent.activeDashboard, card)
              .then(
                  function (updatedContextMapping) {
                      $state.go('index.dashboard',{cmid:$scope.$parent.contextMapping.id,dbid:$scope.$parent.activeDashboard.id});
                  },
                  function (error) {
                      Notification.error('Unable to add card.');
                  });
          }
        };
        
        $scope.isConfigured = function(cardType) {
          var isConfigured = false;
          var card = _.find(activeDashboard.cards, {'cardType':cardType});
          if(card){
            return true;
          }
          return isConfigured;
        }
        
        $scope.editCard = function(cardType) {
          var card = _.find(activeDashboard.cards, {'cardType':cardType});
          if(card){
            $state.go('index.editCard', {cmid:$scope.$parent.contextMapping.id,dbid:activeDashboard.id,cid:card.id});
          }
          else{
            Notification.error('Unable to edit card. A card of type ' + cardType + ' does not exist.');
          }
        };
        
    });

    angular
    .module('OpenDashboard')
    .controller('AddCardController', function($scope, $state, Notification, _, registry, ContextMappingService, contextMapping, dashboardId, card) {
        $scope.$parent.contextMapping = contextMapping;
        $scope.$parent.activeDashboard = _.find($scope.$parent.contextMapping.dashboards,{'id':dashboardId});
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
                    Notification.error('Unable to add card.');
                });
        };
        
    });
    
    angular
    .module('OpenDashboard')
    .controller('EditCardController', function($scope, $state, Notification, _, registry, ContextMappingService, contextMapping, dashboardId, cardId) {
        $scope.$parent.contextMapping = contextMapping;
        $scope.$parent.activeDashboard = _.find($scope.$parent.contextMapping.dashboards,{'id':dashboardId});
        $scope.card = _.find($scope.$parent.activeDashboard.cards,{'id':cardId});
        $scope.cardConfig = angular.copy(registry.registry[$scope.card.cardType].config);
        $scope.newConfig = $scope.card.config;

        $scope.cancel = function() {
        	$state.go('index.dashboard',{cmid:$scope.$parent.contextMapping.id,dbid:$scope.$parent.activeDashboard.id});
        };
        
        $scope.editCard = function() {
            ContextMappingService
            .update($scope.$parent.contextMapping)
            .then(
                function (updatedContextMapping) {
                	$state.go('index.dashboard',{cmid:$scope.$parent.contextMapping.id,dbid:$scope.$parent.activeDashboard.id});
                },
                function (error) {
                    Notification.error('Unable to configure card.');
                });
        };
        
    });

    
    angular
    .module('OpenDashboard')
    .controller('RemoveCardController', function($scope, $state, Notification, _, ContextMappingService, contextMapping, dashboardId, cardId) {
        $scope.$parent.contextMapping = contextMapping;
        $scope.$parent.activeDashboard = _.find($scope.$parent.contextMapping.dashboards,{'id':dashboardId});
        $scope.card = _.find($scope.$parent.activeDashboard.cards,{'id':cardId});
        $scope.cancel = function() {
        	$state.go('index.dashboard',{cmid:$scope.$parent.contextMapping.id,dbid:$scope.$parent.activeDashboard.id});
        };
        
        $scope.removeCard = function() {
        	ContextMappingService
            .removeCard($scope.$parent.contextMapping, $scope.$parent.activeDashboard, $scope.card)
            .then(
                function(updatedContextMapping) {
                	$state.go('index.dashboard',{cmid:$scope.$parent.contextMapping.id,dbid:$scope.$parent.activeDashboard.id});
                },
                function (error) {
                    Notification.error('Unable to remove card.');
                }
            );
        }
    });

})(angular);