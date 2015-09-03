(function(angular, JSON, Math) {
	'use strict';
	
	angular.
	module('OpenDashboard')
	.service('EventService',function($log, $http, _) {
		
		return {
			getEventsForCourse : function (options, courseId, page, size) {
				$log.debug(options);
				$log.debug(courseId);
				$log.debug(page);
				$log.debug(size);
				var p = page || 0;
	            var s = size || 10;
	
			
				var url = '/api/event/course/'+courseId+'?page='+p+'&size='+s;
		    	var promise = $http({
		    		method  : 'POST',
		    		url		: url,
		    		data    : JSON.stringify(options),
		    		headers : { 'Content-Type': 'application/json'}
		    	})
		    	.then(function (response) {
		    		$log.debug(response);
		    		if (response && response.data) {
		    		  return response.data.content;	    	
		    		}
		    		return null;
		    	}, function (error) {
		    		$log.error(error);
		    		return null;
		    	});
				return promise;
			},
			getEventsForUser : function (options, userId, page, size) {
				$log.debug(options);
				$log.debug(userId);
				$log.debug(page);
				$log.debug(size);
				var p = page || 0;
	            var s = size || 10;
	
			
				var url = '/api/event/user/'+userId+'?page='+p+'&size='+s;
		    	var promise = $http({
		    		method  : 'POST',
		    		url		: url,
		    		data    : JSON.stringify(options),
		    		headers : { 'Content-Type': 'application/json'}
		    	})
		    	.then(function (response) {
		    		if (response && response.data) {
		    		  return response.data.content;		    	
		    		}
		    		return null;
		    	}, function (error) {
		    		$log.error(error);
		    		return null;
		    	});
				return promise;
			},
			getEventsForCourseAndUser : function (options, courseId, userId, page, size) {
				$log.debug(options);
				$log.debug(courseId);
				$log.debug(userId);
				$log.debug(page);
				$log.debug(size);
				var p = page || 0;
	            var s = size || 10;
	
			
				var url = '/api/event/course/'+courseId+'/user/'+userId+'?page='+p+'&size='+s;
		    	var promise = $http({
		    		method  : 'POST',
		    		url		: url,
		    		data    : JSON.stringify(options),
		    		headers : { 'Content-Type': 'application/json'}
		    	})
		    	.then(function (response) {
		    		if (response && response.data) {
		    		  return response.data.content;	    	
		    		}
		    		return null;
		    	}, function (error) {
		    		$log.error(error);
		    		return null;
		    	});
				return promise;
			},
			groupByAndMap: function(events,groupByFunction,mapFunction) {
			
				if (!groupByFunction) {
					groupByFunction = function (event) {
						$log.debug(event);
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
	
})(angular, JSON, Math);

