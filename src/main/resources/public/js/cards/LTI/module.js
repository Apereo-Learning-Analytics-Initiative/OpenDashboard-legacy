(function() {
	'use strict';
	
	angular
		.module('LTICard', ['OpenDashboardAPI'])
		.controller('LtiCardController', function($scope, OpenDashboard_API, $timeout, LtiProxyService) {
			$scope.readyToLaunch = false;
			$scope.outboundLaunch = null;

			if (OpenDashboard_API) {
				$scope.inbound_lti_launch_request = OpenDashboard_API.getInbound_LTI_Launch();
			}
		
			LtiProxyService.post($scope.selectedCard,$scope.inbound_lti_launch_request)
				.then(function(proxiedLaunch){
					$scope.outboundLaunch = proxiedLaunch;
					$timeout(function() {
						var selector = '#' + $scope.selectedCard.id + ' > #lti_launch_form';
						$(selector).attr('action', $scope.outboundLaunch.launchUrl);
						$(selector).submit();
				    }, 2000);
				});
		})
		.service('LtiProxyService', function($http) {
			return {
				post : function (cardInstance,inboundLaunch) {
					var promise =
					$http({
				        method  : 'POST',
				        url     : '/api/lti/launch/'+cardInstance.id,
				        data    : JSON.stringify(inboundLaunch),
				        headers : { 'Content-Type': 'application/json' }
					})
					.then(function (response) {
						if (response.data) {
							return response.data;
						}
						else {
							return null;
						}
					});
					return promise;
				}
			}
		});
})();
