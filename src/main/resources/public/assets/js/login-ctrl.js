'use strict';

angular
.module('OpenDashboard')
.controller('LoginCtrl',

function LoginCtrl($log, $scope, $state, $translate, $translatePartialLoader, SessionService, isMultiTenant) {
  $translatePartialLoader.addPart('login');
  $translate.refresh();
    
  $scope.isMultiTenant = isMultiTenant;
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