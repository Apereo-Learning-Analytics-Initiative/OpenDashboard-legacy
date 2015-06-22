(function(angular, JSON) {
  'use strict';
  angular
  .module('OpenDashboard')
  	.service('ForumDataService', function($log, $http, OpenDashboard_API) {
		var strategy = null;

		return {
			setStrategy : function(strategyToUse) {
			  strategy = strategyToUse;
		    },
			getForums: function(options) {
				
				$log.debug(options);
			
				if (strategy) {
					return strategy.getForumData(options);
				}
				else {
					// use default implementation
					var url = '/api/'+options.contextMappingId+'/db/'+options.dashboardId+'/card/'+options.cardId+'/forums';
			    	var promise = $http({
			    		method  : 'POST',
			    		url		: url,
			    		data    : JSON.stringify(options),
			    		headers : { 'Content-Type': 'application/json'}
			    	})
			    	.then(function (response) {
			    		if (response && response.data) {
			    			var forums = [];
			    			angular.forEach(response.data, function(value,key) {
			    				// TODO 
			                    //var assignment = OpenDashboard_API.createAssignmentInstance();
			                    //assignment.fromService(value);
			    				forums.push(value);
			                });
			    			
				    		return forums;		    			
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