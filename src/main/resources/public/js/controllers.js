(function(){
	'use strict';
	
	angular
		.module('OpenDashboard')
		.controller('NavbarController', function($scope, $http, $window){
			
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
})();

(function(){
	'use strict';
	
	angular
		.module('OpenDashboard')
		.controller('WelcomeController', function($scope, $http, $location,
													OpenDashboard_API, contextMapping, ContextMappingService){
			// TODO - move to router
			if (contextMapping && contextMapping.id) {
				var url = '/context/' + contextMapping.id;
				$location.path(url);
			}	
		
			$scope.showWelcome = contextMapping ? false : true;
			
			$scope.saveContextMapping = function() {
				var inbound_lti_launch_request = null;
				if (OpenDashboard_API) {
					inbound_lti_launch_request = OpenDashboard_API.getInbound_LTI_Launch();
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
})();

(function(){
	'use strict';
	
	angular
		.module('OpenDashboard')
		.controller('DashboardController', function($scope, $location, $route, ngDialog, _, OpenDashboard_API,
												contextMappingId, cardInstanceId, selectedCard, installedCards, availableCards, CardInstanceService){
		$scope.contextMappingId = contextMappingId;
		$scope.cardInstanceId = cardInstanceId;
		
		if (!selectedCard) {
			selectedCard = new CardInstance({});
			selectedCard.id = 'home';
		}
		
		$scope.selectedCard = selectedCard;
		$scope.installedCards = installedCards;
		$scope.availableCards = availableCards;
		$scope.isInstructor = OpenDashboard_API.isInstructor();
		$scope.isNotStudent = OpenDashboard_API.isNotStudent();
		$scope.isStudent = OpenDashboard_API.isStudent();
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
				cardInstance.context = $scope.contextMappingId;
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
						var url = '/context/' + $scope.contextMappingId + '/' + savedCardInstance.id;
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
			if (card == 'home') {
				var cardInstance = new CardInstance({});
				cardInstance.id = 'home';
				$scope.selectedCard = cardInstance;
			}
			else {
				$scope.selectedCard = card;
			}
		};
	
	});
})();