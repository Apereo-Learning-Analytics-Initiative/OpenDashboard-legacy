(function (angular, JSON) {
	'use strict';
	
	angular
	.module('OpenDashboard')
	.service('AssignmentService', function($log, $http, OpenDashboard_API) {

		return {
			getAssignments: function(options) {
				
					var url = '/api/assignments';
			    	var promise = $http({
			    		method  : 'POST',
			    		url		: url,
			    		data    : JSON.stringify(options),
			    		headers : { 'Content-Type': 'application/json'}
			    	})
			    	.then(function (response) {
			    		if (response && response.data) {
			    			var assignments = [];
			    			angular.forEach(response.data, function(value,key) {
			                    assignments.push(value);
			                });
			    			
				    		return assignments;		    			
			    		}
			    		$log.debug('No assignments found for course');
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