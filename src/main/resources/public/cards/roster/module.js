(function() {
'use strict';
    
angular
.module('od.cards.roster', ['OpenDashboardRegistry', 'OpenDashboardAPI'])
 .config(function(registryProvider){
    registryProvider.register('roster',{
        title: 'Roster View',
        description: 'Use this card to view your roster',
        imgUrl: '',
        cardType: 'roster',
        styleClasses: 'od-card col-xs-12',
	    config: [
		    {field:'lap_url',fieldName:'LAP URL',fieldType:'url',required:false},
		    {field:'lap_key',fieldName:'LAP Key',fieldType:'text',required:false},
		    {field:'lap_secret',fieldName:'LAP Secret',fieldType:'text',required:false},
		    {field:'url',fieldName:'OpenLRS URL',fieldType:'url',required:false},
		    {field:'key',fieldName:'OpenLRS Key',fieldType:'text',required:false},
		    {field:'secret',fieldName:'OpenLRS Secret',fieldType:'text',required:false}
	    ]
    });
 })
 .controller('RosterCardController', function($scope, $http, $log, _, OpenDashboard_API, RosterService, OpenLRSService, LearningAnalyticsProcessorService) {
	$scope.lapResults = null;
	$scope.message = null;
	$scope.queryString = null;
	
	$scope.course = OpenDashboard_API.getCourse();
	$scope.lti = OpenDashboard_API.getInbound_LTI_Launch();

	if ($scope.lti.ext.ext_ims_lis_memberships_url && $scope.lti.ext.ext_ims_lis_memberships_id) {
		$scope.isStudent = OpenDashboard_API.getCurrentUser().isStudent();
		
		var basicLISData = {};
		basicLISData.ext_ims_lis_memberships_url = $scope.lti.ext.ext_ims_lis_memberships_url;
		basicLISData.ext_ims_lis_memberships_id = $scope.lti.ext.ext_ims_lis_memberships_id;
		
		var handleLRSResponse = function (statements) {
			_.forEach(statements, function (statement) {
				var event = OpenDashboard_API.createEventInstance();
				event.fromXAPI(statement);
				$scope.course.events.push(event);
			});

			var eventsGroupByUser = _.groupBy($scope.course.events,'user_id');
			
			_.forEach($scope.course.learners, function (learner) {
				var learnerEvents = eventsGroupByUser[learner.user_id];
				learner.events = learnerEvents;
			});
			
			var totalEventsForStudents = $scope.course.learners.reduce(function (total, learner) {
				if (learner.events) {
					return total.concat(learner.events.length);
				}
				else {
					return total.concat(0);
				}
			},[]);
			
			function median(values) {
		 
				values.sort( function(a,b) {return a - b;} );
				
				var half = Math.floor(values.length/2);
				
				if(values.length % 2)
				    return values[half];
				else
				    return (values[half-1] + values[half]) / 2.0;
			}
			
			$scope.course.events_median = median(totalEventsForStudents);
			
			_.forEach($scope.course.learners, function (learner) {
				var activityLevel = 'Medium';
				
				var median = $scope.course.events_median;
				var lowrange = median * .5;
				var highrange = median * 1.5;
				
				var learnerTotal = learner.events ? learner.events.length : 0;
				
				if (learnerTotal <= lowrange) {
					activityLevel = 'Low';
				}
				else if (learnerTotal >= highrange) {
					activityLevel = 'High';
				}	
				
				learner.relative_activity_level = activityLevel;
				
				if (learner.events && learner.events.length) {
					var moments = [];
					_.forEach(learner.events, function (event) {
						var ts = event.timestamp;
						moments.push(moment(ts));
					});
			
					var maxMoment = moment.max(moments);
					if (maxMoment) {
						learner.last_activity = maxMoment.fromNow();
					}
					else {
						learner.last_activity = 'No activity';
					}
				}
				else {
					learner.last_activity = 'No activity';
				}
			});

		};
		
		var handleLAPResponse = function (riskResults) {
			_.forEach($scope.course.learners, function (learner) {
				var risk = _.find(riskResults, { 'alternativeId': learner.user_id });
				if (risk) {
					learner.risk = risk.modelRiskConfidence;
				}
			});
		};

		if ($scope.isStudent) {
			var userId = OpenDashboard_API.getCurrentUser().user_id;
			OpenLRSService.getStatementsForUser($scope.contextMapping.id,$scope.activeDashboard.id,$scope.card.id,userId)
				.then(handleLRSResponse);
			LearningAnalyticsProcessorService.getResultsForUser($scope.contextMapping.id,$scope.activeDashboard.id,$scope.card.id,userId)
				.then(handleLAPResponse);
		}
		else {
			RosterService
			.getRoster($scope.contextMapping.id,$scope.activeDashboard.id,$scope.card.id,basicLISData)
			.then(
				function (rosterData) {
					if (rosterData) {
						$scope.course.buildRoster(rosterData);					
						OpenLRSService.getStatements($scope.contextMapping.id,$scope.activeDashboard.id,$scope.card.id)
							.then(handleLRSResponse);
						LearningAnalyticsProcessorService.getResults($scope.contextMapping.id,$scope.activeDashboard.id,$scope.card.id)
							.then(handleLAPResponse);
					}
				}
			);
		}
		
	}
	else {
		$log.error('Card not configured for Roster');
		$scope.message = 'No supporting roster service available';
	}
});

})();
