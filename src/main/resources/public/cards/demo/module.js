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
(function(angular) {
'use strict';
    
angular
.module('od.cards.demo', ['OpenDashboardRegistry', 'OpenDashboardAPI'])
 .config(function(registryProvider){
    registryProvider.register('demo',{
        title: 'Demo Card',
        description: 'This card demonstrates how to retrieve data from various sources.',
        cardType: 'demo',
        styleClasses: 'od-card col-xs-12',
	    config: [{field:'sample',fieldName:'Example field',fieldType:'text',required:true,translatableLabelKey:'EXAMPLE_FIELD'}],
	    requires: [],
	    uses: ['ROSTER','OUTCOME','ASSIGNMENT','FORUM','COURSE']
    });
 })
 .controller('DemoCardController', function($scope, $log, $translate, $translatePartialLoader,
	 SessionService, RosterService, OutcomesService, AssignmentService, ForumDataService, CourseDataService) {
    $translatePartialLoader.addPart('demo-card');
    $translate.refresh();
    
	$scope.course = SessionService.getCourse();
	$scope.lti = SessionService.getInbound_LTI_Launch();

	if ($scope.lti.ext.ext_ims_lis_memberships_url && $scope.lti.ext.ext_ims_lis_memberships_id) {
		
		
		var options = {};
		options.contextMappingId = $scope.contextMapping.id;
		options.dashboardId = $scope.activeDashboard.id;
		options.cardId = $scope.card.id;
		options.courseId = $scope.course.id;
		options.strategy = 'BASIC_LIS';
		options.strategyHost = $scope.lti.ext.ext_ims_lis_memberships_url;
		options.strategyKey = $scope.lti.ext.ext_ims_lis_memberships_id;

		RosterService
		.getRoster(options)
		.then(
			function (rosterData) {
				if (rosterData) {
					$scope.course.buildRoster(rosterData);					
				}
			}
		);
	}
	else {
		$log.error('Card not configured for Roster');
		$scope.message = 'No supporting roster service available';
	}
	
	var providerOptions = {};
	providerOptions.contextMappingId = $scope.contextMapping.id;
	providerOptions.dashboardId = $scope.activeDashboard.id;
	providerOptions.cardId = $scope.card.id;
	providerOptions.courseId = $scope.course.id;

	OutcomesService
	.getOutcomesForCourse(providerOptions)
	.then(
		function(outcomesData) {
			$scope.outcomes = outcomesData;
		}
	);
	
	AssignmentService
	.getAssignments(providerOptions)
	.then(
	    function(assignmentData) {
	    	$scope.assignments = assignmentData;
	    }
	);
	
	ForumDataService
	.getForums(providerOptions)
	.then(
	    function(forumData) {
	      $scope.forums = forumData;
	      ForumDataService
	      .getMessages(providerOptions,$scope.forums[0].topics[0].id)
	      .then(
	    	function (messagesData) {
	    	  $scope.messages = messagesData;
	    	}
	      );
	    }
	);
	
	CourseDataService
	.getContexts(providerOptions)
	.then(
	    function(contextsData) {
	      $scope.contexts = contextsData;
	    }
	);

	CourseDataService
	.getContext(providerOptions, providerOptions.courseId)
	.then(
	    function(contextData) {
	      $scope.contextData = contextData;
	    }
	);


});

})(angular);
