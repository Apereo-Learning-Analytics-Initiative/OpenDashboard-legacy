(function(angular) {
'use strict';
    
angular
.module('od.cards.demo', ['OpenDashboardRegistry', 'OpenDashboardAPI'])
 .config(function(registryProvider){
    registryProvider.register('demo',{
        title: 'Demo Card',
        description: 'This card demonstrates how to retrieve data from various sources.',
        imgUrl: '',
        cardType: 'demo',
        styleClasses: 'od-card col-xs-12',
	    config: [
	    ]
    });
 })
 .controller('DemoCardController', function($scope, $log, $translate, $translatePartialLoader,
	 ContextService, RosterService, OutcomesService, DemographicsService, AssignmentService, ForumDataService) {
    $translatePartialLoader.addPart('demo-card');
    $translate.refresh();
	$scope.course = ContextService.getCourse();
	$scope.lti = ContextService.getInbound_LTI_Launch();

	if ($scope.lti.ext.ext_ims_lis_memberships_url && $scope.lti.ext.ext_ims_lis_memberships_id) {
		
		var basicLISData = {};
		basicLISData.ext_ims_lis_memberships_url = $scope.lti.ext.ext_ims_lis_memberships_url;
		basicLISData.ext_ims_lis_memberships_id = $scope.lti.ext.ext_ims_lis_memberships_id;
		
		var options = {};
		options.contextMappingId = $scope.contextMapping.id;
		options.dashboardId = $scope.activeDashboard.id;
		options.cardId = $scope.card.id;
		options.basicLISData = basicLISData;

		RosterService
		.getRoster(options,null) // pass null so the default implementation is used
		.then(
			function (rosterData) {
				if (rosterData) {
					$scope.course.buildRoster(rosterData);					
				}
			}
		);
		
		OutcomesService
		.getOutcomes(options,null)
		.then(
			function(outcomesData) {
				$scope.outcomes = outcomesData;
			}
		);
		
		DemographicsService
		.getDemographics()
		.then(
			function (demographicsData) {
				$scope.demographics = demographicsData;
			}
			
		);		
	}
	else {
		$log.error('Card not configured for Roster');
		$scope.message = 'No supporting roster service available';
	}
	
	var assignmentOptions = {};
	assignmentOptions.contextMappingId = $scope.contextMapping.id;
	assignmentOptions.dashboardId = $scope.activeDashboard.id;
	assignmentOptions.cardId = $scope.card.id;
	assignmentOptions.courseId = $scope.course.id;
	
	AssignmentService
	.getAssignments(assignmentOptions)
	.then(
	    function(assignmentData) {
	    	$scope.assignments = assignmentData;
	    }
	);
	
	var forumOptions = {};
	forumOptions.contextMappingId = $scope.contextMapping.id;
	forumOptions.dashboardId = $scope.activeDashboard.id;
	forumOptions.cardId = $scope.card.id;
	forumOptions.courseId = $scope.course.id;
	
	ForumDataService
	.getForums(forumOptions)
	.then(
	    function(forumData) {
	      $scope.forums = forumData;
	    }
	);

});

})(angular);
