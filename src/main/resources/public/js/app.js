(function() {
	'use strict';
	
	angular
		.module('underscore', [])
		.factory('_', function () {
			return window._;
		});
})();

(function() {
	'use strict';
	
	angular
		.module('OpenDashboardAPI', [])
		.factory('OpenDashboard_API', function () {
			return window.OpenDashboard_API;
		});
})();

(function() {
	'use strict';
	
	angular
		.module('OpenDashboard', ['ngRoute', 'ngDialog', 'angularDc', 'underscore', 'OpenDashboardAPI', 'LTICard', 'OpenLRSCard', 'RssReaderCard'])
		.config(['$routeProvider', '$locationProvider', function($routeProvider, $locationProvider) {
		$routeProvider.
		    when('/', {
		      templateUrl: '/html/welcome.html',
		      controller: 'WelcomeController',
		      resolve: {
		    	contextMapping: function(ContextMappingService, OpenDashboard_API) {
		    		var inbound_lti_launch_request = {};
			    	if (OpenDashboard_API) {
			    		inbound_lti_launch_request = OpenDashboard_API.getInbound_LTI_Launch();
			    	}
		    		return ContextMappingService.get(inbound_lti_launch_request.oauth_consumer_key, inbound_lti_launch_request.context_id);
		    	}
		      }
		    }).
		    when('/context/:contextMappingId/:cardInstanceId?', {
		      templateUrl: '/html/dashboard.html',
		      controller: 'DashboardController',
		      resolve: {
		    	contextMappingId: function($route) {
		    		return $route.current.params.contextMappingId;
		    	},
		    	cardInstanceId: function($route) {
		    		return $route.current.params.cardInstanceId;
		    	},
		    	selectedCard: function(CardInstanceService, $route) {
		    		return CardInstanceService.getOne($route.current.params.contextMappingId,$route.current.params.cardInstanceId);
		    	},
		    	installedCards: function(CardInstanceService, $route) {
		    		return CardInstanceService.get($route.current.params.contextMappingId);
		    	},
		    	availableCards: function(CardService, $route) {
		    		return CardService.get($route.current.params.contextMappingId);
		    	}
		      }
		    }).
		    otherwise({
		      redirectTo: '/'
		    });
		    
		    $locationProvider.html5Mode(true);
	}]);
})();

