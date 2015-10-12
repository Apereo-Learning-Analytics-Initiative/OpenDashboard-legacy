/*******************************************************************************
 * Copyright 2015 Unicon (R) Licensed under the
 * Educational Community License, Version 2.0 (the "License"); you may
 * not use this file except in compliance with the License. You may
 * obtain a copy of the License at
 *
 * http://www.osedu.org/licenses/ECL-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an "AS IS"
 * BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing
 * permissions and limitations under the License.
 *******************************************************************************/
'use strict';

angular
.module('OpenDashboard')
.controller('IndexCtrl',

function IndexCtrl($scope, $state, $log, SessionService, ContextMappingService) {
  $scope.contextMapping = null;
  $scope.activeDashboard = null;
  var currentState = $state.current;

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
      return;
  };
  
  
  if (SessionService.isAuthenticated()) {
    $scope.isAuthenticated = SessionService.isAuthenticated();
    $scope.isStudent = SessionService.hasStudentRole();
    $scope.isLtiSession = SessionService.isLTISession();
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
  
  $scope.logout = function (){
    
    SessionService.logout()
      .then( function (data) {
            $state.go('login', {loggedOutMessage:'USER_INITIATED'});
            return;
          },
          function (error) {
            $state.go('login', {loggedOutMessage:'USER_INITIATED'});
            return;
          }
       );
  }

});