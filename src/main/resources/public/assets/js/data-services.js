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
(function(angular, JSON, Math) {
  'use strict';
  
  var genericHandleError = function (error) {
	if (error) {
	  error['isError'] = true;
	}
	return error;
  };
	
// Provider Service  
angular
  .module('OpenDashboard')
  .service('ProviderService', function($http, OpenDashboard_API) {

    return {
      getProviderTypes : function() {
        // TODO - make this dynamic
        var providers = [];
        var courseProviders = {
            type : 'COURSE',
            key : 'LABEL_COURSE_PROVIDERS_KEY',
            desc : 'LABEL_COURSE_PROVIDERS_DESC'
        };
        var eventProviders = {
            type : 'EVENT',
            key : 'LABEL_EVENT_PROVIDERS_KEY',
            desc : 'LABEL_EVENT_PROVIDERS_DESC'
        };
        var modelOutputProviders = {
            type : 'MODELOUTPUT',
            key : 'LABEL_MODELOUTPUT_PROVIDERS_KEY',
            desc : 'LABEL_MODELOUTPUT_PROVIDERS_DESC'
        };
        var rosterProviders = {
            type : 'ROSTER',
            key : 'LABEL_ROSTER_PROVIDERS_KEY',
            desc : 'LABEL_ROSTER_PROVIDERS_DESC'
        };
        var userProviders = {
           type : 'USER',
           key : 'LABEL_USER_PROVIDERS_KEY',
           desc : 'LABEL_USER_PROVIDERS_DESC'
        };
        var lineItemProviders = {
         type : 'LINEITEM',
         key : 'LABEL_LINEITEM_PROVIDERS_KEY',
         desc : 'LABEL_LINEITEM_PROVIDERS_DESC'
        };
			  
        providers.push(courseProviders);
        providers.push(eventProviders);
        providers.push(modelOutputProviders);
        providers.push(rosterProviders);
        providers.push(userProviders);
        providers.push(lineItemProviders);
			  
        return providers;
      },
      getProviders: function(type) {
				
        var url = '/api/providers/'+type;
        var promise = $http({
          method  : 'GET',
          url : url,
          headers : { 'Content-Type': 'application/json'}
        })
		.then(function (response) {
		  if (response && response.data) {
		    return response.data;		    			
		  }
		  return null;
		}, genericHandleError);
		return promise;
      },
      getProvider: function(type,key) {
				
        var url = '/api/providers/'+type+'/'+key;
        var promise = $http({
          method  : 'GET',
          url		: url,
          headers : { 'Content-Type': 'application/json'}
        })
        .then(function (response) {
          if (response && response.data) {
            return response.data;		    			
          }
          return null;
		}, genericHandleError);
		return promise;
      }
    }
});
// END Provider Service
  
// Pulse Service
angular
  .module('OpenDashboard')
    .service('PulseApiService', function($log, $http) {
      return {
        getPulseData: function(tenantId,userId) {
		  var url = '/api/tenants/'+tenantId+'/pulse/'+userId;
		  var promise = $http({
		    method  : 'GET',
			url		: url,
			headers : { 'Content-Type': 'application/json'}
		  })
		  .then(
		  function (response) {
		    if (response && response.data) {			    			
		      return response.data;		    			
			}
			$log.debug('Pulse data not found');
			return null;
	      }, 
	      function (error) {
	    	$log.debug(error);
	    	var errorObj = {};
	    	errorObj['isError'] = true;
	    	errorObj['errorCode'] = error.data.errors[0];
	    	
	    	return errorObj;
	      });
		  return promise;
		}
	}
});
// END Pulse Service
  
// User Service
angular
  .module('OpenDashboard')
    .service('UserDataService', function($log, $http) {
      return {
        getUser: function(tenantId,userId) {
		  var url = '/api/tenants/'+tenantId+'/users/'+userId;
		  var promise = $http({
		    method  : 'GET',
			url		: url,
			headers : { 'Content-Type': 'application/json'}
		  })
		  .then(
		  function (response) {
		    if (response && response.data) {			    			
		      return response.data;		    			
			}
			$log.debug('User not found');
			return null;
	      }, 
	      function (error) {
	    	$log.debug(error);
	    	var errorObj = {};
	    	errorObj['isError'] = true;
	    	errorObj['errorCode'] = error.data.errors[0];
	    	
	    	return errorObj;
	      });
		  return promise;
		}
	}
});
// END User Service

// Line Item Service
angular
  .module('OpenDashboard')
    .service('LineItemDataService', function($log, $http) {
      return {
        getLineItemsForClass: function(tenantId,classId) {
		  var url = '/api/tenants/'+tenantId+'/classes/'+classId+'/lineitems';
		  var promise = $http({
		    method  : 'GET',
			url		: url,
			headers : { 'Content-Type': 'application/json'}
		  })
		  .then(
		  function (response) {
		    if (response && response.data) {			    			
		      return response.data;		    			
			}
			$log.debug('No line items found');
			return null;
	      }, 
	      function (error) {
	    	$log.debug(error);
	    	var errorObj = {};
	    	errorObj['isError'] = true;
	    	errorObj['errorCode'] = error.data.errors[0];
	    	
	    	return errorObj;
	      });
		  return promise;
		}
	}
});
// END Line Item Service


// Course Service
angular
  .module('OpenDashboard')
    .service('EnrollmentDataService', function($log, $http, OpenDashboard_API) {
      return {
        getEnrollmentsForUser: function(tenantId,userId) {
		  var url = '/api/tenants/'+tenantId+'/users/'+userId+'/enrollments';
		  var promise = $http({
		    method  : 'GET',
			url		: url,
			headers : { 'Content-Type': 'application/json'}
		  })
		  .then(
		  function (response) {
		    if (response && response.data) {
			  var contexts = [];
			  angular.forEach(response.data, function(value,key) {
			    contexts.push(value);
			  });
			    			
		      return contexts;		    			
			}
			$log.debug('No enrollments found for user');
			return null;
	      }, 
	      function (error) {
	    	$log.debug(error);
	    	var errorObj = {};
	    	errorObj['isError'] = true;
	    	errorObj['errorCode'] = error.data.errors[0];
	    	
	    	return errorObj;
	      });
		  return promise;
		},
        getEnrollmentsForClass: function(tenantId,classId) {
		  var url = '/api/tenants/'+tenantId+'/classes/'+classId+'/enrollments';
		  var promise = $http({
		    method  : 'GET',
			url		: url,
			headers : { 'Content-Type': 'application/json'}
		  })
		  .then(
		  function (response) {
		    if (response && response.data) {
			  var contexts = [];
			  angular.forEach(response.data, function(value,key) {
			    contexts.push(value);
			  });
			    			
		      return contexts;		    			
			}
			$log.debug('No enrollments found for class');
			return null;
	      }, 
	      function (error) {
	    	var errorObj = {};
	    	errorObj['isError'] = true;
	    	errorObj['errorCode'] = error.data.errors[0];
	    	
	    	return errorObj;
	      });
		  return promise;
		}
	}
});
// END Course Service

// Event Service
angular
  .module('OpenDashboard')
    .service('EventService',function($http, _) {
      return {
    	
        postCaliperEvent : function (tenantId, caliperEvent) {
		      		  
          var url = '/api/tenants/'+tenantId+'/event';
          var promise = $http({
            method  : 'POST',
        	url		: url,
        	data    : JSON.stringify({
		                 caliperEvent: caliperEvent
		              }),
        	headers : { 'Content-Type': 'application/json'}
          }).then(function (response) {
              if (response && response.data) {
                return response.data[0];	    	
              }
        	  return null;
        	}, function (error) {
    	    	var errorObj = {};
    	    	errorObj['isError'] = true;
    	    	errorObj['errorCode'] = error.data.errors[0];
    	    	
    	    	return errorObj;
        	}
          );
    		return promise;
        },  
        
        getEventStatisticsForClass : function (tenantId, classId) {
          var url = '/api/tenants/'+tenantId+'/classes/'+classId+'/events/stats';
          var promise = $http({
            method  : 'GET',
            url		: url,
            headers : { 'Content-Type': 'application/json'}
          })
          .then(function (response) {
            return response.data;
          }, function (error) {
	    	$log.debug(error);
	    	var errorObj = {};
	    	errorObj['isError'] = true;
	    	errorObj['errorCode'] = error.data.errors[0];
	    	
	    	return errorObj;
          });
          return promise;
        },

        getEventForClassAndUser : function (tenantId, classId, userId, page, size) {
          var p = page || 0;
          var s = size || 1000;

          var url = '/api/tenants/'+tenantId+'/classes/'+classId+'/events/'+userId+'?page='+p+'&size='+s;
          var promise = $http({
            method  : 'GET',
            url		: url,
            headers : { 'Content-Type': 'application/json'}
          })
          .then(function (response) {
            return response.data;
          }, function (error) {
	    	$log.debug(error);
	    	var errorObj = {};
	    	errorObj['isError'] = true;
	    	errorObj['errorCode'] = error.data.errors[0];
	    	
	    	return errorObj;
          });
          return promise;
        },

        getEventsForCourse : function (tenantId, courseId, page, size) {
          var p = page || 0;
          var s = size || 10;
    	
          var url = '/api/tenants/'+tenantId+'/event/course/'+courseId+'?page='+p+'&size='+s;
          var promise = $http({
            method  : 'GET',
        	url		: url,
        	headers : { 'Content-Type': 'application/json'}
          })
          .then(function (response) {
              if (response && response.data) {
                return response.data.content;	    	
              }
        	  return null;
        	}, function (error) {
    	    	var errorObj = {};
    	    	errorObj['isError'] = true;
    	    	errorObj['errorCode'] = error.data.errors[0];
    	    	
    	    	return errorObj;
        	}
          );
    		return promise;
		},
		groupByAndMap: function(events,groupByFunction,mapFunction) {
		
			if (!groupByFunction) {
				groupByFunction = function (event) {
					//by default groupBy stored date
					var timestamp = event.timestamp;
		    		return timestamp.slice(0, timestamp.indexOf("T"));
				};
			}
			
			if (!mapFunction) {
				mapFunction = function(value,key){
					// by default map to the number of events for the value
    				var numberOfEvents = 0;
    				if (value && value != null) {
    					numberOfEvents = value.length;
    				}
    				
    				return {
    					x:key,
    					y: [numberOfEvents]
    				};
    			};
			}
			
			return _.chain(events)
					.groupBy(groupByFunction)
					.map(mapFunction)
					.value();
		}
	}
});
angular
.module('OpenDashboard')
.service('ModelOutputDataService', function($http, OpenDashboard_API) {

	return {
		getModelOutputForCourse: function(tenantId,courseId,page,size) {
			
			var p = page || 0;
            var s = size || 10;

		
			var url = '/api/tenants/'+tenantId+'/modeloutput/course/'+courseId+'?page='+p+'&size='+s;
	    	var promise = $http({
	    		method  : 'GET',
	    		url		: url,
	    		headers : { 'Content-Type': 'application/json'}
	    	})
	    	.then(function (response) {
	    		if (response && response.data) {
	    			return response.data.content;		    	
	    		}
	    		return null;
	    	}, genericHandleError);
			return promise;
		}
	}
});
angular
.module('OpenDashboard')
.service('RosterService', function($http, OpenDashboard_API) {
  return {
    getRoster: function(tenantId,contextMappingId) {
    		
      var url = '/api/tenants/'+tenantId+'/contexts/'+contextMappingId+'/roster';
      var promise = $http({
        		method  : 'GET',
        		url		: url,
        		headers : { 'Content-Type': 'application/json'}
      })
      .then(
        function (response) {
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
        },
        function (error) {
          var errorObj = {};
          errorObj['isError'] = true;
          errorObj['errorCode'] = error.data.errors[0];
        	
          return errorObj;
        }
      );
      return promise;
    }
  }
});
angular.
module('OpenDashboard')
.service('dataService', function () {
    return {
        checkUniqueValue: function (dashboards, field, value) {
            if(value == undefined)
                value = "";
            return ( _.result(_.find(dashboards,field, value), field) === undefined);
        }
    }
 });
})(angular, JSON, Math);
