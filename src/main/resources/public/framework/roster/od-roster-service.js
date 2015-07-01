(function(angular, JSON, Math) {
	'use strict';
	
	angular
	.module('OpenDashboard')
	.service('RosterService', function($log, $http, OpenDashboard_API) {
		return {
			getRoster: function(options) {
				
				$log.debug(options);
				var url = '/api/roster';
		    	var promise = $http({
		    		method  : 'POST',
		    		url		: url,
		    		data    : JSON.stringify(options),
		    		headers : { 'Content-Type': 'application/json'}
		    	})
		    	.then(function (response) {
		    		if (response && response.data) {
		    			var members = [];
		    			angular.forEach(response.data, function(value,key) {
		                    var member = OpenDashboard_API.createMemberInstance();
		                    member.fromService(value);
		                    members.push(member);
		                });
		    			
			    		return members;		    			
		    		}
		    		$log.debug('No members found for getRoster');
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
