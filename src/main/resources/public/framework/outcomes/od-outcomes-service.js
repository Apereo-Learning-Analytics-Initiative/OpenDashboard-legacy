(function(angular, JSON, Math) {
	'use strict';
	
	angular
	.module('OpenDashboard')
	.service('OutcomesService', function($log, $http, OpenDashboard_API) {
		return {
			getOutcomesForCourse: function(options) {
			
				$log.debug(options);
				var url = '/api/outcomes';
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
		    		
		    		$log.debug('No outcomes found for getOutcomesForCourse');
		    		return null;
		    	}, function (error) {
		    		$log.error(error);
		    		return null;
		    	});
				return promise;
			}
		}
	});
	
})(angular, JSON, Math);
