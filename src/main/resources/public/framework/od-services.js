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
	
	angular.
	module('OpenDashboard')
	.service('OpenLRSEventServiceStrategy', function($http, _) {
		return {
			getEvents: function(contextMappingId,dashboardId,cardId) {
				var url = '/api/'+contextMappingId+'/db/'+dashboardId+'/openlrs/'+cardId+'/statements';
		    	var promise = $http({
		    		method  : 'GET',
		    		url     : url,
		    		headers : { 'Content-Type': 'application/json'}
		    	})
		    	.then(function (response) {
		    		if (response && response.data && response.data.content) {
			    		return response.data.content;		    			
		    		}
		    		
		    		return null;
		    	}, function () {return null;});
				return promise;
			},
			getEventsForUser: function(contextMappingId,dashboardId,cardId,user) {
				var url = '/api/'+contextMappingId+'/db/'+dashboardId+'/openlrs/'+cardId+'/statements/'+user;
		    	var promise = $http({
		    		method  : 'GET',
		    		url     : url,
		    		headers : { 'Content-Type': 'application/json'}
		    	})
		    	.then(function (response) {
		    		if (response && response.data && response.data.content) {
			    		return response.data.content;		    			
		    		}
		    		
		    		return null;
		    	}, function () {return null;});
				return promise;
			},
			groupByAndMap: function(statements,groupByFunction,mapFunction) {
				
				if (!groupByFunction) {
					groupByFunction = function (statement) {
						//by default groupBy stored date
						var stored = statement.stored;
			    		return stored.slice(0, stored.indexOf("T"));
					};
				}
				
				if (!mapFunction) {
					mapFunction = function(value,key){
						// by default map to the number of statements for the value
	    				var numberOfStatements = 0;
	    				if (value && value != null) {
	    					numberOfStatements = value.length;
	    				}
	    				
	    				return {
	    					x:key,
	    					y: [numberOfStatements]
	    				};
	    			};
				}
				
				return _.chain(statements)
						.groupBy(groupByFunction)
						.map(mapFunction)
						.value();
			}
		}
	});
	
	angular.
	module('OpenDashboard')
	.service('OAAILearningAnalyticsProcessorServiceStrategy', function($http, _) {
		return {
			getResults: function(contextMappingId,dashboardId,cardId) {
				var url = '/api/'+contextMappingId+'/db/'+dashboardId+'/lap/'+cardId+'/risk';
		    	var promise = $http({
		    		method  : 'GET',
		    		url     : url,
		    		headers : { 'Content-Type': 'application/json'}
		    	})
		    	.then(function (response) {
		    		if (response && response.data) {
			    		return response.data;		    			
		    		}
		    		
		    		return null;
		    	}, function () {return null;});
				return promise;
			},
			getResultsForUser: function(contextMappingId,dashboardId,cardId,user) {
				var url = '/api/'+contextMappingId+'/db/'+dashboardId+'/lap/'+cardId+'/risk/'+user;
		    	var promise = $http({
		    		method  : 'GET',
		    		url     : url,
		    		headers : { 'Content-Type': 'application/json'}
		    	})
		    	.then(function (response) {
		    		if (response && response.data) {
			    		return response.data;		    			
		    		}
		    		
		    		return null;
		    	}, function () {return null;});
				return promise;
			},
			mapToRoster: function(roster,lapResults) {
				_.map(roster, function(member){
				    return _.assign(member, _.findWhere(lapResults, { alternativeId: member.getUserId() }));
				});
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
	})
	.service('DemographicsService', function($log, $http, _, OpenDashboard_API) {
		return {
			getDemographics: function() {
				var url = '/api/demographics';
		    	var promise = $http({
		    		method  : 'GET',
		    		url		: url,
		    		headers : { 'Content-Type': 'application/json'}
		    	})
		    	.then(function (response) {
		    		if (response && response.data) {
		    			$log.debug(response.data);
		    			var demographics = [];
		    			angular.forEach(response.data, function(value,key) {
		                    var demographic = OpenDashboard_API.createDemographicsInstance(value);
		                    demographics.push(demographic);
		                });
		    			
			    		return demographics;		    			
		    		}
		    		
		    		return null;
		    	}, function (error) {
		    		$log.error(error);
		    		return null;
		    	});
				return promise;
			},
			getDemographicsForUser: function(userId) {
				var url = '/api/demographics/'+userId;
		    	var promise = $http({
		    		method  : 'GET',
		    		url		: url,
		    		headers : { 'Content-Type': 'application/json'}
		    	})
		    	.then(function (response) {
		    		if (response && response.data) {
	                    var demographic = OpenDashboard_API.createDemographicsInstance(response.data);
			    		return demographic;		    			
		    		}
		    		
		    		return null;
		    	}, function () {return null;});
				return promise;
			}			
		}
	})
	.service('LearningAnalyticsProcessorService',function($log, $http, OpenDashboard_API, OAAILearningAnalyticsProcessorServiceStrategy) {
		var strategy = null;
		
		return {
			setStrategy : function (lapStrategyToUse) {
				strategy = lapStrategyToUse;
			},
			getResults: function(contextMappingId,dashboardId,cardId) {
				
				$log.debug('getResults');
				$log.debug('context mapping id: '+contextMappingId);
				$log.debug('dashboard id: '+dashboardId);
				$log.debug('card id: '+cardId);
				
				if (strategy) {
					return strategy.getResults(contextMappingId,dashboardId,cardId);
				}
				return OAAILearningAnalyticsProcessorServiceStrategy.getResults(contextMappingId,dashboardId,cardId);
			},
			getResultsForUser: function(contextMappingId,dashboardId,cardId,user) {
				if (strategy) {
					return strategy.getResultsForUser(contextMappingId,dashboardId,cardId,user);
				}
				return OAAILearningAnalyticsProcessorServiceStrategy.getResultsForUser(contextMappingId,dashboardId,cardId,user);
			},
			mapToRoster: function(roster,lapResults) {
				if (strategy) {
					return strategy.mapToRoster(roster,lapResults);
				}
				return OAAILearningAnalyticsProcessorServiceStrategy.mapToRoster(roster,lapResults);
			}
		}		
	})
	.service('LTIService',function($log, $http, OpenDashboard_API) {
		return {
		}
	})
	.service('EventService',function($log, $http, OpenDashboard_API, OpenLRSEventServiceStrategy) {
		var strategy = null;
		
		return {
			setStrategy : function(eventStrategyToUse) {
				strategy = eventStrategyToUse;
			},
			getEventFromService : function (eventData) {
				var event = OpenDashboard_API.createEventInstance();
				event.fromService(eventData);
				return event;
			},
			getEvents: function(contextMappingId,dashboardId,cardId) {
				if (strategy) {
					return strategy.getEvents(contextMappingId,dashboardId,cardId);
				}
				return OpenLRSEventServiceStrategy.getEvents(contextMappingId,dashboardId,cardId);
			},
			getEventsForUser: function(contextMappingId,dashboardId,cardId,user) {
				if (strategy) {
					return strategy.getEventsForUser(contextMappingId,dashboardId,cardId,user);
				}
				return OpenLRSEventServiceStrategy.getEventsForUser(contextMappingId,dashboardId,cardId,user);
			},
			groupByAndMap: function(eventData,groupByFunction,mapFunction) {
				
				if (strategy) {
					return strategy.groupByAndMap(eventData,groupByFunction,mapFunction);
				}
				return OpenLRSEventServiceStrategy.groupByAndMap(eventData,groupByFunction,mapFunction);
			}
		}
	});
	
})(angular, JSON, Math);

