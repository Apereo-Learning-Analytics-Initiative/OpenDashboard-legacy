(function(angular, JSON) {
  'use strict';
  angular
  .module('OpenDashboard')
  	.service('ForumDataService', function($log, $http, OpenDashboard_API) {
		return {
			getForums: function(options) {
				
				$log.debug(options);
			
				var url = '/api/forums';
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
		    		$log.debug('No forums found for course');
		    		return null;
		    	}, function (error) {
		    		$log.error(error);
		    		return null;
		    	});
				return promise;
			},
			getMessages: function(options, topicId) {
				
				$log.debug(options);
				$log.debug(topicId);
				
				var url = '/api/forums/'+topicId+'/messages';
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
					$log.debug('No messages found for course');
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