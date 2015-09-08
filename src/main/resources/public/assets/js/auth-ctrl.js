'use strict';

angular
.module('OpenDashboard')
.controller('AuthCtrl',

function AuthCtrl($scope, $state, SessionService) {
  if (!SessionService.isAuthenticated()) {
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