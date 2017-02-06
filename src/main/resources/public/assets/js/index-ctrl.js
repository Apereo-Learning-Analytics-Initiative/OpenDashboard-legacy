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

function IndexCtrl($scope, $state, $stateParams, $translate, Notification, SessionService, LocaleService, $rootScope) {
  $scope.localesDisplayNames = LocaleService.getLocalesDisplayNames();
  
// Initial routing logic

  if (SessionService.isAuthenticated()) {
    $scope.isAuthenticated = SessionService.isAuthenticated();
    $scope.isStudent = SessionService.hasStudentRole();
    $scope.isLtiSession = SessionService.isLTISession();
    $rootScope.isLtiSession = SessionService.isLTISession();
    //  uncomment for testing LTI
    // $scope.isLtiSession = true;
    // $rootScope.isLtiSession = true;
    $scope.isAdmin = SessionService.hasAdminRole();
    
    if ($state.is('index')) {
      $state.go('index.courselist');
    }
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
    		// TODO - not sure why we're doing this here instead of SessionService'
    		$scope.isAuthenticated = SessionService.isAuthenticated();
    		$scope.isStudent = SessionService.hasStudentRole();
          $scope.isLtiSession = SessionService.isLTISession();
    	    $rootScope.isLtiSession = SessionService.isLTISession();
          //  uncomment for testing LTI
          // $scope.isLtiSession = true;
          // $rootScope.isLtiSession = true;
    	    $scope.isAdmin = SessionService.hasAdminRole();
    	    
    	    if ($state.is('index')) {
    	      $state.go('index.courselist');
    	    }
    	  }
    	  return;
    	},
		function (error) {
			$state.go('login');
			return;
		}
	  );
  }

  
// App-wide functions
  $scope.changeLanguage = function (locale) {
    LocaleService.setLocaleByDisplayName(locale);
    $state.reload();
  };
  
  $scope.getLocaleImgPath = function (locale) {	
	var path = '/assets/img/locales/'  
  
	if (locale) {
	  var code = LocaleService.getLocaleForDisplayName(locale);
	  path = path + code +'.png';
	}
	else {
	  var code = LocaleService.getLocaleForDisplayName(LocaleService.getLocaleDisplayName());
	  
	  if (!code) {
		code = $translate.use();  
		if (!code) {
		  code = 'en_US';
		}
	  }

	  path = path + code +'.png';
	}
	return path;
  };
  
    $scope.logout = function (){
    
    SessionService.logout()
      .then( function (data) {
            $state.go('login', {loggedOutMessage:'USER_INITIATED'});
            Notification.success({message: 'You have been signed out', positionY: 'top', positionX: 'right'});
            return;
          },
          function (error) {
            $state.go('login', {loggedOutMessage:'USER_INITIATED'});
            Notification.success({message: 'You have been signed out', positionY: 'top', positionX: 'right'});
            return;
          }
       );
  }

  
});