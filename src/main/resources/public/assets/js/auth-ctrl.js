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
.controller('AuthCtrl',

function AuthCtrl($scope, $state, $location, SessionService) {
  $scope.isLogin = function () {
    return $state.is('login');
  }
  
  if (!String.prototype.startsWith) {
    String.prototype.startsWith = function(searchString, position) {
      position = position || 0;
      return this.indexOf(searchString, position) === position;
    };
  }

  if (!$location.path().startsWith("/err")
		  && !SessionService.isAuthenticated()) {
	  SessionService
	  .authenticate()
	  .then(
		function (data) {
		  if (!data) {
		    $state.go('login');
		    return;
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