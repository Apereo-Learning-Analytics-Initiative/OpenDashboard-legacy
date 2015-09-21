(function(angular, Math, moment) {
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
	    config: [],
	    requires: ['ROSTER'],
	    uses: ['EVENT','MODELOUTPUT']
    });
 })
 .controller('RosterCardController', function($scope, $translate, $translatePartialLoader, $log, _, SessionService, EventService, RosterService, ModelOutputDataService) {
	 
	$translatePartialLoader.addPart('roster-card');
    $translate.refresh();

	$scope.lapResults = null;
	$scope.message = null;
	$scope.queryString = null;
	
	$scope.course = SessionService.getCourse();
	$scope.lti = SessionService.getInbound_LTI_Launch();

	if ($scope.lti.ext.ext_ims_lis_memberships_url && $scope.lti.ext.ext_ims_lis_memberships_id) {
		$scope.isStudent = SessionService.hasStudentRole();
		
		var options = {};
		options.contextMappingId = $scope.contextMapping.id;
		options.dashboardId = $scope.activeDashboard.id;
		options.cardId = $scope.card.id;
		options.strategy = 'roster_basiclis';
		options.strategyHost = $scope.lti.ext.ext_ims_lis_memberships_url;
		options.strategyKey = $scope.lti.ext.ext_ims_lis_memberships_id;
		
		var handleLRSResponse = function (statements) {
			_.forEach(statements, function (statement) {
				$scope.course.addEvent(EventService.getEventFromService(statement));
			});

			var eventsGroupByUser = _.groupBy($scope.course.events,function(event){ return event.user_id; });
			_.forEach($scope.course.learners, function (learner) {
				var learnerEvents = eventsGroupByUser[learner.user_id];
				if (!learnerEvents) {
					// if no events were found try with email address
					if (learner.person.contact_email_primary) {
						learnerEvents = eventsGroupByUser[learner.person.contact_email_primary.split('@')[0]];
					}
				}
				
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
			var userId = SessionService.getCurrentUser().user_id;
			EventService.getEventsForUser($scope.contextMapping.id,$scope.activeDashboard.id,$scope.card.id,userId)
				.then(handleLRSResponse);
//			LearningAnalyticsProcessorService.getResultsForUser($scope.contextMapping.id,$scope.activeDashboard.id,$scope.card.id,userId)
//				.then(handleLAPResponse);
		}
		else {
			RosterService
			.getRoster(options, null)
			.then(
				function (rosterData) {
					if (rosterData) {
						$scope.course.buildRoster(rosterData);					
						EventService.getEvents($scope.contextMapping.id,$scope.activeDashboard.id,$scope.card.id)
							.then(handleLRSResponse);
//						LearningAnalyticsProcessorService.getResults($scope.contextMapping.id,$scope.activeDashboard.id,$scope.card.id)
//							.then(handleLAPResponse);
					}
				}
			);
		}
		
	}
	else {
		$scope.isError = true;
		$scope.errorMessage = 'ERROR_NO_PROVIDER_ROSTER';
	}
});

})(angular, Math, moment);
