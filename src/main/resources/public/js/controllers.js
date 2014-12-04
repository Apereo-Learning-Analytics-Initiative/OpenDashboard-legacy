/**
 * Copyright 2014 Unicon (R)
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use $scope file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

var OpenDashboardControllers = angular.module('OpenDashboardControllers', ['ngDialog', 'OpenDashboardServices']);

OpenDashboardControllers.controller('NavbarController', function($scope, $http, $window){
	
	if ($window && $window.OpenDashboard_API) {
		$scope.inbound_lti_launch_request = $window.OpenDashboard_API.getInbound_LTI_Launch();
	}
	
	$scope.isEmbed = function() {
		var isEmbed = false;
		try {
			isEmbed = $window != $window.parent;
		}
		catch (e) {}
		return isEmbed;
	};
	
});

OpenDashboardControllers.controller('WelcomeController', function($scope, $http, $window, $location,
										contextMapping, ContextMappingService){
	// TODO - move to router
	if (contextMapping && contextMapping.id) {
		var url = '/context/' + contextMapping.id;
		$location.path(url);
	}	

	$scope.showWelcome = contextMapping ? false : true;
	
	$scope.saveContextMapping = function() {
		var inbound_lti_launch_request = null;
		if ($window && $window.OpenDashboard_API) {
			inbound_lti_launch_request = $window.OpenDashboard_API.getInbound_LTI_Launch();
		}
		
		var cm = {};
		cm.key = inbound_lti_launch_request.oauth_consumer_key;
		// TODO handle non-context case
		cm.context = inbound_lti_launch_request.context_id;
		ContextMappingService.create(cm)
			.then(function(savedContextMapping) {
				$scope.showWelcome = false;
				var url = '/context/' + savedContextMapping.id;
				$location.path(url);
			});
	};
	
});

OpenDashboardControllers.controller('DashboardController', function($window, $scope, $location, $route, ngDialog, context, selectedCard, installedCards, availableCards, CardInstanceService){
	$scope.context = context;
	$scope.selectedCard = selectedCard;
	$scope.installedCards = installedCards;
	$scope.availableCards = availableCards;
	$scope.isInstructor = $window.OpenDashboard_API.isInstructor();
	$scope.isNotStudent = $window.OpenDashboard_API.isNotStudent();
	$scope.isStudent = $window.OpenDashboard_API.isStudent();
	$scope.dialog = null;
	$scope.card = null;
	$scope.cardConfiguration = {};
	
	$scope.saveCardInstance = function() {
		
		if ($scope.selectedCard) {
			CardInstanceService.update($scope.selectedCard)
			.then(function(savedCardInstance) {
				$scope.card = null;
				$scope.cardConfiguration = {};
				$scope.dialog.close();
				$route.reload();
			});		
		}
		else {
			var cardInstance = new CardInstance({});
			cardInstance.setCard($scope.card);
			cardInstance.setConfig($scope.cardConfiguration);
			cardInstance.context = $scope.context;
			cardInstance.sequence = $scope.installedCards ? $scope.installedCards.length : 0;
			
			CardInstanceService.create(cardInstance)
				.then(function(savedCardInstance){
					if (!$scope.installedCards) {
						$scope.installedCards = [];
					}
					$scope.installedCards.push($scope.selectedCard);
					$scope.card = null;
					$scope.cardConfiguration = {};
					$scope.dialog.close();
					var url = '/context/' + context + '/' + savedCardInstance.id;
					$location.path(url);
				});
		}		
	};
	
	$scope.addCardInstance = function(card) {
		$scope.card = card;
		$scope.cardConfiguration = {};
		$scope.dialog = ngDialog.open(
		{
			template:'/html/cards/'+$scope.card.cardType+'/config.html',
			scope: $scope
		});
	};
	
	$scope.editCardInstance = function(card) {
		$scope.card = card;
		$scope.cardConfiguration = card.config;
		$scope.dialog = ngDialog.open(
		{
			template:'/html/cards/'+$scope.card.cardType+'/config.html',
			scope: $scope
		});
	};
	
	$scope.removeCardInstance = function(card) {
		$scope.card = card;
		$scope.dialog = ngDialog.open(
		{
			template:'/html/removeCard.html',
			scope: $scope
		});
	};
	
	$scope.confirmRemoveCardInstance = function() {
		CardInstanceService.remove($scope.selectedCard)
			.then(function() {
				$scope.installedCards = _.without($scope.installedCards, _.findWhere($scope.installedCards, {id:$scope.selectedCard.id}));
				$scope.selectedCard = null;
				$scope.dialog.close();
			});
	};

	$scope.selectCard = function(card) {
		$scope.selectedCard = card;
	};

});
