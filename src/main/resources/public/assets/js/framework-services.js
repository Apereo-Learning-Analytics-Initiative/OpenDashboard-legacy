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
	})
	.service('SessionService', function($log, $http, OpenDashboard_API, _) {
		
		var ROLE_ADMIN = 'ROLE_ADMIN';
		var ROLE_INSTRUCTOR = 'ROLE_INSTRUCTOR';
		var ROLE_STUDENT = 'ROLE_STUDENT';
		
		var authenticated = false;
		var ltiSession = false;
		var authorities = null;
		var loggedOut = false;
		
		var checkRole = function(role) {
		  var hasRole = false;
		  if (authorities) {
			$log.debug(authorities);
			var values = _.map(authorities, function(authority){ return authority['authority'];});
			$log.debug(values);
            var indexOf = _.indexOf(values,role);
            $log.debug(indexOf);
            if (indexOf >= 0) {
            	hasRole = true;
            }
		  }
		  return hasRole;
		}
		
		return {
		  isLoggedOut : function () {
		    return loggedOut;
		  },
		  isAuthenticated : function () {
		    return authenticated;
		  },
		  isLTISession : function () {
		    return ltiSession;
		  },
		  hasAdminRole : function () {
			  return checkRole(ROLE_ADMIN);
		  },
		  hasInstructorRole : function () {
			  return checkRole(ROLE_INSTRUCTOR);
		  },
		  hasStudentRole : function () {
			  return checkRole(ROLE_STUDENT);
		  },
		  authenticate : function(credentials) {
						  
		    var headers = {'Content-Type' : 'application/json'};
		        
			if (credentials) {
			  headers['Authorization'] = "Basic " + btoa(credentials.username + ":" + credentials.password);
			  if (credentials.tenant) {
			    headers['X-OD-TENANT'] = credentials.tenant;
			  }        	
			}
				
			var promise = $http({
			  method : 'GET',
			  url : '/user',
			  headers : headers})
			  .then(function(response) {
			    if (response && response.data
			      && response.data.authenticated 
			      && response.data.name) {
			    	$log.debug(response);
			    	loggedOut = false;
			    	authenticated = response.data.authenticated;
			    	authorities = response.data.authorities;
			    	
			    	if (response.data.launchRequest) {
			    		ltiSession = true;
				    	OpenDashboard_API.setInbound_LTI_Launch(response.data.launchRequest);
			    	}
			    	
					return authenticated;
			    }
			    return false;
			  }, 
			  function(error) {
			    $log.error(error);
			    return false;
			});
			return promise;
		  },
		  getCourse : function () {
		    return OpenDashboard_API.getCourse();
		  },
		  getInbound_LTI_Launch : function () {
			return OpenDashboard_API.getInbound_LTI_Launch();
		  },
		  getCurrentUser : function () {
			return OpenDashboard_API.getCurrentUser();
		  },
		  logout : function() {
		    authenticated = false;
		    ltiSession = false;
		    authorities = null;
		    loggedOut = true;
		  }
		}
	})
	.service('FeatureFlagService', function($log, $http) {
	    return {
	      isFeatureActive : function(featureKey) {
	        var headers = {'Content-Type' : 'application/json'};
	        
			var promise = $http({
				  method : 'GET',
				  url : '/features/' + featureKey,
				  headers : headers
				})
				.then(function(response) {
				  if (response && response.data) {
					var val = response.data[featureKey];				
					$log.log(val)
					return (val && val.toLowerCase() === 'true');
				  }
				  $log.debug(response);
				  return false;
				}, 
				function(error) {
				  $log.error(error);
				  return false;
				});
				return promise;
	      }
		}
	});
	
	angular
	.module('OpenDashboard')
	.service('DashboardService', function($q, ContextMappingService, UUIDService, _ ){
		
		return {
			getContextMappingById: function(contextMappingId) {
				var deferred = $q.defer();
				
				ContextMappingService.getById(contextMappingId)
				.then(
					function(contextMapping) {
						deferred.resolve(contextMapping);
					},
					function(error) {
						deferred.reject();
					}
				);
				return deferred.promise;
			}
		}
	});
	
	angular
	.module('OpenDashboard')
	.service('ContextMappingService', function($http, UUIDService, OpenDashboard_API, _) {
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
				if (contextMapping && contextMapping.dashboards) {
					card.id = UUIDService.generate();
					
					var db = _.find(contextMapping.dashboards,{'id':dashboard.id});

					if (!db.cards) {
						db.cards = [];
					}
					db.cards.push(card);
					
					return this.update(contextMapping);
				}
			},
			removeCard : function(contextMapping, dashboard, card) {
				if (contextMapping && contextMapping.dashboards) {
					
					var db = _.find(contextMapping.dashboards,{'id':dashboard.id});
					db.cards = _.reject(db.cards,{'id': card.id});
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
		
})(angular, JSON, Math);

