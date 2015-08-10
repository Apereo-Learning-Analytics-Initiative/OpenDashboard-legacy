(function(angular, window){
    'use strict';
    
    angular
    .module('OpenDashboard', ['ngRoute', 'OpenDashboardFramework', 
                              'angularCharts', 'ngVis', 'pascalprecht.translate', 'ngCookies',
                              'od.cards.lti', 'od.cards.openlrs','od.cards.roster', 'od.cards.demo', 'od.cards.snapp'])
    .run(function($http, $log) {
        //TODO
        $log.log(sessionStorage.token);
        $http.defaults.headers.common['X-OD-AUTH'] = sessionStorage.token;
    })
    .config(function($translateProvider, $translatePartialLoaderProvider) {
        $translateProvider.useLoader('$translatePartialLoader', {
            urlTemplate: '/framework/translations/{lang}/{part}.json'
          });

        $translateProvider.preferredLanguage('en_us');
    })
	.provider('requestNotification', function() {
		// This is where we keep subscribed listeners
		var onRequestStartedListeners = [];
		var onRequestEndedListeners = [];
	
		// This is a utility to easily increment the request count
		var count = 0;
		var requestCounter = {
			increment : function() {
				count++;
			},
			decrement : function() {
				if (count > 0)
					count--;
			},
			getCount : function() {
				return count;
			}
		};
		// Subscribe to be notified when request starts
		this.subscribeOnRequestStarted = function(listener) {
			onRequestStartedListeners.push(listener);
		};
	
		// Tell the provider, that the request has started.
		this.fireRequestStarted = function(request) {
			// Increment the request count
			requestCounter.increment();
			// run each subscribed listener
			angular.forEach(onRequestStartedListeners, function(listener) {
				// call the listener with request argument
				listener(request);
			});
			return request;
		};
	
		// this is a complete analogy to the Request START
		this.subscribeOnRequestEnded = function(listener) {
			onRequestEndedListeners.push(listener);
		};
	
		this.fireRequestEnded = function() {
			requestCounter.decrement();
			var passedArgs = arguments;
			angular.forEach(onRequestEndedListeners, function(listener) {
				listener.apply(this, passedArgs);
			});
			return arguments[0];
		};
	
		this.getRequestCount = requestCounter.getCount;
	
		// This will be returned as a service
		this.$get = function() {
			var that = this;
			// just pass all the
			return {
				subscribeOnRequestStarted : that.subscribeOnRequestStarted,
				subscribeOnRequestEnded : that.subscribeOnRequestEnded,
				fireRequestEnded : that.fireRequestEnded,
				fireRequestStarted : that.fireRequestStarted,
				getRequestCount : that.getRequestCount
			};
		};
	})
	.config(function($httpProvider, requestNotificationProvider) {
		$httpProvider.defaults.transformRequest.push(function(data) {
			requestNotificationProvider.fireRequestStarted(data);
			return data;
		});
	
		$httpProvider.defaults.transformResponse.push(function(data) {
			requestNotificationProvider.fireRequestEnded(data);
			return data;
		});
	})
.factory('authHttpResponseInterceptor',['$q','$location',function($q,$location){
    return {
        response: function(response){
            return response || $q.when(response);
        },
        responseError: function(rejection) {
        	
          var errorCode = 'GENERAL_ERROR';
          if (rejection.status === 401 ||
            		rejection.status === 403) {
        	  errorCode = 'AUTHORIZATION_ERROR';
          }
          else if (rejection.data && rejection.data.message) {
        	errorCode = rejection.data.message;
          }
          
          $location.path('/cm/error/' + errorCode);
          return $q.reject(rejection);
        }
    }
}])
.config(['$httpProvider',function($httpProvider) {
    //Http Intercpetor to check auth failures for xhr requests
    $httpProvider.interceptors.push('authHttpResponseInterceptor');
}])
.config(['$routeProvider', '$locationProvider', function($routeProvider, $locationProvider) {
        $routeProvider
            .when('/', {
              controller: 'OpenDashboardController',
              template: '<div></div>',
              resolve: {
                contextMapping: function(ContextMappingService, OpenDashboard_API) {
                    var inbound_lti_launch_request = OpenDashboard_API.getInbound_LTI_Launch();
                    return ContextMappingService.get(inbound_lti_launch_request.oauth_consumer_key, inbound_lti_launch_request.context_id);
                }
              }
            })
            .when('/cm/welcome', {
              templateUrl: '/framework/welcome/view.html',
              controller: 'WelcomeController'
            })
            .when('/cm/error/:errorCode', {
              templateUrl: '/framework/dashboard/error.html',
              controller: 'ErrorController',
              resolve: {
                errorCode: function($route) {
                    return $route.current.params.errorCode;
                }
              }
            })
            .when('/cm/:cmid/dashboard', {
              templateUrl: '/framework/dashboard/add.html',
              controller: 'AddDashboardController',
              resolve: {
                contextMapping: function($route, DashboardService) {
                    return DashboardService.getContextMappingById($route.current.params.cmid);
                }
              }
            })
            .when('/cm/:cmid/dashboard/:dbid', {
              templateUrl: '/framework/dashboard/view.html',
              controller: 'DashboardController',
              resolve: {
                contextMapping: function($route, DashboardService) {
                    return DashboardService.getContextMappingById($route.current.params.cmid);
                },
                dashboard: function($route, DashboardService) {
                    return DashboardService.getActiveDashboard($route.current.params.cmid, $route.current.params.dbid);
                }
              }
            })
            .when('/cm/:cmid/dashboard/:dbid/add/:cardType', {
              templateUrl: '/framework/card/add.html',
              controller: 'AddCardController',
              resolve: {
                contextMapping: function($route, DashboardService) {
                    return DashboardService.getContextMappingById($route.current.params.cmid);
                },
                dashboard: function($route, DashboardService) {
                    return DashboardService.getActiveDashboard($route.current.params.cmid, $route.current.params.dbid);
                },
                cardType: function($route) {
                    return $route.current.params.cardType;
                }
              }
            })
            .when('/cm/:cmid/dashboard/:dbid/edit/:cardid', {
              templateUrl: '/framework/card/edit.html',
              controller: 'EditCardController',
              resolve: {
                contextMapping: function($route, DashboardService) {
                    return DashboardService.getContextMappingById($route.current.params.cmid);
                },
                dashboard: function($route, DashboardService) {
                    return DashboardService.getActiveDashboard($route.current.params.cmid, $route.current.params.dbid);
                },
                card: function($route, DashboardService) {
                    return DashboardService.getActiveCard($route.current.params.cmid, $route.current.params.dbid, $route.current.params.cardid);
                }
              }
            })
            .when('/cm/:cmid/dashboard/:dbid/remove/:cardid', {
              templateUrl: '/framework/card/remove.html',
              controller: 'RemoveCardController',
              resolve: {
                contextMapping: function($route, DashboardService) {
                    return DashboardService.getContextMappingById($route.current.params.cmid);
                },
                dashboard: function($route, DashboardService) {
                    return DashboardService.getActiveDashboard($route.current.params.cmid, $route.current.params.dbid);
                },
                card: function($route, DashboardService) {
                    return DashboardService.getActiveCard($route.current.params.cmid, $route.current.params.dbid, $route.current.params.cardid);
                }
              }
            })
            .otherwise({
              redirectTo: '/'
            });
            
            $locationProvider.html5Mode(true);
    }]);
})(angular, window);