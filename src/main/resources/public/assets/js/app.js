'use strict';

angular
.module('OpenDashboardRegistry',[])
.provider('registry', function() {
    var registry = {};
    var dashboards = {};
    this.register = function(key,card) {
        var c = angular.copy(card);
        registry[key] = c;
        return this;
    };
    this.registerDashboard = function(key,dashboard) {
        var d = angular.copy(dashboard);
        dashboards[key] = d;
        return this;
    };
    this.$get = function() {
        return {
            registry: registry,
            dashboards: dashboards
        };
    };
});

angular
.module('underscore', [])
.factory('_', function ($window) {
    return $window._;
});

angular
.module('OpenDashboardAPI', ['underscore'])
.factory('OpenDashboard_API', function ($log, $window, _) {
    return $window.OpenDashboardApi;
});

angular
.module('OpenDashboardFramework', ['OpenDashboardRegistry', 'underscore', 'OpenDashboardAPI']);


angular
.module('OpenDashboard', ['OpenDashboardFramework', 'ui.bootstrap', 'ui.router', 'ngCookies', 'ngVis', 'pascalprecht.translate', 'ui-notification',
                              'od.cards.lti', 'od.cards.eventviewer','od.cards.roster', 'od.cards.demo', 'od.cards.snapp','od.cards.modelviewer']);

angular
.module('OpenDashboard')
.config(function(NotificationProvider) {
	NotificationProvider.setOptions({
	    delay: 10000,
	    startTop: 20,
	    startRight: 10,
	    verticalSpacing: 20,
	    horizontalSpacing: 20,
	    positionX: 'left',
	    positionY: 'bottom'
	});
 })
.config(function($translateProvider, $translatePartialLoaderProvider) {
    $translateProvider.useLoader('$translatePartialLoader', {
        urlTemplate: '/assets/translations/{lang}/{part}.json'
      });

    $translateProvider.preferredLanguage('en_us');
})
.config(function ($httpProvider, requestNotificationProvider) {
    $httpProvider.interceptors.push(function ($q) {
        return {
            request: function (config) {
               requestNotificationProvider.fireRequestStarted();
               return config;
            },
            response: function (response) {
              requestNotificationProvider.fireRequestEnded();
              return response;
            },
            
            responseError: function (rejection) {
              requestNotificationProvider.fireRequestEnded();              
              return $q.reject(rejection);
            }
        }
    });
})
.provider('requestNotification', function () {
    // This is where we keep subscribed listeners
    var onRequestStartedListeners = [];
    var onRequestEndedListeners = [];

    // This is a utility to easily increment the request count
    var count = 0;
    var requestCounter = {
        increment: function () {
            count++;
        },
        decrement: function () {
            if (count > 0) count--;
        },
        getCount: function () {
            return count;
        }
    };
    // Subscribe to be notified when request starts
    this.subscribeOnRequestStarted = function (listener) {
        onRequestStartedListeners.push(listener);
    };

    // Tell the provider, that the request has started.
    this.fireRequestStarted = function (request) {
        // Increment the request count
        requestCounter.increment();
        //run each subscribed listener
        angular.forEach(onRequestStartedListeners, function (listener) {
            // call the listener with request argument
            listener(request);
        });
        return request;
    };

    // this is a complete analogy to the Request START
    this.subscribeOnRequestEnded = function (listener) {
        onRequestEndedListeners.push(listener);
    };

    this.fireRequestEnded = function () {
        requestCounter.decrement();
        var passedArgs = arguments;
        angular.forEach(onRequestEndedListeners, function (listener) {
            listener.apply(this, passedArgs);
        });
        return arguments[0];
    };

    this.getRequestCount = requestCounter.getCount;

    //This will be returned as a service
    this.$get = function () {
        var that = this;
        // just pass all the 
        return {
            subscribeOnRequestStarted: that.subscribeOnRequestStarted,
            subscribeOnRequestEnded: that.subscribeOnRequestEnded,
            fireRequestEnded: that.fireRequestEnded,
            fireRequestStarted: that.fireRequestStarted,
            getRequestCount: that.getRequestCount
        };
    };
})
.config(['$httpProvider',function($httpProvider) {
	if (!$httpProvider.defaults.headers.get) {
		$httpProvider.defaults.headers.get = {};
	}
	$httpProvider.defaults.headers.get['Cache-Control'] = 'no-cache';
	$httpProvider.defaults.headers.get['Pragma'] = 'no-cache';
	$httpProvider.defaults.headers.common["X-Requested-With"] = 'XMLHttpRequest';
}])
.config(function($stateProvider, $urlRouterProvider, $locationProvider) {

	// For unmatched routes
	$urlRouterProvider.otherwise('/');
	
	// Application routes
	$stateProvider
	    .state('login', {
	        url: '/login',
	        templateUrl: '/assets/templates/login.html',
	        resolve:{
	    	  isMultiTenant : function (FeatureFlagService) {
	    		return FeatureFlagService.isFeatureActive('multitenant');
	    	  }	
	     	},
	        controller: 'LoginCtrl'
	    })
	    .state('index', {
	        url: '/',
	        templateUrl: '/assets/templates/index.html',
		    resolve:{
		      contextMapping: function(ContextMappingService, OpenDashboard_API) {
		        var inbound_lti_launch_request = OpenDashboard_API.getInbound_LTI_Launch();
		        if (inbound_lti_launch_request) {
		          return ContextMappingService.get(inbound_lti_launch_request.oauth_consumer_key, inbound_lti_launch_request.context_id);
		        }
		        else {
		          return null;
		        }
		      }
	     	},
	        controller: 'IndexCtrl'
	    })
	    .state('index.admin', {
	        url: 'direct/admin',
	        templateUrl: '/assets/templates/admin.html',
		    resolve:{
	     	},
	        controller: function () {}
	    })
	    .state('index.welcome', {
	        url: 'welcome',
	        templateUrl: '/assets/templates/welcome.html',
		    resolve:{
	     	},
	        controller: 'WelcomeController'
	    })
	    .state('index.courselist', {
	        url: 'direct/courselist',
	        templateUrl: '/assets/templates/courselist.html',
		    resolve:{
	     	},
	        controller: function () {}
	    })
	    .state('index.addDashboard', {
	        url: 'cm/:cmid/addDashboard',
	        templateUrl: '/assets/templates/dashboard/add.html',
		    resolve:{
		      contextMapping: function(DashboardService, $stateParams) {
	            return DashboardService.getContextMappingById($stateParams.cmid);
	          }
	     	},
	        controller: 'AddDashboardController'
	    })
	    .state('index.dashboard', {
	        url: 'cm/:cmid/dashboard/:dbid',
	        templateUrl: '/assets/templates/dashboard/view.html',
		    resolve:{
		      contextMapping: function(DashboardService, $stateParams) {
                return DashboardService.getContextMappingById($stateParams.cmid);
              },
              dashboard: function($stateParams, DashboardService) {
                return DashboardService.getActiveDashboard($stateParams.cmid, $stateParams.dbid);
              }
	     	},
	        controller: 'DashboardController'
	    })
	    .state('index.selectCard', {
	        url: 'cm/:cmid/dashboard/:dbid/selectCard',
	        templateUrl: '/assets/templates/card/select.html',
		    resolve:{
		      contextMapping: function(DashboardService, $stateParams) {
                return DashboardService.getContextMappingById($stateParams.cmid);
              },
              dashboard: function($stateParams, DashboardService) {
                return DashboardService.getActiveDashboard($stateParams.cmid, $stateParams.dbid);
              }
	     	},
	        controller: 'SelectCardController'
	    })
	    .state('index.addCard', {
	        url: 'cm/:cmid/dashboard/:dbid/addCard/:cardType',
	        templateUrl: '/assets/templates/card/add.html',
		    resolve:{
		      contextMapping: function(DashboardService, $stateParams) {
                return DashboardService.getContextMappingById($stateParams.cmid);
              },
              dashboard: function($stateParams, DashboardService) {
                return DashboardService.getActiveDashboard($stateParams.cmid, $stateParams.dbid);
              },
	          card: function($stateParams, registry) {
            	return angular.copy(registry.registry[$stateParams.cardType]);
              }
              
	     	},
	        controller: 'AddCardController'
	    })
	    .state('index.editCard', {
	        url: 'cm/:cmid/dashboard/:dbid/editCard/:cid',
	        templateUrl: '/assets/templates/card/edit.html',
		    resolve:{
		      contextMapping: function(DashboardService, $stateParams) {
                return DashboardService.getContextMappingById($stateParams.cmid);
              },
              dashboard: function($stateParams, DashboardService) {
                return DashboardService.getActiveDashboard($stateParams.cmid, $stateParams.dbid);
              },
	          card: function($stateParams, DashboardService) {
            	return DashboardService.getActiveCard($stateParams.cmid, $stateParams.dbid, $stateParams.cid);
              }
              
	     	},
	        controller: 'EditCardController'
	    })
	    .state('index.removeCard', {
	        url: 'cm/:cmid/dashboard/:dbid/removeCard/:cid',
	        templateUrl: '/assets/templates/card/remove.html',
		    resolve:{
		      contextMapping: function(DashboardService, $stateParams) {
                return DashboardService.getContextMappingById($stateParams.cmid);
              },
              dashboard: function($stateParams, DashboardService) {
                return DashboardService.getActiveDashboard($stateParams.cmid, $stateParams.dbid);
              },
	          card: function($stateParams, DashboardService) {
            	return DashboardService.getActiveCard($stateParams.cmid, $stateParams.dbid, $stateParams.cid);
              }
              
	     	},
	        controller: 'RemoveCardController'
	    })
	    ;
	$locationProvider.html5Mode(true);
});