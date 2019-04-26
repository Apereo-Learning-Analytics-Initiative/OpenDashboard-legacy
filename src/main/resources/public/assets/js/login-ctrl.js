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

function LoginCtrl($log, $scope, $state, $translate, Notification, SessionService, LocaleService, tenants) {
  $scope.showLoginForm = false;
  $scope.tenants = tenants;
  
  $scope.localesDisplayNames = LocaleService.getLocalesDisplayNames();

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
  
  $scope.toggleLoginForm = function () {
	$scope.showLoginForm = !$scope.showLoginForm;  
  };
  
  $scope.credentials = {};
  $scope.login = function() {
	$log.debug($scope.credentials);
	  SessionService.authenticate($scope.credentials)
      .then(
        function (data) {
          $log.debug(data);
          
          if(data) {
  		    if (SessionService.hasAdminRole()) {
  		      $state.go('index.admin.tenants');
  		    }
  		    else {
  		      $state.go('index.courselist');
  		    }
        	return;
          }
          else {
        	Notification.error({message: $translate.instant('ERROR_LOGIN'), positionY: 'top', positionX: 'right'});
          }
          
          return;
        },
        function (error) {
          $log.error(error);
          $scope.error = true;
          Notification.error($translate.instant('ERROR_LOGIN'));
        }
      );
  };
  
});
