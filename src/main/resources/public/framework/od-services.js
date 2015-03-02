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
	.service('OpenLRSService', function($http, _) {
		return {
			getStatements: function(contextMappingId,dashboardId,cardId) {
				var url = '/api/'+contextMappingId+'/db/'+dashboardId+'/openlrs/'+cardId+'/statements';
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
			getStatementsForUser: function(contextMappingId,dashboardId,cardId,user) {
				var url = '/api/'+contextMappingId+'/db/'+dashboardId+'/openlrs/'+cardId+'/statements/'+user;
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
	.service('LearningAnalyticsProcessorService', function($http, _) {
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
	
	angular.
	module('OpenDashboard')
	.service('RosterService', function($http, _, OpenDashboard_API) {
		return {
			getRoster: function(contextMappingId,dashboardId,cardId,options) {
				var url = '/api/'+contextMappingId+'/db/'+dashboardId+'/card/'+cardId+'/roster';
		    	var promise = $http({
		    		method  : 'POST',
		    		url		: url,
		    		data    : JSON.stringify(options),
		    		headers : { 'Content-Type': 'application/json'}
		    	})
		    	.then(function (response) {
		    		if (response && response.data) {
		    			var members = [];
		    			angular.forEach(response.data, function(value,key) {
		                    var member = OpenDashboard_API.createMemberInstance();
		                    member.fromService(value);
		                    members.push(member);
		                });
		    			
			    		return members;		    			
		    		}
		    		
		    		return null;
		    	}, function () {return null;});
				return promise;
			}
		}
	});
	
	angular.
	module('OpenDashboard')
	.service('DemographicsService', function($http, _, OpenDashboard_API) {
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
		    			var demographics = [];
		    			angular.forEach(response.data, function(value,key) {
		                    var demographic = OpenDashboard_API.createDemographicsInstance(value);
		                    demographics.push(demographic);
		                });
		    			
			    		return demographics;		    			
		    		}
		    		
		    		return null;
		    	}, function () {return null;});
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
	});
	
	angular.
	module('OpenDashboard')
	.service('OutcomesService', function($http, _) {
		return {
			getOutcomes: function(contextMappingId,dashboardId,cardId,options) {
				var url = '/api/'+contextMappingId+'/db/'+dashboardId+'/card/'+cardId+'/outcomes';
		    	var promise = $http({
		    		method  : 'POST',
		    		url		: url,
		    		data    : JSON.stringify(options),
		    		headers : { 'Content-Type': 'application/json'}
		    	})
		    	.then(function (response) {
		    		if (response && response.data) {
			    		return response.data;		    			
		    		}
		    		
		    		return null;
		    	}, function () {return null;});
				return promise;
			}
		}
	});

})(angular, JSON, Math);

