'use strict';

angular
.module('OpenDashboard')
.controller('IndexCtrl',

function IndexCtrl($scope, $state, $translate, $translatePartialLoader, $log, contextMapping, isStudent) {
  $scope.isStudent = isStudent;	
  $scope.contextMapping = contextMapping;
  $scope.activeDashboard = null;

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
  
});