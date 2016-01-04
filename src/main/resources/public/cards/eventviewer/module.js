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
(function(angular, JSON) {
'use strict';
    
angular
.module('od.cards.eventviewer', ['OpenDashboardRegistry', 'OpenDashboardAPI'])
 .config(function(registryProvider){
    registryProvider.register('eventviewer',{
        title: 'Event Viewer',
        description: 'Use this card to view learning events.',
        cardType: 'eventviewer',
        styleClasses: 'od-card col-xs-12',
        config: [],
        requires: ["EVENT"]
    });
 })
.controller('EventViewerCardController', function($scope, $translate, $translatePartialLoader, _, SessionService, EventService) {
    $translatePartialLoader.addPart('eventviewer-card');
    $translate.refresh();
    
	$scope.isError = false;
	$scope.isStudent = SessionService.hasStudentRole();
    $scope.events = null;
    $scope.activeEvent = null;
    $scope.courseName = SessionService.getCourse().title;
    $scope.queryString = null;	
	$scope.course = SessionService.getCourse();

	var user = null;
	if ($scope.isStudent) {
		user = SessionService.getCurrentUser().user_id;
	}
	
	var handleResponse = function (response) {
		if (response.isError) {
		  $scope.isError = true;
		  if (response.data && response.data.data) {
			$scope.errorMessage = response.data.data;
		  }
		  else {
			  $scope.errorMessage = "ERROR_GENERAL";
		  }
		}
		else {
		  $scope.events = response;
		}
	};
	
	var options = {};
	options.contextMappingId = $scope.contextMapping.id;
	options.dashboardId = $scope.activeDashboard.id;
	options.cardId = $scope.card.id;
	options.courseId = $scope.course.id;
	
	if (user) {
		EventService.getEventsForUser(options,user.id)
		.then(handleResponse);
	}
	else {
		EventService.getEventsForCourse(options,$scope.course.id)
		.then(handleResponse);
	}
		
    $scope.expandEvent = function (event) {
        $scope.activeEvent = event;
    };
});
})(angular, JSON);
