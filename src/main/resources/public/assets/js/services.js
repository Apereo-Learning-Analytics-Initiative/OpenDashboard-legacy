(function(angular, JSON, Math) {
	'use strict';
	
	angular
	.module('OpenDashboard')
	.service('UUIDService', function (){
		return {
			generate: function() {
				function _p8(s) {
					var p = (Math.random().toString(16)+"000000000").substr(2,8);
		        	return s ? "-" + p.substr(0,4) + "-" + p.substr(4,4) : p ;
		    	}
		    	return _p8() + _p8(true) + _p8(true) + _p8();
			}
		};
	});
	
	angular
	.module('OpenDashboard')
	.service('DashboardService', function($q, ContextMappingService, UUIDService, _ ){
		var activeContextMapping = null;
		var activeDashboard = null;
		var activeCard = null;
		
		return {
			getActiveContextMapping: function () {
			  return activeContextMapping;
		    },
			getContextMappingById: function(contextMappingId) {
				var deferred = $q.defer();
				
				if (activeContextMapping && activeContextMapping.id === contextMappingId) {
					deferred.resolve(activeContextMapping);
				}
				else {
					ContextMappingService.getById(contextMappingId)
					.then(
						function(contextMapping) {
							activeContextMapping = contextMapping;
							deferred.resolve(activeContextMapping);
						},
						function(error) {
							deferred.reject();
						}
					);
				}
				return deferred.promise;
			},
			getActiveDashboard: function(contextMappingId, dashboardId) {
				var deferred = $q.defer();
				if (activeDashboard && activeDashboard.id === dashboardId) {
					deferred.resolve(activeDashboard);
				}
				else {
					this.getContextMappingById(contextMappingId)
					.then(
						function(contextMapping){
							activeDashboard = _.find(contextMapping.dashboards,function(dashboard){return dashboard.id === dashboardId;});
							deferred.resolve(activeDashboard);
						},
						function(error){
							deferred.reject();
						}
					);
				}
				return deferred.promise;
			},
			getActiveCard: function(contextMappingId, dashboardId, cardId) {
				var deferred = $q.defer();
				if (activeCard && activeCard.id === cardId) {
					deferred.resolve(activeCard);
				}
				else {
					this.getActiveDashboard(contextMappingId, dashboardId)
					.then(
						function(activeDashboard){
							activeCard = _.find(activeDashboard.cards,function(card){return card.id === cardId;});
							deferred.resolve(activeCard);
						},
						function(error){
							deferred.reject();
						}
					);
				}
				return deferred.promise;
			},
			removeCard : function(card, dashboard, contextMapping) {
				dashboard.cards = _.reject(dashboard.cards,{'id': card.id});
				return ContextMappingService.update(contextMapping);
			}
		}
	});
	
	angular
	.module('OpenDashboard')
	.service('ContextMappingService', function($http, UUIDService, OpenDashboard_API) {
		return {
			createContextMappingInstance : function (options) {
				return OpenDashboard_API.createContextMappingInstance(options);
			},
			create : function (contextMapping) {
				var promise =
				$http({
			        method  : 'POST',
			        url     : '/api/consumer/'+contextMapping.key+'/context',
			        data    : JSON.stringify(contextMapping),
			        headers : { 'Content-Type': 'application/json' }
				})
				.then(function (response) {
					return response.data;
				});
				return promise;
			},
			update: function (contextMapping) {
				var promise =
				$http({
			        method  : 'PUT',
			        url     : '/api/consumer/'+contextMapping.key+'/context/'+contextMapping.context,
			        data    : JSON.stringify(contextMapping),
			        headers : { 'Content-Type': 'application/json' }
				})
				.then(function (response) {
					return response.data;
				});
				return promise;
			},
			addDashboard: function (contextMapping, dashboard) {
				if (contextMapping) {
					dashboard.id = UUIDService.generate();
					contextMapping.addDashboard(dashboard);
					return this.update(contextMapping);
				}
			},
			addCard: function(contextMapping, dashboard, card) {
				if (contextMapping && dashboard) {
					card.id = UUIDService.generate();
					if (!dashboard.cards) {
						dashboard.cards = [];
					}
					dashboard.cards.push(card);
					return this.update(contextMapping);
				}
			},
			get : function (key,context) {
				var promise =
				$http({
			        method  : 'GET',
			        url     : '/api/consumer/'+key+'/context/'+context,
			        headers : { 'Content-Type': 'application/json' }
				})
				.then(function (response) {
					if (response.data) {
						return OpenDashboard_API.createContextMappingInstance(response.data);
					}
					else {
						return null;
					}
				});
				return promise;
			},
			getById: function (id) {
				var promise =
				$http({
			        method  : 'GET',
			        url     : '/api/cm/'+id,
			        headers : { 'Content-Type': 'application/json' }
				})
				.then(function (response) {
					if (response.data) {
						return OpenDashboard_API.createContextMappingInstance(response.data);
					}
					else {
						return null;
					}
				});
				return promise;
			}
		}
	});
	
	angular
	.module('OpenDashboard')
	.service('ContextService',function($log, $http, OpenDashboard_API) {
		return {
			getCourse : function () {
				return OpenDashboard_API.getCourse();
			},
			getInbound_LTI_Launch : function () {
				return OpenDashboard_API.getInbound_LTI_Launch();
			},
			getCurrentUser : function () {
				return OpenDashboard_API.getCurrentUser();
			}
		}
	});
	
})(angular, JSON, Math);

