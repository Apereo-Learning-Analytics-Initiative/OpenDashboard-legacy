(function(angular){
	'use strict';
	
	angular
	.module('OpenDashboard')
	.controller('WelcomeController', function($log, $scope, $location,
												OpenDashboard_API, ContextMappingService) {	
		$scope.contextMapping = {};
		$scope.isStudent = OpenDashboard_API.isStudent();
		
		$scope.saveContextMapping = function() {
			var inbound_lti_launch_request = OpenDashboard_API.getInbound_LTI_Launch();
			
			// TODO handle non-context case
			$scope.contextMapping.key = inbound_lti_launch_request.oauth_consumer_key;
			$scope.contextMapping.context = inbound_lti_launch_request.context_id;
			
			ContextMappingService.create($scope.contextMapping)
			.then(function(savedContextMapping) {
				$scope.contextMapping = savedContextMapping;
				var url = '/cm/' + $scope.contextMapping.id + '/dashboard';
				
				var dashboards = $scope.contextMapping.dashboards;
				if (dashboards && dashboards.length > 0) {
					var dashboard = dashboards[0];
					$log.log('default dashboard: '+dashboard);
					url = url + '/' + dashboard.id;
				}
				
				$location.path(url);
			});
		};		

	});
})(angular);
