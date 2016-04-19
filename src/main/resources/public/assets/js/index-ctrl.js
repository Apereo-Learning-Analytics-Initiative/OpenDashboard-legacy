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

function IndexCtrl($scope, $state, $stateParams, $log, $translate, Notification, SessionService, ContextMappingService, LocaleService) {
  $scope.contextMapping = null;
  $scope.activeDashboard = null;  
  $scope.localesDisplayNames = LocaleService.getLocalesDisplayNames();
  
// Initial routing logic

  function doRouting() {
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
  };
  
  function getContextMapping(isLti) {
      if (isLti) {
        $log.debug('IndexCtrl - lti session');
        var ltiLaunch = SessionService.getInbound_LTI_Launch();
        $log.debug(ltiLaunch);
        ContextMappingService
        .getWithKeyAndContext(ltiLaunch.oauth_consumer_key, ltiLaunch.context_id)
        .then(
          function(contextMapping) {
        	$log.debug('IndexCtrl - contextMapping (lti): ');
        	$log.debug(contextMapping);
            $scope.contextMapping = contextMapping;
            doRouting();
          },
          function(error) {
        	$log.error(error);
        	// TODO
          }
        );
      }
      else {
    	$log.debug('IndexCtrl - non lti session');
    	var tenantId = $stateParams.tenantId;
    	var courseId = $stateParams.courseId;
    	$log.debug('IndexCtrl - tenantId: '+tenantId);
    	$log.debug('IndexCtrl - courseId: '+courseId);
    	
    	ContextMappingService
    	.getWithTenantAndCourse(tenantId, courseId)
        .then(
          function(contextMapping) {
        	$log.debug('IndexCtrl - contextMapping (non-lti): ');
        	$log.debug(contextMapping);
            $scope.contextMapping = contextMapping;
            doRouting();
          },
          function(error) {
        	$log.error(error);
        	// TODO
          }
        );
      }
  };

  if (SessionService.isAuthenticated()) {
	$log.debug('IndexCtrl - user is authenticated'); 
    $scope.isAuthenticated = SessionService.isAuthenticated();
    $scope.isStudent = SessionService.hasStudentRole();
    $scope.isLtiSession = SessionService.isLTISession();
    $scope.isAdmin = SessionService.hasAdminRole();
	if (!$scope.contextMapping) {
	  getContextMapping(SessionService.isLTISession());
	}
	else {
	  doRouting();
	}
  }
  else {
	$log.debug('IndexCtrl - user is not authenticated');
	SessionService
	.authenticate()
	.then(
		function (data) {
    	  $log.debug('IndexCtrl - authenticate success:');
    	  $log.debug(data);
    	  if (!data) {
    		$log.debug('IndexCtrl - authenticate false, redirecting to login');
    	    $state.go('login');
    	  }
    	  else {
    		$log.debug('IndexCtrl - authenticate false, redirecting to login');
    		// TODO - not sure why we're doing this here instead of SessionService'
    		$scope.isStudent = SessionService.hasStudentRole();
    		
    		if (!$scope.contextMapping) {
    		  getContextMapping(SessionService.isLTISession());
    		}
    		else {
    		  doRouting();
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