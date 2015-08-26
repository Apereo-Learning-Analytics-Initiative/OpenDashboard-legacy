(function(angular, JSON) {
  'use strict';
  angular
  .module('OpenDashboard')
  	.service('CourseDataService', function($log, $http, OpenDashboard_API) {

		return {
			getContexts: function(options) {
				
				$log.debug(options);
			
				var url = '/api/context';
		    	var promise = $http({
		    		method  : 'POST',
		    		url		: url,
		    		data    : JSON.stringify(options),
		    		headers : { 'Content-Type': 'application/json'}
		    	})
		    	.then(function (response) {
		    		if (response && response.data) {
		    			var contexts = [];
		    			angular.forEach(response.data, function(value,key) {
		    				contexts.push(value);
		                });
		    			
			    		return contexts;		    			
		    		}
		    		$log.debug('No contexts found for user');
		    		return null;
		    	}, function (error) {
		    		$log.error(error);
		    		return null;
		    	});
				return promise;
			},
			getContext: function(options, contextId) {				
				$log.debug(options);
				
				var url = '/api/context/'+contextId;
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
					$log.debug('No contexts found for user');
					return null;
				}, function (error) {
					$log.error(error);
					return null;
				});
				return promise;
			 }		
		}
	});

})(angular, JSON);