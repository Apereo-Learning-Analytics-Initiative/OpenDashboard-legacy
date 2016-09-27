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
'use strict';

angular
.module('OpenDashboard')
.controller('AddPreconfiguredDashboardCtrl',

function AddPreconfiguredDashboardCtrl($scope, $state, _, TenantService, UUIDService, Notification, registry) {
	$scope.cards = registry.registry;
	$scope.dashboard = {};
	
	  $scope.isConfigured = function(cardType) {
		var isConfigured = false;
		
		if ($scope.dashboard.cards) {
		  var indx = _.find($scope.dashboard.cards,{'cardType':cardType});
		  if (indx) isConfigured = true;
		}
		
		return isConfigured;
	  }
	
	$scope.addCard = function(card) {
        var newCard = angular.copy($scope.cards[card.cardType]);
        
        var cardConfig = newCard.config;
        if (cardConfig && cardConfig.length > 0) {
          // TODO
        }
        else {
          newCard.config = {};
          if (!$scope.dashboard.cards) {
            $scope.dashboard.cards = [];
          }
          $scope.dashboard.cards.push(newCard);
        }
	};
	
	$scope.removeCard = function(card) {
	  $scope.dashboard.cards = _.reject($scope.dashboard.cards,{'cardType':card.cardType});
	};
	
	$scope.save = function () {
	  $scope.submitted = true;
	  
	  if (!$scope.tenant.dashboards) {
	    $scope.tenant.dashboards = [];
	  }
	  $scope.dashboard.id = UUIDService.generate();
	  $scope.tenant.dashboards.push($scope.dashboard);


	  TenantService
	  .updateTenant($scope.tenant)
	  .then(
		function(data) {
		  Notification.success('Preconfigured dashboard created');
		  $scope.submitted = false;
		  $state.go('index.admin.tenants.tenant', {id:$scope.tenant.id}, { reload: true });
		},
		function(error) {
		  Notification.error('Unable to create preconfigured dashboard');
		  $scope.submitted = false;
		}
		
		
	  );
	};
	
});

angular
.module('OpenDashboard')
.controller('EditPreconfiguredDashboardCtrl',

function EditPreconfiguredDashboardCtrl($scope, $state, _, TenantService, Notification, registry, preconfiguredDashboard) {
	$scope.cards = registry.registry;
	$scope.dashboard = preconfiguredDashboard;
	
	  $scope.isConfigured = function(cardType) {
		var isConfigured = false;
		
		if ($scope.dashboard.cards) {
		  var indx = _.find($scope.dashboard.cards,{'cardType':cardType});
		  if (indx) isConfigured = true;
		}
		
		return isConfigured;
	  }
	
	$scope.addCard = function(card) {
        var newCard = angular.copy($scope.cards[card.cardType]);
        
        var cardConfig = newCard.config;
        if (cardConfig && cardConfig.length > 0) {
          // TODO
        }
        else {
          newCard.config = {};
          if (!$scope.dashboard.cards) {
            $scope.dashboard.cards = [];
          }
          $scope.dashboard.cards.push(newCard);
        }
	};
	
	$scope.removeCard = function(card) {
	  $scope.dashboard.cards = _.reject($scope.dashboard.cards,{'cardType':card.cardType});
	};
	
	$scope.save = function () {
	  $scope.submitted = true;
	  
	  var index = _.indexOf($scope.tenant.dashboards, _.find($scope.tenant.dashboards, { 'id': $scope.dashboard.id }));
      $scope.tenant.dashboards.splice(index, 1, $scope.dashboard);

	  TenantService
	  .updateTenant($scope.tenant)
	  .then(
		function(data) {
		  Notification.success('Preconfigured dashboard updated');
		  $scope.submitted = false;
		  $state.go('index.admin.tenants.tenant', {id:$scope.tenant.id}, { reload: true });
		},
		function(error) {
		  Notification.error('Unable to create preconfigured dashboard');
		  $scope.submitted = false;
		}
		
		
	  );
	};
	
});

angular
.module('OpenDashboard')
.controller('RemovePreconfiguredDashboardCtrl',

function RemovePreconfiguredDashboardCtrl($scope, $state, _, TenantService, Notification, preconfiguredDashboard) {

	$scope.preconfiguredDashboard = preconfiguredDashboard;
	
	$scope.remove = function () {
	  $scope.submitted = true;
	  var index = _.indexOf($scope.tenant.dashboards, _.find($scope.tenant.dashboards, { 'id': $scope.preconfiguredDashboard.id }));
      $scope.tenant.dashboards.splice(index, 1);

	  TenantService
	  .updateTenant($scope.tenant)
	  .then(
		function(data) {
		  Notification.success('Preconfigured dashboard removed');
		  $scope.submitted = false;
		  $state.go('index.admin.tenants.tenant', {id:$scope.tenant.id}, { reload: true });
		},
		function(error) {
		  Notification.error('Unable to remove preconfigured dashboard');
		  $scope.submitted = false;
		}
		
		
	  );
	};
});
