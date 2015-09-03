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
        config: [
          {field:'url',fieldName:'URL',fieldType:'url',required:true},
          {field:'key',fieldName:'Key',fieldType:'text',required:true},
          {field:'secret',fieldName:'Secret',fieldType:'text',required:true}
        ]
    });
 })
.controller('EventViewerCardController', function($scope, $http, _, ContextService, EventService) {
	$scope.isStudent = ContextService.getCurrentUser().isStudent();
    $scope.events = null;
    $scope.activeEvent = null;
    $scope.courseName = ContextService.getCourse().title;
    $scope.queryString = null;	
	$scope.course = ContextService.getCourse();

	var user = null;
	if ($scope.isStudent) {
		user = ContextService.getCurrentUser().user_id;
	}
	
	var handleResponse = function (events) {
    	$scope.events = events;
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
