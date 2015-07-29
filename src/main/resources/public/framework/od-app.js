(function(angular, window){
    'use strict';
    
    angular
    .module('OpenDashboard', ['ngRoute', 'OpenDashboardFramework', 
                              'angularCharts', 'ngVis', 'pascalprecht.translate',
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