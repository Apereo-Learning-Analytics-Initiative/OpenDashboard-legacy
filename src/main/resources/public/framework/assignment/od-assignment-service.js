(function (angular, JSON) {
	'use strict';
	
	angular
	.module('OpenDashboard')
	.service('AssignmentService', function($log, $http, OpenDashboard_API) {
		var strategy = null;

		return {
			setStrategy : function(strategyToUse) {
			  strategy = strategyToUse;
		    },
			getAssignments: function(options) {
				
				$log.debug(options);
			
				if (strategy) {
					return strategy.getAssignments(options);
				}
				else {
					// use default implementation
					var url = '/api/'+options.contextMappingId+'/db/'+options.dashboardId+'/card/'+options.cardId+'/assignments';
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
			    				// TODO
			                    // var assignment =
								// OpenDashboard_API.createAssignmentInstance();
			                    // assignment.fromService(value);
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
		}
	});

})(angular, JSON);