var OpenDashboard = angular.module('OpenDashboard', ['ngRoute', 'OpenDashboardControllers', 'OpenDashboardServices']);

OpenDashboard.config(['$routeProvider', function($routeProvider) {
	$routeProvider.
    when('/', {
      templateUrl: '/html/welcome.html',
      controller: 'WelcomeController',
      resolve: {
    	contextMapping: function(ContextMappingService, $window) {
    		var inbound_lti_launch_request = {};
	    	if ($window && $window.inbound_lti_launch_request) {
	    		inbound_lti_launch_request = $window.inbound_lti_launch_request;
	    	}
    		return ContextMappingService.get(inbound_lti_launch_request.oauth_consumer_key, inbound_lti_launch_request.context_id);
    	}
      }
    }).
    when('/context/:id', {
      templateUrl: '/html/dashboard.html',
      controller: 'DashboardController',
      resolve: {
    	context: function($route) {
    		return $route.current.params.id;
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
}]);
