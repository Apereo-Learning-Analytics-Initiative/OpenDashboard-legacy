var OpenDashboard = angular.module('OpenDashboard', ['ngRoute', 'OpenDashboardControllers', 'OpenDashboardServices', 'LTICard', 'OpenLRSCard', 'RssReaderCard']);

OpenDashboard.config(['$routeProvider', '$locationProvider', function($routeProvider, $locationProvider) {
	$routeProvider.
    when('/', {
      templateUrl: '/html/welcome.html',
      controller: 'WelcomeController',
      resolve: {
    	contextMapping: function(ContextMappingService, $window) {
    		var inbound_lti_launch_request = {};
	    	if ($window && $window.OpenDashboard_API) {
	    		inbound_lti_launch_request = $window.OpenDashboard_API.getInbound_LTI_Launch();
	    	}
    		return ContextMappingService.get(inbound_lti_launch_request.oauth_consumer_key, inbound_lti_launch_request.context_id);
    	}
      }
    }).
    when('/context/:id/:cardInstanceId?', {
      templateUrl: '/html/dashboard.html',
      controller: 'DashboardController',
      resolve: {
    	context: function($route) {
    		return $route.current.params.id;
    	},
    	selectedCard: function(CardInstanceService, $route) {
    		return CardInstanceService.getOne($route.current.params.id,$route.current.params.cardInstanceId);
    	},
    	installedCards: function(CardInstanceService, $route) {
    		return CardInstanceService.get($route.current.params.id);
    	},
    	availableCards: function(CardService, $route) {
    		return CardService.get($route.current.params.id);
    	}
      }
    }).
    otherwise({
      redirectTo: '/'
    });
    
    $locationProvider.html5Mode(true);
}]);
