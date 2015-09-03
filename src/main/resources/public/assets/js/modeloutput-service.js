(function(angular, JSON) {
  'use strict';
  angular
  .module('OpenDashboard')
  	.service('ModelOutputDataService', function($log, $http, OpenDashboard_API) {

		return {
			getModelOutputForCourse: function(options,courseId,page,size) {
				
				$log.debug(options);
				$log.debug(courseId);
				$log.debug(page);
				$log.debug(size);
				var p = page || 0;
                var s = size || 10;

			
				var url = '/api/modeloutput/course/'+courseId+'?page='+p+'&size='+s;
		    	var promise = $http({
		    		method  : 'POST',
		    		url		: url,
		    		data    : JSON.stringify(options),
		    		headers : { 'Content-Type': 'application/json'}
		    	})
		    	.then(function (response) {
		    		if (response && response.data) {
		    			console.log('****');
		    			console.log(response);
		    			return response.data.content;		    	
		    		}
		    		$log.debug('No model output');
		    		return null;
		    	}, function (error) {
		    		$log.error(error);
		    		return null;
		    	});
				return promise;
			},
			getModelOutputForUser: function(options,userId,page,size) {
				
				$log.debug(options);
				$log.debug(userId);
				$log.debug(page);
				$log.debug(size);
				var p = page || 0;
				var s = size || 10;
				
				
				var url = '/api/modeloutput/user/'+userId+'?page='+p+'&size='+s;
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
					$log.debug('No model output');
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