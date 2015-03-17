(function(angular) {
'use strict';

    var m_w = 123456789;
    var m_z = 987654321;
    var mask = 0xffffffff;

// Takes any integer
    function seed(i) {
        m_w = i;
    }

// Returns number between 0 (inclusive) and 1.0 (exclusive),
// just like Math.random().
    function random()
    {
        m_z = (36969 * (m_z & 65535) + (m_z >> 16)) & mask;
        m_w = (18000 * (m_w & 65535) + (m_w >> 16)) & mask;
        var result = ((m_z << 16) + m_w) & mask;
        result /= 4294967296;
        return result + 0.5;
    }

angular
.module('od.cards.awesome', ['OpenDashboardRegistry', 'OpenDashboardAPI'])
 .config(function(registryProvider){
    registryProvider.register('awesome',{
        title: 'Awesome Card',
        description: 'This is our awesome card.',
        imgUrl: 'http://m.img.brothersoft.com/iphone/1818/538995818_icon175x175.jpg',
        cardType: 'awesome',
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
 .controller('AwesomeCardController', function($scope, $log, ContextService, RosterService, LearningAnalyticsProcessorService, EventService, OutcomesService, DemographicsService) {
	
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

        var handleLRSResponse = function (statements) {
            _.forEach(statements, function (statement) {
                $scope.course.addEvent(EventService.getEventFromService(statement));
            });

            var eventsGroupByUser = _.groupBy($scope.course.events,function(event){  return event.user_id; });
            var eventsGroupByActivity = _.groupBy($scope.course.events,function(event){  return event.raw.object.id; });
            console.log($scope.course.events);

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

        var buildRosterUsageData = function(){
            seed(12345);

            _.forEach($scope.course.learners, function(learner){
                var standards = [];
                var grade = Math.round(random() * 100);
                var engagement = Math.round(grade / 5 + random() * 10);
                var historicalGrade = grade;
                for (var i = 0; i < 3; ++i) {
                    var historical = [];
                    for (var j = 0; j < 30; ++j){
                        historicalGrade += Math.round(Math.random() * 20 - 10);
                        historicalGrade = Math.max(0, Math.min(100, historicalGrade));
                        var historicalEngagement = Math.round(historicalGrade / 5 + random() * 10);

                        historical[j] = {events: historicalEngagement, grade: historicalGrade};
                    }

                    var standardName;
                    switch (i) {
                        case 0:
                            standardName = "Coding Ability";
                            break;
                        case 1:
                            standardName = "Effective Communication";
                            break;
                        case 2:
                            standardName = "Visual Ninja";
                            break;
                    }
                    standards[i] = {name: standardName, events: engagement, grade: grade, historical: historical};
                }
                learner.standards = standards;

            });
            console.log($scope.course.learners);
        };

        RosterService
            .getRoster(options, null)
            .then(
            function (rosterData) {
                if (rosterData) {
                    //$scope.course.buildRoster(rosterData);
                    EventService.getEvents($scope.contextMapping.id,$scope.activeDashboard.id,$scope.card.id)
                        .then(handleLRSResponse);
                    LearningAnalyticsProcessorService.getResults($scope.contextMapping.id,$scope.activeDashboard.id,$scope.card.id)
                        .then(handleLAPResponse);
                }
            }
        );

		RosterService
		.getRoster(options,null) // pass null so the default implementation is used
		.then(
			function (rosterData) {
				if (rosterData) {
					$scope.course.buildRoster(rosterData);
                    _.sortBy($scope.course.learners,function(learner){  return learner.user_id; });
                    buildRosterUsageData();

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

        EventService
        .getEvents($scope.contextMapping.id, $scope.activeDashboard.id, $scope.card.id)
        .then(
            function(statements) {
                $scope.events = statements;
                _.forEach($scope.events, function (event) {
                    // make them pretty (http://www.adlnet.gov/expapi/*):
                    event.verb.id = event.verb.id.split('/').pop();
                    event.object.definition.type = event.object.definition.type.split('/').pop();
                });
            }
        );



    }
	else {
		$log.error('Card not configured for Roster');
		$scope.message = 'No supporting roster service available';
	}
});

})(angular);
