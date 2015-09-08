'use strict';

angular
.module('OpenDashboard')
.controller('IndexCtrl',

function IndexCtrl($scope, $state, $translate, $translatePartialLoader, $log, SessionService, ContextMappingService) {
  $log.debug('index ctrl');

  $scope.contextMapping = null;
  $scope.activeDashboard = null;

  var doRouting = function () {
      if (SessionService.isLTISession()) {
    	var inbound_lti_launch_request = SessionService.getInbound_LTI_Launch();
    	
    	ContextMappingService
    	.get(inbound_lti_launch_request.oauth_consumer_key, inbound_lti_launch_request.context_id)
    	.then(function (contextMapping) {
    		$scope.contextMapping = contextMapping;
    	    if (!$scope.contextMapping) {
		      $state.go('index.welcome'); 
			}
			else {
			  if ($scope.contextMapping.dashboards && $scope.contextMapping.dashboards.length > 0) {
			    $log.debug('Context Mapping exists with dashboards configured');
					
				// TODO - check for current dashboard
				$scope.activeDashboard = $scope.contextMapping.dashboards[0];
				$state.go('index.dashboard', {cmid:$scope.contextMapping.id,dbid:$scope.activeDashboard.id});
			  }
			  else {
			    $log.debug('Context Mapping exists but no dashboards');
			    $state.go('index.addDashboard', {cmid:$scope.contextMapping.id}); 
			  }
			}
    	})
	  }
	  else {
	    if (SessionService.hasAdminRole()) {
	      $state.go('index.admin');
	    }
	    else {
	      $state.go('index.courselist');
	    }
	  }
      return;
  };
  
  
  if (SessionService.isAuthenticated()) {
	$scope.isStudent = SessionService.hasStudentRole();
    doRouting();
  }
  else {
	  SessionService
	  .authenticate()
	  .then(
		function (data) {
		  if (!data) {
		    $state.go('login');
		  }
		  else {
		    $scope.isStudent = SessionService.hasStudentRole();
			doRouting();
		  }
		  return;
		},
		function (error) {
			$state.go('login');
			return;
		}
	  );
  }
  
  
  

});