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
.factory('OpenDashboard_API', function ($window, _) {
    return $window.OpenDashboardApi;
});


angular
.module('OpenDashboardFramework', ['OpenDashboardRegistry', 'underscore', 'OpenDashboardAPI']);


angular
.module('OpenDashboard', ['OpenDashboardFramework', 'ui.bootstrap', 'ui.router', 'ngSanitize', 'ngCookies', 'ngVis', 'pascalprecht.translate', 'ui-notification', 'chart.js',
                              'angularMoment', 'od.cards.riskassessment', 'od.cards.activity','od.cards.caliperform']);

angular
.module('OpenDashboard')
  .constant('LOCALES', {
    'locales': {
      'cy_GB': 'Welsh',
      'en_US': 'English (US)',
      'en_GB': 'English (UK)'
    },
    'preferredLocale': 'en_US'
  })
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
.config(function($translateProvider, $translatePartialLoaderProvider, LOCALES) {
    $translateProvider.useLoader('$translatePartialLoader', {
        urlTemplate: '/assets/translations/{lang}/{part}.json'
      });

    $translateProvider.preferredLanguage(LOCALES.preferredLocale);
    $translateProvider.useLocalStorage();
    $translateProvider.useSanitizeValueStrategy('sanitize');
    $translatePartialLoaderProvider.addPart('framework');
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
          params: { loggedOutMessage : null },
          resolve:{
            tenants : function (TenantService) {
              return TenantService.getTenantsMetadata();
            }
          },
          controller: 'LoginCtrl'
        })
        .state('error',{
          url: '/err/:errorCode',
          templateUrl: '/assets/templates/error.html',
          controller: 'ErrorCtrl'
        })
        .state('index', {
            url: '/',
            params: {
              tenantId : null,
              courseId : null
            },
            templateUrl: '/assets/templates/index.html',
            resolve:{
            },
            controller: 'IndexCtrl'
        })
        .state('index.admin', {
          abstract: true,
          url: 'direct/admin',
          templateUrl: '/assets/templates/admin/index.html',
          resolve:{
            tenants : function(TenantService) {
              return TenantService.getTenants();
            }
          }
        })
        .state('index.admin.tenants', {
          url: '/tenants',
          templateUrl: '/assets/templates/admin/tenant/index.html',
          controller: 'TenantCtrl'
        })      
        .state('index.admin.addTenant', {
            url: '/addTenant',
            templateUrl: '/assets/templates/admin/tenant/add.html',
            resolve:{
            },
            controller: 'TenantCtrl'
        })
        .state('index.admin.editTenant', {
            url: '/editTenant/:tenantId',
            templateUrl: '/assets/templates/admin/tenant/edit.html',
            resolve:{
              tenant: function($stateParams, TenantService) {
                return TenantService.getTenant($stateParams.tenantId);
              }
            },
            controller: 'EditTenantCtrl'
        })
        .state('index.admin.tenants.tenant', {
          url: '/:id',
          templateUrl: '/assets/templates/admin/tenant/tenant.html',
            resolve:{
              tenant: function($stateParams, TenantService) {
                return TenantService.getTenant($stateParams.id);
              },
              providerTypes : function(ProviderService) {
                return ProviderService.getProviderTypes();
              }
            },
            controller: 'SelectTenantCtrl'
        })
        .state('index.admin.tenants.tenant.provider', {
            url: '/:providerType',
            templateUrl: '/assets/templates/admin/tenant/provider/index.html',
            resolve:{
              providerType : function($stateParams) {
                return $stateParams.providerType;
              },
              providers : function(ProviderService, $stateParams) {
                return ProviderService.getProviders($stateParams.providerType);
              }
            },
            controller: 'ProviderListCtrl'
        })
        .state('index.admin.tenants.tenant.provider.configure', {
            url: '/:providerKey/configure',
            templateUrl: '/assets/templates/admin/tenant/provider/configure.html',
            resolve:{
              providerType : function($stateParams) {
                return $stateParams.providerType;
              },
              provider : function(ProviderService, $stateParams) {
                return ProviderService.getProvider($stateParams.providerType,$stateParams.providerKey);
              }
            },
            controller: 'ConfigureProviderCtrl'
        })
        .state('index.admin.tenants.tenant.provider.edit', {
            url: '/:providerKey/edit',
            templateUrl: '/assets/templates/admin/tenant/provider/edit.html',
            resolve:{
              providerType : function($stateParams) {
                return $stateParams.providerType;
              },
              provider : function(ProviderService, $stateParams) {
                return ProviderService.getProvider($stateParams.providerType,$stateParams.providerKey);
              },
              providerData : function(TenantService,$stateParams) {
                return TenantService.getProviderDataByTypeAndKey($stateParams.id,$stateParams.providerType,$stateParams.providerKey);  
              }
            },
            controller: 'EditConfigureProviderCtrl'
        })
        .state('index.admin.tenants.tenant.provider.delete', {
            url: '/:providerType/:providerKey/delete',
            templateUrl: '/assets/templates/admin/tenant/provider/delete.html',
            resolve:{
              providerType : function($stateParams) {
                return $stateParams.providerType;
              },
              provider : function(ProviderService, $stateParams) {
                return ProviderService.getProvider($stateParams.providerType,$stateParams.providerKey);
              },
              providerData : function(TenantService,$stateParams) {
                return TenantService.getProviderDataByTypeAndKey($stateParams.id,$stateParams.providerType,$stateParams.providerKey);  
              }
            },
            controller: 'DeleteConfigureProviderCtrl'
        })
        .state('index.admin.tenants.tenant.addDashboard', {
            url: '/dashboards/add',
            templateUrl: '/assets/templates/admin/tenant/dashboards/add.html',
            resolve:{
            },
            controller: 'AddPreconfiguredDashboardCtrl'
        })
        .state('index.admin.tenants.tenant.editDashboard', {
            url: '/dashboards/edit/:dashboardId',
            templateUrl: '/assets/templates/admin/tenant/dashboards/edit.html',
            resolve:{
              preconfiguredDashboard : function($stateParams, TenantService) {
                return TenantService.getPreconfiguredDashboardById($stateParams.id, $stateParams.dashboardId);
              }
            },
            controller: 'EditPreconfiguredDashboardCtrl'
        })
        .state('index.admin.tenants.tenant.removeDashboard', {
            url: '/dashboards/remove/:dashboardId',
            templateUrl: '/assets/templates/admin/tenant/dashboards/remove.html',
            resolve:{
              preconfiguredDashboard : function($stateParams, TenantService) {
                return TenantService.getPreconfiguredDashboardById($stateParams.id, $stateParams.dashboardId);
              }
            },
            controller: 'RemovePreconfiguredDashboardCtrl'
        })
        .state('index.courselist', {
            url: 'direct/courselist/:groupId',
            templateUrl: '/assets/templates/courselist.html',
            params: {
                groupId: { value: null, squash: true }
            },
            controller: 'CourseListController'
        })
        .state('index.courselist.studentView', {
            url: '/student/:studentId',
            templateUrl: '/assets/templates/studentView.html',
            params: {
                studentId: { value: null, squash: true }
            },
            controller: 'StudentViewController'
        })
        
        // **
        // ** Marist Universal Student Experience (MUSE)
        // **
        /*
        .state('index.MUSE', {
            url: 'direct/courselist/:groupId',
            templateUrl: '/assets/templates/courselist.html',
            params: {
                groupId: { value: null, squash: true }
            },
            controller: 'CourseListController'
        })
        .state('index.MUSE.instructor', {
            url: 'direct/course/:groupId/student/:studentId',
            templateUrl: '/assets/templates/courselist.html',
            params: {
            	courseId: { value: null, squash: true }
            	studentId: { value: null, squash: true }
            },
            controller: 'CourseListController'
        })
        .state('index.MUSE.instructor.student', {
            url: '/student/:studentId',
            templateUrl: '/assets/templates/courselist.html',
            params: {
                groupId: { value: null, squash: true }
            },
            controller: 'CourseListController'
        })
        */
        
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
        .state('index.removeDashboard', {
            url: 'cm/:cmid/removeDashboard/:dbid',
            templateUrl: '/assets/templates/dashboard/remove.html',
            resolve:{
              contextMapping: function(DashboardService, $stateParams) {
                return DashboardService.getContextMappingById($stateParams.cmid);
              },
              dashboardId: function($stateParams) {
                return $stateParams.dbid;
              }
            },
            controller: 'RemoveDashboardController'
        })
        .state('index.dashboard', {
            url: 'cm/:cmid/dashboard/:dbid?data',
            templateUrl: '/assets/templates/dashboard/view.html',
            resolve:{
              contextMapping: function(DashboardService, $stateParams) {
                return DashboardService.getContextMappingById($stateParams.cmid);
              },
              dashboardId: function($stateParams) {
                return $stateParams.dbid;
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
              dashboardId: function($stateParams) {
                return $stateParams.dbid;
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
              dashboardId: function($stateParams) {
                return $stateParams.dbid;
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
              dashboardId: function($stateParams) {
                return $stateParams.dbid;
              },
              cardId: function($stateParams) {
                return $stateParams.cid;
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
              dashboardId: function($stateParams) {
                return $stateParams.dbid;
              },
              cardId: function($stateParams) {
                return $stateParams.cid;
              }
              
            },
            controller: 'RemoveCardController'
        })
        ;
    $locationProvider.html5Mode(true);
});