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
.controller('LoginCtrl',

function LoginCtrl($log, $scope, $state, SessionService, isMultiTenant) {
    
  $scope.isMultiTenant = isMultiTenant;
  $scope.hasLoggedOut = $state.params.loggedOutMessage;
  $scope.credentials = {};
  $scope.login = function() {
	  SessionService.authenticate($scope.credentials)
      .then(
        function (data) {
          $log.debug(data);
          // TODO fix this validate message
          $scope.validationError = !data;
          if(data) {
  		    if (SessionService.hasAdminRole()) {
  		      $state.go('index.admin');
  		    }
  		    else {
  		      $state.go('index.courselist');
  		    }
        	return;
          }
          return;
        },
        function (error) {
          $log.error(error);
          $scope.error = true;
        }
      );
  };
  
});
